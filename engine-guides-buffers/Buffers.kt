import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

fun buffers(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    try {
        // highlight-setup-video-scene
        val scene = engine.scene.createForVideo()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)
        engine.block.setWidth(page, value = 1080F)
        engine.block.setHeight(page, value = 1920F)
        engine.block.setDuration(page, duration = 2.0)
        // highlight-setup-video-scene

        // highlight-create-buffer
        val bufferUri = engine.editor.createBuffer()
        // highlight-create-buffer

        // highlight-generate-samples
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
        // highlight-generate-samples

        // highlight-write-buffer
        val bytesPerSample = 2
        val wavDataSize = sampleCount * bytesPerSample
        val wavFileSize = 44 + wavDataSize
        val wavData = ByteBuffer.allocateDirect(wavFileSize).order(ByteOrder.LITTLE_ENDIAN)

        wavData.put("RIFF".toByteArray())
        wavData.putInt(wavFileSize - 8)
        wavData.put("WAVE".toByteArray())
        wavData.put("fmt ".toByteArray())
        wavData.putInt(16)
        wavData.putShort(1.toShort())
        wavData.putShort(numChannels.toShort())
        wavData.putInt(sampleRate)
        wavData.putInt(sampleRate * numChannels * bytesPerSample)
        wavData.putShort((numChannels * bytesPerSample).toShort())
        wavData.putShort((bytesPerSample * 8).toShort())
        wavData.put("data".toByteArray())
        wavData.putInt(wavDataSize)

        for (sample in samples) {
            val clampedSample = sample.coerceIn(-1F, 1F)
            val pcmScale = if (clampedSample < 0F) 32768F else Short.MAX_VALUE.toFloat()
            val pcmSample = clampedSample * pcmScale
            wavData.putShort(pcmSample.toInt().toShort())
        }

        wavData.flip()
        engine.editor.setBufferData(uri = bufferUri, offset = 0, data = wavData)
        // highlight-write-buffer

        // highlight-read-buffer
        val header = engine.editor.getBufferData(uri = bufferUri, offset = 0, length = 44)
        val riff = ByteArray(size = 4)
        header.get(riff)
        check(String(riff) == "RIFF")
        // highlight-read-buffer

        // highlight-get-buffer-length
        val bufferLength = engine.editor.getBufferLength(uri = bufferUri)
        check(bufferLength == wavFileSize)
        // highlight-get-buffer-length

        // highlight-resize-buffer
        val demoBuffer = engine.editor.createBuffer()
        val demoData =
            ByteBuffer.allocateDirect(8).apply {
                put(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
                flip()
            }
        engine.editor.setBufferData(uri = demoBuffer, offset = 0, data = demoData)
        engine.editor.setBufferLength(uri = demoBuffer, length = 4)
        check(engine.editor.getBufferLength(uri = demoBuffer) == 4)
        engine.editor.destroyBuffer(uri = demoBuffer)
        // highlight-resize-buffer

        // highlight-assign-buffer-to-audio-block
        val audioBlock = engine.block.create(DesignBlockType.Audio)
        engine.block.setUri(block = audioBlock, property = "audio/fileURI", value = bufferUri)
        engine.block.setDuration(audioBlock, duration = durationSeconds.toDouble())
        engine.block.appendChild(parent = page, child = audioBlock)
        engine.block.forceLoadAVResource(audioBlock)
        // highlight-assign-buffer-to-audio-block

        // highlight-find-transient-resources
        val transientResources = engine.editor.findAllTransientResources()
        check(transientResources.any { (uri, size) -> uri == bufferUri && size == bufferLength })
        // highlight-find-transient-resources

        // highlight-persist-buffer
        val relocatedUri = Uri.parse("https://cdn.example.com/audio/generated-tone.wav")
        val persistedData = engine.editor.getBufferData(uri = bufferUri, offset = 0, length = bufferLength)
        check(persistedData.remaining() == bufferLength)
        engine.editor.relocateResource(currentUri = bufferUri, relocatedUri = relocatedUri)
        check(engine.block.getUri(block = audioBlock, property = "audio/fileURI") == relocatedUri)
        engine.editor.destroyBuffer(uri = bufferUri)
        // highlight-persist-buffer
    } finally {
        engine.stop()
    }
}
