import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ly.img.engine.AnimationType
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.PositionMode
import ly.img.engine.ShapeType
import kotlin.math.PI
import kotlin.math.abs

fun transformVideo(
    license: String?,
    userId: String,
): Job = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example.video-transform")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1280, height = 720)

    try {
        val sampleVideoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")

        val scene = engine.scene.createForVideo()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)
        engine.block.setWidth(page, value = 1280F)
        engine.block.setHeight(page, value = 720F)
        engine.block.setDuration(page, duration = 8.0)

        val positionedVideo = engine.block.create(DesignBlockType.Graphic)
        val positionedVideoFill = engine.block.createFill(FillType.Video)

        engine.block.setName(positionedVideo, "Positioned video")
        engine.block.setShape(positionedVideo, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(positionedVideo, value = 80F)
        engine.block.setPositionY(positionedVideo, value = 80F)
        engine.block.setWidth(positionedVideo, value = 300F)
        engine.block.setHeight(positionedVideo, value = 170F)
        // Android does not expose a typed setter for the video fill URI property.
        engine.block.setUri(block = positionedVideoFill, property = "fill/video/fileURI", value = sampleVideoUri)
        engine.block.setFill(block = positionedVideo, fill = positionedVideoFill)
        engine.block.setContentFillMode(block = positionedVideo, mode = ContentFillMode.COVER)
        engine.block.setDuration(block = positionedVideo, duration = 8.0)
        engine.block.appendChild(parent = page, child = positionedVideo)

        val rotatedVideo = engine.block.create(DesignBlockType.Graphic)
        val rotatedVideoFill = engine.block.createFill(FillType.Video)
        engine.block.setName(rotatedVideo, "Rotated video")
        engine.block.setShape(rotatedVideo, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(rotatedVideo, value = 460F)
        engine.block.setPositionY(rotatedVideo, value = 80F)
        engine.block.setWidth(rotatedVideo, value = 300F)
        engine.block.setHeight(rotatedVideo, value = 170F)
        // Android does not expose a typed setter for the video fill URI property.
        engine.block.setUri(block = rotatedVideoFill, property = "fill/video/fileURI", value = sampleVideoUri)
        engine.block.setFill(block = rotatedVideo, fill = rotatedVideoFill)
        engine.block.setContentFillMode(block = rotatedVideo, mode = ContentFillMode.COVER)
        engine.block.setDuration(block = rotatedVideo, duration = 8.0)
        engine.block.appendChild(parent = page, child = rotatedVideo)

        val croppedVideo = engine.block.create(DesignBlockType.Graphic)
        val croppedVideoFill = engine.block.createFill(FillType.Video)
        engine.block.setName(croppedVideo, "Cropped video")
        engine.block.setShape(croppedVideo, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(croppedVideo, value = 80F)
        engine.block.setPositionY(croppedVideo, value = 360F)
        engine.block.setWidth(croppedVideo, value = 300F)
        engine.block.setHeight(croppedVideo, value = 170F)
        // Android does not expose a typed setter for the video fill URI property.
        engine.block.setUri(block = croppedVideoFill, property = "fill/video/fileURI", value = sampleVideoUri)
        engine.block.setFill(block = croppedVideo, fill = croppedVideoFill)
        engine.block.setContentFillMode(block = croppedVideo, mode = ContentFillMode.COVER)
        engine.block.setDuration(block = croppedVideo, duration = 8.0)
        engine.block.appendChild(parent = page, child = croppedVideo)

        val lockedVideo = engine.block.create(DesignBlockType.Graphic)
        val lockedVideoFill = engine.block.createFill(FillType.Video)
        engine.block.setName(lockedVideo, "Locked video")
        engine.block.setShape(lockedVideo, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(lockedVideo, value = 460F)
        engine.block.setPositionY(lockedVideo, value = 360F)
        engine.block.setWidth(lockedVideo, value = 300F)
        engine.block.setHeight(lockedVideo, value = 170F)
        // Android does not expose a typed setter for the video fill URI property.
        engine.block.setUri(block = lockedVideoFill, property = "fill/video/fileURI", value = sampleVideoUri)
        engine.block.setFill(block = lockedVideo, fill = lockedVideoFill)
        engine.block.setContentFillMode(block = lockedVideo, mode = ContentFillMode.COVER)
        engine.block.setDuration(block = lockedVideo, duration = 8.0)
        engine.block.appendChild(parent = page, child = lockedVideo)

        // highlight-android-block-transforms
        engine.block.setPositionXMode(positionedVideo, mode = PositionMode.ABSOLUTE)
        engine.block.setPositionYMode(positionedVideo, mode = PositionMode.ABSOLUTE)
        engine.block.setPositionX(positionedVideo, value = 120F)
        engine.block.setPositionY(positionedVideo, value = 104F)

        engine.block.setFlipHorizontal(rotatedVideo, flip = true)
        engine.block.scale(rotatedVideo, scale = 1.15F, anchorX = 0.5F, anchorY = 0.5F)
        engine.block.setRotation(rotatedVideo, radians = (PI / 8.0).toFloat())

        engine.block.setWidth(lockedVideo, value = 280F, maintainCrop = true)
        engine.block.setHeight(lockedVideo, value = 158F, maintainCrop = true)
        // highlight-android-block-transforms

        // highlight-android-content-transforms
        if (engine.block.supportsCrop(croppedVideo)) {
            engine.block.setContentFillMode(croppedVideo, mode = ContentFillMode.CROP)
            engine.block.setCropScaleRatio(croppedVideo, scaleRatio = 1.35F)
            // Crop translations are relative to the block frame dimensions.
            engine.block.setCropTranslationX(croppedVideo, translationX = -0.12F)
            engine.block.setCropTranslationY(croppedVideo, translationY = 0.08F)
            engine.block.setCropRotation(croppedVideo, rotation = (PI / 18.0).toFloat())
            engine.block.adjustCropToFillFrame(croppedVideo, minScaleRatio = 1.0F)
        }
        // highlight-android-content-transforms

        // highlight-android-transform-controls
        engine.editor.setSettingBoolean("controlGizmo/showMoveHandles", true)
        engine.editor.setSettingBoolean("controlGizmo/showResizeHandles", true)
        engine.editor.setSettingBoolean("controlGizmo/showScaleHandles", true)
        engine.editor.setSettingBoolean("controlGizmo/showRotateHandles", true)
        engine.editor.setSettingBoolean("controlGizmo/showCropHandles", true)
        engine.editor.setSettingFloat("controlGizmo/blockScaleDownLimit", 12F)
        engine.editor.setSettingEnum("touch/rotateAction", "Rotate")
        engine.editor.setSettingEnum("touch/pinchAction", "Scale")
        // highlight-android-transform-controls

        // highlight-android-group-transforms
        if (engine.block.isGroupable(listOf(positionedVideo, croppedVideo))) {
            val group = engine.block.group(listOf(positionedVideo, croppedVideo))
            engine.block.setPositionX(group, value = 180F)
            engine.block.setRotation(group, radians = (PI / 16.0).toFloat())
        }
        // highlight-android-group-transforms

        // highlight-android-animated-transforms
        if (engine.block.supportsAnimation(rotatedVideo)) {
            val loopAnimation = engine.block.createAnimation(AnimationType.SpinLoop)
            engine.block.setLoopAnimation(rotatedVideo, loopAnimation)
            engine.block.setDuration(loopAnimation, duration = 2.0)
            engine.block.setTimeOffset(rotatedVideo, offset = 1.0)
        }
        // highlight-android-animated-transforms

        // highlight-android-lock-transforms
        val blockFrameScopes = listOf("layer/move", "layer/rotate", "layer/resize", "layer/flip")
        (blockFrameScopes + "layer/crop").forEach { scope ->
            engine.editor.setGlobalScope(key = scope, globalScope = GlobalScope.DEFER)
        }

        blockFrameScopes.forEach { scope ->
            engine.block.setScopeEnabled(lockedVideo, key = scope, enabled = false)
        }
        engine.block.setScopeEnabled(lockedVideo, key = "layer/crop", enabled = false)
        engine.block.setTransformLocked(lockedVideo, locked = true)

        val transformsLocked = engine.block.isTransformLocked(lockedVideo)
        val moveScopeEnabled = engine.block.isScopeEnabled(lockedVideo, key = "layer/move")
        val moveAllowed = engine.block.isAllowedByScope(lockedVideo, key = "layer/move")
        val cropScopeEnabled = engine.block.isScopeEnabled(lockedVideo, key = "layer/crop")
        val cropAllowed = engine.block.isAllowedByScope(lockedVideo, key = "layer/crop")
        // highlight-android-lock-transforms

        check(abs(engine.block.getRotation(rotatedVideo) - (PI / 8.0).toFloat()) < 0.001F)
        check(engine.block.isFlipHorizontal(rotatedVideo))
        check(transformsLocked)
        check(!moveScopeEnabled)
        check(!moveAllowed)
        check(!cropScopeEnabled)
        check(!cropAllowed)
        check(engine.editor.getSettingBoolean("controlGizmo/showRotateHandles"))
        check(abs(engine.editor.getSettingFloat("controlGizmo/blockScaleDownLimit") - 12F) < 0.001F)
        check(engine.editor.getSettingEnum("touch/pinchAction") == "Scale")
    } finally {
        engine.stop()
    }
}
