package ly.img.editor.base.components.color_picker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.material3.Slider
import ly.img.editor.compose.material3.SliderDefaults
import ly.img.editor.core.R

@Composable
internal fun OpacitySlider(
    color: Color,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    var sliderValue by remember(color) { mutableStateOf(color.alpha) }
    val colors =
        SliderDefaults.colors(
            thumbColor = Color.White,
        )

    Slider(
        value = sliderValue,
        modifier = modifier,
        onValueChange = {
            sliderValue = it
            onValueChange(it)
        },
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        track = {
            Track(color)
        },
    )
}

@Composable
private fun Track(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val trackHeight = 4.dp
    val image = ImageBitmap.imageResource(R.drawable.checkerboard_pattern)
    val opacityBrush =
        remember(image) { ShaderBrush(ImageShader(image, TileMode.Repeated, TileMode.Repeated)) }
    val gradientBrush =
        remember(color) {
            Brush.linearGradient(
                listOf(
                    color.copy(alpha = 0f),
                    color.copy(alpha = 1f),
                ),
            )
        }
    Canvas(
        modifier
            .fillMaxWidth()
            .height(trackHeight),
    ) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val sliderLeft = Offset(0f, center.y)
        val sliderRight = Offset(size.width, center.y)
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight
        val trackStrokeWidth = trackHeight.toPx()

        drawLine(
            brush = opacityBrush,
            start = sliderStart,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round,
        )

        drawLine(
            brush = gradientBrush,
            start = sliderStart,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
