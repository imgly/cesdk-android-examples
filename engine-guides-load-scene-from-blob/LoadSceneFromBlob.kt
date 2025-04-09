import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import java.io.ByteArrayOutputStream
import java.net.URL

fun loadSceneFromBlob(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-fetch-blob
    val sceneUrl = URL("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
    val sceneBlob = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        sceneUrl.openStream().use { inputStream ->
            outputStream.use(inputStream::copyTo)
        }
        outputStream.toByteArray()
    }
    // highlight-fetch-blob

    // highlight-read-blob
    val blobString = String(sceneBlob, Charsets.UTF_8)
    // highlight-read-blob

    // highlight-load
    val scene = engine.scene.load(scene = blobString)
    // highlight-load

    // highlight-set-text-dropshadow
    val text = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.setDropShadowEnabled(text, enabled = true)
    // highlight-set-text-dropshadow

    engine.stop()
}
