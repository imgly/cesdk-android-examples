package ly.img.editor.base.timeline.clip.trim

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.clip.animatedDashedBorder
import ly.img.editor.base.timeline.state.TimelineZoomState
import ly.img.editor.core.ui.utils.toDp
import kotlin.math.roundToInt

@Composable
fun ClipTrimOutlineView(
    clip: Clip,
    zoomState: TimelineZoomState,
    clipDragType: ClipDragType?,
    dashColor: Color,
    realtimeWidth: Float,
    height: Dp,
) {
    if (clipDragType != null && clipDragType != ClipDragType.Move && clip.footageDuration != null) {
        Box(
            modifier =
                Modifier
                    .wrapContentSize(unbounded = true, align = Alignment.CenterStart)
                    .width(zoomState.toPx(clip.footageDuration).toDp())
                    .height(height)
                    .offset {
                        val extraOffset =
                            if (clipDragType == ClipDragType.Trailing) {
                                0f
                            } else {
                                zoomState.toPx(clip.duration) - realtimeWidth
                            }
                        IntOffset(
                            x = (-zoomState.toPx(clip.trimOffset) - extraOffset).roundToInt(),
                            y = 0,
                        )
                    }
                    .animatedDashedBorder(
                        color = dashColor,
                        strokeWidth = 1.dp,
                        shape = MaterialTheme.shapes.small,
                    ),
        )
    }
}
