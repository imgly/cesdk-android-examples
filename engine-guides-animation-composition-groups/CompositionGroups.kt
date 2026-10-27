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
import java.nio.ByteBuffer

private const val TAG = "CompositionGroups"

data class CompositionGroups(
    val normalIsComposition: Boolean,
    val fastSpeed: Float,
    val plainIsComposition: Boolean,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun compositionGroups(engine: Engine): CompositionGroups {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 6.0)

    // highlight-android-create-lane
    // Creates a lane: a group with a line and a ball that crosses the page in
    // 4 seconds of its own time
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
        // Long enough for every lane clock, so the line stays visible
        engine.block.setDuration(block = line, duration = 12.0)

        val ball = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = ball, shape = engine.block.createShape(ShapeType.Ellipse))
        engine.block.setFill(block = ball, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = ball, color = color)
        engine.block.setWidth(block = ball, value = 80F)
        engine.block.setHeight(block = ball, value = 80F)
        engine.block.setPositionY(block = ball, value = y)
        engine.block.appendChild(parent = page, child = ball)
        engine.block.setDuration(block = ball, duration = 4.0)
        engine.block.setKeyframes(
            block = ball,
            property = "position/x",
            keyframes = listOf(
                Keyframe(time = 0F, value = KeyframeValue.Float(120F)),
                Keyframe(time = 1F, value = KeyframeValue.Float(1720F)),
            ),
        )

        val lane = engine.block.group(blocks = listOf(line, ball))
        engine.block.setDuration(block = lane, duration = 6.0)
        return lane
    }
    // highlight-android-create-lane

    // highlight-android-composition
    // The first lane is a composition group. Its children run on the group's
    // own clock, which starts when the group starts on the page.
    val normal = createLane(y = 150F, color = Color.fromRGBA(r = 0.6F, g = 0.6F, b = 0.6F, a = 1F))
    engine.block.setBoolean(block = normal, property = "group/composition", value = true)
    // true
    val normalIsComposition = engine.block.getBoolean(block = normal, property = "group/composition")
    // highlight-android-composition

    // highlight-android-speed
    // At speed 2, the children see 2 seconds for every second of the page
    val fast = createLane(y = 400F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F))
    engine.block.setBoolean(block = fast, property = "group/composition", value = true)
    engine.block.setPlaybackSpeed(block = fast, speed = 2F)
    // 2.0
    val fastSpeed = engine.block.getPlaybackSpeed(block = fast)
    // highlight-android-speed

    // highlight-android-offset
    // The group starts 1 second into the page, and its children start with it
    val late = createLane(y = 650F, color = Color.fromRGBA(r = 0.9F, g = 0.3F, b = 0.4F, a = 1F))
    engine.block.setBoolean(block = late, property = "group/composition", value = true)
    engine.block.setTimeOffset(block = late, offset = 1.0)
    engine.block.setDuration(block = late, duration = 5.0)
    // highlight-android-offset

    // highlight-android-plain-group
    // Without composition, the speed of a group has no effect
    val plain = createLane(y = 900F, color = Color.fromRGBA(r = 0.2F, g = 0.7F, b = 0.3F, a = 1F))
    engine.block.setPlaybackSpeed(block = plain, speed = 2F)
    // false
    val plainIsComposition = engine.block.getBoolean(block = plain, property = "group/composition")
    Log.i(TAG, "$normalIsComposition, $fastSpeed, $plainIsComposition")
    // highlight-android-plain-group

    // highlight-android-errors
    val errorCode = try {
        // A group runs at 0.25 times the page speed or faster
        engine.block.setPlaybackSpeed(block = normal, speed = 0.1F)
        null
    } catch (error: EngineException) {
        // EDITOR.PLAYBACK_SPEED_TOO_LOW
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 1.5 seconds
    engine.block.setPlaybackTime(block = page, time = 1.5)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    Log.i(TAG, "PNG bytes: ${previewPng.remaining()}")
    // highlight-android-export

    return CompositionGroups(
        normalIsComposition = normalIsComposition,
        fastSpeed = fastSpeed,
        plainIsComposition = plainIsComposition,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
