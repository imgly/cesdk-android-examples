package ly.img.cesdk.core.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.WifiCancel

@Composable
fun InternetErrorDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        icon = {
            Icon(IconPack.WifiCancel, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.cesdk_error_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.cesdk_error_internet_text))
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cesdk_dismiss))
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}