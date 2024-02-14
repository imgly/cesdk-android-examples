package ly.img.editor.core.ui.library.data.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import ly.img.editor.core.ui.library.data.getFontsDir
import java.io.File

private const val DEFAULT_FONT_FAMILY = "Roboto"

class FontFamilyData(val name: String, private val fonts: List<FontData>) {
    private val regularFont = fonts.find { it.isRegular() && !it.isItalic() }
    private val boldFont = fonts.find { it.isBold() && !it.isItalic() }
    private val italicFont = fonts.find { it.isRegular() && it.isItalic() }
    private val boldItalicFont = fonts.find { it.isBold() && it.isItalic() }

    val displayFont = regularFont ?: boldFont ?: italicFont ?: boldItalicFont ?: fonts[0]
    val hasBold = boldFont != null
    val hasItalic = italicFont != null
    val hasRegular = regularFont != null
    val hasBoldItalic = boldItalicFont != null

    val displayFontsData: Array<FontData> =
        if (name == DEFAULT_FONT_FAMILY) {
            arrayOf(displayFont, checkNotNull(boldFont), getFontData(FontWeight.W500))
        } else {
            arrayOf(displayFont)
        }

    // On older Android versions, creating the FontFamily before the font paths exist throws an exception
    val fontFamily by lazy { createFontFamily() }

    fun getFontData(
        fontWeight: FontWeight,
        fontStyle: FontStyle = FontStyle.Normal,
    ): FontData {
        return fonts.find { it.fontWeight == fontWeight && it.fontStyle == fontStyle } ?: displayFont
    }

    fun getFontData(path: String): FontData? {
        return fonts.find { path.endsWith(it.fontPath) }
    }

    fun getFontData(
        bold: Boolean,
        italic: Boolean,
    ): FontData {
        val font =
            if (!bold && !italic) {
                regularFont
            } else if (bold && !italic) {
                boldFont
            } else if (!bold) {
                italicFont
            } else {
                boldItalicFont
            }
        return font ?: displayFont
    }

    private fun createFontFamily(): FontFamily {
        return FontFamily(
            displayFontsData.map {
                Font(File(getFontsDir(), it.fontPath), it.fontWeight, it.fontStyle)
            },
        )
    }
}
