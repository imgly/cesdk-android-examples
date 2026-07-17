import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.core.R
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetLicense
import ly.img.engine.AssetSource
import ly.img.engine.AssetUTM
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class PexelsImportResult(
    val curatedTotal: Int,
    val searchTotal: Int,
    val firstBlock: DesignBlock?,
)

suspend fun importFromPexels(
    engine: Engine,
    apiKey: String = "YOUR_PEXELS_API_KEY",
    client: PexelsClient = HttpPexelsClient(apiKey = apiKey),
): PexelsImportResult {
    // highlight-android-scene-setup
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1080F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-scene-setup

    // highlight-android-programmatic-register
    val source = PexelsAssetSource(client = client)
    if (source.sourceId !in engine.asset.findAllSources()) {
        engine.asset.addSource(source)
    }
    // highlight-android-programmatic-register
    // highlight-android-query-source
    val curated = engine.asset.findAssets(
        sourceId = source.sourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )

    val search = engine.asset.findAssets(
        sourceId = source.sourceId,
        query = FindAssetsQuery(page = 0, perPage = 10, query = "workspace"),
    )
    // highlight-android-query-source

    // highlight-android-apply-asset
    val firstBlock = (search.assets.firstOrNull() ?: curated.assets.firstOrNull())?.let { asset ->
        engine.asset.applyAssetSourceAsset(
            sourceId = source.sourceId,
            asset = asset,
        )
    }
    // highlight-android-apply-asset

    return PexelsImportResult(
        curatedTotal = curated.total,
        searchTotal = search.total,
        firstBlock = firstBlock,
    )
}

@Composable
fun PexelsEditorSolution(
    license: String,
    apiKey: String,
    client: PexelsClient? = null,
    onClose: (Throwable?) -> Unit,
) {
    val pexelsClient = remember(apiKey, client) {
        client ?: HttpPexelsClient(apiKey = apiKey)
    }

    // highlight-android-editor-source-create
    val pexelsAssetSource = remember(pexelsClient) {
        PexelsAssetSource(client = pexelsClient)
    }
    // highlight-android-editor-source-create

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        PexelsProviderLink()

        Box(modifier = Modifier.weight(1F)) {
            Editor(
                license = license,
                configuration = {
                    EditorConfiguration.remember {
                        // highlight-android-editor-source-register
                        onLoaded = {
                            val engine = editorContext.engine
                            if (pexelsAssetSource.sourceId !in engine.asset.findAllSources()) {
                                engine.asset.addSource(pexelsAssetSource)
                            }
                        }
                        // highlight-android-editor-source-register

                        // highlight-android-asset-library-ui
                        assetLibrary = {
                            remember {
                                val pexelsSourceType = AssetSourceType(sourceId = pexelsAssetSource.sourceId)
                                val pexelsSection = LibraryContent.Section(
                                    sourceTypes = listOf(pexelsSourceType),
                                    assetType = AssetType.Image,
                                    expandContent = LibraryContent.Grid(
                                        titleRes = R.string.ly_img_editor_asset_library_section_images,
                                        sourceType = pexelsSourceType,
                                        assetType = AssetType.Image,
                                        title = "Pexels",
                                    ),
                                )
                                AssetLibrary.getDefault(
                                    images = LibraryCategory.Images.addSection(pexelsSection),
                                )
                            }
                        }
                        // highlight-android-asset-library-ui
                    }
                },
                onClose = onClose,
            )
        }
    }
}

// highlight-android-provider-link
const val PEXELS_PROVIDER_URI = "https://www.pexels.com/?utm_source=cesdk-android&utm_medium=referral"

@Composable
fun PexelsProviderLink(onOpenUri: ((Uri) -> Unit)? = null) {
    val uriHandler = LocalUriHandler.current
    val providerUri = remember { Uri.parse(PEXELS_PROVIDER_URI) }
    TextButton(
        onClick = {
            onOpenUri?.invoke(providerUri) ?: uriHandler.openUri(providerUri.toString())
        },
    ) {
        Text("Photos provided by Pexels")
    }
}
// highlight-android-provider-link

