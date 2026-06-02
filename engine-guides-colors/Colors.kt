import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.CMYKColor
import ly.img.engine.Color
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType

fun colors(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    try {
        val scene = engine.scene.create()

        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(block, value = 350F)
        engine.block.setPositionY(block, value = 400F)
        engine.block.setWidth(block, value = 100F)
        engine.block.setHeight(block, value = 100F)
        engine.block.appendChild(parent = page, child = block)

        val fill = engine.block.createFill(FillType.Color)
        engine.block.setFill(block, fill = fill)

        // highlight-android-create-colors
        val rgbaBlue = Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F)
        val cmykRed = Color.fromCMYK(c = 0F, m = 1F, y = 1F, k = 0F, tint = 1F)
        val cmykPartialRed = Color.fromCMYK(c = 0F, m = 1F, y = 1F, k = 0F, tint = 0.5F)

        val spotPinkFlamingo = Color.fromSpotColor(
            name = "Pink-Flamingo",
            tint = 1F,
            externalReference = "Brand-Colors",
        )
        val spotPartialYellow = Color.fromSpotColor(name = "Yellow", tint = 0.3F)
        // highlight-android-create-colors

        // highlight-android-define-spot
        engine.editor.setSpotColor(
            name = "Pink-Flamingo",
            Color.fromRGBA(r = 0.988F, g = 0.455F, b = 0.992F),
        )
        engine.editor.setSpotColor(name = "Yellow", Color.fromCMYK(c = 0F, m = 0F, y = 1F, k = 0F))
        // highlight-android-define-spot

        // highlight-android-apply-fill
        val colorFill = engine.block.getFill(block)
        // Fill colors use the generic color property path for RGB, CMYK, and spot colors.
        engine.block.setColor(
            colorFill,
            property = "fill/color/value",
            value = rgbaBlue,
        )
        engine.block.setColor(
            colorFill,
            property = "fill/color/value",
            value = cmykRed,
        )
        engine.block.setColor(
            colorFill,
            property = "fill/color/value",
            value = spotPinkFlamingo,
        )
        val currentFillColor = engine.block.getColor(
            colorFill,
            property = "fill/color/value",
        )
        // highlight-android-apply-fill

        check(currentFillColor == spotPinkFlamingo)

        // highlight-android-apply-stroke
        engine.block.setStrokeEnabled(block, enabled = true)
        engine.block.setStrokeWidth(block, width = 8F)
        engine.block.setStrokeColor(block, color = cmykPartialRed)
        val currentStrokeColor = engine.block.getStrokeColor(block)
        // highlight-android-apply-stroke

        check(currentStrokeColor == cmykPartialRed)

        // highlight-android-apply-shadow
        engine.block.setDropShadowEnabled(block, enabled = true)
        engine.block.setDropShadowOffsetX(block, offsetX = 12F)
        engine.block.setDropShadowOffsetY(block, offsetY = 12F)
        engine.block.setDropShadowColor(block, color = spotPartialYellow)
        val currentShadowColor = engine.block.getDropShadowColor(block)
        // highlight-android-apply-shadow

        check(currentShadowColor == spotPartialYellow)

        // highlight-android-convert-color
        val cmykBlueConverted = engine.editor.convertColorToColorSpace(
            color = rgbaBlue,
            colorSpace = ColorSpace.CMYK,
        )
        val rgbaPinkFlamingoConverted = engine.editor.convertColorToColorSpace(
            color = spotPinkFlamingo,
            colorSpace = ColorSpace.SRGB,
        )
        // highlight-android-convert-color

        check(cmykBlueConverted is CMYKColor)
        check(rgbaPinkFlamingoConverted is RGBAColor)

        // highlight-android-list-spot
        val definedSpotColors = engine.editor.findAllSpotColors()
        // highlight-android-list-spot

        check("Pink-Flamingo" in definedSpotColors)
        check("Yellow" in definedSpotColors)

        // highlight-android-update-spot
        engine.editor.setSpotColor("Yellow", Color.fromCMYK(c = 0.2F, m = 0F, y = 1F, k = 0F))
        val updatedYellow = engine.editor.getSpotColorCMYK("Yellow")
        // highlight-android-update-spot

        check(updatedYellow.c == 0.2F)

        // highlight-android-remove-spot
        engine.editor.removeSpotColor("Yellow")
        // highlight-android-remove-spot

        check("Yellow" !in engine.editor.findAllSpotColors())
    } finally {
        engine.stop()
    }
}
