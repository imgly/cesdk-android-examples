import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

fun editVideo(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-setupScene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    // highlight-setupScene
    // highlight-setPageDuration
    engine.block.setDuration(page, duration = 20.0)
    // highlight-setPageDuration
    // highlight-assignVideoFill
    val video1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(video1, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setString(
        block = videoFill,
        property = "fill/video/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
    )
    engine.block.setFill(video1, fill = videoFill)

    val video2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(video1, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill2 = engine.block.createFill(FillType.Video)
    engine.block.setString(
        block = videoFill,
        property = "fill/video/fileURI",
        value = "https://cdn.img.ly/assets/demo/v2/ly.img.video/videos/pexels-kampus-production-8154913.mp4",
    )
    engine.block.setFill(video2, fill = videoFill2)
    // highlight-assignVideoFill
    // highlight-addToTrack
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.appendChild(parent = track, child = video1)
    engine.block.appendChild(parent = track, child = video2)
    engine.block.fillParent(track)
    // highlight-addToTrack
    // highlight-setDuration
    engine.block.setDuration(video1, duration = 15.0)
    // highlight-setDuration
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
    val audio = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = audio)
    engine.block.setString(
        block = audio,
        property = "audio/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a",
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
    // Export page as mp4 video.
    val blob = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(page),
        mimeType = MimeType.MP4,
        progressCallback = {
            println(
                "Rendered ${it.renderedFrames} frames and encoded ${it.encodedFrames} frames out of ${it.totalFrames} frames",
            )
        },
    )
    // highlight-exportVideo

    engine.stop()
}
