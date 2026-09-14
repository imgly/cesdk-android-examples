import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun createSceneFromImageURL(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-createFromImage
    val imageRemoteUri = Uri.parse("https://img.ly/static/ubq_samples/sample_4.jpg")
    val scene = engine.scene.createFromImage(imageRemoteUri)
    // highlight-createFromImage

    // highlight-findByType
    val page = engine.block.findByType(DesignBlockType.Page).first()
    // highlight-findByType

    // highlight-check-fill
    // Get the fill from the page and verify it's an image fill
    val pageFill = engine.block.getFill(page)
    val imageFillType = engine.block.getType(pageFill)
    // highlight-check-fill

    engine.stop()
}
