package ly.img.cesdk.core.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.core.iconpack.Cloudalertoutline
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.editorui.R

@Composable
fun UnsavedChangesAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(IconPack.Cloudalertoutline, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.cesdk_unsaved_changes_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.cesdk_unsaved_changes_dialog_text))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.cesdk_exit))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cesdk_cancel))
            }
        }
    )
}