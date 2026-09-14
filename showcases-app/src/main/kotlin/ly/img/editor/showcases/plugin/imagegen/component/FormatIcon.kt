package ly.img.editor.showcases.plugin.imagegen.component

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.toPx
import ly.img.editor.showcases.plugin.imagegen.Format
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun FormatIcon(
    format: Format,
    modifier: Modifier = Modifier,
) {
    val color = MaterialTheme.colorScheme.onSurface
    val strokeWidth = 2.dp
    val iconSize = 20.dp
    val stroke = strokeWidth.toPx()

    Box(
        modifier = when (format) {
            Format.CUSTOM -> {
                modifier
                    .size(iconSize)
                    .drawBehind { drawCustomFormatBrackets(color, stroke) }
            }
            else -> {
                // Standard format with aspect ratio
                val (width, height) = format.ratio.split(":").map { it.toFloat() }
                val ratio = width / height
                val isSquare = ratio == 1f

                modifier
                    .let { if (width > height) it.height(iconSize) else it.width(iconSize) }
                    .aspectRatio(ratio)
                    .border(strokeWidth, color, RoundedCornerShape(4.dp))
                    .drawBehind {
                        if (isSquare) {
                            drawSquareDiagonal(color, stroke)
                        } else {
                            drawRectangleCorners(color, stroke)
                        }
                    }
            }
        },
    )
}

// Private extension functions for drawing logic
private fun DrawScope.drawCustomFormatBrackets(
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float,
) {
    inset(inset = strokeWidth) {
        val cornerLength = 4.dp.toPx()
        val w = size.width
        val h = size.height

        val path = Path().apply {
            // Top-left corner
            moveTo(0f, cornerLength)
            lineTo(0f, 0f)
            lineTo(cornerLength, 0f)
            // Top-right corner
            moveTo(w - cornerLength, 0f)
            lineTo(w, 0f)
            lineTo(w, cornerLength)
            // Bottom-right corner
            moveTo(w, h - cornerLength)
            lineTo(w, h)
            lineTo(w - cornerLength, h)
            // Bottom-left corner
            moveTo(cornerLength, h)
            lineTo(0f, h)
            lineTo(0f, h - cornerLength)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

private fun DrawScope.drawSquareDiagonal(
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float,
) {
    inset(5.dp.toPx()) {
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(2.dp.toPx(), 2.dp.toPx()),
                0f,
            ),
        )
    }
}

private fun DrawScope.drawRectangleCorners(
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float,
) {
    inset(5.dp.toPx()) {
        val cornerSize = 4.dp.toPx()
        val path = Path().apply {
            // Top-right corner detail
            moveTo(size.width - cornerSize, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, cornerSize)
            // Bottom-left corner detail
            moveTo(0f, size.height - cornerSize)
            lineTo(0f, size.height)
            lineTo(cornerSize, size.height)
        }
        drawPath(path, color, style = Stroke(width = strokeWidth))
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun FormatIconPreview() {
    PreviewTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Format Icons",
                    style = MaterialTheme.typography.titleMedium,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Format.values().forEach { format ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            FormatIcon(format = format)
                            Text(
                                text = if (format == Format.CUSTOM) format.label else format.ratio,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
