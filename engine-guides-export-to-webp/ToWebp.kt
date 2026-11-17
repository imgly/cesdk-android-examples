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

data class ToWebpResult(
    val webpData: ByteBuffer,
    val losslessWebpData: ByteBuffer,
    val sizedWebpData: ByteBuffer,
    val savedWebpFile: File,
)

suspend fun toWebp(
    engine: Engine,
    outputFile: File,
): ToWebpResult {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)

    val accent = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(accent, "WebP export sample")
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
    engine.block.setName(overlay, "WebP export overlay")
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

    val webpData = exportToWebpImage(
        engine = engine,
        page = page,
        onPreExport = { waitForExportEngineStartup() },
    )
    val losslessWebpData = exportLosslessWebp(
        engine = engine,
        page = page,
        onPreExport = { waitForExportEngineStartup() },
    )
    val sizedWebpData = exportWebpWithTargetDimensions(
        engine = engine,
        page = page,
        onPreExport = { waitForExportEngineStartup() },
    )
    val savedWebpFile = saveWebpExportToFile(webpData, outputFile)

    return ToWebpResult(
        webpData = webpData.asReadOnlyBuffer(),
        losslessWebpData = losslessWebpData.asReadOnlyBuffer(),
        sizedWebpData = sizedWebpData.asReadOnlyBuffer(),
        savedWebpFile = savedWebpFile,
    )
}

suspend fun exportToWebpImage(
    engine: Engine,
    page: DesignBlock,
    onPreExport: (suspend Engine.() -> Unit)? = null,
): ByteBuffer {
    val webpData = if (onPreExport == null) {
        // highlight-android-export-webp
        val webpData = engine.block.export(
            block = page,
            mimeType = MimeType.WEBP,
            options = ExportOptions(webpQuality = 0.8F),
        )
        // highlight-android-export-webp
        webpData
    } else {
        engine.block.export(
            block = page,
            mimeType = MimeType.WEBP,
            options = ExportOptions(webpQuality = 0.8F),
            onPreExport = onPreExport,
        )
    }

    check(webpData.hasRemaining()) { "WebP export is empty" }
    return webpData
}

suspend fun exportLosslessWebp(
    engine: Engine,
    page: DesignBlock,
    onPreExport: (suspend Engine.() -> Unit)? = null,
): ByteBuffer {
    val webpData = if (onPreExport == null) {
        // highlight-android-lossless
        val losslessWebpData = engine.block.export(
            block = page,
            mimeType = MimeType.WEBP,
            options = ExportOptions(webpQuality = 1.0F),
        )
        // highlight-android-lossless
        losslessWebpData
    } else {
        engine.block.export(
            block = page,
            mimeType = MimeType.WEBP,
            options = ExportOptions(webpQuality = 1.0F),
            onPreExport = onPreExport,
        )
    }

    check(webpData.hasRemaining()) { "lossless WebP export is empty" }
    return webpData
}

suspend fun exportWebpWithTargetDimensions(
    engine: Engine,
    page: DesignBlock,
    onPreExport: (suspend Engine.() -> Unit)? = null,
): ByteBuffer {
    val webpData = if (onPreExport == null) {
        // highlight-android-target-dimensions
        val options = ExportOptions(
            webpQuality = 0.85F,
            targetWidth = 1920F,
            targetHeight = 1080F,
        )
        val sizedWebpData = engine.block.export(
            block = page,
            mimeType = MimeType.WEBP,
            options = options,
        )
        // highlight-android-target-dimensions
        sizedWebpData
    } else {
        val options = ExportOptions(
            webpQuality = 0.85F,
            targetWidth = 1920F,
            targetHeight = 1080F,
        )
        engine.block.export(
            block = page,
            mimeType = MimeType.WEBP,
            options = options,
            onPreExport = onPreExport,
        )
    }

    check(webpData.hasRemaining()) { "target-size WebP export is empty" }
    return webpData
}

// Source-only smoke-test synchronization for Android export worker startup.
// The rendered snippets hide this hook; the runtime gate uses it so the export
// worker settings observer cannot race its background Engine.stop().
private suspend fun waitForExportEngineStartup() {
    yield()
}

// highlight-android-save-file
suspend fun saveWebpExportToFile(
    webpData: ByteBuffer,
    outputFile: File,
): File = withContext(Dispatchers.IO) {
    outputFile.parentFile?.mkdirs()

    FileOutputStream(outputFile).channel.use { channel ->
        val readableData = webpData.asReadOnlyBuffer()
        while (readableData.hasRemaining()) {
            channel.write(readableData)
        }
    }

    check(outputFile.length() > 0L) { "saved WebP file is empty" }
    outputFile
}
// highlight-android-save-file
