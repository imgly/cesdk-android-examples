import android.net.Uri
import android.util.Log
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.math.abs

private const val TAG = "AdjustVolumeGuide"

suspend fun adjustVolume(engine: Engine): AdjustVolumeResult {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 1280F)
    engine.block.setHeight(block = page, value = 720F)
    engine.block.setDuration(block = page, duration = 10.0)

    // highlight-android-create-audio
    val voiceoverAudio = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = voiceoverAudio)
    engine.block.setDuration(block = voiceoverAudio, duration = 10.0)

    val voiceoverUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a")
    engine.block.setUri(
        block = voiceoverAudio,
        property = "audio/fileURI",
        value = voiceoverUri,
    )

    engine.block.forceLoadAVResource(block = voiceoverAudio)
    // highlight-android-create-audio

    // highlight-android-set-volume
    engine.block.setVolume(block = voiceoverAudio, volume = 0.8F)
    val foregroundVolume = engine.block.getVolume(block = voiceoverAudio)
    // highlight-android-set-volume

    // highlight-android-set-low-volume
    val backgroundMusic = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = backgroundMusic)
    engine.block.setDuration(block = backgroundMusic, duration = 10.0)

    val backgroundUri = Uri.parse("https://cdn.img.ly/assets/demo/v3/ly.img.audio/audios/dance_harder.m4a")
    engine.block.setUri(
        block = backgroundMusic,
        property = "audio/fileURI",
        value = backgroundUri,
    )

    engine.block.forceLoadAVResource(block = backgroundMusic)
    engine.block.setVolume(block = backgroundMusic, volume = 0.3F)
    val backgroundVolume = engine.block.getVolume(block = backgroundMusic)
    // highlight-android-set-low-volume

    // highlight-android-video-fill-volume
    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block = videoBlock, value = 1280F)
    engine.block.setHeight(block = videoBlock, value = 720F)

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4"),
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)
    val videoTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = videoTrack)
    engine.block.appendChild(parent = videoTrack, child = videoBlock)
    engine.block.fillParent(block = videoTrack)
    engine.block.forceLoadAVResource(block = videoFill)

    engine.block.setVolume(block = videoFill, volume = 0.5F)
    val videoFillVolume = engine.block.getVolume(block = videoFill)
    // highlight-android-video-fill-volume

    // highlight-android-mute-audio
    engine.block.setMuted(block = voiceoverAudio, muted = true)
    val muted = engine.block.isMuted(block = voiceoverAudio)
    val mutedVolume = engine.block.getVolume(block = voiceoverAudio)

    engine.block.setMuted(block = voiceoverAudio, muted = false)
    val isMutedAfterUnmute = engine.block.isMuted(block = voiceoverAudio)
    // highlight-android-mute-audio

    // highlight-android-query-volume
    val currentVolume = engine.block.getVolume(block = voiceoverAudio)
    val userMuted = engine.block.isMuted(block = voiceoverAudio)
    val forceMuted = engine.block.isForceMuted(block = voiceoverAudio)

    Log.i(TAG, "Audio volume: ${(currentVolume * 100).toInt()}%")
    Log.i(TAG, "Muted by user: $userMuted")
    Log.i(TAG, "Muted by engine: $forceMuted")
    // highlight-android-query-volume

    // highlight-android-volume-slider
    val sliderPercent = 75
    val sliderVolume = sliderPercent / 100F

    engine.block.setVolume(block = voiceoverAudio, volume = sliderVolume)
    val displayedPercent = (engine.block.getVolume(block = voiceoverAudio) * 100).toInt()
    // highlight-android-volume-slider

    // highlight-android-mute-toggle
    val currentlyMuted = engine.block.isMuted(block = voiceoverAudio)
    engine.block.setMuted(block = voiceoverAudio, muted = !currentlyMuted)

    val volumeIconState = when {
        engine.block.isForceMuted(block = voiceoverAudio) -> "force-muted"
        engine.block.isMuted(block = voiceoverAudio) -> "muted"
        else -> "volume"
    }
    // highlight-android-mute-toggle

    check(abs(foregroundVolume - 0.8F) < 0.001F)
    check(abs(backgroundVolume - 0.3F) < 0.001F)
    check(abs(videoFillVolume - 0.5F) < 0.001F)
    check(muted)
    check(abs(mutedVolume - 0.8F) < 0.001F)
    check(!isMutedAfterUnmute)
    check(displayedPercent == sliderPercent)
    check(volumeIconState == "muted")

    return AdjustVolumeResult(
        foregroundVolume = foregroundVolume,
        backgroundVolume = backgroundVolume,
        videoFillVolume = videoFillVolume,
        mutedVolume = mutedVolume,
        muted = muted,
        isMutedAfterUnmute = isMutedAfterUnmute,
        sliderVolume = sliderVolume,
        sliderPercent = displayedPercent,
        toggledMuted = engine.block.isMuted(block = voiceoverAudio),
        forceMuted = forceMuted,
    )
}
