package ly.img.editor.configuration.memories.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.img.editor.configuration.memories.iconPack.Pause
import ly.img.editor.configuration.memories.iconPack.PlayArrow
import ly.img.editor.core.iconpack.IconPack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenVideoControls(
    isPlaying: Boolean,
    progress: Float,
    currentTime: Double,
    totalDuration: Double,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun formatTime(seconds: Double): String {
        val totalSeconds = seconds.toInt().coerceAtLeast(0)
        return "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.6f),
                    ),
                ),
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Play/Pause Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 28.dp,
                            color = Color.White,
                        ),
                    ) { onPlayPauseClick() }
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isPlaying) IconPack.Pause else IconPack.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }

            // Slider
            Slider(
                value = progress,
                onValueChange = { newValue ->
                    onSeek(newValue)
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                ),
                modifier = Modifier.weight(1f),
            )

            // Current time over total duration, to the right of the slider
            Text(
                text = "${formatTime(currentTime)} / ${formatTime(totalDuration)}",
                color = Color.White,
                fontSize = 12.sp,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun FullscreenVideoControlsPreview() {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0.3f) }

    FullscreenVideoControls(
        isPlaying = isPlaying,
        progress = progress,
        currentTime = progress * 120.0,
        totalDuration = 120.0,
        onPlayPauseClick = { isPlaying = !isPlaying },
        onSeek = { progress = it },
    )
}
