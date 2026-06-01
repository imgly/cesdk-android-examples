import android.net.Uri
import android.util.Log
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

private const val TAG = "ControlAudioVideo"
private const val SAMPLE_VIDEO_URI = "https://img.ly/static/ubq_video_samples/bbb.mp4"

suspend fun controlAudioVideo(engine: Engine) {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1920F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)

    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(videoBlock, value = 1920F)
    engine.block.setHeight(videoBlock, value = 1080F)

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse(SAMPLE_VIDEO_URI),
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)

    engine.block.appendChild(parent = track, child = videoBlock)
    engine.block.setDuration(videoBlock, duration = 10.0)

    // highlight-android-force-load
    engine.block.forceLoadAVResource(videoFill)
    // highlight-android-force-load

    // highlight-android-get-metadata
    val videoWidth = engine.block.getVideoWidth(videoFill)
    val videoHeight = engine.block.getVideoHeight(videoFill)
    val totalDuration = engine.block.getAVResourceTotalDuration(videoFill)
    Log.i(TAG, "Video dimensions: ${videoWidth}x$videoHeight")
    Log.i(TAG, "Total duration: ${totalDuration}s")
    // highlight-android-get-metadata

    // highlight-android-playback-control
    if (engine.block.supportsPlaybackTime(page)) {
        engine.block.setPlaying(block = page, enabled = true)
        Log.i(TAG, "Is playing: ${engine.block.isPlaying(page)}")
        engine.block.setPlaying(block = page, enabled = false)
        Log.i(TAG, "Is playing after pause: ${engine.block.isPlaying(page)}")
    }
    // highlight-android-playback-control

    // highlight-android-seeking
    if (engine.block.supportsPlaybackTime(page)) {
        engine.block.setPlaybackTime(block = page, time = 1.0)
        Log.i(TAG, "Playback time: ${engine.block.getPlaybackTime(page)}s")
    }
    // highlight-android-seeking

    // highlight-android-visibility
    Log.i(
        TAG,
        "Visible at current time: ${engine.block.isVisibleAtCurrentPlaybackTime(videoBlock)}",
    )
    // highlight-android-visibility

    // highlight-android-solo-playback
    if (engine.block.supportsPlaybackTime(videoFill)) {
        engine.block.setSoloPlaybackEnabled(block = videoFill, enabled = true)
        Log.i(TAG, "Solo enabled: ${engine.block.isSoloPlaybackEnabled(videoFill)}")
        engine.block.setSoloPlaybackEnabled(block = videoFill, enabled = false)
    }
    // highlight-android-solo-playback
}
