import android.util.Log
import ly.img.engine.AnimationEasingType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeEasing
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.ModifierType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "ShapeModifiers"

data class ShapeModifiers(
    val shapeSupportsModifiers: Boolean,
    val graphicSupportsModifiers: Boolean,
    val badgeModifierTypes: List<String>,
    val foundTrims: List<DesignBlock>,
    val trim: DesignBlock,
    val zigzagValidAfterRemove: Boolean,
    val badgeModifierCountAfterRemove: Int,
    val flowerModifierCount: Int,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun shapeModifiers(engine: Engine): ShapeModifiers {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-blocks
    // Adds a 5 second graphic with a shape and a color fill to the page
    fun addGraphic(
        shapeType: ShapeType,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color,
    ): DesignBlock {
        val graphic = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = graphic, shape = engine.block.createShape(shapeType))
        val fill = engine.block.createFill(FillType.Color)
        engine.block.setColor(block = fill, property = "fill/color/value", value = color)
        engine.block.setFill(block = graphic, fill = fill)
        engine.block.setWidth(block = graphic, value = width)
        engine.block.setHeight(block = graphic, value = height)
        engine.block.setPositionX(block = graphic, value = x)
        engine.block.setPositionY(block = graphic, value = y)
        engine.block.setDuration(block = graphic, duration = 5.0)
        engine.block.appendChild(parent = page, child = graphic)
        return graphic
    }
    // highlight-android-create-blocks

    // highlight-android-create-modifier
    val orange = Color.fromRGBA(r = 1F, g = 0.5F, b = 0F, a = 1F)
    val outline = addGraphic(ShapeType.Star, x = 120F, y = 360F, width = 360F, height = 360F, color = orange)

    // Modifiers belong to the shape block, not to the graphic block
    val outlineShape = engine.block.getShape(block = outline)
    val shapeSupportsModifiers = engine.block.supportsModifiers(block = outlineShape) // true
    val graphicSupportsModifiers = engine.block.supportsModifiers(block = outline) // false
    Log.i(TAG, "shape: $shapeSupportsModifiers, graphic: $graphicSupportsModifiers")

    val trim = engine.block.createModifier(ModifierType.Trim)
    engine.block.appendModifier(block = outlineShape, modifierBlock = trim)
    // highlight-android-create-modifier

    // highlight-android-trim
    // Draw the outline only: no fill and a thick stroke
    engine.block.setFillEnabled(block = outline, enabled = false)
    engine.block.setStrokeEnabled(block = outline, enabled = true)
    engine.block.setStrokeWidth(block = outline, width = 16F)
    engine.block.setStrokeColor(block = outline, color = orange)

    // Keep the first 75% of the path, moved by an eighth of the path length
    engine.block.setFloat(block = trim, property = "modifier/trim/start", value = 0F)
    engine.block.setFloat(block = trim, property = "modifier/trim/end", value = 0.75F)
    engine.block.setFloat(block = trim, property = "modifier/trim/offset", value = 0.125F)
    // "Simultaneous" trims each contour on its own, "Individual" trims all contours as one path
    engine.block.setEnum(block = trim, property = "modifier/trim/mode", value = "Simultaneous")
    // highlight-android-trim

    // highlight-android-animate-trim
    // Draw the whole outline during the first 3 seconds of the 5 second block
    engine.block.setKeyframes(
        block = trim,
        property = "modifier/trim/end",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Float(0F),
                easing = KeyframeEasing.Preset(AnimationEasingType.EASE_IN_OUT),
            ),
            Keyframe(time = 0.6F, value = KeyframeValue.Float(1F)),
        ),
    )
    // highlight-android-animate-trim

    // highlight-android-repeater
    // A 40 x 140 bar, repeated around a point 220 units below its top edge
    val blue = Color.fromRGBA(r = 0.2F, g = 0.5F, b = 1F, a = 1F)
    val bar = addGraphic(ShapeType.Rect, x = 720F, y = 320F, width = 40F, height = 140F, color = blue)
    val repeater = engine.block.createModifier(ModifierType.Repeater)
    engine.block.setFloat(block = repeater, property = "modifier/repeater/copies", value = 12F)
    // Each copy turns 30 degrees further about the anchor and does not move
    engine.block.setFloat(block = repeater, property = "modifier/repeater/anchorX", value = 20F)
    engine.block.setFloat(block = repeater, property = "modifier/repeater/anchorY", value = 220F)
    engine.block.setFloat(block = repeater, property = "modifier/repeater/rotation", value = 30F)
    engine.block.setFloat(block = repeater, property = "modifier/repeater/positionX", value = 0F)
    engine.block.setFloat(block = repeater, property = "modifier/repeater/positionY", value = 0F)
    // The copies fade from full opacity towards 10%
    engine.block.setFloat(block = repeater, property = "modifier/repeater/startOpacity", value = 1F)
    engine.block.setFloat(block = repeater, property = "modifier/repeater/endOpacity", value = 0.1F)
    // "Above" draws each copy on top of the copy before it
    engine.block.setEnum(block = repeater, property = "modifier/repeater/composite", value = "Above")
    engine.block.appendModifier(block = engine.block.getShape(block = bar), modifierBlock = repeater)
    // highlight-android-repeater

    // highlight-android-animate-repeater
    // Grow the ring from 1 to 12 bars
    engine.block.setKeyframes(
        block = repeater,
        property = "modifier/repeater/copies",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(1F)),
            Keyframe(time = 0.8F, value = KeyframeValue.Float(12F)),
        ),
    )
    // highlight-android-animate-repeater

    // highlight-android-order
    val purple = Color.fromRGBA(r = 0.5F, g = 0.2F, b = 0.9F, a = 1F)
    val badge = addGraphic(ShapeType.Rect, x = 1000F, y = 360F, width = 360F, height = 360F, color = purple)
    val badgeShape = engine.block.getShape(block = badge)

    // Smooth peaks and valleys, 6 per side, 12 units from the path
    val zigzag = engine.block.createModifier(ModifierType.Zigzag)
    engine.block.setFloat(block = zigzag, property = "modifier/zigzag/size", value = 12F)
    engine.block.setFloat(block = zigzag, property = "modifier/zigzag/ridges", value = 6F)
    engine.block.setEnum(block = zigzag, property = "modifier/zigzag/pointType", value = "Smooth")
    engine.block.appendModifier(block = badgeShape, modifierBlock = zigzag)

    // Insert the rounding at index 0, so it applies before the zig zag
    val roundCorners = engine.block.createModifier(ModifierType.RoundCorners)
    engine.block.setFloat(block = roundCorners, property = "modifier/round_corners/radius", value = 60F)
    engine.block.insertModifier(block = badgeShape, modifierBlock = roundCorners, index = 0)
    // highlight-android-order

    // highlight-android-geometry
    val teal = Color.fromRGBA(r = 0F, g = 0.6F, b = 0.6F, a = 1F)
    val flower = addGraphic(ShapeType.Star, x = 1440F, y = 360F, width = 360F, height = 360F, color = teal)
    val flowerShape = engine.block.getShape(block = flower)

    // A negative amount bloats: it pushes the vertices away from the center
    val puckerBloat = engine.block.createModifier(ModifierType.PuckerBloat)
    engine.block.setFloat(block = puckerBloat, property = "modifier/pucker_bloat/amount", value = -0.3F)
    engine.block.appendModifier(block = flowerShape, modifierBlock = puckerBloat)

    // Turn the path 45 degrees at the center of the block, less towards the edge
    val twist = engine.block.createModifier(ModifierType.Twist)
    engine.block.setFloat(block = twist, property = "modifier/twist/angle", value = 45F)
    engine.block.setFloat(block = twist, property = "modifier/twist/centerX", value = 180F)
    engine.block.setFloat(block = twist, property = "modifier/twist/centerY", value = 180F)
    engine.block.appendModifier(block = flowerShape, modifierBlock = twist)

    // Grow the result by 12 units with round corners
    val offset = engine.block.createModifier(ModifierType.Offset)
    engine.block.setFloat(block = offset, property = "modifier/offset/amount", value = 12F)
    engine.block.setEnum(block = offset, property = "modifier/offset/join", value = "Round")
    engine.block.appendModifier(block = flowerShape, modifierBlock = offset)
    // highlight-android-geometry

    // highlight-android-read-back
    // [//ly.img.ubq/modifier/round_corners, //ly.img.ubq/modifier/zigzag]
    val badgeModifierTypes = engine.block.getModifiers(block = badgeShape).map { engine.block.getType(it) }

    // Find every trim modifier in the scene
    val foundTrims = engine.block.findByType(ModifierType.Trim) // [trim]
    Log.i(TAG, "badge: $badgeModifierTypes, trims: $foundTrims")
    // highlight-android-read-back

    // highlight-android-remove
    // Remove the zig zag at index 1. The modifier block still exists.
    engine.block.removeModifier(block = badgeShape, index = 1)
    val zigzagValidAfterRemove = engine.block.isValid(block = zigzag) // true
    val badgeModifierCountAfterRemove = engine.block.getModifiers(block = badgeShape).size // 1

    // Destroy it when no shape uses it any more
    engine.block.destroy(block = zigzag)
    // highlight-android-remove

    // highlight-android-errors
    val errorCode = try {
        // A graphic block has no modifiers. Pass its shape instead.
        engine.block.getModifiers(block = outline)
        null
    } catch (error: EngineException) {
        // BLOCK.MODIFIERS_UNSUPPORTED
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 2.5 seconds, halfway through the animation
    engine.block.setPlaybackTime(block = page, time = 2.5)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return ShapeModifiers(
        shapeSupportsModifiers = shapeSupportsModifiers,
        graphicSupportsModifiers = graphicSupportsModifiers,
        badgeModifierTypes = badgeModifierTypes,
        foundTrims = foundTrims,
        trim = trim,
        zigzagValidAfterRemove = zigzagValidAfterRemove,
        badgeModifierCountAfterRemove = badgeModifierCountAfterRemove,
        flowerModifierCount = engine.block.getModifiers(block = flowerShape).size,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
