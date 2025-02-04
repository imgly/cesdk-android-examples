package ly.img.cesdk.core.components.color_picker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ColorPickerButton(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val gradient = remember {
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
        modifier = modifier
            .size(40.dp)
            .clickable { onClick() },
        onDraw = {
            val strokeWidthPx = 2.dp.toPx()

            // rainbow stroke
            drawCircle(
                brush = gradient,
                radius = size.minDimension / 2 - (strokeWidthPx / 2),
                style = Stroke(width = strokeWidthPx)
            )

            // contrast stroke
            drawCircle(
                color = outlineColor,
                radius = size.minDimension / 2 - (strokeWidthPx * 2)
            )

            // color circle
            drawCircle(
                color = color,
                radius = (size.minDimension / 2) - (strokeWidthPx * 2) - (strokeWidthPx / 2)
            )
        }
    )
}