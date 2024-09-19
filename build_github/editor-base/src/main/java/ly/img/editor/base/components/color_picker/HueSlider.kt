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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import com.github.ajalt.colormath.model.HSV
import ly.img.editor.compose.material3.Slider
import ly.img.editor.compose.material3.SliderDefaults

@Composable
internal fun HueSlider(
    value: Float,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    var sliderValue by remember(value) { mutableStateOf(value) }
    val colors =
        SliderDefaults.colors(
            thumbColor =
                HSV(
                    h = sliderValue,
                    s = 1.0,
                    v = 1.0,
                    alpha = 1,
                ).toComposeColor(),
        )
    Slider(
        value = sliderValue,
        modifier = modifier,
        onValueChange = {
            sliderValue = it
            onValueChange(it)
        },
        steps = 359,
        valueRange = 0f..360f,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        track = {
            Track()
        },
    )
}

@Composable
private fun Track(modifier: Modifier = Modifier) {
    val trackHeight = 4.dp
    val gradient =
        remember {
            Brush.linearGradient(
                listOf(
                    Color(0xFFEC5151),
                    Color(0xFFFFDB00),
                    Color(0xFF00F900),
                    Color(0xFF00FCEC),
                    Color(0xFF1D0EE3),
                    Color(0xFFFF37C4),
                    Color(0xFFFF271E),
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
            brush = gradient,
            start = sliderStart,
            end = sliderEnd,
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
