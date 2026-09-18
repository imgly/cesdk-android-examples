import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer
import kotlin.math.ceil
import kotlin.math.max

// highlight-android-observe-changes
fun observeMaxImageSizeChanges(
    engine: Engine,
    scope: CoroutineScope,
    onMaxImageSizeChanged: (Int) -> Unit,
): Job = scope.launch(Dispatchers.Main) {
    engine.editor.onSettingsChanged().collect {
        onMaxImageSizeChanged(engine.editor.getSettingInt("maxImageSize"))
    }
}
// highlight-android-observe-changes

suspend fun sizeLimits(engine: Engine): ByteBuffer = withContext(Dispatchers.Main) {
    val scene = engine.scene.create()
    engine.scene.setDesignUnit(DesignUnit.PIXEL)

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 800F)
    engine.block.setHeight(background, value = 600F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromHex("#FF2457D6"))
    engine.block.appendChild(parent = page, child = background)

    val originalMaxImageSize = engine.editor.getSettingInt("maxImageSize")

    try {
        // highlight-android-read-setting
        val currentMaxImageSize = engine.editor.getSettingInt("maxImageSize")
        // The default value is 4096 pixels.
        // highlight-android-read-setting
        check(currentMaxImageSize > 0)

        // highlight-android-write-setting
        // Lower the limit on memory-constrained devices. Apply this before
        // loading images so newly loaded textures are downscaled to the new limit.
        engine.editor.setSettingInt(keypath = "maxImageSize", value = 2048)

        // Or raise it for high-quality workflows on capable devices:
        // engine.editor.setSettingInt(keypath = "maxImageSize", value = 8192)
        // highlight-android-write-setting
        check(engine.editor.getSettingInt("maxImageSize") == 2048)

        // highlight-android-max-export-size
        // The engine reports the maximum export size supported on the current device.
        // The value is an upper bound. Exports may still fail for memory or other
        // reasons. When the limit is unknown, the engine returns Int.MAX_VALUE.
        val maxExportSize = engine.editor.getMaxExportSize()
        // highlight-android-max-export-size
        check(maxExportSize > 0)

        // highlight-android-validate-export
        // This helper is scoped to this sample's untransformed page. In a more
        // general scene, validate against the world-space dimensions your export
        // workflow uses, or rely on export error handling.
        val plannedExportOptions = ExportOptions(targetWidth = 1600F, targetHeight = 1000F)
        val designUnit = engine.scene.getDesignUnit()
        val widthMode = engine.block.getWidthMode(page)
        val heightMode = engine.block.getHeightMode(page)
        require(
            designUnit == DesignUnit.PIXEL &&
                widthMode == SizeMode.ABSOLUTE &&
                heightMode == SizeMode.ABSOLUTE,
        )

        val pageWidth = engine.block.getWidth(page)
        val pageHeight = engine.block.getHeight(page)
        val targetWidth = plannedExportOptions.targetWidth ?: pageWidth
        val targetHeight = plannedExportOptions.targetHeight ?: pageHeight
        val fillScale = max(targetWidth / pageWidth, targetHeight / pageHeight)
        val renderedWidth = ceil(pageWidth * fillScale).toInt()
        val renderedHeight = ceil(pageHeight * fillScale).toInt()
        val withinLimit = renderedWidth <= maxExportSize && renderedHeight <= maxExportSize
        // highlight-android-validate-export
        check(withinLimit)

        // highlight-android-handle-export
        // Catch export errors so the app can recover. Common remediations are
        // lowering the target box for the retry.
        // ExportOptions.targetWidth/targetHeight are pixel values for the box
        // that the exported block fills while preserving its aspect ratio.
        val initialOptions = ExportOptions(targetWidth = 1600F, targetHeight = 1200F)
        val pngData = try {
            engine.block.export(block = page, mimeType = MimeType.PNG, options = initialOptions)
        } catch (error: EngineException) {
            val retryOptions = ExportOptions(targetWidth = 640F, targetHeight = 480F)
            engine.block.export(block = page, mimeType = MimeType.PNG, options = retryOptions)
        }
        // highlight-android-handle-export

        check(engine.editor.getSettingInt("maxImageSize") == 2048)
        pngData.asReadOnlyBuffer()
    } finally {
        engine.editor.setSettingInt(keypath = "maxImageSize", value = originalMaxImageSize)
    }
}
