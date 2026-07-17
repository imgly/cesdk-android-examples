import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

private const val TAG = "DuotoneGuide"

suspend fun duotone(
    engine: Engine,
    assetBaseUri: Uri,
): DuotoneResult = withContext(Dispatchers.Main) {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1100F)
    engine.block.setHeight(page, value = 380F)
    engine.block.appendChild(parent = scene, child = page)

    val imageUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_1.jpg")
        .build()

    val presetImage = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(presetImage, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(presetImage, value = 340F)
    engine.block.setHeight(presetImage, value = 320F)
    engine.block.setPositionX(presetImage, value = 20F)
    engine.block.setPositionY(presetImage, value = 30F)
    val presetFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = presetFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )
    engine.block.setFill(block = presetImage, fill = presetFill)
    engine.block.appendChild(parent = page, child = presetImage)

    // highlight-android-check-support
    val canApplyEffects = engine.block.supportsEffects(presetImage)
    check(canApplyEffects) { "The image block must support effects." }
    // highlight-android-check-support

    // highlight-android-query-presets
    val filterSourceId = "ly.img.filter"
    if (filterSourceId !in engine.asset.findAllSources()) {
        val filterContentUri = assetBaseUri.buildUpon()
            .appendPath(filterSourceId)
            .appendPath("content.json")
            .build()
        engine.asset.addLocalSourceFromJSON(
            contentUri = filterContentUri,
            matcher = listOf("ly.img.filter.duotone.*"),
        )
    }

    val presetResult = engine.asset.findAssets(
        sourceId = filterSourceId,
        query = FindAssetsQuery(
            query = null,
            page = 0,
            groups = listOf("duotone"),
            perPage = 10,
        ),
    )
    val duotonePresets = presetResult.assets
    // highlight-android-query-presets

    // highlight-android-create-effect
    val presetEffect = engine.block.createEffect(type = EffectType.DuoToneFilter)
    // highlight-android-create-effect

    // highlight-android-apply-preset
    val preset = checkNotNull(duotonePresets.firstOrNull()) {
        "The filter source did not return any duotone presets."
    }
    val darkHex = checkNotNull(preset.meta?.get("darkColor")) {
        "The selected duotone preset is missing darkColor."
    }
    val lightHex = checkNotNull(preset.meta?.get("lightColor")) {
        "The selected duotone preset is missing lightColor."
    }
    engine.block.setColor(
        block = presetEffect,
        property = "effect/duotone_filter/darkColor",
        value = Color.fromHex(darkHex),
    )
    engine.block.setColor(
        block = presetEffect,
        property = "effect/duotone_filter/lightColor",
        value = Color.fromHex(lightHex),
    )
    engine.block.setFloat(
        block = presetEffect,
        property = "effect/duotone_filter/intensity",
        value = 0.9F,
    )
    // highlight-android-apply-preset

    // highlight-android-append-preset
    engine.block.appendEffect(block = presetImage, effectBlock = presetEffect)
    // highlight-android-append-preset

    val customImage = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(customImage, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(customImage, value = 340F)
    engine.block.setHeight(customImage, value = 320F)
    engine.block.setPositionX(customImage, value = 380F)
    engine.block.setPositionY(customImage, value = 30F)
    val customFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = customFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )
    engine.block.setFill(block = customImage, fill = customFill)
    engine.block.appendChild(parent = page, child = customImage)

    // highlight-android-custom-colors
    val customEffect = engine.block.createEffect(type = EffectType.DuoToneFilter)
    // Dark color maps to shadows, light color maps to highlights.
    engine.block.setColor(
        block = customEffect,
        property = "effect/duotone_filter/darkColor",
        value = Color.fromRGBA(r = 0.1F, g = 0.15F, b = 0.3F, a = 1.0F),
    )
    engine.block.setColor(
        block = customEffect,
        property = "effect/duotone_filter/lightColor",
        value = Color.fromRGBA(r = 0.95F, g = 0.9F, b = 0.8F, a = 1.0F),
    )
    engine.block.setFloat(
        block = customEffect,
        property = "effect/duotone_filter/intensity",
        value = 0.85F,
    )
    engine.block.appendEffect(block = customImage, effectBlock = customEffect)
    // highlight-android-custom-colors

    val combinedImage = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(combinedImage, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(combinedImage, value = 340F)
    engine.block.setHeight(combinedImage, value = 320F)
    engine.block.setPositionX(combinedImage, value = 740F)
    engine.block.setPositionY(combinedImage, value = 30F)
    val combinedFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = combinedFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )
    engine.block.setFill(block = combinedImage, fill = combinedFill)
    engine.block.appendChild(parent = page, child = combinedImage)

    // highlight-android-combine-effects
    // Adjustments run first, then duotone maps the adjusted tones.
    val adjustments = engine.block.createEffect(type = EffectType.Adjustments)
    engine.block.setFloat(adjustments, property = "effect/adjustments/brightness", value = 0.1F)
    engine.block.setFloat(adjustments, property = "effect/adjustments/contrast", value = 0.15F)
    engine.block.appendEffect(block = combinedImage, effectBlock = adjustments)

    val combinedDuotone = engine.block.createEffect(type = EffectType.DuoToneFilter)
    engine.block.setColor(
        block = combinedDuotone,
        property = "effect/duotone_filter/darkColor",
        value = Color.fromRGBA(r = 0.2F, g = 0.1F, b = 0.3F, a = 1.0F),
    )
    engine.block.setColor(
        block = combinedDuotone,
        property = "effect/duotone_filter/lightColor",
        value = Color.fromRGBA(r = 1.0F, g = 0.85F, b = 0.7F, a = 1.0F),
    )
    engine.block.setFloat(
        block = combinedDuotone,
        property = "effect/duotone_filter/intensity",
        value = 0.75F,
    )
    engine.block.appendEffect(block = combinedImage, effectBlock = combinedDuotone)
    // highlight-android-combine-effects

    // Ensure remote image fills are ready before exporting the preview for smoke validation.
    engine.block.forceLoadResources(listOf(presetImage, customImage, combinedImage))
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    val presetIntensity = engine.block.getFloat(
        block = presetEffect,
        property = "effect/duotone_filter/intensity",
    )
    val combinedEffectsCount = engine.block.getEffects(combinedImage).size

    // highlight-android-list-effects
    val appliedEffects = engine.block.getEffects(presetImage)
    Log.i(TAG, "Image has ${appliedEffects.size} effect(s) applied")
    // highlight-android-list-effects

    var disabledPresetEffectEnabled = true
    var restoredPresetEffectEnabled = false

    // highlight-android-toggle-effects
    appliedEffects.firstOrNull()?.let { firstEffect ->
        engine.block.setEffectEnabled(effectBlock = firstEffect, enabled = false)
        disabledPresetEffectEnabled = engine.block.isEffectEnabled(firstEffect)
        Log.i(TAG, "Effect enabled: $disabledPresetEffectEnabled")
        engine.block.setEffectEnabled(effectBlock = firstEffect, enabled = true)
        restoredPresetEffectEnabled = engine.block.isEffectEnabled(firstEffect)
    }
    // highlight-android-toggle-effects

    // highlight-android-remove-effect
    val customEffects = engine.block.getEffects(customImage)
    val customEffectsBeforeRemoval = customEffects.size
    customEffects.firstOrNull()?.let { effectToRemove ->
        // Detach the effect from the block's stack, then destroy it to free resources.
        engine.block.removeEffect(block = customImage, index = 0)
        engine.block.destroy(effectToRemove)
    }
    // highlight-android-remove-effect

    val customEffectsAfterRemoval = engine.block.getEffects(customImage).size

    DuotoneResult(
        presetCount = duotonePresets.size,
        presetEffectsCount = appliedEffects.size,
        presetIntensity = presetIntensity,
        disabledPresetEffectEnabled = disabledPresetEffectEnabled,
        restoredPresetEffectEnabled = restoredPresetEffectEnabled,
        customEffectsBeforeRemoval = customEffectsBeforeRemoval,
        customEffectsAfterRemoval = customEffectsAfterRemoval,
        combinedEffectsCount = combinedEffectsCount,
        previewPng = previewPng,
    )
}
