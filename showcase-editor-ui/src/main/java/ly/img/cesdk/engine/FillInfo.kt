package ly.img.cesdk.engine

import androidx.compose.ui.graphics.Color
import ly.img.engine.RGBAColor
import ly.img.engine.GradientColorStop
import kotlin.math.atan2
import kotlin.math.tan

interface Fill {
    val fillColor: Color
}

interface GradientFill : Fill {
    val colorStops: List<GradientColorStop>
    override val fillColor: Color get() = (colorStops.first().color as? RGBAColor)?.toComposeColor() ?: Color.Black
}

class SolidFill(
    override val fillColor: Color
) : Fill

data class LinearGradientFill(
    val startPointX: Float,
    val startPointY: Float,
    val endPointX: Float,
    val endPointY: Float,
    override val colorStops: List<GradientColorStop>
) : GradientFill {
    val gradientRotation: Float
        get() {
            val dx = endPointX - startPointX
            val dy = endPointY - startPointY
            val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
            return if (angle < 0) angle + 360 else angle
        }

    companion object {
        fun calculateLinearGradientFromRotation(rotationInDegrees: Float, colorStops: List<GradientColorStop>): LinearGradientFill {

            val absRotationInDegrees = ((rotationInDegrees % 360.0) + 360.0) % 360.0

            val slope = tan(Math.toRadians(absRotationInDegrees))

            val startX: Double
            val startY: Double
            val endX: Double
            val endY: Double

            when (absRotationInDegrees) {
                in 0f..45f, in 315f..360f -> {
                    startX = 0.0
                    startY = 0.5 - 0.5 * slope
                    endX = 1.0
                    endY = 0.5 + 0.5 * slope
                }
                in 135.0f..225.0f -> {
                    startX = 1.0
                    startY = 0.5 + 0.5 * slope
                    endX = 0.0
                    endY = 0.5 - 0.5 * slope
                }
                in 45f..135.0f -> {
                    startX = 0.5 - 0.5 / slope
                    startY = 0.0
                    endX = 0.5 + 0.5 / slope
                    endY = 1.0
                }
                else -> { // 225.0f..315.0f
                    startX = 0.5 + 0.5 / slope
                    startY = 1.0
                    endX = 0.5 - 0.5 / slope
                    endY = 0.0
                }
            }

            return LinearGradientFill(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), colorStops)
        }
    }
}

class RadialGradientFill(
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    override val colorStops: List<GradientColorStop>
) : GradientFill

class ConicalGradientFill(
    val centerX: Float,
    val centerY: Float,
    override val colorStops: List<GradientColorStop>
) : GradientFill