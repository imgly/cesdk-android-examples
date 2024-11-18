package ly.img.editor.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColorScheme(
    val purple: ColorFamily,
    val green: ColorFamily,
    val yellow: ColorFamily,
    val black: Color = Color(0xFF000000),
    val white: Color = Color(0xFFFFFFFF),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
)

internal fun lightExtendedColorScheme() =
    ExtendedColorScheme(
        purple =
            ColorFamily(
                color = Color(0xFF310048),
                onColor = Color(0xFF8D2EBC),
                colorContainer = Color(0xFFF6D9FF),
            ),
        green =
            ColorFamily(
                color = Color(0xFF3C6A1C),
                onColor = Color(0xFFFFFFFF),
                colorContainer = Color(0xFFBCF293),
            ),
        yellow =
            ColorFamily(
                color = Color(0xFFE7C42E),
                onColor = Color(0xFF3B2F00),
                colorContainer = Color(0xFF554500),
            ),
    )

internal fun darkExtendedColorScheme() =
    ExtendedColorScheme(
        purple =
            ColorFamily(
                color = Color(0xFFF6D9FF),
                onColor = Color(0xFFE8B3FF),
                colorContainer = Color(0xFF7201A2),
            ),
        green =
            ColorFamily(
                color = Color(0xFFA0D57A),
                onColor = Color(0xFFBCF293),
                colorContainer = Color(0xFF255102),
            ),
        yellow =
            ColorFamily(
                color = Color(0xFFE7C42E),
                onColor = Color(0xFF3B2F00),
                colorContainer = Color(0xFF554500),
            ),
    )

val LocalExtendedColorScheme = staticCompositionLocalOf { lightExtendedColorScheme() }
