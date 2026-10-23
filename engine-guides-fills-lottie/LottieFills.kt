import android.util.Log
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "LottieFills"

data class LottieFills(
    val duration: Double,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

// A 3 second Lottie at 30 frames per second. Its image layer fades in a
// photo and zooms into it. The asset names the photo by an absolute URL.
// highlight-android-document
private val lottieDocument =
    """
    {
      "v": "5.7.0", "fr": 30, "ip": 0, "op": 90, "w": 1000, "h": 1000,
      "assets": [{
        "id": "photo", "w": 2500, "h": 1667, "u": "",
        "p": "https://img.ly/static/ubq_samples/sample_1.jpg", "e": 0
      }],
      "layers": [{
        "ty": 2, "refId": "photo", "ip": 0, "op": 90, "st": 0,
        "ks": {
          "a": { "a": 0, "k": [1250, 833] },
          "p": { "a": 0, "k": [500, 500] },
          "s": { "a": 1, "k": [{ "t": 0, "s": [60, 60] }, { "t": 90, "s": [80, 80] }] },
          "o": { "a": 1, "k": [{ "t": 0, "s": [0] }, { "t": 30, "s": [100] }] }
        }
      }]
    }
    """.trimIndent()
// highlight-android-document

// A buffer takes a direct ByteBuffer
private fun directBytes(text: String): ByteBuffer {
    val bytes = text.toByteArray(Charsets.UTF_8)
    return ByteBuffer.allocateDirect(bytes.size).put(bytes).apply { flip() }
}

suspend fun lottieFills(engine: Engine): LottieFills {
    // highlight-android-setup
    // A page of 3 seconds, the length of the animation
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1000F)
    engine.block.setHeight(block = page, value = 1000F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 3.0)
    // highlight-android-setup

    // highlight-android-buffer
    // A buffer holds the document in memory and gives it a URI
    val lottieUri = engine.editor.createBuffer()
    engine.editor.setBufferData(uri = lottieUri, offset = 0, data = directBytes(lottieDocument))
    // highlight-android-buffer

    // highlight-android-fill
    // A video fill plays the Lottie. Set a hosted .json or .lottie URL the same way.
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block = block, value = 1000F)
    engine.block.setHeight(block = block, value = 1000F)
    val fill = engine.block.createFill(FillType.Video)
    engine.block.setUri(block = fill, property = "fill/video/fileURI", value = lottieUri)
    engine.block.setFill(block = block, fill = fill)
    engine.block.appendChild(parent = page, child = block)
    engine.block.setDuration(block = block, duration = 3.0)
    // highlight-android-fill

    // highlight-android-load
    // The load finishes when the Lottie and the images it references are loaded
    engine.block.forceLoadAVResource(block = fill)
    // 3.0
    val duration = engine.block.getAVResourceTotalDuration(block = fill)
    Log.i(TAG, "Duration: $duration")
    // highlight-android-load

    // highlight-android-export-frame
    // Seek the page to 1.5 seconds and export that frame
    engine.block.setPlaybackTime(block = page, time = 1.5)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    Log.i(TAG, "PNG bytes: ${previewPng.remaining()}")
    // highlight-android-export-frame

    // highlight-android-errors
    // A file that is not a Lottie or a video does not load
    val brokenUri = engine.editor.createBuffer()
    engine.editor.setBufferData(uri = brokenUri, offset = 0, data = directBytes("not a Lottie"))
    val brokenFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(block = brokenFill, property = "fill/video/fileURI", value = brokenUri)
    val errorCode = try {
        engine.block.forceLoadAVResource(block = brokenFill)
        null
    } catch (error: EngineException) {
        // EDITOR.RESOURCE_LOAD_FAILED
        error.code
    }
    engine.block.destroy(block = brokenFill)
    engine.editor.destroyBuffer(uri = brokenUri)
    // highlight-android-errors

    return LottieFills(
        duration = duration,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
