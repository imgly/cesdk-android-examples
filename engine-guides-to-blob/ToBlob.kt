import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

data class ToBlobResult(
    val pngData: ByteBuffer,
    val jpegData: ByteBuffer,
    val pageExports: List<ByteBuffer>,
    val savedPngFile: File,
)

fun toBlob(
    application: Application,
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
    outputDir: File,
): Deferred<ToBlobResult> = CoroutineScope(Dispatchers.Main).async {
    val engine = startToBlobEngine(
        application = application,
        license = license,
        userId = userId,
    )

    try {
        val pages = createExportScene(engine)
        val page = pages.first()
        val pngData = exportBlockToBinaryData(engine, page).copyForVerification()
        val jpegData = exportWithOptions(engine, page).copyForVerification()
        val pageExports = exportMultipleBlocks(engine).map(ByteBuffer::copyForVerification)
        val savedPngFile = saveByteBufferToFile(
            buffer = pngData,
            outputFile = File(outputDir, "to-blob-page.png"),
        )

        ToBlobResult(
            pngData = pngData,
            jpegData = jpegData,
            pageExports = pageExports,
            savedPngFile = savedPngFile,
        )
    } finally {
        engine.stop()
    }
}

suspend fun startToBlobEngine(
    application: Application,
    license: String?,
    userId: String,
): Engine {
    Engine.init(application)
    val engine = Engine.getInstance(id = "ly.img.engine.toBlob")

    try {
        engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1080, height = 1920)
        return engine
    } catch (error: Throwable) {
        engine.stop()
        throw error
    }
}

private fun ByteBuffer.copyForVerification(): ByteBuffer {
    val duplicate = asReadOnlyBuffer()
    val bytes = ByteArray(duplicate.remaining())
    duplicate.get(bytes)
    return ByteBuffer.wrap(bytes).asReadOnlyBuffer()
}

private fun createExportScene(engine: Engine): List<DesignBlock> {
    val scene = engine.scene.create()

    return List(2) { pageIndex ->
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 1280F)
        engine.block.setHeight(page, value = 720F)
        engine.block.appendChild(parent = scene, child = page)

        addPageContent(engine, page, pageIndex)
        page
    }
}

private fun addPageContent(
    engine: Engine,
    page: DesignBlock,
    pageIndex: Int,
) {
    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, "To Blob background")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 1280F)
    engine.block.setHeight(background, value = 720F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = background,
        color = Color.fromHex("#FFF8FAFC"),
    )
    engine.block.appendChild(parent = page, child = background)

    val panel = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(panel, "To Blob export panel")
    engine.block.setShape(panel, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(panel, value = 820F)
    engine.block.setHeight(panel, value = 420F)
    engine.block.setPositionX(panel, value = 230F)
    engine.block.setPositionY(panel, value = 150F)
    engine.block.setFill(panel, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = panel,
        color = if (pageIndex == 0) Color.fromHex("#FF1F6FEB") else Color.fromHex("#FFCF3E53"),
    )
    engine.block.appendChild(parent = page, child = panel)

    val stripe = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(stripe, "To Blob accent stripe")
    engine.block.setShape(stripe, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(stripe, value = 820F)
    engine.block.setHeight(stripe, value = 72F)
    engine.block.setPositionX(stripe, value = 230F)
    engine.block.setPositionY(stripe, value = 498F)
    engine.block.setFill(stripe, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = stripe,
        color = Color.fromHex("#FF111827"),
    )
    engine.block.appendChild(parent = page, child = stripe)
}

// highlight-android-export-png
suspend fun exportBlockToBinaryData(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
    )

    check(pngData.hasRemaining()) { "PNG export is empty" }
    return pngData
}
// highlight-android-export-png

// highlight-android-export-options
suspend fun exportWithOptions(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(
        jpegQuality = 0.8F,
        targetWidth = 1920F,
        targetHeight = 1080F,
    )
    val jpegData = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = options,
    )

    check(jpegData.hasRemaining()) { "JPEG export is empty" }
    return jpegData
}
// highlight-android-export-options

// highlight-android-export-multiple
suspend fun exportMultipleBlocks(engine: Engine): List<ByteBuffer> {
    val pages = engine.scene.getPages()
    val pngBuffers = engine.block.export(
        blocks = pages,
        mimeType = MimeType.PNG,
    )

    check(pngBuffers.size == pages.size)
    pngBuffers.forEachIndexed { index, pngData ->
        check(pngData.hasRemaining()) { "PNG export ${index + 1} is empty" }
    }
    return pngBuffers
}
// highlight-android-export-multiple

// highlight-android-save-to-file
suspend fun saveByteBufferToFile(
    buffer: ByteBuffer,
    outputFile: File,
): File = withContext(Dispatchers.IO) {
    outputFile.parentFile?.mkdirs()
    val readableBuffer = buffer.asReadOnlyBuffer()

    FileOutputStream(outputFile).channel.use { channel ->
        while (readableBuffer.hasRemaining()) {
            channel.write(readableBuffer)
        }
    }

    check(outputFile.length() > 0L) { "Saved export is empty" }
    outputFile
}
// highlight-android-save-to-file
