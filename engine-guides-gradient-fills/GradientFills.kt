import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.ShapeType

suspend fun gradientFills(engine: Engine): GradientFillsResult {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val linearBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(linearBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(linearBlock, value = 60F)
    engine.block.setPositionY(linearBlock, value = 80F)
    engine.block.setWidth(linearBlock, value = 200F)
    engine.block.setHeight(linearBlock, value = 160F)
    engine.block.appendChild(parent = page, child = linearBlock)

    val radialBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(radialBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(radialBlock, value = 300F)
    engine.block.setPositionY(radialBlock, value = 80F)
    engine.block.setWidth(radialBlock, value = 200F)
    engine.block.setHeight(radialBlock, value = 160F)
    engine.block.appendChild(parent = page, child = radialBlock)

    val conicalBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(conicalBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(conicalBlock, value = 540F)
    engine.block.setPositionY(conicalBlock, value = 80F)
    engine.block.setWidth(conicalBlock, value = 160F)
    engine.block.setHeight(conicalBlock, value = 160F)
    engine.block.appendChild(parent = page, child = conicalBlock)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(textBlock, text = "Solid color text")
    engine.block.setPositionX(textBlock, value = 60F)
    engine.block.setPositionY(textBlock, value = 460F)
    engine.block.setWidth(textBlock, value = 280F)
    engine.block.appendChild(parent = page, child = textBlock)

    // highlight-android-check-fill-support
    val sceneSupportsFill = engine.block.supportsFill(scene)
    val pageSupportsFill = engine.block.supportsFill(page)
    val graphicSupportsFill = engine.block.supportsFill(linearBlock)
    val textSupportsFill = engine.block.supportsFill(textBlock)
    // highlight-android-check-fill-support
    check(!sceneSupportsFill)
    check(pageSupportsFill)
    check(graphicSupportsFill)
    check(textSupportsFill)

    // highlight-android-create-gradients
    val pageGradient = engine.block.createFill(FillType.LinearGradient)
    val linearGradient = engine.block.createFill(FillType.LinearGradient)
    val radialGradient = engine.block.createFill(FillType.RadialGradient)
    val conicalGradient = engine.block.createFill(FillType.ConicalGradient)
    // highlight-android-create-gradients

    // highlight-android-apply-gradient
    val previousPageFill = engine.block.getFill(page)
    val previousLinearFill = engine.block.getFill(linearBlock)
    engine.block.setFill(page, fill = pageGradient)
    engine.block.setFill(linearBlock, fill = linearGradient)
    // Only destroy previous fills when you know they are not shared by other blocks.
    if (engine.block.isValid(previousPageFill)) {
        engine.block.destroy(previousPageFill)
    }
    if (engine.block.isValid(previousLinearFill)) {
        engine.block.destroy(previousLinearFill)
    }

    val pageFillType = engine.block.getType(engine.block.getFill(page))
    val currentFill = engine.block.getFill(linearBlock)
    val currentFillType = engine.block.getType(currentFill)
    // highlight-android-apply-gradient
    check(pageFillType == FillType.LinearGradient.key)
    check(currentFill == linearGradient)
    check(currentFillType == FillType.LinearGradient.key)

    // highlight-android-text-fill-compatibility
    val textSolidColor = Color.fromRGBA(r = 0.1F, g = 0.1F, b = 0.1F, a = 1F)
    engine.block.setFillSolidColor(textBlock, color = textSolidColor)
    val currentTextColor = engine.block.getFillSolidColor(textBlock)

    val rejectedTextGradient = engine.block.createFill(FillType.LinearGradient)
    val textRejectsGradientFill = runCatching {
        engine.block.setFill(textBlock, fill = rejectedTextGradient)
    }.isFailure
    if (textRejectsGradientFill) {
        engine.block.destroy(rejectedTextGradient)
    }
    // highlight-android-text-fill-compatibility
    check(currentTextColor == textSolidColor)
    check(engine.block.getType(engine.block.getFill(textBlock)) == FillType.Color.key)
    check(textRejectsGradientFill)

    // highlight-android-color-stops
    val linearColorStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 1F, g = 0.8F, b = 0.2F, a = 1F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.3F, g = 0.4F, b = 0.7F, a = 1F)),
    )
    engine.block.setGradientColorStops(
        block = linearGradient,
        property = "fill/gradient/colors",
        colorStops = linearColorStops,
    )
    val currentColorStops = engine.block.getGradientColorStops(
        block = linearGradient,
        property = "fill/gradient/colors",
    )
    // highlight-android-color-stops
    check(currentColorStops == linearColorStops)

    // highlight-android-color-spaces
    engine.editor.setSpotColor(
        name = "BrandPrimary",
        color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F),
    )
    val colorSpaceStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromHex("#3366CC")),
        GradientColorStop(stop = 0.25F, color = Color.fromRGBA(r = 255, g = 204, b = 0, a = 255)),
        GradientColorStop(
            stop = 0.75F,
            color = Color.fromCMYK(c = 0F, m = 1F, y = 1F, k = 0F, tint = 1F),
        ),
        GradientColorStop(
            stop = 1F,
            color = Color.fromSpotColor(name = "BrandPrimary", tint = 1F),
        ),
    )
    engine.block.setGradientColorStops(
        block = linearGradient,
        property = "fill/gradient/colors",
        colorStops = colorSpaceStops,
    )
    // highlight-android-color-spaces
    val currentColorSpaceStops = engine.block.getGradientColorStops(
        block = linearGradient,
        property = "fill/gradient/colors",
    )
    check(currentColorSpaceStops == colorSpaceStops)
    engine.block.setGradientColorStops(linearGradient, property = "fill/gradient/colors", colorStops = linearColorStops)

    // highlight-android-linear-position
    engine.block.setFloat(linearGradient, property = "fill/gradient/linear/startPointX", value = 0F)
    engine.block.setFloat(linearGradient, property = "fill/gradient/linear/startPointY", value = 0F)
    engine.block.setFloat(linearGradient, property = "fill/gradient/linear/endPointX", value = 1F)
    engine.block.setFloat(linearGradient, property = "fill/gradient/linear/endPointY", value = 1F)

    val linearStartX = engine.block.getFloat(linearGradient, property = "fill/gradient/linear/startPointX")
    val linearStartY = engine.block.getFloat(linearGradient, property = "fill/gradient/linear/startPointY")
    val linearEndX = engine.block.getFloat(linearGradient, property = "fill/gradient/linear/endPointX")
    val linearEndY = engine.block.getFloat(linearGradient, property = "fill/gradient/linear/endPointY")
    // highlight-android-linear-position
    check(linearStartX == 0F)
    check(linearStartY == 0F)
    check(linearEndX == 1F)
    check(linearEndY == 1F)

    val previousRadialFill = engine.block.getFill(radialBlock)
    engine.block.setFill(radialBlock, fill = radialGradient)
    if (engine.block.isValid(previousRadialFill)) {
        engine.block.destroy(previousRadialFill)
    }
    val radialColorStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 0.3F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F)),
    )
    engine.block.setGradientColorStops(
        block = radialGradient,
        property = "fill/gradient/colors",
        colorStops = radialColorStops,
    )

    // highlight-android-radial-gradient
    engine.block.setFloat(radialGradient, property = "fill/gradient/radial/centerPointX", value = 0.5F)
    engine.block.setFloat(radialGradient, property = "fill/gradient/radial/centerPointY", value = 0.5F)
    engine.block.setFloat(radialGradient, property = "fill/gradient/radial/radius", value = 0.8F)

    val radialCenterX = engine.block.getFloat(radialGradient, property = "fill/gradient/radial/centerPointX")
    val radialCenterY = engine.block.getFloat(radialGradient, property = "fill/gradient/radial/centerPointY")
    val radialRadius = engine.block.getFloat(radialGradient, property = "fill/gradient/radial/radius")
    // highlight-android-radial-gradient
    check(radialCenterX == 0.5F)
    check(radialCenterY == 0.5F)
    check(radialRadius == 0.8F)

    val previousConicalFill = engine.block.getFill(conicalBlock)
    engine.block.setFill(conicalBlock, fill = conicalGradient)
    if (engine.block.isValid(previousConicalFill)) {
        engine.block.destroy(previousConicalFill)
    }
    val conicalColorStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F)),
        GradientColorStop(stop = 0.75F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 0F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F)),
    )
    engine.block.setGradientColorStops(
        block = conicalGradient,
        property = "fill/gradient/colors",
        colorStops = conicalColorStops,
    )

    // highlight-android-conical-gradient
    engine.block.setFloat(conicalGradient, property = "fill/gradient/conical/centerPointX", value = 0.5F)
    engine.block.setFloat(conicalGradient, property = "fill/gradient/conical/centerPointY", value = 0.5F)

    val conicalCenterX = engine.block.getFloat(conicalGradient, property = "fill/gradient/conical/centerPointX")
    val conicalCenterY = engine.block.getFloat(conicalGradient, property = "fill/gradient/conical/centerPointY")
    // highlight-android-conical-gradient
    check(conicalCenterX == 0.5F)
    check(conicalCenterY == 0.5F)

    // highlight-android-use-case-aurora
    val auroraStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0.4F, g = 0.1F, b = 0.8F, a = 1F)),
        GradientColorStop(stop = 0.3F, color = Color.fromRGBA(r = 0.8F, g = 0.2F, b = 0.6F, a = 1F)),
        GradientColorStop(stop = 0.6F, color = Color.fromRGBA(r = 1F, g = 0.5F, b = 0.3F, a = 1F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 1F, g = 0.8F, b = 0.2F, a = 1F)),
    )
    engine.block.setGradientColorStops(
        block = pageGradient,
        property = "fill/gradient/colors",
        colorStops = auroraStops,
    )
    // highlight-android-use-case-aurora
    check(engine.block.getGradientColorStops(pageGradient, property = "fill/gradient/colors") == auroraStops)

    // highlight-android-use-case-button-highlight
    val buttonHighlightStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 0.3F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F)),
    )
    engine.block.setFloat(radialGradient, property = "fill/gradient/radial/centerPointX", value = 0.5F)
    engine.block.setFloat(radialGradient, property = "fill/gradient/radial/centerPointY", value = 0.35F)
    engine.block.setFloat(radialGradient, property = "fill/gradient/radial/radius", value = 0.9F)
    engine.block.setGradientColorStops(
        block = radialGradient,
        property = "fill/gradient/colors",
        colorStops = buttonHighlightStops,
    )
    // highlight-android-use-case-button-highlight
    check(engine.block.getGradientColorStops(radialGradient, property = "fill/gradient/colors") == buttonHighlightStops)

    // highlight-android-use-case-spinner
    val spinnerStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F)),
        GradientColorStop(stop = 0.75F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 0F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F)),
    )
    engine.block.setGradientColorStops(
        block = conicalGradient,
        property = "fill/gradient/colors",
        colorStops = spinnerStops,
    )
    // highlight-android-use-case-spinner
    check(engine.block.getGradientColorStops(conicalGradient, property = "fill/gradient/colors") == spinnerStops)

    // highlight-android-use-case-transparency
    val transparencyStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0.7F)),
    )
    engine.block.setGradientColorStops(
        block = pageGradient,
        property = "fill/gradient/colors",
        colorStops = transparencyStops,
    )
    // highlight-android-use-case-transparency
    check(engine.block.getGradientColorStops(pageGradient, property = "fill/gradient/colors") == transparencyStops)

    // highlight-android-use-case-duotone
    val duotoneStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0.8F, g = 0.2F, b = 0.9F, a = 1F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.2F, g = 0.9F, b = 0.8F, a = 1F)),
    )
    engine.block.setGradientColorStops(
        block = linearGradient,
        property = "fill/gradient/colors",
        colorStops = duotoneStops,
    )
    // highlight-android-use-case-duotone
    check(engine.block.getGradientColorStops(linearGradient, property = "fill/gradient/colors") == duotoneStops)
    engine.block.setGradientColorStops(linearGradient, property = "fill/gradient/colors", colorStops = linearColorStops)

    val sharedBlockA = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(sharedBlockA, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(sharedBlockA, value = 120F)
    engine.block.setPositionY(sharedBlockA, value = 300F)
    engine.block.setWidth(sharedBlockA, value = 160F)
    engine.block.setHeight(sharedBlockA, value = 120F)
    engine.block.appendChild(parent = page, child = sharedBlockA)

    val sharedBlockB = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(sharedBlockB, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(sharedBlockB, value = 340F)
    engine.block.setPositionY(sharedBlockB, value = 300F)
    engine.block.setWidth(sharedBlockB, value = 160F)
    engine.block.setHeight(sharedBlockB, value = 120F)
    engine.block.appendChild(parent = page, child = sharedBlockB)

    // highlight-android-share-gradient
    val sharedGradient = engine.block.createFill(FillType.LinearGradient)
    val previousSharedFillA = engine.block.getFill(sharedBlockA)
    val previousSharedFillB = engine.block.getFill(sharedBlockB)
    engine.block.setFill(sharedBlockA, fill = sharedGradient)
    engine.block.setFill(sharedBlockB, fill = sharedGradient)
    // Only destroy previous fills when you know they are not shared by other blocks.
    if (engine.block.isValid(previousSharedFillA)) {
        engine.block.destroy(previousSharedFillA)
    }
    if (engine.block.isValid(previousSharedFillB)) {
        engine.block.destroy(previousSharedFillB)
    }

    val sharedGradientStops = listOf(
        GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0F, g = 1F, b = 0F, a = 1F)),
        GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 1F, g = 1F, b = 0F, a = 1F)),
    )
    engine.block.setGradientColorStops(sharedGradient, property = "fill/gradient/colors", colorStops = sharedGradientStops)
    // highlight-android-share-gradient
    check(engine.block.getFill(sharedBlockA) == sharedGradient)
    check(engine.block.getFill(sharedBlockB) == sharedGradient)

    // highlight-android-duplicate-gradient
    val duplicateBlock = engine.block.duplicate(linearBlock)
    val duplicateGradient = engine.block.getFill(duplicateBlock)
    engine.block.setGradientColorStops(
        block = duplicateGradient,
        property = "fill/gradient/colors",
        colorStops = listOf(
            GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 0.8F, g = 0.2F, b = 0.9F, a = 1F)),
            GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0.2F, g = 0.9F, b = 0.8F, a = 1F)),
        ),
    )
    // highlight-android-duplicate-gradient
    val duplicateFillIsIndependent = duplicateGradient != linearGradient &&
        engine.block.getGradientColorStops(linearGradient, property = "fill/gradient/colors") == linearColorStops
    check(duplicateFillIsIndependent)

    return GradientFillsResult(
        sceneSupportsFill = sceneSupportsFill,
        pageSupportsFill = pageSupportsFill,
        graphicSupportsFill = graphicSupportsFill,
        textSupportsFill = textSupportsFill,
        textRejectsGradientFill = textRejectsGradientFill,
        pageFillType = pageFillType,
        linearFillType = currentFillType,
        radialFillType = engine.block.getType(radialGradient),
        conicalFillType = engine.block.getType(conicalGradient),
        linearStops = currentColorStops,
        colorSpaceStops = currentColorSpaceStops,
        linearStartX = linearStartX,
        linearStartY = linearStartY,
        linearEndX = linearEndX,
        linearEndY = linearEndY,
        radialRadius = radialRadius,
        conicalCenterX = conicalCenterX,
        sharedStops = sharedGradientStops,
        duplicateFillIsIndependent = duplicateFillIsIndependent,
    )
}
