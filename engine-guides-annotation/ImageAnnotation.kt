import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.StrokeStyle

data class ImageAnnotationResult(
    val page: DesignBlock,
    val highlight: DesignBlock,
    val callout: DesignBlock,
    val underline: DesignBlock,
    val redaction: DesignBlock,
)

fun imageAnnotation(engine: Engine): ImageAnnotationResult {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)

    val imageArea = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageArea, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(imageArea, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = imageArea,
        color = Color.fromRGBA(r = 0.92F, g = 0.94F, b = 0.96F, a = 1F),
    )
    engine.block.setPositionX(imageArea, value = 40F)
    engine.block.setPositionY(imageArea, value = 40F)
    engine.block.setWidth(imageArea, value = 720F)
    engine.block.setHeight(imageArea, value = 520F)
    engine.block.appendChild(parent = page, child = imageArea)

    val highlight = addRectangleAnnotation(engine = engine, page = page)
    val callout = addCircleAnnotation(engine = engine, page = page)
    val underline = addLineAnnotation(engine = engine, page = page)
    val redaction = addRedactionBox(engine = engine, page = page)
    styleRectangleAnnotationAppearance(engine = engine, rectangle = highlight)

    check(engine.block.isValid(highlight))
    check(engine.block.isValid(callout))
    check(engine.block.isValid(underline))
    check(engine.block.isValid(redaction))
    check(engine.block.getOpacity(highlight) == 0.5F)
    check(engine.block.isStrokeEnabled(callout))

    return ImageAnnotationResult(
        page = page,
        highlight = highlight,
        callout = callout,
        underline = underline,
        redaction = redaction,
    )
}

// highlight-android-rectangle-annotation
fun addRectangleAnnotation(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val highlight = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = highlight, shape = rectShape)

    engine.block.setPositionX(highlight, value = 100F)
    engine.block.setPositionY(highlight, value = 100F)
    engine.block.setWidth(highlight, value = 220F)
    engine.block.setHeight(highlight, value = 90F)

    engine.block.setFill(highlight, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = highlight,
        color = Color.fromRGBA(r = 1F, g = 0.82F, b = 0F, a = 0.4F),
    )

    engine.block.appendChild(parent = page, child = highlight)
    return highlight
}
// highlight-android-rectangle-annotation

// highlight-android-circle-annotation
fun addCircleAnnotation(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val callout = engine.block.create(DesignBlockType.Graphic)
    val ellipseShape = engine.block.createShape(ShapeType.Ellipse)
    engine.block.setShape(block = callout, shape = ellipseShape)

    engine.block.setPositionX(callout, value = 360F)
    engine.block.setPositionY(callout, value = 155F)
    engine.block.setWidth(callout, value = 120F)
    engine.block.setHeight(callout, value = 120F)

    engine.block.setFillEnabled(block = callout, enabled = false)
    engine.block.setStrokeEnabled(block = callout, enabled = true)
    engine.block.setStrokeColor(
        block = callout,
        color = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
    )
    engine.block.setStrokeWidth(block = callout, width = 4F)

    engine.block.appendChild(parent = page, child = callout)
    return callout
}
// highlight-android-circle-annotation

// highlight-android-line-annotation
fun addLineAnnotation(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val underline = engine.block.create(DesignBlockType.Graphic)
    val lineShape = engine.block.createShape(ShapeType.Line)
    engine.block.setShape(block = underline, shape = lineShape)

    engine.block.setPositionX(underline, value = 85F)
    engine.block.setPositionY(underline, value = 430F)
    engine.block.setWidth(underline, value = 320F)
    val lineThickness = 8F
    engine.block.setHeight(underline, value = lineThickness)

    engine.block.setStrokeEnabled(block = underline, enabled = true)
    engine.block.setStrokeColor(
        block = underline,
        color = Color.fromRGBA(r = 0.05F, g = 0.25F, b = 0.95F, a = 1F),
    )
    engine.block.setStrokeWidth(block = underline, width = lineThickness)

    engine.block.appendChild(parent = page, child = underline)
    return underline
}
// highlight-android-line-annotation

// highlight-android-redaction-box
fun addRedactionBox(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    val redaction = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(redaction, shape = engine.block.createShape(ShapeType.Rect))

    engine.block.setPositionX(redaction, value = 500F)
    engine.block.setPositionY(redaction, value = 360F)
    engine.block.setWidth(redaction, value = 180F)
    engine.block.setHeight(redaction, value = 34F)

    engine.block.setFill(redaction, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = redaction,
        color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F),
    )

    engine.block.appendChild(parent = page, child = redaction)
    return redaction
}
// highlight-android-redaction-box

// highlight-android-style-appearance
fun styleRectangleAnnotationAppearance(
    engine: Engine,
    rectangle: DesignBlock,
) {
    engine.block.setOpacity(block = rectangle, value = 0.5F)

    val shape = engine.block.getShape(rectangle)
    engine.block.setFloat(shape, property = "shape/rect/cornerRadiusTL", value = 10F)
    engine.block.setFloat(shape, property = "shape/rect/cornerRadiusTR", value = 10F)
    engine.block.setFloat(shape, property = "shape/rect/cornerRadiusBL", value = 10F)
    engine.block.setFloat(shape, property = "shape/rect/cornerRadiusBR", value = 10F)

    engine.block.setStrokeEnabled(block = rectangle, enabled = true)
    engine.block.setStrokeStyle(block = rectangle, style = StrokeStyle.DASHED)
    engine.block.setStrokeWidth(block = rectangle, width = 3F)
    engine.block.setStrokeColor(
        block = rectangle,
        color = Color.fromRGBA(r = 0.9F, g = 0.35F, b = 0F, a = 1F),
    )
}
// highlight-android-style-appearance
