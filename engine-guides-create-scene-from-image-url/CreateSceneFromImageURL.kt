import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*

fun createSceneFromImageURL(
    license: String,
    userId: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-createFromImage
    val imageRemoteUri = Uri.parse("https://img.ly/static/ubq_samples/sample_4.jpg")
    val scene = engine.scene.createFromImage(imageRemoteUri)
    // highlight-createFromImage

    // highlight-findByType
    // Find the automatically added graphic block in the scene that contains the image fill.
    val block = engine.block.findByType(DesignBlockType.Graphic).first()
    // highlight-findByType

    // highlight-setOpacity
    // Change its opacity.
    engine.block.setOpacity(block, value = 0.5F)
    // highlight-setOpacity

    engine.stop()
}
