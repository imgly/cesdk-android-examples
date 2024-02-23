package ly.img.editor.base.components.color_picker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPreview(
    color: Color,
    modifier: Modifier = Modifier,
    outlineColor: Color = MaterialTheme.colorScheme.outline,
) {
    Canvas(
        modifier = modifier.size(80.dp),
    ) {
        val cornerRadiusPx = 12.dp.toPx()
        val cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)

        drawRoundRect(
            color = outlineColor,
            cornerRadius = cornerRadius,
        )

        val outlineWidth = 1.dp.toPx()
        val offset = Offset(outlineWidth, outlineWidth)

        drawRoundRect(
            color = color,
            cornerRadius = cornerRadius,
            topLeft = offset,
            size = Size(size.width - 2 * outlineWidth, size.height - 2 * outlineWidth),
        )
    }
}
