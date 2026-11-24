import android.net.Uri
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import kotlin.math.PI

suspend fun photobookSpreads(engine: Engine): PhotobookSpreadsGuideSummary = withContext(engine.dispatcher) {
    // highlight-android-photobook-spreads-create-scene
    val scene = engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
    engine.scene.setDesignUnit(DesignUnit.MILLIMETER)
    val stack = engine.block.findByType(DesignBlockType.Stack).first()
    // highlight-android-photobook-spreads-create-scene

    // highlight-android-photobook-spreads-create-cover
    // The print shop gives the spine width for a page count and a paper.
    val spineWidth = 12F
    val cover = createSpread(
        engine = engine,
        stack = stack,
        pages = listOf(
            SpreadPage(name = "Back Cover", width = 210F),
            SpreadPage(name = "Spine", width = spineWidth),
            SpreadPage(name = "Front Cover", width = 210F),
        ),
    )
    val spine = engine.block.getPageSlices(cover)[1]
    // A title longer or wider than the spine stops at its folds.
    engine.block.setClipped(block = spine, clipped = true)

    val spineTitle = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = cover, child = spineTitle)
    engine.block.replaceText(block = spineTitle, text = "Summer 2026")
    engine.block.setTextFontSize(block = spineTitle, fontSize = 14F)
    val titleWidth = 120F
    val titleHeight = 8F
    engine.block.setWidth(block = spineTitle, value = titleWidth)
    engine.block.setHeight(block = spineTitle, value = titleHeight)
    engine.block.setEnum(block = spineTitle, property = "text/horizontalAlignment", value = "Center")
    engine.block.setEnum(block = spineTitle, property = "text/verticalAlignment", value = "Center")
    // setRotation keeps the center and moves the position. After the turn,
    // the position is the top-right corner of the turned frame.
    engine.block.setRotation(block = spineTitle, radians = (PI / 2).toFloat())
    engine.block.setPositionX(block = spineTitle, value = 210F + spineWidth / 2 + titleHeight / 2)
    engine.block.setPositionY(block = spineTitle, value = (210F - titleWidth) / 2)
    // highlight-android-photobook-spreads-create-cover

    // highlight-android-photobook-spreads-create-inside
    val firstSpread = createSpread(
        engine = engine,
        stack = stack,
        pages = listOf(
            SpreadPage(name = "Lake Left", width = 210F),
            SpreadPage(name = "Lake Right", width = 210F),
        ),
    )
    // The second spread gets its right page from a copy, the third from a
    // duplicate.
    val secondSpread = createSpread(
        engine = engine,
        stack = stack,
        pages = listOf(SpreadPage(name = "Trip Left", width = 210F)),
    )
    val thirdSpread = createSpread(
        engine = engine,
        stack = stack,
        pages = listOf(SpreadPage(name = "Notes Left", width = 210F)),
    )
    val (lakeLeft, lakeRight) = engine.block.getPageSlices(firstSpread)
    // highlight-android-photobook-spreads-create-inside

    // highlight-android-photobook-spreads-photo-across-fold
    // One photo over both pages, bleed included, centered on the fold
    val photo = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = photo, shape = engine.block.createShape(ShapeType.Rect))
    val photoFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = photoFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = photo, fill = photoFill)
    engine.block.appendChild(parent = firstSpread, child = photo)
    engine.block.setPositionX(block = photo, value = -3F)
    engine.block.setPositionY(block = photo, value = -3F)
    engine.block.setWidth(block = photo, value = 426F)
    engine.block.setHeight(block = photo, value = 216F)

    // On the fold, the earlier page slice owns the photo.
    val photoOnLeftPage = engine.block.getOwningPageSlice(photo) == lakeLeft
    println("The photo belongs to the left page: $photoOnLeftPage")
    // highlight-android-photobook-spreads-photo-across-fold

    // highlight-android-photobook-spreads-copy-page
    // A page slice fill and the blocks that a page slice owns go with a copy.
    val pageFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = pageFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.1F, g = 0.2F, b = 0.3F, a = 1F),
    )
    engine.block.setFill(block = lakeRight, fill = pageFill)
    val caption = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = firstSpread, child = caption)
    engine.block.replaceText(block = caption, text = "At the lake")
    engine.block.setTextFontSize(block = caption, fontSize = 12F)
    engine.block.setTextColor(block = caption, color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F))
    engine.block.setWidth(block = caption, value = 190F)
    engine.block.setHeight(block = caption, value = 10F)
    engine.block.setPositionX(block = caption, value = 220F)
    engine.block.setPositionY(block = caption, value = 180F)

    val saved = engine.block.saveToString(blocks = listOf(lakeRight))
    val copy = engine.block.loadFromString(saved).first()
    engine.block.setName(block = copy, name = "Trip Right")
    // The copy becomes the right page. The next update puts the caption on it.
    engine.block.insertChild(parent = secondSpread, child = copy, index = 1)
    // highlight-android-photobook-spreads-copy-page

    // highlight-android-photobook-spreads-duplicate-page
    val note = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = thirdSpread, child = note)
    engine.block.replaceText(block = note, text = "Notes")
    engine.block.setTextFontSize(block = note, fontSize = 18F)
    engine.block.setWidth(block = note, value = 190F)
    engine.block.setHeight(block = note, value = 12F)
    engine.block.setPositionX(block = note, value = 10F)
    engine.block.setPositionY(block = note, value = 20F)

    // The duplicate goes right after the left page, with a copy of the note.
    val leftPage = engine.block.getPageSlices(thirdSpread).first()
    val rightPage = engine.block.duplicate(leftPage)
    engine.block.setName(block = rightPage, name = "Notes Right")
    println("Pages in the third spread: ${engine.block.getPageSlices(thirdSpread).size}")
    // highlight-android-photobook-spreads-duplicate-page

    // highlight-android-photobook-spreads-export
    // The cover as one sheet, with crop marks and the two spine folds
    val coverPdf = engine.block.export(
        block = cover,
        mimeType = MimeType.PDF,
        options = ExportOptions(
            exportPdfWithCropMarks = true,
            exportPdfWithFoldMarks = true,
            printMarkOffset = 3F,
            pdfImageQuality = 0.9F,
        ),
    )
    // The inside pages: one PDF page per page slice, without the cover
    // A hidden page is left out of a scene export.
    engine.block.setVisible(block = cover, visible = false)
    val insidePdf = try {
        engine.block.export(
            block = scene,
            mimeType = MimeType.PDF,
            options = ExportOptions(exportPdfPageSlices = true, pdfImageQuality = 0.9F),
        )
    } finally {
        engine.block.setVisible(block = cover, visible = true)
    }
    // Save or share the PDFs in your app.
    println("Cover PDF bytes: ${coverPdf.remaining()}")
    println("Inside PDF bytes: ${insidePdf.remaining()}")
    // highlight-android-photobook-spreads-export

    // highlight-android-photobook-spreads-single-page-mode
    engine.editor.setSettingBoolean("features/singlePageModeEnabled", true)
    // Two page slices of one spread form a facing pair.
    engine.scene.setCurrentPages(listOf(lakeLeft, lakeRight))
    println("Shown pages: ${engine.scene.getCurrentPages().size}")
    // The camera does not move by itself.
    engine.scene.zoomToBlock(
        block = firstSpread,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-android-photobook-spreads-single-page-mode

    val spreads = listOf(cover, firstSpread, secondSpread, thirdSpread)
    PhotobookSpreadsGuideSummary(
        pageNamesBySpread = spreads.map { spread ->
            engine.block.getPageSlices(spread).map { engine.block.getName(it) }
        },
        photoOnLeftPage = photoOnLeftPage,
        copiedCaptionCount = engine.block.getChildren(secondSpread).count { child ->
            engine.block.getOwningPageSlice(child) == copy
        },
        coverVisibleAfterExport = engine.block.isVisible(cover),
        shownPageNames = engine.scene.getCurrentPages().map { engine.block.getName(it) },
        coverPdf = coverPdf,
        insidePdf = insidePdf,
    )
}

// highlight-android-photobook-spreads-create-spread
private data class SpreadPage(
    val name: String,
    val width: Float,
)

private fun createSpread(
    engine: Engine,
    stack: DesignBlock,
    pages: List<SpreadPage>,
): DesignBlock {
    val spread = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = stack, child = spread)

    // A 3 mm bleed on every side of the spread
    engine.block.setBoolean(block = spread, property = "page/marginEnabled", value = true)
    listOf("top", "bottom", "left", "right").forEach { side ->
        engine.block.setFloat(block = spread, property = "page/margin/$side", value = 3F)
    }

    pages.forEach { page ->
        val pageSlice = engine.block.create(DesignBlockType.PageSlice)
        // Size the page slice before it joins the spread.
        engine.block.setWidth(block = pageSlice, value = page.width)
        engine.block.setHeight(block = pageSlice, value = 210F)
        engine.block.setName(block = pageSlice, name = page.name)
        engine.block.appendChild(parent = spread, child = pageSlice)
    }
    return spread
}
// highlight-android-photobook-spreads-create-spread
