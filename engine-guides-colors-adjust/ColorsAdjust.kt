import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.math.abs

data class ColorsAdjust(
    val brightness: Float,
    val contrast: Float,
    val saturation: Float,
    val propertyCount: Int,
    val disabledState: Boolean,
    val enabledState: Boolean,
    val moodySaturation: Float,
    val orderedStackMatches: Boolean,
    val sharpness: Float,
    val resetSucceeded: Boolean,
    val removed: Boolean,
)

suspend fun colorsAdjust(engine: Engine): ColorsAdjust {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageGraphicBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageGraphicBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageGraphicBlock, value = 100F)
    engine.block.setPositionY(imageGraphicBlock, value = 50F)
    engine.block.setWidth(imageGraphicBlock, value = 300F)
    engine.block.setHeight(imageGraphicBlock, value = 300F)
    engine.block.appendChild(parent = page, child = imageGraphicBlock)

    val fill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = fill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(imageGraphicBlock, fill = fill)

    // highlight-android-check-support
    val sceneSupportsEffects = engine.block.supportsEffects(scene)
    val pageSupportsEffects = engine.block.supportsEffects(page)
    val imageGraphicSupportsEffects = engine.block.supportsEffects(imageGraphicBlock)

    require(!sceneSupportsEffects) { "Scenes do not support effect stacks." }
    require(pageSupportsEffects) { "Pages can expose effect stacks." }
    require(imageGraphicSupportsEffects) { "Image-backed graphic blocks can render adjustments." }
    // highlight-android-check-support

    // highlight-android-create-adjustments
    val adjustmentsEffect = engine.block.createEffect(type = EffectType.Adjustments)
    engine.block.appendEffect(block = imageGraphicBlock, effectBlock = adjustmentsEffect)
    // highlight-android-create-adjustments

    check(engine.block.getEffects(imageGraphicBlock).contains(adjustmentsEffect))

    // highlight-android-set-properties
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/brightness", value = 0.2F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/contrast", value = 0.15F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/saturation", value = 0.3F)
    // highlight-android-set-properties

    // highlight-android-read-values
    val brightness = engine.block.getFloat(adjustmentsEffect, property = "effect/adjustments/brightness")
    val contrast = engine.block.getFloat(adjustmentsEffect, property = "effect/adjustments/contrast")
    val saturation = engine.block.getFloat(adjustmentsEffect, property = "effect/adjustments/saturation")
    val availableProperties = engine.block.findAllProperties(adjustmentsEffect)
    // highlight-android-read-values

    check(abs(brightness - 0.2F) < 0.0001F)
    check(abs(contrast - 0.15F) < 0.0001F)
    check(abs(saturation - 0.3F) < 0.0001F)
    check(availableProperties.any { it.startsWith("effect/adjustments/") })

    // highlight-android-enable-disable
    engine.block.setEffectEnabled(effectBlock = adjustmentsEffect, enabled = false)
    val disabledState = engine.block.isEffectEnabled(adjustmentsEffect)

    engine.block.setEffectEnabled(effectBlock = adjustmentsEffect, enabled = true)
    val enabledState = engine.block.isEffectEnabled(adjustmentsEffect)
    // highlight-android-enable-disable

    check(!disabledState)
    check(enabledState)

    // highlight-android-combine-effects
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/brightness", value = -0.1F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/contrast", value = 0.35F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/saturation", value = -0.25F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/temperature", value = -0.2F)
    // highlight-android-combine-effects

    val moodySaturation = engine.block.getFloat(adjustmentsEffect, property = "effect/adjustments/saturation")
    check(abs(moodySaturation - -0.25F) < 0.0001F)

    // highlight-android-stack-order
    val pixelizeEffect = engine.block.createEffect(type = EffectType.Pixelize)
    engine.block.insertEffect(block = imageGraphicBlock, effectBlock = pixelizeEffect, index = 1)

    val orderedEffects = engine.block.getEffects(imageGraphicBlock)
    // highlight-android-stack-order

    val orderedStackMatches = orderedEffects == listOf(adjustmentsEffect, pixelizeEffect)
    check(orderedStackMatches)

    // highlight-android-refinement-adjustments
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/sharpness", value = 0.3F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/clarity", value = 0.25F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/highlights", value = -0.15F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/shadows", value = 0.2F)
    // highlight-android-refinement-adjustments

    val sharpness = engine.block.getFloat(adjustmentsEffect, property = "effect/adjustments/sharpness")
    check(abs(sharpness - 0.3F) < 0.0001F)

    // highlight-android-reset-adjustments
    val adjustmentProperties = engine.block
        .findAllProperties(adjustmentsEffect)
        .filter { it.startsWith("effect/adjustments/") }
    check(adjustmentProperties.isNotEmpty())

    adjustmentProperties.forEach { property ->
        engine.block.setFloat(adjustmentsEffect, property = property, value = 0F)
    }
    // highlight-android-reset-adjustments

    val resetSucceeded = adjustmentProperties.all { property ->
        abs(engine.block.getFloat(adjustmentsEffect, property = property)) < 0.0001F
    }
    check(resetSucceeded)

    // highlight-android-remove-adjustments
    val effects = engine.block.getEffects(imageGraphicBlock)
    val adjustmentIndex = effects.indexOf(adjustmentsEffect)

    require(adjustmentIndex >= 0) { "The adjustments effect must be attached before it can be removed." }
    engine.block.removeEffect(block = imageGraphicBlock, index = adjustmentIndex)
    engine.block.destroy(adjustmentsEffect)
    // highlight-android-remove-adjustments

    val removed = engine.block.getEffects(imageGraphicBlock).none { it == adjustmentsEffect }
    check(removed)

    val pixelizeIndex = engine.block.getEffects(imageGraphicBlock).indexOf(pixelizeEffect)
    if (pixelizeIndex >= 0) {
        engine.block.removeEffect(block = imageGraphicBlock, index = pixelizeIndex)
    }
    engine.block.destroy(pixelizeEffect)

    return ColorsAdjust(
        brightness = brightness,
        contrast = contrast,
        saturation = saturation,
        propertyCount = adjustmentProperties.size,
        disabledState = disabledState,
        enabledState = enabledState,
        moodySaturation = moodySaturation,
        orderedStackMatches = orderedStackMatches,
        sharpness = sharpness,
        resetSucceeded = resetSucceeded,
        removed = removed,
    )
}
