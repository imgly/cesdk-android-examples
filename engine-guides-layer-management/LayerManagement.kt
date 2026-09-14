import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType

fun layerManagement(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-block
    val backBlock = createLayerBlock(
        engine = engine,
        x = 120F,
        y = 120F,
        color = Color.fromRGBA(r = 0.10F, g = 0.40F, b = 0.95F, a = 1F),
    )
    val middleBlock = createLayerBlock(
        engine = engine,
        x = 190F,
        y = 190F,
        color = Color.fromRGBA(r = 0.20F, g = 0.75F, b = 0.45F, a = 1F),
    )
    val frontBlock = createLayerBlock(
        engine = engine,
        x = 260F,
        y = 260F,
        color = Color.fromRGBA(r = 0.95F, g = 0.30F, b = 0.25F, a = 1F),
    )
    // highlight-android-create-block

    // highlight-android-append-child
    engine.block.appendChild(parent = page, child = backBlock)
    engine.block.appendChild(parent = page, child = middleBlock)
    engine.block.appendChild(parent = page, child = frontBlock)
    // highlight-android-append-child

    // highlight-android-get-parent
    val parent = engine.block.getParent(middleBlock)
    // highlight-android-get-parent

    // highlight-android-get-children
    val children = engine.block.getChildren(page)
    // highlight-android-get-children

    // highlight-android-insert-child
    val insertedBlock = createLayerBlock(
        engine = engine,
        x = 330F,
        y = 330F,
        color = Color.fromRGBA(r = 0.98F, g = 0.78F, b = 0.20F, a = 1F),
    )
    engine.block.insertChild(parent = page, child = insertedBlock, index = 0)
    // highlight-android-insert-child

    // highlight-android-bring-to-front
    engine.block.bringToFront(backBlock)
    // highlight-android-bring-to-front

    // highlight-android-send-to-back
    engine.block.sendToBack(frontBlock)
    // highlight-android-send-to-back

    // highlight-android-bring-forward
    engine.block.bringForward(insertedBlock)
    // highlight-android-bring-forward

    // highlight-android-send-backward
    engine.block.sendBackward(middleBlock)
    // highlight-android-send-backward

    // highlight-android-visibility
    val isVisible = engine.block.isVisible(insertedBlock)
    engine.block.setVisible(block = insertedBlock, visible = !isVisible)
    engine.block.setVisible(block = insertedBlock, visible = true)
    // highlight-android-visibility

    // highlight-android-duplicate
    val duplicate = engine.block.duplicate(middleBlock)
    engine.block.setPositionX(duplicate, value = 430F)
    engine.block.setPositionY(duplicate, value = 140F)
    // highlight-android-duplicate

    // highlight-android-is-valid
    val duplicateIsValid = engine.block.isValid(duplicate)
    // highlight-android-is-valid

    // highlight-android-destroy
    engine.block.destroy(frontBlock)
    val frontBlockIsValid = engine.block.isValid(frontBlock)
    // highlight-android-destroy

    // Keep values live for the compiled guide sample.
    check(parent == page)
    check(children.containsAll(listOf(backBlock, middleBlock, frontBlock)))
    check(duplicateIsValid)
    check(!frontBlockIsValid)

    // highlight-android-zoom
    engine.scene.zoomToBlock(
        page,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-android-zoom

    engine.stop()
}

// highlight-android-create-helper
private fun createLayerBlock(
    engine: Engine,
    x: Float,
    y: Float,
    color: RGBAColor,
): DesignBlock {
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block, value = 180F)
    engine.block.setHeight(block, value = 180F)
    engine.block.setPositionX(block, value = x)
    engine.block.setPositionY(block, value = y)
    engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(block = block, color = color)
    return block
}
// highlight-android-create-helper
