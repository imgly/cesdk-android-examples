package ly.img.editor.base.components.color_picker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.github.ajalt.colormath.model.HSV

@Composable
internal fun SaturationValueArea(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onSaturationValueChanged: (saturation: Float, value: Float) -> Unit,
    onSaturationValueChangeFinished: () -> Unit,
) {
    val blackGradientBrush =
        remember {
            Brush.verticalGradient(listOf(Color(0xffffffff), Color(0xff000000)))
        }

    val currentColorGradientBrush =
        remember(currentColor.hue) {
            val rgb = HSV(h = currentColor.hue, s = 1.0f, v = 1.0f).toSRGB()
            Brush.horizontalGradient(
                listOf(
                    Color(0xffffffff),
                    Color(
                        red = rgb.redInt,
                        green = rgb.greenInt,
                        blue = rgb.blueInt,
                        alpha = rgb.alphaInt,
                    ),
                ),
            )
        }

    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(120.dp)
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val down = awaitFirstDown()
                            val (s, v) = getSaturationPoint(down.position, size)
                            onSaturationValueChanged(s, v)
                            drag(down.id) { change ->
                                if (change.positionChange() != Offset.Zero) change.consume()
                                val (newSaturation, newValue) =
                                    getSaturationPoint(
                                        change.position,
                                        size,
                                    )
                                onSaturationValueChanged(newSaturation, newValue)
                            }
                            onSaturationValueChangeFinished()
                        }
                    }
                },
    ) {
        val cornerRadiusPx = 12.dp.toPx()
        val cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)

        drawRoundRect(blackGradientBrush, cornerRadius = cornerRadius)
        drawRoundRect(
            currentColorGradientBrush,
            blendMode = BlendMode.Modulate,
            cornerRadius = cornerRadius,
        )

        val point = getSaturationValuePoint(currentColor, size = size)

        // outer circle
        drawCircle(
            color = outlineColor,
            radius = cornerRadiusPx,
            center = point,
        )

        // inner circle
        drawCircle(
            color = currentColor.toComposeColor(),
            radius = cornerRadiusPx - 1.dp.toPx(),
            center = point,
        )
    }
}

private fun getSaturationPoint(
    offset: Offset,
    size: IntSize,
): Pair<Float, Float> {
    val (saturation, value) =
        getSaturationValueFromPosition(
            offset,
            size.toSize(),
        )
    return saturation to value
}

private fun getSaturationValuePoint(
    color: HsvColor,
    size: Size,
): Offset {
    val height: Float = size.height
    val width: Float = size.width
    return Offset((color.saturation * width), (1f - color.value) * height)
}

private fun getSaturationValueFromPosition(
    offset: Offset,
    size: Size,
): Pair<Float, Float> {
    val width = size.width
    val height = size.height

    val newX = offset.x.coerceIn(0f, width)

    val newY = offset.y.coerceIn(0f, size.height)
    val saturation = 1f / width * newX
    val value = 1f - 1f / height * newY

    return saturation.coerceIn(0f, 1f) to value.coerceIn(0f, 1f)
}
