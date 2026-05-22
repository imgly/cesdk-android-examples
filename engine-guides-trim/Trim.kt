import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import kotlin.math.abs

data class TrimVideoClipsSummary(
    val sourceDuration: Double,
    val trimOffset: Double,
    val trimLength: Double,
    val blockDuration: Double,
    val audioSourceDuration: Double,
    val audioTrimOffset: Double,
    val audioTrimLength: Double,
    val looping: Boolean,
)

fun trimVideoClips(
    license: String?,
    userId: String,
): Job = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.trim.example")
    try {
        engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1280, height = 720)
        trimVideoClips(engine)
    } finally {
        engine.stop()
    }
}

suspend fun trimVideoClips(engine: Engine): TrimVideoClipsSummary = withContext(Dispatchers.Main) {
    val sourceVideoUri =
        Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/" +
                "pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        )

    engine.scene.createFromVideo(sourceVideoUri)
    val page = engine.scene.getPages().first()
    val videoBlock = engine.block.findByType(DesignBlockType.Graphic).first()
    val videoFill = engine.block.getFill(videoBlock)

    // highlight-android-load-resource
    engine.block.forceLoadAVResource(block = videoFill)
    val sourceDuration = engine.block.getAVResourceTotalDuration(block = videoFill)
    // highlight-android-load-resource

    check(sourceDuration >= 8.0) {
        "The sample video must be at least 8 seconds long."
    }

    // highlight-android-check-support
    check(engine.block.supportsTrim(block = videoFill)) {
        "This video fill does not support trim properties."
    }
    // highlight-android-check-support

    // highlight-android-apply-trim
    engine.block.setTrimOffset(block = videoFill, offset = 2.0)
    engine.block.setTrimLength(block = videoFill, length = 5.0)
    // highlight-android-apply-trim

    // highlight-android-read-trim-values
    val currentTrimOffset = engine.block.getTrimOffset(block = videoFill)
    val currentTrimLength = engine.block.getTrimLength(block = videoFill)
    check(abs(currentTrimOffset - 2.0) < 0.001)
    check(abs(currentTrimLength - 5.0) < 0.001)
    // highlight-android-read-trim-values

    // highlight-android-trim-with-duration
    check(engine.block.supportsDuration(block = videoBlock))
    engine.block.setLooping(block = videoFill, looping = false)
    engine.block.setTrimOffset(block = videoFill, offset = 3.0)
    engine.block.setTrimLength(block = videoFill, length = 5.0)
    engine.block.setDuration(block = videoBlock, duration = 5.0)

    val blockDuration = engine.block.getDuration(block = videoBlock)
    check(abs(blockDuration - 5.0) < 0.001)
    // highlight-android-trim-with-duration

    // highlight-android-trim-with-looping
    engine.block.setLooping(block = videoFill, looping = true)
    engine.block.setTrimOffset(block = videoFill, offset = 5.0)
    engine.block.setTrimLength(block = videoFill, length = 3.0)
    engine.block.setDuration(block = videoBlock, duration = 9.0)

    val looping = engine.block.isLooping(block = videoFill)
    check(looping)
    // highlight-android-trim-with-looping

    // highlight-android-frame-accurate-trim
    // Supply this from your media pipeline; Android does not expose source frame rate through the Engine API.
    val knownFrameRate = 30.0
    val startFrame = 60
    val frameCount = 150
    val frameOffset = startFrame / knownFrameRate
    val frameLength = frameCount / knownFrameRate

    engine.block.setTrimOffset(block = videoFill, offset = frameOffset)
    engine.block.setTrimLength(block = videoFill, length = frameLength)
    // highlight-android-frame-accurate-trim

    // highlight-android-batch-trim-videos
    val trimmableFills =
        engine.block
            .findByType(DesignBlockType.Graphic)
            .map(engine.block::getFill)
            .filter { fill -> engine.block.supportsTrim(block = fill) }

    val loadedFillDurations =
        coroutineScope {
            trimmableFills
                .map { fill ->
                    async {
                        engine.block.forceLoadAVResource(block = fill)
                        fill to engine.block.getAVResourceTotalDuration(block = fill)
                    }
                }.awaitAll()
        }

    for ((fill, duration) in loadedFillDurations) {
        if (duration >= 4.0) {
            engine.block.setTrimOffset(block = fill, offset = 1.0)
            engine.block.setTrimLength(block = fill, length = 3.0)
        }
    }
    // highlight-android-batch-trim-videos

    val audioBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = audioBlock)

    // highlight-android-trim-audio
    // Android exposes the audio source URI through the "audio/fileURI" property key.
    engine.block.setUri(
        block = audioBlock,
        property = "audio/fileURI",
        value =
            Uri.parse(
                "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a",
            ),
    )
    engine.block.forceLoadAVResource(block = audioBlock)
    val audioSourceDuration = engine.block.getAVResourceTotalDuration(block = audioBlock)
    check(audioSourceDuration >= 9.0) {
        "The sample audio must be at least 9 seconds long."
    }
    check(engine.block.supportsTrim(block = audioBlock))

    engine.block.setTrimOffset(block = audioBlock, offset = 1.0)
    engine.block.setTrimLength(block = audioBlock, length = 8.0)
    engine.block.setTimeOffset(block = audioBlock, offset = 2.0)
    engine.block.setDuration(block = audioBlock, duration = 8.0)
    engine.block.setVolume(block = audioBlock, volume = 0.7F)
    // highlight-android-trim-audio

    TrimVideoClipsSummary(
        sourceDuration = sourceDuration,
        trimOffset = currentTrimOffset,
        trimLength = currentTrimLength,
        blockDuration = blockDuration,
        audioSourceDuration = audioSourceDuration,
        audioTrimOffset = engine.block.getTrimOffset(block = audioBlock),
        audioTrimLength = engine.block.getTrimLength(block = audioBlock),
        looping = looping,
    )
}