interface PexelsClient {
    suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
    ): PexelsResponse

    suspend fun curatedPhotos(
        page: Int,
        perPage: Int,
    ): PexelsResponse
}

const val PEXELS_CONNECT_TIMEOUT_MILLIS = 10_000
const val PEXELS_READ_TIMEOUT_MILLIS = 30_000

data class PexelsHttpRequest(
    val uri: Uri,
    val headers: Map<String, String>,
    val connectTimeoutMillis: Int = PEXELS_CONNECT_TIMEOUT_MILLIS,
    val readTimeoutMillis: Int = PEXELS_READ_TIMEOUT_MILLIS,
)

data class PexelsHttpResponse(
    val statusCode: Int,
    val body: String,
)

fun interface PexelsTransport {
    suspend fun execute(request: PexelsHttpRequest): PexelsHttpResponse
}

fun configurePexelsHttpConnection(
    connection: HttpURLConnection,
    request: PexelsHttpRequest,
) {
    connection.connectTimeout = request.connectTimeoutMillis
    connection.readTimeout = request.readTimeoutMillis
    request.headers.forEach { (key, value) ->
        connection.setRequestProperty(key, value)
    }
}

object UrlConnectionPexelsTransport : PexelsTransport {
    override suspend fun execute(request: PexelsHttpRequest): PexelsHttpResponse = withContext(Dispatchers.IO) {
        val connection = URL(request.uri.toString()).openConnection() as HttpURLConnection
        try {
            configurePexelsHttpConnection(connection = connection, request = request)
            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            PexelsHttpResponse(
                statusCode = statusCode,
                body = stream?.bufferedReader()?.use { it.readText() }.orEmpty(),
            )
        } finally {
            connection.disconnect()
        }
    }
}

// highlight-android-api-client-implementation
class HttpPexelsClient(
    private val apiKey: String,
    private val transport: PexelsTransport = UrlConnectionPexelsTransport,
) : PexelsClient {
    override suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int,
    ): PexelsResponse = requestPexels(
        apiKey = apiKey,
        transport = transport,
        endpoint = "search",
        queryParameters = mapOf(
            "query" to query,
            "page" to page.toString(),
            "per_page" to perPage.toString(),
        ),
    )

    override suspend fun curatedPhotos(
        page: Int,
        perPage: Int,
    ): PexelsResponse = requestPexels(
        apiKey = apiKey,
        transport = transport,
        endpoint = "curated",
        queryParameters = mapOf(
            "page" to page.toString(),
            "per_page" to perPage.toString(),
        ),
    )
}
// highlight-android-api-client-implementation

// highlight-android-api-request
suspend fun requestPexels(
    apiKey: String,
    transport: PexelsTransport,
    endpoint: String,
    queryParameters: Map<String, String>,
): PexelsResponse {
    val uriBuilder = Uri.parse("https://api.pexels.com/v1")
        .buildUpon()
        .appendPath(endpoint)
    queryParameters.forEach { (key, value) ->
        uriBuilder.appendQueryParameter(key, value)
    }

    val response = transport.execute(
        PexelsHttpRequest(
            uri = uriBuilder.build(),
            headers = mapOf("Authorization" to apiKey),
        ),
    )
    require(response.statusCode == HttpURLConnection.HTTP_OK) {
        response.body.ifBlank { "Pexels request failed with HTTP ${response.statusCode}." }
    }
    return PexelsResponse.fromJson(JSONObject(response.body))
}
// highlight-android-api-request

data class PexelsResponse(
    val photos: List<PexelsPhoto>,
    val totalResults: Int?,
    val hasNextPage: Boolean,
) {
    companion object {
        // highlight-android-response-decoding
        fun fromJson(json: JSONObject): PexelsResponse {
            val photosJson = json.getJSONArray("photos")
            val photos = (0 until photosJson.length()).map { index ->
                PexelsPhoto.fromJson(photosJson.getJSONObject(index))
            }
            return PexelsResponse(
                photos = photos,
                totalResults = if (json.has("total_results")) json.getInt("total_results") else null,
                hasNextPage = photos.isNotEmpty() && getNullableJsonString(json, "next_page")?.isNotBlank() == true,
            )
        }
        // highlight-android-response-decoding
    }
}

