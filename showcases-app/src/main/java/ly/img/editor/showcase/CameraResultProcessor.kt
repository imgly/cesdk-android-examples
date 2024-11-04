package ly.img.editor.showcase

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import ly.img.camera.core.CameraResult
import java.io.File

fun processCameraResult(
    context: Context,
    result: CameraResult?,
) {
    val recordings = (result as? CameraResult.Record)?.recordings ?: return
    val uris =
        recordings.flatMap { recording -> recording.videos }.map { video ->
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                File(video.uri.path!!),
            )
        }

    val shareIntent =
        Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            type = "video/*"
        }

    context.startActivity(shareIntent)
}
