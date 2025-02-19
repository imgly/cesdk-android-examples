import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*

fun createSceneFromVideoURL() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine(id = "ly.img.engine.example")
    engine.start()
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-createFromVideo
    val videoRemoteUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")
    val scene = engine.scene.createFromVideo(videoRemoteUri)
    // highlight-createFromVideo

    // highlight-findByType
    // Find the automatically added rect block in the scene that contains the video fill.
    val block = engine.block.findByType(DesignBlockType.RECT_SHAPE).first()
    // highlight-findByType

    // highlight-setOpacity
    // Change its opacity.
    engine.block.setOpacity(block, value = 0.5F)
    // highlight-setOpacity

    engine.stop()
}
