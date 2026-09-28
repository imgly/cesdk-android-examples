package ly.img.editor.configuration.video.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.component.Timeline
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAddAudio
import ly.img.editor.core.component.rememberAddClip
import ly.img.editor.core.component.rememberLoop
import ly.img.editor.core.component.rememberPlayPause
import ly.img.editor.core.component.rememberTimecode
import ly.img.editor.core.component.rememberToggleExpanded

/**
 * The configuration of the timeline of the video editor.
 *
 * The [Timeline] component renders tracks alone, so this starter kit declares the header and the
 * two lane buttons.
 */
@Composable
fun VideoConfigurationBuilder.rememberTimeline() = Timeline.remember {
    // highlight-starter-kit-timeline-header
    headerListBuilder = {
        Timeline.HeaderListBuilder.remember {
            aligned(alignment = Alignment.Start) {
                add { Timeline.Label.rememberTimecode() }
            }
            aligned(alignment = Alignment.CenterHorizontally) {
                add { Timeline.Button.rememberPlayPause() }
            }
            aligned(alignment = Alignment.End) {
                add { Timeline.Button.rememberLoop() }
                add { Timeline.Button.rememberToggleExpanded() }
            }
        }
    }
    // highlight-starter-kit-timeline-header

    // highlight-starter-kit-timeline-lane-buttons
    addClipButton = { Timeline.Button.rememberAddClip() }
    addAudioButton = { Timeline.Button.rememberAddAudio() }
    // highlight-starter-kit-timeline-lane-buttons
}
