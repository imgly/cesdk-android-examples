import ly.img.engine.CMYKColor
import ly.img.engine.Color
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType

data class CMYKColors(
    val fillColor: CMYKColor,
    val tintedColor: CMYKColor,
    val strokeColor: CMYKColor,
    val shadowColor: CMYKColor,
    val readColor: CMYKColor,
    val convertedCmyk: CMYKColor,
    val convertedSrgb: RGBAColor,
    val gradientStops: List<GradientColorStop>,
)

fun cmykColors(engine: Engine): CMYKColors {
    // highlight-android-create-cmyk
    // CMYK components (c, m, y, k) and tint all range from 0F to 1F.
    val cmykCyan = Color.fromCMYK(c = 1F, m = 0F, y = 0F, k = 0F, tint = 1F)
    val cmykMagenta = Color.fromCMYK(c = 0F, m = 1F, y = 0F, k = 0F, tint = 1F)
    val cmykYellow = Color.fromCMYK(c = 0F, m = 0F, y = 1F, k = 0F, tint = 1F)
    val cmykBlack = Color.fromCMYK(c = 0F, m = 0F, y = 0F, k = 1F, tint = 1F)
    // highlight-android-create-cmyk

    // highlight-android-apply-fill
    val fillBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(fillBlock, shape = engine.block.createShape(ShapeType.Rect))

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(fillBlock, fill = fill)

    // Color fill values currently use the generic color property key.
    engine.block.setColor(fill, property = "fill/color/value", value = cmykCyan)
    // highlight-android-apply-fill

    // highlight-android-tint
    // Tint scales the color intensity without changing the CMYK components.
    val cmykHalfMagenta = Color.fromCMYK(c = 0F, m = 1F, y = 0F, k = 0F, tint = 0.5F)
    val tintedBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(tintedBlock, shape = engine.block.createShape(ShapeType.Rect))
    val tintedFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(tintedBlock, fill = tintedFill)
    engine.block.setColor(tintedFill, property = "fill/color/value", value = cmykHalfMagenta)
    // highlight-android-tint

    // highlight-android-stroke
    val strokeBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(strokeBlock, shape = engine.block.createShape(ShapeType.Rect))

    engine.block.setStrokeEnabled(strokeBlock, enabled = true)
    engine.block.setStrokeWidth(strokeBlock, width = 8F)

    val cmykStrokeColor = Color.fromCMYK(c = 0.8F, m = 0.2F, y = 0F, k = 0.1F, tint = 1F)
    engine.block.setStrokeColor(strokeBlock, color = cmykStrokeColor)
    // highlight-android-stroke

    // highlight-android-shadow
    val shadowBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(shadowBlock, shape = engine.block.createShape(ShapeType.Rect))

    engine.block.setDropShadowEnabled(shadowBlock, enabled = true)
    engine.block.setDropShadowOffsetX(shadowBlock, offsetX = 8F)
    engine.block.setDropShadowOffsetY(shadowBlock, offsetY = 8F)
    engine.block.setDropShadowBlurRadiusX(shadowBlock, blurRadiusX = 12F)
    engine.block.setDropShadowBlurRadiusY(shadowBlock, blurRadiusY = 12F)

    val cmykShadowColor = cmykBlack
    engine.block.setDropShadowColor(shadowBlock, color = cmykShadowColor)
    // highlight-android-shadow

    // highlight-android-read
    val readBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(readBlock, shape = engine.block.createShape(ShapeType.Rect))
    val readFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(readBlock, fill = readFill)
    val cmykOrange = Color.fromCMYK(c = 0F, m = 0.5F, y = 1F, k = 0F, tint = 1F)
    engine.block.setColor(readFill, property = "fill/color/value", value = cmykOrange)

    val retrievedColor = engine.block.getColor(readFill, property = "fill/color/value")
    val retrievedCmyk = when (retrievedColor) {
        is CMYKColor -> retrievedColor
        else -> error("Expected a CMYK color, got $retrievedColor")
    }
    println(
        "CMYK Color - C: ${retrievedCmyk.c}, M: ${retrievedCmyk.m}, " +
            "Y: ${retrievedCmyk.y}, K: ${retrievedCmyk.k}, Tint: ${retrievedCmyk.tint}",
    )
    // highlight-android-read

    // highlight-android-convert
    val rgbBlue = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F)
    val convertedCmyk = engine.editor.convertColorToColorSpace(
        color = rgbBlue,
        colorSpace = ColorSpace.CMYK,
    )

    val cmykGreen = Color.fromCMYK(c = 0.7F, m = 0F, y = 1F, k = 0.2F, tint = 1F)
    val convertedSrgb = engine.editor.convertColorToColorSpace(
        color = cmykGreen,
        colorSpace = ColorSpace.SRGB,
    )
    // highlight-android-convert

    // highlight-android-gradient
    val gradientBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(gradientBlock, shape = engine.block.createShape(ShapeType.Rect))

    val gradientFill = engine.block.createFill(FillType.LinearGradient)
    engine.block.setFill(gradientBlock, fill = gradientFill)

    val gradientStops = listOf(
        GradientColorStop(stop = 0F, color = cmykCyan),
        GradientColorStop(stop = 0.5F, color = cmykMagenta),
        GradientColorStop(stop = 1F, color = cmykYellow),
    )
    // Gradient fills currently expose their stops through the generic property key.
    engine.block.setGradientColorStops(
        gradientFill,
        property = "fill/gradient/colors",
        colorStops = gradientStops,
    )
    // highlight-android-gradient

    return CMYKColors(
        fillColor = engine.block.getColor(fill, property = "fill/color/value") as CMYKColor,
        tintedColor = engine.block.getColor(tintedFill, property = "fill/color/value") as CMYKColor,
        strokeColor = engine.block.getStrokeColor(strokeBlock) as CMYKColor,
        shadowColor = engine.block.getDropShadowColor(shadowBlock) as CMYKColor,
        readColor = retrievedCmyk,
        convertedCmyk = convertedCmyk as CMYKColor,
        convertedSrgb = convertedSrgb as RGBAColor,
        gradientStops = engine.block.getGradientColorStops(
            gradientFill,
            property = "fill/gradient/colors",
        ),
    )
}
