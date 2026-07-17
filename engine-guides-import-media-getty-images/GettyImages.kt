import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.ShapeType
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

suspend fun gettyImages(
    engine: Engine,
    proxyBaseUrl: String,
    proxyResponseProvider: (suspend (Uri) -> JSONObject)? = null,
): GettyImagesImportResult {
    // highlight-android-scene-setup
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-scene-setup

    // highlight-android-programmatic-register
    val source = GettyImagesAssetSource(
        proxyBaseUrl = proxyBaseUrl,
        proxyResponseProvider = proxyResponseProvider,
    )
    if (source.sourceId !in engine.asset.findAllSources()) {
        engine.asset.addSource(source)
    }
    // highlight-android-programmatic-register

    // highlight-android-query-and-apply
    val results = engine.asset.findAssets(
        sourceId = GettyImagesAssetSource.SOURCE_ID,
        query = FindAssetsQuery(query = "architecture", page = 0, perPage = 12),
    )
    val firstBlock = results.assets.firstOrNull()?.let { asset ->
        engine.asset.applyAssetSourceAsset(
            sourceId = GettyImagesAssetSource.SOURCE_ID,
            asset = asset,
        )
    }
    return GettyImagesImportResult(total = results.total, firstBlock = firstBlock)
    // highlight-android-query-and-apply
}

data class GettyImagesImportResult(
    val total: Int,
    val firstBlock: DesignBlock?,
)

@Composable
fun GettyImagesEditorSolution(
    license: String,
    proxyBaseUrl: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-editor-source-create
    val gettyImagesAssetSource = remember(proxyBaseUrl) {
        GettyImagesAssetSource(proxyBaseUrl = proxyBaseUrl)
    }
    // highlight-android-editor-source-create

    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-editor-source-register
                onLoaded = {
                    val engine = editorContext.engine
                    if (gettyImagesAssetSource.sourceId !in engine.asset.findAllSources()) {
                        engine.asset.addSource(gettyImagesAssetSource)
                    }
                }
                // highlight-android-editor-source-register

                // highlight-android-asset-library-ui
                assetLibrary = {
                    remember {
                        val gettyImagesSourceType = AssetSourceType(sourceId = gettyImagesAssetSource.sourceId)
                        val gettyImagesSection = LibraryContent.Section(
                            sourceTypes = listOf(gettyImagesSourceType),
                            assetType = AssetType.Image,
                            expandContent = LibraryContent.Grid(
                                titleRes = R.string.ly_img_editor_asset_library_section_images,
                                sourceType = gettyImagesSourceType,
                                assetType = AssetType.Image,
                                title = "Getty Images",
                            ),
                        )
                        AssetLibrary.getDefault(
                            images = LibraryCategory.Images.addSection(gettyImagesSection),
                        )
                    }
                }
                // highlight-android-asset-library-ui
            }
        },
        onClose = onClose,
    )
}

// highlight-android-source-definition
class GettyImagesAssetSource(
    private val proxyBaseUrl: String,
    private val proxyResponseProvider: (suspend (Uri) -> JSONObject)? = null,
) : AssetSource(sourceId = SOURCE_ID) {
    override val supportedMimeTypes = listOf("image/jpeg")

    override val credits = AssetCredits(
        name = "Getty Images",
        uri = Uri.parse("https://www.gettyimages.com/"),
    )

    override val license = AssetLicense(
        name = "Getty Images Content License Agreement",
        uri = Uri.parse("https://www.gettyimages.com/eula"),
    )

    override suspend fun getGroups(): List<String>? = null

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
        val proxyResponse = loadGettyImagesResponse(
            uri = buildGettyImagesSearchUri(proxyBaseUrl = proxyBaseUrl, query = query),
            responseProvider = proxyResponseProvider,
        )
        toFindAssetsResult(response = proxyResponse, requestedPage = query.page, perPage = query.perPage)
    }

    companion object {
        const val SOURCE_ID = "ly.img.asset.source.getty.images"
    }
}
// highlight-android-source-definition