data class PexelsPhoto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String?,
    val alt: String?,
    val src: PexelsPhotoSource,
) {
    companion object {
        fun fromJson(json: JSONObject): PexelsPhoto {
            val src = json.getJSONObject("src")
            return PexelsPhoto(
                id = json.getInt("id"),
                width = json.getInt("width"),
                height = json.getInt("height"),
                url = json.getString("url"),
                photographer = json.getString("photographer"),
                photographerUrl = getNullableJsonString(json, "photographer_url"),
                alt = getNullableJsonString(json, "alt"),
                src = PexelsPhotoSource(
                    original = src.getString("original"),
                    medium = src.getString("medium"),
                ),
            )
        }
    }
}

data class PexelsPhotoSource(
    val original: String,
    val medium: String,
)

fun getNullableJsonString(
    json: JSONObject,
    name: String,
): String? = if (json.has(name) && !json.isNull(name)) json.getString(name) else null

// highlight-android-source-metadata
class PexelsAssetSource(
    private val client: PexelsClient,
) : AssetSource(sourceId = SOURCE_ID) {
    override suspend fun getGroups(): List<String>? = null

    override val supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key)
    override val credits = AssetCredits(
        name = "Pexels",
        uri = Uri.parse("https://www.pexels.com/"),
    )

    override val license = AssetLicense(
        name = "Pexels license (free)",
        uri = Uri.parse("https://www.pexels.com/license/"),
    )

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
        findPexelsAssets(
            client = client,
            sourceId = sourceId,
            query = query,
        )
    }

    companion object {
        const val SOURCE_ID = "ly.img.asset.source.pexels"
    }
}
// highlight-android-source-metadata

// highlight-android-find-assets
suspend fun findPexelsAssets(
    client: PexelsClient,
    sourceId: String,
    query: FindAssetsQuery,
): FindAssetsResult {
    val pexelsPage = query.page + 1
    val searchQuery = query.query?.takeIf { it.isNotBlank() }
    val response =
        if (searchQuery == null) {
            client.curatedPhotos(page = pexelsPage, perPage = query.perPage)
        } else {
            client.searchPhotos(
                query = searchQuery,
                page = pexelsPage,
                perPage = query.perPage,
            )
        }

    val assets = response.photos.map { photo ->
        toPexelsAsset(photo = photo, sourceId = sourceId)
    }
    return FindAssetsResult(
        assets = assets,
        currentPage = query.page,
        nextPage = if (response.hasNextPage) query.page + 1 else -1,
        total = response.totalResults ?: -1,
    )
}
// highlight-android-find-assets

// highlight-android-mime-type
fun getPexelsMimeType(uri: String): String? {
    val extension = Uri.parse(uri).lastPathSegment?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase()
    return when (extension) {
        "jpg", "jpeg" -> MimeType.JPEG.key
        "png" -> MimeType.PNG.key
        else -> null
    }
}
// highlight-android-mime-type

// highlight-android-translate
fun toPexelsAsset(
    photo: PexelsPhoto,
    sourceId: String,
) = Asset(
    id = photo.id.toString(),
    context = AssetContext(sourceId = sourceId),
    label = photo.alt?.takeIf { it.isNotBlank() },
    locale = "en",
    meta = buildMap {
        put("uri", photo.src.original)
        put("thumbUri", photo.src.medium)
        getPexelsMimeType(photo.src.original)?.let { put("mimeType", it) }
        put("blockType", DesignBlockType.Graphic.key)
        put("fillType", FillType.Image.key)
        put("shapeType", ShapeType.Rect.key)
        put("kind", "image")
        put("width", photo.width.toString())
        put("height", photo.height.toString())
    },
    credits = AssetCredits(
        name = photo.photographer,
        uri = Uri.parse(photo.url),
    ),
    utm = AssetUTM(
        source = "cesdk-android",
        medium = "referral",
    ),
)
// highlight-android-translate
