import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun undoAndHistory(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    val page = createDemoPage(engine)
    val historyUpdates = subscribeToHistoryUpdates(scope = this, engine = engine)

    try {
        val primaryBlock = createPrimaryBlock(engine, page)
        undoLatestChange(engine)
        redoLatestChange(engine)
        applyManualUndoStep(engine, primaryBlock)
        createSecondaryHistoryDemo(engine, page)
    } finally {
        historyUpdates.cancel()
        engine.stop()
    }
}

private fun subscribeToHistoryUpdates(
    scope: CoroutineScope,
    engine: Engine,
): Job = scope.launch {
    // highlight-android-subscribe-history
    engine.editor.onHistoryUpdated().collect {
        println(
            "History updated: canUndo=${engine.editor.canUndo()}, " +
                "canRedo=${engine.editor.canRedo()}",
        )
    }
    // highlight-android-subscribe-history
}

private fun createPrimaryBlock(
    engine: Engine,
    page: DesignBlock,
): DesignBlock {
    // highlight-android-create-block
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setPositionX(block, 140F)
    engine.block.setPositionY(block, 95F)
    engine.block.setWidth(block, 265F)
    engine.block.setHeight(block, 265F)
    val triangleShape = engine.block.createShape(ShapeType.Polygon)
    engine.block.setInt(
        triangleShape,
        property = "shape/polygon/sides",
        value = 3,
    )
    engine.block.setShape(block, triangleShape)
    val triangleFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        triangleFill,
        property = "fill/color/value",
        value = Color.fromRGBA(0.2F, 0.5F, 0.9F, 1F),
    )
    engine.block.setFill(block, triangleFill)
    engine.block.appendChild(page, block)
    engine.editor.addUndoStep()
    // highlight-android-create-block

    return block
}

private fun undoLatestChange(engine: Engine) {
    // highlight-android-undo
    if (engine.editor.canUndo()) {
        engine.editor.undo()
    }
    // highlight-android-undo
}

private fun redoLatestChange(engine: Engine) {
    // highlight-android-redo
    if (engine.editor.canRedo()) {
        engine.editor.redo()
    }
    // highlight-android-redo
}

private fun applyManualUndoStep(
    engine: Engine,
    primaryBlock: DesignBlock,
) {
    // highlight-android-manual-step
    engine.block.setPositionX(primaryBlock, 190F)
    engine.editor.addUndoStep()
    if (engine.editor.canUndo()) {
        engine.editor.removeUndoStep()
    }
    engine.block.setPositionX(primaryBlock, 140F)
    // highlight-android-manual-step
}

private fun createSecondaryHistoryDemo(
    engine: Engine,
    page: DesignBlock,
) {
    // highlight-android-multiple-histories
    val primaryHistory = engine.editor.getActiveHistory()
    val secondaryHistory = engine.editor.createHistory()
    engine.editor.setActiveHistory(secondaryHistory)
    val secondaryBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setPositionX(secondaryBlock, 440F)
    engine.block.setPositionY(secondaryBlock, 95F)
    engine.block.setWidth(secondaryBlock, 220F)
    engine.block.setHeight(secondaryBlock, 220F)
    val circleShape = engine.block.createShape(ShapeType.Ellipse)
    engine.block.setShape(secondaryBlock, circleShape)
    val circleFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        circleFill,
        property = "fill/color/value",
        value = Color.fromRGBA(0.9F, 0.3F, 0.3F, 1F),
    )
    engine.block.setFill(secondaryBlock, circleFill)
    engine.block.appendChild(page, secondaryBlock)
    engine.editor.addUndoStep()
    engine.editor.setActiveHistory(primaryHistory)
    // highlight-android-multiple-histories

    // highlight-android-destroy-history
    engine.editor.destroyHistory(secondaryHistory)
    // highlight-android-destroy-history
}

private fun createDemoPage(engine: Engine): DesignBlock {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1920F)
    return page
}
