package ly.img.editor.base.timeline.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.core.ui.iconpack.Expandmore
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Pause
import ly.img.editor.core.ui.iconpack.Play
import ly.img.editor.core.ui.iconpack.Repeat
import ly.img.editor.core.ui.iconpack.RepeatOff
import ly.img.editor.core.ui.iconpack.Timeline

@Composable
fun PlayerHeader(
    timelineState: TimelineState,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(TimelineConfiguration.headerHeight)
            .alpha(0.95f),
    ) {
        Row(
            Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 20.dp),
        ) {
            val textStyle = MaterialTheme.typography.labelMedium
            Text(
                text = "${timelineState.playerState.formattedPlayheadPosition} / ",
                style = textStyle,
            )
            Text(
                text = timelineState.formattedTotalDuration,
                style = textStyle,
                modifier = Modifier.alpha(0.75f),
            )
        }

        Row(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    // Offset is needed to make the play button appear in the center
                    .offset(x = 24.dp),
        ) {
            IconButton(
                onClick = {
                    timelineState.playerState.togglePlayback()
                },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            ) {
                val isPlaying = timelineState.playerState.isPlaying
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = if (isPlaying) IconPack.Pause else IconPack.Play,
                    contentDescription =
                        if (isPlaying) {
                            stringResource(R.string.ly_img_editor_pause)
                        } else {
                            stringResource(R.string.ly_img_editor_play)
                        },
                )
            }
            val isLooping = timelineState.playerState.isLooping
            IconToggleButton(
                checked = isLooping,
                onCheckedChange = {
                    timelineState.playerState.toggleLooping()
                },
                colors =
                    IconButtonDefaults.iconToggleButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            ) {
                Icon(
                    if (isLooping) IconPack.Repeat else IconPack.RepeatOff,
                    contentDescription = stringResource(R.string.ly_img_editor_loop),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (expanded) {
            IconButton(
                onClick = {
                    onToggleExpand()
                },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 4.dp),
            ) {
                Icon(IconPack.Expandmore, contentDescription = stringResource(ly.img.editor.core.R.string.ly_img_editor_close))
            }
        } else {
            TextButton(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 4.dp),
                onClick = {
                    onToggleExpand()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            ) {
                Icon(IconPack.Timeline, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = stringResource(R.string.ly_img_editor_timeline),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
