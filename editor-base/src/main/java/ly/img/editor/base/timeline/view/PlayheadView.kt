package ly.img.editor.base.timeline.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.utils.toPx

@Composable
fun PlayheadView(
    modifier: Modifier = Modifier,
    outlineColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    color: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: CornerRadius = CornerRadius(4.dp.toPx()),
) {
    Canvas(
        modifier =
            modifier
                .fillMaxHeight()
                .width(3.dp),
    ) {
        drawRoundRect(color = outlineColor, cornerRadius = cornerRadius, alpha = 0.24f)
        inset(1.dp.roundToPx().toFloat()) {
            drawRoundRect(color = color, cornerRadius = cornerRadius)
        }
    }
}
