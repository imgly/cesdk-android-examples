package ly.img.editor.base.components.color_picker

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.HSV
import com.github.ajalt.colormath.model.RGB

internal data class HsvColor(
    @FloatRange(from = 0.0, to = 360.0) val hue: Float,
    @FloatRange(from = 0.0, to = 1.0) val saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) val value: Float,
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float,
) {
    companion object {
        fun from(color: Color): HsvColor {
            return RGB(
                r = color.red,
                g = color.green,
                b = color.blue,
                alpha = color.alpha,
            ).toHSV().toColor()
        }

        private fun HSV.toColor(): HsvColor {
            return HsvColor(
                hue = if (this.h.isNaN()) 0f else this.h,
                saturation = this.s,
                value = this.v,
                alpha = this.alpha,
            )
        }
    }
}

internal fun HsvColor.toComposeColor(): Color {
    return HSV(
        h = if (hue == 360f) 0 else hue,
        s = saturation,
        v = value,
        alpha = alpha,
    ).toComposeColor()
}
