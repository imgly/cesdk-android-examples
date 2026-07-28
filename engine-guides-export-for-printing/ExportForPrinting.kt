import kotlinx.coroutines.delay
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

suspend fun exportForPrinting(
    engine: Engine,
    synchronizeHeadlessSmokeExport: Boolean = false,
): Map<String, ByteBuffer> {
    // Demo scaffolding: build a small scene with one renderable graphic so the
    // PDF exports below produce a non-empty page. In your app you would start
    // from a scene that the editor has already loaded.
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 1131.6F)
    engine.block.appendChild(parent = scene, child = page)

    val star = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(star, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setPositionX(star, value = 250F)
    engine.block.setPositionY(star, value = 415.8F)
    engine.block.setWidth(star, value = 300F)
    engine.block.setHeight(star, value = 300F)
    engine.block.setFill(star, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = star,
        color = Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F),
    )
    engine.block.appendChild(parent = page, child = star)

    // highlight-android-dpi
    // 300 DPI is standard for high-quality print output.
    engine.block.setFloat(scene, property = "scene/dpi", value = 300F)
    // highlight-android-dpi

    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()
    val highCompatibilityPdf = exportHighCompatibilityPdf(
        engine = engine,
        page = page,
    )
    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()

    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()
    val standardPdf = exportStandardPdf(
        engine = engine,
        page = page,
    )
    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()

    // highlight-android-define-spot-color
    // Define the spot color that represents the underlayer ink before export.
    // The RGB values are a preview; print software uses the spot color name.
    val underlayerSpotColorName = "UnderlayerWhite"
    engine.editor.setSpotColor(
        name = underlayerSpotColorName,
        color = Color.fromRGBA(r = 0.8F, g = 0.8F, b = 0.8F, a = 1F),
    )
    // highlight-android-define-spot-color
    // Keep the settings stream synchronized before the headless PDF exports.
    engine.editor.getSpotColorRGB(underlayerSpotColorName)

    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()
    val underlayerPdf = exportUnderlayerPdf(
        engine = engine,
        page = page,
        underlayerSpotColorName = underlayerSpotColorName,
    )
    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()

    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()
    val sizedPdf = exportTargetSizePdf(
        engine = engine,
        page = page,
    )
    if (synchronizeHeadlessSmokeExport) synchronizeHeadlessExport()

    return mapOf(
        "highCompatibility" to highCompatibilityPdf,
        "standard" to standardPdf,
        "underlayer" to underlayerPdf,
        "targetSize" to sizedPdf,
    )
}

private suspend fun exportHighCompatibilityPdf(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    // highlight-android-high-compatibility
    // High compatibility mode rasterizes complex elements like gradients with
    // transparency at the scene's DPI so they render consistently across PDF
    // viewers and print RIPs.
    val highCompatibilityOptions = ExportOptions(exportPdfWithHighCompatibility = true)
    val highCompatibilityPdf = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = highCompatibilityOptions,
    ).also { highCompatibilityPdf ->
        check(highCompatibilityPdf.hasRemaining()) { "High compatibility PDF export is empty" }
    }
    // highlight-android-high-compatibility

    check(highCompatibilityPdf.hasRemaining()) { "High compatibility PDF export is empty" }
    return highCompatibilityPdf
}

private suspend fun exportStandardPdf(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    // highlight-android-standard-pdf
    // Disabling high compatibility keeps complex elements as vectors. The export
    // is faster and the PDF is smaller, but rendering may differ across viewers.
    val standardOptions = ExportOptions(exportPdfWithHighCompatibility = false)
    val standardPdf = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = standardOptions,
    ).also { standardPdf ->
        check(standardPdf.hasRemaining()) { "Standard PDF export is empty" }
    }
    // highlight-android-standard-pdf

    check(standardPdf.hasRemaining()) { "Standard PDF export is empty" }
    return standardPdf
}

private suspend fun exportUnderlayerPdf(
    engine: Engine,
    page: DesignBlock,
    underlayerSpotColorName: String,
): ByteBuffer {
    // highlight-android-export-with-underlayer
    // Generate an underlayer from the design contours filled with the spot color.
    // A negative `underlayerOffset` shrinks the underlayer inward so misaligned
    // print layers do not show visible white edges around design elements.
    val underlayerOptions = ExportOptions(
        exportPdfWithHighCompatibility = true,
        exportPdfWithUnderlayer = true,
        underlayerSpotColorName = underlayerSpotColorName,
        underlayerOffset = -2F,
    )
    val underlayerPdf = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = underlayerOptions,
    ).also { underlayerPdf ->
        check(underlayerPdf.hasRemaining()) { "Underlayer PDF export is empty" }
    }
    // highlight-android-export-with-underlayer

    check(underlayerPdf.hasRemaining()) { "Underlayer PDF export is empty" }
    return underlayerPdf
}

private suspend fun exportTargetSizePdf(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    // highlight-android-target-size
    // `targetWidth` / `targetHeight` are pixel dimensions. Combined with the
    // scene DPI set above, they determine the physical print size: 2480 x 3508
    // pixels at 300 DPI is A4 (210 x 297 mm).
    val sizedOptions = ExportOptions(
        targetWidth = 2480F,
        targetHeight = 3508F,
        exportPdfWithHighCompatibility = true,
    )
    val sizedPdf = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = sizedOptions,
    ).also { sizedPdf ->
        check(sizedPdf.hasRemaining()) { "Target-size PDF export is empty" }
    }
    // highlight-android-target-size

    check(sizedPdf.hasRemaining()) { "Target-size PDF export is empty" }
    return sizedPdf
}

private suspend fun synchronizeHeadlessExport() {
    // The isolated offscreen smoke test tears down the engine immediately after
    // export, so give asynchronous export/settings callbacks time to drain.
    delay(100)
}
