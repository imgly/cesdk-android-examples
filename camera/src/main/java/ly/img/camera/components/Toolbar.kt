package ly.img.camera.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.camera.record.components.TimecodeView
import ly.img.editor.core.R
import ly.img.editor.core.iconpack.Close
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.theme.LocalExtendedColorScheme
import kotlin.time.Duration

internal val TOOLBAR_HEIGHT = 64.dp

@Composable
internal fun Toolbar(
    isRecording: Boolean,
    duration: Duration,
    maxDuration: Duration,
    recordingColor: Color,
    onCloseClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(TOOLBAR_HEIGHT),
    ) {
        IconButton(
            modifier =
                Modifier.align(Alignment.CenterStart)
                    .padding(start = 12.dp),
            onClick = onCloseClick,
        ) {
            Icon(
                IconPack.Close,
                tint = LocalExtendedColorScheme.current.white,
                contentDescription = stringResource(R.string.ly_img_editor_close),
            )
        }

        TimecodeView(
            modifier = Modifier.align(Alignment.Center),
            duration = duration,
            maxDuration = maxDuration,
            isRecording = isRecording,
            recordingColor = recordingColor,
        )
    }
}
