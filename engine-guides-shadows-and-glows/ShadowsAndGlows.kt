import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.Typeface

private const val TAG = "ShadowsAndGlows"

suspend fun shadowsAndGlows(engine: Engine): List<DesignBlock> = withContext(Dispatchers.Main) {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = textBlock, text = "Soft Shadow")
    val regularFont = Font(
        uri = Uri.parse("file:///android_asset/imgly-assets/ly.img.typeface/fonts/FiraSans/FiraSans-Regular.ttf"),
        subFamily = "Regular",
        weight = FontWeight.NORMAL,
        style = FontStyle.NORMAL,
    )
    val typeface = Typeface(name = "Fira Sans", fonts = listOf(regularFont))
    engine.block.setFont(
        block = textBlock,
        fontFileUri = regularFont.uri,
        typeface = typeface,
    )
    engine.block.setWidthMode(block = textBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(block = textBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(block = textBlock, value = 640F)
    engine.block.setHeight(block = textBlock, value = 96F)
    engine.block.setTextFontSize(block = textBlock, fontSize = 36F)
    engine.block.setTextColor(block = textBlock, color = Color.fromRGBA(r = 0.08F, g = 0.12F, b = 0.2F, a = 1F))
    engine.block.setPositionX(block = textBlock, value = 72F)
    engine.block.setPositionY(block = textBlock, value = 64F)
    engine.block.appendChild(parent = page, child = textBlock)

    val glowBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = glowBlock, shape = engine.block.createShape(ShapeType.Rect))
    val glowImageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = glowImageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = glowBlock, fill = glowImageFill)
    engine.block.setPositionX(block = glowBlock, value = 104F)
    engine.block.setPositionY(block = glowBlock, value = 210F)
    engine.block.setWidth(block = glowBlock, value = 220F)
    engine.block.setHeight(block = glowBlock, value = 180F)
    engine.block.appendChild(parent = page, child = glowBlock)

    val shapeBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = shapeBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = shapeBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = shapeBlock,
        color = Color.fromRGBA(r = 0.94F, g = 0.22F, b = 0.37F, a = 1F),
    )
    engine.block.setPositionX(block = shapeBlock, value = 440F)
    engine.block.setPositionY(block = shapeBlock, value = 210F)
    engine.block.setWidth(block = shapeBlock, value = 220F)
    engine.block.setHeight(block = shapeBlock, value = 180F)
    engine.block.appendChild(parent = page, child = shapeBlock)

    // highlight-android-check-drop-shadow-support
    val textSupportsDropShadow = engine.block.supportsDropShadow(textBlock)
    // highlight-android-check-drop-shadow-support

    // highlight-android-enable-drop-shadow
    if (textSupportsDropShadow) {
        engine.block.setDropShadowEnabled(block = textBlock, enabled = true)
    }
    // highlight-android-enable-drop-shadow

    // highlight-android-set-drop-shadow-color
    if (textSupportsDropShadow) {
        engine.block.setDropShadowColor(
            block = textBlock,
            color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0.45F),
        )
    }
    // highlight-android-set-drop-shadow-color

    // highlight-android-set-drop-shadow-offset
    if (textSupportsDropShadow) {
        engine.block.setDropShadowOffsetX(block = textBlock, offsetX = 12F)
        engine.block.setDropShadowOffsetY(block = textBlock, offsetY = 16F)
    }
    // highlight-android-set-drop-shadow-offset

    // highlight-android-set-drop-shadow-blur
    if (textSupportsDropShadow) {
        engine.block.setDropShadowBlurRadiusX(block = textBlock, blurRadiusX = 18F)
        engine.block.setDropShadowBlurRadiusY(block = textBlock, blurRadiusY = 18F)
    }
    // highlight-android-set-drop-shadow-blur

    // highlight-android-set-shape-shadow-clip
    if (engine.block.supportsDropShadow(shapeBlock)) {
        engine.block.setDropShadowEnabled(block = shapeBlock, enabled = true)
        engine.block.setDropShadowClip(block = shapeBlock, clip = true)
    }
    // highlight-android-set-shape-shadow-clip

    // highlight-android-check-glow-support
    val glowSupported = engine.block.supportsEffects(glowBlock)
    // highlight-android-check-glow-support

    // highlight-android-create-glow-effect
    val glowEffect = if (glowSupported) {
        engine.block.createEffect(type = EffectType.Glow).also { effect ->
            engine.block.appendEffect(block = glowBlock, effectBlock = effect)
        }
    } else {
        null
    }
    // highlight-android-create-glow-effect

    // highlight-android-configure-glow
    glowEffect?.let { effect ->
        engine.block.setFloat(block = effect, property = "effect/glow/size", value = 8F)
        engine.block.setFloat(block = effect, property = "effect/glow/amount", value = 0.7F)
        engine.block.setFloat(block = effect, property = "effect/glow/darkness", value = 0.25F)
    }
    // highlight-android-configure-glow

    // highlight-android-combine-shadow-glow
    if (engine.block.supportsDropShadow(shapeBlock)) {
        engine.block.setDropShadowEnabled(block = shapeBlock, enabled = true)
        engine.block.setDropShadowColor(
            block = shapeBlock,
            color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0.35F),
        )
        engine.block.setDropShadowOffsetX(block = shapeBlock, offsetX = 10F)
        engine.block.setDropShadowOffsetY(block = shapeBlock, offsetY = 14F)
        engine.block.setDropShadowBlurRadiusX(block = shapeBlock, blurRadiusX = 14F)
        engine.block.setDropShadowBlurRadiusY(block = shapeBlock, blurRadiusY = 14F)
    }

    if (engine.block.supportsEffects(shapeBlock)) {
        val combinedGlow = engine.block.createEffect(type = EffectType.Glow)
        engine.block.appendEffect(block = shapeBlock, effectBlock = combinedGlow)
        engine.block.setFloat(block = combinedGlow, property = "effect/glow/size", value = 6F)
        engine.block.setFloat(block = combinedGlow, property = "effect/glow/amount", value = 0.5F)
        engine.block.setFloat(block = combinedGlow, property = "effect/glow/darkness", value = 0.15F)
    }
    // highlight-android-combine-shadow-glow

    // highlight-android-toggle-shadow
    if (textSupportsDropShadow) {
        engine.block.setDropShadowEnabled(block = textBlock, enabled = false)
        val shadowIsDisabled = !engine.block.isDropShadowEnabled(textBlock)
        Log.i(TAG, "Drop shadow disabled: $shadowIsDisabled")
        engine.block.setDropShadowEnabled(block = textBlock, enabled = true)
    }
    // highlight-android-toggle-shadow

    // highlight-android-toggle-glow
    glowEffect?.let { effect ->
        engine.block.setEffectEnabled(effectBlock = effect, enabled = false)
        val glowIsDisabled = !engine.block.isEffectEnabled(effect)
        Log.i(TAG, "Glow disabled: $glowIsDisabled")
        engine.block.setEffectEnabled(effectBlock = effect, enabled = true)
    }
    // highlight-android-toggle-glow

    // highlight-android-remove-glow-effect
    glowEffect?.let { effect ->
        val glowIndex = engine.block.getEffects(glowBlock).indexOf(effect)

        require(glowIndex >= 0) { "The glow effect must be attached before it can be removed." }
        engine.block.removeEffect(block = glowBlock, index = glowIndex)
        engine.block.destroy(effect)
    }
    // highlight-android-remove-glow-effect

    if (glowSupported) {
        engine.block.createEffect(type = EffectType.Glow).also { effect ->
            engine.block.appendEffect(block = glowBlock, effectBlock = effect)
            engine.block.setFloat(block = effect, property = "effect/glow/size", value = 8F)
            engine.block.setFloat(block = effect, property = "effect/glow/amount", value = 0.7F)
            engine.block.setFloat(block = effect, property = "effect/glow/darkness", value = 0.25F)
        }
    }

    engine.block.forceLoadResources(listOf(textBlock, glowBlock, shapeBlock))

    listOf(textBlock, glowBlock, shapeBlock)
}
