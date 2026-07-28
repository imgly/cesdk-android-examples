import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import kotlin.math.roundToInt

suspend fun designUnits(engine: Engine): DesignBlock = withContext(engine.dispatcher) {
    val page = configureDesignUnits(engine)
    engine.scene.zoomToBlock(page)
    engine.block.forceLoadResources(listOf(page))
    page
}

internal fun configureDesignUnits(engine: Engine): DesignBlock {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-design-units-get-design-unit
    // Get the current design unit. New scenes default to PIXEL.
    val currentUnit = engine.scene.getDesignUnit()
    println("Current design unit: $currentUnit") // PIXEL
    // highlight-android-design-units-get-design-unit

    // highlight-android-design-units-set-design-unit
    // Switch to millimeters for a print workflow.
    engine.scene.setDesignUnit(DesignUnit.MILLIMETER)

    // Verify the change.
    val newUnit = engine.scene.getDesignUnit()
    println("Design unit changed to: $newUnit") // MILLIMETER
    // highlight-android-design-units-set-design-unit

    // highlight-android-design-units-configure-dpi
    // Set DPI to 300 for print-quality exports.
    engine.block.setFloat(scene, property = "scene/dpi", value = 300F)

    // Read back the DPI value.
    val dpi = engine.block.getFloat(scene, property = "scene/dpi")
    println("DPI set to: $dpi") // 300.0
    // highlight-android-design-units-configure-dpi

    // highlight-android-design-units-set-page-dimensions
    // Set the page to A4 dimensions (210 x 297 mm).
    engine.block.setWidth(page, value = 210F)
    engine.block.setHeight(page, value = 297F)

    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    println("Page dimensions: ${pageWidth}mm x ${pageHeight}mm")
    // highlight-android-design-units-set-page-dimensions

    // highlight-android-design-units-create-text-block
    // Create a text block positioned and sized in millimeters.
    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = textBlock)

    // Position at 20 mm from left, 30 mm from top.
    engine.block.setPositionX(textBlock, value = 20F)
    engine.block.setPositionY(textBlock, value = 30F)

    // Size: 170 mm wide, 50 mm tall.
    engine.block.setWidth(textBlock, value = 170F)
    engine.block.setHeight(textBlock, value = 50F)

    engine.block.setString(
        textBlock,
        property = "text/text",
        value = "This A4 document uses millimeter units with 300 DPI for print-ready output.",
    )
    // Font sizes stay in points even when the scene uses millimeters.
    engine.block.setTextFontSize(textBlock, fontSize = 24F)
    // highlight-android-design-units-create-text-block

    // highlight-android-design-units-compare-units
    // At 300 DPI: 1 inch = 300 pixels, 1 mm ~= 11.81 pixels.
    val a4WidthPixels = 210.0 * (300.0 / 25.4)
    val a4HeightPixels = 297.0 * (300.0 / 25.4)
    println("A4 at 300 DPI exports as ${a4WidthPixels.roundToInt()} x ${a4HeightPixels.roundToInt()} pixels")
    // highlight-android-design-units-compare-units

    return page
}
