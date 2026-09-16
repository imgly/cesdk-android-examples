import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

suspend fun createAudioAdjustSpeed(engine: Engine) {
    // highlight-android-create-video-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 1280F)
    engine.block.setHeight(block = page, value = 720F)
    engine.block.setDuration(block = page, duration = 45.0)
    // highlight-android-create-video-scene

    // highlight-android-load-audio
    val normalSpeedAudio = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = normalSpeedAudio)

    // audio/fileURI is the standard Engine property key for an audio block's source URI.
    engine.block.setString(
        block = normalSpeedAudio,
        property = "audio/fileURI",
        value = "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a",
    )
    engine.block.forceLoadAVResource(block = normalSpeedAudio)
    engine.block.setDuration(block = normalSpeedAudio, duration = 10.0)
    val normalDuration = engine.block.getDuration(block = normalSpeedAudio)
    // highlight-android-load-audio

    // highlight-android-set-normal-speed
    engine.block.setPlaybackSpeed(block = normalSpeedAudio, speed = 1.0F)
    // highlight-android-set-normal-speed

    // highlight-android-query-current-speed
    val currentSpeed = engine.block.getPlaybackSpeed(block = normalSpeedAudio)
    check(currentSpeed == 1.0F)
    // highlight-android-query-current-speed

    // highlight-android-set-slow-motion
    val slowMotionAudio = engine.block.duplicate(block = normalSpeedAudio)
    engine.block.setTimeOffset(block = slowMotionAudio, offset = 11.0)
    engine.block.forceLoadAVResource(block = slowMotionAudio)
    engine.block.setDuration(block = slowMotionAudio, duration = normalDuration)
    engine.block.setPlaybackSpeed(block = slowMotionAudio, speed = 0.5F)

    val slowMotionDuration = engine.block.getDuration(block = slowMotionAudio)
    check(engine.block.getPlaybackSpeed(block = slowMotionAudio) == 0.5F)
    check(slowMotionDuration > normalDuration)
    // highlight-android-set-slow-motion

    // highlight-android-set-maximum-speed
    val maximumSpeedAudio = engine.block.duplicate(block = normalSpeedAudio)
    engine.block.setTimeOffset(block = maximumSpeedAudio, offset = 32.0)
    engine.block.forceLoadAVResource(block = maximumSpeedAudio)
    engine.block.setDuration(block = maximumSpeedAudio, duration = normalDuration)
    engine.block.setPlaybackSpeed(block = maximumSpeedAudio, speed = 3.0F)

    val maximumSpeedDuration = engine.block.getDuration(block = maximumSpeedAudio)
    check(engine.block.getPlaybackSpeed(block = maximumSpeedAudio) == 3.0F)
    check(maximumSpeedDuration < normalDuration)
    // highlight-android-set-maximum-speed

    // highlight-android-speed-and-duration
    val doubleSpeedAudio = engine.block.duplicate(block = normalSpeedAudio)
    engine.block.setTimeOffset(block = doubleSpeedAudio, offset = 37.0)
    engine.block.forceLoadAVResource(block = doubleSpeedAudio)
    engine.block.setDuration(block = doubleSpeedAudio, duration = normalDuration)

    val durationBeforeSpeedChange = engine.block.getDuration(block = doubleSpeedAudio)
    engine.block.setPlaybackSpeed(block = doubleSpeedAudio, speed = 2.0F)
    val durationAfterSpeedChange = engine.block.getDuration(block = doubleSpeedAudio)

    check(durationAfterSpeedChange < durationBeforeSpeedChange)
    // highlight-android-speed-and-duration

    // highlight-android-export
    val sceneString = engine.scene.saveToString(scene = scene)
    check(sceneString.isNotBlank())
    // highlight-android-export
}
