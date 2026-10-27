import android.graphics.PointF
import android.util.Log
import kotlinx.coroutines.withContext
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPlacement
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

private const val TAG = "DragAndDrop"
private const val PLACEMENT_SOURCE_ID = "ly.img.guide.drag-and-drop"
private const val PLACEMENT_ASSET_ID = "sample-image"
private const val PLACEMENT_ASSET_URI = "https://img.ly/static/ubq_samples/sample_3.jpg"

suspend fun dragAndDrop(engine: Engine): DesignBlock? = withContext(engine.dispatcher) {
    // highlight-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // A block lifts or receives an image when it has an image fill, is not a sticker, and its
    // `fill/change` scope is allowed. Give each block a rectangle shape and an image fill.
    fun addImage(
        uri: String,
        x: Float,
    ) {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
        val fill = engine.block.createFill(FillType.Image)
        engine.block.setString(block = fill, property = "fill/image/imageFileURI", value = uri)
        engine.block.setFill(block = block, fill = fill)
        engine.block.setWidth(block = block, value = 300F)
        engine.block.setHeight(block = block, value = 300F)
        engine.block.setPositionX(block = block, value = x)
        engine.block.setPositionY(block = block, value = 150F)
        engine.block.appendChild(parent = page, child = block)
    }

    addImage(uri = "https://img.ly/static/ubq_samples/sample_1.jpg", x = 50F)
    addImage(uri = "https://img.ly/static/ubq_samples/sample_2.jpg", x = 450F)
    // highlight-setup

    // highlight-gesture-enable
    // Let users exchange images by dragging one image block onto another.
    engine.editor.setSettingBoolean("dragToSwapFills/enabled", value = true)

    // How long the pointer stays pressed and still before the image lifts.
    // Use 0F for fixed layouts, where the image lifts at once.
    engine.editor.setSettingFloat("dragToSwapFills/longPressDurationMs", value = 500F)
    // highlight-gesture-enable

    // highlight-asset-source
    // A source with its own applyAsset function ignores the placement, so this one has none.
    engine.asset.addLocalSource(
        sourceId = PLACEMENT_SOURCE_ID,
        supportedMimeTypes = listOf(MimeType.JPEG.key),
    )
    engine.asset.addAsset(
        sourceId = PLACEMENT_SOURCE_ID,
        asset = AssetDefinition(
            id = PLACEMENT_ASSET_ID,
            label = mapOf("en" to "Sample image"),
            meta = mapOf(
                "uri" to PLACEMENT_ASSET_URI,
                "thumbUri" to PLACEMENT_ASSET_URI,
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "width" to "1667",
                "height" to "2500",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
            ),
        ),
    )

    val asset = engine.asset.fetchAsset(
        sourceId = PLACEMENT_SOURCE_ID,
        assetId = PLACEMENT_ASSET_ID,
    ) ?: error("Could not fetch the asset: $PLACEMENT_ASSET_ID")
    // highlight-asset-source

    // highlight-placement
    // Add the asset to `page`, centered at (200, 300) relative to `page`.
    // Both coordinates must be finite.
    val placedBlock = engine.asset.applyAssetSourceAsset(
        sourceId = PLACEMENT_SOURCE_ID,
        asset = asset,
        placement = AssetPlacement(parent = page, center = PointF(200F, 300F)),
    )
    Log.i(TAG, "Placed block $placedBlock")

    // A null parent adds the block to the current page.
    val defaultPlacedBlock = engine.asset.defaultApplyAsset(
        asset = asset,
        placement = AssetPlacement(parent = null, center = PointF(600F, 300F)),
    )
    Log.i(TAG, "Placed block $defaultPlacedBlock")
    // highlight-placement

    placedBlock
}
