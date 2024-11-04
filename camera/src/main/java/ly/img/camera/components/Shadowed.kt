package ly.img.camera.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.core.theme.LocalExtendedColorScheme
import kotlin.math.ceil

// Compose the given content with a drop shadow on all non-transparent pixels
// Code taken from: https://skip.tools/blog/shadow-content-in-compose/ with some changes:
// 1. Added default values for parameters.
// 2. Added option to enable/disable the shadow.
// 3. Added `addWithSaturation()` to avoid overflow during addition.
@Composable
internal fun Shadowed(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = LocalExtendedColorScheme.current.black.copy(alpha = 0.45f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
    blurRadius: Dp = 6.dp,
    content: @Composable () -> Unit,
) {
    if (enabled) {
        val density = LocalDensity.current
        val offsetXPx = with(density) { offsetX.toPx() }.toInt()
        val offsetYPx = with(density) { offsetY.toPx() }.toInt()
        val blurRadiusPx =
            ceil(
                with(density) {
                    blurRadius.toPx()
                },
            ).toInt()

        // Modifier to render the content in the shadow color, then
        // blur it by blurRadius
        val shadowModifier =
            Modifier
                .drawWithContent {
                    val matrix = shadowColorMatrix(color)
                    val filter = ColorFilter.colorMatrix(matrix)
                    val paint =
                        Paint().apply {
                            colorFilter = filter
                        }
                    drawIntoCanvas { canvas ->
                        canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
                        drawContent()
                        canvas.restore()
                    }
                }
                .blur(radius = blurRadius, BlurredEdgeTreatment.Unbounded)
                .padding(all = blurRadius) // Pad to prevent clipping blur

        // Layout based solely on the content, placing shadow behind it
        Layout(modifier = modifier, content = {
            content()
            Box(modifier = shadowModifier) { content() }
        }) { measurables, constraints ->
            fun addWithSaturation(
                a: Int,
                b: Int,
            ): Int {
                return when {
                    a > 0 && b > 0 && a > Int.MAX_VALUE - b -> Int.MAX_VALUE // Positive overflow
                    a < 0 && b < 0 && a < Int.MIN_VALUE - b -> Int.MIN_VALUE // Negative overflow
                    else -> a + b // No overflow, safe to add
                }
            }
            // Allow shadow to go beyond bounds without affecting layout
            val contentPlaceable = measurables[0].measure(constraints)
            val shadowPlaceable =
                measurables[1].measure(
                    Constraints(
                        maxWidth = addWithSaturation(contentPlaceable.width, blurRadiusPx * 2),
                        maxHeight = addWithSaturation(contentPlaceable.height, blurRadiusPx * 2),
                    ),
                )
            layout(width = contentPlaceable.width, height = contentPlaceable.height) {
                shadowPlaceable.placeRelative(x = offsetXPx - blurRadiusPx, y = offsetYPx - blurRadiusPx)
                contentPlaceable.placeRelative(x = 0, y = 0)
            }
        }
    } else {
        content()
    }
}

// Return a color matrix with which to paint our content as a shadow of the given color
private fun shadowColorMatrix(color: Color): ColorMatrix {
    return ColorMatrix().apply {
        set(0, 0, 0f) // Do not preserve original R
        set(1, 1, 0f) // Do not preserve original G
        set(2, 2, 0f) // Do not preserve original B

        set(0, 4, color.red * 255) // Use given color's R
        set(1, 4, color.green * 255) // Use given color's G
        set(2, 4, color.blue * 255) // Use given color's B
        set(3, 3, color.alpha) // Multiply by given color's alpha
    }
}
