import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

data class CreateAudioLoop(
    val audioDuration: Double,
    val pageDuration: Double,
    val loopingTimeOffset: Double,
    val loopingEnabled: Boolean,
    val loopingDuration: Double,
    val nonLoopingTimeOffset: Double,
    val nonLoopingEnabled: Boolean,
    val nonLoopingDuration: Double,
    val trimmedTimeOffset: Double,
    val trimmedLoopingEnabled: Boolean,
    val trimOffset: Double,
    val trimLength: Double,
    val trimmedDuration: Double,
    val sceneString: String,
)

suspend fun createAudioLoop(engine: Engine): CreateAudioLoop {
    // highlight-android-setup
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    // highlight-android-setup

    val audioUri = "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a"

    // highlight-android-create-audio-block
    val audioBlock = engine.block.create(DesignBlockType.Audio)
    // Android exposes the audio source as the `audio/fileURI` property key.
    engine.block.setString(block = audioBlock, property = "audio/fileURI", value = audioUri)
    // highlight-android-create-audio-block

    // highlight-android-load-audio-resource
    engine.block.forceLoadAVResource(audioBlock)

    val audioDuration = engine.block.getAVResourceTotalDuration(audioBlock)
    println("Audio duration: $audioDuration seconds")
    // highlight-android-load-audio-resource

    // highlight-android-size-timeline
    val loopingTimeOffset = 0.0
    val loopingDuration = audioDuration * 3.0

    val nonLoopingTimeOffset = loopingTimeOffset + loopingDuration + 1.0
    val nonLoopingDuration = audioDuration + 12.0

    val trimmedTimeOffset = nonLoopingTimeOffset + nonLoopingDuration + 1.0
    val trimOffset = 1.0
    val trimLength = 2.0
    val trimmedDuration = trimLength * 4.0

    val pageDuration = maxOf(
        loopingTimeOffset + loopingDuration,
        nonLoopingTimeOffset + nonLoopingDuration,
        trimmedTimeOffset + trimmedDuration,
    )
    engine.block.setDuration(page, duration = pageDuration)
    // highlight-android-size-timeline

    // highlight-android-enable-looping
    val loopingAudio = engine.block.duplicate(block = audioBlock, attachToParent = false)
    engine.block.appendChild(parent = page, child = loopingAudio)
    engine.block.setTimeOffset(loopingAudio, offset = loopingTimeOffset)
    engine.block.setLooping(loopingAudio, looping = true)
    engine.block.setDuration(loopingAudio, duration = loopingDuration)
    // highlight-android-enable-looping

    // highlight-android-query-looping-state
    val isLooping = engine.block.isLooping(loopingAudio)
    println("Is looping: $isLooping")
    // highlight-android-query-looping-state

    // highlight-android-non-looping-audio
    val nonLoopingAudio = engine.block.duplicate(block = audioBlock, attachToParent = false)
    engine.block.appendChild(parent = page, child = nonLoopingAudio)
    engine.block.setTimeOffset(nonLoopingAudio, offset = nonLoopingTimeOffset)
    engine.block.setLooping(nonLoopingAudio, looping = false)
    engine.block.setDuration(nonLoopingAudio, duration = nonLoopingDuration)
    // highlight-android-non-looping-audio

    // highlight-android-looping-with-trim
    val trimmedLoopAudio = engine.block.duplicate(block = audioBlock, attachToParent = false)
    engine.block.appendChild(parent = page, child = trimmedLoopAudio)
    engine.block.setTimeOffset(trimmedLoopAudio, offset = trimmedTimeOffset)

    engine.block.setTrimOffset(trimmedLoopAudio, offset = trimOffset)
    engine.block.setTrimLength(trimmedLoopAudio, length = trimLength)

    engine.block.setLooping(trimmedLoopAudio, looping = true)
    engine.block.setDuration(trimmedLoopAudio, duration = trimmedDuration)
    // highlight-android-looping-with-trim

    engine.block.destroy(audioBlock)

    // highlight-android-export
    val sceneString = engine.scene.saveToString(scene = scene)
    println("Scene saved (${sceneString.length} characters)")
    // highlight-android-export

    return CreateAudioLoop(
        audioDuration = audioDuration,
        pageDuration = engine.block.getDuration(page),
        loopingTimeOffset = engine.block.getTimeOffset(loopingAudio),
        loopingEnabled = isLooping,
        loopingDuration = engine.block.getDuration(loopingAudio),
        nonLoopingTimeOffset = engine.block.getTimeOffset(nonLoopingAudio),
        nonLoopingEnabled = engine.block.isLooping(nonLoopingAudio),
        nonLoopingDuration = engine.block.getDuration(nonLoopingAudio),
        trimmedTimeOffset = engine.block.getTimeOffset(trimmedLoopAudio),
        trimmedLoopingEnabled = engine.block.isLooping(trimmedLoopAudio),
        trimOffset = engine.block.getTrimOffset(trimmedLoopAudio),
        trimLength = engine.block.getTrimLength(trimmedLoopAudio),
        trimmedDuration = engine.block.getDuration(trimmedLoopAudio),
        sceneString = sceneString,
    )
}
