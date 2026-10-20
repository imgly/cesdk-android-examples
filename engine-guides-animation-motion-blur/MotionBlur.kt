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
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "MotionBlur"

data class MotionBlur(
    val page: DesignBlock,
    val blockMotionBlurEnabled: Boolean,
    val blockHasMotionBlur: Boolean,
    val pageHasMotionBlur: Boolean,
    val shutterAngle: Float,
    val shutterPhase: Float,
    val samples: Int,
    val previewMotionBlurEnabled: Boolean,
    val errorCode: String?,
)

fun motionBlur(engine: Engine): MotionBlur {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 2.0)

    // highlight-android-animate-block
    // A square that crosses the page in one second. Motion blur needs motion.
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = fill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.9F, g = 0.2F, b = 0.4F, a = 1F),
    )
    engine.block.setFill(block = block, fill = fill)
    engine.block.setWidth(block = block, value = 300F)
    engine.block.setHeight(block = block, value = 300F)
    engine.block.setPositionY(block = block, value = 390F)
    engine.block.appendChild(parent = page, child = block)
    engine.block.setDuration(block = block, duration = 2.0)

    engine.block.setKeyframes(
        block = block,
        property = "position/x",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(100F)),
            Keyframe(time = 0.5F, value = KeyframeValue.Float(1520F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(100F)),
        ),
    )
    // highlight-android-animate-block

    // highlight-android-enable
    // Blur the block while it moves
    engine.block.setBoolean(block = block, property = "motionBlur/enabled", value = true)
    val blockMotionBlurEnabled = engine.block.getBoolean(block = block, property = "motionBlur/enabled")
    // highlight-android-enable

    // highlight-android-check-support
    // Graphic, text and caption blocks have the property. Pages do not.
    val blockHasMotionBlur = "motionBlur/enabled" in engine.block.findAllProperties(block = block)
    val pageHasMotionBlur = "motionBlur/enabled" in engine.block.findAllProperties(block = page)
    Log.i(TAG, "block: $blockHasMotionBlur, page: $pageHasMotionBlur")
    // highlight-android-check-support

    // highlight-android-shutter
    // The scene holds the shutter for all blocks with motion blur.
    // 360 degrees keeps the shutter open for one full frame.
    engine.block.setFloat(block = scene, property = "motionBlur/shutterAngle", value = 360F)
    // -180 degrees, half the angle, centers the shutter on the frame time
    engine.block.setFloat(block = scene, property = "motionBlur/shutterPhase", value = -180F)
    // highlight-android-shutter

    // highlight-android-samples
    // More samples give a smoother trail and take longer to render
    engine.block.setInt(block = scene, property = "motionBlur/samples", value = 32)
    // highlight-android-samples

    // highlight-android-frame-rate
    // One frame of the scene frame rate is the span the shutter angle refers to
    engine.block.setFloat(block = scene, property = "scene/frameRate", value = 30F)
    // highlight-android-frame-rate

    // highlight-android-preview-setting
    // The preview draws the motion blur by default.
    // Set the value to false to show the blocks sharp while editing.
    engine.editor.setSettingBoolean(keypath = "features/previewMotionBlurEnabled", value = false)
    val previewMotionBlurEnabled = engine.editor.getSettingBoolean(keypath = "features/previewMotionBlurEnabled")
    // highlight-android-preview-setting

    // highlight-android-errors
    val errorCode = try {
        engine.block.setInt(block = scene, property = "motionBlur/samples", value = 100)
        null
    } catch (error: EngineException) {
        // BLOCK.MOTION_BLUR_VALUE_OUT_OF_RANGE
        error.code
    }
    // highlight-android-errors

    return MotionBlur(
        page = page,
        blockMotionBlurEnabled = blockMotionBlurEnabled,
        blockHasMotionBlur = blockHasMotionBlur,
        pageHasMotionBlur = pageHasMotionBlur,
        shutterAngle = engine.block.getFloat(block = scene, property = "motionBlur/shutterAngle"),
        shutterPhase = engine.block.getFloat(block = scene, property = "motionBlur/shutterPhase"),
        samples = engine.block.getInt(block = scene, property = "motionBlur/samples"),
        previewMotionBlurEnabled = previewMotionBlurEnabled,
        errorCode = errorCode,
    )
}

suspend fun exportMotionBlurVideo(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    // highlight-android-export-video
    // Image export draws no motion blur. Export a video to see it in a file.
    val video = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(block = page),
        mimeType = MimeType.MP4,
        progressCallback = {},
    )
    // highlight-android-export-video
    return video
}
