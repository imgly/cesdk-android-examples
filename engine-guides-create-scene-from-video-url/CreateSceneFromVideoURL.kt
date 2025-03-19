import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun createSceneFromVideoURL(
    license: String,
    userId: String,
) = CoroutineScope(
    Dispatchers.Main,
).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-createFromVideo
    val videoRemoteUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")
    val scene = engine.scene.createFromVideo(videoRemoteUri)
    // highlight-createFromVideo

    // highlight-findByType
    // Find the automatically added graphic block in the scene that contains the video fill.
    val block = engine.block.findByType(DesignBlockType.Graphic).first()
    // highlight-findByType

    // highlight-setOpacity
    // Change its opacity.
    engine.block.setOpacity(block, value = 0.5F)
    // highlight-setOpacity

    engine.stop()
}
