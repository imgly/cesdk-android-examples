import android.app.Application
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportVideoOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SplitOptions
import java.nio.ByteBuffer

fun editVideoProgrammatically(
    application: Application,
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    exportProgrammaticVideoEdit(application = application, license = license, userId = userId)
}

suspend fun exportProgrammaticVideoEdit(
    application: Application,
    license: String?,
    userId: String,
): ProgrammaticVideoEditResult = withContext(Dispatchers.Main) {
    var engine: Engine? = null
    var engineStarted = false
    try {
        Engine.init(application)
        val currentEngine = Engine.getInstance(id = "ly.img.engine.programmatic-video-editing")
        engine = currentEngine
        engineStarted = currentEngine.start(license = license, userId = userId)
        currentEngine.bindOffscreen(width = 1280, height = 720)

        buildProgrammaticVideoEdit(currentEngine)
    } finally {
        if (engineStarted) {
            engine?.stop()
        }
    }
}

private suspend fun buildProgrammaticVideoEdit(engine: Engine): ProgrammaticVideoEditResult {
    // highlight-android-create-video-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 4.0)
    // highlight-android-create-video-scene

    // highlight-android-add-clips
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = track)
    engine.block.fillParent(track)

    val firstClip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(firstClip, shape = engine.block.createShape(ShapeType.Rect))
    val firstVideoFill = engine.block.createFill(FillType.Video)
    // Video fill sources use the generic property-keyed URI setter.
    engine.block.setUri(
        block = firstVideoFill,
        property = "fill/video/fileURI",
        value = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4"),
    )
    engine.block.setFill(block = firstClip, fill = firstVideoFill)
    engine.block.setContentFillMode(firstClip, mode = ContentFillMode.COVER)

    val secondClip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(secondClip, shape = engine.block.createShape(ShapeType.Rect))
    val secondVideoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = secondVideoFill,
        property = "fill/video/fileURI",
        value = Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        ),
    )
    engine.block.setFill(block = secondClip, fill = secondVideoFill)
    engine.block.setContentFillMode(secondClip, mode = ContentFillMode.COVER)

    engine.block.appendChild(parent = track, child = firstClip)
    engine.block.appendChild(parent = track, child = secondClip)
    // highlight-android-add-clips

    // highlight-android-change-timing-trim
    engine.block.forceLoadAVResource(block = firstVideoFill)
    engine.block.forceLoadAVResource(block = secondVideoFill)

    check(engine.block.getAVResourceTotalDuration(firstVideoFill) >= 3.0)
    engine.block.setDuration(block = firstClip, duration = 2.0)
    engine.block.setDuration(block = secondClip, duration = 2.0)
    engine.block.setTrimOffset(block = firstVideoFill, offset = 1.0)
    engine.block.setTrimLength(block = firstVideoFill, length = 2.0)
    // highlight-android-change-timing-trim

    // highlight-android-split-clip
    val secondSegment = engine.block.split(
        block = secondClip,
        atTime = 1.0,
        options = SplitOptions(selectNewBlock = false),
    )
    // highlight-android-split-clip

    // highlight-android-timed-overlay
    val overlay = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(overlay, shape = engine.block.createShape(ShapeType.Rect))
    val overlayFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = overlay, fill = overlayFill)
    engine.block.setFillSolidColor(
        block = overlay,
        color = Color.fromRGBA(r = 1F, g = 0.82F, b = 0.1F, a = 0.85F),
    )
    engine.block.setWidth(overlay, value = 1280F)
    engine.block.setHeight(overlay, value = 72F)
    engine.block.setPositionY(overlay, value = 648F)
    engine.block.setTimeOffset(block = overlay, offset = 1.25)
    engine.block.setDuration(block = overlay, duration = 1.5)
    engine.block.appendChild(parent = page, child = overlay)
    // highlight-android-timed-overlay

    // highlight-android-export-video
    val editedVideo = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(page),
        mimeType = MimeType.MP4,
        progressCallback = { progress ->
            println(
                "Rendered ${progress.renderedFrames} of ${progress.totalFrames} frames",
            )
        },
        options = ExportVideoOptions(
            videoBitrate = 8_000_000,
            audioBitrate = 128_000,
            frameRate = 30F,
            targetWidth = 1280F,
            targetHeight = 720F,
        ),
    )
    check(editedVideo.remaining() > 0)
    // highlight-android-export-video

    return ProgrammaticVideoEditResult(
        exportedVideo = editedVideo,
        pageDuration = engine.block.getDuration(page),
        firstClipTrimOffset = engine.block.getTrimOffset(firstVideoFill),
        firstClipTrimLength = engine.block.getTrimLength(firstVideoFill),
        splitSegmentDuration = engine.block.getDuration(secondSegment),
        overlayTimeOffset = engine.block.getTimeOffset(overlay),
    )
}

data class ProgrammaticVideoEditResult(
    val exportedVideo: ByteBuffer,
    val pageDuration: Double,
    val firstClipTrimOffset: Double,
    val firstClipTrimLength: Double,
    val splitSegmentDuration: Double,
    val overlayTimeOffset: Double,
)
