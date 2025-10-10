import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Engine

fun saveSceneToString(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    val sceneUri = Uri.parse(
        "https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene",
    )
    val scene = engine.scene.load(sceneUri = sceneUri)

    // highlight-saveToString
    val savedSceneString = engine.scene.saveToString(scene = scene)
    // highlight-saveToString

    // highlight-result-string
    println(savedSceneString)
    // highlight-result-string

    engine.stop()
}
