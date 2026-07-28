import android.net.Uri
import android.util.Log
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import kotlin.math.abs

private const val TAG = "InsertAudioGuide"

data class InsertMediaAudio(
    val sourceUri: Uri,
    val sourceDurationSeconds: Double,
    val timeOffsetSeconds: Double,
    val durationSeconds: Double,
    val volume: Float,
    val muted: Boolean,
    val unmuted: Boolean,
    val looping: Boolean,
    val audioBlockCountBeforeRemoval: Int,
    val audioBlockCountAfterRemoval: Int,
    val removedBlockValid: Boolean,
)

suspend fun insertMediaAudio(
    engine: Engine,
    assetBaseUri: Uri,
): InsertMediaAudio {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.setDuration(block = page, duration = 30.0)

    // highlight-android-create-audio-block
    val audioBlock = engine.block.create(DesignBlockType.Audio)
    val audioUri = assetBaseUri
        .buildUpon()
        .appendPath("ly.img.audio")
        .appendPath("audios")
        .appendPath("far_from_home.m4a")
        .build()
    engine.block.setUri(block = audioBlock, property = "audio/fileURI", value = audioUri)
    engine.block.appendChild(parent = page, child = audioBlock)
    // highlight-android-create-audio-block

    // highlight-android-configure-timeline
    engine.block.forceLoadAVResource(block = audioBlock)
    val sourceDuration = engine.block.getAVResourceTotalDuration(block = audioBlock)

    // This sample uses a 30-second page, so clamp the audio block to that timeline.
    val playbackDuration = sourceDuration.coerceAtMost(30.0)
    engine.block.setTimeOffset(block = audioBlock, offset = 0.0)
    engine.block.setDuration(block = audioBlock, duration = playbackDuration)

    val timeOffset = engine.block.getTimeOffset(block = audioBlock)
    val duration = engine.block.getDuration(block = audioBlock)
    // highlight-android-configure-timeline

    // highlight-android-adjust-volume
    engine.block.setVolume(block = audioBlock, volume = 0.8F)
    val currentVolume = engine.block.getVolume(block = audioBlock)
    Log.i(TAG, "Audio volume: ${(currentVolume * 100).toInt()}%")
    // highlight-android-adjust-volume

    // highlight-android-mute-audio
    engine.block.setMuted(block = audioBlock, muted = true)
    val muted = engine.block.isMuted(block = audioBlock)

    engine.block.setMuted(block = audioBlock, muted = false)
    val unmuted = !engine.block.isMuted(block = audioBlock)
    // highlight-android-mute-audio

    // highlight-android-loop-audio
    engine.block.setLooping(block = audioBlock, looping = true)
    val looping = engine.block.isLooping(block = audioBlock)
    // highlight-android-loop-audio

    // highlight-android-find-audio-blocks
    val audioBlocks = engine.block.findByType(DesignBlockType.Audio)
    audioBlocks.forEach { block ->
        val uri = engine.block.getUri(block = block, property = "audio/fileURI")
        val offset = engine.block.getTimeOffset(block = block)
        val blockDuration = engine.block.getDuration(block = block)
        val blockVolume = engine.block.getVolume(block = block)
        Log.i(TAG, "Audio starts at ${offset}s for ${blockDuration}s at ${(blockVolume * 100).toInt()}%: $uri")
    }
    // highlight-android-find-audio-blocks

    // highlight-android-remove-audio
    engine.block.destroy(block = audioBlock)
    val removedBlockValid = engine.block.isValid(block = audioBlock)
    val remainingAudioBlocks = engine.block.findByType(DesignBlockType.Audio)
    // highlight-android-remove-audio

    check(abs(timeOffset - 0.0) < 0.001)
    check(duration > 0.0)
    check(duration <= 30.0)
    check(abs(currentVolume - 0.8F) < 0.001F)
    check(muted)
    check(unmuted)
    check(looping)
    check(audioBlock in audioBlocks)
    check(!removedBlockValid)
    check(audioBlock !in remainingAudioBlocks)

    return InsertMediaAudio(
        sourceUri = audioUri,
        sourceDurationSeconds = sourceDuration,
        timeOffsetSeconds = timeOffset,
        durationSeconds = duration,
        volume = currentVolume,
        muted = muted,
        unmuted = unmuted,
        looping = looping,
        audioBlockCountBeforeRemoval = audioBlocks.size,
        audioBlockCountAfterRemoval = remainingAudioBlocks.size,
        removedBlockValid = removedBlockValid,
    )
}
