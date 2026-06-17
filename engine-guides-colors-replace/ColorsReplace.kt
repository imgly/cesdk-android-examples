import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun colorsReplace(engine: Engine) {
    // highlight-android-prepare-scene
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-prepare-scene

    val imageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg")

    // highlight-android-create-image-blocks
    fun addImageBlock(
        x: Float,
        y: Float,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(block, value = x)
        engine.block.setPositionY(block, value = y)
        engine.block.setWidth(block, value = 200F)
        engine.block.setHeight(block, value = 150F)
        engine.block.appendChild(parent = page, child = block)

        val fill = engine.block.createFill(FillType.Image)
        engine.block.setUri(
            block = fill,
            property = "fill/image/imageFileURI",
            value = imageUri,
        )
        engine.block.setFill(block, fill = fill)

        return block
    }
    // highlight-android-create-image-blocks

    // highlight-android-create-recolor
    val recolorBlock = addImageBlock(x = 50F, y = 50F)
    val recolorEffect = engine.block.createEffect(type = EffectType.Recolor)
    engine.block.setColor(
        block = recolorEffect,
        property = "effect/recolor/fromColor",
        value = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
    )
    engine.block.setColor(
        block = recolorEffect,
        property = "effect/recolor/toColor",
        value = Color.fromRGBA(r = 0F, g = 0.5F, b = 1F, a = 1F),
    )
    engine.block.appendEffect(block = recolorBlock, effectBlock = recolorEffect)
    // highlight-android-create-recolor

    check(engine.block.getEffects(recolorBlock) == listOf(recolorEffect))

    // highlight-android-configure-recolor
    val tolerancesBlock = addImageBlock(x = 300F, y = 50F)
    val tolerancesEffect = engine.block.createEffect(type = EffectType.Recolor)

    engine.block.setColor(
        block = tolerancesEffect,
        property = "effect/recolor/fromColor",
        value = Color.fromRGBA(r = 0.8F, g = 0.6F, b = 0.4F, a = 1F),
    )
    engine.block.setColor(
        block = tolerancesEffect,
        property = "effect/recolor/toColor",
        value = Color.fromRGBA(r = 0.3F, g = 0.7F, b = 0.3F, a = 1F),
    )
    engine.block.setFloat(tolerancesEffect, property = "effect/recolor/colorMatch", value = 0.3F)
    engine.block.setFloat(tolerancesEffect, property = "effect/recolor/brightnessMatch", value = 0.2F)
    engine.block.setFloat(tolerancesEffect, property = "effect/recolor/smoothness", value = 0.1F)
    engine.block.appendEffect(block = tolerancesBlock, effectBlock = tolerancesEffect)
    // highlight-android-configure-recolor

    check(engine.block.getFloat(tolerancesEffect, property = "effect/recolor/colorMatch") == 0.3F)
    check(engine.block.getFloat(tolerancesEffect, property = "effect/recolor/brightnessMatch") == 0.2F)
    check(engine.block.getFloat(tolerancesEffect, property = "effect/recolor/smoothness") == 0.1F)

    // highlight-android-create-green-screen
    val greenScreenBlock = addImageBlock(x = 550F, y = 50F)
    val greenScreenEffect = engine.block.createEffect(type = EffectType.GreenScreen)
    engine.block.setColor(
        block = greenScreenEffect,
        property = "effect/green_screen/fromColor",
        value = Color.fromRGBA(r = 0F, g = 1F, b = 0F, a = 1F),
    )
    engine.block.appendEffect(block = greenScreenBlock, effectBlock = greenScreenEffect)
    // highlight-android-create-green-screen

    check(engine.block.getEffects(greenScreenBlock) == listOf(greenScreenEffect))

    // highlight-android-configure-green-screen
    val spillBlock = addImageBlock(x = 50F, y = 250F)
    val spillEffect = engine.block.createEffect(type = EffectType.GreenScreen)

    engine.block.setColor(
        block = spillEffect,
        property = "effect/green_screen/fromColor",
        value = Color.fromRGBA(r = 0.2F, g = 0.8F, b = 0.3F, a = 1F),
    )
    engine.block.setFloat(spillEffect, property = "effect/green_screen/colorMatch", value = 0.4F)
    engine.block.setFloat(spillEffect, property = "effect/green_screen/smoothness", value = 0.2F)
    engine.block.setFloat(spillEffect, property = "effect/green_screen/spill", value = 0.5F)
    engine.block.appendEffect(block = spillBlock, effectBlock = spillEffect)
    // highlight-android-configure-green-screen

    check(engine.block.getFloat(spillEffect, property = "effect/green_screen/colorMatch") == 0.4F)
    check(engine.block.getFloat(spillEffect, property = "effect/green_screen/smoothness") == 0.2F)
    check(engine.block.getFloat(spillEffect, property = "effect/green_screen/spill") == 0.5F)

    // highlight-android-manage-effects
    val stackedBlock = addImageBlock(x = 300F, y = 250F)
    val redToBlue = engine.block.createEffect(type = EffectType.Recolor)
    engine.block.setColor(
        block = redToBlue,
        property = "effect/recolor/fromColor",
        value = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
    )
    engine.block.setColor(
        block = redToBlue,
        property = "effect/recolor/toColor",
        value = Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F),
    )
    engine.block.appendEffect(block = stackedBlock, effectBlock = redToBlue)

    val stackedGreenScreen = engine.block.createEffect(type = EffectType.GreenScreen)
    engine.block.setColor(
        block = stackedGreenScreen,
        property = "effect/green_screen/fromColor",
        value = Color.fromRGBA(r = 0F, g = 1F, b = 0F, a = 1F),
    )
    engine.block.appendEffect(block = stackedBlock, effectBlock = stackedGreenScreen)

    val stackedEffects = engine.block.getEffects(stackedBlock)
    check(stackedEffects == listOf(redToBlue, stackedGreenScreen))

    engine.block.setEffectEnabled(effectBlock = stackedEffects[0], enabled = false)
    val isFirstEffectEnabled = engine.block.isEffectEnabled(stackedEffects[0])

    engine.block.removeEffect(block = stackedBlock, index = 1)
    engine.block.destroy(stackedGreenScreen)
    // highlight-android-manage-effects

    check(!isFirstEffectEnabled)
    check(engine.block.getEffects(stackedBlock) == listOf(redToBlue))

    val batchBlock = addImageBlock(x = 550F, y = 250F)

    // highlight-android-batch-processing
    val allGraphicBlocks = engine.block.findByType(type = DesignBlockType.Graphic)
    for (block in allGraphicBlocks) {
        if (engine.block.getEffects(block).isNotEmpty()) {
            continue
        }

        val batchRecolor = engine.block.createEffect(type = EffectType.Recolor)
        engine.block.setColor(
            block = batchRecolor,
            property = "effect/recolor/fromColor",
            value = Color.fromRGBA(r = 0.8F, g = 0.7F, b = 0.6F, a = 1F),
        )
        engine.block.setColor(
            block = batchRecolor,
            property = "effect/recolor/toColor",
            value = Color.fromRGBA(r = 0.6F, g = 0.7F, b = 0.9F, a = 1F),
        )
        engine.block.setFloat(batchRecolor, property = "effect/recolor/colorMatch", value = 0.25F)
        engine.block.appendEffect(block = block, effectBlock = batchRecolor)
    }
    // highlight-android-batch-processing

    check(engine.block.getEffects(batchBlock).size == 1)
}
