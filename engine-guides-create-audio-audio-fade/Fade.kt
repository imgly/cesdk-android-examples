import android.net.Uri
import android.util.Log
import ly.img.engine.AnimationEasingType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.math.abs

private const val TAG = "FadeGuide"

suspend fun fadeAudio(engine: Engine): FadeResult {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 1280F)
    engine.block.setHeight(block = page, value = 720F)
    engine.block.setDuration(block = page, duration = 15.0)

    // highlight-android-create-audio
    val narration = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = narration)
    engine.block.setDuration(block = narration, duration = 10.0)

    engine.block.setUri(
        block = narration,
        property = "audio/fileURI",
        value = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a"),
    )

    engine.block.forceLoadAVResource(block = narration)
    engine.block.setVolume(block = narration, volume = 0.8F)
    // highlight-android-create-audio

    // highlight-android-fade-in
    engine.block.setAudioFadeIn(block = narration, duration = 2.0)
    // highlight-android-fade-in

    // highlight-android-fade-out
    engine.block.setAudioFadeOut(block = narration, duration = 3.0)
    // highlight-android-fade-out

    // highlight-android-fade-easing
    val backgroundMusic = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = backgroundMusic)
    engine.block.setDuration(block = backgroundMusic, duration = 15.0)

    engine.block.setUri(
        block = backgroundMusic,
        property = "audio/fileURI",
        value = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a"),
    )

    engine.block.forceLoadAVResource(block = backgroundMusic)
    engine.block.setVolume(block = backgroundMusic, volume = 0.3F)

    engine.block.setAudioFadeIn(
        block = backgroundMusic,
        duration = 4.0,
        easing = AnimationEasingType.EASE_IN_OUT,
    )
    engine.block.setAudioFadeOut(
        block = backgroundMusic,
        duration = 4.0,
        easing = AnimationEasingType.EASE_OUT,
    )
    // highlight-android-fade-easing

    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block = videoBlock, value = 1280F)
    engine.block.setHeight(block = videoBlock, value = 720F)

    val videoTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = videoTrack)
    engine.block.appendChild(parent = videoTrack, child = videoBlock)
    engine.block.fillParent(block = videoTrack)

    // highlight-android-video-fill-fade
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4"),
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)
    engine.block.forceLoadAVResource(block = videoFill)

    // The audio of a video clip lives on its video fill, so the fade goes on the fill
    // and not on the graphic block that owns it.
    engine.block.setAudioFadeIn(block = videoFill, duration = 1.0)
    engine.block.setAudioFadeOut(
        block = videoFill,
        duration = 1.5,
        easing = AnimationEasingType.EASE_OUT,
    )
    // highlight-android-video-fill-fade

    // highlight-android-read-fade
    val fadeInDuration = engine.block.getDouble(block = narration, property = "playback/fadeIn/duration")
    val fadeInEasing = engine.block.getEnum(block = narration, property = "playback/fadeIn/easing")
    val fadeOutDuration = engine.block.getDouble(block = narration, property = "playback/fadeOut/duration")
    val fadeOutEasing = engine.block.getEnum(block = narration, property = "playback/fadeOut/easing")

    Log.i(TAG, "Fade in: $fadeInDuration s ($fadeInEasing)")
    Log.i(TAG, "Fade out: $fadeOutDuration s ($fadeOutEasing)")
    // highlight-android-read-fade

    // highlight-android-remove-fade
    engine.block.setAudioFadeIn(block = narration, duration = 0.0)
    val removedFadeInDuration = engine.block.getDouble(block = narration, property = "playback/fadeIn/duration")
    // highlight-android-remove-fade

    val musicFadeInDuration = engine.block.getDouble(block = backgroundMusic, property = "playback/fadeIn/duration")
    val musicFadeInEasing = engine.block.getEnum(block = backgroundMusic, property = "playback/fadeIn/easing")
    val videoFillFadeInDuration = engine.block.getDouble(block = videoFill, property = "playback/fadeIn/duration")
    val videoFillFadeOutEasing = engine.block.getEnum(block = videoFill, property = "playback/fadeOut/easing")

    check(abs(fadeInDuration - 2.0) < 0.001)
    check(fadeInEasing == AnimationEasingType.LINEAR.key)
    check(abs(fadeOutDuration - 3.0) < 0.001)
    check(fadeOutEasing == AnimationEasingType.LINEAR.key)
    check(abs(musicFadeInDuration - 4.0) < 0.001)
    check(musicFadeInEasing == AnimationEasingType.EASE_IN_OUT.key)
    check(abs(videoFillFadeInDuration - 1.0) < 0.001)
    check(videoFillFadeOutEasing == AnimationEasingType.EASE_OUT.key)
    check(removedFadeInDuration == 0.0)

    return FadeResult(
        fadeInDuration = fadeInDuration,
        fadeInEasing = fadeInEasing,
        fadeOutDuration = fadeOutDuration,
        fadeOutEasing = fadeOutEasing,
        musicFadeInDuration = musicFadeInDuration,
        musicFadeInEasing = musicFadeInEasing,
        videoFillFadeInDuration = videoFillFadeInDuration,
        videoFillFadeOutEasing = videoFillFadeOutEasing,
        removedFadeInDuration = removedFadeInDuration,
    )
}
