package ly.img.cesdk.library.components.uploads

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.cesdk.library.components.assets.AssetsGrid
import ly.img.cesdk.library.components.assets.ImageSource
import ly.img.engine.Asset

@Composable
fun UploadsLibrary(
    title: String,
    onClose: () -> Unit,
    onImagePicked: (ImageSource, Asset) -> Unit,
    onImageUploaded: (String) -> Unit
) {

    val onImagePickedFromGallery = remember {
        { uri: Uri ->
            onClose()
            onImageUploaded(uri.toString())
        }
    }

    Column {
        SheetHeaderWithAddImage(
            title = title,
            onClose = onClose,
            onImagePicked = {
                onClose()
                onImageUploaded(it.toString())
            }
        )
        AssetsGrid(
            assetSource = ImageSource.Uploads,
            onClick = {
                onClose()
                onImagePicked(ImageSource.Uploads, it)
            },
            onImagePicked = onImagePickedFromGallery
        )
    }
}