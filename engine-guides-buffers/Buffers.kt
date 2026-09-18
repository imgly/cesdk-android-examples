import android.net.Uri
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

suspend fun buffers(engine: Engine) = withContext(engine.dispatcher) {
    // highlight-android-setup-video-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1920F)
    engine.block.setDuration(page, duration = 2.0)
    // highlight-android-setup-video-scene

    // highlight-android-create-buffer
    val bufferUri = engine.editor.createBuffer()
    // highlight-android-create-buffer

    try {
        // highlight-android-generate-samples
        val sampleRate = 44_100
        val durationSeconds = 2
        val frequencyHz = 440.0
        val numChannels = 2
        val samplesPerChannel = sampleRate * durationSeconds
        val sampleCount = samplesPerChannel * numChannels
        val samples = FloatArray(sampleCount)

        for (sampleIndex in 0 until samplesPerChannel) {
            val time = sampleIndex / sampleRate.toDouble()
            val sampleValue = (sin(2 * PI * frequencyHz * time) * 0.5).toFloat()
            val bufferIndex = sampleIndex * numChannels
            samples[bufferIndex] = sampleValue
            samples[bufferIndex + 1] = sampleValue
        }
        // highlight-android-generate-samples

        // highlight-android-write-buffer
        val bytesPerSample = 2
        val wavDataSize = sampleCount * bytesPerSample
        val wavFileSize = 44 + wavDataSize
        val wavData = ByteBuffer.allocateDirect(wavFileSize).order(ByteOrder.LITTLE_ENDIAN)

        "RIFF".forEach { wavData.put(it.code.toByte()) }
        wavData.putInt(wavFileSize - 8)
        "WAVE".forEach { wavData.put(it.code.toByte()) }
        "fmt ".forEach { wavData.put(it.code.toByte()) }
        wavData.putInt(16)
        wavData.putShort(1.toShort())
        wavData.putShort(numChannels.toShort())
        wavData.putInt(sampleRate)
        wavData.putInt(sampleRate * numChannels * bytesPerSample)
        wavData.putShort((numChannels * bytesPerSample).toShort())
        wavData.putShort((bytesPerSample * 8).toShort())
        "data".forEach { wavData.put(it.code.toByte()) }
        wavData.putInt(wavDataSize)

        for (sample in samples) {
            val clampedSample = sample.coerceIn(-1F, 1F)
            val pcmScale = if (clampedSample < 0F) 32768F else Short.MAX_VALUE.toFloat()
            val pcmSample = clampedSample * pcmScale
            wavData.putShort(pcmSample.toInt().toShort())
        }

        wavData.flip()
        engine.editor.setBufferData(uri = bufferUri, offset = 0, data = wavData)
        // highlight-android-write-buffer

        // highlight-android-read-buffer
        val header = engine.editor.getBufferData(uri = bufferUri, offset = 0, length = 44)
        val riff = buildString {
            repeat(4) { append(header.get().toInt().toChar()) }
        }
        check(riff == "RIFF")
        // highlight-android-read-buffer

        // highlight-android-get-buffer-length
        val bufferLength = engine.editor.getBufferLength(uri = bufferUri)
        check(bufferLength == wavFileSize)
        // highlight-android-get-buffer-length

        // highlight-android-resize-buffer
        val demoBuffer = engine.editor.createBuffer()
        try {
            val demoData =
                ByteBuffer.allocateDirect(8).apply {
                    for (value in 1..8) put(value.toByte())
                    flip()
                }
            engine.editor.setBufferData(uri = demoBuffer, offset = 0, data = demoData)
            engine.editor.setBufferLength(uri = demoBuffer, length = 4)
            check(engine.editor.getBufferLength(uri = demoBuffer) == 4)
        } finally {
            engine.editor.destroyBuffer(uri = demoBuffer)
        }
        // highlight-android-resize-buffer

        // highlight-android-assign-buffer-to-audio-block
        val audioBlock = engine.block.create(DesignBlockType.Audio)
        engine.block.setUri(block = audioBlock, property = "audio/fileURI", value = bufferUri)
        engine.block.setDuration(audioBlock, duration = durationSeconds.toDouble())
        engine.block.appendChild(parent = page, child = audioBlock)
        engine.block.forceLoadAVResource(audioBlock)
        // highlight-android-assign-buffer-to-audio-block

        // highlight-android-find-transient-resources
        val transientResources = engine.editor.findAllTransientResources()
        check(transientResources.any { (uri, size) -> uri == bufferUri && size == bufferLength })
        // highlight-android-find-transient-resources

        // highlight-android-persist-buffer
        val relocatedUri = Uri.parse("https://cdn.example.com/audio/generated-tone.wav")
        val persistedData = engine.editor.getBufferData(uri = bufferUri, offset = 0, length = bufferLength)
        check(persistedData.remaining() == bufferLength)
        engine.editor.relocateResource(currentUri = bufferUri, relocatedUri = relocatedUri)
        check(engine.block.getUri(block = audioBlock, property = "audio/fileURI") == relocatedUri)
        // highlight-android-persist-buffer
    } finally {
        engine.block.findByType(DesignBlockType.Audio)
            .filter(engine.block::isValid)
            .filter { audioBlock ->
                engine.block.getUri(audioBlock, property = "audio/fileURI") == bufferUri
            }
            .forEach(engine.block::destroy)
        engine.editor.destroyBuffer(uri = bufferUri)
    }
}
