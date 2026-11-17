import android.net.Uri
import android.util.Log
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.Source
import java.nio.ByteBuffer
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

private const val TAG = "PerformanceGuide"

data class PerformanceSummary(
    val sourceSetWidths: List<Int>,
    val usedMemoryBytes: Long,
    val availableMemoryBytes: Long,
    val maxExportSize: Int,
    val maxExportSizeIsKnownLimit: Boolean,
    val exportWithinSizeLimit: Boolean,
    val exportedJpeg: ByteBuffer,
    val assetBasePath: String,
)

suspend fun performance(engine: Engine): PerformanceSummary {
    val assetBasePath = "file:///android_asset/assets"

    // highlight-android-asset-base-path
    engine.editor.setSettingString(keypath = "basePath", value = assetBasePath)
    // highlight-android-asset-base-path

    val scene = engine.scene.create(designUnit = DesignUnit.PIXEL)
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block = imageBlock, value = 1280F)
    engine.block.setHeight(block = imageBlock, value = 853F)
    engine.block.setPositionX(block = imageBlock, value = 320F)
    engine.block.setPositionY(block = imageBlock, value = 113F)

    val imageFill = engine.block.createFill(FillType.Image)
    // highlight-android-source-sets
    engine.block.setSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
        sourceSet = listOf(
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_512x341.jpg"),
                width = 512,
                height = 341,
            ),
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_1024x683.jpg"),
                width = 1024,
                height = 683,
            ),
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_2048x1366.jpg"),
                width = 2048,
                height = 1366,
            ),
        ),
    )
    // highlight-android-source-sets
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
    engine.block.appendChild(parent = page, child = imageBlock)

    val configuredSourceWidths = engine.block
        .getSourceSet(block = imageFill, property = "fill/image/sourceSet")
        .map(Source::width)
        .sorted()
    check(configuredSourceWidths == listOf(512, 1024, 2048))

    // highlight-android-memory-monitoring
    val usedMemory = engine.editor.getUsedMemory()
    val availableMemory = engine.editor.getAvailableMemory()
    val totalMemory = usedMemory + availableMemory
    if (totalMemory > 0L) {
        val usagePercentage = usedMemory.toDouble() / totalMemory.toDouble() * 100.0
        Log.i(TAG, "Memory usage: ${String.format(Locale.US, "%.2f", usagePercentage)}%")
    }
    // highlight-android-memory-monitoring

    // highlight-android-export-settings
    val exportOptions = ExportOptions(
        jpegQuality = 0.8F,
        targetWidth = 1280F,
        targetHeight = 720F,
    )
    // highlight-android-export-settings

    // highlight-android-max-export-size
    val maxExportSize = engine.editor.getMaxExportSize()
    val maxExportSizeIsKnownLimit = maxExportSize != Int.MAX_VALUE

    val exportWithinSizeLimit = if (!maxExportSizeIsKnownLimit) {
        true
    } else if (
        engine.scene.getDesignUnit() == DesignUnit.PIXEL &&
        engine.block.getWidthMode(page) == SizeMode.ABSOLUTE &&
        engine.block.getHeightMode(page) == SizeMode.ABSOLUTE
    ) {
        val pageWidth = engine.block.getWidth(page)
        val pageHeight = engine.block.getHeight(page)
        val targetWidth = exportOptions.targetWidth ?: pageWidth
        val targetHeight = exportOptions.targetHeight ?: pageHeight
        val fillScale = max(targetWidth / pageWidth, targetHeight / pageHeight)
        val renderedWidth = (pageWidth * fillScale).roundToInt()
        val renderedHeight = (pageHeight * fillScale).roundToInt()

        renderedWidth <= maxExportSize && renderedHeight <= maxExportSize
    } else {
        true
    }
    // highlight-android-max-export-size

    check(exportWithinSizeLimit) { "Requested export dimensions exceed the device export limit." }

    val exportedJpeg = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = exportOptions,
    )

    check(exportedJpeg.hasRemaining()) { "JPEG export is empty." }

    return PerformanceSummary(
        sourceSetWidths = configuredSourceWidths,
        usedMemoryBytes = usedMemory,
        availableMemoryBytes = availableMemory,
        maxExportSize = maxExportSize,
        maxExportSizeIsKnownLimit = maxExportSizeIsKnownLimit,
        exportWithinSizeLimit = exportWithinSizeLimit,
        exportedJpeg = exportedJpeg.asReadOnlyBuffer(),
        assetBasePath = engine.editor.getSettingString("basePath"),
    )
}

// highlight-android-lifecycle
fun releasePerformanceEngine(
    engine: Engine,
    finishingSession: Boolean,
) {
    if (!engine.isEngineRunning()) return

    if (engine.isBound()) {
        engine.unbind()
    }

    if (finishingSession) {
        engine.stop()
    } else {
        engine.editor.setAppIsPaused(paused = true)
        engine.pause()
    }
}
// highlight-android-lifecycle

// highlight-android-resume-lifecycle
fun resumePerformanceEngine(
    engine: Engine,
    bindRenderTarget: () -> Unit,
) {
    if (!engine.isEngineRunning()) return

    if (!engine.isBound()) {
        bindRenderTarget()
    }

    engine.editor.setAppIsPaused(paused = false)
    engine.unpause()
}
// highlight-android-resume-lifecycle
