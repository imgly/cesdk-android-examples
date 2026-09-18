import android.util.Log
import ly.img.engine.CMYKColor
import ly.img.engine.Color
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.SpotColor

private const val TAG = "FillsColor"

suspend fun fillsColor(engine: Engine) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block, value = 200F)
    engine.block.setHeight(block, value = 150F)
    engine.block.setPositionX(block, value = 50F)
    engine.block.setPositionY(block, value = 50F)
    engine.block.appendChild(parent = page, child = block)

    // highlight-android-check-fill-support
    if (!engine.block.supportsFill(block)) {
        error("Block does not support fills.")
    }
    // highlight-android-check-fill-support

    // highlight-android-create-fill
    val colorFill = engine.block.createFill(FillType.Color)
    // highlight-android-create-fill
    check(engine.block.getType(colorFill) == FillType.Color.key)
    val discardedFill = engine.block.createFill(FillType.Color)
    engine.block.destroy(discardedFill)

    // highlight-android-default-properties
    val allFillProperties = engine.block.findAllProperties(colorFill)
    Log.i(TAG, "Color fill properties: $allFillProperties")
    // highlight-android-default-properties
    check("fill/color/value" in allFillProperties)

    // highlight-android-apply-fill
    engine.block.setFill(block = block, fill = colorFill)
    // highlight-android-apply-fill

    // highlight-android-get-fill
    val currentFill = engine.block.getFill(block)
    val fillType = engine.block.getType(currentFill)
    Log.i(TAG, "Fill type: $fillType")
    // highlight-android-get-fill
    check(currentFill == colorFill)
    check(fillType == FillType.Color.key)

    // highlight-android-set-rgb
    val red = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F)
    engine.block.setColor(colorFill, property = "fill/color/value", value = red)
    // highlight-android-set-rgb
    check(engine.block.getColor(colorFill, property = "fill/color/value") == red)

    // highlight-android-get-color
    val currentColor = engine.block.getColor(colorFill, property = "fill/color/value")
    when (currentColor) {
        is RGBAColor -> Log.i(TAG, "sRGB: r=${currentColor.r}, g=${currentColor.g}, b=${currentColor.b}")
        is CMYKColor -> Log.i(TAG, "CMYK: c=${currentColor.c}, m=${currentColor.m}, y=${currentColor.y}")
        is SpotColor -> Log.i(TAG, "Spot: name=${currentColor.name}, tint=${currentColor.tint}")
    }
    // highlight-android-get-color
    check(currentColor == red)

    val cmykBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(cmykBlock, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(cmykBlock, value = 150F)
    engine.block.setHeight(cmykBlock, value = 150F)
    engine.block.setPositionX(cmykBlock, value = 300F)
    engine.block.setPositionY(cmykBlock, value = 50F)
    engine.block.appendChild(parent = page, child = cmykBlock)
    val cmykFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(cmykBlock, fill = cmykFill)

    // highlight-android-set-cmyk
    val magenta = Color.fromCMYK(c = 0F, m = 1F, y = 0F, k = 0F, tint = 1F)
    engine.block.setColor(cmykFill, property = "fill/color/value", value = magenta)
    // highlight-android-set-cmyk
    check(engine.block.getColor(cmykFill, property = "fill/color/value") == magenta)

    val spotBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(spotBlock, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(spotBlock, value = 150F)
    engine.block.setHeight(spotBlock, value = 150F)
    engine.block.setPositionX(spotBlock, value = 500F)
    engine.block.setPositionY(spotBlock, value = 50F)
    engine.block.appendChild(parent = page, child = spotBlock)
    val spotFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(spotBlock, fill = spotFill)

    // highlight-android-set-spot
    engine.editor.setSpotColor(
        name = "BrandRed",
        color = Color.fromRGBA(r = 0.9F, g = 0.1F, b = 0.1F, a = 1F),
    )
    val brandRed = Color.fromSpotColor(
        name = "BrandRed",
        tint = 1F,
        externalReference = "BrandBook",
    )
    engine.block.setColor(spotFill, property = "fill/color/value", value = brandRed)
    // highlight-android-set-spot
    val appliedSpot = engine.block.getColor(spotFill, property = "fill/color/value")
    check(appliedSpot is SpotColor)
    check(appliedSpot.name == brandRed.name)
    check(appliedSpot.tint == brandRed.tint)
    check(appliedSpot.externalReference == brandRed.externalReference)

    val toggleBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(toggleBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(toggleBlock, value = 150F)
    engine.block.setHeight(toggleBlock, value = 100F)
    engine.block.setPositionX(toggleBlock, value = 50F)
    engine.block.setPositionY(toggleBlock, value = 250F)
    engine.block.appendChild(parent = page, child = toggleBlock)
    val toggleFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(toggleBlock, fill = toggleFill)
    engine.block.setColor(
        toggleFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 1F, g = 0.5F, b = 0F, a = 1F),
    )
    engine.block.setFillEnabled(toggleBlock, enabled = true)

    // highlight-android-toggle-fill
    val isEnabled = engine.block.isFillEnabled(toggleBlock)
    Log.i(TAG, "Fill enabled: $isEnabled")

    engine.block.setFillEnabled(toggleBlock, enabled = !isEnabled)
    // highlight-android-toggle-fill
    check(isEnabled)
    engine.block.setFillEnabled(toggleBlock, enabled = true)
    check(engine.block.isFillEnabled(toggleBlock))

    val block1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block1, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block1, value = 100F)
    engine.block.setHeight(block1, value = 100F)
    engine.block.setPositionX(block1, value = 250F)
    engine.block.setPositionY(block1, value = 250F)
    engine.block.appendChild(parent = page, child = block1)

    val block2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block2, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block2, value = 100F)
    engine.block.setHeight(block2, value = 100F)
    engine.block.setPositionX(block2, value = 370F)
    engine.block.setPositionY(block2, value = 250F)
    engine.block.appendChild(parent = page, child = block2)

    // highlight-android-share-fill
    val sharedFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        sharedFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.5F, g = 0F, b = 0.5F, a = 1F),
    )

    engine.block.setFill(block1, fill = sharedFill)
    engine.block.setFill(block2, fill = sharedFill)

    engine.block.setColor(
        sharedFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0F, g = 0.5F, b = 0.5F, a = 1F),
    )
    // highlight-android-share-fill
    check(engine.block.getFill(block1) == sharedFill)
    check(engine.block.getFill(block2) == sharedFill)

    // highlight-android-convert-color
    val rgbColor = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F)
    val cmykColor = engine.editor.convertColorToColorSpace(
        color = rgbColor,
        colorSpace = ColorSpace.CMYK,
    ) as CMYKColor
    Log.i(TAG, "Converted CMYK color: $cmykColor")
    // highlight-android-convert-color

    val brandBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(brandBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(brandBlock, value = 150F)
    engine.block.setHeight(brandBlock, value = 100F)
    engine.block.setPositionX(brandBlock, value = 500F)
    engine.block.setPositionY(brandBlock, value = 250F)
    engine.block.appendChild(parent = page, child = brandBlock)

    // highlight-android-brand-colors
    engine.editor.setSpotColor(
        name = "PrimaryBrand",
        color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F),
    )

    val brandFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(brandBlock, fill = brandFill)
    engine.block.setColor(
        brandFill,
        property = "fill/color/value",
        value = Color.fromSpotColor(name = "PrimaryBrand"),
    )
    // highlight-android-brand-colors
    val brandColor = engine.block.getColor(brandFill, property = "fill/color/value")
    check(brandColor is SpotColor)
    check(brandColor.name == "PrimaryBrand")

    val transparentBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(transparentBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(transparentBlock, value = 150F)
    engine.block.setHeight(transparentBlock, value = 100F)
    engine.block.setPositionX(transparentBlock, value = 50F)
    engine.block.setPositionY(transparentBlock, value = 400F)
    engine.block.appendChild(parent = page, child = transparentBlock)
    val transparentFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(transparentBlock, fill = transparentFill)

    // highlight-android-transparency
    val transparentGreen = Color.fromRGBA(r = 0F, g = 0.8F, b = 0.2F, a = 0.5F)
    engine.block.setColor(transparentFill, property = "fill/color/value", value = transparentGreen)
    // highlight-android-transparency
    val transparencyColor = engine.block.getColor(transparentFill, property = "fill/color/value")
    check(transparencyColor is RGBAColor)
    check(transparencyColor.a == 0.5F)

    val printBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(printBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(printBlock, value = 150F)
    engine.block.setHeight(printBlock, value = 100F)
    engine.block.setPositionX(printBlock, value = 250F)
    engine.block.setPositionY(printBlock, value = 400F)
    engine.block.appendChild(parent = page, child = printBlock)
    val printFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(printBlock, fill = printFill)

    // highlight-android-print-colors
    val printColor = Color.fromCMYK(c = 0F, m = 0.85F, y = 1F, k = 0F, tint = 1F)
    engine.block.setColor(printFill, property = "fill/color/value", value = printColor)
    // highlight-android-print-colors
    check(engine.block.getColor(printFill, property = "fill/color/value") == printColor)
}
