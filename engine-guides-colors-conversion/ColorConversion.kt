import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.CMYKColor
import ly.img.engine.Color
import ly.img.engine.ColorSpace
import ly.img.engine.Engine
import ly.img.engine.RGBAColor
import ly.img.engine.SpotColor

suspend fun colorConversion(engine: Engine): ColorConversionResult = withContext(Dispatchers.Main) {
    // highlight-android-define-spot-color
    // Define a spot color with an RGB approximation for screen preview.
    engine.editor.setSpotColor(
        name = "Brand Red",
        color = Color.fromRGBA(r = 0.95F, g = 0.25F, b = 0.21F, a = 1F),
    )
    // highlight-android-define-spot-color

    // highlight-android-create-colors
    val srgbColor = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F)
    val cmykColor = Color.fromCMYK(c = 0F, m = 0.8F, y = 0.95F, k = 0F, tint = 1F)
    val spotColor = Color.fromSpotColor(
        name = "Brand Red",
        tint = 1F,
        externalReference = "",
    )
    // highlight-android-create-colors

    // highlight-android-convert-to-srgb
    val cmykToSrgb = engine.editor.convertColorToColorSpace(
        color = cmykColor,
        colorSpace = ColorSpace.SRGB,
    ) as RGBAColor

    val spotToSrgb = engine.editor.convertColorToColorSpace(
        color = spotColor,
        colorSpace = ColorSpace.SRGB,
    ) as RGBAColor

    println("CMYK converted to sRGB: $cmykToSrgb")
    println("Spot color converted to sRGB: $spotToSrgb")
    // highlight-android-convert-to-srgb

    // highlight-android-convert-to-cmyk
    val srgbToCmyk = engine.editor.convertColorToColorSpace(
        color = srgbColor,
        colorSpace = ColorSpace.CMYK,
    ) as CMYKColor

    // Add a CMYK approximation before converting the spot color for print output.
    engine.editor.setSpotColor(
        name = "Brand Red",
        color = Color.fromCMYK(c = 0F, m = 0.85F, y = 0.9F, k = 0.05F, tint = 1F),
    )
    val spotToCmyk = engine.editor.convertColorToColorSpace(
        color = spotColor,
        colorSpace = ColorSpace.CMYK,
    ) as CMYKColor

    println("sRGB converted to CMYK: $srgbToCmyk")
    println("Spot color converted to CMYK: $spotToCmyk")
    // highlight-android-convert-to-cmyk

    // highlight-android-identify-types
    val detectedTypes = listOf(srgbColor, cmykColor, spotColor).map { color ->
        when (color) {
            is RGBAColor -> "sRGB"
            is CMYKColor -> "CMYK"
            is SpotColor -> "SpotColor"
        }
    }
    // highlight-android-identify-types

    // highlight-android-handle-tint-alpha
    val transparentSrgb = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 0.5F)
    val transparentSrgbToCmyk = engine.editor.convertColorToColorSpace(
        color = transparentSrgb,
        colorSpace = ColorSpace.CMYK,
    ) as CMYKColor

    val tintedCmyk = Color.fromCMYK(c = 0F, m = 0.8F, y = 0.95F, k = 0F, tint = 0.5F)
    val tintedCmykPreview = engine.editor.convertColorToColorSpace(
        color = tintedCmyk,
        colorSpace = ColorSpace.SRGB,
    ) as RGBAColor

    val tintedSpotColor = Color.fromSpotColor(
        name = "Brand Red",
        tint = 0.4F,
        externalReference = "",
    )
    val tintedSpotPreview = engine.editor.convertColorToColorSpace(
        color = tintedSpotColor,
        colorSpace = ColorSpace.SRGB,
    ) as RGBAColor
    val tintedSpotToCmyk = engine.editor.convertColorToColorSpace(
        color = tintedSpotColor,
        colorSpace = ColorSpace.CMYK,
    ) as CMYKColor
    // highlight-android-handle-tint-alpha

    // highlight-android-color-picker
    val colorFromDesign: Color = spotColor
    val pickerPreviewColor = engine.editor.convertColorToColorSpace(
        color = colorFromDesign,
        colorSpace = ColorSpace.SRGB,
    ) as RGBAColor
    // Display pickerPreviewColor.r, pickerPreviewColor.g, pickerPreviewColor.b, and pickerPreviewColor.a.
    // highlight-android-color-picker

    // highlight-android-print-preparation
    val colorForExport: Color = srgbColor
    val printColor = if (colorForExport is CMYKColor) {
        colorForExport
    } else {
        engine.editor.convertColorToColorSpace(
            color = colorForExport,
            colorSpace = ColorSpace.CMYK,
        ) as CMYKColor
    }
    // highlight-android-print-preparation

    ColorConversionResult(
        cmykToSrgb = cmykToSrgb,
        spotToSrgb = spotToSrgb,
        srgbToCmyk = srgbToCmyk,
        spotToCmyk = spotToCmyk,
        transparentSrgbToCmyk = transparentSrgbToCmyk,
        tintedCmykPreview = tintedCmykPreview,
        tintedSpotPreview = tintedSpotPreview,
        tintedSpotToCmyk = tintedSpotToCmyk,
        detectedTypes = detectedTypes,
        pickerPreviewColor = pickerPreviewColor,
        printColor = printColor,
    )
}
