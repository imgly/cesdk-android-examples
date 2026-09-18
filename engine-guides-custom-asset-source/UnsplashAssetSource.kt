import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.core.R
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetDefinition
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
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class CustomAssetSourceSummary(
    val sourceRegistered: Boolean,
    val sourceCreditsName: String?,
    val sourceLicenseName: String?,
    val localAssetCount: Int,
    val localAssetId: String?,
    val queriedRemoteSource: Boolean,
    val appliedRemoteAsset: Boolean,
)

suspend fun customAssetSource(
    engine: Engine,
    unsplashBaseUrl: String,
    utmSource: String,
    unsplashAssetSource: UnsplashAssetSource? = null,
): CustomAssetSourceSummary {
    runCatching { engine.asset.removeSource(UnsplashAssetSource.SOURCE_ID) }
    runCatching { engine.asset.removeSource(LOCAL_SOURCE_ID) }

    val source = unsplashAssetSource ?: UnsplashAssetSource(
        engine = engine,
        baseUrl = unsplashBaseUrl,
        utmSource = utmSource,
    )
    // highlight-android-register-source
    engine.asset.addSource(source)
    // highlight-android-register-source

    val queriedRemoteSource = unsplashBaseUrl.isNotBlank()
    var appliedRemoteAsset = false

    // highlight-android-query-and-apply
    if (unsplashBaseUrl.isNotBlank()) {
        val popularResults = engine.asset.findAssets(
            sourceId = source.sourceId,
            query = FindAssetsQuery(query = "", page = 0, perPage = 10),
        )
        val searchResults = engine.asset.findAssets(
            sourceId = source.sourceId,
            query = FindAssetsQuery(query = "landscape", page = 0, perPage = 10),
        )

        val assetToApply = searchResults.assets.firstOrNull() ?: popularResults.assets.firstOrNull()
        if (assetToApply != null) {
            appliedRemoteAsset = engine.asset.applyAssetSourceAsset(
                sourceId = source.sourceId,
                asset = assetToApply,
            ) != null
        }
    }
    // highlight-android-query-and-apply

    // highlight-android-add-local-source
    engine.asset.addLocalSource(
        sourceId = LOCAL_SOURCE_ID,
        supportedMimeTypes = listOf(MimeType.JPEG.key),
    )
    // highlight-android-add-local-source

    // highlight-android-add-asset-to-source
    val localAsset = AssetDefinition(
        id = "sample-landscape",
        label = mapOf("en" to "Sample Landscape"),
        tags = mapOf("en" to listOf("landscape", "sample")),
        meta = mapOf(
            "uri" to "https://img.ly/static/ubq_samples/sample_1.jpg",
            "thumbUri" to "https://img.ly/static/ubq_samples/sample_1.jpg",
            "mimeType" to MimeType.JPEG.key,
            "blockType" to DesignBlockType.Graphic.key,
            "fillType" to FillType.Image.key,
            "shapeType" to ShapeType.Rect.key,
            "kind" to "image",
            "width" to "1080",
            "height" to "1080",
        ),
    )
    engine.asset.addAsset(sourceId = LOCAL_SOURCE_ID, asset = localAsset)
    engine.asset.assetSourceContentsChanged(sourceId = LOCAL_SOURCE_ID)
    // highlight-android-add-asset-to-source

    val localResults = engine.asset.findAssets(
        sourceId = LOCAL_SOURCE_ID,
        query = FindAssetsQuery(query = "landscape", page = 0, perPage = 10),
    )

    return CustomAssetSourceSummary(
        sourceRegistered = source.sourceId in engine.asset.findAllSources(),
        sourceCreditsName = engine.asset.getCredits(source.sourceId)?.name,
        sourceLicenseName = engine.asset.getLicense(source.sourceId)?.name,
        localAssetCount = localResults.assets.size,
        localAssetId = localResults.assets.firstOrNull()?.id,
        queriedRemoteSource = queriedRemoteSource,
        appliedRemoteAsset = appliedRemoteAsset,
    )
}

