package ly.img.cesdk.library.components.uploads

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Imageplusoutline
import ly.img.cesdk.editorui.R

@Composable
fun SheetHeaderWithAddImage(
    title: String,
    onClose: () -> Unit,
    onImagePicked: (Uri) -> Unit
) {
    SheetHeader(
        title = title,
        onClose = onClose,
        actionContent = {
            AddImageButton(onImagePicked = onImagePicked)
        }
    )
}

@Composable
fun AddImageButton(
    modifier: Modifier = Modifier,
    onImagePicked: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            onImagePicked(it)
        }
    }
    TextButton(
        modifier = modifier,
        onClick = {
            launcher.launch("image/*")
        },
    ) {
        Icon(IconPack.Imageplusoutline, contentDescription = null, modifier = Modifier.size(18.dp))
        Text(
            text = stringResource(R.string.cesdk_add),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}