package ly.img.editor.base.timeline.clip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.timeline.thumbnail.ThumbnailsView
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.utils.ifTrue

@Composable
fun ClipBackgroundView(
    clip: Clip,
    timelineState: TimelineState,
    modifier: Modifier = Modifier,
    inTimeline: Boolean = false,
) {
    val backgroundColor =
        when (clip.clipType) {
            ClipType.Audio -> LocalExtendedColorScheme.current.purple.colorContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        }

    val thumbnails =
        if (clip.clipType == ClipType.Audio) {
            null
        } else {
            timelineState.getThumbnailProvider(clip.id)?.thumbnails ?: emptyList()
        }
    val isLoading = thumbnails != null && thumbnails.isEmpty()

    Box(
        modifier
            .ifTrue(isLoading) {
                shimmer()
            }
            .fillMaxSize()
            .clip(MaterialTheme.shapes.small)
            .ifTrue(!isLoading && clip.clipType != ClipType.Audio) {
                // The 0.5 alpha allows to view other adjacent clips during trimming
                background(backgroundColor.copy(alpha = 0.5f))
            }
            .ifTrue(isLoading || clip.clipType == ClipType.Audio) {
                background(backgroundColor)
            }
            .ifTrue(inTimeline) {
                if (clip.allowsSelecting) {
                    border(
                        border =
                            BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            ),
                        shape = MaterialTheme.shapes.small,
                    )
                } else {
                    alpha(0.3f).dashedBorder(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        strokeWidth = 1.dp,
                        shape = MaterialTheme.shapes.small,
                    )
                }
            },
    ) {
        if (thumbnails != null) {
            ThumbnailsView(thumbnails = thumbnails)
        }
    }
}
