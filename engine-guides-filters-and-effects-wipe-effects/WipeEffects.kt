import android.util.Log
import ly.img.editor.defaultBaseUri
import ly.img.engine.AnimationEasingType
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeEasing
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "WipeEffects"

data class WipeEffects(
    val linearWipeEffectTypes: List<String>,
    val radialWipeEffectTypes: List<String>,
    val venetianBlindsEffectTypes: List<String>,
    val revealEffectTypes: List<String>,
    val linearWipeCompletion: Float,
    val revealKeyframes: List<Keyframe>,
    val radialWipeProperties: List<String>,
    // The frame of the reveal block on the page, in design units
    val revealX: Float,
    val revealY: Float,
    val revealWidth: Float,
    val revealHeight: Float,
    val earlyFramePng: ByteBuffer,
    val lateFramePng: ByteBuffer,
)

suspend fun wipeEffects(engine: Engine): WipeEffects {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 620F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-image
    // Adds a 360 x 270 image block to the page at a position
    val imageUri = defaultBaseUri.buildUpon()
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_1.jpg")
        .build()

    fun addImageBlock(
        x: Float,
        y: Float,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
        val fill = engine.block.createFill(FillType.Image)
        engine.block.setUri(block = fill, property = "fill/image/imageFileURI", value = imageUri)
        engine.block.setFill(block = block, fill = fill)
        engine.block.setWidth(block = block, value = 360F)
        engine.block.setHeight(block = block, value = 270F)
        engine.block.setPositionX(block = block, value = x)
        engine.block.setPositionY(block = block, value = y)
        engine.block.appendChild(parent = page, child = block)
        return block
    }

    val linearWipeBlock = addImageBlock(x = 30F, y = 30F)
    val radialWipeBlock = addImageBlock(x = 410F, y = 30F)
    val venetianBlindsBlock = addImageBlock(x = 30F, y = 320F)
    val revealBlock = addImageBlock(x = 410F, y = 320F)
    // highlight-android-create-image

    // highlight-android-linear-wipe
    // Hide the left half of the block with a soft edge of 40 design units
    val linearWipe = engine.block.createEffect(EffectType.LinearWipe)
    engine.block.setFloat(block = linearWipe, property = "effect/linear_wipe/angle", value = 90F)
    engine.block.setFloat(block = linearWipe, property = "effect/linear_wipe/completion", value = 0.5F)
    engine.block.setFloat(block = linearWipe, property = "effect/linear_wipe/feather", value = 40F)
    engine.block.appendEffect(block = linearWipeBlock, effectBlock = linearWipe)
    // highlight-android-linear-wipe

    // highlight-android-radial-wipe
    // Hide 60% of the circle, sweeping clockwise from 12 o'clock around the block center
    val radialWipe = engine.block.createEffect(EffectType.RadialWipe)
    engine.block.setFloat(block = radialWipe, property = "effect/radial_wipe/completion", value = 0.6F)
    engine.block.setFloat(block = radialWipe, property = "effect/radial_wipe/startAngle", value = 0F)
    engine.block.setFloat(block = radialWipe, property = "effect/radial_wipe/centerX", value = 0.5F)
    engine.block.setFloat(block = radialWipe, property = "effect/radial_wipe/centerY", value = 0.5F)
    engine.block.setEnum(block = radialWipe, property = "effect/radial_wipe/direction", value = "Clockwise")
    engine.block.setFloat(block = radialWipe, property = "effect/radial_wipe/feather", value = 20F)
    engine.block.appendEffect(block = radialWipeBlock, effectBlock = radialWipe)
    // highlight-android-radial-wipe

    // highlight-android-venetian-blinds
    // Hide half of each 60 design unit wide stripe, with the stripes at 45 degrees
    val venetianBlinds = engine.block.createEffect(EffectType.VenetianBlinds)
    engine.block.setFloat(block = venetianBlinds, property = "effect/venetian_blinds/completion", value = 0.5F)
    engine.block.setFloat(block = venetianBlinds, property = "effect/venetian_blinds/direction", value = 45F)
    engine.block.setFloat(block = venetianBlinds, property = "effect/venetian_blinds/width", value = 60F)
    engine.block.setFloat(block = venetianBlinds, property = "effect/venetian_blinds/feather", value = 2F)
    engine.block.appendEffect(block = venetianBlindsBlock, effectBlock = venetianBlinds)
    // highlight-android-venetian-blinds

    // highlight-android-reveal
    // Reveal the block over its 2 second duration: all hidden at the start, all shown at the end
    engine.block.setDuration(block = revealBlock, duration = 2.0)
    val reveal = engine.block.createEffect(EffectType.LinearWipe)
    engine.block.appendEffect(block = revealBlock, effectBlock = reveal)
    engine.block.setKeyframes(
        block = reveal,
        property = "effect/linear_wipe/completion",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Float(1F),
                easing = KeyframeEasing.Preset(AnimationEasingType.EASE_OUT),
            ),
            Keyframe(time = 1F, value = KeyframeValue.Float(0F)),
        ),
    )
    val revealKeyframes = engine.block.getKeyframes(block = reveal, property = "effect/linear_wipe/completion")
    Log.i(TAG, "Completion keyframes: $revealKeyframes")
    // highlight-android-reveal

    // highlight-android-export
    // Export the page at 0.5 and 1.5 seconds. The reveal block shows more of the image in the later frame.
    engine.block.setPlaybackTime(block = page, time = 0.5)
    val earlyFramePng = engine.block.export(block = page, mimeType = MimeType.PNG)
    engine.block.setPlaybackTime(block = page, time = 1.5)
    val lateFramePng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    val effectTypes = { block: DesignBlock ->
        engine.block.getEffects(block).map { engine.block.getType(it) }
    }
    return WipeEffects(
        linearWipeEffectTypes = effectTypes(linearWipeBlock),
        radialWipeEffectTypes = effectTypes(radialWipeBlock),
        venetianBlindsEffectTypes = effectTypes(venetianBlindsBlock),
        revealEffectTypes = effectTypes(revealBlock),
        linearWipeCompletion = engine.block.getFloat(block = linearWipe, property = "effect/linear_wipe/completion"),
        revealKeyframes = revealKeyframes,
        radialWipeProperties = engine.block.findAllProperties(radialWipe),
        revealX = engine.block.getPositionX(revealBlock),
        revealY = engine.block.getPositionY(revealBlock),
        revealWidth = engine.block.getWidth(revealBlock),
        revealHeight = engine.block.getHeight(revealBlock),
        earlyFramePng = earlyFramePng,
        lateFramePng = lateFramePng,
    )
}
