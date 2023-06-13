import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*
import java.io.File
import java.util.*

fun exportingBlocks() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine(id = "ly.img.engine.example")
    engine.start()
    engine.bindOffscreen(width = 100, height = 100)

    val sceneUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
    val scene = engine.scene.load(sceneUri = sceneUri)

    // Export scene as PNG image.
    val mimeType = MimeType.PNG
    val options = ExportOptions(pngCompressionLevel = 9)
    val blob = engine.block.export(scene, mimeType = mimeType, options = options)

    // Save the blob to file
    withContext(Dispatchers.IO) {
        File.createTempFile(UUID.randomUUID().toString(), ".png").apply {
            outputStream().use { it.write(blob) }
        }
    }

    engine.stop()
}
