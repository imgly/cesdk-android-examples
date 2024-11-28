package ly.img.editor.base.engine

import ly.img.engine.Color
import ly.img.engine.RGBAColor
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

fun RGBAColor.toComposeColor(): ComposeColor {
    return ComposeColor(
        red = this.r,
        green = this.g,
        blue = this.b,
        alpha = this.a,
    )
}

fun ComposeColor.toEngineColor(): RGBAColor {
    return Color.fromRGBA(
        r = this.red,
        g = this.green,
        b = this.blue,
        a = this.alpha,
    )
}

/**
 * Returns a new instance of [RGBAColor] with an increased lightness by [lightnessChangeInPercentage].
 * @param lightnessChangeInPercentage a value between -1 and 1. -1 means 100% darker, 1 means 100% lighter.
 * @return The new instance of [RGBAColor].
 */
fun RGBAColor.increaseLightnessBy(lightnessChangeInPercentage: Float): Color {
    val lightnessChange = lightnessChangeInPercentage.coerceIn(-1f, 1f)
    val hsl =
        FloatArray(3).also { hsl ->
            AndroidColor.RGBToHSV(
                (this.r * 255).roundToInt(),
                (this.g * 255).roundToInt(),
                (this.b * 255).roundToInt(),
                hsl,
            )
        }
    hsl[2] = (hsl[2] + lightnessChange).coerceIn(0f, 1f)
    return Color.fromColor(AndroidColor.HSVToColor(hsl))
}

/**
 * Returns a new instance of [RGBAColor] with a changed lightness by [lightnessChangeInPercentage].
 * In contrast to [increaseLightnessBy], this method will increase the lightness if the current lightness is below 50% and decrease it otherwise.
 * @param lightnessChangeInPercentage a value between 0f and 1f. 0 means no change, 1 means a 100% of the lightness.
 * @return The new instance of [RGBAColor].
 */
fun RGBAColor.changeLightnessBy(lightnessChangeInPercentage: Float): Color {
    val lightnessChange = lightnessChangeInPercentage.coerceIn(0f, 1f)
    val hsl =
        FloatArray(3).also { hsl ->
            AndroidColor.RGBToHSV(
                (this.r * 255).roundToInt(),
                (this.g * 255).roundToInt(),
                (this.b * 255).roundToInt(),
                hsl,
            )
        }

    if (hsl[2] > 0.5) {
        hsl[2] = (hsl[2] - lightnessChange).coerceAtLeast(0f)
    } else {
        hsl[2] = (hsl[2] + lightnessChange).coerceAtMost(1f)
    }
    return Color.fromColor(AndroidColor.HSVToColor((this.a * 255).roundToInt(), hsl))
}
