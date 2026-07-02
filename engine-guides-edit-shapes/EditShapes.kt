import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import kotlin.math.PI
import kotlin.math.abs

suspend fun editShapes(engine: Engine): EditShapesResult {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val graphic = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(graphic, shape = rectShape)

    val initialFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(graphic, fill = initialFill)
    engine.block.setColor(
        block = initialFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.93F, g = 0.94F, b = 0.96F, a = 1F),
    )

    engine.block.setWidth(graphic, value = 240F)
    engine.block.setHeight(graphic, value = 160F)
    engine.block.setPositionX(graphic, value = 80F)
    engine.block.setPositionY(graphic, value = 80F)
    engine.block.appendChild(parent = page, child = graphic)

    // highlight-android-access-shapes
    val supportsGraphicShape = engine.block.supportsShape(block = graphic)
    val text = engine.block.create(DesignBlockType.Text)
    val supportsTextShape = engine.block.supportsShape(block = text)

    val currentShape = engine.block.getShape(block = graphic)
    val initialShapeType = engine.block.getType(block = currentShape)
    // highlight-android-access-shapes
    check(supportsGraphicShape)
    check(!supportsTextShape)
    check(initialShapeType == ShapeType.Rect.key)
    engine.block.destroy(text)

    // highlight-android-rounded-corners
    engine.block.setFloat(block = currentShape, property = "shape/rect/cornerRadiusTL", value = 24F)
    engine.block.setFloat(block = currentShape, property = "shape/rect/cornerRadiusTR", value = 24F)
    engine.block.setFloat(block = currentShape, property = "shape/rect/cornerRadiusBR", value = 24F)
    engine.block.setFloat(block = currentShape, property = "shape/rect/cornerRadiusBL", value = 24F)
    // highlight-android-rounded-corners
    val cornerRadiusTopLeft = engine.block.getFloat(block = currentShape, property = "shape/rect/cornerRadiusTL")
    check(abs(cornerRadiusTopLeft - 24F) < 0.001F)

    // highlight-android-replace-shape
    val starShape = engine.block.createShape(type = ShapeType.Star)
    engine.block.setShape(block = graphic, shape = starShape)
    engine.block.destroy(block = currentShape)

    val replacementShapeType = engine.block.getType(block = engine.block.getShape(block = graphic))
    // highlight-android-replace-shape
    check(replacementShapeType == ShapeType.Star.key)

    // highlight-android-shape-properties
    val starProperties = engine.block.findAllProperties(block = starShape)
    engine.block.setInt(block = starShape, property = "shape/star/points", value = 6)
    engine.block.setFloat(block = starShape, property = "shape/star/innerDiameter", value = 0.45F)
    // highlight-android-shape-properties
    val starPoints = engine.block.getInt(block = starShape, property = "shape/star/points")
    val starInnerDiameter = engine.block.getFloat(block = starShape, property = "shape/star/innerDiameter")
    check("shape/star/points" in starProperties)
    check(starPoints == 6)
    check(abs(starInnerDiameter - 0.45F) < 0.001F)

    // highlight-android-fill-color
    val fill = engine.block.getFill(block = graphic)
    val fillColor = Color.fromRGBA(r = 0.1F, g = 0.46F, b = 0.85F, a = 1F)
    engine.block.setColor(block = fill, property = "fill/color/value", value = fillColor)
    // highlight-android-fill-color
    val appliedFillColor = engine.block.getColor(block = fill, property = "fill/color/value") as RGBAColor
    check(appliedFillColor == fillColor)

    // highlight-android-transform-shape
    engine.block.setPositionX(block = graphic, value = 160F)
    engine.block.setPositionY(block = graphic, value = 120F)
    engine.block.setWidth(block = graphic, value = 320F)
    engine.block.setHeight(block = graphic, value = 220F)
    val positionX = engine.block.getPositionX(block = graphic)
    val positionY = engine.block.getPositionY(block = graphic)
    val width = engine.block.getWidth(block = graphic)
    val height = engine.block.getHeight(block = graphic)

    val rotationRadians = (15F * PI / 180F).toFloat()
    engine.block.setRotation(block = graphic, radians = rotationRadians)
    // highlight-android-transform-shape
    val rotation = engine.block.getRotation(block = graphic)
    check(abs(positionX - 160F) < 0.001F)
    check(abs(positionY - 120F) < 0.001F)
    check(abs(width - 320F) < 0.001F)
    check(abs(height - 220F) < 0.001F)
    check(abs(rotation - rotationRadians) < 0.001F)

    val previewPngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 640F, targetHeight = 480F),
    )
    check(previewPngData.hasRemaining()) { "edit shapes preview export is empty" }

    return EditShapesResult(
        supportsGraphicShape = supportsGraphicShape,
        supportsTextShape = supportsTextShape,
        initialShapeType = initialShapeType,
        cornerRadiusTopLeft = cornerRadiusTopLeft,
        replacementShapeType = replacementShapeType,
        starProperties = starProperties,
        starPoints = starPoints,
        starInnerDiameter = starInnerDiameter,
        fillColor = appliedFillColor,
        positionX = positionX,
        positionY = positionY,
        width = width,
        height = height,
        rotation = rotation,
        previewPngData = previewPngData,
    )
}
