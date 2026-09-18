import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetLicense
import ly.img.engine.AssetSource
import ly.img.engine.AssetUTM
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

private const val CUSTOM_IMPORTER_SOURCE_ID = "com.example.media.images"
private const val DEMO_MEDIA_BASE_URI = "https://media.example.com"

data class CreateCustomImporterResult(
    val sourceId: String,
    val assetIds: List<String>,
    val groups: List<String>,
    val nextPage: Int,
    val total: Int,
    val sourceStillRegistered: Boolean,
)

suspend fun createCustomImporter(engine: Engine): CreateCustomImporterResult {
    val source = BackendImageAssetSource(mediaItems = demoMediaItems())

    if (source.sourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(source.sourceId)
    }

    // highlight-android-register-source
    engine.asset.addSource(source)
    // highlight-android-register-source

    // highlight-android-query-source
    val groups = engine.asset.getGroups(sourceId = source.sourceId).orEmpty()
    val result = engine.asset.findAssets(
        sourceId = source.sourceId,
        query = FindAssetsQuery(
            query = "summer",
            page = 0,
            perPage = 2,
            locale = "en",
        ),
    )
    // highlight-android-query-source

    // highlight-android-refresh-source
    engine.asset.assetSourceContentsChanged(sourceId = source.sourceId)
    // highlight-android-refresh-source

    return CreateCustomImporterResult(
        sourceId = source.sourceId,
        assetIds = result.assets.map { asset -> asset.id },
        groups = groups,
        nextPage = result.nextPage,
        total = result.total,
        sourceStillRegistered = source.sourceId in engine.asset.findAllSources(),
    )
}

// highlight-android-source-definition
class BackendImageAssetSource(
    private val mediaItems: List<BackendMediaItem>,
) : AssetSource(sourceId = CUSTOM_IMPORTER_SOURCE_ID) {
    override val supportedMimeTypes = listOf(MimeType.JPEG.key)

    override val credits = AssetCredits(
        name = "App media library",
        uri = null,
    )

    override val license = AssetLicense(
        name = "App media license",
        uri = null,
    )

    override suspend fun getGroups(): List<String> = withContext(Dispatchers.IO) {
        mediaItems.flatMap { item -> item.groups }.distinct()
    }
    // highlight-android-source-definition

    // highlight-android-find-assets
    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
        val terms = query.query.orEmpty()
            .lowercase()
            .split(" ")
            .filter { term -> term.isNotBlank() }
        val requestedPage = query.page.coerceAtLeast(0)
        val pageSize = query.perPage.coerceAtLeast(1)
        val requestedGroups = query.groups.orEmpty()

        val filteredItems = mediaItems.filter { item ->
            val matchesQuery = terms.isEmpty() ||
                terms.all { term ->
                    item.label.lowercase().contains(term) ||
                        item.tags.any { tag -> tag.lowercase().contains(term) }
                }
            val matchesGroup = requestedGroups.isEmpty() ||
                item.groups.any { group -> group in requestedGroups }
            matchesQuery && matchesGroup
        }

        val start = requestedPage * pageSize
        val pageItems = filteredItems.drop(start).take(pageSize)

        FindAssetsResult(
            assets = pageItems.map { item -> item.toAsset(sourceId = sourceId) },
            currentPage = requestedPage,
            nextPage = if (start + pageSize < filteredItems.size) requestedPage + 1 else -1,
            total = filteredItems.size,
        )
    }
    // highlight-android-find-assets

    // highlight-android-map-asset
    private fun BackendMediaItem.toAsset(sourceId: String) = Asset(
        id = id,
        context = AssetContext(sourceId = sourceId),
        label = label,
        locale = locale,
        tags = tags,
        groups = groups,
        meta = mapOf(
            "uri" to uri,
            "thumbUri" to thumbUri,
            "mimeType" to mimeType,
            "blockType" to DesignBlockType.Graphic.key,
            "fillType" to FillType.Image.key,
            "shapeType" to ShapeType.Rect.key,
            "kind" to "image",
            "width" to width.toString(),
            "height" to height.toString(),
        ),
        credits = AssetCredits(
            name = authorName,
            uri = authorUri?.let(Uri::parse),
        ),
        license = AssetLicense(
            name = licenseName,
            uri = licenseUri?.let(Uri::parse),
        ),
        utm = AssetUTM(source = "app-media", medium = "importer"),
    )
    // highlight-android-map-asset
}

private fun demoMediaItems() = listOf(
    BackendMediaItem(
        id = "summer-card",
        label = "Summer card",
        tags = listOf("summer", "card", "campaign"),
        groups = listOf("campaigns"),
        path = "summer-card.jpg",
        thumbPath = "summer-card-thumb.jpg",
        width = 1600,
        height = 1200,
        authorName = "Design Team",
    ),
    BackendMediaItem(
        id = "summer-banner",
        label = "Summer banner",
        tags = listOf("summer", "banner", "product"),
        groups = listOf("products"),
        path = "summer-banner.jpg",
        thumbPath = "summer-banner-thumb.jpg",
        width = 1920,
        height = 1080,
        authorName = "Design Team",
    ),
    BackendMediaItem(
        id = "summer-square",
        label = "Summer square",
        tags = listOf("summer", "square", "social"),
        groups = listOf("campaigns"),
        path = "summer-square.jpg",
        thumbPath = "summer-square-thumb.jpg",
        width = 1200,
        height = 1200,
        authorName = "Content Team",
    ),
)

data class BackendMediaItem(
    val id: String,
    val label: String,
    val tags: List<String>,
    val groups: List<String>,
    val path: String,
    val thumbPath: String,
    val width: Int,
    val height: Int,
    val authorName: String,
    val locale: String = "en",
    val mimeType: String = MimeType.JPEG.key,
    val licenseName: String = "App media license",
    val authorUri: String? = null,
    val licenseUri: String? = null,
) {
    val uri = "$DEMO_MEDIA_BASE_URI/$path"
    val thumbUri = "$DEMO_MEDIA_BASE_URI/thumbnails/$thumbPath"
}
