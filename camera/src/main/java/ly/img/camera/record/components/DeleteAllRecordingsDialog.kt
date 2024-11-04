package ly.img.camera.record.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.editor.core.R

@Composable
internal fun DeleteAllRecordingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text =
                    stringResource(
                        ly.img.camera.R.string.ly_img_camera_delete_recordings_dialog_title,
                    ),
            )
        },
        text = {
            Text(
                text =
                    stringResource(
                        ly.img.camera.R.string.ly_img_camera_delete_recordings_dialog_text,
                    ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text(stringResource(ly.img.camera.R.string.ly_img_camera_delete_recordings_dialog_confirm_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(R.string.ly_img_editor_cancel))
            }
        },
    )
}
