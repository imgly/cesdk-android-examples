import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import java.nio.ByteBuffer
import kotlin.math.PI
import kotlin.math.sin

fun buffers(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-setup

    // Create an audio block and append it to the page
    val audioBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.appendChild(parent = page, child = audioBlock)

    // Create a buffer
    // highlight-EditorApi.createBuffer
    val audioBuffer = engine.editor.createBuffer()

    // Reference the audio buffer resource from the audio block
    engine.block.setUri(
        block = audioBlock,
        property = "audio/fileURI",
        value = audioBuffer,
    )

    // highlight-EditorApi.setBufferData
    // Generate 10 seconds of stereo 48 kHz audio data
    val sampleCount = 10 * 48000
    val byteBuffer = ByteBuffer.allocateDirect(2 * 4 * sampleCount) // 2 channels, each 4 bytes
    repeat(sampleCount) {
        val sample = sin((440 * it * 2 * PI) / 48000).toFloat()
        byteBuffer.putFloat(sample)
        byteBuffer.putFloat(sample)
    }
    engine.editor.setBufferData(uri = audioBuffer, offset = 0, data = byteBuffer)
    // highlight-EditorApi.setBufferData

    // We can get subranges of the buffer data
    // highlight-EditorApi.getBufferData
    val chunk = engine.editor.getBufferData(uri = audioBuffer, offset = 0, length = 4096)

    // Get current length of the buffer in bytes
    // highlight-EditorApi.getBufferLength
    val length = engine.editor.getBufferLength(uri = audioBuffer)

    // Reduce the buffer to half its length, leading to 5 seconds worth of audio
    // highlight-EditorApi.setBufferLength
    engine.editor.setBufferLength(uri = audioBuffer, length = byteBuffer.capacity() / 2)

    // Free data
    // highlight-EditorApi.destroyBuffer
    engine.editor.destroyBuffer(uri = audioBuffer)
}