// highlight-android-build-search-uri
fun buildGettyImagesSearchUri(
    proxyBaseUrl: String,
    query: FindAssetsQuery,
): Uri = Uri.parse(proxyBaseUrl).buildUpon()
    .appendQueryParameter("phrase", query.query.orEmpty())
    .appendQueryParameter("page", (query.page + 1).toString())
    .appendQueryParameter("page_size", query.perPage.toString())
    .build()
// highlight-android-build-search-uri

// highlight-android-load-proxy-response
suspend fun loadGettyImagesResponse(
    uri: Uri,
    responseProvider: (suspend (Uri) -> JSONObject)? = null,
): JSONObject = responseProvider?.invoke(uri) ?: readGettyImagesJson(uri)

fun readGettyImagesJson(uri: Uri): JSONObject {
    val connection = URL(uri.toString()).openConnection() as HttpURLConnection
    return try {
        require(connection.responseCode in 200 until 300) {
            connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
        }
        JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
    } finally {
        connection.disconnect()
    }
}
// highlight-android-load-proxy-response

// highlight-android-response-pagination
fun toFindAssetsResult(
    response: JSONObject,
    requestedPage: Int,
    perPage: Int,
): FindAssetsResult {
    val images = response.optJSONArray("images")
    val total = response.optInt("result_count", -1)
    val loadedSoFar = (requestedPage + 1) * perPage
    val hasNextPage = images != null && images.length() > 0 && (total < 0 || loadedSoFar < total)
    val assets = if (images == null) {
        emptyList()
    } else {
        (0 until images.length()).map { index ->
            toGettyImagesAsset(images.getJSONObject(index))
        }
    }
    return FindAssetsResult(
        assets = assets,
        currentPage = requestedPage,
        nextPage = if (hasNextPage) requestedPage + 1 else -1,
        total = total,
    )
}
// highlight-android-response-pagination

// highlight-android-response-asset
fun toGettyImagesAsset(photo: JSONObject): Asset = Asset(
    id = photo.getString("id"),
    context = AssetContext(sourceId = GettyImagesAssetSource.SOURCE_ID),
    label = photo.optString("title").takeIf { it.isNotBlank() },
    locale = "en",
    meta = toGettyImagesImageMeta(photo),
)

fun toGettyImagesImageMeta(photo: JSONObject): Map<String, String> {
    val sizes = getGettyImagesDisplaySizesByName(photo)
    val fullUri = sizes["comp"] ?: sizes["preview"] ?: sizes.values.firstOrNull().orEmpty()
    val thumbUri = sizes["thumb"] ?: sizes["preview"] ?: fullUri
    val maxDimensions = photo.optJSONObject("max_dimensions")
    return mapOf(
        "uri" to fullUri,
        "thumbUri" to thumbUri,
        "mimeType" to "image/jpeg",
        "blockType" to DesignBlockType.Graphic.key,
        "fillType" to FillType.Image.key,
        "shapeType" to ShapeType.Rect.key,
        "kind" to "image",
        "width" to maxDimensions?.optInt("width")?.takeIf { it > 0 }?.toString().orEmpty(),
        "height" to maxDimensions?.optInt("height")?.takeIf { it > 0 }?.toString().orEmpty(),
    )
}
// highlight-android-response-asset

// highlight-android-response-sizes
fun getGettyImagesDisplaySizesByName(photo: JSONObject): Map<String, String> {
    val displaySizes = photo.optJSONArray("display_sizes") ?: return emptyMap()
    return (0 until displaySizes.length())
        .mapNotNull { index ->
            val size = displaySizes.optJSONObject(index) ?: return@mapNotNull null
            val name = size.optString("name").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val uri = size.optString("uri").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            name to uri
        }
        .toMap()
}
// highlight-android-response-sizes
