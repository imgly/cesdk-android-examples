import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.Vector2
import java.nio.ByteBuffer

private const val TAG = "MotionPaths"

data class MotionPaths(
    val ballPosition: Vector2,
    val keyframeCount: Int,
    val firstSpatialOut: Vector2?,
    val valueKindErrorCode: String?,
    val tangentErrorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun motionPaths(engine: Engine): MotionPaths {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 5.0)

    // highlight-android-create-circle
    // Creates a circle graphic of the given size and color on the page
    fun createCircle(
        size: Float,
        color: RGBAColor,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Ellipse))
        engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = block, color = color)
        engine.block.setWidth(block = block, value = size)
        engine.block.setHeight(block = block, value = size)
        engine.block.appendChild(parent = page, child = block)
        engine.block.setDuration(block = block, duration = 5.0)
        return block
    }
    // highlight-android-create-circle

    // highlight-android-curved-path
    // Both tangents of a segment sit on its first keyframe. `spatialOut` is
    // relative to this keyframe's value, `spatialIn` to the next one's.
    val ball = createCircle(size = 80F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F))
    engine.block.setVec2(block = ball, property = "position", value = Vector2(200F, 800F))
    engine.block.setKeyframes(
        block = ball,
        property = "position",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Vec2(Vector2(200F, 800F)),
                spatialOut = Vector2(0F, -400F),
                spatialIn = Vector2(-400F, 0F),
            ),
            Keyframe(
                time = 0.5F,
                value = KeyframeValue.Vec2(Vector2(920F, 300F)),
                spatialOut = Vector2(400F, 0F),
                spatialIn = Vector2(0F, -400F),
            ),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(1640F, 800F))),
        ),
    )
    val ballPosition = engine.block.getVec2(block = ball, property = "position")
    // Vector2(x=200.0, y=800.0)
    Log.i(TAG, "$ballPosition")
    // highlight-android-curved-path

    // highlight-android-straight-path
    val dot = createCircle(size = 40F, color = Color.fromRGBA(r = 0.9F, g = 0.3F, b = 0.4F, a = 1F))
    engine.block.setVec2(block = dot, property = "position", value = Vector2(200F, 980F))
    engine.block.setKeyframes(
        block = dot,
        property = "position",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Vec2(Vector2(200F, 980F))),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(1680F, 980F))),
        ),
    )
    // highlight-android-straight-path

    // highlight-android-read-back
    val keyframes = engine.block.getKeyframes(block = ball, property = "position")
    // 3, Vector2(x=0.0, y=-400.0)
    Log.i(TAG, "${keyframes.size}, ${keyframes[0].spatialOut}")
    // highlight-android-read-back

    // highlight-android-axis-override
    // The `position/y` track replaces the y-axis of the `position` track
    val hopper = createCircle(size = 60F, color = Color.fromRGBA(r = 0.2F, g = 0.7F, b = 0.3F, a = 1F))
    engine.block.setKeyframes(
        block = hopper,
        property = "position",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Vec2(Vector2(200F, 100F))),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(1660F, 100F))),
        ),
    )
    engine.block.setKeyframes(
        block = hopper,
        property = "position/y",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(160F)),
            Keyframe(time = 0.25F, value = KeyframeValue.Float(40F)),
            Keyframe(time = 0.5F, value = KeyframeValue.Float(160F)),
            Keyframe(time = 0.75F, value = KeyframeValue.Float(40F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(160F)),
        ),
    )
    // highlight-android-axis-override

    // highlight-android-errors
    val valueKindErrorCode = try {
        // A motion path takes vec2 values only
        engine.block.setKeyframes(
            block = ball,
            property = "position",
            keyframes = listOf(Keyframe(time = 0F, value = KeyframeValue.Float(0F))),
        )
        null
    } catch (error: EngineException) {
        // BLOCK.KEYFRAMES_VALUE_KIND_MISMATCH
        error.code
    }
    val tangentErrorCode = try {
        // A single axis takes no spatial tangents
        engine.block.setKeyframes(
            block = ball,
            property = "position/x",
            keyframes = listOf(
                Keyframe(time = 0F, value = KeyframeValue.Float(0F), spatialOut = Vector2(10F, 0F)),
            ),
        )
        null
    } catch (error: EngineException) {
        // BLOCK.KEYFRAMES_SPATIAL_TANGENT_UNSUPPORTED
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 1.25 seconds, while the ball climbs its path
    engine.block.setPlaybackTime(block = page, time = 1.25)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    Log.i(TAG, "PNG bytes: ${previewPng.remaining()}")
    // highlight-android-export

    return MotionPaths(
        ballPosition = ballPosition,
        keyframeCount = keyframes.size,
        firstSpatialOut = keyframes[0].spatialOut,
        valueKindErrorCode = valueKindErrorCode,
        tangentErrorCode = tangentErrorCode,
        previewPng = previewPng,
    )
}
