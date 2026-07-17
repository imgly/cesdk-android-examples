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
import kotlin.math.ceil
import kotlin.math.max

data class ToRawDataResult(
    val width: Int,
    val height: Int,
    val maxExportSize: Int,
    val rawByteCount: Int,
    val centerPixel: RgbaPixel,
    val rawFile: File,
    val thumbnailByteCount: Int,
)

suspend fun toRawData(
    engine: Engine,
    outputDir: File,
): ToRawDataResult {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 640F)
    engine.block.setHeight(page, value = 360F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, "Raw data background")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 640F)
    engine.block.setHeight(background, value = 360F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = background,
        color = Color.fromHex("#FF101827"),
    )
    engine.block.appendChild(parent = page, child = background)

    // The centered panel gives the smoke test a deterministic raw pixel value.
    val panel = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(panel, "Raw data sample panel")
    engine.block.setShape(panel, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(panel, value = 360F)
    engine.block.setHeight(panel, value = 200F)
    engine.block.setPositionX(panel, value = 140F)
    engine.block.setPositionY(panel, value = 80F)
    engine.block.setFill(panel, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = panel,
        color = Color.fromHex("#FFFF0000"),
    )
    engine.block.appendChild(parent = page, child = panel)

    val width = 1920
    val height = 1080
    val maxExportSize = ensureRawExportFits(engine, page, width, height)
    val rawData = exportRawData(
        engine = engine,
        page = page,
        width = width,
        height = height,
        onPreExport = {
            waitForBackgroundExportEngine()
        },
    )
    val centerPixel = readPixel(
        rawData = rawData,
        width = width,
        x = width / 2,
        y = height / 2,
    )
    val rawFile = saveRawDataFile(
        rawData = rawData,
        outputFile = File(outputDir, "design.rgba"),
    )
    val thumbnailData = exportRawThumbnail(
        engine = engine,
        page = page,
        onPreExport = {
            waitForBackgroundExportEngine()
        },
    )

    return ToRawDataResult(
        width = width,
        height = height,
        maxExportSize = maxExportSize,
        rawByteCount = rawData.remaining(),
        centerPixel = centerPixel,
        rawFile = rawFile,
        thumbnailByteCount = thumbnailData.remaining(),
    )
}

// highlight-android-export
suspend fun exportRawData(
    engine: Engine,
    page: DesignBlock,
    width: Int,
    height: Int,
    onPreExport: suspend Engine.() -> Unit = {},
): ByteBuffer {
    val options = ExportOptions(
        targetWidth = width.toFloat(),
        targetHeight = height.toFloat(),
    )
    val rawData = engine.block.export(
        block = page,
        mimeType = MimeType.BINARY,
        options = options,
        onPreExport = onPreExport,
    )

    check(rawData.remaining() % 4 == 0) {
        "Expected complete RGBA pixels, got ${rawData.remaining()} bytes"
    }
    return rawData
}
// highlight-android-export

// highlight-android-read-pixel
data class RgbaPixel(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int,
)

fun readPixel(
    rawData: ByteBuffer,
    width: Int,
    x: Int,
    y: Int,
): RgbaPixel {
    check(width > 0) {
        "Raw data width must be greater than zero"
    }
    val bytesPerPixel = 4
    val rowByteCount = width * bytesPerPixel
    val byteCount = rawData.limit()
    check(byteCount % rowByteCount == 0) {
        "Raw data buffer does not contain complete rows for width $width"
    }
    val height = byteCount / rowByteCount
    check(x in 0 until width && y in 0 until height) {
        "Pixel coordinate ($x, $y) is outside the ${width}x$height raw data buffer"
    }
    val index = (y * width + x) * 4
    val buffer = rawData.asReadOnlyBuffer()

    return RgbaPixel(
        red = buffer.get(index).toInt() and 0xFF,
        green = buffer.get(index + 1).toInt() and 0xFF,
        blue = buffer.get(index + 2).toInt() and 0xFF,
        alpha = buffer.get(index + 3).toInt() and 0xFF,
    )
}
// highlight-android-read-pixel

// highlight-android-save-file
suspend fun saveRawDataFile(
    rawData: ByteBuffer,
    outputFile: File,
): File = withContext(Dispatchers.IO) {
    outputFile.parentFile?.mkdirs()
    val readableData = rawData.asReadOnlyBuffer()
    val expectedByteCount = readableData.remaining().toLong()

    FileOutputStream(outputFile).channel.use { channel ->
        while (readableData.hasRemaining()) {
            channel.write(readableData)
        }
    }

    check(outputFile.length() == expectedByteCount) {
        "Saved raw data file has ${outputFile.length()} bytes, expected $expectedByteCount"
    }
    outputFile
}
// highlight-android-save-file

// highlight-android-target-size
suspend fun exportRawThumbnail(
    engine: Engine,
    page: DesignBlock,
    onPreExport: suspend Engine.() -> Unit = {},
): ByteBuffer {
    val width = 960
    val height = 540
    val options = ExportOptions(
        targetWidth = width.toFloat(),
        targetHeight = height.toFloat(),
    )
    val rawData = engine.block.export(
        block = page,
        mimeType = MimeType.BINARY,
        options = options,
        onPreExport = onPreExport,
    )

    check(rawData.remaining() % 4 == 0) {
        "Expected complete RGBA pixels, got ${rawData.remaining()} bytes"
    }
    return rawData
}
// highlight-android-target-size

// highlight-android-check-limits
fun ensureRawExportFits(
    engine: Engine,
    block: DesignBlock,
    width: Int,
    height: Int,
): Int {
    check(width > 0 && height > 0) {
        "Requested raw export size must be greater than zero"
    }
    val blockWidth = engine.block.getWidth(block)
    val blockHeight = engine.block.getHeight(block)
    check(blockWidth > 0F && blockHeight > 0F) {
        "Raw export block size must be greater than zero"
    }
    val scale = max(width.toFloat() / blockWidth, height.toFloat() / blockHeight)
    val filledWidth = ceil(blockWidth * scale).toInt()
    val filledHeight = ceil(blockHeight * scale).toInt()
    val maxExportSize = engine.editor.getMaxExportSize()
    check(filledWidth <= maxExportSize && filledHeight <= maxExportSize) {
        "Requested raw export size ${width}x$height fills to ${filledWidth}x$filledHeight, exceeding the $maxExportSize px limit"
    }
    return maxExportSize
}
// highlight-android-check-limits

private suspend fun waitForBackgroundExportEngine() {
    // Raw data exports finish quickly enough that the Android background export engine
    // can otherwise stop before its startup settings stream has registered.
    yield()
}
