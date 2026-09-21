import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DemoAssetSource
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.ShapeType
import ly.img.engine.SplitOptions
import ly.img.engine.addDemoAssetSources
import kotlin.math.abs

data class SplitTiming(
    val originalTrimOffsetBefore: Double,
    val originalTrimLengthBefore: Double,
    val originalTrimOffsetAfter: Double,
    val originalTrimLengthAfter: Double,
    val newBlockTrimOffset: Double,
    val newBlockTrimLength: Double,
)

data class SplitGuideResult(
    val basicSegmentDurations: List<Double>,
    val audioSegmentDurations: List<Double>,
    val playheadSegmentDurations: List<Double>,
    val multiTrackSegmentDurations: List<Double>,
    val splitTiming: SplitTiming,
    val audioSplitTiming: SplitTiming,
    val remainingSegmentCount: Int,
    val validatedSplitCreated: Boolean,
)

suspend fun splitVideoAndAudio(engine: Engine): SplitGuideResult = withContext(Dispatchers.Main) {
    runSplitGuide(engine)
}

private suspend fun runSplitGuide(engine: Engine): SplitGuideResult {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 12.0)

    val basicClip = createVideoClip(engine, page, name = "Basic split", videoUri = VIDEO_URI)
    val basicNewClip = splitAtSpecificTime(engine, basicClip)
    checkClose(engine.block.getDuration(basicClip), 5.0, "basic first segment duration")
    checkClose(engine.block.getDuration(basicNewClip), 5.0, "basic second segment duration")

    val audioUri = loadDemoAudioUri(engine)

    val audioClip = createAudioClip(engine, page, name = "Audio split", audioUri = audioUri)
    val audioNewClip = splitAtSpecificTime(engine, audioClip)
    checkClose(engine.block.getDuration(audioClip), 5.0, "audio first segment duration")
    checkClose(engine.block.getDuration(audioNewClip), 5.0, "audio second segment duration")

    val optionsClip = createVideoClip(engine, page, name = "Options split", videoUri = VIDEO_URI)
    splitWithOptions(engine, optionsClip)

    val playheadClip = createVideoClip(
        engine = engine,
        page = page,
        name = "Playhead split",
        videoUri = VIDEO_URI,
        trackOffset = 1.0,
    )
    engine.block.setPlaybackTime(page, time = 5.0)
    val playheadNewClip = splitAtPlayhead(engine, page, playheadClip)
    checkClose(engine.block.getDuration(playheadClip), 4.0, "playhead first segment duration")
    checkClose(engine.block.getDuration(playheadNewClip), 6.0, "playhead second segment duration")

    val multiTrackPage = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = multiTrackPage)
    engine.block.setWidth(multiTrackPage, value = 1280F)
    engine.block.setHeight(multiTrackPage, value = 720F)
    engine.block.setDuration(multiTrackPage, duration = 12.0)
    val multiTrackClipA = createVideoClip(
        engine = engine,
        page = multiTrackPage,
        name = "Track A",
        videoUri = VIDEO_URI,
        trackOffset = 1.0,
    )
    val multiTrackClipB = createVideoClip(
        engine = engine,
        page = multiTrackPage,
        name = "Track B",
        videoUri = VIDEO_URI,
        trackOffset = 2.0,
    )
    val multiTrackNewSegments = splitClipsAcrossTracks(
        engine = engine,
        page = multiTrackPage,
        timelineTime = 4.0,
    )
    check(multiTrackNewSegments.size == 2)
    val multiTrackSegmentDurations = listOf(
        engine.block.getDuration(multiTrackClipA),
        engine.block.getDuration(multiTrackNewSegments[0]),
        engine.block.getDuration(multiTrackClipB),
        engine.block.getDuration(multiTrackNewSegments[1]),
    )
    multiTrackSegmentDurations.forEachIndexed { index, duration ->
        val expected = listOf(3.0, 7.0, 2.0, 8.0)[index]
        checkClose(duration, expected, "multi-track segment $index duration")
    }

    val resultsClip = createVideoClip(engine, page, name = "Results split", videoUri = VIDEO_URI)
    val splitTiming = readSplitTimingAfterSplit(engine, resultsClip)
    checkClose(splitTiming.originalTrimOffsetAfter, splitTiming.originalTrimOffsetBefore, "original trim offset")
    checkClose(splitTiming.originalTrimLengthAfter, 6.0, "original trim length")
    checkClose(splitTiming.newBlockTrimOffset, splitTiming.originalTrimOffsetBefore + 6.0, "new trim offset")
    checkClose(splitTiming.newBlockTrimLength, 4.0, "new trim length")
    val audioResultsClip = createAudioClip(engine, page, name = "Audio results split", audioUri = audioUri)
    val audioSplitTiming = readSplitTimingAfterSplit(engine, audioResultsClip)
    checkClose(audioSplitTiming.originalTrimLengthAfter, 6.0, "audio original trim length")
    checkClose(audioSplitTiming.newBlockTrimOffset, 6.0, "audio new trim offset")
    checkClose(audioSplitTiming.newBlockTrimLength, 4.0, "audio new trim length")

    val deleteClip = createVideoClip(engine, page, name = "Split and delete", videoUri = VIDEO_URI)
    val keptAfterDeletedRange = splitAndDeleteRange(
        engine = engine,
        clipBlock = deleteClip,
        startTime = 2.0,
        endTime = 5.0,
    )
    check(engine.block.isValid(deleteClip))
    check(engine.block.isValid(keptAfterDeletedRange))
    checkClose(engine.block.getDuration(deleteClip), 2.0, "leading segment duration")
    checkClose(engine.block.getDuration(keptAfterDeletedRange), 5.0, "trailing segment duration")
    val remainingSegmentCount = listOf(deleteClip, keptAfterDeletedRange)
        .count { segment -> engine.block.isValid(segment) }

    val validateClip = createVideoClip(engine, page, name = "Validated split", videoUri = VIDEO_URI)
    val validatedSplit = splitWithValidation(
        engine = engine,
        clipBlock = validateClip,
        desiredSplitTime = 4.0,
    )
    val validatedSplitCreated = validatedSplit?.let { splitBlock ->
        engine.block.isValid(splitBlock)
    } == true
    check(validatedSplitCreated)

    return SplitGuideResult(
        basicSegmentDurations = listOf(
            engine.block.getDuration(basicClip),
            engine.block.getDuration(basicNewClip),
        ),
        audioSegmentDurations = listOf(
            engine.block.getDuration(audioClip),
            engine.block.getDuration(audioNewClip),
        ),
        playheadSegmentDurations = listOf(
            engine.block.getDuration(playheadClip),
            engine.block.getDuration(playheadNewClip),
        ),
        multiTrackSegmentDurations = multiTrackSegmentDurations,
        splitTiming = splitTiming,
        audioSplitTiming = audioSplitTiming,
        remainingSegmentCount = remainingSegmentCount,
        validatedSplitCreated = validatedSplitCreated,
    )
}

