import ly.img.editor.defaultBaseUri
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer
import kotlin.math.abs

private val sampleImageUri = defaultBaseUri.buildUpon()
    .appendPath("ly.img.image")
    .appendPath("images")
    .appendPath("sample_1.jpg")
    .build()
private val sampleLutUri = defaultBaseUri.buildUpon()
    .appendPath("ly.img.filter.lut")
    .appendPath("LUTs")
    .appendPath("imgly_lut_ad1920_5_5_128.png")
    .build()
    .toString()

data class ApplyFiltersAndEffects(
    val sceneSupportsEffects: Boolean,
    val pageSupportsEffects: Boolean,
    val imageSupportsEffects: Boolean,
    val orderedStackMatches: Boolean,
    val pixelizePropertyCount: Int,
    val adjustmentPropertyCount: Int,
    val brightness: Float,
    val horizontalPixelSize: Int,
    val lutUri: String,
    val lutIntensity: Float,
    val duotoneIntensity: Float,
    val combinedStackMatches: Boolean,
    val queriedStackSize: Int,
    val disabledState: Boolean,
    val enabledState: Boolean,
    val removed: Boolean,
    val batchEffectCount: Int,
    val presetSaturation: Float,
    val exportedPage: ByteBuffer,
)

suspend fun applyFiltersAndEffects(engine: Engine): ApplyFiltersAndEffects {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1200F)
    engine.block.setHeight(page, value = 900F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = createImageGraphicBlock(engine, page, x = 70F, y = 70F)
    val lutBlock = createImageGraphicBlock(engine, page, x = 430F, y = 70F)
    val duotoneBlock = createImageGraphicBlock(engine, page, x = 790F, y = 70F)
    val combinedBlock = createImageGraphicBlock(engine, page, x = 70F, y = 430F)
    val presetBlock = createImageGraphicBlock(engine, page, x = 430F, y = 430F)
    val batchBlock = createImageGraphicBlock(engine, page, x = 790F, y = 430F)

    // highlight-android-check-effect-support
    val sceneSupportsEffects = engine.block.supportsEffects(scene)
    val pageSupportsEffects = engine.block.supportsEffects(page)
    val imageSupportsEffects = engine.block.supportsEffects(imageBlock)

    require(!sceneSupportsEffects) { "Scene blocks do not render effect stacks." }
    require(pageSupportsEffects) { "Pages can render effects." }
    require(imageSupportsEffects) { "Image-backed graphic blocks can render effects." }
    // highlight-android-check-effect-support

    // highlight-android-create-effects
    val pixelizeEffect = engine.block.createEffect(type = EffectType.Pixelize)
    val adjustmentsEffect = engine.block.createEffect(type = EffectType.Adjustments)
    // highlight-android-create-effects

    // highlight-android-add-effects
    engine.block.appendEffect(block = imageBlock, effectBlock = pixelizeEffect)
    engine.block.insertEffect(block = imageBlock, effectBlock = adjustmentsEffect, index = 0)

    val orderedEffects = engine.block.getEffects(imageBlock)
    // highlight-android-add-effects

    val orderedStackMatches = orderedEffects == listOf(adjustmentsEffect, pixelizeEffect)
    check(orderedStackMatches)

    // highlight-android-configure-effect-properties
    val pixelizeProperties = engine.block.findAllProperties(pixelizeEffect)
    val adjustmentProperties = engine.block.findAllProperties(adjustmentsEffect)

    engine.block.setInt(pixelizeEffect, property = "effect/pixelize/horizontalPixelSize", value = 18)
    engine.block.setInt(pixelizeEffect, property = "effect/pixelize/verticalPixelSize", value = 18)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/brightness", value = 0.18F)
    engine.block.setFloat(adjustmentsEffect, property = "effect/adjustments/contrast", value = 0.22F)

    val brightness = engine.block.getFloat(adjustmentsEffect, property = "effect/adjustments/brightness")
    val horizontalPixelSize = engine.block.getInt(pixelizeEffect, property = "effect/pixelize/horizontalPixelSize")
    // highlight-android-configure-effect-properties

    check(pixelizeProperties.contains("effect/pixelize/horizontalPixelSize"))
    check(adjustmentProperties.contains("effect/adjustments/brightness"))
    check(abs(brightness - 0.18F) < 0.0001F)
    check(horizontalPixelSize == 18)

    // highlight-android-apply-lut-filter
    val lutFilter = engine.block.createEffect(type = EffectType.LutFilter)

    engine.block.setString(lutFilter, property = "effect/lut_filter/lutFileURI", value = sampleLutUri)
    engine.block.setInt(lutFilter, property = "effect/lut_filter/horizontalTileCount", value = 5)
    engine.block.setInt(lutFilter, property = "effect/lut_filter/verticalTileCount", value = 5)
    engine.block.setFloat(lutFilter, property = "effect/lut_filter/intensity", value = 0.85F)
    engine.block.appendEffect(block = lutBlock, effectBlock = lutFilter)
    // highlight-android-apply-lut-filter

    val lutUri = engine.block.getString(lutFilter, property = "effect/lut_filter/lutFileURI")
    val lutIntensity = engine.block.getFloat(lutFilter, property = "effect/lut_filter/intensity")
    check(lutUri == sampleLutUri)
    check(abs(lutIntensity - 0.85F) < 0.0001F)

    // highlight-android-apply-duotone-filter
    val duotoneFilter = engine.block.createEffect(type = EffectType.DuoToneFilter)

    engine.block.setColor(
        duotoneFilter,
        property = "effect/duotone_filter/darkColor",
        value = Color.fromRGBA(r = 0.08F, g = 0.12F, b = 0.22F, a = 1F),
    )
    engine.block.setColor(
        duotoneFilter,
        property = "effect/duotone_filter/lightColor",
        value = Color.fromRGBA(r = 0.96F, g = 0.76F, b = 0.34F, a = 1F),
    )
    engine.block.setFloat(duotoneFilter, property = "effect/duotone_filter/intensity", value = 0.65F)
    engine.block.appendEffect(block = duotoneBlock, effectBlock = duotoneFilter)
    // highlight-android-apply-duotone-filter

    val duotoneIntensity = engine.block.getFloat(duotoneFilter, property = "effect/duotone_filter/intensity")
    check(abs(duotoneIntensity - 0.65F) < 0.0001F)

    // highlight-android-combine-effects
    val combinedAdjustments = engine.block.createEffect(type = EffectType.Adjustments)
    engine.block.setFloat(combinedAdjustments, property = "effect/adjustments/saturation", value = -0.35F)

    val combinedDuotone = engine.block.createEffect(type = EffectType.DuoToneFilter)
    engine.block.setColor(
        combinedDuotone,
        property = "effect/duotone_filter/darkColor",
        value = Color.fromRGBA(r = 0.1F, g = 0.18F, b = 0.3F, a = 1F),
    )
    engine.block.setColor(
        combinedDuotone,
        property = "effect/duotone_filter/lightColor",
        value = Color.fromRGBA(r = 0.9F, g = 0.55F, b = 0.22F, a = 1F),
    )
    engine.block.setFloat(combinedDuotone, property = "effect/duotone_filter/intensity", value = 0.5F)

    val combinedPixelize = engine.block.createEffect(type = EffectType.Pixelize)
    engine.block.setInt(combinedPixelize, property = "effect/pixelize/horizontalPixelSize", value = 12)
    engine.block.setInt(combinedPixelize, property = "effect/pixelize/verticalPixelSize", value = 12)

    engine.block.appendEffect(block = combinedBlock, effectBlock = combinedDuotone)
    engine.block.appendEffect(block = combinedBlock, effectBlock = combinedPixelize)
    engine.block.insertEffect(block = combinedBlock, effectBlock = combinedAdjustments, index = 0)

    val combinedEffects = engine.block.getEffects(combinedBlock)
    // highlight-android-combine-effects

    val combinedStackMatches = combinedEffects == listOf(
        combinedAdjustments,
        combinedDuotone,
        combinedPixelize,
    )
    check(combinedStackMatches)

    // highlight-android-query-effects
    val currentEffects = engine.block.getEffects(imageBlock)
    val currentAdjustmentProperties = engine.block.findAllProperties(adjustmentsEffect)
    // highlight-android-query-effects

    check(currentEffects == orderedEffects)
    check(currentAdjustmentProperties.contains("effect/adjustments/contrast"))

    // highlight-android-toggle-effects
    engine.block.setEffectEnabled(effectBlock = pixelizeEffect, enabled = false)
    val disabledState = engine.block.isEffectEnabled(pixelizeEffect)

    engine.block.setEffectEnabled(effectBlock = pixelizeEffect, enabled = true)
    val enabledState = engine.block.isEffectEnabled(pixelizeEffect)
    // highlight-android-toggle-effects

    check(!disabledState)
    check(enabledState)

    // highlight-android-remove-effects
    val removableIndex = engine.block.getEffects(imageBlock).indexOf(pixelizeEffect)
    require(removableIndex >= 0) { "The pixelize effect must be attached before removal." }

    engine.block.removeEffect(block = imageBlock, index = removableIndex)
    engine.block.destroy(pixelizeEffect)

    val removed = engine.block.getEffects(imageBlock).none { effect -> effect == pixelizeEffect }
    // highlight-android-remove-effects

    check(removed)

    // highlight-android-batch-processing
    val batchTargets = listOf(imageBlock, lutBlock, duotoneBlock, batchBlock)
    val batchEffects = mutableListOf<DesignBlock>()

    for (targetBlock in batchTargets) {
        if (engine.block.supportsEffects(targetBlock)) {
            val batchAdjustment = engine.block.createEffect(type = EffectType.Adjustments)
            engine.block.setFloat(batchAdjustment, property = "effect/adjustments/brightness", value = 0.08F)
            engine.block.appendEffect(block = targetBlock, effectBlock = batchAdjustment)
            batchEffects += batchAdjustment
        }
    }
    // highlight-android-batch-processing

    check(batchEffects.size == batchTargets.size)

    // highlight-android-reusable-preset
    val presetValues = mapOf(
        "effect/adjustments/brightness" to -0.08F,
        "effect/adjustments/contrast" to 0.3F,
        "effect/adjustments/saturation" to -0.2F,
    )

    val presetAdjustment = engine.block.createEffect(type = EffectType.Adjustments)
    for ((property, value) in presetValues) {
        engine.block.setFloat(presetAdjustment, property = property, value = value)
    }
    engine.block.appendEffect(block = presetBlock, effectBlock = presetAdjustment)
    // highlight-android-reusable-preset

    val presetSaturation = engine.block.getFloat(presetAdjustment, property = "effect/adjustments/saturation")
    check(abs(presetSaturation - -0.2F) < 0.0001F)

    val exportedPage = engine.block.export(page, mimeType = MimeType.PNG)

    return ApplyFiltersAndEffects(
        sceneSupportsEffects = sceneSupportsEffects,
        pageSupportsEffects = pageSupportsEffects,
        imageSupportsEffects = imageSupportsEffects,
        orderedStackMatches = orderedStackMatches,
        pixelizePropertyCount = pixelizeProperties.size,
        adjustmentPropertyCount = adjustmentProperties.size,
        brightness = brightness,
        horizontalPixelSize = horizontalPixelSize,
        lutUri = lutUri,
        lutIntensity = lutIntensity,
        duotoneIntensity = duotoneIntensity,
        combinedStackMatches = combinedStackMatches,
        queriedStackSize = currentEffects.size,
        disabledState = disabledState,
        enabledState = enabledState,
        removed = removed,
        batchEffectCount = batchEffects.size,
        presetSaturation = presetSaturation,
        exportedPage = exportedPage,
    )
}

private fun createImageGraphicBlock(
    engine: Engine,
    page: DesignBlock,
    x: Float,
    y: Float,
): DesignBlock {
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block, value = x)
    engine.block.setPositionY(block, value = y)
    engine.block.setWidth(block, value = 310F)
    engine.block.setHeight(block, value = 310F)
    engine.block.appendChild(parent = page, child = block)

    val fill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = fill,
        property = "fill/image/imageFileURI",
        value = sampleImageUri,
    )
    engine.block.setFill(block = block, fill = fill)

    return block
}
