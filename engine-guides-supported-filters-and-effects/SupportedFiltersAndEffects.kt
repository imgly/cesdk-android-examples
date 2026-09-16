import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

data class SupportedFiltersAndEffects(
    val sceneSupportsEffects: Boolean,
    val imageBlockSupportsEffects: Boolean,
    val appliedEffectCount: Int,
    val effectType: String,
    val intensity: Float,
    val darkColor: Color,
    val lightColor: Color,
    val previewPng: ByteBuffer,
)

suspend fun supportedFiltersAndEffects(engine: Engine): SupportedFiltersAndEffects {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 100F)
    engine.block.setPositionY(imageBlock, value = 75F)
    engine.block.setWidth(imageBlock, value = 600F)
    engine.block.setHeight(imageBlock, value = 450F)
    engine.block.appendChild(parent = page, child = imageBlock)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)

    // highlight-android-check-effect-support
    val sceneSupportsEffects = engine.block.supportsEffects(scene)
    val imageBlockSupportsEffects = engine.block.supportsEffects(imageBlock)

    require(!sceneSupportsEffects) { "Scenes do not support effect stacks." }
    require(imageBlockSupportsEffects) { "Image-backed graphic blocks can render effects." }
    // highlight-android-check-effect-support

    // highlight-android-add-effect
    val duotoneEffect = engine.block.createEffect(type = EffectType.DuoToneFilter)
    engine.block.appendEffect(block = imageBlock, effectBlock = duotoneEffect)
    // highlight-android-add-effect

    // highlight-android-configure-effect
    val darkColor = Color.fromRGBA(r = 0.02F, g = 0.04F, b = 0.12F, a = 1F)
    val lightColor = Color.fromRGBA(r = 0.5F, g = 0.7F, b = 1F, a = 1F)

    engine.block.setColor(
        block = duotoneEffect,
        property = "effect/duotone_filter/darkColor",
        value = darkColor,
    )
    engine.block.setColor(
        block = duotoneEffect,
        property = "effect/duotone_filter/lightColor",
        value = lightColor,
    )
    engine.block.setFloat(
        block = duotoneEffect,
        property = "effect/duotone_filter/intensity",
        value = 0.8F,
    )
    // highlight-android-configure-effect

    // highlight-android-retrieve-effects
    val appliedEffects = engine.block.getEffects(imageBlock)
    require(appliedEffects == listOf(duotoneEffect)) {
        "Expected one duotone effect on the image block."
    }
    // highlight-android-retrieve-effects

    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    check(previewPng.hasRemaining()) { "The supported filters and effects preview export is empty." }

    return SupportedFiltersAndEffects(
        sceneSupportsEffects = sceneSupportsEffects,
        imageBlockSupportsEffects = imageBlockSupportsEffects,
        appliedEffectCount = appliedEffects.size,
        effectType = engine.block.getType(duotoneEffect),
        intensity = engine.block.getFloat(duotoneEffect, property = "effect/duotone_filter/intensity"),
        darkColor = engine.block.getColor(duotoneEffect, property = "effect/duotone_filter/darkColor"),
        lightColor = engine.block.getColor(duotoneEffect, property = "effect/duotone_filter/lightColor"),
        previewPng = previewPng.asReadOnlyBuffer(),
    )
}
