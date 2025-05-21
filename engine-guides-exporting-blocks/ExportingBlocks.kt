import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.MimeType
import ly.img.engine.addDefaultAssetSources
import java.io.File
import java.util.UUID

fun exportingBlocks(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)
    engine.scene.create()
    engine.editor.setSettingString(
        "basePath",
        value = "https://cdn.img.ly/packages/imgly/cesdk-engine/1.23.0/assets",
    )
    engine.addDefaultAssetSources()

    val sceneUri = Uri.parse(
        "https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene",
    )
    val scene = engine.scene.load(sceneUri = sceneUri)

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
