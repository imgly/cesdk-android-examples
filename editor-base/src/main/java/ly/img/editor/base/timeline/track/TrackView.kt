package ly.img.editor.base.timeline.track

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ly.img.editor.base.timeline.clip.ClipView
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.ui.Event

@Composable
fun TrackView(
    track: Track,
    timelineState: TimelineState,
    modifier: Modifier = Modifier,
    onEvent: (Event) -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        track.clips.forEach { clip ->
            ClipView(
                clip = clip,
                timelineState = timelineState,
                onEvent = onEvent,
            )
        }
    }
}
