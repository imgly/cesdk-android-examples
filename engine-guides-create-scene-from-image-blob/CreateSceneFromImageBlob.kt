import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*
import java.io.*
import java.net.URL
import java.util.*

fun createSceneFromImageBlob() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine(id = "ly.img.engine.example")
    engine.start()
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-blob
    val blobUrl = URL("https://img.ly/static/ubq_samples/sample_4.jpg")
    val blob = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        blobUrl.openStream().use { inputStream ->
            outputStream.use(inputStream::copyTo)
        }
        outputStream.toByteArray()
    }
    // highlight-blob

    // highlight-objectURL
    val blobFile = withContext(Dispatchers.IO) {
        File.createTempFile(UUID.randomUUID().toString(), ".tmp").apply {
            outputStream().use { it.write(blob) }
        }
    }
    val blobUri = Uri.fromFile(blobFile)
    // highlight-objectURL

    // highlight-initialImageURL
    val scene = engine.scene.createFromImage(blobUri)
    // highlight-initialImageURL

    // highlight-find-image
    // Find the automatically added image element in the scene.
    val image = engine.block.findByType(DesignBlockType.IMAGE).first()
    // highlight-find-image

    // highlight-set-opacity
    // Change its opacity.
    engine.block.setOpacity(image, value = 0.5F)
    // highlight-set-opacity

    engine.stop()
}
