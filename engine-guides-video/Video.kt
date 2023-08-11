import kotlinx.coroutines.*
import ly.img.engine.*

fun editVideo() = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start()
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-setupScene
    val scene = engine.scene.createForVideo()
    val stack = engine.block.findByType(DesignBlockType.STACK).first()

    val page1 = engine.block.create(DesignBlockType.PAGE)
    val page2 = engine.block.create(DesignBlockType.PAGE)
    engine.block.appendChild(parent = stack, child = page1)
    engine.block.appendChild(parent = stack, child = page2)

    engine.block.setWidth(page1, value = 1280F)
    engine.block.setHeight(page1, value = 720F)
    engine.block.setWidth(page2, value = 1280F)
    engine.block.setHeight(page2, value = 720F)
    // highlight-setupScene
    // highlight-setPageDuration
    // Show the first page for 4 seconds and the second page for 20 seconds.
    engine.block.setDuration(page1, duration = 4.0)
    engine.block.setDuration(page2, duration = 20.0)
    // highlight-setPageDuration
    // highlight-assignVideoFill
    val rectShape = engine.block.create(DesignBlockType.RECT_SHAPE)
    engine.block.destroy(engine.block.getFill(rectShape))
    val videoFill = engine.block.createFill("video")
    engine.block.setFill(rectShape, fill = videoFill)

    engine.block.setString(
        block = videoFill,
        property = "fill/video/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4"
    )

    engine.block.appendChild(parent = page2, child = rectShape)
    engine.block.setPositionX(rectShape, value = 0F)
    engine.block.setPositionY(rectShape, value = 0F)
    engine.block.setWidth(rectShape, value = engine.block.getWidth (page2))
    engine.block.setHeight(rectShape, value = engine.block.getHeight (page2))
    // highlight-assignVideoFill
    // highlight-trim
    // Make sure that the video is loaded before calling the trim APIs.
    engine.block.forceLoadAVResource(videoFill)
    engine.block.setTrimOffset(videoFill, offset = 1.0)
    engine.block.setTrimLength(videoFill, length = 10.0)
    // highlight-trim
    // highlight-looping
    engine.block.setLooping(videoFill, looping = true)

    // highlight-mute-audio
    engine.block.setMuted(videoFill, muted = true)

    // highlight-audio
    val audio = engine.block.create(DesignBlockType.AUDIO)
    engine.block.appendChild(parent = scene, child = audio)
    engine.block.setString(
        block = audio,
        property = "audio/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a"
    )
    // highlight-audio
    // highlight-audio-volume
    // Set the volume level to 70%.
    engine.block.setVolume(audio, volume = 0.7F)
    // highlight-audio-volume
    // highlight-timeOffset
    // Start the audio after two seconds of playback.
    engine.block.setTimeOffset(audio, offset = 2.0)
    // highlight-timeOffset
    // highlight-audioDuration
    // Give the Audio block a duration of 7 seconds.
    engine.block.setDuration(audio, duration = 7.0)
    // highlight-audioDuration
    // highlight-exportVideo
    // Export scene as mp4 video.
    val blob = engine.block.exportVideo(
        block = scene,
        timeOffset = 0F,
        duration = engine.block.getTotalSceneDuration(scene = scene).toFloat(),
        mimeType = MimeType.MP4,
        progressCallback = {
            println("Rendered ${it.renderedFrames} frames and encoded ${it.encodedFrames} frames out of ${it.totalFrames} frames")
        }
    )
    // highlight-exportVideo

    engine.stop()
}
