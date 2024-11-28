package ly.img.editor.base.dock.options.reorder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.clip.ClipBackgroundView
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.core.theme.surface2
import ly.img.editor.core.ui.utils.formatForClip
import kotlin.time.Duration

@Composable
fun ReorderingThumbnailsView(
    clip: Clip,
    timelineState: TimelineState,
    elevation: Dp,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(64.dp),
        shadowElevation = elevation,
        shape = MaterialTheme.shapes.small,
    ) {
        Box {
            ClipBackgroundView(
                clip = clip,
                timelineState = timelineState,
            )
            DurationText(
                duration = clip.duration,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
            )
        }
    }
}

@Composable
private fun DurationText(
    duration: Duration,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier.alpha(0.75f),
        color = MaterialTheme.colorScheme.surface2,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = duration.formatForClip(),
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
