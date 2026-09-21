import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ly.img.editor.defaultBaseUri
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

private const val TAG = "EditOrRemoveAssets"

suspend fun editOrRemoveAssets(
    engine: Engine,
    assetBaseUri: Uri = defaultBaseUri,
): EditOrRemoveAssetsResult = coroutineScope {
    val sourceId = "my-local-images"
    val temporarySourceId = "temporary-session-images"
    val assetSourceLifecycleScope = this
    val sourceObserverJob = observeAssetSourceUpdates(
        engine = engine,
        sourceId = sourceId,
        assetSourceLifecycleScope = assetSourceLifecycleScope,
    )

    try {
        listOf(sourceId, temporarySourceId).forEach { staleSourceId ->
            if (staleSourceId in engine.asset.findAllSources()) {
                engine.asset.removeSource(sourceId = staleSourceId)
            }
        }

        engine.asset.addLocalSource(
            sourceId = sourceId,
            supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key),
        )

        val logoUri = assetBaseUri.buildUpon()
            .appendPath("ly.img.image")
            .appendPath("images")
            .appendPath("sample_1.jpg")
            .build()
        val logoThumbnailUri = assetBaseUri.buildUpon()
            .appendPath("ly.img.image")
            .appendPath("thumbnails")
            .appendPath("sample_1.jpg")
            .build()
        val backgroundUri = assetBaseUri.buildUpon()
            .appendPath("ly.img.image")
            .appendPath("images")
            .appendPath("sample_4.jpg")
            .build()
        val backgroundThumbnailUri = assetBaseUri.buildUpon()
            .appendPath("ly.img.image")
            .appendPath("thumbnails")
            .appendPath("sample_4.jpg")
            .build()

        val logoAsset = AssetDefinition(
            id = "brand-logo",
            label = mapOf("en" to "Brand Logo"),
            tags = mapOf("en" to listOf("logo", "brand")),
            groups = listOf("brand"),
            meta = mapOf(
                "uri" to logoUri.toString(),
                "thumbUri" to logoThumbnailUri.toString(),
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "720",
            ),
        )
        val backgroundAsset = AssetDefinition(
            id = "campaign-background",
            label = mapOf("en" to "Campaign Background"),
            tags = mapOf("en" to listOf("campaign", "background")),
            groups = listOf("campaign"),
            meta = mapOf(
                "uri" to backgroundUri.toString(),
                "thumbUri" to backgroundThumbnailUri.toString(),
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "1440",
            ),
        )

        engine.asset.addAsset(sourceId = sourceId, asset = logoAsset)
        engine.asset.addAsset(sourceId = sourceId, asset = backgroundAsset)

        // highlight-android-find-assets
        val queryResult = engine.asset.findAssets(
            sourceId = sourceId,
            query = FindAssetsQuery(
                page = 0,
                perPage = 100,
                query = "logo",
                locale = "en",
            ),
        )
        val assetToEdit = queryResult.assets.firstOrNull { asset -> asset.id == "brand-logo" }
            ?: error("Expected brand-logo in $sourceId.")
        // highlight-android-find-assets

        val initialAssetIds = engine.asset.findAssets(
            sourceId = sourceId,
            query = FindAssetsQuery(page = 0, perPage = 100),
        ).assets.map { asset -> asset.id }

        // highlight-android-update-metadata
        val updatedLogoAsset = logoAsset.copy(
            label = mapOf("en" to "Primary Brand Logo"),
            tags = mapOf("en" to listOf("logo", "brand", "primary")),
        )

        engine.asset.removeAsset(sourceId = sourceId, assetId = assetToEdit.id)
        engine.asset.addAsset(sourceId = sourceId, asset = updatedLogoAsset)
        // highlight-android-update-metadata

        val updatedAsset = engine.asset.findAssets(
            sourceId = sourceId,
            query = FindAssetsQuery(page = 0, perPage = 10, query = "primary", locale = "en"),
        ).assets.firstOrNull { asset -> asset.id == updatedLogoAsset.id }
            ?: error("Expected updated logo asset in $sourceId.")

        // highlight-android-remove-asset
        engine.asset.removeAsset(
            sourceId = sourceId,
            assetId = "campaign-background",
        )
        // highlight-android-remove-asset

        val remainingAssetIds = engine.asset.findAssets(
            sourceId = sourceId,
            query = FindAssetsQuery(page = 0, perPage = 100),
        ).assets.map { asset -> asset.id }

        engine.asset.addLocalSource(
            sourceId = temporarySourceId,
            supportedMimeTypes = listOf(MimeType.JPEG.key),
        )

        // highlight-android-notify-source
        // Use this after changing backing data outside addAsset/removeAsset.
        engine.asset.assetSourceContentsChanged(sourceId = temporarySourceId)
        // highlight-android-notify-source

        // highlight-android-remove-source
        engine.asset.removeSource(sourceId = temporarySourceId)
        // highlight-android-remove-source

        val temporarySourceExistsAfterRemoval = temporarySourceId in engine.asset.findAllSources()
        val removedAssetWasPresent = remainingAssetIds.contains("campaign-background")

        engine.asset.removeSource(sourceId = sourceId)
        val managedSourcesCleanedUp = listOf(sourceId, temporarySourceId).none { managedSourceId ->
            managedSourceId in engine.asset.findAllSources()
        }

        EditOrRemoveAssetsResult(
            queriedAssetId = assetToEdit.id,
            initialAssetIds = initialAssetIds,
            updatedLabel = updatedAsset.label,
            remainingAssetIds = remainingAssetIds,
            removedAssetWasPresent = removedAssetWasPresent,
            temporarySourceExistsAfterRemoval = temporarySourceExistsAfterRemoval,
            managedSourcesCleanedUp = managedSourcesCleanedUp,
        )
    } finally {
        listOf(sourceId, temporarySourceId).forEach { managedSourceId ->
            if (managedSourceId in engine.asset.findAllSources()) {
                engine.asset.removeSource(sourceId = managedSourceId)
            }
        }
        sourceObserverJob.cancelAndJoin()
    }
}

fun observeAssetSourceUpdates(
    engine: Engine,
    sourceId: String,
    assetSourceLifecycleScope: CoroutineScope,
): Job {
    // highlight-android-source-events
    return engine.asset.onAssetSourceUpdated()
        .onEach { updatedSourceId ->
            if (updatedSourceId == sourceId) {
                Log.i(TAG, "Asset source updated: $updatedSourceId")
            }
        }
        .launchIn(assetSourceLifecycleScope)
    // highlight-android-source-events
}
