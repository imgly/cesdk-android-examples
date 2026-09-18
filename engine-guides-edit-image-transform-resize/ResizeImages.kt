import android.net.Uri
import kotlinx.coroutines.yield
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.HandleVisibility
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.getResizeHandlesVisibility
import ly.img.engine.setResizeHandlesVisibility

data class ResizeImages(
    val absoluteWidth: Float,
    val absoluteHeight: Float,
    val percentWidth: Float,
    val percentHeight: Float,
    val percentWidthMode: SizeMode,
    val percentHeightMode: SizeMode,
    val frameWidth: Float,
    val frameHeight: Float,
    val cropModeAfterResize: ContentFillMode,
    val groupWidth: Float,
    val pageWidthAfterContentAwareResize: Float,
    val resizeHandlesVisibility: HandleVisibility,
    val resizeScopeEnabled: Boolean,
    val transformLocked: Boolean,
)

suspend fun resizeImages(engine: Engine): ResizeImages {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = createImageBlock(engine, page)

    // highlight-android-resize-handles
    engine.editor.setResizeHandlesVisibility(HandleVisibility.ALWAYS)

    val resizeHandlesVisibility = engine.editor.getResizeHandlesVisibility()
    // highlight-android-resize-handles

    // highlight-android-absolute-size
    engine.block.setWidthMode(imageBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(imageBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(imageBlock, value = 400F)
    engine.block.setHeight(imageBlock, value = 300F)

    val absoluteWidth = engine.block.getWidth(imageBlock)
    val absoluteHeight = engine.block.getHeight(imageBlock)
    // highlight-android-absolute-size

    // highlight-android-percent-size
    engine.block.setWidthMode(imageBlock, mode = SizeMode.PERCENT)
    engine.block.setHeightMode(imageBlock, mode = SizeMode.PERCENT)
    engine.block.setWidth(imageBlock, value = 0.5F)
    engine.block.setHeight(imageBlock, value = 0.5F)

    val percentWidth = engine.block.getWidth(imageBlock)
    val percentHeight = engine.block.getHeight(imageBlock)
    val percentWidthMode = engine.block.getWidthMode(imageBlock)
    val percentHeightMode = engine.block.getHeightMode(imageBlock)
    // highlight-android-percent-size

    // Let the offscreen engine resolve one layout pass before reading frame dimensions.
    yield()

    // highlight-android-frame-dimensions
    val frameWidth = engine.block.getFrameWidth(imageBlock)
    val frameHeight = engine.block.getFrameHeight(imageBlock)
    // highlight-android-frame-dimensions

    // highlight-android-maintain-crop
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.CROP)
    engine.block.setWidthMode(imageBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(imageBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(imageBlock, value = 520F, maintainCrop = true)
    engine.block.setHeight(imageBlock, value = 320F, maintainCrop = true)

    val cropModeAfterResize = engine.block.getContentFillMode(imageBlock)
    // highlight-android-maintain-crop

    val secondImageBlock = createImageBlock(engine, page).also { block ->
        engine.block.setPositionX(block, value = 460F)
    }

    // highlight-android-group-resize
    val group = engine.block.group(listOf(imageBlock, secondImageBlock))
    engine.block.setWidth(group, value = 600F)

    val groupWidth = engine.block.getWidth(group)
    // highlight-android-group-resize

    // highlight-android-content-aware-resize
    engine.block.resizeContentAware(blocks = listOf(page), width = 1080F, height = 1080F)

    val pageWidthAfterContentAwareResize = engine.block.getWidth(page)
    // highlight-android-content-aware-resize

    // highlight-android-lock-resize
    engine.block.setScopeEnabled(block = group, key = "layer/resize", enabled = false)
    val resizeScopeEnabled = engine.block.isScopeEnabled(block = group, key = "layer/resize")

    engine.block.setTransformLocked(block = group, locked = true)
    val transformLocked = engine.block.isTransformLocked(group)
    // highlight-android-lock-resize

    return ResizeImages(
        absoluteWidth = absoluteWidth,
        absoluteHeight = absoluteHeight,
        percentWidth = percentWidth,
        percentHeight = percentHeight,
        percentWidthMode = percentWidthMode,
        percentHeightMode = percentHeightMode,
        frameWidth = frameWidth,
        frameHeight = frameHeight,
        cropModeAfterResize = cropModeAfterResize,
        groupWidth = groupWidth,
        pageWidthAfterContentAwareResize = pageWidthAfterContentAwareResize,
        resizeHandlesVisibility = resizeHandlesVisibility,
        resizeScopeEnabled = resizeScopeEnabled,
        transformLocked = transformLocked,
    )
}

// highlight-android-create-image-block
private fun createImageBlock(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(imageBlock, value = 320F)
    engine.block.setHeight(imageBlock, value = 240F)
    engine.block.setPositionX(imageBlock, value = 120F)
    engine.block.setPositionY(imageBlock, value = 120F)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.appendChild(parent = page, child = imageBlock)

    return imageBlock
}
// highlight-android-create-image-block
