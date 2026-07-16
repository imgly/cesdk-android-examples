import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.ShapeType

suspend fun createShapes(engine: Engine): DesignBlock {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 300F)
    engine.block.setHeight(page, value = 200F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-check-shape-support
    val testGraphic = engine.block.create(DesignBlockType.Graphic)
    val graphicSupportsShapes = engine.block.supportsShape(testGraphic)

    val testText = engine.block.create(DesignBlockType.Text)
    val textSupportsShapes = engine.block.supportsShape(testText)
    check(graphicSupportsShapes)
    check(!textSupportsShapes)
    engine.block.destroy(testText)
    engine.block.destroy(testGraphic)
    // highlight-android-check-shape-support

    // highlight-android-create-rectangle
    val rectGraphic = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(rectGraphic, shape = rectShape)

    val redFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(rectGraphic, fill = redFill)
    engine.block.setFillSolidColor(
        block = rectGraphic,
        color = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
    )

    engine.block.setWidth(rectGraphic, value = 64F)
    engine.block.setHeight(rectGraphic, value = 64F)
    engine.block.appendChild(parent = page, child = rectGraphic)
    engine.block.setPositionX(rectGraphic, value = 10F)
    engine.block.setPositionY(rectGraphic, value = 10F)
    // highlight-android-create-rectangle
    check(engine.block.getShape(rectGraphic) == rectShape)
    check(engine.block.getFill(rectGraphic) == redFill)

    // highlight-android-create-ellipse
    val ellipseGraphic = engine.block.create(DesignBlockType.Graphic)
    val ellipseShape = engine.block.createShape(ShapeType.Ellipse)
    engine.block.setShape(ellipseGraphic, shape = ellipseShape)

    val gradientFill = engine.block.createFill(FillType.LinearGradient)
    engine.block.setGradientColorStops(
        block = gradientFill,
        property = "fill/gradient/colors",
        colorStops = listOf(
            GradientColorStop(
                stop = 0F,
                color = Color.fromRGBA(r = 0.2F, g = 0.6F, b = 0.9F, a = 1F),
            ),
            GradientColorStop(
                stop = 1F,
                color = Color.fromRGBA(r = 0.9F, g = 0.3F, b = 0.6F, a = 1F),
            ),
        ),
    )
    engine.block.setFill(ellipseGraphic, fill = gradientFill)

    engine.block.setWidth(ellipseGraphic, value = 64F)
    engine.block.setHeight(ellipseGraphic, value = 64F)
    engine.block.appendChild(parent = page, child = ellipseGraphic)
    engine.block.setPositionX(ellipseGraphic, value = 82F)
    engine.block.setPositionY(ellipseGraphic, value = 10F)
    // highlight-android-create-ellipse
    check(engine.block.getGradientColorStops(gradientFill, property = "fill/gradient/colors").size == 2)

    // highlight-android-discover-properties
    val exampleStarShape = engine.block.createShape(ShapeType.Star)
    val starProperties = engine.block.findAllProperties(exampleStarShape)
    check("shape/star/points" in starProperties)
    engine.block.destroy(exampleStarShape)
    // highlight-android-discover-properties

    // highlight-android-create-star
    val starGraphic = engine.block.create(DesignBlockType.Graphic)
    val starShape = engine.block.createShape(ShapeType.Star)
    engine.block.setShape(starGraphic, shape = starShape)

    val yellowFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(starGraphic, fill = yellowFill)
    engine.block.setFillSolidColor(
        block = starGraphic,
        color = Color.fromRGBA(r = 1F, g = 0.8F, b = 0F, a = 1F),
    )

    engine.block.setWidth(starGraphic, value = 64F)
    engine.block.setHeight(starGraphic, value = 64F)
    engine.block.appendChild(parent = page, child = starGraphic)
    engine.block.setPositionX(starGraphic, value = 154F)
    engine.block.setPositionY(starGraphic, value = 10F)
    // highlight-android-create-star

    // highlight-android-configure-star-properties
    engine.block.setInt(starShape, property = "shape/star/points", value = 5)
    engine.block.setFloat(starShape, property = "shape/star/innerDiameter", value = 0.5F)
    // highlight-android-configure-star-properties
    check(engine.block.getInt(starShape, property = "shape/star/points") == 5)
    check(engine.block.getFloat(starShape, property = "shape/star/innerDiameter") == 0.5F)

    // highlight-android-create-polygon
    val polygonGraphic = engine.block.create(DesignBlockType.Graphic)
    val polygonShape = engine.block.createShape(ShapeType.Polygon)
    engine.block.setShape(polygonGraphic, shape = polygonShape)

    val greenFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(polygonGraphic, fill = greenFill)
    engine.block.setFillSolidColor(
        block = polygonGraphic,
        color = Color.fromRGBA(r = 0.2F, g = 0.8F, b = 0.3F, a = 1F),
    )

    engine.block.setWidth(polygonGraphic, value = 64F)
    engine.block.setHeight(polygonGraphic, value = 64F)
    engine.block.appendChild(parent = page, child = polygonGraphic)
    engine.block.setPositionX(polygonGraphic, value = 226F)
    engine.block.setPositionY(polygonGraphic, value = 10F)
    // highlight-android-create-polygon

    // highlight-android-configure-polygon-properties
    engine.block.setInt(polygonShape, property = "shape/polygon/sides", value = 8)
    // highlight-android-configure-polygon-properties
    check(engine.block.getInt(polygonShape, property = "shape/polygon/sides") == 8)

    // highlight-android-create-line
    val lineGraphic = engine.block.create(DesignBlockType.Graphic)
    val lineShape = engine.block.createShape(ShapeType.Line)
    engine.block.setShape(lineGraphic, shape = lineShape)

    engine.block.setStrokeColor(
        block = lineGraphic,
        color = Color.fromRGBA(r = 0.6F, g = 0.2F, b = 0.9F, a = 1F),
    )

    engine.block.setWidth(lineGraphic, value = 64F)
    // For line shapes, block height controls the visible line thickness.
    engine.block.setHeight(lineGraphic, value = 10F)
    engine.block.appendChild(parent = page, child = lineGraphic)
    engine.block.setPositionX(lineGraphic, value = 10F)
    engine.block.setPositionY(lineGraphic, value = 109F)
    // highlight-android-create-line

    // highlight-android-create-vector-path
    val vectorPathGraphic = engine.block.create(DesignBlockType.Graphic)
    val vectorPathShape = engine.block.createShape(ShapeType.VectorPath)
    engine.block.setShape(vectorPathGraphic, shape = vectorPathShape)

    engine.block.setString(
        block = vectorPathShape,
        property = "shape/vector_path/path",
        value = "M 0,0 L 100,50 L 0,100 Z",
    )

    val orangeFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(vectorPathGraphic, fill = orangeFill)
    engine.block.setFillSolidColor(
        block = vectorPathGraphic,
        color = Color.fromRGBA(r = 1F, g = 0.5F, b = 0F, a = 1F),
    )

    engine.block.setWidth(vectorPathGraphic, value = 64F)
    engine.block.setHeight(vectorPathGraphic, value = 64F)
    engine.block.appendChild(parent = page, child = vectorPathGraphic)
    engine.block.setPositionX(vectorPathGraphic, value = 82F)
    engine.block.setPositionY(vectorPathGraphic, value = 82F)
    // highlight-android-create-vector-path
    check(engine.block.getString(vectorPathShape, property = "shape/vector_path/path").isNotEmpty())

    // highlight-android-configure-properties
    val roundedRectGraphic = engine.block.create(DesignBlockType.Graphic)
    val roundedRectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(roundedRectGraphic, shape = roundedRectShape)

    engine.block.setFloat(roundedRectShape, property = "shape/rect/cornerRadiusTL", value = 5F)
    engine.block.setFloat(roundedRectShape, property = "shape/rect/cornerRadiusTR", value = 5F)
    engine.block.setFloat(roundedRectShape, property = "shape/rect/cornerRadiusBL", value = 5F)
    engine.block.setFloat(roundedRectShape, property = "shape/rect/cornerRadiusBR", value = 5F)

    val cyanFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(roundedRectGraphic, fill = cyanFill)
    engine.block.setFillSolidColor(
        block = roundedRectGraphic,
        color = Color.fromRGBA(r = 0F, g = 0.8F, b = 0.8F, a = 1F),
    )

    engine.block.setWidth(roundedRectGraphic, value = 64F)
    engine.block.setHeight(roundedRectGraphic, value = 64F)
    engine.block.appendChild(parent = page, child = roundedRectGraphic)
    engine.block.setPositionX(roundedRectGraphic, value = 154F)
    engine.block.setPositionY(roundedRectGraphic, value = 82F)
    // highlight-android-configure-properties
    check(engine.block.getFloat(roundedRectShape, property = "shape/rect/cornerRadiusTL") == 5F)

    // highlight-android-retrieve-shape
    val currentShape = engine.block.getShape(rectGraphic)
    val currentShapeType = ShapeType.get(engine.block.getType(currentShape))
    // highlight-android-retrieve-shape
    check(currentShapeType == ShapeType.Rect)

    // highlight-android-replace-shape
    val oldShape = engine.block.getShape(rectGraphic)
    val newShape = engine.block.createShape(ShapeType.Ellipse)

    engine.block.setShape(rectGraphic, shape = newShape)
    engine.block.destroy(oldShape)
    // highlight-android-replace-shape
    check(engine.block.getShape(rectGraphic) == newShape)

    engine.scene.zoomToBlock(
        page,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )

    return page
}
