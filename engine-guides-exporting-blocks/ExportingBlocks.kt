import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.MimeType
import java.io.File
import java.util.UUID

fun exportingBlocks(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)
    engine.scene.create()

    val scene = engine.scene.createFromImage(
        imageUri = Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg"),
    )

    // Export scene as PNG image.
    val mimeType = MimeType.PNG
    // Optionally, the maximum supported export size can be checked before exporting.
    val maxExportSizeInPixels = engine.editor.getMaxExportSize()
    // Optionally, the compression level and the target size can be specified.
    val options = ExportOptions(pngCompressionLevel = 9, targetWidth = null, targetHeight = null)
    val blob = engine.block.export(scene, mimeType = mimeType, options = options)

    // Save the blob to file
    withContext(Dispatchers.IO) {
        File.createTempFile(UUID.randomUUID().toString(), ".png").apply {
            outputStream().channel.write(blob)
        }
    }

    engine.stop()
}
