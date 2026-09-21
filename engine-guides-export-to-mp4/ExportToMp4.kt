import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportVideoOptions
import ly.img.engine.ExportVideoProgress
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File
import java.nio.ByteBuffer
import kotlin.coroutines.cancellation.CancellationException

data class Mp4ExportResult(
    val outputFile: File,
    val progressEvents: List<ExportVideoProgress>,
    val cancelableExportCanceled: Boolean,
    val configuredExport: ByteBuffer,
    val partialExport: ByteBuffer,
)

suspend fun exportToMp4(engine: Engine): Mp4ExportResult = withContext(Dispatchers.Main) {
    val page = createVideoPage(engine)
    val pageDuration = engine.block.getDuration(page)

    val defaultExport = exportMp4(engine, page, pageDuration)
    val progressEvents = mutableListOf<ExportVideoProgress>()
    val progressExport = exportMp4WithProgress(engine, page, pageDuration, progressEvents)
    val cancelableJob = startCancelableMp4Export(
        scope = this,
        engine = engine,
        page = page,
        pageDuration = pageDuration,
        onProgress = {},
    )
    cancelMp4Export(cancelableJob)
    cancelableJob.join()
    val resolutionExport = exportMp4WithResolutionOptions(engine, page, pageDuration)
    val configuredExport = exportMp4WithBitrateOptions(engine, page, pageDuration)
    val partialExport = exportPartialTimeline(engine, page)

    check(progressExport.hasRemaining()) { "progress MP4 export is empty" }
    check(cancelableJob.isCancelled) { "MP4 export cancellation was not observed" }
    check(resolutionExport.hasRemaining()) { "resolution MP4 export is empty" }

    Mp4ExportResult(
        outputFile = writeMp4ToTempFile(defaultExport),
        progressEvents = progressEvents.toList(),
        cancelableExportCanceled = cancelableJob.isCancelled,
        configuredExport = configuredExport,
        partialExport = partialExport,
    )
}

// highlight-android-export-video
suspend fun exportMp4(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
): ByteBuffer {
    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = pageDuration,
        mimeType = MimeType.MP4,
        progressCallback = {},
    )

    check(videoBytes.hasRemaining()) { "MP4 export is empty" }
    return videoBytes
}
// highlight-android-export-video

// highlight-android-progress
suspend fun exportMp4WithProgress(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
    progressEvents: MutableList<ExportVideoProgress>,
): ByteBuffer {
    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = pageDuration,
        mimeType = MimeType.MP4,
        progressCallback = { progress ->
            progressEvents += progress
            Log.i(
                "ExportToMp4Guide",
                "Encoded ${progress.encodedFrames} of ${progress.totalFrames} frames",
            )
        },
    )

    check(videoBytes.hasRemaining()) { "progress MP4 export is empty" }
    return videoBytes
}
// highlight-android-progress

// highlight-android-cancel
fun startCancelableMp4Export(
    scope: CoroutineScope,
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
    onProgress: (ExportVideoProgress) -> Unit,
): Job = scope.launch(Dispatchers.Main) {
    try {
        val videoBytes = engine.block.exportVideo(
            block = page,
            timeOffset = 0.0,
            duration = pageDuration,
            mimeType = MimeType.MP4,
            progressCallback = onProgress,
        )
        check(videoBytes.hasRemaining()) { "cancelable MP4 export is empty" }
    } catch (exception: CancellationException) {
        Log.i("ExportToMp4Guide", "MP4 export canceled")
        throw exception
    }
}

fun cancelMp4Export(exportJob: Job) {
    exportJob.cancel()
}
// highlight-android-cancel

// highlight-android-resolution
suspend fun exportMp4WithResolutionOptions(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
): ByteBuffer {
    val options = ExportVideoOptions(
        targetWidth = 1280F,
        targetHeight = 720F,
        frameRate = 30F, // Use 30 fps for smooth playback on common mobile targets.
    )

    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = pageDuration,
        mimeType = MimeType.MP4,
        progressCallback = {},
        options = options,
    )

    check(videoBytes.hasRemaining()) { "resolution MP4 export is empty" }
    return videoBytes
}
// highlight-android-resolution

// highlight-android-bitrate
suspend fun exportMp4WithBitrateOptions(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
): ByteBuffer {
    val options = ExportVideoOptions(
        videoBitrate = 8_000_000,
        audioBitrate = 128_000,
    )

    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = pageDuration,
        mimeType = MimeType.MP4,
        progressCallback = {},
        options = options,
    )

    check(videoBytes.hasRemaining()) { "configured MP4 export is empty" }
    return videoBytes
}
// highlight-android-bitrate

// highlight-android-partial
suspend fun exportPartialTimeline(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.25, // Start after the first quarter-second to demonstrate offsets.
        duration = 0.5, // Export a short segment so partial exports stay fast.
        mimeType = MimeType.MP4,
        progressCallback = {},
        options = ExportVideoOptions(
            frameRate = 12F, // Lower fps keeps this short preview segment lightweight.
        ),
    )

    check(videoBytes.hasRemaining()) { "partial MP4 export is empty" }
    return videoBytes
}
// highlight-android-partial

// highlight-android-write-file
suspend fun writeMp4ToTempFile(videoBytes: ByteBuffer): File = withContext(Dispatchers.IO) {
    val outputFile = File.createTempFile("export-to-mp4-", ".mp4")
    val videoData = videoBytes.asReadOnlyBuffer()
    outputFile.outputStream().channel.use { channel ->
        while (videoData.hasRemaining()) {
            channel.write(videoData)
        }
    }

    check(outputFile.length() > 0L) { "MP4 output file is empty" }
    outputFile
}
// highlight-android-write-file

private fun createVideoPage(engine: Engine): DesignBlock {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 640F)
    engine.block.setHeight(page, value = 360F)
    engine.block.setDuration(page, duration = 1.0)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 640F)
    engine.block.setHeight(background, value = 360F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = background,
        color = Color.fromHex("#FF101827"),
    )
    engine.block.appendChild(parent = page, child = background)

    val accent = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(accent, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(accent, value = 320F)
    engine.block.setHeight(accent, value = 180F)
    engine.block.setPositionX(accent, value = 160F)
    engine.block.setPositionY(accent, value = 90F)
    engine.block.setFill(accent, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = accent,
        color = Color.fromHex("#FF42C3A7"),
    )
    engine.block.appendChild(parent = page, child = accent)

    return page
}
