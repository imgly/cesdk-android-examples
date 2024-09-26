package ly.img.editor.core.ui.library.components

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.foundation.clickable
import ly.img.editor.core.ui.iconpack.Addcamerabackground
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.library.resultcontract.prepareUriForCameraLauncher
import ly.img.editor.core.ui.library.resultcontract.rememberCameraLauncherForActivityResult

@Composable
fun ClipMenuItem(
    @StringRes textResourceId: Int,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = stringResource(id = textResourceId),
                modifier =
                    Modifier
                        .widthIn(min = 96.dp)
                        .offset(x = (-4).dp),
            )
        },
        trailingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.offset(x = 8.dp),
            )
        },
    )
}

@Composable
fun CameraClipMenuItem(
    @StringRes textResourceId: Int,
    captureVideo: Boolean,
    onCapture: (Uri) -> Unit,
) {
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val cameraLauncher =
        rememberCameraLauncherForActivityResult(captureVideo = captureVideo, onCapture = {
            onCapture(checkNotNull(uri))
        })
    val context = LocalContext.current
    ClipMenuItem(
        textResourceId = textResourceId,
        icon = IconPack.Addcamerabackground,
    ) {
        uri = prepareUriForCameraLauncher(context)
        cameraLauncher.launch(uri)
    }
}
