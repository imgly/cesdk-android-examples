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
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "TimeRemap"

data class TimeRemap(
    val normalRemapEnabled: Boolean,
    val reversedStaticRemap: Float,
    val remapIsAnimatable: Boolean,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun timeRemap(engine: Engine): TimeRemap {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 5.0)

    // highlight-android-create-lane
    // Creates a lane: a composition group with a line and a ball that crosses
    // the page in 5 seconds of the group's own clock
    fun createLane(
        y: Float,
        color: RGBAColor,
    ): DesignBlock {
        val line = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = line, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(block = line, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = line, color = Color.fromRGBA(r = 0.85F, g = 0.85F, b = 0.85F, a = 1F))
        engine.block.setWidth(block = line, value = 1680F)
        engine.block.setHeight(block = line, value = 6F)
        engine.block.setPositionX(block = line, value = 120F)
        engine.block.setPositionY(block = line, value = y + 37F)
        engine.block.appendChild(parent = page, child = line)
        engine.block.setDuration(block = line, duration = 5.0)

        val ball = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = ball, shape = engine.block.createShape(ShapeType.Ellipse))
        engine.block.setFill(block = ball, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = ball, color = color)
        engine.block.setWidth(block = ball, value = 80F)
        engine.block.setHeight(block = ball, value = 80F)
        engine.block.setPositionY(block = ball, value = y)
        engine.block.appendChild(parent = page, child = ball)
        engine.block.setDuration(block = ball, duration = 5.0)
        engine.block.setKeyframes(
            block = ball,
            property = "position/x",
            keyframes = listOf(
                Keyframe(time = 0F, value = KeyframeValue.Float(120F)),
                Keyframe(time = 1F, value = KeyframeValue.Float(1720F)),
            ),
        )

        val lane = engine.block.group(blocks = listOf(line, ball))
        engine.block.setDuration(block = lane, duration = 5.0)
        // A composition group gives its children their own clock
        engine.block.setBoolean(block = lane, property = "group/composition", value = true)
        return lane
    }
    // highlight-android-create-lane

    // highlight-android-lanes
    val normal = createLane(y = 150F, color = Color.fromRGBA(r = 0.6F, g = 0.6F, b = 0.6F, a = 1F))
    val frozen = createLane(y = 400F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F))
    val reversed = createLane(y = 650F, color = Color.fromRGBA(r = 0.9F, g = 0.3F, b = 0.4F, a = 1F))
    val pingPong = createLane(y = 900F, color = Color.fromRGBA(r = 0.2F, g = 0.7F, b = 0.3F, a = 1F))
    // false
    val normalRemapEnabled = engine.block.getBoolean(block = normal, property = "playback/timeRemapEnabled")
    // highlight-android-lanes

    // highlight-android-freeze
    // A static remap holds the children at 2.5 seconds of their own time
    engine.block.setBoolean(block = frozen, property = "playback/timeRemapEnabled", value = true)
    engine.block.setFloat(block = frozen, property = "playback/timeRemap", value = 2.5F)
    // highlight-android-freeze

    // highlight-android-reverse
    // The track values are times on the children's clock, in seconds. They
    // stay below 5, because a clock at a child's end hides the child.
    engine.block.setBoolean(block = reversed, property = "playback/timeRemapEnabled", value = true)
    engine.block.setKeyframes(
        block = reversed,
        property = "playback/timeRemap",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(4.99F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(0F)),
        ),
    )
    // highlight-android-reverse

    // highlight-android-ping-pong
    val easeInOut = KeyframeEasing.Preset(AnimationEasingType.EASE_IN_OUT)
    engine.block.setBoolean(block = pingPong, property = "playback/timeRemapEnabled", value = true)
    engine.block.setKeyframes(
        block = pingPong,
        property = "playback/timeRemap",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(0F), easing = easeInOut),
            Keyframe(time = 0.5F, value = KeyframeValue.Float(4.99F), easing = easeInOut),
            Keyframe(time = 1F, value = KeyframeValue.Float(0F)),
        ),
    )
    // highlight-android-ping-pong

    // highlight-android-read-back
    // The track leaves the static remap unchanged: 0.0
    val reversedStaticRemap = engine.block.getFloat(block = reversed, property = "playback/timeRemap")
    // true
    val remapIsAnimatable = engine.block.isPropertyAnimatable(block = reversed, property = "playback/timeRemap")
    Log.i(TAG, "$reversedStaticRemap, $remapIsAnimatable")
    // highlight-android-read-back

    // highlight-android-errors
    val errorCode = try {
        // Only a group has a time remap
        val ball = engine.block.getChildren(block = normal)[1]
        engine.block.setFloat(block = ball, property = "playback/timeRemap", value = 1F)
        null
    } catch (error: EngineException) {
        // EDITOR.BLOCK_NO_TIME_REMAP_SUPPORT
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 1.5 seconds
    engine.block.setPlaybackTime(block = page, time = 1.5)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    Log.i(TAG, "PNG bytes: ${previewPng.remaining()}")
    // highlight-android-export

    return TimeRemap(
        normalRemapEnabled = normalRemapEnabled,
        reversedStaticRemap = reversedStaticRemap,
        remapIsAnimatable = remapIsAnimatable,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
