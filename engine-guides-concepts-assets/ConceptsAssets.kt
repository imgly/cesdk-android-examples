import android.net.Uri
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
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
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.util.UUID

suspend fun conceptsAssets(engine: Engine) = withContext(engine.dispatcher) {
    val sourceEventJobs = mutableListOf<Job>()
    val invocationId = UUID.randomUUID()
    val source = BrandedAssetSource(sourceId = "ly.img.asset.source.branded.$invocationId")
    val localSourceId = "my-local-images.$invocationId"
    val registeredGuideSourceIds = mutableListOf<String>()

    try {
        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)

        // highlight-android-concepts-assets-source-events
        sourceEventJobs += engine.asset.onAssetSourceAdded()
            .onEach { println("Asset source added: $it") }
            .launchIn(this)

        sourceEventJobs += engine.asset.onAssetSourceRemoved()
            .onEach { println("Asset source removed: $it") }
            .launchIn(this)

        sourceEventJobs += engine.asset.onAssetSourceUpdated()
            .onEach { println("Asset source updated: $it") }
            .launchIn(this)
        // highlight-android-concepts-assets-source-events

        engine.asset.addSource(source)
        registeredGuideSourceIds += source.sourceId

        // highlight-android-concepts-assets-query-assets
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
        // highlight-android-concepts-assets-query-assets

        // highlight-android-concepts-assets-apply-asset
        val appliedBlock = engine.asset.applyAssetSourceAsset(
            sourceId = source.sourceId,
            asset = queriedAsset,
        )
        if (appliedBlock != null) {
            engine.block.setPositionX(appliedBlock, 64F)
            engine.block.setPositionY(appliedBlock, 64F)
        }
        // highlight-android-concepts-assets-apply-asset

        if (appliedBlock != null) {
            engine.block.forceLoadResources(listOf(appliedBlock))
        }

        // highlight-android-concepts-assets-local-source
        engine.asset.addLocalSource(
            sourceId = localSourceId,
            supportedMimeTypes = listOf(MimeType.JPEG.key),
        )
        registeredGuideSourceIds += localSourceId

        val localAsset = AssetDefinition(
            id = "sunrise-poster",
            label = mapOf("en" to "Sunrise Poster"),
            tags = mapOf("en" to listOf("poster", "sunrise", "brand")),
            groups = listOf("posters"),
            meta = mapOf(
                "uri" to "https://img.ly/static/ubq_samples/sample_1.jpg",
                "thumbUri" to "https://img.ly/static/ubq_samples/sample_1.jpg",
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "1080",
            ),
        )
        engine.asset.addAsset(sourceId = localSourceId, asset = localAsset)
        engine.asset.assetSourceContentsChanged(sourceId = localSourceId)
        // highlight-android-concepts-assets-local-source
    } finally {
        withContext(NonCancellable) {
            try {
                removeRegisteredGuideSources(engine = engine, sourceIds = registeredGuideSourceIds)
            } finally {
                sourceEventJobs.forEach { it.cancel() }
                sourceEventJobs.forEach { it.join() }
            }
        }
    }
}

private fun removeRegisteredGuideSources(
    engine: Engine,
    sourceIds: List<String>,
) {
    var cleanupFailure: Throwable? = null

    sourceIds.asReversed().forEach { sourceId ->
        try {
            engine.asset.removeSource(sourceId)
        } catch (throwable: Throwable) {
            val previousFailure = cleanupFailure
            if (previousFailure == null) {
                cleanupFailure = throwable
            } else {
                previousFailure.addSuppressed(throwable)
            }
        }
    }

    cleanupFailure?.let { throw it }
}

private class BrandedAssetSource(
    sourceId: String,
) : AssetSource(sourceId = sourceId) {
    override val supportedMimeTypes = listOf(MimeType.JPEG.key)

    override val credits = AssetCredits(
        name = "IMG.LY",
        uri = Uri.parse("https://img.ly/"),
    )

    override val license = AssetLicense(
        name = "Sample content",
        uri = Uri.parse("https://img.ly/legal/"),
    )

    // highlight-android-concepts-assets-asset-source
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
    // highlight-android-concepts-assets-asset-source

    private val brandedAssets = listOf(
        // highlight-android-concepts-assets-asset-definition
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
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "640",
                "height" to "320",
            ),
        ),
        // highlight-android-concepts-assets-asset-definition
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
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "720",
            ),
        ),
    )
}