// highlight-android-basic-split
fun splitAtSpecificTime(
    engine: Engine,
    clipBlock: DesignBlock,
): DesignBlock {
    val splitTime = 5.0
    return engine.block.split(block = clipBlock, atTime = splitTime)
}
// highlight-android-basic-split

// highlight-android-split-options
fun splitWithOptions(
    engine: Engine,
    clipBlock: DesignBlock,
): DesignBlock = engine.block.split(
    block = clipBlock,
    atTime = 4.0,
    options = SplitOptions(
        attachToParent = true,
        createParentTrackIfNeeded = true,
        selectNewBlock = false,
    ),
)
// highlight-android-split-options

// highlight-android-split-at-playhead
fun splitAtPlayhead(
    engine: Engine,
    page: DesignBlock,
    clipBlock: DesignBlock,
): DesignBlock {
    val playheadTime = engine.block.getPlaybackTime(page)
    val clipStartTime = absoluteTimelineOffset(
        engine = engine,
        timelineRoot = page,
        block = clipBlock,
    )
    val splitTime = playheadTime - clipStartTime
    val clipDuration = engine.block.getDuration(clipBlock)

    require(splitTime > 0.0 && splitTime < clipDuration) {
        "Split time must be inside the clip duration."
    }

    return engine.block.split(block = clipBlock, atTime = splitTime)
}
// highlight-android-split-at-playhead

// highlight-android-absolute-timeline-offset
fun absoluteTimelineOffset(
    engine: Engine,
    timelineRoot: DesignBlock,
    block: DesignBlock,
): Double {
    var timelineOffset = 0.0
    var currentBlock: DesignBlock? = block

    while (currentBlock != null && currentBlock != timelineRoot) {
        if (engine.block.supportsTimeOffset(currentBlock)) {
            timelineOffset += engine.block.getTimeOffset(currentBlock)
        }
        currentBlock = engine.block.getParent(currentBlock)
    }

    return timelineOffset
}
// highlight-android-absolute-timeline-offset

// highlight-android-split-multiple-tracks
fun splitClipsAcrossTracks(
    engine: Engine,
    page: DesignBlock,
    timelineTime: Double,
): List<DesignBlock> {
    val splitBlocks = mutableListOf<DesignBlock>()

    engine.block.getChildren(page)
        .filter { child -> engine.block.getType(child) == DesignBlockType.Track.key }
        .forEach { track ->
            engine.block.getChildren(track).forEach { clip ->
                val clipStartTime = absoluteTimelineOffset(
                    engine = engine,
                    timelineRoot = page,
                    block = clip,
                )
                val clipDuration = engine.block.getDuration(clip)
                val splitTime = timelineTime - clipStartTime

                if (splitTime > 0.0 && splitTime < clipDuration) {
                    splitBlocks += engine.block.split(
                        block = clip,
                        atTime = splitTime,
                        options = SplitOptions(selectNewBlock = false),
                    )
                }
            }
        }

    return splitBlocks
}
// highlight-android-split-multiple-tracks

