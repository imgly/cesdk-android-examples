package ly.img.editor.base.timeline.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.utils.detectZoomGestures
import ly.img.editor.core.ui.utils.toDp
import ly.img.editor.core.ui.utils.toPx

@Composable
internal fun TimelineBaseView(
    timelineState: TimelineState,
    onEvent: (Event) -> Unit,
    content:
        @Composable @UiComposable
        BoxWithConstraintsScope.(ScrollState) -> Unit,
) {
    val zoomState = timelineState.zoomState

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .padding(top = TimelineConfiguration.clipPadding)
            .height(timelineState.timelineViewHeight)
            .pointerInput(Unit) {
                detectZoomGestures(
                    onZoom = { zoom ->
                        zoomState.setZoom(zoom)
                    },
                    onZoomEnd = {
                        timelineState.refreshThumbnails()
                    },
                )
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    onEvent(BlockEvent.OnDeselect)
                }
            },
    ) {
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val playerState = timelineState.playerState

        // Play/Pause playback on scroll end/start
        val isScrollInProgress = scrollState.isScrollInProgress
        var playerWasPlayingBeforeScrollStart by remember { mutableStateOf(false) }
        LaunchedEffect(isScrollInProgress) {
            if (isScrollInProgress) {
                playerWasPlayingBeforeScrollStart = playerState.isPlaying
                playerState.pause()
            } else {
                if (playerWasPlayingBeforeScrollStart && playerState.playheadPosition < timelineState.totalDuration) {
                    playerState.play()
                }
            }
        }

        // Set playback time corresponding to scroll position
        val onePxInDp = 1f.toDp()
        LaunchedEffect(scrollState.value) {
            if (isScrollInProgress) {
                val time = zoomState.toSeconds(maxOf(0, scrollState.value) * onePxInDp).coerceAtMost(timelineState.totalDuration)
                playerState.setPlaybackTime(time)
            }
        }

        // Set scroll position corresponding to playback time
        val oneDpInPx = 1.dp.toPx()
        LaunchedEffect(playerState.playheadPosition, zoomState.zoomLevel) {
            // If user is scrolling, no need to reset the scroll position
            if (isScrollInProgress) return@LaunchedEffect
            coroutineScope.launch {
                scrollState.scrollTo(
                    (zoomState.toDp(playerState.playheadPosition).value * oneDpInPx).toInt(),
                )
            }
        }

        content(scrollState)
    }
}
