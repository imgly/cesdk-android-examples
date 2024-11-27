package ly.img.editor.core.ui.library.resultcontract

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.util.LibraryEvent
import java.io.File

fun prepareUriForCameraLauncher(context: Context): Uri {
    val file = File.createTempFile("imgly_", null, context.filesDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.ly.img.editor.fileprovider", file)
}

@Composable
fun rememberGalleryLauncherForActivityResult(
    addToBackgroundTrack: Boolean = false,
    onEvent: (LibraryEvent) -> Unit,
): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(contract = GetMimeTypesContent()) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val type = contentResolver.getType(uri) ?: ""
            val isVideo = type.startsWith("video")
            onEvent(
                LibraryEvent.OnAddUri(
                    assetSource = if (isVideo) AssetSourceType.VideoUploads else AssetSourceType.ImageUploads,
                    uri = uri,
                    addToBackgroundTrack = addToBackgroundTrack,
                ),
            )
        }
    }
}

object GalleryMimeType {
    val All = arrayOf("image/*", "video/*")
    val Image = arrayOf("image/*")
    val Video = arrayOf("video/*")
}
