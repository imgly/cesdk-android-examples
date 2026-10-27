import android.util.Log
import ly.img.engine.AnimationEasingType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DiscreteValue
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeEasing
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.TrackMode
import ly.img.engine.Vector2
import java.nio.ByteBuffer
import kotlin.math.PI

private const val TAG = "Keyframes"

data class Keyframes(
    val opacityIsAnimatable: Boolean,
    val fillColorIsAnimatable: Boolean,
    val visibleIsAnimatable: Boolean,
    val animatedProperties: List<String>,
    val opacityKeyframes: List<Keyframe>,
    val positionMode: TrackMode,
    val opacityKeyframeCountAfterStaticChange: Int,
    val shapeAnimatedProperties: List<String>,
    val dotAnimatedProperties: List<String>,
    val arrow: DesignBlock,
    val riderTransformParent: DesignBlock?,
    val transformParentErrorCode: String?,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun keyframes(engine: Engine): Keyframes {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-block
    // A 5 second rectangle with a color fill and a rect shape
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
    engine.block.setWidth(block = block, value = 600F)
    engine.block.setHeight(block = block, value = 400F)
    engine.block.setPositionX(block = block, value = 360F)
    engine.block.setPositionY(block = block, value = 340F)
    engine.block.appendChild(parent = page, child = block)
    engine.block.setDuration(block = block, duration = 5.0)

    // The fill and the shape own their properties and their keyframes
    val fill = engine.block.getFill(block = block)
    val shape = engine.block.getShape(block = block)
    // highlight-android-create-block

    // highlight-android-check-animatable
    val opacityIsAnimatable = engine.block.isPropertyAnimatable(block = block, property = "opacity")
    val fillColorIsAnimatable = engine.block.isPropertyAnimatable(block = fill, property = "fill/color/value")
    val visibleIsAnimatable = engine.block.isPropertyAnimatable(block = block, property = "visible")
    Log.i(TAG, "opacity: $opacityIsAnimatable, fill color: $fillColorIsAnimatable, visible: $visibleIsAnimatable")
    // highlight-android-check-animatable

    // highlight-android-fade-in
    // Fade in during the first 40% of the block's duration (2 seconds)
    engine.block.setKeyframes(
        block = block,
        property = "opacity",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Float(0F),
                easing = KeyframeEasing.Preset(AnimationEasingType.EASE_OUT),
            ),
            Keyframe(time = 0.4F, value = KeyframeValue.Float(1F)),
        ),
    )
    // highlight-android-fade-in

    // highlight-android-color-hold
    // Orange, then a hold on purple, then a fade to teal
    engine.block.setKeyframes(
        block = fill,
        property = "fill/color/value",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Color(Color.fromRGBA(r = 1F, g = 0.5F, b = 0F, a = 1F))),
            Keyframe(
                time = 0.5F,
                value = KeyframeValue.Color(Color.fromRGBA(r = 0.5F, g = 0.2F, b = 0.9F, a = 1F)),
                easing = KeyframeEasing.Hold,
            ),
            Keyframe(time = 0.75F, value = KeyframeValue.Color(Color.fromRGBA(r = 0.4F, g = 0.8F, b = 0.8F, a = 1F))),
            Keyframe(time = 1F, value = KeyframeValue.Color(Color.fromRGBA(r = 0F, g = 0.6F, b = 0.6F, a = 1F))),
        ),
    )
    // highlight-android-color-hold

    // highlight-android-bezier
    // Grow every corner radius with a CSS-style cubic bezier
    for (corner in listOf("TL", "TR", "BL", "BR")) {
        engine.block.setKeyframes(
            block = shape,
            property = "shape/rect/cornerRadius$corner",
            keyframes = listOf(
                Keyframe(
                    time = 0F,
                    value = KeyframeValue.Float(0F),
                    easing = KeyframeEasing.Bezier(out = Vector2(0.7F, 0F), `in` = Vector2(0.3F, 1F)),
                ),
                Keyframe(time = 1F, value = KeyframeValue.Float(200F)),
            ),
        )
    }
    // highlight-android-bezier

    // highlight-android-relative
    // Slide 600 units to the right of the block's static x position
    engine.block.setKeyframes(
        block = block,
        property = "position/x",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(0F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(600F)),
        ),
        mode = TrackMode.RELATIVE,
    )
    // highlight-android-relative

    // highlight-android-discrete
    // Switch the stroke on halfway; discrete values never interpolate
    engine.block.setStrokeWidth(block = block, width = 12F)
    engine.block.setStrokeColor(block = block, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F))
    engine.block.setKeyframes(
        block = block,
        property = "stroke/enabled",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Discrete(DiscreteValue.Bool(false))),
            Keyframe(time = 0.5F, value = KeyframeValue.Discrete(DiscreteValue.Bool(true))),
        ),
    )
    // highlight-android-discrete

    // highlight-android-motion-path
    // A dot follows a curved path. Vec2 keyframes animate `position/x` and
    // `position/y` together, and the tangents bend the path upward.
    val dot = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = dot, shape = engine.block.createShape(ShapeType.Ellipse))
    val dotFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = dotFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.9F, g = 0.2F, b = 0.4F, a = 1F),
    )
    engine.block.setFill(block = dot, fill = dotFill)
    engine.block.setWidth(block = dot, value = 120F)
    engine.block.setHeight(block = dot, value = 120F)
    engine.block.appendChild(parent = page, child = dot)
    engine.block.setDuration(block = dot, duration = 5.0)
    // The static position is the start of the path.
    engine.block.setVec2(block = dot, property = "position", value = Vector2(200F, 900F))

    engine.block.setKeyframes(
        block = dot,
        property = "position",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Vec2(Vector2(200F, 900F)),
                spatialOut = Vector2(300F, -600F),
                spatialIn = Vector2(-300F, -600F),
            ),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(1600F, 900F))),
        ),
    )
    // highlight-android-motion-path

    // highlight-android-scale
    // Pulse the dot to 1.5 times its size and back. Its frame does not change.
    for (axis in listOf("x", "y")) {
        engine.block.setKeyframes(
            block = dot,
            property = "transform/scale/$axis",
            keyframes = listOf(
                Keyframe(time = 0F, value = KeyframeValue.Float(1F)),
                Keyframe(time = 0.5F, value = KeyframeValue.Float(1.5F)),
                Keyframe(time = 1F, value = KeyframeValue.Float(1F)),
            ),
        )
    }
    val dotAnimatedProperties = engine.block.getAnimatedProperties(block = dot)
    // highlight-android-scale

    // highlight-android-anchor-skew
    // A bar that leans by 20 degrees and turns half a turn about its centre.
    // The anchor is in design units from the top-left corner of the frame.
    val bar = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = bar, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = bar, fill = engine.block.createFill(FillType.Color))
    engine.block.setWidth(block = bar, value = 320F)
    engine.block.setHeight(block = bar, value = 80F)
    engine.block.setPositionX(block = bar, value = 120F)
    engine.block.setPositionY(block = bar, value = 120F)
    engine.block.appendChild(parent = page, child = bar)
    engine.block.setDuration(block = bar, duration = 5.0)
    engine.block.setFloat(block = bar, property = "transform/anchor/x", value = 160F)
    engine.block.setFloat(block = bar, property = "transform/anchor/y", value = 40F)
    engine.block.setFloat(block = bar, property = "transform/skew", value = 20F)
    engine.block.setKeyframes(
        block = bar,
        property = "rotation",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(0F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(PI.toFloat())),
        ),
    )
    // highlight-android-anchor-skew

    // highlight-android-has-anchor
    // The bar has an anchor, so its rotation track turns it about the anchor.
    // A graphic or text without an anchor turns about its frame centre.
    val barHasAnchor = engine.block.hasTransformAnchor(block = bar)
    Log.i(TAG, "bar has an anchor: $barHasAnchor") // true
    // highlight-android-has-anchor

    // highlight-android-auto-orient
    // A bar on a wavy path turns to follow the direction of its motion
    val arrow = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = arrow, shape = engine.block.createShape(ShapeType.Rect))
    val arrowFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = arrowFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.1F, g = 0.6F, b = 0.3F, a = 1F),
    )
    engine.block.setFill(block = arrow, fill = arrowFill)
    engine.block.setWidth(block = arrow, value = 160F)
    engine.block.setHeight(block = arrow, value = 40F)
    engine.block.appendChild(parent = page, child = arrow)
    engine.block.setDuration(block = arrow, duration = 5.0)
    engine.block.setFloat(block = arrow, property = "transform/anchor/x", value = 80F)
    engine.block.setFloat(block = arrow, property = "transform/anchor/y", value = 20F)
    engine.block.setVec2(block = arrow, property = "position", value = Vector2(700F, 140F))
    engine.block.setKeyframes(
        block = arrow,
        property = "position",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Vec2(Vector2(700F, 140F)),
                spatialOut = Vector2(300F, 150F),
                spatialIn = Vector2(-300F, -150F),
            ),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(1600F, 140F))),
        ),
    )
    engine.block.setBoolean(block = arrow, property = "transform/autoOrient", value = true)
    // highlight-android-auto-orient

    // highlight-android-transform-parent
    // A square rides in front of the arrow. Its position is read in the
    // arrow's local space, so it moves and turns with the arrow.
    val rider = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = rider, shape = engine.block.createShape(ShapeType.Rect))
    val riderFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = riderFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.95F, g = 0.7F, b = 0.1F, a = 1F),
    )
    engine.block.setFill(block = rider, fill = riderFill)
    engine.block.setWidth(block = rider, value = 40F)
    engine.block.setHeight(block = rider, value = 40F)
    engine.block.setPositionX(block = rider, value = 170F)
    engine.block.setPositionY(block = rider, value = 0F)
    engine.block.appendChild(parent = page, child = rider)
    engine.block.setTransformParent(block = rider, parent = arrow)
    // Returns the arrow
    val riderTransformParent = engine.block.getTransformParent(block = rider)

    val transformParentErrorCode = try {
        // The arrow cannot follow a block that follows the arrow
        engine.block.setTransformParent(block = arrow, parent = rider)
        null
    } catch (error: EngineException) {
        // BLOCK.TRANSFORM_PARENT_CYCLE
        error.code
    }
    // highlight-android-transform-parent

    // highlight-android-read-back
    // [opacity, position/x, stroke/enabled]
    val animatedProperties = engine.block.getAnimatedProperties(block = block)

    // The second keyframe reports the default easing, Preset(LINEAR)
    val opacityKeyframes = engine.block.getKeyframes(block = block, property = "opacity")

    // RELATIVE
    val positionMode = engine.block.getKeyframeMode(block = block, property = "position/x")
    Log.i(TAG, "$animatedProperties, ${opacityKeyframes[1].easing}, $positionMode")
    // highlight-android-read-back

    // highlight-android-static-value
    // Changes the static value only. The absolute opacity track still replaces it.
    engine.block.setOpacity(block = block, value = 0.5F)
    val opacityKeyframeCountAfterStaticChange = engine.block.getKeyframes(block = block, property = "opacity").size
    // highlight-android-static-value

    // highlight-android-remove
    // The bottom corners return to their static radius of 0
    engine.block.removeKeyframes(block = shape, property = "shape/rect/cornerRadiusBL")
    engine.block.removeKeyframes(block = shape, property = "shape/rect/cornerRadiusBR")
    // [shape/rect/cornerRadiusTL, shape/rect/cornerRadiusTR]
    val shapeAnimatedProperties = engine.block.getAnimatedProperties(block = shape)
    // highlight-android-remove

    // highlight-android-errors
    val errorCode = try {
        engine.block.setKeyframes(
            block = block,
            property = "visible",
            keyframes = listOf(Keyframe(time = 0F, value = KeyframeValue.Discrete(DiscreteValue.Bool(true)))),
        )
        null
    } catch (error: EngineException) {
        // BLOCK.KEYFRAMES_PROPERTY_NOT_ANIMATABLE
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 2.5 seconds, halfway through the animation
    engine.block.setPlaybackTime(block = page, time = 2.5)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return Keyframes(
        opacityIsAnimatable = opacityIsAnimatable,
        fillColorIsAnimatable = fillColorIsAnimatable,
        visibleIsAnimatable = visibleIsAnimatable,
        animatedProperties = animatedProperties,
        opacityKeyframes = opacityKeyframes,
        positionMode = positionMode,
        opacityKeyframeCountAfterStaticChange = opacityKeyframeCountAfterStaticChange,
        shapeAnimatedProperties = shapeAnimatedProperties,
        dotAnimatedProperties = dotAnimatedProperties,
        arrow = arrow,
        riderTransformParent = riderTransformParent,
        transformParentErrorCode = transformParentErrorCode,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
