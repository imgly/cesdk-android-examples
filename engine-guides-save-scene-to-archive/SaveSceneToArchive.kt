import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Engine
import java.net.HttpURLConnection
import java.net.URL
import java.nio.channels.Channels

fun saveSceneToArchive(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    val sceneUri = Uri.parse(
        "https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene",
    )
    val scene = engine.scene.load(sceneUri = sceneUri)

    // highlight-saveToArchive
    val blob = engine.scene.saveToArchive(scene = scene)
    // highlight-saveToArchive

    // highlight-create-form-data-archive
    withContext(Dispatchers.IO) {
        val connection = URL("https://example.com/upload/").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.outputStream.use { Channels.newChannel(it).write(blob) }
        connection.connect()
    }
    // highlight-create-form-data-archive

    engine.stop()
}
