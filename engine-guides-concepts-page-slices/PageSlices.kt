import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.EngineErrorCode
import ly.img.engine.EngineException
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.io.File
import java.nio.ByteBuffer

suspend fun pageSlices(engine: Engine): PageSlicesGuideSummary = withContext(engine.dispatcher) {
    // highlight-android-page-slices-create-page
    engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
    engine.scene.setDesignUnit(DesignUnit.MILLIMETER)
    val stack = engine.block.findByType(DesignBlockType.Stack).first()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = stack, child = page)

    // A 3 mm bleed on every side of the sheet
    engine.block.setBoolean(block = page, property = "page/marginEnabled", value = true)
    listOf("top", "bottom", "left", "right").forEach { side ->
        engine.block.setFloat(block = page, property = "page/margin/$side", value = 3F)
    }
    // highlight-android-page-slices-create-page

    // highlight-android-page-slices-create-panels
    // The outside of a tri-fold: the inside flap, the back and the cover
    val panels: List<DesignBlock> = listOf("Inside Flap", "Back", "Cover").map { panelName ->
        val panel = engine.block.create(DesignBlockType.PageSlice)
        // Size the page slice before it joins the page.
        engine.block.setWidth(block = panel, value = 99F)
        engine.block.setHeight(block = panel, value = 210F)
        engine.block.setName(block = panel, name = panelName)
        engine.block.appendChild(parent = page, child = panel)
        panel
    }
    val flap = panels[0]
    val cover = panels[2]
    // highlight-android-page-slices-create-panels

    // highlight-android-page-slices-panel-fill
    val coverFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = coverFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.13F, g = 0.36F, b = 0.6F, a = 1F),
    )
    // setFill on a page slice also turns its fill on.
    engine.block.setFill(block = cover, fill = coverFill)
    // highlight-android-page-slices-panel-fill

    // highlight-android-page-slices-add-content
    // Content is a child of the page, in the coordinates of the page.
    val title = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = title)
    engine.block.replaceText(block = title, text = "Summer Workshops")
    engine.block.setTextFontSize(block = title, fontSize = 28F)
    engine.block.setTextColor(block = title, color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F))
    engine.block.setWidth(block = title, value = 79F)
    engine.block.setHeightMode(block = title, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = title, value = 208F)
    engine.block.setPositionY(block = title, value = 30F)

    val flapNote = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = flapNote, shape = engine.block.createShape(ShapeType.Rect))
    val noteFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = noteFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.95F, g = 0.75F, b = 0.2F, a = 1F),
    )
    engine.block.setFill(block = flapNote, fill = noteFill)
    engine.block.appendChild(parent = page, child = flapNote)
    engine.block.setWidth(block = flapNote, value = 79F)
    engine.block.setHeight(block = flapNote, value = 60F)
    engine.block.setPositionX(block = flapNote, value = 10F)
    engine.block.setPositionY(block = flapNote, value = 140F)
    // highlight-android-page-slices-add-content

    // highlight-android-page-slices-owning-page-slice
    // The page slice under the center of a block owns it.
    engine.block.getOwningPageSlice(title)?.let { owner ->
        println("The title is on: ${engine.block.getName(owner)}")
    }

    // The blocks of one panel: filter the children of the page.
    val coverBlocks = engine.block.getChildren(page).filter { child ->
        engine.block.getOwningPageSlice(child) == cover
    }
    println("Blocks on the cover: ${coverBlocks.size}")
    // highlight-android-page-slices-owning-page-slice

    // highlight-android-page-slices-get-page-slices
    // Reading order, hidden page slices too
    val readingOrder = engine.block.getPageSlices(page).map { engine.block.getName(it) }
    println("Panels in reading order: ${readingOrder.joinToString()}")
    // highlight-android-page-slices-get-page-slices

    // highlight-android-page-slices-move-out-and-in
    // Take the flap out as its own page, after the leaflet.
    val flapPage = engine.block.movePageSliceOutOfPage(pageSlice = flap, index = 1)
    engine.editor.addUndoStep()

    // Put it back as the first panel. The id changes on each move.
    val flapAgain = engine.block.movePageSliceIntoPage(pageOrPageSlice = flapPage, page = page, index = 0)
    engine.editor.addUndoStep()
    val noteOnFlap = engine.block.getOwningPageSlice(flapNote) == flapAgain
    println("The note is on the flap again: $noteOnFlap")
    // highlight-android-page-slices-move-out-and-in

    // highlight-android-page-slices-move-error
    // The scene has one page, so index 9 is a bug in the calling code.
    val refusedMoveCode = try {
        engine.block.movePageSliceOutOfPage(pageSlice = flapAgain, index = 9)
        null
    } catch (error: EngineException) {
        if (error.code != EngineErrorCode.BLOCK_PAGE_SLICE_MOVE_INDEX_INVALID) throw error
        error.code
    }
    // highlight-android-page-slices-move-error

    // highlight-android-page-slices-export
    // One panel, with the bleed of the page on all four sides
    val coverImage = engine.block.export(block = cover, mimeType = MimeType.PNG)
    writePageSlicesExport(fileName = "cover.png", buffer = coverImage)

    // The whole sheet, as a printer wants a folded leaflet
    val sheet = engine.block.export(
        block = page,
        mimeType = MimeType.PDF,
        options = ExportOptions(exportPdfWithCropMarks = true, exportPdfWithFoldMarks = true),
    )
    writePageSlicesExport(fileName = "tri-fold-outside.pdf", buffer = sheet)
    // highlight-android-page-slices-export

    PageSlicesGuideSummary(
        panelNames = engine.block.getPageSlices(page).map { engine.block.getName(it) },
        coverBlockCount = coverBlocks.size,
        noteOnFlapAfterMoves = noteOnFlap,
        refusedMoveCode = refusedMoveCode,
        sheetWidth = engine.block.getWidth(page),
    )
}

private suspend fun writePageSlicesExport(
    fileName: String,
    buffer: ByteBuffer,
): File = withContext(Dispatchers.IO) {
    val source = buffer.asReadOnlyBuffer()
    File.createTempFile(fileName.substringBeforeLast('.'), "." + fileName.substringAfterLast('.')).apply {
        outputStream().use { output ->
            while (source.hasRemaining()) {
                output.channel.write(source)
            }
        }
    }
}
