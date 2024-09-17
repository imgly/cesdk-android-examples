package ly.img.editor.base.timeline.clip.trim

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

enum class IconStyle {
    Neutral,
    Left,
    Right,
}

@Composable
fun ClipTrimHandleIconView(
    style: IconStyle,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Canvas(modifier = modifier.size(4.dp, 12.dp)) {
        val strokeWidth = (if (style == IconStyle.Neutral) 4f else 3.5f).dp.toPx()
        val path =
            Path().apply {
                when (style) {
                    IconStyle.Neutral -> {
                        moveTo(center.x, 0f)
                        lineTo(center.x, size.height)
                    }

                    IconStyle.Left -> {
                        moveTo(size.width * 0.75f, 0f)
                        lineTo(0f, size.height / 2)
                        lineTo(size.width * 0.75f, size.height)
                    }

                    IconStyle.Right -> {
                        moveTo(size.width * 0.25f, 0f)
                        lineTo(size.width, size.height / 2)
                        lineTo(size.width * 0.25f, size.height)
                    }
                }
            }
        drawPath(
            path = path,
            color = color,
            style =
                Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Miter,
                ),
        )
    }
}
