import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetLicense
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FetchAssetOptions
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.ShapeType

fun conceptsAssets(
    license: String?, // Pass null for evaluation mode or your production key in app code.
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)
    val sourceEventJobs = mutableListOf<Job>()

    try {
        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)

        // highlight-conceptsAssets-sourceEvents
        sourceEventJobs += engine.asset.onAssetSourceAdded()
            .onEach { println("Asset source added: $it") }
            .launchIn(this)

        sourceEventJobs += engine.asset.onAssetSourceRemoved()
            .onEach { println("Asset source removed: $it") }
            .launchIn(this)

        sourceEventJobs += engine.asset.onAssetSourceUpdated()
            .onEach { println("Asset source updated: $it") }
            .launchIn(this)
        // highlight-conceptsAssets-sourceEvents

        val source = BrandedAssetSource()
        engine.asset.addSource(source)

        // highlight-conceptsAssets-queryAssets
        val queriedAssets = engine.asset.findAssets(
            sourceId = source.sourceId,
            query = FindAssetsQuery(
                perPage = 10,
                page = 0,
                query = "logo",
                groups = listOf("logos"),
            ),
        )
        val queriedAsset = queriedAssets.assets.first()
        val groups = engine.asset.getGroups(sourceId = source.sourceId)
        println("Found ${queriedAssets.total} assets in groups $groups")
        // highlight-conceptsAssets-queryAssets

        // highlight-conceptsAssets-applyAsset
        val appliedBlock = engine.asset.applyAssetSourceAsset(
            sourceId = source.sourceId,
            asset = queriedAsset,
        )
        if (appliedBlock != null) {
            engine.block.setPositionX(appliedBlock, 64F)
            engine.block.setPositionY(appliedBlock, 64F)
        }
        // highlight-conceptsAssets-applyAsset

        // highlight-conceptsAssets-localSource
        engine.asset.addLocalSource(
            sourceId = "my-local-images",
            supportedMimeTypes = listOf("image/jpeg"),
        )

        val localAsset = AssetDefinition(
            id = "sunrise-poster",
            label = mapOf("en" to "Sunrise Poster"),
            tags = mapOf("en" to listOf("poster", "sunrise", "brand")),
            groups = listOf("posters"),
            meta = mapOf(
                "uri" to "https://img.ly/static/ubq_samples/sample_1.jpg",
                "thumbUri" to "https://img.ly/static/ubq_samples/sample_1.jpg",
                "mimeType" to "image/jpeg",
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "1080",
            ),
        )
        engine.asset.addAsset(sourceId = "my-local-images", asset = localAsset)
        engine.asset.assetSourceContentsChanged(sourceId = "my-local-images")
        // highlight-conceptsAssets-localSource

        engine.asset.removeSource(sourceId = "my-local-images")
        engine.asset.removeSource(sourceId = source.sourceId)
    } finally {
        sourceEventJobs.forEach { it.cancelAndJoin() }
        engine.stop()
    }
}

private class BrandedAssetSource : AssetSource(sourceId = SOURCE_ID) {
    override val supportedMimeTypes = listOf("image/jpeg")

    override val credits = AssetCredits(
        name = "IMG.LY",
        uri = Uri.parse("https://img.ly/"),
    )

    override val license = AssetLicense(
        name = "Sample content",
        uri = Uri.parse("https://img.ly/legal/"),
    )

    // highlight-conceptsAssets-assetSource
    override suspend fun getGroups(): List<String>? = brandedAssets.flatMap { it.groups.orEmpty() }.distinct()

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val searchQuery = query.query
        val queryGroups = query.groups.orEmpty()
        val filteredAssets = brandedAssets.filter { asset ->
            val matchesQuery =
                searchQuery.isNullOrBlank() ||
                    buildList {
                        asset.label?.let(::add)
                        addAll(asset.tags.orEmpty())
                    }.any { value ->
                        value.contains(searchQuery, ignoreCase = true)
                    }

            val matchesGroups =
                queryGroups.isEmpty() ||
                    asset.groups.orEmpty().any(queryGroups::contains)

            matchesQuery && matchesGroups
        }
        val startIndex = query.page * query.perPage
        val pageAssets = filteredAssets.drop(startIndex).take(query.perPage)
        val nextPage =
            if (startIndex + pageAssets.size < filteredAssets.size) {
                query.page + 1
            } else {
                -1
            }

        return FindAssetsResult(
            assets = pageAssets,
            currentPage = query.page,
            nextPage = nextPage,
            total = filteredAssets.size,
        )
    }

    override suspend fun fetchAsset(
        id: String,
        options: FetchAssetOptions,
    ): Asset? = brandedAssets.firstOrNull { it.id == id }
    // highlight-conceptsAssets-assetSource

    private val brandedAssets = listOf(
        // highlight-conceptsAssets-assetDefinition
        Asset(
            id = "imgly-logo",
            context = AssetContext(sourceId = sourceId),
            label = "IMG.LY Logo",
            locale = "en",
            tags = listOf("logo", "brand", "header"),
            groups = listOf("logos"),
            meta = mapOf(
                "uri" to "https://img.ly/static/ubq_samples/imgly_logo.jpg",
                "thumbUri" to "https://img.ly/static/ubq_samples/imgly_logo.jpg",
                "mimeType" to "image/jpeg",
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "640",
                "height" to "320",
            ),
        ),
        // highlight-conceptsAssets-assetDefinition
        Asset(
            id = "brand-background",
            context = AssetContext(sourceId = sourceId),
            label = "Brand Background",
            locale = "en",
            tags = listOf("background", "brand", "hero"),
            groups = listOf("backgrounds"),
            meta = mapOf(
                "uri" to "https://img.ly/static/ubq_samples/sample_4.jpg",
                "thumbUri" to "https://img.ly/static/ubq_samples/sample_4.jpg",
                "mimeType" to "image/jpeg",
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "720",
            ),
        ),
    )

    private companion object {
        const val SOURCE_ID = "ly.img.asset.source.branded"
    }
}
