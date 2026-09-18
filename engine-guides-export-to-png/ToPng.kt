import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
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

data class ToPngResult(
    val pngData: ByteBuffer,
    val compressedPngData: ByteBuffer,
    val sizedPngData: ByteBuffer,
    val savedPngFile: File,
)

suspend fun toPng(
    engine: Engine,
    outputFile: File,
): ToPngResult {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)

    val accent = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(accent, "PNG export sample")
    engine.block.setShape(accent, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(accent, value = 520F)
    engine.block.setHeight(accent, value = 520F)
    engine.block.setPositionX(accent, value = 380F)
    engine.block.setPositionY(accent, value = 70F)
    engine.block.setFill(accent, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = accent,
        color = Color.fromRGBA(r = 0.14F, g = 0.34F, b = 0.84F, a = 0.86F),
    )
    engine.block.appendChild(parent = page, child = accent)

    val overlay = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(overlay, "PNG export overlay")
    engine.block.setShape(overlay, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(overlay, value = 360F)
    engine.block.setHeight(overlay, value = 260F)
    engine.block.setPositionX(overlay, value = 500F)
    engine.block.setPositionY(overlay, value = 230F)
    engine.block.setFill(overlay, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = overlay,
        color = Color.fromRGBA(r = 0.96F, g = 0.36F, b = 0.18F, a = 0.78F),
    )
    engine.block.appendChild(parent = page, child = overlay)

    val highlight = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(highlight, "PNG export highlight")
    engine.block.setShape(highlight, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(highlight, value = 180F)
    engine.block.setHeight(highlight, value = 180F)
    engine.block.setPositionX(highlight, value = 418F)
    engine.block.setPositionY(highlight, value = 182F)
    engine.block.setFill(highlight, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = highlight,
        color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 0.9F),
    )
    engine.block.appendChild(parent = page, child = highlight)

    val pngData = exportToPngImage(
        engine = engine,
        page = page,
        onPreExport = { waitForExportEngineStartup() },
    )
    val compressedPngData = exportPngWithCompression(
        engine = engine,
        page = page,
        onPreExport = { waitForExportEngineStartup() },
    )
    val sizedPngData = exportPngWithTargetDimensions(
        engine = engine,
        page = page,
        onPreExport = { waitForExportEngineStartup() },
    )
    val savedPngFile = savePngExportToFile(pngData, outputFile)

    return ToPngResult(
        pngData = pngData.asReadOnlyBuffer(),
        compressedPngData = compressedPngData.asReadOnlyBuffer(),
        sizedPngData = sizedPngData.asReadOnlyBuffer(),
        savedPngFile = savedPngFile,
    )
}

suspend fun exportToPngImage(
    engine: Engine,
    page: DesignBlock,
    onPreExport: (suspend Engine.() -> Unit)? = null,
): ByteBuffer {
    val pngData = if (onPreExport == null) {
        // highlight-android-export-png
        val pngData = engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
        )
        // highlight-android-export-png
        pngData
    } else {
        engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            onPreExport = onPreExport,
        )
    }

    check(pngData.hasRemaining()) { "PNG export is empty" }
    return pngData
}

suspend fun exportPngWithCompression(
    engine: Engine,
    page: DesignBlock,
    onPreExport: (suspend Engine.() -> Unit)? = null,
): ByteBuffer {
    val pngData = if (onPreExport == null) {
        // highlight-android-compression-level
        val options = ExportOptions(pngCompressionLevel = 9)
        val compressedPngData = engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = options,
        )
        // highlight-android-compression-level
        compressedPngData
    } else {
        val options = ExportOptions(pngCompressionLevel = 9)
        engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = options,
            onPreExport = onPreExport,
        )
    }

    check(pngData.hasRemaining()) { "compressed PNG export is empty" }
    return pngData
}

suspend fun exportPngWithTargetDimensions(
    engine: Engine,
    page: DesignBlock,
    onPreExport: (suspend Engine.() -> Unit)? = null,
): ByteBuffer {
    val pngData = if (onPreExport == null) {
        // highlight-android-target-dimensions
        val options = ExportOptions(
            targetWidth = 1920F,
            targetHeight = 1080F,
        )
        val sizedPngData = engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = options,
        )
        // highlight-android-target-dimensions
        sizedPngData
    } else {
        val options = ExportOptions(
            targetWidth = 1920F,
            targetHeight = 1080F,
        )
        engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = options,
            onPreExport = onPreExport,
        )
    }

    check(pngData.hasRemaining()) { "target-size PNG export is empty" }
    return pngData
}

// Source-only smoke-test synchronization for Android export worker startup.
// The rendered snippets hide this hook; the runtime gate uses it so the export
// worker settings observer cannot race its background Engine.stop().
private suspend fun waitForExportEngineStartup() {
    yield()
}

// highlight-android-save-file
suspend fun savePngExportToFile(
    pngData: ByteBuffer,
    outputFile: File,
): File = withContext(Dispatchers.IO) {
    outputFile.parentFile?.mkdirs()

    FileOutputStream(outputFile).channel.use { channel ->
        val readableData = pngData.asReadOnlyBuffer()
        while (readableData.hasRemaining()) {
            channel.write(readableData)
        }
    }

    check(outputFile.length() > 0L) { "saved PNG file is empty" }
    outputFile
}
// highlight-android-save-file
