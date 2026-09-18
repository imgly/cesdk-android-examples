package ly.img.editor.configuration.memories.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.img.editor.configuration.memories.iconPack.Fullscreen
import ly.img.editor.configuration.memories.iconPack.Pause
import ly.img.editor.configuration.memories.iconPack.PlayArrow
import ly.img.editor.configuration.memories.iconPack.RepeatOff
import ly.img.editor.configuration.memories.iconPack.RepeatOn
import ly.img.editor.configuration.memories.iconPack.SkipPrevious
import ly.img.editor.configuration.memories.iconPack.VolumeUp
import ly.img.editor.core.iconpack.IconPack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoControlPanel(
    isPlaying: Boolean,
    isLooping: Boolean,
    onPlayPauseClick: () -> Unit,
    onRestartClick: () -> Unit,
    onLoopClick: () -> Unit,
    onVolumeClick: () -> Unit,
    onFullscreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
        ) {
            // Left: Volume button (where the time used to be)
            Icon(
                imageVector = IconPack.VolumeUp,
                contentDescription = "Volume",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 20.dp,
                        ),
                    ) { onVolumeClick() }
                    .padding(vertical = 4.dp),
            )

            // Center: Play controls
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Restart button
                    Icon(
                        imageVector = IconPack.SkipPrevious,
                        contentDescription = "Restart",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 28.dp,
                                ),
                            ) { onRestartClick() }
                            .padding(vertical = 8.dp),
                    )

                    // Play/Pause button
                    Icon(
                        imageVector = if (isPlaying) {
                            IconPack.Pause
                        } else {
                            IconPack.PlayArrow
                        },
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 28.dp,
                                ),
                            ) { onPlayPauseClick() }
                            .padding(vertical = 8.dp),
                    )

                    // Loop button
                    Icon(
                        imageVector = if (isLooping) IconPack.RepeatOn else IconPack.RepeatOff,
                        contentDescription = if (isLooping) "Disable Loop" else "Enable Loop",
                        tint = if (isLooping) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 28.dp,
                                ),
                            ) { onLoopClick() }
                            .padding(vertical = 8.dp),
                    )
                }
            }

            // Right: Fullscreen button (moved from the top navigation bar)
            Icon(
                imageVector = IconPack.Fullscreen,
                contentDescription = "Fullscreen",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 20.dp,
                        ),
                    ) { onFullscreenClick() }
                    .padding(vertical = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoControlPanelPreview() {
    var isPlaying by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(true) }

    MaterialTheme {
        VideoControlPanel(
            isPlaying = isPlaying,
            isLooping = isLooping,
            onPlayPauseClick = { isPlaying = !isPlaying },
            onRestartClick = { isPlaying = false },
            onLoopClick = { isLooping = !isLooping },
            onVolumeClick = { /* Volume control */ },
            onFullscreenClick = { /* Enter fullscreen */ },
        )
    }
}
