package ly.img.editor.base.timeline.clip.audio

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.core.theme.LocalExtendedColorScheme
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun AudioWaveformView(
    zoomLevel: Float,
    modifier: Modifier = Modifier,
    barWidth: Dp = 1.25.dp,
    barGap: Dp = 1.25.dp,
    barColor: Color = LocalExtendedColorScheme.current.purple.color,
) {
    Canvas(
        modifier = modifier,
    ) {
        val barWidthPx = barWidth.toPx()
        val barGapPx = barGap.toPx()
        val waveformZoomLevel = zoomLevel * 0.25f + 1

        var index = 0
        var xOffset = 0f
        while (xOffset < size.width) {
            val height = abs(sin(index / waveformZoomLevel)) * size.height * 0.7f + size.height * 0.3f
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x = xOffset, y = (size.height - height) / 2),
                size = Size(width = barWidthPx, height = height),
                cornerRadius = CornerRadius(0.5f),
            )
            index++
            xOffset = index * (barWidthPx + barGapPx)
        }
    }
}
