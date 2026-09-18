import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.MimeType
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

data class ThumbnailSize(
    val label: String,
    val width: Float,
    val height: Float,
)

data class ThumbnailExportResult(
    val smallJpeg: ByteBuffer,
    val mediumJpeg: ByteBuffer,
    val pngPreview: ByteBuffer,
    val savedMediumJpeg: File,
    val savedPngPreview: File,
)

suspend fun createThumbnail(
    engine: Engine,
    outputDir: File,
): ThumbnailExportResult {
    // highlight-android-select-page
    val page = engine.scene.getCurrentPage()
        ?: engine.scene.getPages().firstOrNull()
        ?: error("Load a scene with at least one page before exporting thumbnails.")
    // highlight-android-select-page

    val smallSize = ThumbnailSize(label = "small", width = 150F, height = 150F)
    val mediumSize = ThumbnailSize(label = "medium", width = 400F, height = 300F)

    val smallJpeg = exportJpegThumbnail(engine, page, smallSize)
    val mediumJpeg = exportJpegThumbnail(engine, page, mediumSize)
    val pngPreview = exportPngThumbnail(engine, page)
    val savedMediumJpeg = saveThumbnailToFile(
        buffer = mediumJpeg.asReadOnlyBuffer(),
        outputFile = File(outputDir, "thumbnail-medium.jpg"),
    )
    val savedPngPreview = saveThumbnailToFile(
        buffer = pngPreview.asReadOnlyBuffer(),
        outputFile = File(outputDir, "thumbnail-preview.png"),
    )

    return ThumbnailExportResult(
        smallJpeg = smallJpeg,
        mediumJpeg = mediumJpeg,
        pngPreview = pngPreview,
        savedMediumJpeg = savedMediumJpeg,
        savedPngPreview = savedPngPreview,
    )
}

// highlight-android-export-thumbnail
suspend fun exportJpegThumbnail(
    engine: Engine,
    page: DesignBlock,
    size: ThumbnailSize,
): ByteBuffer {
    val options = ExportOptions(
        targetWidth = size.width,
        targetHeight = size.height,
        jpegQuality = 0.8F,
    )

    val thumbnail = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = options,
    )

    check(thumbnail.hasRemaining()) { "${size.label} thumbnail is empty" }
    return thumbnail
}
// highlight-android-export-thumbnail

// highlight-android-export-png
suspend fun exportPngThumbnail(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(
        targetWidth = 400F,
        targetHeight = 300F,
        pngCompressionLevel = 6,
    )

    val thumbnail = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = options,
    )

    check(thumbnail.hasRemaining()) { "PNG thumbnail is empty" }
    return thumbnail
}
// highlight-android-export-png

// highlight-android-export-multiple
suspend fun exportThumbnailSet(
    engine: Engine,
    page: DesignBlock,
): Map<String, ByteBuffer> {
    val sizes = listOf(
        ThumbnailSize(label = "small", width = 150F, height = 150F),
        ThumbnailSize(label = "medium", width = 400F, height = 300F),
        ThumbnailSize(label = "large", width = 800F, height = 600F),
    )

    return sizes.associate { size ->
        size.label to exportJpegThumbnail(engine, page, size)
    }
}
// highlight-android-export-multiple

// highlight-android-save-file
suspend fun saveThumbnailToFile(
    buffer: ByteBuffer,
    outputFile: File,
): File = withContext(Dispatchers.IO) {
    outputFile.parentFile?.mkdirs()

    val readableBuffer = buffer.asReadOnlyBuffer()
    FileOutputStream(outputFile).channel.use { channel ->
        while (readableBuffer.hasRemaining()) {
            channel.write(readableBuffer)
        }
    }

    check(outputFile.length() > 0L) { "Saved thumbnail is empty" }
    outputFile
}
// highlight-android-save-file
