package ly.img.editor.base.timeline.clip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ly.img.editor.base.timeline.clip.audio.AudioWaveformView
import ly.img.editor.base.timeline.state.TimelineZoomState

@Composable
fun ClipForegroundView(
    clip: Clip,
    isSelected: Boolean,
    zoomState: TimelineZoomState,
    clipDurationText: String,
    overlayWidth: Dp,
    overlayShape: Shape? = null,
) {
    Box(Modifier.fillMaxSize()) {
        if (clip.clipType == ClipType.Audio) {
            AudioWaveformView(
                zoomLevel = zoomState.zoomLevel,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .height(20.dp)
                        .padding(vertical = 2.dp),
            )
        }

        if (isSelected && overlayShape != null) {
            ClipOverlay(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .clip(overlayShape)
                        .width(overlayWidth)
                        .fillMaxHeight(),
            )
        }
    }

    ClipLabelView(
        modifier = Modifier.zIndex(1f),
        clip = clip,
        duration = clipDurationText,
        isSelected = isSelected,
    )
}
