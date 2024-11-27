package ly.img.editor.base.timeline.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.base.timeline.modifier.OffsetDirection
import ly.img.editor.base.timeline.modifier.offsetByWidth
import ly.img.editor.base.timeline.state.TimelineZoomState
import ly.img.editor.core.ui.utils.formatForClip
import ly.img.editor.core.ui.utils.formatForPlayer
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimelineRulerView(
    duration: Duration,
    zoomState: TimelineZoomState,
    height: Dp,
    extraWidth: Dp,
) {
    Box(modifier = Modifier.height(height)) {
        val tickInterval by remember {
            derivedStateOf {
                when (zoomState.zoomLevel) {
                    in 0.0..<0.4 -> 60
                    in 0.4..<0.6 -> 40
                    in 0.6..<1.0 -> 20
                    in 1.0..<2.0 -> 10
                    else -> 5
                }
            }
        }
        val tickIntervalInSeconds = tickInterval.seconds
        val totalDuration =
            remember(duration, extraWidth, zoomState.zoomLevel) {
                val drawingDuration = duration + zoomState.toSeconds(extraWidth)
                (ceil(drawingDuration.inWholeSeconds / 10.0) * 10).toInt()
            }
        val totalDurationInSeconds = totalDuration.seconds

        (0..totalDuration step tickInterval).forEach { secs ->
            val seconds = secs.seconds
            Text(
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .offset(zoomState.toDp(seconds))
                        // offset the duration text by half of its width to make it appear in center
                        .offsetByWidth(OffsetDirection.Left)
                        .alpha(0.5f),
                text =
                    if (seconds < 60.seconds) {
                        seconds.formatForClip(showFractionalPart = false)
                    } else {
                        seconds.formatForPlayer()
                    },
                style = MaterialTheme.typography.labelMedium,
            )

            if (tickInterval == 5) {
                (1..4).forEach {
                    val inSeconds = it.seconds
                    if (seconds + inSeconds <= totalDurationInSeconds) {
                        Tick(
                            modifier =
                                Modifier
                                    .align(Alignment.CenterStart)
                                    .offset(zoomState.toDp(seconds + inSeconds)),
                        )
                    }
                }
            } else {
                if (seconds + tickIntervalInSeconds / 2 <= totalDurationInSeconds) {
                    Tick(
                        modifier =
                            Modifier
                                .align(Alignment.CenterStart)
                                .offset(zoomState.toDp(seconds + tickIntervalInSeconds / 2)),
                    )
                }
            }
        }
    }
}

@Composable
private fun Tick(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Canvas(
        modifier =
            modifier
                .size(3.dp)
                .alpha(0.5f),
    ) {
        drawCircle(color = color)
    }
}
