import android.net.Uri
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

suspend fun joinAndArrangeVideoClips(engine: Engine): JoinAndArrangeVideoResult = withContext(engine.dispatcher) {
    // highlight-android-create-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1920F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.setDuration(page, duration = 15.0)
    // highlight-android-create-scene

    val videoUri = Uri.parse(
        "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/" +
            "pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
    )

    // highlight-android-create-clips
    val clipA = createVideoClip(
        engine = engine,
        name = "Clip A",
        videoUri = videoUri,
        width = 1920F,
        height = 1080F,
    )
    val clipB = createVideoClip(
        engine = engine,
        name = "Clip B",
        videoUri = videoUri,
        width = 1920F,
        height = 1080F,
    )
    val clipC = createVideoClip(
        engine = engine,
        name = "Clip C",
        videoUri = videoUri,
        width = 1920F,
        height = 1080F,
    )
    // highlight-android-create-clips

    // highlight-android-create-track
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.setBoolean(
        block = track,
        property = "track/automaticallyManageBlockOffsets",
        value = false,
    )
    // highlight-android-create-track

    // highlight-android-add-clips-to-track
    engine.block.appendChild(parent = track, child = clipA)
    engine.block.appendChild(parent = track, child = clipB)
    engine.block.appendChild(parent = track, child = clipC)

    engine.block.fillParent(track)
    val initialTrackChildren = engine.block.getChildren(track)
    check(initialTrackChildren == listOf(clipA, clipB, clipC))
    // highlight-android-add-clips-to-track

    // highlight-android-set-clip-durations
    engine.block.setDuration(clipA, duration = 5.0)
    engine.block.setDuration(clipB, duration = 5.0)
    engine.block.setDuration(clipC, duration = 5.0)
    engine.block.setDuration(track, duration = 15.0)
    // highlight-android-set-clip-durations

    // highlight-android-time-offsets
    engine.block.setTimeOffset(clipA, offset = 0.0)
    engine.block.setTimeOffset(clipB, offset = 5.0)
    engine.block.setTimeOffset(clipC, offset = 10.0)
    val initialTrackDuration = engine.block.getDuration(track)
    check(initialTrackDuration == 15.0)

    val initialClipStates = engine.block.getChildren(track).map { clip ->
        TrackClipState(
            name = engine.block.getName(clip),
            timeOffset = engine.block.getTimeOffset(clip),
            duration = engine.block.getDuration(clip),
        )
    }
    // highlight-android-time-offsets

    // highlight-android-reorder-clips
    engine.block.insertChild(parent = track, child = clipC, index = 0)
    engine.block.setTimeOffset(clipC, offset = 0.0)
    engine.block.setTimeOffset(clipA, offset = 5.0)
    engine.block.setTimeOffset(clipB, offset = 10.0)
    val reorderedTrackDuration = engine.block.getDuration(track)
    check(reorderedTrackDuration == 15.0)

    val reorderedClipStates = engine.block.getChildren(track).map { clip ->
        TrackClipState(
            name = engine.block.getName(clip),
            timeOffset = engine.block.getTimeOffset(clip),
            duration = engine.block.getDuration(clip),
        )
    }
    // highlight-android-reorder-clips

    // highlight-android-query-track-children
    val finalClipOrder = engine.block.getChildren(track).map { clip ->
        engine.block.getName(clip)
    }
    val finalClipOffsets = engine.block.getChildren(track).map { clip ->
        engine.block.getTimeOffset(clip)
    }
    check(finalClipOrder == listOf("Clip C", "Clip A", "Clip B"))
    check(finalClipOffsets == listOf(0.0, 5.0, 10.0))
    // highlight-android-query-track-children

    // highlight-android-multi-track
    val overlayTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = overlayTrack)
    engine.block.setTimeOffset(overlayTrack, offset = 2.0)

    val overlayClip = createVideoClip(
        engine = engine,
        name = "Overlay Clip",
        videoUri = videoUri,
        width = 1920F / 4F,
        height = 1080F / 4F,
    )
    engine.block.setDuration(overlayClip, duration = 5.0)
    engine.block.appendChild(parent = overlayTrack, child = overlayClip)
    engine.block.setPositionX(overlayClip, value = 1920F - 1920F / 4F - 40F)
    engine.block.setPositionY(overlayClip, value = 1080F - 1080F / 4F - 40F)
    // highlight-android-multi-track

    JoinAndArrangeVideoResult(
        initialTrackClips = initialClipStates,
        reorderedTrackClips = reorderedClipStates,
        pageDuration = engine.block.getDuration(page),
        mainTrackDuration = reorderedTrackDuration,
        overlayTrackOffset = engine.block.getTimeOffset(overlayTrack),
        overlayTrackDuration = engine.block.getDuration(overlayTrack),
        overlayClipCount = engine.block.getChildren(overlayTrack).size,
    )
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
    var videoFill: DesignBlock? = null

    try {
        engine.block.setName(clip, name)
        engine.block.setShape(clip, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setWidth(clip, value = width)
        engine.block.setHeight(clip, value = height)

        val fill = engine.block.createFill(FillType.Video)
        videoFill = fill
        // The Android binding has no typed property helper for video fill URIs yet.
        engine.block.setUri(block = fill, property = "fill/video/fileURI", value = videoUri)
        engine.block.setFill(block = clip, fill = fill)
        engine.block.setContentFillMode(block = clip, mode = ContentFillMode.COVER)
        engine.block.forceLoadAVResource(block = fill)

        return clip
    } catch (error: Throwable) {
        runCatching {
            if (engine.block.isValid(clip)) engine.block.destroy(clip)
        }
        videoFill?.let { fill ->
            runCatching {
                if (engine.block.isValid(fill)) engine.block.destroy(fill)
            }
        }
        throw error
    }
}
// highlight-android-create-video-helper
