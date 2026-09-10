import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer

data class PngExport(
    val label: String,
    val pngData: ByteBuffer,
)

data class ConversionToPngResult(
    val singlePage: PngExport,
    val allPages: List<PngExport>,
    val compressed: PngExport,
    val targetDimensions: PngExport,
    val textOverhang: PngExport,
) {
    val allExports: List<PngExport>
        get() = listOf(singlePage) +
            allPages +
            listOf(compressed, targetDimensions, textOverhang)
}

fun conversionToPng(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): Deferred<ConversionToPngResult> = CoroutineScope(Dispatchers.Main).async {
    val engine = Engine.getInstance(id = "ly.img.engine.example")

    try {
        engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1080, height = 1920)

        createSceneWithPages(engine)
        val currentPage = engine.scene.getCurrentPage() ?: engine.scene.getPages().first()

        ConversionToPngResult(
            singlePage = PngExport("single page", exportSinglePage(engine, currentPage).copyForVerification()),
            allPages = exportAllPages(engine).mapIndexed { index, pngData ->
                PngExport("page ${index + 1}", pngData.copyForVerification())
            },
            compressed = PngExport("compressed", exportWithCompression(engine, currentPage).copyForVerification()),
            targetDimensions = PngExport(
                "target dimensions",
                exportWithTargetDimensions(engine, currentPage).copyForVerification(),
            ),
            textOverhang = PngExport(
                "text overhang",
                exportWithTextOverhang(engine, currentPage).copyForVerification(),
            ),
        )
    } finally {
        engine.stop()
    }
}

private fun ByteBuffer.copyForVerification(): ByteBuffer {
    val duplicate = asReadOnlyBuffer()
    val bytes = ByteArray(duplicate.remaining())
    duplicate.get(bytes)
    return ByteBuffer.wrap(bytes).asReadOnlyBuffer()
}

// highlight-android-create-scene
fun createSceneWithPages(engine: Engine): DesignBlock {
    val scene = engine.scene.create()

    repeat(2) { pageIndex ->
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        addVisiblePageContent(engine, page, pageIndex)
    }

    return scene
}
// highlight-android-create-scene

private fun addVisiblePageContent(
    engine: Engine,
    page: DesignBlock,
    pageIndex: Int,
) {
    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, "PNG export background")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 800F)
    engine.block.setHeight(background, value = 600F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = background,
        color = Color.fromHex("#FFFFFBF1"),
    )
    engine.block.appendChild(parent = page, child = background)

    val accent = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(accent, "PNG export accent")
    engine.block.setShape(accent, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(accent, value = 520F)
    engine.block.setHeight(accent, value = 280F)
    engine.block.setPositionX(accent, value = 140F)
    engine.block.setPositionY(accent, value = 150F)
    engine.block.setFill(accent, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = accent,
        color = if (pageIndex == 0) Color.fromHex("#FF2457D6") else Color.fromHex("#FFE15D2A"),
    )
    engine.block.appendChild(parent = page, child = accent)

    val label = engine.block.create(DesignBlockType.Text)
    engine.block.setName(label, "PNG export text")
    engine.block.setPositionX(label, value = 190F)
    engine.block.setPositionY(label, value = 245F)
    engine.block.setWidthMode(label, mode = SizeMode.AUTO)
    engine.block.setHeightMode(label, mode = SizeMode.AUTO)
    engine.block.replaceText(label, text = "PNG ${pageIndex + 1}")
    engine.block.setTextFontSize(label, fontSize = 88F)
    engine.block.setTextColor(label, color = Color.fromHex("#FFFFFFFF"))
    engine.block.appendChild(parent = page, child = label)

    // This fixed frame gives the allowTextOverhang export option real text glyphs to preserve.
    val overhangText = engine.block.create(DesignBlockType.Text)
    engine.block.setName(overhangText, "Text overhang sample")
    engine.block.setPositionX(overhangText, value = 84F)
    engine.block.setPositionY(overhangText, value = 438F)
    engine.block.setWidth(overhangText, value = 260F)
    engine.block.setHeight(overhangText, value = 56F)
    engine.block.replaceText(overhangText, text = "Jolly glyphs")
    engine.block.setTextFontSize(overhangText, fontSize = 72F)
    engine.block.setTextColor(overhangText, color = Color.fromHex("#FF0B1220"))
    engine.block.appendChild(parent = page, child = overhangText)
}

// highlight-android-export-single-page
suspend fun exportSinglePage(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
    )

    check(pngData.hasRemaining()) { "single page PNG export is empty" }
    return pngData
}
// highlight-android-export-single-page

// highlight-android-export-all-pages
suspend fun exportAllPages(engine: Engine): List<ByteBuffer> {
    val pages = engine.scene.getPages()
    val pngFiles = engine.block.export(
        blocks = pages,
        mimeType = MimeType.PNG,
    )

    check(pngFiles.size == pages.size)
    pngFiles.forEachIndexed { index, pngData ->
        check(pngData.hasRemaining()) { "page ${index + 1} PNG export is empty" }
    }
    return pngFiles
}
// highlight-android-export-all-pages

// highlight-android-compression-level
suspend fun exportWithCompression(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(pngCompressionLevel = 9)
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = options,
    )

    check(pngData.hasRemaining()) { "compressed PNG export is empty" }
    return pngData
}
// highlight-android-compression-level

// highlight-android-target-dimensions
suspend fun exportWithTargetDimensions(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(
        targetWidth = 1200F,
        targetHeight = 900F,
    )
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = options,
    )

    check(pngData.hasRemaining()) { "target dimensions PNG export is empty" }
    return pngData
}
// highlight-android-target-dimensions

// highlight-android-text-overhang
suspend fun exportWithTextOverhang(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer {
    val options = ExportOptions(allowTextOverhang = true)
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = options,
    )

    check(pngData.hasRemaining()) { "text overhang PNG export is empty" }
    return pngData
}
// highlight-android-text-overhang
