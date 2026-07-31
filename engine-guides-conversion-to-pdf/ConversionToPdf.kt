import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.SceneLayout
import java.io.File
import java.nio.ByteBuffer

data class ConversionToPdfResult(
    val singleImagePdf: File,
    val multiPagePdf: File,
    val highCompatibilityPdf: File,
    val underlayerPdf: File,
    val configuredPdf: File,
    val pageCount: Int,
)

suspend fun conversionToPdf(
    engine: Engine,
    imageUris: List<Uri>,
    outputDirectory: File,
): ConversionToPdfResult = withContext(engine.dispatcher) {
    require(imageUris.isNotEmpty()) { "Provide at least one image URI." }

    // highlight-android-conversion-to-pdf-single-image
    engine.scene.createFromImage(imageUri = imageUris.first())

    val singleImagePage = checkNotNull(engine.scene.getCurrentPage())
    val singleImageData = engine.block.export(
        block = singleImagePage,
        mimeType = MimeType.PDF,
    )
    val singleImagePdf = saveConversionPdf(
        outputDirectory = outputDirectory,
        fileName = "single-image.pdf",
        buffer = singleImageData,
    )
    // highlight-android-conversion-to-pdf-single-image

    // highlight-android-conversion-to-pdf-multi-image
    val stackedScene = engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
    val stack = engine.block.findByType(DesignBlockType.Stack).first()

    imageUris.forEach { imageUri ->
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = stack, child = page)

        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = imageUri,
        )
        engine.block.setFill(block = page, fill = imageFill)
    }

    val multiPageData = engine.block.export(
        block = stackedScene,
        mimeType = MimeType.PDF,
    )
    val multiPagePdf = saveConversionPdf(
        outputDirectory = outputDirectory,
        fileName = "multi-page.pdf",
        buffer = multiPageData,
    )
    // highlight-android-conversion-to-pdf-multi-image

    // highlight-android-conversion-to-pdf-dpi
    engine.block.setFloat(block = stackedScene, property = "scene/dpi", value = 150F)
    // highlight-android-conversion-to-pdf-dpi

    // highlight-android-conversion-to-pdf-high-compatibility
    val compatibilityOptions = ExportOptions(exportPdfWithHighCompatibility = true)
    val highCompatibilityData = engine.block.export(
        block = stackedScene,
        mimeType = MimeType.PDF,
        options = compatibilityOptions,
    )
    val highCompatibilityPdf = saveConversionPdf(
        outputDirectory = outputDirectory,
        fileName = "high-compatibility.pdf",
        buffer = highCompatibilityData,
    )
    // highlight-android-conversion-to-pdf-high-compatibility

    // highlight-android-conversion-to-pdf-spot-color
    engine.editor.setSpotColor(
        name = "BrandUnderlay",
        color = Color.fromRGBA(r = 0.8F, g = 0.8F, b = 0.8F, a = 1F),
    )
    // highlight-android-conversion-to-pdf-spot-color

    // highlight-android-conversion-to-pdf-underlayer
    val underlayerOptions = ExportOptions(
        exportPdfWithHighCompatibility = true,
        exportPdfWithUnderlayer = true,
        underlayerSpotColorName = "BrandUnderlay",
        underlayerOffset = -2F,
    )
    val underlayerData = engine.block.export(
        block = stackedScene,
        mimeType = MimeType.PDF,
        options = underlayerOptions,
    )
    val underlayerPdf = saveConversionPdf(
        outputDirectory = outputDirectory,
        fileName = "with-underlayer.pdf",
        buffer = underlayerData,
    )
    // highlight-android-conversion-to-pdf-underlayer

    // highlight-android-conversion-to-pdf-combined
    engine.block.setFloat(block = stackedScene, property = "scene/dpi", value = 300F)
    val combinedOptions = ExportOptions(
        targetWidth = 2480F,
        targetHeight = 3508F,
        exportPdfWithHighCompatibility = true,
        exportPdfWithUnderlayer = true,
        underlayerSpotColorName = "BrandUnderlay",
        underlayerOffset = -2F,
    )
    val configuredData = engine.block.export(
        block = stackedScene,
        mimeType = MimeType.PDF,
        options = combinedOptions,
    )
    val configuredPdf = saveConversionPdf(
        outputDirectory = outputDirectory,
        fileName = "configured.pdf",
        buffer = configuredData,
    )
    // highlight-android-conversion-to-pdf-combined

    ConversionToPdfResult(
        singleImagePdf = singleImagePdf,
        multiPagePdf = multiPagePdf,
        highCompatibilityPdf = highCompatibilityPdf,
        underlayerPdf = underlayerPdf,
        configuredPdf = configuredPdf,
        pageCount = engine.scene.getPages().size,
    )
}

// highlight-android-conversion-to-pdf-save
suspend fun saveConversionPdf(
    outputDirectory: File,
    fileName: String,
    buffer: ByteBuffer,
): File = withContext(Dispatchers.IO) {
    check(outputDirectory.isDirectory || outputDirectory.mkdirs()) {
        "Could not create the PDF output directory."
    }

    val source = buffer.asReadOnlyBuffer()
    File(outputDirectory, fileName).apply {
        outputStream().channel.use { channel ->
            while (source.hasRemaining()) {
                channel.write(source)
            }
        }
        check(length() > 0L) { "PDF export was empty." }
    }
}
// highlight-android-conversion-to-pdf-save
