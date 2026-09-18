import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.toList
import ly.img.engine.AudioFromVideoOptions
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import java.io.ByteArrayOutputStream
import kotlin.math.abs

private const val TAG = "AudioGuide"

suspend fun audio(engine: Engine): String {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 12.0)

    // highlight-android-create-audio
    val audioBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = audioBlock)
    engine.block.setUri(
        block = audioBlock,
        property = "audio/fileURI",
        value = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a"),
    )

    engine.block.forceLoadAVResource(audioBlock)
    val resourceDuration = engine.block.getAVResourceTotalDuration(audioBlock)
    // highlight-android-create-audio

    // highlight-android-video-fill-setup
    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse("https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-kampus-production-8154913.mp4"),
    )
    engine.block.setFill(videoBlock, fill = videoFill)
    engine.block.appendChild(parent = page, child = videoBlock)

    engine.block.forceLoadAVResource(videoFill)
    // highlight-android-video-fill-setup

    // highlight-android-extract-audio
    val trackCountBeforeExtraction = engine.block.getAudioTrackCountFromVideo(videoFill)
    check(trackCountBeforeExtraction > 0) {
        "Video source must contain an audio track."
    }
    val extractedAudioBlock = engine.block.createAudioFromVideo(
        videoFill = videoFill,
        trackIndex = 0,
        options = AudioFromVideoOptions(
            keepTrimSettings = true,
            muteOriginalVideo = true,
        ),
    )
    engine.block.appendChild(parent = page, child = extractedAudioBlock)
    // highlight-android-extract-audio

    // highlight-android-extract-all-audio
    val allExtractedAudioBlocks = engine.block.createAudiosFromVideo(
        videoFill = videoFill,
        options = AudioFromVideoOptions(
            keepTrimSettings = true,
            muteOriginalVideo = true,
        ),
    )
    allExtractedAudioBlocks.forEach { audio ->
        engine.block.appendChild(parent = page, child = audio)
    }
    // highlight-android-extract-all-audio

    // highlight-android-track-info
    val audioTrackCount = engine.block.getAudioTrackCountFromVideo(videoFill)
    Log.i(TAG, "Video has $audioTrackCount audio track(s).")
    // highlight-android-track-info

    // highlight-android-playback-control
    engine.block.setPlaybackTime(page, time = 3.0)
    val playbackTime = engine.block.getPlaybackTime(page)

    engine.block.setPlaying(page, enabled = true)
    val playing = engine.block.isPlaying(page)
    engine.block.setPlaying(page, enabled = false)
    val paused = !engine.block.isPlaying(page)

    engine.block.setVolume(audioBlock, volume = 0.7F)
    val volume = engine.block.getVolume(audioBlock)

    engine.block.setMuted(audioBlock, muted = true)
    val muted = engine.block.isMuted(audioBlock)

    engine.block.setPlaybackSpeed(audioBlock, speed = 1.25F)
    val playbackSpeed = engine.block.getPlaybackSpeed(audioBlock)

    engine.block.setSoloPlaybackEnabled(audioBlock, enabled = true)
    val soloPlaybackEnabled = engine.block.isSoloPlaybackEnabled(audioBlock)
    engine.block.setSoloPlaybackEnabled(audioBlock, enabled = false)
    val soloPlaybackDisabled = !engine.block.isSoloPlaybackEnabled(audioBlock)
    // highlight-android-playback-control

    // highlight-android-timing
    engine.block.setPlaybackSpeed(audioBlock, speed = 1.0F)
    engine.block.setTimeOffset(audioBlock, offset = 2.0)
    val timeOffset = engine.block.getTimeOffset(audioBlock)

    engine.block.setDuration(audioBlock, duration = 8.0)

    engine.block.setTrimOffset(audioBlock, offset = 1.0)
    val trimOffset = engine.block.getTrimOffset(audioBlock)

    engine.block.setLooping(audioBlock, looping = true)
    val looping = engine.block.isLooping(audioBlock)

    engine.block.setTrimLength(audioBlock, length = 6.0)
    val trimLength = engine.block.getTrimLength(audioBlock)

    val blockDuration = engine.block.getDuration(audioBlock)
    // highlight-android-timing

    // highlight-android-waveform
    val waveformChunks = engine.block.generateAudioThumbnailSequence(
        block = audioBlock,
        samplesPerChunk = 4,
        timeBegin = 0.0,
        timeEnd = 4.0,
        numberOfSamples = 16,
        numberOfChannels = 1,
    ).toList()

    val waveformSampleCount = waveformChunks.sumOf { chunk -> chunk.samples.size }
    // highlight-android-waveform

    // highlight-android-save-scene
    val transientAudioResources = engine.editor.findAllTransientResources()
    transientAudioResources.forEach { (transientUri, _) ->
        val resourceBytes = ByteArrayOutputStream()
        engine.editor.getResourceData(
            uri = transientUri,
            chunkSize = 64 * 1024,
        ) { chunk ->
            val copy = chunk.duplicate()
            val bytes = ByteArray(copy.remaining())
            copy.get(bytes)
            resourceBytes.write(bytes)
            true
        }

        val permanentUri = uploadTransientAudioResource(
            sourceUri = transientUri,
            data = resourceBytes.toByteArray(),
        )
        engine.editor.relocateResource(
            currentUri = transientUri,
            relocatedUri = permanentUri,
        )
    }

    val remainingTransientAudioResources = engine.editor.findAllTransientResources()
    val savedScene = engine.scene.saveToString(
        scene = scene,
        allowedResourceSchemes = listOf("http", "https"),
    )
    // highlight-android-save-scene

    check(audioBlock != extractedAudioBlock)
    check(audioTrackCount > 0)
    check(allExtractedAudioBlocks.size == audioTrackCount)
    check(abs(playbackTime - 3.0) < 0.001)
    check(playing)
    check(paused)
    check(abs(volume - 0.7F) < 0.001F)
    check(muted)
    check(abs(playbackSpeed - 1.25F) < 0.001F)
    check(soloPlaybackEnabled)
    check(soloPlaybackDisabled)
    check(abs(timeOffset - 2.0) < 0.001)
    check(abs(blockDuration - 8.0) < 0.001)
    check(abs(trimOffset - 1.0) < 0.001)
    check(abs(trimLength - 6.0) < 0.001)
    check(looping)
    check(resourceDuration > 0.0)
    check(waveformChunks.isNotEmpty())
    check(waveformSampleCount > 0)
    check(transientAudioResources.isNotEmpty())
    check(remainingTransientAudioResources.isEmpty())
    check(savedScene.isNotBlank())

    return savedScene
}

// highlight-android-upload-helper
private fun uploadTransientAudioResource(
    sourceUri: Uri,
    data: ByteArray,
): Uri {
    check(data.isNotEmpty()) { "Cannot persist an empty audio resource." }

    // Replace this with your app's storage client and return its permanent URI.
    // Transient buffer URIs do not carry stable file names, so the app owns the storage key.
    val sourceId = sourceUri.toString().hashCode().toString(radix = 16)
    val fileName = "extracted-audio-$sourceId-${data.size}.m4a"
    return Uri.parse("https://your-storage.example/audio/$fileName")
}
// highlight-android-upload-helper
