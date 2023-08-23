import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*

fun loadSceneFromRemote() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start()
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-url
    val sceneUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
    // highlight-url

    // highlight-load
    val scene = engine.scene.load(sceneUri = sceneUri)
    // highlight-load

    // highlight-set-text-dropshadow
    val text = engine.block.findByType(DesignBlockType.TEXT).first()
    engine.block.setDropShadowEnabled(text, enabled = true)
    // highlight-set-text-dropshadow

    engine.stop()
}
