import android.net.Uri
import android.util.Log
import kotlinx.coroutines.yield
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.TransitionType

private const val TAG = "ApplyTransitions"

// The engine lays out track children on its scheduled update pass, so derived values such as time
// offsets only settle once that pass has run. Offscreen runs like this sample have to wait for it.
private suspend fun waitForScheduledEngineUpdate() = yield()

suspend fun applyTransitions(engine: Engine): TransitionSummary {
    // highlight-android-setup
    val scene = engine.scene.createForVideo()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1280F)
    engine.block.setHeight(block = page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-setup

    // highlight-android-create-clips
    // Build a sequence of four clips on a single track.
    val videoUris = listOf(
        "https://img.ly/static/ubq_video_samples/bbb.mp4",
        "https://img.ly/static/ubq_video_samples/test30.mp4",
        "https://img.ly/static/ubq_video_samples/multitrack_output.mp4",
        "https://img.ly/static/ubq_video_samples/bbb.mp4",
    )

    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)

    val clips = videoUris.map { videoUri ->
        val clip = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = clip, shape = engine.block.createShape(ShapeType.Rect))

        val videoFill = engine.block.createFill(FillType.Video)
        engine.block.setUri(block = videoFill, property = "fill/video/fileURI", value = Uri.parse(videoUri))
        engine.block.setFill(block = clip, fill = videoFill)

        engine.block.setDuration(block = clip, duration = 4.0)
        engine.block.appendChild(parent = track, child = clip)
        clip
    }
    engine.block.fillParent(track)

    val (clipA, clipB, clipC) = clips
    // highlight-android-create-clips

    // highlight-android-check-support
    // Individual clips placed directly in a video track support transitions, no matter whether they
    // show a video, an image, a shape, a sticker or text.
    val clipsSupportTransitions =
        engine.block.supportsTransition(clipA) && engine.block.supportsTransition(clipB)
    Log.i(TAG, "Clips support transitions: $clipsSupportTransitions")
    // highlight-android-check-support

    // highlight-android-create-transition
    // The duration defines how long the two clips overlap.
    val crossFade = engine.block.createTransition(TransitionType.CrossFade)
    engine.block.setDuration(block = crossFade, duration = 1.0)
    // highlight-android-create-transition

    // highlight-android-set-transition
    // Assign the transition to the outgoing clip. It blends this clip into the next clip
    // on the same track.
    engine.block.setTransition(block = clipA, transition = crossFade)
    // highlight-android-set-transition

    waitForScheduledEngineUpdate()

    // highlight-android-timeline-reflow
    // Assigning a transition overlaps the two clips: the incoming clip and everything after it
    // move earlier by the transition duration.
    val incomingClipOffset = engine.block.getTimeOffset(clipB)
    Log.i(TAG, "Clip B now starts at $incomingClipOffset seconds (was 4)")
    // highlight-android-timeline-reflow

    // highlight-android-configure-properties
    // Per-type properties use the generic block setters with "transition/{type}/{property}" keys.
    // Discover what a type exposes with findAllProperties.
    val push = engine.block.createTransition(TransitionType.Push)
    engine.block.setDuration(block = push, duration = 1.0)
    engine.block.setTransition(block = clipB, transition = push)

    val pushProperties = engine.block.findAllProperties(push)
    Log.i(TAG, "Push properties: $pushProperties")

    engine.block.setEnum(block = push, property = "transition/push/direction", value = "Left")
    // highlight-android-configure-properties

    // highlight-android-morph
    // With morph enabled, position, rotation, scale, and shape are interpolated between the
    // outgoing and incoming clip.
    engine.block.setBoolean(block = push, property = "transition/push/morph", value = true)
    // highlight-android-morph

    // highlight-android-get-transition
    // An unset relation returns an invalid block, so check the result with isValid.
    val assigned = engine.block.getTransition(clipA)
    val assignedType = if (engine.block.isValid(assigned)) engine.block.getType(assigned) else null
    Log.i(TAG, "Clip A transitions with: $assignedType")
    // highlight-android-get-transition

    // highlight-android-remove-transition
    val fadeToBlack = engine.block.createTransition(TransitionType.FadeToBlack)
    engine.block.setDuration(block = fadeToBlack, duration = 1.0)
    engine.block.setTransition(block = clipC, transition = fadeToBlack)

    // removeTransition detaches the block and restores the original clip timing, but does not
    // destroy it: the detached block stays valid until you destroy it yourself.
    engine.block.removeTransition(clipC)
    val detachedTransitionIsValid = engine.block.isValid(fadeToBlack)
    engine.block.destroy(fadeToBlack)

    val colorWipe = engine.block.createTransition(TransitionType.ColorWipe)
    engine.block.setDuration(block = colorWipe, duration = 1.0)
    engine.block.setTransition(block = clipC, transition = colorWipe)
    engine.block.setEnum(block = colorWipe, property = "transition/color-wipe/direction", value = "Up")
    engine.block.setColor(
        block = colorWipe,
        property = "transition/color-wipe/color",
        value = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F),
    )
    // highlight-android-remove-transition

    // Fit the page duration to the reflowed sequence and park the playhead inside the first
    // overlap window so the cross-fade blend is the visible frame.
    val lastClip = clips.last()
    engine.block.setDuration(
        block = page,
        duration = engine.block.getTimeOffset(lastClip) + engine.block.getDuration(lastClip),
    )
    engine.block.setPlaybackTime(block = page, time = 3.5)

    return TransitionSummary(
        clipsSupportTransitions = clipsSupportTransitions,
        incomingClipOffset = incomingClipOffset,
        pushProperties = pushProperties,
        assignedTransitionType = assignedType,
        replacedTransitionType = engine.block.getType(engine.block.getTransition(clipC)),
        detachedTransitionIsValid = detachedTransitionIsValid,
    )
}
