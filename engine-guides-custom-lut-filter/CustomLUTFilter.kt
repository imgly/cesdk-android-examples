import android.net.Uri
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

data class CustomLUTFilterResult(
    val imageBlock: DesignBlock,
    val lutFilter: DesignBlock,
    val appliedEffects: List<DesignBlock>,
    val effectEnabled: Boolean,
)

suspend fun customLUTFilter(engine: Engine): CustomLUTFilterResult {
    // highlight-android-prepare-scene
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-prepare-scene

    // highlight-android-create-image-block
    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 100F)
    engine.block.setPositionY(imageBlock, value = 50F)
    engine.block.setWidth(imageBlock, value = 300F)
    engine.block.setHeight(imageBlock, value = 300F)
    engine.block.appendChild(parent = page, child = imageBlock)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(imageBlock, fill = imageFill)
    // highlight-android-create-image-block

    // highlight-android-create-effect
    val lutFilter = engine.block.createEffect(EffectType.LutFilter)
    // highlight-android-create-effect

    // highlight-android-configure-lut
    val bundledAssetBaseUri = "file:///android_asset/imgly-assets"
    val lutUri = Uri.parse(
        "$bundledAssetBaseUri/ly.img.filter.lut/LUTs/imgly_lut_ad1920_5_5_128.png",
    )

    engine.block.setUri(
        lutFilter,
        property = "effect/lut_filter/lutFileURI",
        value = lutUri,
    )
    engine.block.setInt(lutFilter, property = "effect/lut_filter/verticalTileCount", value = 5)
    engine.block.setInt(lutFilter, property = "effect/lut_filter/horizontalTileCount", value = 5)
    // highlight-android-configure-lut

    // highlight-android-set-intensity
    engine.block.setFloat(lutFilter, property = "effect/lut_filter/intensity", value = 0.9F)
    // highlight-android-set-intensity

    // highlight-android-apply-effect
    val supportsEffects = engine.block.supportsEffects(imageBlock)
    require(supportsEffects) { "The image block must support effects." }

    engine.block.appendEffect(block = imageBlock, effectBlock = lutFilter)
    val appliedEffects = engine.block.getEffects(imageBlock)
    // highlight-android-apply-effect

    check(appliedEffects == listOf(lutFilter))

    // highlight-android-toggle-effect
    engine.block.setEffectEnabled(effectBlock = lutFilter, enabled = false)
    val disabledState = engine.block.isEffectEnabled(lutFilter)

    engine.block.setEffectEnabled(effectBlock = lutFilter, enabled = true)
    val effectEnabled = engine.block.isEffectEnabled(lutFilter)
    // highlight-android-toggle-effect

    check(!disabledState)
    check(effectEnabled)

    return CustomLUTFilterResult(
        imageBlock = imageBlock,
        lutFilter = lutFilter,
        appliedEffects = appliedEffects,
        effectEnabled = effectEnabled,
    )
}

fun removeCustomLUTFilter(
    engine: Engine,
    imageBlock: DesignBlock,
    lutFilter: DesignBlock,
) {
    // highlight-android-remove-effect
    val lutFilterIndex = engine.block.getEffects(imageBlock).indexOf(lutFilter)
    require(lutFilterIndex >= 0) { "The LUT filter must be attached before it can be removed." }

    engine.block.removeEffect(block = imageBlock, index = lutFilterIndex)
    engine.block.destroy(lutFilter)
    // highlight-android-remove-effect
}
