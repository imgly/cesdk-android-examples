import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

data class PartialExport(
    val individualGraphic: ByteBuffer,
    val groupedElements: ByteBuffer,
    val selection: ByteBuffer,
    val currentPage: ByteBuffer,
    val allPages: List<ByteBuffer>,
    val resizedPage: ByteBuffer,
    val jpegPage: ByteBuffer,
    val pdfPage: ByteBuffer,
    val maxExportSize: Int,
    val availableMemory: Long,
) {
    val pngExports: List<ByteBuffer>
        get() = listOf(individualGraphic, groupedElements, selection, currentPage, resizedPage) + allPages
}

suspend fun partialExport(engine: Engine): PartialExport {
    // Demo scaffolding: the guide snippets operate on an existing scene. This
    // source file builds a deterministic two-page scene so the smoke test can
    // verify every export path with real renderable blocks.
    val scene = engine.scene.create()

    val page1 = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page1, value = 800F)
    engine.block.setHeight(page1, value = 600F)
    engine.block.appendChild(parent = scene, child = page1)

    val rectangle = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(rectangle, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(rectangle, value = 220F)
    engine.block.setHeight(rectangle, value = 220F)
    engine.block.setPositionX(rectangle, value = 80F)
    engine.block.setPositionY(rectangle, value = 100F)
    engine.block.setName(rectangle, "background-rect")
    engine.block.setFill(rectangle, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = rectangle,
        color = Color.fromHex("#FF3366CC"),
    )
    engine.block.appendChild(parent = page1, child = rectangle)

    val ellipse = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(ellipse, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(ellipse, value = 220F)
    engine.block.setHeight(ellipse, value = 220F)
    engine.block.setPositionX(ellipse, value = 340F)
    engine.block.setPositionY(ellipse, value = 100F)
    engine.block.setFill(ellipse, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = ellipse,
        color = Color.fromHex("#FFF4C542"),
    )
    engine.block.appendChild(parent = page1, child = ellipse)

    val star = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(star, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setWidth(star, value = 220F)
    engine.block.setHeight(star, value = 220F)
    engine.block.setPositionX(star, value = 210F)
    engine.block.setPositionY(star, value = 350F)
    engine.block.setFill(star, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = star,
        color = Color.fromHex("#FFE54B4B"),
    )
    engine.block.appendChild(parent = page1, child = star)

    // highlight-android-find-blocks
    val graphicBlocks = engine.block.findByType(DesignBlockType.Graphic)
    val namedBlocks = engine.block.findByName("background-rect")
    // highlight-android-find-blocks
    check(graphicBlocks.contains(rectangle))
    check(namedBlocks.single() == rectangle)
    engine.block.forceLoadResources(listOf(page1, rectangle, ellipse, star))

    // highlight-android-export-individual-block
    val firstGraphic = engine.block.findByType(DesignBlockType.Graphic).first()
    val pngOptions = ExportOptions(pngCompressionLevel = 5)
    val blockData = engine.block.export(
        block = firstGraphic,
        mimeType = MimeType.PNG,
        options = pngOptions,
    )
    // highlight-android-export-individual-block
    check(blockData.hasRemaining()) { "individual graphic export is empty" }

    // highlight-android-create-and-export-group
    val groupBlocks = engine.block.findByType(DesignBlockType.Graphic).take(2)
    val group = engine.block.group(groupBlocks)
    val groupData = engine.block.export(
        block = group,
        mimeType = MimeType.PNG,
    )
    // highlight-android-create-and-export-group
    check(groupData.hasRemaining()) { "group export is empty" }

    // In a real app the user selects blocks in the editor UI and the export
    // action reads it. The smoke test sets one block selected here so the
    // highlighted findAllSelected() snippet is deterministic offscreen.
    engine.block.setSelected(star, selected = true)
    // highlight-android-export-selected
    val selectedBlocks = engine.block.findAllSelected()
    val selectionData = when {
        selectedBlocks.size == 1 -> engine.block.export(
            block = selectedBlocks.single(),
            mimeType = MimeType.PNG,
        )
        selectedBlocks.size > 1 && engine.block.isGroupable(selectedBlocks) -> {
            val selectionGroup = engine.block.group(selectedBlocks)
            try {
                engine.block.export(
                    block = selectionGroup,
                    mimeType = MimeType.PNG,
                )
            } finally {
                engine.block.ungroup(selectionGroup)
                selectedBlocks.forEach { block ->
                    engine.block.setSelected(block, selected = true)
                }
            }
        }
        else -> null
    }
    // highlight-android-export-selected
    checkNotNull(selectionData) { "no exportable selection" }
    check(selectionData.hasRemaining()) { "selection export is empty" }

    // highlight-android-export-current-page
    val currentPage = engine.scene.getCurrentPage()
    val currentPageData = currentPage?.let { page ->
        engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
        )
    }
    // highlight-android-export-current-page
    checkNotNull(currentPageData) { "scene has no current page" }
    check(currentPageData.hasRemaining()) { "current page export is empty" }

    // highlight-android-target-size
    val page = engine.scene.getCurrentPage() ?: error("Scene has no current page")
    val resizedOptions = ExportOptions(
        targetWidth = 1200F,
        targetHeight = 900F,
    )
    val resizedData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = resizedOptions,
    )
    // highlight-android-target-size
    check(resizedData.hasRemaining()) { "resized page export is empty" }

    // highlight-android-quality-options
    val jpegPage = engine.scene.getCurrentPage() ?: error("Scene has no current page")
    val jpegOptions = ExportOptions(jpegQuality = 0.8F)
    val jpegData = engine.block.export(
        block = jpegPage,
        mimeType = MimeType.JPEG,
        options = jpegOptions,
    )
    // highlight-android-quality-options
    check(jpegData.hasRemaining()) { "JPEG export is empty" }

    // highlight-android-check-limits
    val maxExportSize = engine.editor.getMaxExportSize()
    val availableMemory = engine.editor.getAvailableMemory()
    // highlight-android-check-limits

    // highlight-android-export-pdf
    val pdfPage = engine.scene.getCurrentPage() ?: error("Scene has no current page")
    val pdfOptions = ExportOptions(exportPdfWithHighCompatibility = true)
    val pdfData = engine.block.export(
        block = pdfPage,
        mimeType = MimeType.PDF,
        options = pdfOptions,
    )
    // highlight-android-export-pdf
    check(pdfData.hasRemaining()) { "PDF export is empty" }

    val page2 = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page2, value = 800F)
    engine.block.setHeight(page2, value = 600F)
    engine.block.appendChild(parent = scene, child = page2)

    val page2Graphic = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(page2Graphic, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(page2Graphic, value = 400F)
    engine.block.setHeight(page2Graphic, value = 300F)
    engine.block.setPositionX(page2Graphic, value = 200F)
    engine.block.setPositionY(page2Graphic, value = 150F)
    engine.block.setFill(page2Graphic, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = page2Graphic,
        color = Color.fromHex("#FF35A66B"),
    )
    engine.block.appendChild(parent = page2, child = page2Graphic)

    // highlight-android-export-all-pages
    val pages = engine.scene.getPages()
    val pageData = engine.block.export(
        blocks = pages,
        mimeType = MimeType.PNG,
    )
    // highlight-android-export-all-pages
    check(pageData.size == pages.size)
    pageData.forEachIndexed { index, data ->
        check(data.hasRemaining()) { "page ${index + 1} export is empty" }
    }

    return PartialExport(
        individualGraphic = blockData.copyForVerification(),
        groupedElements = groupData.copyForVerification(),
        selection = selectionData.copyForVerification(),
        currentPage = currentPageData.copyForVerification(),
        allPages = pageData.map(ByteBuffer::copyForVerification),
        resizedPage = resizedData.copyForVerification(),
        jpegPage = jpegData.copyForVerification(),
        pdfPage = pdfData.copyForVerification(),
        maxExportSize = maxExportSize,
        availableMemory = availableMemory,
    )
}

private fun ByteBuffer.copyForVerification(): ByteBuffer {
    val duplicate = asReadOnlyBuffer()
    val bytes = ByteArray(duplicate.remaining())
    duplicate.get(bytes)
    return ByteBuffer.wrap(bytes).asReadOnlyBuffer()
}
