import android.net.Uri
import ly.img.engine.BlurType
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import kotlin.math.PI
import kotlin.math.abs

suspend fun editStickers(engine: Engine): EditStickersResult {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 450F)
    engine.block.setHeight(block = page, value = 250F)
    engine.block.appendChild(parent = scene, child = page)

    val sticker = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = sticker, shape = engine.block.createShape(ShapeType.Rect))

    val stickerFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = stickerFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse(
            "file:///android_asset/imgly-assets/" +
                "ly.img.sticker/images/emoticons/imgly_sticker_emoticons_grin.svg",
        ),
    )
    engine.block.setFill(block = sticker, fill = stickerFill)

    require(engine.block.supportsContentFillMode(block = sticker)) {
        "Sticker graphic blocks must support content fill modes."
    }
    engine.block.setContentFillMode(block = sticker, mode = ContentFillMode.CONTAIN)
    engine.block.setKind(block = sticker, kind = "sticker")
    engine.block.setWidth(block = sticker, value = 120F)
    engine.block.setHeight(block = sticker, value = 120F)
    engine.block.setPositionX(block = sticker, value = 150F)
    engine.block.setPositionY(block = sticker, value = 65F)
    engine.block.appendChild(parent = page, child = sticker)

    // highlight-android-replace-image
    val fill = engine.block.getFill(block = sticker)
    val replacementUri = Uri.parse(
        "file:///android_asset/imgly-assets/" +
            "ly.img.sticker/images/emoticons/imgly_sticker_emoticons_blush.svg",
    )
    engine.block.setUri(
        block = fill,
        property = "fill/image/imageFileURI",
        value = replacementUri,
    )
    val currentImageUri = engine.block.getUri(
        block = fill,
        property = "fill/image/imageFileURI",
    )
    // highlight-android-replace-image
    check(currentImageUri == replacementUri)

    // highlight-android-transform
    engine.block.setWidth(block = sticker, value = 120F)
    engine.block.setHeight(block = sticker, value = 120F)
    engine.block.setFlipHorizontal(block = sticker, flip = true)
    engine.block.setRotation(block = sticker, radians = (PI / 12).toFloat())
    engine.block.setPositionX(block = sticker, value = 150F)
    engine.block.setPositionY(block = sticker, value = 65F)

    val positionX = engine.block.getPositionX(block = sticker)
    val positionY = engine.block.getPositionY(block = sticker)
    val width = engine.block.getWidth(block = sticker)
    val height = engine.block.getHeight(block = sticker)
    val rotation = engine.block.getRotation(block = sticker)
    val isFlippedHorizontally = engine.block.isFlipHorizontal(block = sticker)
    // highlight-android-transform
    check(abs(positionX - 150F) < 0.001F) { "Expected sticker x=150.0, got $positionX." }
    check(abs(positionY - 65F) < 0.001F) { "Expected sticker y=65.0, got $positionY." }
    check(abs(width - 120F) < 0.001F) { "Expected sticker width=120.0, got $width." }
    check(abs(height - 120F) < 0.001F) { "Expected sticker height=120.0, got $height." }
    check(abs(rotation - (PI / 12).toFloat()) < 0.001F) { "Expected sticker rotation=${(PI / 12).toFloat()}, got $rotation." }
    check(isFlippedHorizontally) { "Expected sticker to be flipped horizontally." }

    // highlight-android-content-fill-mode
    if (engine.block.supportsContentFillMode(block = sticker)) {
        engine.block.setContentFillMode(block = sticker, mode = ContentFillMode.COVER)
    }
    val coverMode = engine.block.getContentFillMode(block = sticker)
    // highlight-android-content-fill-mode
    check(coverMode == ContentFillMode.COVER)

    engine.block.setContentFillMode(block = sticker, mode = ContentFillMode.CONTAIN)
    val finalContentFillMode = engine.block.getContentFillMode(block = sticker)

    // highlight-android-opacity
    require(engine.block.supportsOpacity(block = sticker)) {
        "Sticker graphic blocks must support opacity."
    }
    engine.block.setOpacity(block = sticker, value = 0.88F)
    val opacity = engine.block.getOpacity(block = sticker)
    // highlight-android-opacity
    check(abs(opacity - 0.88F) < 0.001F)

    // highlight-android-drop-shadow
    require(engine.block.supportsDropShadow(block = sticker)) {
        "Sticker graphic blocks must support drop shadows."
    }
    engine.block.setDropShadowEnabled(block = sticker, enabled = true)
    engine.block.setDropShadowColor(
        block = sticker,
        color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0.4F),
    )
    engine.block.setDropShadowOffsetX(block = sticker, offsetX = 4F)
    engine.block.setDropShadowOffsetY(block = sticker, offsetY = 4F)
    engine.block.setDropShadowBlurRadiusX(block = sticker, blurRadiusX = 6F)
    engine.block.setDropShadowBlurRadiusY(block = sticker, blurRadiusY = 6F)

    val dropShadowEnabled = engine.block.isDropShadowEnabled(block = sticker)
    val dropShadowColor = engine.block.getDropShadowColor(block = sticker)
    val dropShadowOffsetX = engine.block.getDropShadowOffsetX(block = sticker)
    val dropShadowOffsetY = engine.block.getDropShadowOffsetY(block = sticker)
    val dropShadowBlurRadiusX = engine.block.getDropShadowBlurRadiusX(block = sticker)
    val dropShadowBlurRadiusY = engine.block.getDropShadowBlurRadiusY(block = sticker)
    // highlight-android-drop-shadow
    check(dropShadowEnabled)
    check(dropShadowOffsetX == 4F)
    check(dropShadowOffsetY == 4F)
    check(dropShadowBlurRadiusX == 6F)
    check(dropShadowBlurRadiusY == 6F)

    // highlight-android-duplicate
    val copy = engine.block.duplicate(block = sticker)
    engine.block.setPositionX(block = copy, value = 300F)
    engine.block.setPositionY(block = copy, value = 65F)

    val duplicateKind = engine.block.getKind(block = copy)
    val duplicatePositionX = engine.block.getPositionX(block = copy)
    val duplicatePositionY = engine.block.getPositionY(block = copy)
    // highlight-android-duplicate
    check(duplicateKind == "sticker")
    check(duplicatePositionX == 300F)
    check(duplicatePositionY == 65F)

    // highlight-android-stroke
    require(engine.block.supportsStroke(block = sticker)) {
        "Sticker graphic blocks must support strokes."
    }
    engine.block.setStrokeEnabled(block = sticker, enabled = true)
    engine.block.setStrokeColor(
        block = sticker,
        color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F),
    )
    engine.block.setStrokeWidth(block = sticker, width = 4F)

    val strokeEnabled = engine.block.isStrokeEnabled(block = sticker)
    val strokeColor = engine.block.getStrokeColor(block = sticker)
    val strokeWidth = engine.block.getStrokeWidth(block = sticker)
    // highlight-android-stroke
    check(strokeEnabled)
    check(strokeWidth == 4F)

    // highlight-android-blur
    require(engine.block.supportsBlur(block = sticker)) {
        "Sticker graphic blocks must support blur."
    }
    val blur = engine.block.createBlur(type = BlurType.Uniform)
    engine.block.setBlur(block = sticker, blurBlock = blur)
    engine.block.setBlurEnabled(block = sticker, enabled = true)
    val blurEnabled = engine.block.isBlurEnabled(block = sticker)
    // highlight-android-blur
    check(blurEnabled)

    // highlight-android-effects
    require(engine.block.supportsEffects(block = sticker)) {
        "Sticker graphic blocks must support effects."
    }
    val glow = engine.block.createEffect(type = EffectType.Glow)
    engine.block.appendEffect(block = sticker, effectBlock = glow)
    val effects = engine.block.getEffects(block = sticker)
    val effectEnabled = engine.block.isEffectEnabled(effectBlock = glow)
    // highlight-android-effects
    check(effects.contains(glow))
    check(effectEnabled)

    engine.block.forceLoadResources(listOf(page, sticker, copy))
    val previewPngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 900F, targetHeight = 500F),
    )
    check(previewPngData.hasRemaining()) { "edit stickers preview export is empty" }

    return EditStickersResult(
        currentImageUri = currentImageUri,
        positionX = positionX,
        positionY = positionY,
        width = width,
        height = height,
        rotation = rotation,
        isFlippedHorizontally = isFlippedHorizontally,
        coverMode = coverMode,
        finalContentFillMode = finalContentFillMode,
        opacity = opacity,
        dropShadowEnabled = dropShadowEnabled,
        dropShadowColor = dropShadowColor,
        dropShadowOffsetX = dropShadowOffsetX,
        dropShadowOffsetY = dropShadowOffsetY,
        dropShadowBlurRadiusX = dropShadowBlurRadiusX,
        dropShadowBlurRadiusY = dropShadowBlurRadiusY,
        duplicateKind = duplicateKind,
        duplicatePositionX = duplicatePositionX,
        duplicatePositionY = duplicatePositionY,
        strokeEnabled = strokeEnabled,
        strokeColor = strokeColor,
        strokeWidth = strokeWidth,
        blurEnabled = blurEnabled,
        effectCount = effects.size,
        effectEnabled = effectEnabled,
        previewPngData = previewPngData,
    )
}
