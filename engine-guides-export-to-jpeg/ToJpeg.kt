import kotlinx.coroutines.Dispatchers
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

data class JpegExport(
    val label: String,
    val jpegData: ByteBuffer,
)

data class ToJpegResult(
    val defaultQuality: JpegExport,
    val highQuality: JpegExport,
    val targetDimensions: JpegExport,
    val savedFile: File,
) {
    val allExports: List<JpegExport>
        get() = listOf(defaultQuality, highQuality, targetDimensions)
}

suspend fun toJpeg(
    engine: Engine,
    outputDir: File,
): ToJpegResult {
    val page = createJpegExportPage(engine)

    val defaultQualityData = exportToJpeg(engine, page)
    val highQualityData = exportJpegWithQuality(engine, page)
    val targetDimensionsData = exportJpegWithTargetDimensions(engine, page)
    val savedFile = saveJpegToFile(
        buffer = defaultQualityData,
        outputFile = File(outputDir, "to-jpeg-page.jpg"),
    )

    return ToJpegResult(
        defaultQuality = JpegExport("default quality", defaultQualityData),
        highQuality = JpegExport("high quality", highQualityData),
        targetDimensions = JpegExport("target dimensions", targetDimensionsData),
        savedFile = savedFile,
    )
}

suspend fun createJpegExportPage(engine: Engine): DesignBlock {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, "JPEG export background")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 1280F)
    engine.block.setHeight(background, value = 720F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromHex("#FFF4F0EA"))
    engine.block.appendChild(parent = page, child = background)

    val photoPanel = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(photoPanel, "JPEG export photo panel")
    engine.block.setShape(photoPanel, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(photoPanel, value = 820F)
    engine.block.setHeight(photoPanel, value = 460F)
    engine.block.setPositionX(photoPanel, value = 230F)
    engine.block.setPositionY(photoPanel, value = 130F)
    engine.block.setFill(photoPanel, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(photoPanel, color = Color.fromHex("#FF255C99"))
    engine.block.appendChild(parent = page, child = photoPanel)

    val highlight = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(highlight, "JPEG export highlight")
    engine.block.setShape(highlight, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(highlight, value = 520F)
    engine.block.setHeight(highlight, value = 160F)
    engine.block.setPositionX(highlight, value = 380F)
    engine.block.setPositionY(highlight, value = 270F)
    engine.block.setFill(highlight, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(highlight, color = Color.fromHex("#FFE7B65E"))
    engine.block.appendChild(parent = page, child = highlight)

    return page
}

// highlight-android-export-jpeg
suspend fun exportToJpeg(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(jpegQuality = 0.9F)
    val jpegData = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = options,
    )

    check(jpegData.hasRemaining()) { "JPEG export is empty" }
    return jpegData
}
// highlight-android-export-jpeg

// highlight-android-quality-control
suspend fun exportJpegWithQuality(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(jpegQuality = 1.0F)
    val jpegData = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = options,
    )

    check(jpegData.hasRemaining()) { "high-quality JPEG export is empty" }
    return jpegData
}
// highlight-android-quality-control

// highlight-android-target-dimensions
suspend fun exportJpegWithTargetDimensions(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(
        jpegQuality = 0.85F,
        targetWidth = 1920F,
        targetHeight = 1080F,
    )
    val jpegData = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = options,
    )

    check(jpegData.hasRemaining()) { "target-dimension JPEG export is empty" }
    return jpegData
}
// highlight-android-target-dimensions

// highlight-android-save-file
suspend fun saveJpegToFile(
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

    check(outputFile.length() > 0L) { "Saved JPEG export is empty" }
    outputFile
}
// highlight-android-save-file
