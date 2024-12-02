package ly.img.camera.record.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.utils.formatForPlayer
import kotlin.time.Duration

@Composable
internal fun TimecodeView(
    modifier: Modifier,
    duration: Duration,
    maxDuration: Duration,
    isRecording: Boolean,
    recordingColor: Color,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(40))
                .background(LocalExtendedColorScheme.current.black.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val sizeAnimation by animateDpAsState(
            targetValue = if (isRecording) 20.dp else 0.dp,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        )

        Canvas(
            modifier =
                Modifier
                    .width(sizeAnimation)
                    .height(14.dp),
        ) {
            drawCircle(
                color = recordingColor,
                radius = size.width * 7 / 20,
                center = this.center - Offset(x = 3.dp.toPx(), y = 0f),
            )
        }

        val isMaxDurationLimited =
            remember(maxDuration) {
                maxDuration < Duration.INFINITE
            }

        // Setting `includeFontPadding` to false ensures that the recording indicator and the text are correctly aligned.
        val textStyle =
            MaterialTheme.typography.labelLarge.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            )

        Text(
            text = duration.formatForPlayer(),
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (isMaxDurationLimited) {
            Text(
                text = " / ${maxDuration.formatForPlayer()}",
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alpha(0.75f),
            )
        }
    }
}
