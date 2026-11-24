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
import ly.img.engine.MovementConstraintRule
import ly.img.engine.MovementConstraintScope
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

suspend fun foldedCards(engine: Engine): FoldedCardsGuideSummary = withContext(engine.dispatcher) {
    // highlight-android-folded-cards-create-scene
    engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
    engine.scene.setDesignUnit(DesignUnit.MILLIMETER)
    val stack = engine.block.findByType(DesignBlockType.Stack).first()
    // highlight-android-folded-cards-create-scene

    // highlight-android-folded-cards-create-bi-fold
    // An A5 card folds to A6: two panels of 105 × 148 mm on each side.
    val outside = createSheet(
        engine = engine,
        stack = stack,
        panels = listOf(
            CardPanel(name = "Back", width = 105F, height = 148F),
            CardPanel(name = "Front", width = 105F, height = 148F),
        ),
    )
    val inside = createSheet(
        engine = engine,
        stack = stack,
        panels = listOf(
            CardPanel(name = "Inside Left", width = 105F, height = 148F),
            CardPanel(name = "Inside Right", width = 105F, height = 148F),
        ),
    )
    val (back, front) = engine.block.getPageSlices(outside)
    // highlight-android-folded-cards-create-bi-fold

    // highlight-android-folded-cards-create-tri-fold
    // The inside flap is 3 mm narrower, so it fits inside without buckling.
    // The inside sheet holds the same widths in the opposite order.
    val leafletOutside = createSheet(
        engine = engine,
        stack = stack,
        panels = listOf(
            CardPanel(name = "Inside Flap", width = 97F, height = 210F),
            CardPanel(name = "Back", width = 100F, height = 210F),
            CardPanel(name = "Cover", width = 100F, height = 210F),
        ),
    )
    val leafletInside = createSheet(
        engine = engine,
        stack = stack,
        panels = listOf(
            CardPanel(name = "Inside Left", width = 100F, height = 210F),
            CardPanel(name = "Inside Middle", width = 100F, height = 210F),
            CardPanel(name = "Inside Right", width = 97F, height = 210F),
        ),
    )
    // highlight-android-folded-cards-create-tri-fold

    // highlight-android-folded-cards-add-content
    val frontFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = frontFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.55F, g = 0.12F, b = 0.2F, a = 1F),
    )
    engine.block.setFill(block = front, fill = frontFill)

    // Content is a child of the page, in the coordinates of the sheet.
    val title = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = outside, child = title)
    engine.block.replaceText(block = title, text = "Happy Birthday")
    engine.block.setTextFontSize(block = title, fontSize = 24F)
    engine.block.setTextColor(block = title, color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F))
    engine.block.setWidth(block = title, value = 85F)
    engine.block.setHeightMode(block = title, mode = SizeMode.AUTO)
    engine.block.setPositionX(block = title, value = 115F)
    engine.block.setPositionY(block = title, value = 60F)

    // One photo across the inside, bleed included, centered on the fold
    val photo = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = photo, shape = engine.block.createShape(ShapeType.Rect))
    val photoFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = photoFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = photo, fill = photoFill)
    engine.block.appendChild(parent = inside, child = photo)
    engine.block.setPositionX(block = photo, value = -3F)
    engine.block.setPositionY(block = photo, value = -3F)
    engine.block.setWidth(block = photo, value = 216F)
    engine.block.setHeight(block = photo, value = 154F)

    // A band on the leaflet cover that reaches 20 mm onto the back and 3 mm
    // into the bleed
    val band = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = band, shape = engine.block.createShape(ShapeType.Rect))
    val bandFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = bandFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.13F, g = 0.36F, b = 0.6F, a = 1F),
    )
    engine.block.setFill(block = band, fill = bandFill)
    engine.block.appendChild(parent = leafletOutside, child = band)
    engine.block.setPositionX(block = band, value = 177F)
    engine.block.setPositionY(block = band, value = 20F)
    engine.block.setWidth(block = band, value = 123F)
    engine.block.setHeight(block = band, value = 40F)
    // highlight-android-folded-cards-add-content

    // highlight-android-folded-cards-clip-panels
    // A clipped panel cuts the blocks that it owns at its folds, so the band
    // stops at the fold between the back and the cover of the leaflet.
    listOf(outside, leafletOutside).forEach { sheet ->
        engine.block.getPageSlices(sheet).forEach { panel ->
            engine.block.setClipped(block = panel, clipped = true)
        }
    }
    // The inside panels stay open, so the photo crosses the fold.
    val photoOwner = engine.block.getOwningPageSlice(photo)
    photoOwner?.let { owner ->
        println("The photo belongs to: ${engine.block.getName(owner)}")
    }
    // highlight-android-folded-cards-clip-panels

    // highlight-android-folded-cards-movement-constraint
    // Users cannot drag the blocks of an outside panel past its edges.
    engine.editor.setMovementConstraint(
        rules = listOf(
            MovementConstraintRule(overshoot = 0F, scope = MovementConstraintScope.Block(back)),
            MovementConstraintRule(overshoot = 0F, scope = MovementConstraintScope.Block(front)),
        ),
    )
    val titleConstraint = engine.editor.getMovementConstraint(title)
    println("Constraint on the title: $titleConstraint")
    // highlight-android-folded-cards-movement-constraint

    // highlight-android-folded-cards-resize-format
    // Pass the size of the whole sheet: half US Letter, landscape.
    engine.block.resizeContentAware(blocks = listOf(outside, inside), width = 215.9F, height = 139.7F)
    val titleOnFront = engine.block.getOwningPageSlice(title) == front
    println("The title stays on the front: $titleOnFront")
    // highlight-android-folded-cards-resize-format

    // highlight-android-folded-cards-export
    // One flat sheet per printed side, with crop and fold marks
    val printMarks = ExportOptions(
        exportPdfWithCropMarks = true,
        exportPdfWithFoldMarks = true,
        // The marks start at the edge of the 3 mm bleed.
        printMarkOffset = 3F,
    )
    val sheetPdfs = listOf(outside, inside, leafletOutside, leafletInside).associateWith { sheet ->
        engine.block.export(block = sheet, mimeType = MimeType.PDF, options = printMarks)
    }
    // Save or share each PDF in your app.
    sheetPdfs.forEach { (sheet, pdf) ->
        println("${engine.block.getName(sheet)} PDF bytes: ${pdf.remaining()}")
    }
    // highlight-android-folded-cards-export

    FoldedCardsGuideSummary(
        sheetNames = sheetPdfs.keys.map { engine.block.getName(it) },
        photoOwnerName = photoOwner?.let { engine.block.getName(it) },
        clippedPanelCount = engine.scene.getPages()
            .flatMap { engine.block.getPageSlices(it) }
            .count { engine.block.isClipped(it) },
        titleOvershoot = titleConstraint?.overshoot,
        titleOnFront = titleOnFront,
        cardSheetWidth = engine.block.getWidth(outside),
        sheetPdfs = sheetPdfs.values.toList(),
    )
}

// highlight-android-folded-cards-create-sheet
private data class CardPanel(
    val name: String,
    val width: Float,
    val height: Float,
)

private fun createSheet(
    engine: Engine,
    stack: DesignBlock,
    panels: List<CardPanel>,
): DesignBlock {
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setName(block = page, name = panels.joinToString(separator = " | ") { it.name })
    engine.block.appendChild(parent = stack, child = page)

    // A 3 mm bleed on every side of the sheet
    engine.block.setBoolean(block = page, property = "page/marginEnabled", value = true)
    listOf("top", "bottom", "left", "right").forEach { side ->
        engine.block.setFloat(block = page, property = "page/margin/$side", value = 3F)
    }

    panels.forEach { panel ->
        val pageSlice = engine.block.create(DesignBlockType.PageSlice)
        // Size the page slice before it joins the page.
        engine.block.setWidth(block = pageSlice, value = panel.width)
        engine.block.setHeight(block = pageSlice, value = panel.height)
        engine.block.setName(block = pageSlice, name = panel.name)
        engine.block.appendChild(parent = page, child = pageSlice)
    }
    return page
}
// highlight-android-folded-cards-create-sheet
