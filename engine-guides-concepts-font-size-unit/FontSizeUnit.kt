import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.FontUnit
import ly.img.engine.SceneLayout
import ly.img.engine.SizeMode
import kotlin.math.abs

private const val FONT_SIZE_EPSILON = 0.001F

fun fontSizeUnit(engine: Engine) {
    // highlight-android-create-with-units
    // `scene.create(designUnit, fontSizeUnit)` lets you pair both units
    // explicitly. When `fontSizeUnit` is null, CE.SDK pairs it with
    // `designUnit`: `Pixel` design -> `Pixel` fonts, `Millimeter` and
    // `Inch` -> `Point` fonts.
    val scene = engine.scene.create(
        designUnit = DesignUnit.PIXEL,
        fontSizeUnit = FontUnit.POINT,
        sceneLayout = SceneLayout.FREE,
    )
    // highlight-android-create-with-units

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)

    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = text)
    engine.block.replaceText(text, text = "Font Size Unit")
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)

    // highlight-android-get-font-size-unit
    // Read the scene's current font-size unit. This scene passes `Point`
    // explicitly even though the design unit is `Pixel`.
    val initialUnit = engine.scene.getFontSizeUnit()
    println("Initial font-size unit: $initialUnit") // POINT
    // highlight-android-get-font-size-unit
    check(initialUnit == FontUnit.POINT)

    // highlight-android-set-font-size-unit
    // Switch the scene-wide default. Existing text keeps its visual size;
    // only future `setTextFontSize` / `getTextFontSizes` calls use the new
    // unit. `setDesignUnit` does not overwrite this setting, so the choice
    // survives changes to the design coordinate system.
    engine.scene.setFontSizeUnit(FontUnit.PIXEL)
    val switchedUnit = engine.scene.getFontSizeUnit()
    println("After switch: $switchedUnit") // PIXEL
    // highlight-android-set-font-size-unit
    check(switchedUnit == FontUnit.PIXEL)

    // highlight-android-implicit-set
    // The value 24f is interpreted in the scene's `fontSizeUnit`, so the
    // engine reads it as 24 px.
    engine.block.setTextFontSize(text, fontSize = 24F)

    // Font-size float properties use the same font-size unit.
    engine.block.setFloat(block = text, property = "text/fontSize", value = 24F)
    // highlight-android-implicit-set

    // highlight-android-read-sizes
    // `getTextFontSizes` returns values in the scene's `fontSizeUnit`,
    // mirroring how `setTextFontSize` interpreted them.
    val sizesInPixels = engine.block.getTextFontSizes(text)
    val propertyFontSizeInPixels = engine.block.getFloat(block = text, property = "text/fontSize")
    println("Sizes (px): $sizesInPixels")
    println("Property size (px): $propertyFontSizeInPixels")
    // highlight-android-read-sizes
    check(sizesInPixels.size == 1 && abs(sizesInPixels.first() - 24F) < FONT_SIZE_EPSILON)
    check(abs(propertyFontSizeInPixels - 24F) < FONT_SIZE_EPSILON)
}
