package ly.img.editor.core.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

enum class ColorSchemeKeyToken {
    // Currently we only use one token, this can be expanded in the future when the need arises
    Error,
}

fun ColorScheme.fromToken(value: ColorSchemeKeyToken): Color {
    return when (value) {
        ColorSchemeKeyToken.Error -> error
    }
}
