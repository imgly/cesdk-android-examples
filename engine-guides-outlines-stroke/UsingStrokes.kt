import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.StrokeCornerGeometry
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle

fun usingStrokes(engine: Engine): UsingStrokesResult? {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block, value = 400F)
    engine.block.setHeight(block, value = 300F)
    engine.block.setPositionX(block, value = 200F)
    engine.block.setPositionY(block, value = 150F)
    engine.block.setFill(block, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = block,
        color = Color.fromRGBA(r = 0.95F, g = 0.95F, b = 0.95F, a = 1F),
    )
    engine.block.appendChild(parent = page, child = block)

    // highlight-android-check-support
    val canHaveStroke = engine.block.supportsStroke(block = block)
    if (!canHaveStroke) {
        return null
    }
    // highlight-android-check-support

    // highlight-android-enable-stroke
    engine.block.setStrokeEnabled(block = block, enabled = true)
    val strokeIsEnabled = engine.block.isStrokeEnabled(block = block)
    // highlight-android-enable-stroke

    // highlight-android-stroke-color
    engine.block.setStrokeColor(
        block = block,
        color = Color.fromRGBA(r = 0F, g = 0.4F, b = 0.9F, a = 1F),
    )
    val strokeColor = engine.block.getStrokeColor(block = block)
    // highlight-android-stroke-color

    // highlight-android-stroke-width
    engine.block.setStrokeWidth(block = block, width = 8F)
    val strokeWidth = engine.block.getStrokeWidth(block = block)
    // highlight-android-stroke-width

    // highlight-android-stroke-style
    engine.block.setStrokeStyle(block = block, style = StrokeStyle.DASHED)
    val strokeStyle = engine.block.getStrokeStyle(block = block)
    // highlight-android-stroke-style

    // highlight-android-stroke-position
    engine.block.setStrokePosition(block = block, position = StrokePosition.OUTER)
    val strokePosition = engine.block.getStrokePosition(block = block)
    // highlight-android-stroke-position

    // highlight-android-stroke-corner
    engine.block.setStrokeCornerGeometry(block = block, geometry = StrokeCornerGeometry.ROUND)
    val strokeCornerGeometry = engine.block.getStrokeCornerGeometry(block = block)
    // highlight-android-stroke-corner

    check(strokeIsEnabled)
    check(strokeColor is RGBAColor)
    check(strokeWidth > 0F)
    check(strokeStyle == StrokeStyle.DASHED)
    check(strokePosition == StrokePosition.OUTER)
    check(strokeCornerGeometry == StrokeCornerGeometry.ROUND)

    return UsingStrokesResult(
        canHaveStroke = canHaveStroke,
        strokeIsEnabled = engine.block.isStrokeEnabled(block = block),
        strokeColor = engine.block.getStrokeColor(block = block),
        strokeWidth = engine.block.getStrokeWidth(block = block),
        strokeStyle = engine.block.getStrokeStyle(block = block),
        strokePosition = engine.block.getStrokePosition(block = block),
        strokeCornerGeometry = engine.block.getStrokeCornerGeometry(block = block),
    )
}
