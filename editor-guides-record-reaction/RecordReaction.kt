import android.graphics.RectF
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import ly.img.camera.core.CameraLayoutMode
import ly.img.camera.core.CameraMode
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureMedia
import ly.img.camera.core.EngineConfiguration
import ly.img.camera.core.Recording
import ly.img.camera.core.Video
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.time.DurationUnit

data class ReactionSceneComposition(
    val page: DesignBlock,
    val baseVideoBlock: DesignBlock,
    val reactionTrack: DesignBlock,
    val reactionBlocks: List<DesignBlock>,
    val durationSeconds: Double,
)

// highlight-android-handle-result
private fun handleReactionCameraResult(
    result: CameraResult?,
    onReactionReady: (CameraResult.Reaction) -> Unit,
    onDismissed: () -> Unit,
) {
    when (result) {
        null -> onDismissed()
        is CameraResult.Reaction -> onReactionReady(result)
        else -> Unit
    }
}
// highlight-android-handle-result

// highlight-android-launch-reaction
@Composable
fun rememberRecordReactionLauncher(
    baseVideoUri: Uri,
    license: String?,
    userId: String?,
    onReactionReady: (CameraResult.Reaction) -> Unit,
    onDismissed: () -> Unit = {},
): () -> Unit {
    val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureMedia()) { result ->
        handleReactionCameraResult(result, onReactionReady, onDismissed)
    }

    return {
        val input = CaptureMedia.Input(
            engineConfiguration = EngineConfiguration(
                license = license,
                userId = userId,
            ),
            cameraMode = CameraMode.Reaction(
                video = baseVideoUri,
                cameraLayoutMode = CameraLayoutMode.Vertical,
                positionsSwapped = false,
            ),
        )
        cameraLauncher.launch(input)
    }
}
// highlight-android-launch-reaction

suspend fun createReactionVideoScene(
    engine: Engine,
    cameraResult: CameraResult.Reaction,
): ReactionSceneComposition {
    // highlight-android-build-scene
    val firstReactionVideo = cameraResult.reaction
        .firstNotNullOfOrNull { recording -> recording.videos.firstOrNull() }
        ?: error("Reaction result does not contain a recorded video.")

    check(engine.scene.get() == null) { "Call this before loading another scene." }
    engine.scene.createFromVideo(cameraResult.video.uri)

    val page = checkNotNull(engine.scene.getCurrentPage())
    val sceneFrame = RectF(cameraResult.video.rect).apply {
        union(firstReactionVideo.rect)
    }
    setFrame(engine = engine, designBlock = page, rect = sceneFrame)

    val baseVideoBlock = engine.block.findByType(DesignBlockType.Graphic).first()
    setFrame(engine = engine, designBlock = baseVideoBlock, rect = cameraResult.video.rect)

    val reactionTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = reactionTrack)
    // highlight-android-build-scene

    // highlight-android-sync-duration
    val baseFill = engine.block.getFill(baseVideoBlock)
    engine.block.forceLoadAVResource(baseFill)
    val baseDurationSeconds = engine.block.getAVResourceTotalDuration(baseFill)

    val reactionBlocks = mutableListOf<DesignBlock>()
    var reactionOffsetSeconds = 0.0
    for (recording in cameraResult.reaction) {
        val remainingSeconds = baseDurationSeconds - reactionOffsetSeconds
        if (remainingSeconds <= 0.0) break

        val reactionVideo = recording.videos.firstOrNull() ?: continue
        val reactionBlock = addReactionRecording(
            engine = engine,
            recording = recording,
            reactionVideo = reactionVideo,
            parent = reactionTrack,
        )

        val recordingDurationSeconds = recording.duration.toDouble(DurationUnit.SECONDS)
        val clipDurationSeconds = minOf(recordingDurationSeconds, remainingSeconds)
        if (clipDurationSeconds < recordingDurationSeconds) {
            engine.block.setDuration(reactionBlock, duration = clipDurationSeconds)
        }

        reactionOffsetSeconds += clipDurationSeconds
        reactionBlocks += reactionBlock
    }

    val finalDurationSeconds = minOf(reactionOffsetSeconds, baseDurationSeconds)
    engine.block.setTrimOffset(baseFill, offset = 0.0)
    engine.block.setTrimLength(baseFill, length = finalDurationSeconds)
    engine.block.setDuration(baseVideoBlock, duration = finalDurationSeconds)
    // highlight-android-sync-duration

    return ReactionSceneComposition(
        page = page,
        baseVideoBlock = baseVideoBlock,
        reactionTrack = reactionTrack,
        reactionBlocks = reactionBlocks,
        durationSeconds = finalDurationSeconds,
    )
}

// highlight-android-add-reaction-clips
private fun addReactionRecording(
    engine: Engine,
    recording: Recording,
    reactionVideo: Video,
    parent: DesignBlock,
): DesignBlock {
    val reactionBlock = engine.block.create(DesignBlockType.Graphic)
    val shape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = reactionBlock, shape = shape)
    setFrame(engine = engine, designBlock = reactionBlock, rect = reactionVideo.rect)

    val fill = engine.block.createFill(FillType.Video)
    // Point the video fill at the recorded reaction segment.
    engine.block.setUri(
        block = fill,
        property = "fill/video/fileURI",
        value = reactionVideo.uri,
    )
    engine.block.setFill(block = reactionBlock, fill = fill)
    engine.block.setDuration(reactionBlock, duration = recording.duration.toDouble(DurationUnit.SECONDS))
    engine.block.appendChild(parent = parent, child = reactionBlock)

    return reactionBlock
}
// highlight-android-add-reaction-clips

// highlight-android-rect-frame
private fun setFrame(
    engine: Engine,
    designBlock: DesignBlock,
    rect: RectF,
) {
    engine.block.setWidth(block = designBlock, value = rect.width())
    engine.block.setHeight(block = designBlock, value = rect.height())
    engine.block.setPositionX(block = designBlock, value = rect.left)
    engine.block.setPositionY(block = designBlock, value = rect.top)
}
// highlight-android-rect-frame
