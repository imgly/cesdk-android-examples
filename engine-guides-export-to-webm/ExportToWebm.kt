import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.AudioCodec
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportVideoOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.VideoCodec
import java.io.File
import java.nio.ByteBuffer

data class WebmExportResult(
    val outputFile: File,
    val codecExport: ByteBuffer,
    val alphaExport: ByteBuffer,
)

suspend fun exportToWebm(engine: Engine): WebmExportResult = withContext(Dispatchers.Main) {
    val page = createVideoPage(engine)
    val pageDuration = engine.block.getDuration(page)

    val defaultExport = exportWebm(engine, page, pageDuration)
    val codecExport = exportWebmWithCodecs(engine, page, pageDuration)
    val alphaExport = exportWebmWithAlpha(engine, page, pageDuration)

    WebmExportResult(
        outputFile = writeWebmToTempFile(defaultExport),
        codecExport = codecExport,
        alphaExport = alphaExport,
    )
}

// highlight-android-export-webm
suspend fun exportWebm(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
): ByteBuffer {
    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = pageDuration,
        mimeType = MimeType.WEBM,
        progressCallback = { progress ->
            Log.i(
                "ExportToWebmGuide",
                "Encoded ${progress.encodedFrames} of ${progress.totalFrames} frames",
            )
        },
    )

    check(videoBytes.hasRemaining()) { "WebM export is empty" }
    return videoBytes
}
// highlight-android-export-webm

// highlight-android-codecs
suspend fun exportWebmWithCodecs(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
): ByteBuffer {
    val options = ExportVideoOptions(
        videoCodec = VideoCodec.VP8,
        audioCodec = AudioCodec.VORBIS,
        videoBitrate = 4_000_000,
    )

    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = pageDuration,
        mimeType = MimeType.WEBM,
        progressCallback = {},
        options = options,
    )

    check(videoBytes.hasRemaining()) { "VP8 WebM export is empty" }
    return videoBytes
}
// highlight-android-codecs

// highlight-android-alpha
suspend fun exportWebmWithAlpha(
    engine: Engine,
    page: DesignBlock,
    pageDuration: Double,
): ByteBuffer {
    // Hide the page background, so only the blocks on the page are opaque.
    engine.block.setFillEnabled(page, enabled = false)
    val background = engine.block.getChildren(page).first()
    engine.block.setVisible(background, visible = false)

    try {
        val videoBytes = engine.block.exportVideo(
            block = page,
            timeOffset = 0.0,
            duration = pageDuration,
            mimeType = MimeType.WEBM,
            progressCallback = {},
            options = ExportVideoOptions(
                videoCodec = VideoCodec.VP9,
                exportAlpha = true,
            ),
        )

        check(videoBytes.hasRemaining()) { "alpha WebM export is empty" }
        return videoBytes
    } finally {
        engine.block.setVisible(background, visible = true)
        engine.block.setFillEnabled(page, enabled = true)
    }
}
// highlight-android-alpha

// highlight-android-write-file
suspend fun writeWebmToTempFile(videoBytes: ByteBuffer): File = withContext(Dispatchers.IO) {
    val outputFile = File.createTempFile("export-to-webm-", ".webm")
    val videoData = videoBytes.asReadOnlyBuffer()
    outputFile.outputStream().channel.use { channel ->
        while (videoData.hasRemaining()) {
            channel.write(videoData)
        }
    }

    check(outputFile.length() > 0L) { "WebM output file is empty" }
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
