package ly.img.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.camera.R
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.iconpack.FlashOff
import ly.img.editor.core.ui.iconpack.FlashOn
import ly.img.editor.core.ui.iconpack.FlipCamera
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
internal fun CameraControls(
    isCameraReady: Boolean,
    isFlashEnabled: Boolean,
    isFlashOn: Boolean,
    toggleFlash: () -> Unit,
    toggleCamera: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(LocalExtendedColorScheme.current.black)
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 28.dp),
    ) {
        if (isCameraReady) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                if (isFlashEnabled) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = toggleFlash,
                    ) {
                        Icon(
                            if (isFlashOn) IconPack.FlashOn else IconPack.FlashOff,
                            contentDescription = stringResource(R.string.ly_img_camera_toggle_flash),
                        )
                    }
                }

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = toggleCamera,
                ) {
                    Icon(
                        IconPack.FlipCamera,
                        contentDescription = stringResource(R.string.ly_img_camera_flip_camera),
                    )
                }
            }
        }
    }
}
