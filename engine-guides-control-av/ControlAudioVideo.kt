import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

suspend fun controlAudioVideo(engine: Engine) = coroutineScope {
    // Setup a minimal video scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280.0f)
    engine.block.setHeight(page, value = 720.0f)

    // Create a video block and track
    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setString(
        block = videoFill,
        property = "fill/video/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
    )
    engine.block.setFill(videoBlock, fill = videoFill)
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.appendChild(parent = track, child = videoBlock)
    engine.block.fillParent(track)

    // Create an audio block
    val audio = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = audio)
    engine.block.setString(
        block = audio,
        property = "audio/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a",
    )

    // Time Offset and Duration
    engine.block.supportsTimeOffset(audio)
    engine.block.setTimeOffset(audio, offset = 2.0)
    engine.block.getTimeOffset(audio) // Returns 2

    engine.block.supportsDuration(page)
    engine.block.setDuration(page, duration = 10.0)
    engine.block.getDuration(page) // Returns 10

    // Duration of the page can be that of a block
    engine.block.supportsPageDurationSource(page, videoBlock)
    engine.block.setPageDurationSource(page, videoBlock)
    engine.block.isPageDurationSource(videoBlock)
    engine.block.getDuration(page) // Returns duration plus offset of the block

    // Duration of the page can be the maximum end time of all page child blocks
    engine.block.removePageDurationSource(page)
    engine.block.getDuration(page) // Returns the maximum end time of all page child blocks

    // Trim
    engine.block.supportsTrim(videoFill)
    engine.block.setTrimOffset(videoFill, offset = 1.0)
    engine.block.getTrimOffset(videoFill) // Returns 1
    engine.block.setTrimLength(videoFill, length = 5.0)
    engine.block.getTrimLength(videoFill) // Returns 5

    // Playback Control
    engine.block.setPlaying(page, enabled = true)
    engine.block.isPlaying(page)

    engine.block.setSoloPlaybackEnabled(videoFill, enabled = true)
    engine.block.isSoloPlaybackEnabled(videoFill)

    engine.block.supportsPlaybackTime(page)
    engine.block.setPlaybackTime(page, time = 1.0)
    engine.block.getPlaybackTime(page)
    engine.block.isVisibleAtCurrentPlaybackTime(videoBlock)

    engine.block.supportsPlaybackControl(videoFill)
    engine.block.setLooping(videoFill, looping = true)
    engine.block.isLooping(videoFill)
    engine.block.setMuted(videoFill, muted = true)
    engine.block.isMuted(videoFill)
    engine.block.setVolume(videoFill, volume = 0.5F) // 50% volume
    engine.block.getVolume(videoFill)

    // Playback Speed
    engine.block.setPlaybackSpeed(videoFill, speed = 0.5f) // Half speed
    val currentSpeed = engine.block.getPlaybackSpeed(videoFill) // 0.5
    engine.block.setPlaybackSpeed(videoFill, speed = 2.0f) // Double speed
    engine.block.setPlaybackSpeed(videoFill, speed = 1.0f) // Normal speed

    // Resource Control
    engine.block.forceLoadAVResource(videoFill)
    // Unstable engine api
    engine.block.isAVResourceLoaded(videoFill)
    engine.block.getAVResourceTotalDuration(videoFill)
    val videoWidth = engine.block.getVideoWidth(videoFill)
    val videoHeight = engine.block.getVideoHeight(videoFill)

    // Thumbnail Previews
    launch {
        engine.block.generateVideoThumbnailSequence(
            block = videoFill,
            thumbnailHeight = 128,
            timeBegin = 0.5,
            timeEnd = 9.5,
            numberOfFrames = 10,
        ).onEach {
            println("frameIndex = ${it.frameIndex}, width = ${it.width}, height = ${it.height}")
        }.collect()
    }

    launch {
        engine.block.generateAudioThumbnailSequence(
            block = audio,
            samplesPerChunk = 20,
            timeBegin = 0.5,
            timeEnd = 9.5,
            numberOfSamples = 10 * 20,
            numberOfChannels = 2,
        ).onEach {
            println("chunkIndex = ${it.chunkIndex}, samples:size = ${it.samples.size}")
            // drawWavePattern(it.samples)
        }.collect()
    }
}
