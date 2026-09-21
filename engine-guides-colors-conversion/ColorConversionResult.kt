import ly.img.engine.CMYKColor
import ly.img.engine.RGBAColor

data class ColorConversionResult(
    val cmykToSrgb: RGBAColor,
    val spotToSrgb: RGBAColor,
    val srgbToCmyk: CMYKColor,
    val spotToCmyk: CMYKColor,
    val transparentSrgbToCmyk: CMYKColor,
    val tintedCmykPreview: RGBAColor,
    val tintedSpotPreview: RGBAColor,
    val tintedSpotToCmyk: CMYKColor,
    val detectedTypes: List<String>,
    val pickerPreviewColor: RGBAColor,
    val printColor: CMYKColor,
)
