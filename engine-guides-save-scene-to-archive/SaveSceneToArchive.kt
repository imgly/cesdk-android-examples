import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*
import java.net.HttpURLConnection
import java.net.URL

fun saveSceneToArchive(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    val sceneUri =
        Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene",
        )
    val scene = engine.scene.load(sceneUri = sceneUri)

    // highlight-save
    val blob = engine.scene.saveToArchive(scene = scene)
    // highlight-save

    // highlight-create-form-data
    withContext(Dispatchers.IO) {
        val connection = URL("https://example.com/upload/").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.outputStream.use { it.write(blob) }
        connection.connect()
    }
    // highlight-create-form-data

    engine.stop()
}