// highlight-android-split-results
private fun trimmableTargetForClip(
    engine: Engine,
    clipBlock: DesignBlock,
): DesignBlock {
    val clipType = engine.block.getType(clipBlock)
    val trimTarget = if (clipType == DesignBlockType.Audio.key) {
        clipBlock
    } else {
        engine.block.getFill(clipBlock)
    }

    require(engine.block.supportsTrim(trimTarget)) {
        "Clip does not expose trim properties."
    }

    return trimTarget
}

fun readSplitTimingAfterSplit(
    engine: Engine,
    clipBlock: DesignBlock,
): SplitTiming {
    val originalTrimTarget = trimmableTargetForClip(engine, clipBlock)
    val originalTrimOffset = engine.block.getTrimOffset(originalTrimTarget)
    val originalTrimLength = engine.block.getTrimLength(originalTrimTarget)

    val newBlock = engine.block.split(block = clipBlock, atTime = 6.0)
    val newBlockTrimTarget = trimmableTargetForClip(engine, newBlock)

    return SplitTiming(
        originalTrimOffsetBefore = originalTrimOffset,
        originalTrimLengthBefore = originalTrimLength,
        originalTrimOffsetAfter = engine.block.getTrimOffset(originalTrimTarget),
        originalTrimLengthAfter = engine.block.getTrimLength(originalTrimTarget),
        newBlockTrimOffset = engine.block.getTrimOffset(newBlockTrimTarget),
        newBlockTrimLength = engine.block.getTrimLength(newBlockTrimTarget),
    )
}
// highlight-android-split-results

// highlight-android-split-and-delete
fun splitAndDeleteRange(
    engine: Engine,
    clipBlock: DesignBlock,
    startTime: Double,
    endTime: Double,
): DesignBlock {
    val middleSegment = engine.block.split(
        block = clipBlock,
        atTime = startTime,
        options = SplitOptions(selectNewBlock = false),
    )
    val trailingSegment = engine.block.split(
        block = middleSegment,
        atTime = endTime - startTime,
        options = SplitOptions(selectNewBlock = false),
    )

    engine.block.destroy(middleSegment)
    return trailingSegment
}
// highlight-android-split-and-delete

// highlight-android-validate-split-time
fun splitWithValidation(
    engine: Engine,
    clipBlock: DesignBlock,
    desiredSplitTime: Double,
): DesignBlock? {
    val blockDuration = engine.block.getDuration(clipBlock)

    return if (desiredSplitTime > 0.0 && desiredSplitTime < blockDuration) {
        engine.block.split(block = clipBlock, atTime = desiredSplitTime)
    } else {
        null
    }
}
// highlight-android-validate-split-time

suspend fun createVideoClip(
    engine: Engine,
    page: DesignBlock,
    name: String,
    videoUri: String,
    trackOffset: Double = 0.0,
): DesignBlock {
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.fillParent(track)
    engine.block.setTimeOffset(block = track, offset = trackOffset)

    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(videoBlock, name)
    engine.block.setShape(videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(videoBlock, value = 320F)
    engine.block.setHeight(videoBlock, value = 180F)

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse(videoUri),
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)
    engine.block.appendChild(parent = track, child = videoBlock)
    engine.block.forceLoadAVResource(videoFill)
    engine.block.setDuration(videoBlock, duration = 10.0)

    return videoBlock
}

suspend fun createAudioClip(
    engine: Engine,
    page: DesignBlock,
    name: String,
    audioUri: String,
): DesignBlock {
    val audioBlock = engine.block.create(DesignBlockType.Audio)
    engine.block.setName(audioBlock, name)
    engine.block.setUri(
        block = audioBlock,
        property = "audio/fileURI",
        value = Uri.parse(audioUri),
    )
    engine.block.appendChild(parent = page, child = audioBlock)
    engine.block.forceLoadAVResource(audioBlock)
    engine.block.setDuration(audioBlock, duration = 10.0)

    return audioBlock
}

private suspend fun loadDemoAudioUri(engine: Engine): String {
    val audioSourceId = DemoAssetSource.AUDIO.key
    if (audioSourceId !in engine.asset.findAllSources()) {
        engine.addDemoAssetSources(exclude = DemoAssetSource.values().toSet() - DemoAssetSource.AUDIO)
    }

    val audioAsset = engine.asset.fetchAsset(
        sourceId = audioSourceId,
        assetId = "far_from_home",
    ) ?: engine.asset.findAssets(
        sourceId = audioSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    ).assets.first { it.id.endsWith("far_from_home") }

    return requireNotNull(audioAsset.meta?.get("uri")) {
        "The demo audio asset does not provide a URI."
    }
}

private fun checkClose(
    actual: Double,
    expected: Double,
    label: String,
) {
    check(abs(actual - expected) < 0.001) {
        "$label expected $expected but was $actual"
    }
}

private const val VIDEO_URI = "https://img.ly/static/ubq_video_samples/bbb.mp4"
