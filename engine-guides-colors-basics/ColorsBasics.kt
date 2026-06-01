import android.util.Log
import ly.img.engine.CMYKColor
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.SpotColor

private const val TAG = "ColorsBasics"

suspend fun colorsBasics(engine: Engine) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val srgbBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(srgbBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(srgbBlock, value = 80F)
    engine.block.setPositionY(srgbBlock, value = 120F)
    engine.block.setWidth(srgbBlock, value = 160F)
    engine.block.setHeight(srgbBlock, value = 160F)

    val srgbFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(srgbBlock, fill = srgbFill)
    engine.block.appendChild(parent = page, child = srgbBlock)

    // highlight-android-srgb-color
    val srgbColor = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F)
    engine.block.setColor(srgbFill, property = "fill/color/value", value = srgbColor)
    // highlight-android-srgb-color
    check(engine.block.getColor(srgbFill, property = "fill/color/value") == srgbColor)

    val cmykBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(cmykBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(cmykBlock, value = 320F)
    engine.block.setPositionY(cmykBlock, value = 120F)
    engine.block.setWidth(cmykBlock, value = 160F)
    engine.block.setHeight(cmykBlock, value = 160F)

    val cmykFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(cmykBlock, fill = cmykFill)
    engine.block.appendChild(parent = page, child = cmykBlock)

    // highlight-android-cmyk-color
    val cmykColor = Color.fromCMYK(c = 0F, m = 0.8F, y = 0.95F, k = 0F, tint = 1F)
    engine.block.setColor(cmykFill, property = "fill/color/value", value = cmykColor)
    // highlight-android-cmyk-color
    check(engine.block.getColor(cmykFill, property = "fill/color/value") == cmykColor)

    // highlight-android-define-spot-color
    engine.editor.setSpotColor(
        name = "MyBrand Red",
        // RGB spot-color approximations are stored as opaque colors; apply tint on the
        // SpotColor reference.
        color = Color.fromRGBA(r = 0.95F, g = 0.25F, b = 0.21F, a = 1F),
    )
    engine.editor.setSpotColor(
        name = "MyBrand Blue",
        color = Color.fromCMYK(c = 1F, m = 0.7F, y = 0F, k = 0.1F),
    )
    val storedBrandRed = engine.editor.getSpotColorRGB("MyBrand Red")
    val storedBrandBlue = engine.editor.getSpotColorCMYK("MyBrand Blue")
    check(storedBrandRed == Color.fromRGBA(r = 0.95F, g = 0.25F, b = 0.21F, a = 1F))
    check(storedBrandBlue == Color.fromCMYK(c = 1F, m = 0.7F, y = 0F, k = 0.1F))
    // highlight-android-define-spot-color

    val spotBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(spotBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(spotBlock, value = 560F)
    engine.block.setPositionY(spotBlock, value = 120F)
    engine.block.setWidth(spotBlock, value = 160F)
    engine.block.setHeight(spotBlock, value = 160F)

    val spotFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(spotBlock, fill = spotFill)
    engine.block.appendChild(parent = page, child = spotBlock)

    // highlight-android-spot-color
    val spotColor = Color.fromSpotColor(
        name = "MyBrand Red",
        tint = 1F,
        externalReference = "brand-palette-v1",
    )
    engine.block.setColor(spotFill, property = "fill/color/value", value = spotColor)
    // highlight-android-spot-color
    val appliedSpotColor = engine.block.getColor(spotFill, property = "fill/color/value")
    check(appliedSpotColor is SpotColor)
    check(appliedSpotColor.name == spotColor.name)
    check(appliedSpotColor.tint == spotColor.tint)

    // highlight-android-stroke-color
    if (engine.block.supportsStroke(srgbBlock)) {
        engine.block.setStrokeEnabled(srgbBlock, enabled = true)
        engine.block.setStrokeWidth(srgbBlock, width = 4F)
        engine.block.setStrokeColor(
            srgbBlock,
            color = Color.fromRGBA(r = 0.1F, g = 0.2F, b = 0.5F, a = 1F),
        )
    }

    val cmykStroke = Color.fromCMYK(c = 0F, m = 0.5F, y = 0.6F, k = 0.2F, tint = 1F)
    if (engine.block.supportsStroke(cmykBlock)) {
        engine.block.setStrokeEnabled(cmykBlock, enabled = true)
        engine.block.setStrokeWidth(cmykBlock, width = 4F)
        engine.block.setStrokeColor(cmykBlock, color = cmykStroke)
    }

    if (engine.block.supportsStroke(spotBlock)) {
        engine.block.setStrokeEnabled(spotBlock, enabled = true)
        engine.block.setStrokeWidth(spotBlock, width = 4F)
        engine.block.setStrokeColor(
            spotBlock,
            color = Color.fromSpotColor(name = "MyBrand Red", tint = 0.7F),
        )
    }
    // highlight-android-stroke-color

    // highlight-android-get-color
    val readSrgb = engine.block.getColor(srgbFill, property = "fill/color/value")
    val readCmyk = engine.block.getColor(cmykFill, property = "fill/color/value")
    val readSpot = engine.block.getColor(spotFill, property = "fill/color/value")
    val readStroke = engine.block.getStrokeColor(cmykBlock)

    for (color in listOf(readSrgb, readCmyk, readSpot, readStroke)) {
        when (color) {
            is RGBAColor -> Log.i(TAG, "sRGB: r=${color.r}, g=${color.g}, b=${color.b}, a=${color.a}")
            is CMYKColor -> Log.i(
                TAG,
                "CMYK: c=${color.c}, m=${color.m}, y=${color.y}, k=${color.k}, tint=${color.tint}",
            )
            is SpotColor -> Log.i(
                TAG,
                "Spot: name=${color.name}, tint=${color.tint}, ref=${color.externalReference}",
            )
        }
    }
    // highlight-android-get-color
    check(readSrgb is RGBAColor)
    check(readCmyk is CMYKColor)
    check(readSpot is SpotColor)
    check(readStroke == cmykStroke)
}
