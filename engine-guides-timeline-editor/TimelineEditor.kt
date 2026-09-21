import android.net.Uri
import kotlinx.coroutines.flow.toList
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

data class TimelineEditor(
    val pageDuration: Double,
    val primaryClipDuration: Double,
    val overlayStartTime: Double,
    val videoThumbnailCount: Int,
    val audioWaveformChunkCount: Int,
    val exportedVideoDuration: Double,
    val exportedVideoBytes: Int,
)

suspend fun timelineEditor(engine: Engine): TimelineEditor {
    // highlight-android-create-video-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 10.0)
    // highlight-android-create-video-scene

    // highlight-android-create-tracks
    val primaryTrack = engine.block.create(DesignBlockType.Track)
    val overlayTrack = engine.block.create(DesignBlockType.Track)
    val audioTrack = engine.block.create(DesignBlockType.Track)

    engine.block.appendChild(parent = page, child = primaryTrack)
    engine.block.appendChild(parent = page, child = overlayTrack)
    engine.block.appendChild(parent = page, child = audioTrack)

    // No type-safe Android helper exists for this track property yet.
    engine.block.setBoolean(
        block = overlayTrack,
        property = "track/automaticallyManageBlockOffsets",
        value = false,
    )
    // highlight-android-create-tracks

    // highlight-android-add-video-clips
    val primaryClip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(primaryClip, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(primaryClip, value = 0F)
    engine.block.setPositionY(primaryClip, value = 0F)
    engine.block.setWidth(primaryClip, value = 1280F)
    engine.block.setHeight(primaryClip, value = 720F)

    val primaryFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = primaryFill,
        property = "fill/video/fileURI",
        value = Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        ),
    )
    engine.block.setFill(primaryClip, fill = primaryFill)
    engine.block.appendChild(parent = primaryTrack, child = primaryClip)

    val overlayClip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(overlayClip, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(overlayClip, value = 820F)
    engine.block.setPositionY(overlayClip, value = 80F)
    engine.block.setWidth(overlayClip, value = 360F)
    engine.block.setHeight(overlayClip, value = 220F)

    val overlayFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = overlayFill,
        property = "fill/video/fileURI",
        value = Uri.parse(
            "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-kampus-production-8154913.mp4",
        ),
    )
    engine.block.setFill(overlayClip, fill = overlayFill)
    engine.block.appendChild(parent = overlayTrack, child = overlayClip)
    // highlight-android-add-video-clips

    // highlight-android-add-audio
    val audioClip = engine.block.create(DesignBlockType.Audio)
    engine.block.setUri(
        block = audioClip,
        property = "audio/fileURI",
        value = Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.audio/audios/far_from_home.m4a",
        ),
    )
    engine.block.appendChild(parent = audioTrack, child = audioClip)
    // highlight-android-add-audio

    // highlight-android-trim-and-position
    engine.block.forceLoadAVResource(primaryFill)
    engine.block.forceLoadAVResource(overlayFill)
    engine.block.forceLoadAVResource(audioClip)

    engine.block.setDuration(primaryClip, duration = 8.0)
    engine.block.setTrimOffset(primaryFill, offset = 2.0)
    engine.block.setTrimLength(primaryFill, length = 8.0)
    engine.block.setLooping(primaryFill, looping = false)
    engine.block.setMuted(primaryFill, muted = true)

    engine.block.setTimeOffset(overlayClip, offset = 3.0)
    engine.block.setDuration(overlayClip, duration = 4.0)
    engine.block.setTimeOffset(audioClip, offset = 0.0)
    engine.block.setDuration(audioClip, duration = 10.0)
    // highlight-android-trim-and-position

    // highlight-android-playback
    engine.block.setPlaybackTime(page, time = 3.5)
    check(engine.block.isVisibleAtCurrentPlaybackTime(overlayClip))

    engine.block.setPlaying(page, enabled = true)
    check(engine.block.isPlaying(page))
    engine.block.setPlaying(page, enabled = false)
    // highlight-android-playback

    // highlight-android-thumbnails
    val videoThumbnails = engine.block.generateVideoThumbnailSequence(
        block = primaryFill,
        thumbnailHeight = 72,
        timeBegin = 0.0,
        timeEnd = 8.0,
        numberOfFrames = 4,
    ).toList()

    val audioWaveformChunks = engine.block.generateAudioThumbnailSequence(
        block = audioClip,
        samplesPerChunk = 40,
        timeBegin = 0.0,
        timeEnd = 10.0,
        numberOfSamples = 160,
        numberOfChannels = 2,
    ).toList()
    // highlight-android-thumbnails

    // highlight-android-export
    val exportDuration = engine.block.getDuration(page)
    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = exportDuration,
        mimeType = MimeType.MP4,
        progressCallback = { progress ->
            println("Encoded ${progress.encodedFrames} of ${progress.totalFrames} frames")
        },
    )
    // highlight-android-export

    return TimelineEditor(
        pageDuration = engine.block.getDuration(page),
        primaryClipDuration = engine.block.getDuration(primaryClip),
        overlayStartTime = engine.block.getTimeOffset(overlayClip),
        videoThumbnailCount = videoThumbnails.size,
        audioWaveformChunkCount = audioWaveformChunks.size,
        exportedVideoDuration = exportDuration,
        exportedVideoBytes = videoBytes.remaining(),
    )
}
