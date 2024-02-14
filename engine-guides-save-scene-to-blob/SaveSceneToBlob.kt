import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*
import java.net.HttpURLConnection
import java.net.URL

fun saveSceneToBlob(
    license: String,
    userId: String,
    uploadUrl: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    val sceneUri =
        Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene",
        )
    val scene = engine.scene.load(sceneUri = sceneUri)

    // highlight-save
    val savedSceneString = engine.scene.saveToString(scene = scene)
    // highlight-save

    // highlight-create-blob
    val blob = savedSceneString.toByteArray(Charsets.UTF_8)
    // highlight-create-blob

    // highlight-create-form-data
    runCatching {
        withContext(Dispatchers.IO) {
            val connection = URL(uploadUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.use { it.write(blob) }
            connection.connect()
        }
    }
    // highlight-create-form-data

    engine.stop()
}
