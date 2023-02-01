import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*

fun createSceneFromImageURL() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.also { it.start() }
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-initialImageURL
    val imageRemoteUri = Uri.parse("https://img.ly/static/ubq_samples/sample_4.jpg")
    val scene = engine.scene.createFromImage(imageRemoteUri)
    // highlight-initialImageURL

    // highlight-find-image
    // Find the automatically added image element in the scene.
    val image = engine.block.findByType(DesignBlockType.IMAGE).first()
    // highlight-find-image

    // highlight-set-opacity
    // Change its opacity.
    engine.block.setOpacity(image, value = 0.5F)
    // highlight-set-opacity

    engine.stop()
}
