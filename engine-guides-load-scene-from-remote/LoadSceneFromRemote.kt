import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun loadSceneFromRemote(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-url
    val sceneUri = Uri.parse(
        "https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene",
    )
    // highlight-url

    // highlight-load-remote
    val scene = engine.scene.load(sceneUri = sceneUri)
    // highlight-load-remote

    // highlight-modify-text-remote
    val text = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.setDropShadowEnabled(text, enabled = true)
    // highlight-modify-text-remote

    engine.stop()
}
