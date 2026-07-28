import android.net.Uri
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

data class Distortion(
    val liquidAmount: Float,
    val mirrorSide: Int,
    val shifterAmount: Float,
    val radialPixelRadius: Float,
    val tvGlitchDistortion: Float,
    val combinedEffectCount: Int,
    val disabledState: Boolean,
    val removed: Boolean,
    val liquidProperties: List<String>,
    val previewPng: ByteBuffer,
)

suspend fun distortion(engine: Engine): Distortion {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1180F)
    engine.block.setHeight(page, value = 620F)
    engine.block.appendChild(parent = scene, child = page)

    val imageCells =
        listOf(
            30F to 30F,
            410F to 30F,
            790F to 30F,
            30F to 320F,
            410F to 320F,
            790F to 320F,
        ).map { (x, y) ->
            val cell = engine.block.create(DesignBlockType.Graphic)
            engine.block.setShape(cell, shape = engine.block.createShape(ShapeType.Rect))
            engine.block.setWidth(cell, value = 360F)
            engine.block.setHeight(cell, value = 270F)
            engine.block.setPositionX(cell, value = x)
            engine.block.setPositionY(cell, value = y)

            val fill = engine.block.createFill(FillType.Image)
            engine.block.setUri(
                block = fill,
                property = "fill/image/imageFileURI",
                value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
            )
            engine.block.setFill(cell, fill = fill)
            engine.block.setContentFillMode(block = cell, mode = ContentFillMode.COVER)
            engine.block.appendChild(parent = page, child = cell)

            cell
        }

    val liquidBlock = imageCells[1]
    val mirrorBlock = imageCells[2]
    val shifterBlock = imageCells[3]
    val radialPixelBlock = imageCells[4]
    val tvGlitchBlock = imageCells[5]

    // highlight-android-liquid-effect
    require(engine.block.supportsEffects(liquidBlock)) {
        "Image-backed graphic blocks can render distortion effects."
    }
    val liquid = engine.block.createEffect(type = EffectType.Liquid)
    engine.block.setFloat(liquid, property = "effect/liquid/amount", value = 0.5F)
    engine.block.setFloat(liquid, property = "effect/liquid/scale", value = 1.0F)
    engine.block.appendEffect(block = liquidBlock, effectBlock = liquid)
    // highlight-android-liquid-effect

    val liquidAmount = engine.block.getFloat(liquid, property = "effect/liquid/amount")
    check(liquidAmount == 0.5F)

    // highlight-android-mirror-effect
    val mirror = engine.block.createEffect(type = EffectType.Mirror)
    engine.block.setInt(mirror, property = "effect/mirror/side", value = 0)
    engine.block.appendEffect(block = mirrorBlock, effectBlock = mirror)
    // highlight-android-mirror-effect

    val mirrorSide = engine.block.getInt(mirror, property = "effect/mirror/side")
    check(mirrorSide == 0)

    // highlight-android-shifter-effect
    val shifter = engine.block.createEffect(type = EffectType.Shifter)
    engine.block.setFloat(shifter, property = "effect/shifter/amount", value = 0.3F)
    engine.block.setFloat(shifter, property = "effect/shifter/angle", value = 0.785F)
    engine.block.appendEffect(block = shifterBlock, effectBlock = shifter)
    // highlight-android-shifter-effect

    val shifterAmount = engine.block.getFloat(shifter, property = "effect/shifter/amount")
    check(shifterAmount == 0.3F)

    // highlight-android-radial-pixel-effect
    val radialPixel = engine.block.createEffect(type = EffectType.RadialPixel)
    engine.block.setFloat(radialPixel, property = "effect/radial_pixel/radius", value = 0.5F)
    engine.block.setFloat(radialPixel, property = "effect/radial_pixel/segments", value = 0.5F)
    engine.block.appendEffect(block = radialPixelBlock, effectBlock = radialPixel)
    // highlight-android-radial-pixel-effect

    val radialPixelRadius = engine.block.getFloat(radialPixel, property = "effect/radial_pixel/radius")
    check(radialPixelRadius == 0.5F)

    // highlight-android-tv-glitch-effect
    val tvGlitch = engine.block.createEffect(type = EffectType.TvGlitch)
    engine.block.setFloat(tvGlitch, property = "effect/tv_glitch/distortion", value = 0.4F)
    engine.block.setFloat(tvGlitch, property = "effect/tv_glitch/distortion2", value = 0.2F)
    engine.block.setFloat(tvGlitch, property = "effect/tv_glitch/speed", value = 0.5F)
    engine.block.setFloat(tvGlitch, property = "effect/tv_glitch/rollSpeed", value = 0.5F)
    engine.block.appendEffect(block = tvGlitchBlock, effectBlock = tvGlitch)
    // highlight-android-tv-glitch-effect

    val tvGlitchDistortion = engine.block.getFloat(tvGlitch, property = "effect/tv_glitch/distortion")
    check(tvGlitchDistortion == 0.4F)

    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)

    // highlight-android-combine-effects
    val extraShifter = engine.block.createEffect(type = EffectType.Shifter)
    engine.block.setFloat(extraShifter, property = "effect/shifter/amount", value = 0.2F)
    engine.block.appendEffect(block = liquidBlock, effectBlock = extraShifter)
    // highlight-android-combine-effects

    // highlight-android-list-effects
    val combinedEffects = engine.block.getEffects(liquidBlock)
    // highlight-android-list-effects

    check(combinedEffects == listOf(liquid, extraShifter))

    // highlight-android-toggle-effect
    engine.block.setEffectEnabled(effectBlock = extraShifter, enabled = false)
    val disabledState = engine.block.isEffectEnabled(extraShifter)
    // highlight-android-toggle-effect

    check(!disabledState)

    // highlight-android-remove-effect
    engine.block.removeEffect(block = liquidBlock, index = 1)
    engine.block.destroy(extraShifter)
    // highlight-android-remove-effect

    val removed = engine.block.getEffects(liquidBlock) == listOf(liquid)
    check(removed)

    // highlight-android-effect-properties
    val liquidProperties = engine.block.findAllProperties(liquid)
    // highlight-android-effect-properties

    check("effect/liquid/amount" in liquidProperties)

    return Distortion(
        liquidAmount = liquidAmount,
        mirrorSide = mirrorSide,
        shifterAmount = shifterAmount,
        radialPixelRadius = radialPixelRadius,
        tvGlitchDistortion = tvGlitchDistortion,
        combinedEffectCount = combinedEffects.size,
        disabledState = disabledState,
        removed = removed,
        liquidProperties = liquidProperties,
        previewPng = previewPng,
    )
}
