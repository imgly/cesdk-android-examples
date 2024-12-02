package ly.img.editor.core.component.data

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import ly.img.engine.GradientColorStop
import ly.img.engine.RGBAColor
import kotlin.math.atan2

/**
 * Class that contains information about the fill of a design block.
 */
@Stable
sealed interface Fill {
    /**
     * The main color of the fill.
     */
    val mainColor: Color
}

/**
 * A type of [Fill] that is a solid color.
 */
@Stable
data class SolidFill(
    /**
     * The only and main color of the fill.
     */
    override val mainColor: Color,
) : Fill

/**
 * A type of [Fill] that is a gradient.
 */
@Stable
interface GradientFill : Fill {
    /**
     * The list of all the color stops of the gradient. Check the documentation of [GradientColorStop] for more information.
     */
    val colorStops: List<GradientColorStop>

    /**
     * The main color of the fill. It returns the first color in the [colorStops] list.
     */
    override val mainColor: Color
        get() = (colorStops.first().color as? RGBAColor)?.toComposeColor() ?: Color.Black
}

/**
 * A type of [Fill] that is a linear gradient.
 */
@Stable
data class LinearGradientFill(
    /**
     * The x coordinate of the start point.
     */
    val startPointX: Float,
    /**
     * The y coordinate of the start point.
     */
    val startPointY: Float,
    /**
     * The x coordinate of the end point.
     */
    val endPointX: Float,
    /**
     * The y coordinate of the end point.
     */
    val endPointY: Float,
    /**
     * The list of all the color stops of the gradient. Check the documentation of [GradientColorStop] for more information.
     */
    override val colorStops: List<GradientColorStop>,
) : GradientFill {
    /**
     * The angle of gradient in degrees.
     */
    val gradientRotation: Float by lazy {
        val dx = endPointX - startPointX
        val dy = endPointY - startPointY
        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angle < 0) angle + 360 else angle
    }
}

/**
 * A type of [Fill] that is a radial gradient.
 */
@Stable
data class RadialGradientFill(
    /**
     * The x coordinate of the center.
     */
    val centerX: Float,
    /**
     * The y coordinate of the center.
     */
    val centerY: Float,
    /**
     * The radius of the gradient.
     */
    val radius: Float,
    /**
     * The list of all the color stops of the gradient. Check the documentation of [GradientColorStop] for more information.
     */
    override val colorStops: List<GradientColorStop>,
) : GradientFill

/**
 * A type of [Fill] that is a conical gradient.
 */
@Stable
data class ConicalGradientFill(
    /**
     * The x coordinate of the center.
     */
    val centerX: Float,
    /**
     * The y coordinate of the center.
     */
    val centerY: Float,
    /**
     * The list of all the color stops of the gradient. Check the documentation of [GradientColorStop] for more information.
     */
    override val colorStops: List<GradientColorStop>,
) : GradientFill

internal fun RGBAColor.toComposeColor(): Color {
    return Color(
        red = this.r,
        green = this.g,
        blue = this.b,
        alpha = this.a,
    )
}
