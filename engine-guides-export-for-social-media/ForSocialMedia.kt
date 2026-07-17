import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.ExportVideoOptions
import ly.img.engine.ExportVideoProgress
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File

suspend fun exportForSocialMedia(
    engine: Engine,
    progressObserver: (ExportVideoProgress) -> Unit = {},
): File = withContext(Dispatchers.Main) {
    // highlight-android-create-scene
    val scene = engine.scene.createForVideo()
    engine.scene.setDesignUnit(DesignUnit.PIXEL)

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(block = page, value = 1080F)
    engine.block.setHeight(block = page, value = 1920F)
    // highlight-android-create-scene

    // highlight-android-add-video
    val clip = createSocialMediaVideoClip(
        engine = engine,
        videoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4"),
    )

    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.appendChild(parent = track, child = clip.block)
    engine.block.fillParent(track)
    // highlight-android-add-video

    // highlight-android-load-media
    engine.block.forceLoadAVResource(block = clip.fill)
    // Keep this sample compact by exporting at most one second of source media.
    val exportDuration = 1.0.coerceAtMost(engine.block.getAVResourceTotalDuration(block = clip.fill))
    check(exportDuration > 0.0) { "The source video must contain playable media." }
    engine.block.setDuration(block = clip.block, duration = exportDuration)
    engine.block.setDuration(block = page, duration = exportDuration)
    // highlight-android-load-media

    // highlight-android-export-options
    val exportOptions = ExportVideoOptions(
        targetWidth = 1080F,
        targetHeight = 1920F,
        // 30 fps is a common default for short-form vertical video.
        frameRate = 30F,
        // 8 Mbps balances quality and upload size for short-form video.
        videoBitrate = 8_000_000,
    )
    // highlight-android-export-options

    // highlight-android-export-video
    val progressCallback: (ExportVideoProgress) -> Unit = { progress ->
        reportExportProgress(progress)
        progressObserver(progress)
    }

    val videoData = engine.block.exportVideo(
        block = page,
        // Start at the beginning of the page timeline.
        timeOffset = 0.0,
        duration = engine.block.getDuration(block = page),
        mimeType = MimeType.MP4,
        progressCallback = progressCallback,
        options = exportOptions,
    )

    check(videoData.remaining() > 0) { "The exported MP4 data must not be empty." }
    // highlight-android-export-video

    // highlight-android-save-file
    val outputFile = withContext(Dispatchers.IO) {
        val file = File.createTempFile("social-media-export-", ".mp4")
        file.outputStream().use { output ->
            val buffer = videoData.asReadOnlyBuffer()
            while (buffer.hasRemaining()) {
                output.channel.write(buffer)
            }
        }
        file
    }
    check(outputFile.length() > 0L) { "The exported MP4 file must not be empty." }
    // highlight-android-save-file

    outputFile
}

// highlight-android-report-progress
private fun reportExportProgress(progress: ExportVideoProgress) {
    if (progress.totalFrames > 0) {
        val percent = (progress.encodedFrames.toFloat() / progress.totalFrames * 100).toInt()
        Log.i(
            "SocialMediaExport",
            "Encoded $percent% (${progress.encodedFrames}/${progress.totalFrames} frames)",
        )
    }
}
// highlight-android-report-progress

// highlight-android-create-video-clip-helper
private data class SocialMediaVideoClip(
    val block: DesignBlock,
    val fill: DesignBlock,
)

private fun createSocialMediaVideoClip(
    engine: Engine,
    videoUri: Uri,
): SocialMediaVideoClip {
    val clip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = clip, shape = engine.block.createShape(type = ShapeType.Rect))
    engine.block.setContentFillMode(block = clip, mode = ContentFillMode.COVER)

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(block = clip, fill = videoFill)

    return SocialMediaVideoClip(block = clip, fill = videoFill)
}
// highlight-android-create-video-clip-helper
