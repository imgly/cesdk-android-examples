import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import ly.img.engine.AnimationEasingType
import ly.img.engine.AnimationType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun applyTransitions(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1280, height = 720)

    val scene = engine.scene.createForVideo()

    // highlight-android-create-clips
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 8.0)

    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)

    val firstClip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(firstClip, shape = engine.block.createShape(ShapeType.Rect))

    val firstVideoFill = engine.block.createFill(FillType.Video)
    // Video source assignment uses CE.SDK's URI-valued fill property key.
    engine.block.setUri(
        block = firstVideoFill,
        property = "fill/video/fileURI",
        value = Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        ),
    )
    engine.block.setFill(block = firstClip, fill = firstVideoFill)

    val secondClip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(secondClip, shape = engine.block.createShape(ShapeType.Rect))

    val secondVideoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = secondVideoFill,
        property = "fill/video/fileURI",
        value = Uri.parse("https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-kampus-production-8154913.mp4"),
    )
    engine.block.setFill(block = secondClip, fill = secondVideoFill)
    engine.block.appendChild(parent = track, child = firstClip)
    engine.block.appendChild(parent = track, child = secondClip)
    engine.block.fillParent(track)
    // highlight-android-create-clips

    // highlight-android-check-support
    if (!engine.block.supportsAnimation(firstClip) || !engine.block.supportsAnimation(secondClip)) {
        engine.stop()
        return@launch
    }
    // highlight-android-check-support

    // highlight-android-coordinate-timing
    engine.block.setDuration(block = firstClip, duration = 4.0)
    engine.block.setDuration(block = secondClip, duration = 4.0)
    // highlight-android-coordinate-timing

    // highlight-android-exit-transition
    val firstClipOut = engine.block.createAnimation(AnimationType.Fade)
    engine.block.setDuration(block = firstClipOut, duration = 0.8)
    engine.block.setOutAnimation(block = firstClip, animation = firstClipOut)
    // highlight-android-exit-transition

    // highlight-android-entrance-transition
    val secondClipIn = engine.block.createAnimation(AnimationType.Slide)
    engine.block.setDuration(block = secondClipIn, duration = 0.8)
    engine.block.setInAnimation(block = secondClip, animation = secondClipIn)
    // highlight-android-entrance-transition

    // highlight-android-configure-properties
    val slideProperties = engine.block.findAllProperties(secondClipIn)
    // Animation properties use engine property keys; inspect type-specific keys first.
    if ("animation/slide/direction" in slideProperties) {
        engine.block.setFloat(
            block = secondClipIn,
            property = "animation/slide/direction",
            value = Math.PI.toFloat(),
        )
    }
    engine.block.setEnum(
        block = secondClipIn,
        property = "animationEasing",
        value = AnimationEasingType.EASE_OUT.key,
    )
    // highlight-android-configure-properties

    // highlight-android-replace-transition
    val currentOut = engine.block.getOutAnimation(firstClip)
    if (engine.block.isValid(currentOut)) {
        engine.block.destroy(currentOut)
    }

    val replacementOut = engine.block.createAnimation(AnimationType.Fade)
    engine.block.setDuration(block = replacementOut, duration = 0.6)
    engine.block.setOutAnimation(block = firstClip, animation = replacementOut)
    // highlight-android-replace-transition

    // highlight-android-readback
    val attachedIn = engine.block.getInAnimation(secondClip)
    val attachedOut = engine.block.getOutAnimation(firstClip)
    // Let the scheduled engine update lay out track children before reading derived offsets.
    yield()

    check(engine.block.isValid(attachedIn))
    check(engine.block.isValid(attachedOut))
    check(engine.block.getType(attachedIn) == AnimationType.Slide.key)
    check(engine.block.getType(attachedOut) == AnimationType.Fade.key)
    check(engine.block.getDuration(attachedIn) == 0.8)
    check(engine.block.getDuration(attachedOut) == 0.6)
    check(engine.block.getEnum(attachedIn, "animationEasing") == AnimationEasingType.EASE_OUT.key)
    check(engine.block.getTimeOffset(secondClip) == 4.0)
    // highlight-android-readback

    engine.stop()
}
