import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

private const val SAMPLE_VIDEO_URI = "https://img.ly/static/ubq_video_samples/bbb.mp4"

suspend fun createSceneFromVideoURL(engine: Engine): DesignBlock = withContext(Dispatchers.Main) {
    // highlight-android-create-from-video
    val videoRemoteUri = Uri.parse(SAMPLE_VIDEO_URI)
    val scene = engine.scene.createFromVideo(videoUri = videoRemoteUri)
    // highlight-android-create-from-video

    // highlight-android-work-with-video-block
    // Find the automatically added graphic block in the scene that contains the video fill.
    val block = engine.block.findByType(type = DesignBlockType.Graphic).first()
    engine.block.setOpacity(block = block, value = 0.5F)
    // highlight-android-work-with-video-block

    val page = engine.scene.getPages().first()

    // highlight-android-control-playback
    val duration = engine.block.getDuration(block = page)

    if (engine.block.supportsPlaybackTime(block = page) && duration >= 1.0) {
        engine.block.setPlaybackTime(block = page, time = 1.0)
        engine.block.setPlaying(block = page, enabled = true)
        engine.block.setPlaying(block = page, enabled = false)
    }
    // highlight-android-control-playback

    scene
}
