package ly.img.editor

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun ExportProgressIndicator(
    progress: Float,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gradientColor: Color = MaterialTheme.colorScheme.onSurface,
    strokeWidth: Dp = 8.dp,
    size: Dp = 144.dp,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    val stroke =
        with(LocalDensity.current) {
            Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        }

    val transition = rememberInfiniteTransition()
    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
            ),
        label = "Gradient Rotation",
    )

    val sweepGradient =
        remember(gradientColor) {
            Brush.sweepGradient(
                colorStops =
                    listOf(
                        0.02f to Color.Transparent,
                        0.5f to gradientColor.copy(alpha = 0.24f),
                        1f to Color.Transparent,
                    ).toTypedArray(),
            )
        }

    Canvas(
        Modifier
            .progressSemantics(animatedProgress)
            .size(size - strokeWidth * 2),
    ) {
        drawSweepGradientCircularIndicator(
            rotationAngle = rotationAngle,
            stroke = stroke,
            gradientBrush = sweepGradient,
        )

        drawCircularIndicator(
            startAngle = 270f,
            sweep = animatedProgress * 360f,
            color = color,
            stroke = stroke,
        )
    }
}

private fun DrawScope.drawSweepGradientCircularIndicator(
    rotationAngle: Float,
    stroke: Stroke,
    gradientBrush: Brush,
) {
    rotate(-180f + rotationAngle) {
        val diameterOffset = stroke.width / 2
        val arcDimen = size.width - 2 * diameterOffset
        drawArc(
            brush = gradientBrush,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            style = stroke,
            size = Size(arcDimen, arcDimen),
        )
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke,
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke,
    )
}
