import ly.img.engine.Color
import ly.img.engine.CutoutType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SpotColor

fun spotColors(engine: Engine) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val primaryBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(primaryBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(primaryBlock, value = 50F)
    engine.block.setPositionY(primaryBlock, value = 50F)
    engine.block.setWidth(primaryBlock, value = 150F)
    engine.block.setHeight(primaryBlock, value = 150F)
    engine.block.appendChild(parent = page, child = primaryBlock)

    val primaryFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(primaryBlock, fill = primaryFill)

    val tintedBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(tintedBlock, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setPositionX(tintedBlock, value = 240F)
    engine.block.setPositionY(tintedBlock, value = 50F)
    engine.block.setWidth(tintedBlock, value = 150F)
    engine.block.setHeight(tintedBlock, value = 150F)
    engine.block.appendChild(parent = page, child = tintedBlock)

    val tintedFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(tintedBlock, fill = tintedFill)

    val strokeBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(strokeBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(strokeBlock, value = 430F)
    engine.block.setPositionY(strokeBlock, value = 50F)
    engine.block.setWidth(strokeBlock, value = 150F)
    engine.block.setHeight(strokeBlock, value = 150F)
    engine.block.appendChild(parent = page, child = strokeBlock)

    val strokeFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(strokeBlock, fill = strokeFill)
    engine.block.setColor(
        strokeFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 1F, g = 1F, b = 1F),
    )

    val shadowBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(shadowBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(shadowBlock, value = 50F)
    engine.block.setPositionY(shadowBlock, value = 250F)
    engine.block.setWidth(shadowBlock, value = 150F)
    engine.block.setHeight(shadowBlock, value = 150F)
    engine.block.appendChild(parent = page, child = shadowBlock)

    val shadowFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(shadowBlock, fill = shadowFill)
    engine.block.setColor(
        shadowFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.95F, g = 0.95F, b = 0.95F),
    )

    val temporaryBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(temporaryBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(temporaryBlock, value = 240F)
    engine.block.setPositionY(temporaryBlock, value = 250F)
    engine.block.setWidth(temporaryBlock, value = 150F)
    engine.block.setHeight(temporaryBlock, value = 150F)
    engine.block.appendChild(parent = page, child = temporaryBlock)

    val temporaryFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(temporaryBlock, fill = temporaryFill)

    // highlight-android-define-rgb
    engine.editor.setSpotColor(
        name = "Brand-Primary",
        Color.fromRGBA(r = 0.8F, g = 0.1F, b = 0.2F, a = 1F),
    )
    engine.editor.setSpotColor(
        name = "Brand-Accent",
        Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F),
    )
    // highlight-android-define-rgb

    // highlight-android-define-cmyk
    engine.editor.setSpotColor(
        name = "Brand-Primary",
        Color.fromCMYK(c = 0.05F, m = 0.95F, y = 0.85F, k = 0F),
    )
    engine.editor.setSpotColor(
        name = "Brand-Accent",
        Color.fromCMYK(c = 0.75F, m = 0.5F, y = 0F, k = 0F),
    )
    // highlight-android-define-cmyk

    // highlight-android-apply-spot-fill
    val brandPrimary = Color.fromSpotColor(
        name = "Brand-Primary",
        tint = 1F,
        externalReference = "BrandBook",
    )

    engine.block.setColor(
        primaryFill,
        property = "fill/color/value",
        value = brandPrimary,
    )
    // highlight-android-apply-spot-fill

    // highlight-android-tint
    val brandPrimaryHalfTint = Color.fromSpotColor(
        name = "Brand-Primary",
        tint = 0.5F,
        externalReference = "BrandBook",
    )

    engine.block.setColor(
        tintedFill,
        property = "fill/color/value",
        value = brandPrimaryHalfTint,
    )
    // highlight-android-tint

    // highlight-android-stroke-shadow
    engine.block.setStrokeEnabled(strokeBlock, enabled = true)
    engine.block.setStrokeWidth(strokeBlock, width = 8F)
    engine.block.setStrokeColor(
        strokeBlock,
        color = Color.fromSpotColor(name = "Brand-Primary"),
    )

    engine.block.setDropShadowEnabled(shadowBlock, enabled = true)
    engine.block.setDropShadowOffsetX(shadowBlock, offsetX = 10F)
    engine.block.setDropShadowOffsetY(shadowBlock, offsetY = 10F)
    engine.block.setDropShadowBlurRadiusX(shadowBlock, blurRadiusX = 15F)
    engine.block.setDropShadowBlurRadiusY(shadowBlock, blurRadiusY = 15F)
    engine.block.setDropShadowColor(
        shadowBlock,
        color = Color.fromSpotColor(name = "Brand-Accent", tint = 0.8F),
    )
    // highlight-android-stroke-shadow

    // highlight-android-query-spot
    val spotColorNames = engine.editor.findAllSpotColors()
    val brandPrimaryRgb = engine.editor.getSpotColorRGB(name = "Brand-Primary")
    val brandPrimaryCmyk = engine.editor.getSpotColorCMYK(name = "Brand-Primary")

    check(spotColorNames.containsAll(listOf("Brand-Primary", "Brand-Accent")))
    check(brandPrimaryRgb == Color.fromRGBA(r = 0.8F, g = 0.1F, b = 0.2F, a = 1F))
    check(brandPrimaryCmyk == Color.fromCMYK(c = 0.05F, m = 0.95F, y = 0.85F, k = 0F))
    // highlight-android-query-spot

    // highlight-android-read-color
    val retrievedColor = engine.block.getColor(
        primaryFill,
        property = "fill/color/value",
    )

    val retrievedSpotColor = when (retrievedColor) {
        is SpotColor -> retrievedColor
        else -> error("Expected a spot color, got $retrievedColor")
    }

    check(retrievedSpotColor.name == "Brand-Primary")
    check(retrievedSpotColor.tint == 1F)
    // highlight-android-read-color

    // highlight-android-update-spot
    engine.editor.setSpotColor(
        name = "Brand-Accent",
        Color.fromRGBA(r = 0.3F, g = 0.5F, b = 0.9F, a = 1F),
    )
    // highlight-android-update-spot

    // highlight-android-remove-spot
    engine.editor.setSpotColor(
        name = "Temporary-Color",
        Color.fromRGBA(r = 0.5F, g = 0.8F, b = 0.3F, a = 1F),
    )
    engine.block.setColor(
        temporaryFill,
        property = "fill/color/value",
        value = Color.fromSpotColor(name = "Temporary-Color"),
    )

    engine.editor.removeSpotColor(name = "Temporary-Color")
    check("Temporary-Color" !in engine.editor.findAllSpotColors())
    // highlight-android-remove-spot

    // highlight-android-cutout
    engine.editor.setSpotColor(
        name = "DieLine",
        Color.fromRGBA(r = 1F, g = 0F, b = 1F, a = 1F),
    )
    engine.editor.setSpotColor(
        name = "DieLine",
        Color.fromCMYK(c = 0F, m = 1F, y = 0F, k = 0F),
    )

    engine.block.setSpotColorForCutoutType(type = CutoutType.SOLID, name = "DieLine")
    val cutoutSpotColor = engine.block.getSpotColorForCutoutType(type = CutoutType.SOLID)
    check(cutoutSpotColor == "DieLine")
    // highlight-android-cutout
}
