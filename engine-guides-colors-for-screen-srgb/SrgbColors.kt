import ly.img.engine.CMYKColor
import ly.img.engine.Color
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.SpotColor

fun srgbColors(engine: Engine) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
    engine.block.setWidth(block = block, value = 400F)
    engine.block.setHeight(block = block, value = 300F)
    engine.block.setPositionX(block = block, value = 200F)
    engine.block.setPositionY(block = block, value = 150F)
    engine.block.appendChild(parent = page, child = block)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(textBlock, text = "sRGB Background")
    engine.block.setWidth(block = textBlock, value = 320F)
    engine.block.setHeight(block = textBlock, value = 80F)
    engine.block.setPositionX(block = textBlock, value = 240F)
    engine.block.setPositionY(block = textBlock, value = 480F)
    engine.block.appendChild(parent = page, child = textBlock)

    // highlight-android-create-rgba
    val rgbaBlue = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F)
    val rgbaBlueFromInts = Color.fromRGBA(r = 51, g = 102, b = 230, a = 255)
    val rgbaRed = Color.fromHex("#FFD91A1A")
    val rgbaNavy = Color.fromColor(android.graphics.Color.rgb(13, 20, 46))
    val rgbaOrange = Color.fromResource(android.R.color.holo_orange_light)
    // highlight-android-create-rgba

    check(rgbaBlueFromInts.a == 1F)

    // highlight-android-create-transparent
    val semiTransparentBlack = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0.5F)
    // highlight-android-create-transparent

    // highlight-android-apply-fill
    engine.block.setFillSolidColor(block = block, color = rgbaBlue)
    // highlight-android-apply-fill

    // highlight-android-apply-text
    engine.block.setTextColor(block = textBlock, color = rgbaNavy)
    // highlight-android-apply-text

    // highlight-android-apply-stroke
    engine.block.setStrokeEnabled(block = block, enabled = true)
    engine.block.setStrokeWidth(block = block, width = 8F)
    engine.block.setStrokeColor(block = block, color = rgbaRed)
    // highlight-android-apply-stroke

    // highlight-android-apply-background
    engine.block.setBackgroundColorEnabled(block = textBlock, enabled = true)
    engine.block.setBackgroundColor(block = textBlock, color = rgbaOrange)
    // highlight-android-apply-background

    check(engine.block.isBackgroundColorEnabled(textBlock))

    // highlight-android-apply-shadow
    engine.block.setDropShadowEnabled(block = block, enabled = true)
    engine.block.setDropShadowOffsetX(block = block, offsetX = 15F)
    engine.block.setDropShadowOffsetY(block = block, offsetY = 15F)
    engine.block.setDropShadowColor(block = block, color = semiTransparentBlack)
    // highlight-android-apply-shadow

    // highlight-android-get-color
    val currentFillColor = engine.block.getFillSolidColor(block)
    val currentTextColors = engine.block.getTextColors(block = textBlock)
    val currentBackgroundColor = engine.block.getBackgroundColor(textBlock)
    val currentStrokeColor = engine.block.getStrokeColor(block)
    val currentShadowColor = engine.block.getDropShadowColor(block)
    // highlight-android-get-color

    check(currentFillColor == rgbaBlue)
    check(currentTextColors.single() == rgbaNavy)
    check(currentBackgroundColor == rgbaOrange)
    check(currentShadowColor == semiTransparentBlack)

    // highlight-android-identify-rgba
    when (currentStrokeColor) {
        is RGBAColor -> {
            check(currentStrokeColor.r == rgbaRed.r)
            check(currentStrokeColor.g == rgbaRed.g)
            check(currentStrokeColor.b == rgbaRed.b)
            check(currentStrokeColor.a == rgbaRed.a)
        }

        is CMYKColor -> error("Expected sRGB color, got CMYK: $currentStrokeColor")
        is SpotColor -> error("Expected sRGB color, got spot color: $currentStrokeColor")
    }
    // highlight-android-identify-rgba

    // highlight-android-convert-to-srgb
    val cmykOrange = Color.fromCMYK(c = 0F, m = 0.5F, y = 1F, k = 0F, tint = 1F)
    val convertedToSrgb = engine.editor.convertColorToColorSpace(
        color = cmykOrange,
        colorSpace = ColorSpace.SRGB,
    )
    check(convertedToSrgb is RGBAColor)
    // highlight-android-convert-to-srgb
}
