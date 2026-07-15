import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

suspend fun createSceneFromImageBlob(engine: Engine) {
    // highlight-android-fetch-image-bytes
    val blobUrl = URL("https://img.ly/static/ubq_samples/sample_4.jpg")
    val imageBytes = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        blobUrl.openStream().use { inputStream ->
            outputStream.use(inputStream::copyTo)
        }
        outputStream.toByteArray()
    }
    // highlight-android-fetch-image-bytes

    check(imageBytes.isNotEmpty())

    // highlight-android-write-image-file
    val blobFile = withContext(Dispatchers.IO) {
        File.createTempFile("cesdk-image-", ".jpg").apply {
            outputStream().use { it.write(imageBytes) }
        }
    }
    val blobUri = Uri.fromFile(blobFile)
    // highlight-android-write-image-file

    check(blobFile.length() > 0L)

    // highlight-android-create-from-file
    engine.scene.createFromImage(blobUri)
    // highlight-android-create-from-file

    // highlight-android-work-with-blob-scene
    val page = engine.block.findByType(DesignBlockType.Page).first()
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    // highlight-android-work-with-blob-scene

    check(pageWidth > 0F)
    check(pageHeight > 0F)

    // highlight-android-inspect-blob-page-fill
    val pageFill = engine.block.getFill(page)
    val imageFillType = engine.block.getType(pageFill)
    // highlight-android-inspect-blob-page-fill

    check(imageFillType == FillType.Image.key)
}
