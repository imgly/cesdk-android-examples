import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.UUID

fun createSceneFromImageBlob(
    license: String,
    userId: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

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

    // highlight-findByType
    // Find the automatically added graphic block in the scene that contains the image fill.
    val block = engine.block.findByType(DesignBlockType.Graphic).first()
    // highlight-findByType

    // highlight-set-opacity
    // Change its opacity.
    engine.block.setOpacity(block, value = 0.5F)
    // highlight-set-opacity

    engine.stop()
}
