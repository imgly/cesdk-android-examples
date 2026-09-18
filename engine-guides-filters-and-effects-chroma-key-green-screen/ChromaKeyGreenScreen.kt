import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

private const val TAG = "ChromaKeyGuide"

suspend fun chromaKeyGreenScreen(
    engine: Engine,
    assetBaseUri: Uri,
): ChromaKeyGreenScreenResult = withContext(Dispatchers.Main) {
    // Demo scaffolding: a scene with one page, plus synthesized green-screen
    // footage — an astronaut sticker flattened onto a uniform green backdrop,
    // exported into an engine buffer — so the example has a keyable frame to work with.
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val backdrop = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(backdrop, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = backdrop, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(block = backdrop, color = Color.fromRGBA(r = 0F, g = 0.8F, b = 0.25F, a = 1F))
    engine.block.setWidth(backdrop, value = 800F)
    engine.block.setHeight(backdrop, value = 600F)
    engine.block.setPositionX(backdrop, value = 0F)
    engine.block.setPositionY(backdrop, value = 0F)
    engine.block.appendChild(parent = page, child = backdrop)

    val subject = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(subject, shape = engine.block.createShape(ShapeType.Rect))
    val subjectFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = subjectFill,
        property = "fill/image/imageFileURI",
        value = assetBaseUri.buildUpon()
            .appendPath("ly.img.sticker")
            .appendPath("images")
            .appendPath("3Dstickers")
            .appendPath("3d_stickers_astronaut.png")
            .build(),
    )
    engine.block.setFill(block = subject, fill = subjectFill)
    engine.block.setWidth(subject, value = 360F)
    engine.block.setHeight(subject, value = 400F)
    engine.block.setPositionX(subject, value = 220F)
    engine.block.setPositionY(subject, value = 130F)
    engine.block.appendChild(parent = page, child = subject)

    val frameData = engine.block.export(page, mimeType = MimeType.PNG)
    // Keep the buffer alive while the image fill references it.
    // Destroy it when the fill is no longer needed.
    val frameBufferUri = engine.editor.createBuffer()
    engine.editor.setBufferData(uri = frameBufferUri, offset = 0, data = frameData)
    engine.block.destroy(backdrop)
    engine.block.destroy(subject)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = frameBufferUri,
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.setWidth(imageBlock, value = 600F)
    engine.block.setHeight(imageBlock, value = 450F)
    engine.block.setPositionX(imageBlock, value = 100F)
    engine.block.setPositionY(imageBlock, value = 75F)
    engine.block.appendChild(parent = page, child = imageBlock)

    // highlight-android-apply-green-screen
    val greenScreenEffect = engine.block.createEffect(type = EffectType.GreenScreen)
    engine.block.appendEffect(block = imageBlock, effectBlock = greenScreenEffect)
    // highlight-android-apply-green-screen

    // highlight-android-set-key-color
    engine.block.setColor(
        greenScreenEffect,
        property = "effect/green_screen/fromColor",
        value = Color.fromRGBA(r = 0F, g = 0.8F, b = 0.25F, a = 1F),
    )
    // highlight-android-set-key-color

    // highlight-android-color-match
    engine.block.setFloat(greenScreenEffect, property = "effect/green_screen/colorMatch", value = 0.26F)
    // highlight-android-color-match

    // highlight-android-smoothness
    engine.block.setFloat(greenScreenEffect, property = "effect/green_screen/smoothness", value = 0.15F)
    // highlight-android-smoothness

    // highlight-android-spill
    engine.block.setFloat(greenScreenEffect, property = "effect/green_screen/spill", value = 0.4F)
    // highlight-android-spill

    val colorMatch = engine.block.getFloat(greenScreenEffect, property = "effect/green_screen/colorMatch")
    val smoothness = engine.block.getFloat(greenScreenEffect, property = "effect/green_screen/smoothness")
    val spill = engine.block.getFloat(greenScreenEffect, property = "effect/green_screen/spill")

    // A full-page background block with a solid color fill for the composite.
    val backgroundBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(backgroundBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = backgroundBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(block = backgroundBlock, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F))
    engine.block.setWidth(backgroundBlock, value = 800F)
    engine.block.setHeight(backgroundBlock, value = 600F)
    engine.block.setPositionX(backgroundBlock, value = 0F)
    engine.block.setPositionY(backgroundBlock, value = 0F)

    // highlight-android-composite-background
    engine.block.appendChild(parent = page, child = backgroundBlock)
    engine.block.sendToBack(backgroundBlock)
    engine.block.bringToFront(imageBlock)
    // highlight-android-composite-background

    val heroPng = engine.block.export(page, mimeType = MimeType.PNG)

    // highlight-android-check-enabled
    val isEnabled = engine.block.isEffectEnabled(greenScreenEffect)
    Log.i(TAG, "Green screen effect enabled: $isEnabled")
    // highlight-android-check-enabled

    // highlight-android-toggle-green-screen
    engine.block.setEffectEnabled(effectBlock = greenScreenEffect, enabled = !isEnabled)
    // highlight-android-toggle-green-screen

    val enabledAfterToggle = engine.block.isEffectEnabled(greenScreenEffect)

    // highlight-android-manage-green-screen
    val blockSupportsEffects = engine.block.supportsEffects(imageBlock)
    Log.i(TAG, "Block supports effects: $blockSupportsEffects")

    val effects = engine.block.getEffects(imageBlock)
    Log.i(TAG, "Number of effects: ${effects.size}")

    val effectIndex = effects.indexOf(greenScreenEffect)
    if (effectIndex >= 0) {
        engine.block.removeEffect(block = imageBlock, index = effectIndex)
    }
    engine.block.destroy(greenScreenEffect)
    // highlight-android-manage-green-screen

    val removed = engine.block.getEffects(imageBlock).none { effect -> effect == greenScreenEffect }

    ChromaKeyGreenScreenResult(
        blockSupportsEffects = blockSupportsEffects,
        colorMatch = colorMatch,
        smoothness = smoothness,
        spill = spill,
        enabledAfterToggle = enabledAfterToggle,
        removed = removed,
        heroPng = heroPng,
    )
}