// highlight-android-source-definition
open class UnsplashAssetSource(
    private val engine: Engine,
    private val baseUrl: String,
    private val utmSource: String,
) : AssetSource(sourceId = "ly.img.asset.source.unsplash") {
    companion object {
        const val SOURCE_ID = "ly.img.asset.source.unsplash"
    }

    override val supportedMimeTypes = listOf(MimeType.JPEG.key)

    override suspend fun getGroups(): List<String>? = null
    // highlight-android-source-definition

    // highlight-android-credits-license
    override val credits = AssetCredits(
        name = "Unsplash",
        uri = Uri.parse("https://unsplash.com/"),
    )

    override val license = AssetLicense(
        name = "Unsplash license (free)",
        uri = Uri.parse("https://unsplash.com/license"),
    )
    // highlight-android-credits-license

    // highlight-android-find-assets
    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
        if (query.query.isNullOrBlank()) {
            query.getPopularList()
        } else {
            query.getSearchList()
        }
    }
    // highlight-android-find-assets

    // highlight-android-popular-list
    private suspend fun FindAssetsQuery.getPopularList(): FindAssetsResult {
        val requestUrl = "$baseUrl/photos?order_by=popular&page=${page + 1}&per_page=$perPage"
        val assetsArray = getResponseAsString(requestUrl).let(::JSONArray)
        val assets = mutableListOf<Asset>()
        for (index in 0 until assetsArray.length()) {
            assets += assetsArray.getJSONObject(index).toAsset()
        }
        return FindAssetsResult(
            assets = assets,
            currentPage = page,
            nextPage = if (assets.isEmpty()) -1 else page + 1,
            total = Int.MAX_VALUE,
        )
    }
    // highlight-android-popular-list

    // highlight-android-search-query
    private suspend fun FindAssetsQuery.getSearchList(): FindAssetsResult {
        val encodedQuery = URLEncoder.encode(query.orEmpty(), Charsets.UTF_8.name())
        val requestUrl = "$baseUrl/search/photos?query=$encodedQuery&page=${page + 1}&per_page=$perPage"
        val response = getResponseAsString(requestUrl).let(::JSONObject)
        val assetsArray = response.getJSONArray("results")
        val assets = mutableListOf<Asset>()
        for (index in 0 until assetsArray.length()) {
            assets += assetsArray.getJSONObject(index).toAsset()
        }

        val total = response.getInt("total")
        val totalPages = response.getInt("total_pages")
        return FindAssetsResult(
            assets = assets,
            currentPage = page,
            nextPage = if (page + 1 >= totalPages) -1 else page + 1,
            total = total,
        )
    }
    // highlight-android-search-query

    // highlight-android-apply-and-track
    override suspend fun applyAsset(asset: Asset): DesignBlock? {
        trackDownloadEvent(asset)
        return engine.asset.defaultApplyAsset(asset)
    }

    override suspend fun applyAsset(
        asset: Asset,
        block: DesignBlock,
    ) {
        trackDownloadEvent(asset)
        engine.asset.defaultApplyAsset(asset, block)
    }
    // highlight-android-apply-and-track

    // highlight-android-download-tracking
    private suspend fun trackDownloadEvent(asset: Asset) {
        val downloadLocation = asset.meta
            ?.get("downloadLocation")
            ?.takeIf { it.isNotBlank() }
            ?: return

        getResponseAsString(proxiedApiUrl(downloadLocation))
    }
    // highlight-android-download-tracking

    // highlight-android-translate-asset
    private suspend fun JSONObject.toAsset() = Asset(
        id = getString("id"),
        locale = "en",
        label = listOf(
            optString("description"),
            optString("alt_description"),
        ).firstOrNull { it.isNotBlank() },
        tags = optJSONArray("tags")
            ?.let { array ->
                (0 until array.length()).mapNotNull { index ->
                    array.optJSONObject(index)?.optString("title")?.takeIf { it.isNotBlank() }
                }
            }
            ?.takeIf { it.isNotEmpty() },
        meta = mapOf(
            "uri" to getJSONObject("urls").getString("regular"),
            "thumbUri" to getJSONObject("urls").getString("thumb"),
            "downloadLocation" to optJSONObject("links")
                ?.optString("download_location")
                .orEmpty(),
            "blockType" to DesignBlockType.Graphic.key,
            "fillType" to FillType.Image.key,
            "shapeType" to ShapeType.Rect.key,
            "kind" to "image",
            "width" to getInt("width").toString(),
            "height" to getInt("height").toString(),
        ),
        context = AssetContext(sourceId = sourceId),
        credits = AssetCredits(
            name = optJSONObject("user")?.optString("name")?.takeIf { it.isNotBlank() } ?: "Unknown photographer",
            uri = optJSONObject("user")
                ?.optJSONObject("links")
                ?.optString("html")
                ?.takeIf { it.isNotBlank() }
                ?.let(Uri::parse),
        ),
        utm = AssetUTM(source = utmSource, medium = "referral"),
    )
    // highlight-android-translate-asset

    // highlight-android-networking-helpers
    protected open suspend fun getResponseAsString(url: String) = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        require(connection.responseCode in 200 until 300) {
            connection.errorStream.bufferedReader().use { it.readText() }
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun proxiedApiUrl(url: String): String {
        val parsedUrl = Uri.parse(url)
        if (parsedUrl.scheme == null) {
            val separator = if (url.startsWith("/")) "" else "/"
            return "$baseUrl$separator$url"
        }
        require(parsedUrl.host == "api.unsplash.com") {
            "Expected an Unsplash API URL but received ${parsedUrl.host}"
        }

        val path = parsedUrl.encodedPath.orEmpty()
        val query = parsedUrl.encodedQuery?.let { "?$it" }.orEmpty()
        return "$baseUrl$path$query"
    }
    // highlight-android-networking-helpers
}

private const val LOCAL_SOURCE_ID = "sample-local-images"

// highlight-android-asset-library-ui
fun unsplashAssetLibrary(): AssetLibrary {
    val unsplashSourceType = AssetSourceType(sourceId = UnsplashAssetSource.SOURCE_ID)
    val unsplashSection = LibraryContent.Section(
        titleRes = R.string.ly_img_editor_asset_library_section_images,
        sourceTypes = listOf(unsplashSourceType),
        assetType = AssetType.Image,
        expandContent = LibraryContent.Grid(
            titleRes = R.string.ly_img_editor_asset_library_section_images,
            sourceType = unsplashSourceType,
            assetType = AssetType.Image,
        ),
    )

    return AssetLibrary.getDefault(
        images = LibraryCategory.Images.addSection(unsplashSection),
    )
}
// highlight-android-asset-library-ui
