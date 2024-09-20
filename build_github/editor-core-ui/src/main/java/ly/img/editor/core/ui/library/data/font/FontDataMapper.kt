package ly.img.editor.core.ui.library.data.font

import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.Typeface

class FontDataMapper {
    fun map(asset: WrappedAsset): FontData {
        val typeface = requireNotNull(asset.asset.payload.typeface)
        val displayFont = getDefaultDisplayFont(typeface)
        return FontData(
            typeface = typeface,
            uri = displayFont.uri,
            weight = androidx.compose.ui.text.font.FontWeight(displayFont.weight.value),
            style = displayFont.style,
        )
    }

    fun getFontData(
        typeface: Typeface,
        weight: Int?,
    ): FontData {
        val finalWeight = weight ?: FontWeight.NORMAL.value
        val displayFont =
            typeface.fonts.firstOrNull {
                it.weight.value == finalWeight && it.style == FontStyle.NORMAL
            } ?: typeface.fonts.first()
        return FontData(
            typeface = typeface,
            uri = displayFont.uri,
            weight = androidx.compose.ui.text.font.FontWeight(displayFont.weight.value),
            style = displayFont.style,
        )
    }

    private fun getDefaultDisplayFont(typeface: Typeface): Font {
        val fonts = typeface.fonts
        return fonts.find { it.weight == FontWeight.NORMAL && it.style != FontStyle.ITALIC }
            ?: fonts.find { it.weight == FontWeight.BOLD && it.style != FontStyle.ITALIC }
            ?: fonts.find { it.weight == FontWeight.NORMAL && it.style == FontStyle.ITALIC }
            ?: fonts.find { it.weight == FontWeight.BOLD && it.style == FontStyle.ITALIC }
            ?: fonts[0]
    }
}
