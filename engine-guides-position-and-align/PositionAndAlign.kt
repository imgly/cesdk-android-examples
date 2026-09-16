import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.HorizontalBlockAlignment
import ly.img.engine.PositionMode
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.VerticalBlockAlignment

fun positionAndAlign(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-android-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-setup

    // highlight-android-absolute-position
    val primaryBlock = createShapeBlock(
        engine = engine,
        parent = page,
        color = Color.fromRGBA(r = 0.18F, g = 0.38F, b = 0.85F),
        x = 0F,
        y = 0F,
        width = 150F,
        height = 100F,
    )

    engine.block.setPositionX(primaryBlock, value = 50F)
    engine.block.setPositionY(primaryBlock, value = 50F)

    val currentX = engine.block.getPositionX(primaryBlock)
    val currentY = engine.block.getPositionY(primaryBlock)
    // highlight-android-absolute-position

    // highlight-android-percent-position
    val percentBlock = createShapeBlock(
        engine = engine,
        parent = page,
        color = Color.fromRGBA(r = 0.04F, g = 0.64F, b = 0.43F),
        x = 0F,
        y = 0F,
        width = 150F,
        height = 100F,
    )

    engine.block.setPositionXMode(percentBlock, mode = PositionMode.PERCENT)
    engine.block.setPositionYMode(percentBlock, mode = PositionMode.PERCENT)
    engine.block.setPositionX(percentBlock, value = 0.5F)
    engine.block.setPositionY(percentBlock, value = 0.5F)

    val currentXMode = engine.block.getPositionXMode(percentBlock)
    val currentYMode = engine.block.getPositionYMode(percentBlock)
    // highlight-android-percent-position

    // highlight-android-check-alignable
    val alignmentBlocks = listOf(
        100F to 100F,
        250F to 150F,
        180F to 250F,
        350F to 200F,
    ).map { (positionX, positionY) ->
        createShapeBlock(
            engine = engine,
            parent = page,
            color = Color.fromRGBA(r = 0.18F, g = 0.38F, b = 0.85F),
            x = positionX,
            y = positionY,
            width = 100F,
            height = 80F,
        )
    }

    if (engine.block.isAlignable(alignmentBlocks)) {
        // Blocks can be aligned inside their combined bounding box.
    }
    // highlight-android-check-alignable

    // highlight-android-align-horizontal
    if (engine.block.isAlignable(alignmentBlocks)) {
        engine.block.alignHorizontally(alignmentBlocks, alignment = HorizontalBlockAlignment.LEFT)
        engine.block.alignVertically(alignmentBlocks, alignment = VerticalBlockAlignment.TOP)
    }
    // highlight-android-align-horizontal

    // highlight-android-align-single-block
    val headline = createShapeBlock(
        engine = engine,
        parent = page,
        color = Color.fromRGBA(r = 0.84F, g = 0.13F, b = 0.47F),
        x = 500F,
        y = 300F,
    )

    if (engine.block.isAlignable(listOf(headline))) {
        engine.block.alignHorizontally(listOf(headline), alignment = HorizontalBlockAlignment.CENTER)
        engine.block.alignVertically(listOf(headline), alignment = VerticalBlockAlignment.CENTER)
    }
    // highlight-android-align-single-block

    // highlight-android-check-distributable
    val horizontalDistributionBlocks = listOf(50F, 180F, 400F, 650F).map { positionX ->
        createShapeBlock(
            engine = engine,
            parent = page,
            color = Color.fromRGBA(r = 0.96F, g = 0.56F, b = 0.18F),
            x = positionX,
            y = 430F,
            width = 100F,
            height = 80F,
        )
    }

    val canDistributeHorizontally = engine.block.isDistributable(horizontalDistributionBlocks)
    if (canDistributeHorizontally) {
        // Blocks can be distributed with equal spacing.
    }
    // highlight-android-check-distributable

    // highlight-android-distribute-horizontal
    if (canDistributeHorizontally) {
        engine.block.distributeHorizontally(horizontalDistributionBlocks)
    }
    // highlight-android-distribute-horizontal

    // highlight-android-distribute-vertical
    val verticalDistributionBlocks = listOf(50F, 150F, 350F, 500F).map { positionY ->
        createShapeBlock(
            engine = engine,
            parent = page,
            color = Color.fromRGBA(r = 0.84F, g = 0.13F, b = 0.47F),
            x = 600F,
            y = positionY,
            width = 100F,
            height = 80F,
        )
    }

    if (engine.block.isDistributable(verticalDistributionBlocks)) {
        engine.block.distributeVertically(verticalDistributionBlocks)
    }
    // highlight-android-distribute-vertical

    // highlight-android-snapping-threshold
    engine.editor.setSettingFloat(keypath = "positionSnappingThreshold", value = 8F)
    engine.editor.setSettingFloat(keypath = "rotationSnappingThreshold", value = 0.26F)
    // highlight-android-snapping-threshold

    // highlight-android-snapping-colors
    engine.editor.setSettingColor(
        keypath = "snappingGuideColor",
        value = Color.fromRGBA(r = 0.18F, g = 0.38F, b = 0.85F),
    )
    engine.editor.setSettingColor(
        keypath = "rotationSnappingGuideColor",
        value = Color.fromRGBA(r = 0.96F, g = 0.56F, b = 0.18F),
    )
    // highlight-android-snapping-colors

    engine.stop()
}

// highlight-android-create-helper
fun createShapeBlock(
    engine: Engine,
    parent: DesignBlock,
    color: RGBAColor,
    x: Float,
    y: Float,
    width: Float = 120F,
    height: Float = 120F,
): DesignBlock {
    val block = engine.block.create(DesignBlockType.Graphic)
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block, fill = fill)
    engine.block.setFillSolidColor(block, color = color)
    engine.block.setWidth(block, value = width)
    engine.block.setHeight(block, value = height)
    engine.block.setPositionX(block, value = x)
    engine.block.setPositionY(block, value = y)
    engine.block.appendChild(parent = parent, child = block)
    return block
}
// highlight-android-create-helper
