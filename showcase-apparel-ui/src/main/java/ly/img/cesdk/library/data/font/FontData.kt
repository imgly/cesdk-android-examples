package ly.img.cesdk.library.data.font

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.json.JSONObject

data class FontData(
    val id: String,
    val fontFamily: String,
    val fontWeight: FontWeight,
    val fontStyle: FontStyle,
    val fontPath: String
) {
    companion object {
        fun createFontData(jsonObject: JSONObject): FontData {
            return FontData(
                id = jsonObject.getString("id"),
                fontFamily = jsonObject.getString("fontFamily"),
                fontPath = jsonObject.getString("fontPath"),
                fontStyle = if (jsonObject.optString("fontStyle") == "italic") FontStyle.Italic else FontStyle.Normal,
                fontWeight = getFontWeight(jsonObject["fontWeight"])
            )
        }

        private fun getFontWeight(any: Any): FontWeight {
            return when (any) {
                is Int -> FontWeight(any)
                "bold" -> FontWeight.Bold
                "light" -> FontWeight.Light
                "medium" -> FontWeight.Medium
                "normal" -> FontWeight.Normal
                else -> throw IllegalArgumentException("Font Weight can be only be bold, light, medium, normal, or in the range 1-1000.")
            }
        }
    }

    fun isRegular() = fontWeight == FontWeight.Normal
    fun isBold() = fontWeight == FontWeight.Bold
    fun isItalic() = fontStyle == FontStyle.Italic
}
