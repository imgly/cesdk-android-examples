import kotlinx.coroutines.*
import ly.img.engine.*
import java.io.ByteArrayOutputStream
import java.net.URL

fun loadSceneFromString() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start()
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-fetch-string
    val sceneUrl = URL("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
    val sceneBlob = withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        sceneUrl.openStream().use { inputStream ->
            outputStream.use(inputStream::copyTo)
        }
        outputStream.toByteArray()
    }
    val blobString = String(sceneBlob, Charsets.UTF_8)
    // highlight-fetch-string

    // highlight-load
    val scene = engine.scene.load(scene = blobString)
    // highlight-load

    // highlight-set-text-dropshadow
    val text = engine.block.findByType(DesignBlockType.TEXT).first()
    engine.block.setDropShadowEnabled(text, enabled = true)
    // highlight-set-text-dropshadow

    engine.stop()
}
