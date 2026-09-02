import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File
import java.nio.ByteBuffer

suspend fun exportToPdf(engine: Engine): List<File> {
    // Demo scaffolding: create renderable content for the export snippets. In
    // an app, start from the scene already loaded in the editor.
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setPositionX(block, value = 350F)
    engine.block.setPositionY(block, value = 250F)
    engine.block.setWidth(block, value = 100F)
    engine.block.setHeight(block, value = 100F)

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block, fill = fill)
    engine.block.setFillSolidColor(block, color = Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F))
    engine.block.appendChild(parent = page, child = block)

    // highlight-android-export-pdf
    val pdfData = engine.block.export(
        block = scene,
        mimeType = MimeType.PDF,
    )
    val defaultPdf = writePdfExport(fileName = "design-pages.pdf", buffer = pdfData)
    // highlight-android-export-pdf

    // highlight-android-progress
    // Report per-page progress as the PDF is written. The callback runs once
    // per page; only PDF exports invoke it.
    val progressData = engine.block.export(
        block = scene,
        mimeType = MimeType.PDF,
        progressCallback = { progress ->
            println("Exported ${progress.exportedPages} of ${progress.totalPages} pages")
        },
    )
    val progressPdf = writePdfExport(fileName = "design-with-progress.pdf", buffer = progressData)
    // highlight-android-progress

    // highlight-android-cancel
    // Cancelling the coroutine that runs the export stops the export itself.
    // Keep the job in your view model and cancel it from your Cancel button.
    coroutineScope {
        val exportJob = launch {
            try {
                engine.block.export(block = scene, mimeType = MimeType.PDF)
            } catch (cancellation: CancellationException) {
                // A cancelled export produces no data.
                println("Export cancelled")
                throw cancellation
            }
        }
        exportJob.cancel()
    }
    // highlight-android-cancel

    // highlight-android-high-compatibility
    val highCompatibilityOptions = ExportOptions(exportPdfWithHighCompatibility = true)
    val highCompatibilityData = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = highCompatibilityOptions,
    )
    val highCompatibilityPdf = writePdfExport(
        fileName = "design-high-compatibility.pdf",
        buffer = highCompatibilityData,
    )
    // highlight-android-high-compatibility

    // highlight-android-spot-color
    engine.editor.setSpotColor(
        name = "UnderlayerWhite",
        Color.fromRGBA(r = 0.8F, g = 0.8F, b = 0.8F),
    )
    // highlight-android-spot-color

    // highlight-android-underlayer
    val underlayerOptions = ExportOptions(
        exportPdfWithHighCompatibility = true,
        exportPdfWithUnderlayer = true,
        underlayerSpotColorName = "UnderlayerWhite",
        underlayerOffset = -2.0F,
    )
    val underlayerData = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = underlayerOptions,
    )
    val underlayerPdf = writePdfExport(fileName = "design-with-underlayer.pdf", buffer = underlayerData)
    // highlight-android-underlayer

    // highlight-android-target-size
    val a4Options = ExportOptions(targetWidth = 2480F, targetHeight = 3508F)
    val a4Data = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = a4Options,
    )
    val a4Pdf = writePdfExport(fileName = "design-a4.pdf", buffer = a4Data)
    // highlight-android-target-size

    return listOf(defaultPdf, progressPdf, highCompatibilityPdf, underlayerPdf, a4Pdf)
}

// highlight-android-save-pdf
private suspend fun writePdfExport(
    fileName: String,
    buffer: ByteBuffer,
): File = withContext(Dispatchers.IO) {
    val prefix = fileName.substringBeforeLast(".pdf")
    val source = buffer.asReadOnlyBuffer()
    File.createTempFile(prefix, ".pdf").apply {
        outputStream().use { output ->
            while (source.hasRemaining()) {
                output.channel.write(source)
            }
        }
        check(length() > 0L) { "PDF export was empty." }
    }
}
// highlight-android-save-pdf
