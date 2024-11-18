package ly.img.editor.core.ui.permissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.R
import ly.img.editor.core.ui.iconpack.Check
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Mic
import ly.img.editor.core.ui.iconpack.Photocameraoutline
import ly.img.editor.core.ui.iconpack.Warning
import ly.img.editor.core.ui.permissions.PermissionManager.Companion.hasCameraPermission
import ly.img.editor.core.ui.permissions.PermissionManager.Companion.hasMicPermission
import ly.img.editor.core.ui.utils.lifecycle.LifecycleEventEffect

@Composable
fun PermissionsView(
    requestOnlyCameraPermission: Boolean = false,
    onAllPermissionsGranted: () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Icon(
            IconPack.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text =
                stringResource(
                    if (requestOnlyCameraPermission) R.string.ly_img_camera_permission_text else R.string.ly_img_camera_mic_permission_text,
                ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
    Spacer(modifier = Modifier.height(56.dp))

    val context = LocalContext.current
    val permissionManager = remember { PermissionManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var isCameraPermissionGranted by remember { mutableStateOf(context.hasCameraPermission()) }
    var isMicPermissionGranted by remember { mutableStateOf(context.hasMicPermission()) }

    fun refresh() {
        isMicPermissionGranted = context.hasMicPermission()
        isCameraPermissionGranted = context.hasCameraPermission()
        if ((requestOnlyCameraPermission && isCameraPermissionGranted) || (isCameraPermissionGranted && isMicPermissionGranted)) {
            onAllPermissionsGranted()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
            // refresh() is already being handled below.
        }

    // The user could enable the permissions from settings
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        refresh()
    }

    fun requestPermission(permission: String) {
        coroutineScope.launch {
            val status = permissionManager.checkPermission(permission)
            when (status) {
                PermissionStatus.DENIED_CAN_REQUEST, PermissionStatus.DENIED_FIRST_TIME -> permissionLauncher.launch(permission)
                PermissionStatus.DENIED_DONT_ASK -> permissionManager.openAppSettings()
                else -> {
                }
            }
        }
    }

    PermissionButton(
        onClick = {
            requestPermission(permission = Manifest.permission.CAMERA)
        },
        icon = IconPack.Photocameraoutline,
        text = R.string.ly_img_camera_permission_camera,
        isPermissionGranted = isCameraPermissionGranted,
    )
    if (!requestOnlyCameraPermission) {
        Spacer(modifier = Modifier.height(16.dp))
        PermissionButton(
            onClick = {
                requestPermission(permission = Manifest.permission.RECORD_AUDIO)
            },
            icon = IconPack.Mic,
            text = R.string.ly_img_camera_permission_mic,
            isPermissionGranted = isMicPermissionGranted,
        )
    }
}

@Composable
private fun PermissionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    @StringRes text: Int,
    isPermissionGranted: Boolean,
    disabledContainerColor: Color = LocalExtendedColorScheme.current.green.colorContainer.copy(alpha = 0.5f),
    disabledContentColor: Color = LocalExtendedColorScheme.current.green.onColor.copy(alpha = 0.5f),
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isPermissionGranted,
        colors =
            ButtonDefaults.filledTonalButtonColors(
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = disabledContentColor,
            ),
    ) {
        Icon(
            if (isPermissionGranted) IconPack.Check else icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isPermissionGranted) disabledContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(text))
    }
}
