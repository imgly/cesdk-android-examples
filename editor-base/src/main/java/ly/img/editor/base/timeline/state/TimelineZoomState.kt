package ly.img.editor.base.timeline.state

import android.content.res.Resources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class TimelineZoomState(
    // How many dps per second in the timeline when the zoom is set to 100% (1.0)
    private val dpToSecondsRatio: Int = 12,
    private val minimumZoomLevel: Float = 0.6f,
    private val maximumZoomLevel: Float = 6.0f,
) {
    var zoomLevel by mutableStateOf(2.0f)
        private set

    fun setZoom(zoom: Float) {
        zoomLevel = (zoomLevel * zoom).coerceIn(minimumZoomLevel, maximumZoomLevel)
    }

    fun toSeconds(dp: Dp) = (dp.value / zoomLevel / dpToSecondsRatio).toDouble().seconds

    fun toSeconds(px: Float) = toSeconds((px / Resources.getSystem().displayMetrics.density).dp)

    fun toDp(duration: Duration) = (duration.toDouble(DurationUnit.SECONDS) * zoomLevel * dpToSecondsRatio).dp

    fun toPx(duration: Duration) = toDp(duration).value * Resources.getSystem().displayMetrics.density
}
