import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun colors(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
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

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block, fill = fill)
    // highlight-setup

    // highlight-create-colors
    val rgbaBlue = Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F)
    val cmykRed = Color.fromCMYK(c = 0F, m = 1F, y = 1F, k = 0F, tint = 1F)
    val cmykPartialRed = Color.fromCMYK(c = 0F, m = 1F, y = 1F, k = 0F, tint = 0.5F)

    engine.editor.setSpotColor(
        name = "Pink-Flamingo",
        Color.fromRGBA(r = 0.988F, g = 0.455F, b = 0.992F),
    )
    engine.editor.setSpotColor(name = "Yellow", Color.fromCMYK(c = 0F, m = 0F, y = 1F, k = 0F))
    val spotPinkFlamingo = Color.fromSpotColor(
        name = "Pink-Flamingo",
        tint = 1F,
        externalReference = "Crayola",
    )
    val spotPartialYellow = Color.fromSpotColor(name = "Yellow", tint = 0.3F)
    // highlight-create-colors

    // highlight-apply-colors
    engine.block.setColor(fill, property = "fill/color/value", value = rgbaBlue)
    engine.block.setColor(fill, property = "fill/color/value", value = cmykRed)
    engine.block.setColor(block, property = "stroke/color", value = cmykPartialRed)
    engine.block.setColor(fill, property = "fill/color/value", value = spotPinkFlamingo)
    engine.block.setColor(block, property = "dropShadow/color", value = spotPartialYellow)
    // highlight-apply-colors

    // highlight-convert-color
    val cmykBlueConverted = engine.editor.convertColorToColorSpace(
        rgbaBlue,
        colorSpace = ColorSpace.CMYK,
    )
    val rgbaPinkFlamingoConverted = engine.editor.convertColorToColorSpace(
        spotPinkFlamingo,
        colorSpace = ColorSpace.SRGB,
    )
    // highlight-convert-color

    // highlight-find-spot
    engine.editor.findAllSpotColors() // ["Crayola-Pink-Flamingo", "Yellow"]
    // highlight-find-spot

    // highlight-change-spot
    engine.editor.setSpotColor("Yellow", Color.fromCMYK(c = 0.2F, m = 0F, y = 1F, k = 0F))
    // highlight-change-spot

    // highlight-undefine-spot
    engine.editor.removeSpotColor("Yellow")
    // highlight-undefine-spot

    engine.stop()
}
