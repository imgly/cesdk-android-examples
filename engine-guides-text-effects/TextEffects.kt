import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle
import java.nio.ByteBuffer

suspend fun textEffects(engine: Engine): TextEffectsResult {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 500F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 800F)
    engine.block.setHeight(background, value = 500F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = background,
        color = Color.fromHex("#FFF8FAFC"),
    )
    engine.block.appendChild(parent = page, child = background)

    val shadowText = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(shadowText, text = "Drop Shadow")
    engine.block.setTextFontSize(shadowText, fontSize = 90F)
    engine.block.setWidthMode(shadowText, mode = SizeMode.AUTO)
    engine.block.setHeightMode(shadowText, mode = SizeMode.AUTO)
    engine.block.setPositionX(shadowText, value = 50F)
    engine.block.setPositionY(shadowText, value = 50F)
    engine.block.appendChild(parent = page, child = shadowText)

    // highlight-android-drop-shadow
    check(engine.block.supportsDropShadow(block = shadowText))
    engine.block.setDropShadowEnabled(block = shadowText, enabled = true)
    engine.block.setDropShadowColor(
        block = shadowText,
        color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0.6F),
    )
    engine.block.setDropShadowOffsetX(block = shadowText, offsetX = 5F)
    engine.block.setDropShadowOffsetY(block = shadowText, offsetY = 5F)
    engine.block.setDropShadowBlurRadiusX(block = shadowText, blurRadiusX = 10F)
    engine.block.setDropShadowBlurRadiusY(block = shadowText, blurRadiusY = 10F)
    // highlight-android-drop-shadow

    val outlineText = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(outlineText, text = "Outline")
    engine.block.setTextFontSize(outlineText, fontSize = 90F)
    engine.block.setWidthMode(outlineText, mode = SizeMode.AUTO)
    engine.block.setHeightMode(outlineText, mode = SizeMode.AUTO)
    engine.block.setPositionX(outlineText, value = 50F)
    engine.block.setPositionY(outlineText, value = 180F)
    engine.block.appendChild(parent = page, child = outlineText)

    // highlight-android-stroke
    check(engine.block.supportsStroke(block = outlineText))
    engine.block.setStrokeEnabled(block = outlineText, enabled = true)
    engine.block.setStrokeWidth(block = outlineText, width = 2F)
    engine.block.setStrokeColor(
        block = outlineText,
        color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F),
    )
    engine.block.setStrokeStyle(block = outlineText, style = StrokeStyle.SOLID)
    engine.block.setStrokePosition(block = outlineText, position = StrokePosition.CENTER)
    // highlight-android-stroke

    val pngData = engine.block.export(block = page, mimeType = MimeType.PNG)
    check(pngData.hasRemaining()) { "Text effects PNG export is empty" }
    val pngBytes = ByteArray(pngData.remaining())
    pngData.asReadOnlyBuffer().get(pngBytes)

    return TextEffectsResult(
        dropShadowEnabled = engine.block.isDropShadowEnabled(block = shadowText),
        dropShadowColor = engine.block.getDropShadowColor(block = shadowText),
        dropShadowOffsetX = engine.block.getDropShadowOffsetX(block = shadowText),
        dropShadowOffsetY = engine.block.getDropShadowOffsetY(block = shadowText),
        dropShadowBlurRadiusX = engine.block.getDropShadowBlurRadiusX(block = shadowText),
        dropShadowBlurRadiusY = engine.block.getDropShadowBlurRadiusY(block = shadowText),
        strokeEnabled = engine.block.isStrokeEnabled(block = outlineText),
        strokeWidth = engine.block.getStrokeWidth(block = outlineText),
        strokeColor = engine.block.getStrokeColor(block = outlineText),
        strokeStyle = engine.block.getStrokeStyle(block = outlineText),
        strokePosition = engine.block.getStrokePosition(block = outlineText),
        pngData = ByteBuffer.wrap(pngBytes).asReadOnlyBuffer(),
    )
}
