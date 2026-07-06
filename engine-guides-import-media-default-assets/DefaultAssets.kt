import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

// highlight-android-cdn-url
fun defaultAssetsUri(cesdkVersion: String): Uri = Uri.parse(
    "https://cdn.img.ly/packages/imgly/cesdk-android/$cesdkVersion/assets",
)
// highlight-android-cdn-url

suspend fun defaultAssets(
    engine: Engine,
    assetBaseUri: Uri,
): DefaultAssetsResult {
    // highlight-android-source-groups
    val defaultAssetSourceIds = listOf(
        "ly.img.sticker",
        "ly.img.vector.shape",
        "ly.img.color.palette",
        "ly.img.filter",
        "ly.img.effect",
        "ly.img.blur",
        "ly.img.typeface",
        "ly.img.crop.presets",
        "ly.img.page.presets",
        "ly.img.text.components",
        "ly.img.text",
        "ly.img.text.styles",
        "ly.img.text.curves",
        "ly.img.caption.presets",
    )

    val demoAssetSourceIds = listOf(
        "ly.img.image",
        "ly.img.video",
        "ly.img.audio",
        "ly.img.templates",
    )
    // highlight-android-source-groups

    val sourceIdsToReset = defaultAssetSourceIds + demoAssetSourceIds + "my-json-shapes"
    sourceIdsToReset
        .filter { sourceId -> sourceId in engine.asset.findAllSources() }
        .forEach { sourceId -> engine.asset.removeSource(sourceId) }

    // highlight-android-load-default-assets
    val loadedDefaultSourceIds = defaultAssetSourceIds.map { sourceId ->
        engine.asset.addLocalSourceFromJSON(
            contentUri = assetBaseUri.buildUpon()
                .appendPath(sourceId)
                .appendPath("content.json")
                .build(),
        )
    }
    // highlight-android-load-default-assets

    // highlight-android-load-demo-assets
    val loadedDemoSourceIds = demoAssetSourceIds.map { sourceId ->
        engine.asset.addLocalSourceFromJSON(
            contentUri = assetBaseUri.buildUpon()
                .appendPath(sourceId)
                .appendPath("content.json")
                .build(),
        )
    }
    // highlight-android-load-demo-assets

    // highlight-android-json-string
    val jsonSourceId = engine.asset.addLocalSourceFromJSON(
        contentJSON = """
            {
              "version": "1.0.0",
              "id": "my-json-shapes",
              "assets": [
                {
                  "id": "brand-star",
                  "label": { "en": "Brand Star" },
                  "meta": {
                    "blockType": "${DesignBlockType.Graphic.key}",
                    "shapeType": "${ShapeType.Star.key}"
                  }
                }
              ]
            }
        """.trimIndent(),
        basePath = null,
        matcher = null,
    )
    // highlight-android-json-string

    // highlight-android-filter-assets
    val stickerSourceId = "ly.img.sticker"
    engine.asset.removeSource(sourceId = stickerSourceId)
    val filteredStickerSourceId = engine.asset.addLocalSourceFromJSON(
        contentUri = assetBaseUri.buildUpon()
            .appendPath(stickerSourceId)
            .appendPath("content.json")
            .build(),
        matcher = listOf("ly.img.sticker.emoji.happyface"),
    )

    val filteredStickerCount = engine.asset.findAssets(
        sourceId = filteredStickerSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    ).total
    // highlight-android-filter-assets

    // highlight-android-create-scene
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-create-scene

    // highlight-android-fetch-apply-assets
    val assetsToAdd = listOf(
        "ly.img.vector.shape" to "ly.img.vector.shape.filled.star",
        filteredStickerSourceId to "ly.img.sticker.emoji.happyface",
        "ly.img.image" to "ly.img.image.sample_1",
    )

    val blockSize = 140F
    val spacing = 36F
    val startX = (800F - (assetsToAdd.size * blockSize + (assetsToAdd.size - 1) * spacing)) / 2F
    val centerY = (600F - blockSize) / 2F

    val createdBlocks = assetsToAdd.mapIndexed { index, (sourceId, assetId) ->
        val asset = checkNotNull(
            engine.asset.fetchAsset(
                sourceId = sourceId,
                assetId = assetId,
            ),
        ) {
            "Missing asset $assetId from $sourceId."
        }

        val block = checkNotNull(
            engine.asset.applyAssetSourceAsset(
                sourceId = sourceId,
                asset = asset,
            ),
        ) {
            "Asset $assetId did not create a block."
        }

        engine.block.setWidth(block, value = blockSize)
        engine.block.setHeight(block, value = blockSize)
        engine.block.setPositionX(block, value = startX + index * (blockSize + spacing))
        engine.block.setPositionY(block, value = centerY)
        block
    }
    // highlight-android-fetch-apply-assets

    // highlight-android-export
    engine.block.forceLoadResources(blocks = listOf(page) + createdBlocks)
    val exportedPng = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 800F, targetHeight = 600F),
    )
    // highlight-android-export
    check(exportedPng.hasRemaining()) { "Default assets PNG export is empty." }

    return DefaultAssetsResult(
        loadedDefaultSourceIds = loadedDefaultSourceIds,
        loadedDemoSourceIds = loadedDemoSourceIds,
        jsonSourceId = jsonSourceId,
        filteredStickerCount = filteredStickerCount,
        createdBlocks = createdBlocks,
        exportedPng = exportedPng.asReadOnlyBuffer(),
    )
}
