import ly.img.engine.BooleanOperation
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

suspend fun combineShapes(engine: Engine) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val circle1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(circle1, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(circle1, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = circle1,
        color = Color.fromRGBA(r = 1F, g = 0.4F, b = 0.4F, a = 1F),
    )
    engine.block.setPositionX(circle1, value = 90F)
    engine.block.setPositionY(circle1, value = 70F)
    engine.block.setWidth(circle1, value = 96F)
    engine.block.setHeight(circle1, value = 96F)
    engine.block.appendChild(parent = page, child = circle1)

    val circle2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(circle2, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(circle2, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = circle2,
        color = Color.fromRGBA(r = 0.4F, g = 1F, b = 0.4F, a = 1F),
    )
    engine.block.setPositionX(circle2, value = 162F)
    engine.block.setPositionY(circle2, value = 70F)
    engine.block.setWidth(circle2, value = 96F)
    engine.block.setHeight(circle2, value = 96F)
    engine.block.appendChild(parent = page, child = circle2)

    val circle3 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(circle3, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(circle3, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = circle3,
        color = Color.fromRGBA(r = 0.4F, g = 0.4F, b = 1F, a = 1F),
    )
    engine.block.setPositionX(circle3, value = 126F)
    engine.block.setPositionY(circle3, value = 130F)
    engine.block.setWidth(circle3, value = 120F)
    engine.block.setHeight(circle3, value = 120F)
    engine.block.appendChild(parent = page, child = circle3)

    // highlight-android-check-combinability
    val unionBlocks = listOf(circle1, circle2, circle3)
    val canCombineUnion = engine.block.isCombinable(unionBlocks)
    check(canCombineUnion) { "Choose graphic or text blocks that can be combined." }
    // highlight-android-check-combinability

    // highlight-android-combine-union
    val unionResult = engine.block.combine(
        blocks = unionBlocks,
        op = BooleanOperation.UNION,
    )
    // highlight-android-combine-union
    check(engine.block.isValid(unionResult))

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.setPositionX(imageBlock, value = 450F)
    engine.block.setPositionY(imageBlock, value = 70F)
    engine.block.setWidth(imageBlock, value = 260F)
    engine.block.setHeight(imageBlock, value = 170F)
    engine.block.appendChild(parent = page, child = imageBlock)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = textBlock, text = "CUTOUT")
    engine.block.setTextFontSize(block = textBlock, fontSize = 88F)
    engine.block.setPositionX(textBlock, value = 468F)
    engine.block.setPositionY(textBlock, value = 115F)
    engine.block.setWidth(textBlock, value = 224F)
    engine.block.setHeight(textBlock, value = 80F)
    engine.block.appendChild(parent = page, child = textBlock)

    // highlight-android-combine-difference
    engine.block.sendToBack(imageBlock)
    // Load the image fill and text font resources before combining.
    engine.block.forceLoadResources(listOf(imageBlock, textBlock))
    val differenceBlocks = listOf(imageBlock, textBlock)
    check(engine.block.isCombinable(differenceBlocks))
    val differenceResult = engine.block.combine(
        blocks = differenceBlocks,
        op = BooleanOperation.DIFFERENCE,
    )
    // highlight-android-combine-difference
    check(engine.block.isValid(differenceResult))

    val intersectionCircle1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(
        intersectionCircle1,
        shape = engine.block.createShape(ShapeType.Ellipse),
    )
    engine.block.setFill(intersectionCircle1, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = intersectionCircle1,
        color = Color.fromRGBA(r = 1F, g = 0.55F, b = 0.12F, a = 1F),
    )
    engine.block.setPositionX(intersectionCircle1, value = 130F)
    engine.block.setPositionY(intersectionCircle1, value = 385F)
    engine.block.setWidth(intersectionCircle1, value = 150F)
    engine.block.setHeight(intersectionCircle1, value = 150F)
    engine.block.appendChild(parent = page, child = intersectionCircle1)

    val intersectionCircle2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(
        intersectionCircle2,
        shape = engine.block.createShape(ShapeType.Ellipse),
    )
    engine.block.setFill(intersectionCircle2, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = intersectionCircle2,
        color = Color.fromRGBA(r = 0.12F, g = 0.74F, b = 0.92F, a = 1F),
    )
    engine.block.setPositionX(intersectionCircle2, value = 220F)
    engine.block.setPositionY(intersectionCircle2, value = 385F)
    engine.block.setWidth(intersectionCircle2, value = 150F)
    engine.block.setHeight(intersectionCircle2, value = 150F)
    engine.block.appendChild(parent = page, child = intersectionCircle2)

    // highlight-android-combine-intersection
    val intersectionBlocks = listOf(intersectionCircle1, intersectionCircle2)
    check(engine.block.isCombinable(intersectionBlocks))
    val intersectionResult = engine.block.combine(
        blocks = intersectionBlocks,
        op = BooleanOperation.INTERSECTION,
    )
    // highlight-android-combine-intersection
    check(engine.block.isValid(intersectionResult))

    val xorCircle1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(xorCircle1, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(xorCircle1, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = xorCircle1,
        color = Color.fromRGBA(r = 0.12F, g = 0.74F, b = 0.92F, a = 1F),
    )
    engine.block.setPositionX(xorCircle1, value = 470F)
    engine.block.setPositionY(xorCircle1, value = 380F)
    engine.block.setWidth(xorCircle1, value = 150F)
    engine.block.setHeight(xorCircle1, value = 150F)
    engine.block.appendChild(parent = page, child = xorCircle1)

    val xorCircle2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(xorCircle2, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFill(xorCircle2, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = xorCircle2,
        color = Color.fromRGBA(r = 0.12F, g = 0.74F, b = 0.92F, a = 1F),
    )
    engine.block.setPositionX(xorCircle2, value = 560F)
    engine.block.setPositionY(xorCircle2, value = 380F)
    engine.block.setWidth(xorCircle2, value = 150F)
    engine.block.setHeight(xorCircle2, value = 150F)
    engine.block.appendChild(parent = page, child = xorCircle2)

    // highlight-android-combine-xor
    val xorBlocks = listOf(xorCircle1, xorCircle2)
    check(engine.block.isCombinable(xorBlocks))
    val xorResult = engine.block.combine(
        blocks = xorBlocks,
        op = BooleanOperation.XOR,
    )
    // highlight-android-combine-xor
    check(engine.block.isValid(xorResult))
}
