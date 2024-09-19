package ly.img.editor.base.timeline.clip

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.utils.toPx

fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 1.dp,
    dashWidth: Dp = 4.dp,
    gapWidth: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Butt,
) = this.drawWithContent {
    val outline = shape.createOutline(size, layoutDirection, this)

    val path = Path()
    path.addOutline(outline)

    val stroke =
        Stroke(
            cap = cap,
            width = strokeWidth.toPx(),
            pathEffect =
                PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
                    phase = 0f,
                ),
        )

    this.drawContent()

    drawPath(
        path = path,
        style = stroke,
        color = color,
    )
}

@Composable
fun Modifier.animatedDashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 1.dp,
    dashWidth: Dp = 2.dp,
    gapWidth: Dp = 2.dp,
    cap: StrokeCap = StrokeCap.Butt,
    animationDuration: Int = 1500,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "Phase Infinite Transition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = dashWidth.toPx() + gapWidth.toPx(),
        animationSpec =
            infiniteRepeatable(
                animation = tween(animationDuration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "Dash Phase Animation",
    )

    return this.drawWithContent {
        val outline = shape.createOutline(size, layoutDirection, this)

        val path = Path()
        path.addOutline(outline)

        val stroke =
            Stroke(
                cap = cap,
                width = strokeWidth.toPx(),
                pathEffect =
                    PathEffect.dashPathEffect(
                        intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
                        phase = phase,
                    ),
            )

        this.drawContent()

        drawPath(
            path = path,
            style = stroke,
            color = color,
        )
    }
}
