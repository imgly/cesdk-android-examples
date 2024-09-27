package ly.img.editor.base.timeline.clip.trim

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ClipSelectionView(
    modifier: Modifier,
    handleWidth: Dp,
    cornerRadius: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Canvas(
        modifier = modifier,
    ) {
        val markerWidthPx = 4.dp.roundToPx().toFloat()
        val markerHeightPx = 1.dp.roundToPx().toFloat()
        val verticalInsetPx = 2.dp.roundToPx().toFloat()
        val handleWidthPx = handleWidth.roundToPx()

        val outerRect =
            Rect(
                topLeft = Offset.Zero,
                bottomRight = Offset(x = size.width, y = size.height),
            )

        val innerRect =
            Rect(
                left = outerRect.left + handleWidthPx,
                top = outerRect.top + verticalInsetPx,
                right = outerRect.right - handleWidthPx,
                bottom = outerRect.bottom - verticalInsetPx,
            )

        val outerPath =
            Path().apply {
                addRoundRect(
                    RoundRect(outerRect, cornerRadius = CornerRadius((cornerRadius + 2.dp).roundToPx().toFloat())),
                )
            }

        val innerPath =
            Path().apply {
                addRoundRect(
                    RoundRect(innerRect, cornerRadius = CornerRadius((cornerRadius).roundToPx().toFloat())),
                )
            }

        fun getTrianglePath(
            offset: Offset,
            pointingUp: Boolean,
            width: Float = markerWidthPx,
            height: Float = markerHeightPx,
        ): Path {
            return Path().apply {
                moveTo(offset.x, offset.y)
                if (pointingUp) {
                    relativeLineTo(width, 0f)
                    relativeLineTo(-width / 2, -height)
                } else {
                    relativeLineTo(width / 2, height)
                    relativeLineTo(width / 2, -height)
                }
                close()
            }
        }

        val path =
            Path().apply {
                op(outerPath, innerPath, PathOperation.Difference)
                op(
                    this,
                    getTrianglePath(Offset(x = handleWidthPx - markerWidthPx / 2, y = 0f), pointingUp = false),
                    PathOperation.Difference,
                )
                op(
                    this,
                    getTrianglePath(Offset(x = size.width - handleWidthPx - markerWidthPx / 2, y = 0f), pointingUp = false),
                    PathOperation.Difference,
                )
                op(
                    this,
                    getTrianglePath(Offset(x = handleWidthPx - markerWidthPx / 2, y = size.height), pointingUp = true),
                    PathOperation.Difference,
                )
                op(
                    this,
                    getTrianglePath(
                        Offset(x = size.width - handleWidthPx - markerWidthPx / 2, y = size.height),
                        pointingUp = true,
                    ),
                    PathOperation.Difference,
                )
            }

        drawPath(path = path, color = color)
    }
}
