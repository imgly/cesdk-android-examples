import android.app.Application
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

data class TrackClipState(
    val name: String,
    val timeOffset: Double,
    val duration: Double,
)

data class JoinAndArrangeVideoResult(
    val initialTrackClips: List<TrackClipState>,
    val reorderedTrackClips: List<TrackClipState>,
    val pageDuration: Double,
    val mainTrackDuration: Double,
    val overlayTrackOffset: Double,
    val overlayTrackDuration: Double,
    val overlayClipCount: Int,
)

suspend fun joinAndArrangeVideoClips(
    application: Application,
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): JoinAndArrangeVideoResult = withContext(Dispatchers.Main) {
    var engine: Engine? = null
    var engineStarted = false

    try {
        Engine.init(application)
        val currentEngine = Engine.getInstance(id = "ly.img.engine.join-and-arrange-video.example")
        engine = currentEngine
        engineStarted = currentEngine.start(license = license, userId = userId)
        currentEngine.bindOffscreen(width = 1920, height = 1080)

        // highlight-android-create-scene
        val scene = currentEngine.scene.createForVideo()
        val page = currentEngine.block.create(DesignBlockType.Page)
        currentEngine.block.appendChild(parent = scene, child = page)
        currentEngine.block.setWidth(page, value = 1920F)
        currentEngine.block.setHeight(page, value = 1080F)
        currentEngine.block.setDuration(page, duration = 15.0)
        // highlight-android-create-scene

        val videoUri = Uri.parse(
            "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/" +
                "pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        )

        // highlight-android-create-clips
        val clipA = createVideoClip(
            engine = currentEngine,
            name = "Clip A",
            videoUri = videoUri,
            width = 1920F,
            height = 1080F,
        )
        val clipB = createVideoClip(
            engine = currentEngine,
            name = "Clip B",
            videoUri = videoUri,
            width = 1920F,
            height = 1080F,
        )
        val clipC = createVideoClip(
            engine = currentEngine,
            name = "Clip C",
            videoUri = videoUri,
            width = 1920F,
            height = 1080F,
        )
        // highlight-android-create-clips

        // highlight-android-create-track
        val track = currentEngine.block.create(DesignBlockType.Track)
        currentEngine.block.appendChild(parent = page, child = track)
        currentEngine.block.setBoolean(
            block = track,
            property = "track/automaticallyManageBlockOffsets",
            value = false,
        )
        // highlight-android-create-track

        // highlight-android-add-clips-to-track
        currentEngine.block.appendChild(parent = track, child = clipA)
        currentEngine.block.appendChild(parent = track, child = clipB)
        currentEngine.block.appendChild(parent = track, child = clipC)

        currentEngine.block.fillParent(track)
        val initialTrackChildren = currentEngine.block.getChildren(track)
        check(initialTrackChildren == listOf(clipA, clipB, clipC))
        // highlight-android-add-clips-to-track

        // highlight-android-set-clip-durations
        currentEngine.block.setDuration(clipA, duration = 5.0)
        currentEngine.block.setDuration(clipB, duration = 5.0)
        currentEngine.block.setDuration(clipC, duration = 5.0)
        currentEngine.block.setDuration(track, duration = 15.0)
        // highlight-android-set-clip-durations

        // highlight-android-time-offsets
        currentEngine.block.setTimeOffset(clipA, offset = 0.0)
        currentEngine.block.setTimeOffset(clipB, offset = 5.0)
        currentEngine.block.setTimeOffset(clipC, offset = 10.0)
        val initialTrackDuration = currentEngine.block.getDuration(track)
        check(initialTrackDuration == 15.0)

        val initialClipStates = currentEngine.block.getChildren(track).map { clip ->
            TrackClipState(
                name = currentEngine.block.getName(clip),
                timeOffset = currentEngine.block.getTimeOffset(clip),
                duration = currentEngine.block.getDuration(clip),
            )
        }
        // highlight-android-time-offsets

        // highlight-android-reorder-clips
        currentEngine.block.insertChild(parent = track, child = clipC, index = 0)
        currentEngine.block.setTimeOffset(clipC, offset = 0.0)
        currentEngine.block.setTimeOffset(clipA, offset = 5.0)
        currentEngine.block.setTimeOffset(clipB, offset = 10.0)
        val reorderedTrackDuration = currentEngine.block.getDuration(track)
        check(reorderedTrackDuration == 15.0)

        val reorderedClipStates = currentEngine.block.getChildren(track).map { clip ->
            TrackClipState(
                name = currentEngine.block.getName(clip),
                timeOffset = currentEngine.block.getTimeOffset(clip),
                duration = currentEngine.block.getDuration(clip),
            )
        }
        // highlight-android-reorder-clips

        // highlight-android-query-track-children
        val finalClipOrder = currentEngine.block.getChildren(track).map { clip ->
            currentEngine.block.getName(clip)
        }
        val finalClipOffsets = currentEngine.block.getChildren(track).map { clip ->
            currentEngine.block.getTimeOffset(clip)
        }
        check(finalClipOrder == listOf("Clip C", "Clip A", "Clip B"))
        check(finalClipOffsets == listOf(0.0, 5.0, 10.0))
        // highlight-android-query-track-children

        // highlight-android-multi-track
        val overlayTrack = currentEngine.block.create(DesignBlockType.Track)
        currentEngine.block.appendChild(parent = page, child = overlayTrack)
        currentEngine.block.setTimeOffset(overlayTrack, offset = 2.0)

        val overlayClip = createVideoClip(
            engine = currentEngine,
            name = "Overlay Clip",
            videoUri = videoUri,
            width = 1920F / 4F,
            height = 1080F / 4F,
        )
        currentEngine.block.setDuration(overlayClip, duration = 5.0)
        currentEngine.block.appendChild(parent = overlayTrack, child = overlayClip)
        currentEngine.block.setPositionX(overlayClip, value = 1920F - 1920F / 4F - 40F)
        currentEngine.block.setPositionY(overlayClip, value = 1080F - 1080F / 4F - 40F)
        // highlight-android-multi-track

        JoinAndArrangeVideoResult(
            initialTrackClips = initialClipStates,
            reorderedTrackClips = reorderedClipStates,
            pageDuration = currentEngine.block.getDuration(page),
            mainTrackDuration = reorderedTrackDuration,
            overlayTrackOffset = currentEngine.block.getTimeOffset(overlayTrack),
            overlayTrackDuration = currentEngine.block.getDuration(overlayTrack),
            overlayClipCount = currentEngine.block.getChildren(overlayTrack).size,
        )
    } finally {
        if (engineStarted) {
            engine?.stop()
        }
    }
}

// highlight-android-create-video-helper
private suspend fun createVideoClip(
    engine: Engine,
    name: String,
    videoUri: Uri,
    width: Float,
    height: Float,
): DesignBlock {
    val clip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(clip, name)
    engine.block.setShape(clip, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(clip, value = width)
    engine.block.setHeight(clip, value = height)

    val videoFill = engine.block.createFill(FillType.Video)
    // The Android binding has no typed property helper for video fill URIs yet.
    engine.block.setUri(block = videoFill, property = "fill/video/fileURI", value = videoUri)
    engine.block.setFill(block = clip, fill = videoFill)
    engine.block.setContentFillMode(block = clip, mode = ContentFillMode.COVER)
    engine.block.forceLoadAVResource(block = videoFill)

    return clip
}
// highlight-android-create-video-helper
