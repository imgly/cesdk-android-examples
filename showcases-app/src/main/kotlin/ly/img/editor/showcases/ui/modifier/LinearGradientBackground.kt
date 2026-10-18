package ly.img.editor.showcases.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.linearGradientBackground(
    height: Dp,
    shape: Shape,
) = composed {
    this.background(
        brush = with(LocalDensity.current) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0x39C8C6CB),
                    Color(0x3978777C),
                ),
                start = Offset(0f, 0f),
                end = Offset(0f, height.toPx()),
            )
        },
        shape = shape,
    )
}
