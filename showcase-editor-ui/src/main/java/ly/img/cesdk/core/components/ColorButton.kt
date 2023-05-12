package ly.img.cesdk.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.utils.ifTrue
import ly.img.cesdk.editorui.R

@Composable
fun ColorButton(
    color: Color?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    punchHole: Boolean = false,
    onClick: (() -> Unit)? = null,
    buttonSize: Dp = 40.dp,
    selectionStrokeWidth: Dp = 2.dp,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    val noStroke = color == null
    var brush: Brush? = null
    if (noStroke) {
        val image = ImageBitmap.imageResource(R.drawable.checkerboard_pattern)
        brush = remember(image) { ShaderBrush(ImageShader(image, TileMode.Repeated, TileMode.Repeated)) }
    }
    Canvas(
        modifier = modifier
            .size(buttonSize)
            .ifTrue(onClick != null) { clip(if (noStroke) RoundedCornerShape(30) else CircleShape) }
            .ifTrue(onClick != null) { clickable { onClick?.invoke() } },
        onDraw = {
            val contrastStrokeWidthPx = 1.dp.toPx()
            val selectionStrokeWidthPx = selectionStrokeWidth.toPx()

            // Blend modes are only working with using the layer directly
            // Consider using CompositionStrategy instead when it's available
            // https://stackoverflow.com/a/73317659/21169736
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // selection stroke
                if (selected) {
                    drawCircle(
                        color = primaryColor,
                        radius = size.minDimension / 2 - (selectionStrokeWidthPx / 2),
                        style = Stroke(width = selectionStrokeWidthPx)
                    )
                }

                // contrast stroke
                drawCircle(
                    color = outlineColor,
                    radius = size.minDimension / 2 - (selectionStrokeWidthPx * 2)
                )

                val circleRadius = size.minDimension / 2 - selectionStrokeWidthPx * 2 - contrastStrokeWidthPx

                if (noStroke) {
                    drawCircle(
                        brush = checkNotNull(brush),
                        radius = circleRadius
                    )
                } else {
                    drawCircle(
                        color = checkNotNull(color),
                        radius = circleRadius
                    )
                }

                if (punchHole) {
                    drawCircle(outlineColor, size.minDimension / 4)
                    drawCircle(Color.Black, size.minDimension / 4 - contrastStrokeWidthPx, blendMode = BlendMode.Clear)
                }

                if (noStroke) {
                    val coord = contrastStrokeWidthPx * 2 + selectionStrokeWidthPx * 1.5f
                    drawLine(
                        start = Offset(x = coord, y = coord),
                        end = Offset(x = size.width - coord, y = size.height - coord),
                        strokeWidth = contrastStrokeWidthPx * 3 + selectionStrokeWidthPx / 2,
                        cap = StrokeCap.Round,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    drawLine(
                        start = Offset(x = coord, y = coord),
                        end = Offset(x = size.width - coord, y = size.height - coord),
                        strokeWidth = contrastStrokeWidthPx * 2 + selectionStrokeWidthPx / 2,
                        cap = StrokeCap.Round,
                        color = Color.Black
                    )
                }

                restoreToCount(checkPoint)
            }
        }
    )
}