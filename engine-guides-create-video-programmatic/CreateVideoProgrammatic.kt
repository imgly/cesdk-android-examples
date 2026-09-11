import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportVideoOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File

suspend fun createVideoProgrammatic(engine: Engine): File = withContext(Dispatchers.Main) {
    // highlight-android-create-scene
    val scene = engine.scene.createForVideo()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-create-scene

    // highlight-android-add-video-clips
    val introClip = createVideoClip(
        engine,
        Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4"),
    )
    val detailClip = createVideoClip(
        engine,
        Uri.parse("https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-kampus-production-8154913.mp4"),
    )
    // highlight-android-add-video-clips

    // highlight-android-arrange-track
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.appendChild(parent = track, child = introClip.block)
    engine.block.appendChild(parent = track, child = detailClip.block)
    engine.block.fillParent(track)
    // highlight-android-arrange-track

    // highlight-android-load-media-and-timing
    // Keep the guide export short; use the clip length your app needs.
    val sampleClipDurationSeconds = 2.0

    engine.block.forceLoadAVResource(introClip.fill)
    val introDuration = sampleClipDurationSeconds.coerceAtMost(engine.block.getAVResourceTotalDuration(introClip.fill))
    check(introDuration > 0.0) { "The intro video must contain playable media." }
    engine.block.setDuration(introClip.block, duration = introDuration)

    engine.block.forceLoadAVResource(detailClip.fill)
    val detailDuration = sampleClipDurationSeconds.coerceAtMost(engine.block.getAVResourceTotalDuration(detailClip.fill))
    check(detailDuration > 0.0) { "The detail video must contain playable media." }
    engine.block.setDuration(detailClip.block, duration = detailDuration)

    val pageDuration = introDuration + detailDuration
    engine.block.setDuration(page, duration = pageDuration)
    // highlight-android-load-media-and-timing

    // highlight-android-export-video
    val logTag = "CreateVideoGuide"
    // Export a compact preview file; use your delivery size and frame rate in production.
    val previewExportWidth = 640F
    val previewExportHeight = 360F
    val previewFrameRate = 15F

    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(page),
        mimeType = MimeType.MP4,
        progressCallback = { progress ->
            Log.i(
                logTag,
                "Rendered ${progress.renderedFrames} frames and encoded ${progress.encodedFrames} " +
                    "frames out of ${progress.totalFrames} frames",
            )
        },
        options = ExportVideoOptions(
            targetWidth = previewExportWidth,
            targetHeight = previewExportHeight,
            frameRate = previewFrameRate,
        ),
    )
    // highlight-android-export-video

    // highlight-android-write-file
    val outputFile = withContext(Dispatchers.IO) {
        val outputFile = File.createTempFile("programmatic-video-", ".mp4")
        val bytes = ByteArray(videoBytes.remaining())
        videoBytes.get(bytes)
        outputFile.outputStream().use { output ->
            output.write(bytes)
        }
        outputFile
    }
    check(outputFile.length() > 0L) { "The exported MP4 file must not be empty." }
    // highlight-android-write-file

    outputFile
}

// highlight-android-create-video-clip-helper
private data class VideoClip(
    val block: DesignBlock,
    val fill: DesignBlock,
)

private fun createVideoClip(
    engine: Engine,
    videoUri: Uri,
): VideoClip {
    val clip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(clip, shape = engine.block.createShape(ShapeType.Rect))

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        // Video fills read their media source from this Engine property key.
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(clip, fill = videoFill)

    return VideoClip(block = clip, fill = videoFill)
}
// highlight-android-create-video-clip-helper

// highlight-android-create-from-video
suspend fun createSingleSourceVideoScene(engine: Engine): DesignBlock {
    val videoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")
    val scene = engine.scene.createFromVideo(videoUri)

    return scene
}
// highlight-android-create-from-video
