import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

data class AutoResizeMetrics(
    val titleWidth: Float,
    val titleHeight: Float,
    val subtitleWidth: Float,
    val titleWidthMode: SizeMode,
    val titleHeightMode: SizeMode,
    val backgroundWidthMode: SizeMode,
    val backgroundHeightMode: SizeMode,
)

fun autoResize(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): Job = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.autoResize")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    try {
        runAutoResizeGuide(engine)
    } finally {
        engine.stop()
    }
}

suspend fun runAutoResizeGuide(engine: Engine): AutoResizeMetrics {
    // highlight-android-setup
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-setup

    // highlight-android-auto-mode
    val titleBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(titleBlock, text = "Auto-Resize Demo")
    engine.block.setFloat(titleBlock, property = "text/fontSize", value = 64F)
    engine.block.setWidthMode(titleBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(titleBlock, mode = SizeMode.AUTO)
    engine.block.appendChild(parent = page, child = titleBlock)
    // highlight-android-auto-mode

    val coverBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(coverBlock, shape = engine.block.createShape(ShapeType.Rect))
    val coverFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        coverFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 0.08F),
    )
    engine.block.setFill(coverBlock, fill = coverFill)
    engine.block.appendChild(parent = page, child = coverBlock)
    // highlight-android-fill-parent
    engine.block.fillParent(coverBlock)
    // highlight-android-fill-parent
    engine.block.destroy(coverBlock)

    yield()

    // highlight-android-read-frame-dimensions
    val titleWidth = engine.block.getFrameWidth(titleBlock)
    val titleHeight = engine.block.getFrameHeight(titleBlock)
    println("Title dimensions: ${titleWidth.toInt()}x${titleHeight.toInt()} pixels")
    // highlight-android-read-frame-dimensions

    // highlight-android-center-block
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    val centerX = (pageWidth - titleWidth) / 2F
    val centerY = (pageHeight - titleHeight) / 2F - 100F
    engine.block.setPositionX(titleBlock, value = centerX)
    engine.block.setPositionY(titleBlock, value = centerY)
    // highlight-android-center-block

    // highlight-android-percent-mode
    val backgroundBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(backgroundBlock, shape = engine.block.createShape(ShapeType.Rect))
    val backgroundFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        backgroundFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 0.3F),
    )
    engine.block.setFill(backgroundBlock, fill = backgroundFill)
    engine.block.setWidthMode(backgroundBlock, mode = SizeMode.PERCENT)
    engine.block.setHeightMode(backgroundBlock, mode = SizeMode.PERCENT)
    engine.block.setWidth(backgroundBlock, value = 0.8F)
    engine.block.setHeight(backgroundBlock, value = 0.3F)
    engine.block.setPositionX(backgroundBlock, value = pageWidth * 0.1F)
    engine.block.setPositionY(backgroundBlock, value = pageHeight * 0.6F)
    engine.block.appendChild(parent = page, child = backgroundBlock)
    engine.block.sendToBack(backgroundBlock)
    // highlight-android-percent-mode

    // highlight-android-subtitle-auto
    val subtitleBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(subtitleBlock, text = "Text automatically sizes to fit content")
    engine.block.setFloat(subtitleBlock, property = "text/fontSize", value = 32F)
    engine.block.setWidthMode(subtitleBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(subtitleBlock, mode = SizeMode.AUTO)
    engine.block.appendChild(parent = page, child = subtitleBlock)

    yield()

    val subtitleWidth = engine.block.getFrameWidth(subtitleBlock)
    val subtitleCenterX = (pageWidth - subtitleWidth) / 2F
    engine.block.setPositionX(subtitleBlock, value = subtitleCenterX)
    engine.block.setPositionY(subtitleBlock, value = pageHeight * 0.7F)
    // highlight-android-subtitle-auto

    // highlight-android-check-modes
    val titleWidthMode = engine.block.getWidthMode(titleBlock)
    val titleHeightMode = engine.block.getHeightMode(titleBlock)
    val backgroundWidthMode = engine.block.getWidthMode(backgroundBlock)
    val backgroundHeightMode = engine.block.getHeightMode(backgroundBlock)
    println("Title modes: width=$titleWidthMode, height=$titleHeightMode")
    println("Background modes: width=$backgroundWidthMode, height=$backgroundHeightMode")
    // highlight-android-check-modes

    return AutoResizeMetrics(
        titleWidth = titleWidth,
        titleHeight = titleHeight,
        subtitleWidth = subtitleWidth,
        titleWidthMode = titleWidthMode,
        titleHeightMode = titleHeightMode,
        backgroundWidthMode = backgroundWidthMode,
        backgroundHeightMode = backgroundHeightMode,
    )
}
