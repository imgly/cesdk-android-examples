package ly.img.editor.base.components.color_picker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.utils.ifTrue

@Composable
fun ColorPickerButton(
    color: Color,
    modifier: Modifier = Modifier,
    punchHole: Boolean = false,
    onClick: (() -> Unit)?,
) {
    val gradient =
        remember {
            Brush.sweepGradient(
                0.0f to Color(0xFFFFC700),
                0.18f to Color(0xFF05FF00),
                0.34f to Color(0xFF00FFFF),
                0.47f to Color(0xFF001AFF),
                0.6f to Color(0xFFFA00FF),
                0.75f to Color(0xFFFF0000),
                0.9f to Color(0xFFFF6500),
                1.0f to Color(0xFFFFC700),
            )
        }
    val outlineColor = MaterialTheme.colorScheme.outline
    Canvas(
        modifier =
            modifier
                .size(40.dp)
                .ifTrue(onClick != null) {
                    clickable { onClick?.invoke() }
                },
        onDraw = {
            // Blend modes are only working with using the layer directly
            // Consider using CompositionStrategy instead when it's available
            // https://stackoverflow.com/a/73317659/21169736
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                val strokeWidthPx = 2.dp.toPx()

                // rainbow stroke
                drawCircle(
                    brush = gradient,
                    radius = size.minDimension / 2 - (strokeWidthPx / 2),
                    style = Stroke(width = strokeWidthPx),
                )

                // contrast stroke
                drawCircle(
                    color = outlineColor,
                    radius = size.minDimension / 2 - (strokeWidthPx * 2),
                )

                // color circle
                drawCircle(
                    color = color,
                    radius = (size.minDimension / 2) - (strokeWidthPx * 2) - (strokeWidthPx / 2),
                )

                if (punchHole) {
                    drawCircle(outlineColor, size.minDimension / 4)
                    drawCircle(
                        Color.Black,
                        size.minDimension / 4 - 1.dp.toPx(),
                        blendMode = BlendMode.Clear,
                    )
                }
                restoreToCount(checkPoint)
            }
        },
    )
}
