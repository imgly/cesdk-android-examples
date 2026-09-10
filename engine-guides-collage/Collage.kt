import android.app.Application
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import kotlin.math.roundToInt

fun collage(
    application: Application,
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    Engine.init(application)
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    var engineStarted = false

    try {
        engineStarted = engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1080, height = 1920)
        createAndApplyCollage(engine = engine)
    } finally {
        if (engineStarted) {
            engine.stop()
        }
    }
}

private suspend fun createAndApplyCollage(engine: Engine) {
    val scene = engine.scene.create()
    val page = createCollagePage(engine = engine, width = 1080F, height = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    addImageSlot(
        engine = engine,
        page = page,
        uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
        x = 32F,
        y = 32F,
        width = 480F,
        height = 480F,
    )
    addImageSlot(
        engine = engine,
        page = page,
        uri = Uri.parse("https://img.ly/static/ubq_samples/sample_2.jpg"),
        x = 568F,
        y = 32F,
        width = 480F,
        height = 480F,
    )
    addTextBlock(
        engine = engine,
        page = page,
        text = "Weekend trip",
        x = 64F,
        y = 830F,
    )

    // highlight-android-define-layout
    val layoutPage = createCollagePage(engine = engine, width = 1080F, height = 1080F)
    addImageSlot(engine = engine, page = layoutPage, x = 32F, y = 32F, width = 1016F, height = 496F)
    addImageSlot(engine = engine, page = layoutPage, x = 32F, y = 560F, width = 492F, height = 360F)
    addImageSlot(engine = engine, page = layoutPage, x = 556F, y = 560F, width = 492F, height = 360F)
    addTextBlock(engine = engine, page = layoutPage, text = "Title", x = 64F, y = 952F)

    val layoutBlocksString = engine.block.saveToString(blocks = listOf(layoutPage))
    engine.block.destroy(layoutPage)
    // highlight-android-define-layout

    // highlight-android-apply-layout
    val collagePage = applyCollageLayout(
        engine = engine,
        currentPage = page,
        layoutBlocksString = layoutBlocksString,
        addUndoStep = true,
    )
    // highlight-android-apply-layout

    engine.scene.zoomToBlock(
        block = collagePage,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
}

// highlight-android-layout-workflow
suspend fun applyCollageLayout(
    engine: Engine,
    currentPage: DesignBlock,
    layoutBlocksString: String,
    addUndoStep: Boolean = true,
): DesignBlock {
    val previousDestroyScope = engine.editor.getGlobalScope("lifecycle/destroy")
    engine.editor.setGlobalScope(key = "lifecycle/destroy", globalScope = GlobalScope.ALLOW)

    return try {
        engine.block.findAllSelected().forEach { selectedBlock ->
            engine.block.setSelected(block = selectedBlock, selected = false)
        }

        var oldPage: DesignBlock? = null
        var loadedLayoutPage: DesignBlock? = null

        try {
            oldPage = engine.block.duplicate(block = currentPage, attachToParent = false)
            loadedLayoutPage = engine.block.loadFromString(layoutBlocksString).first()

            engine.block.getChildren(currentPage).forEach(engine.block::destroy)
            engine.block.getChildren(loadedLayoutPage).forEach { child ->
                engine.block.insertChild(parent = currentPage, child = child, index = engine.block.getChildren(currentPage).size)
            }

            transferCollageContent(engine = engine, fromPage = oldPage, toPage = currentPage)
        } finally {
            loadedLayoutPage?.let { engine.block.destroy(it) }
            oldPage?.let { engine.block.destroy(it) }
        }

        if (addUndoStep) {
            engine.editor.addUndoStep()
        }
        currentPage
    } finally {
        engine.editor.setGlobalScope(key = "lifecycle/destroy", globalScope = previousDestroyScope)
    }
}
// highlight-android-layout-workflow

// highlight-android-transfer-content
private fun transferCollageContent(
    engine: Engine,
    fromPage: DesignBlock,
    toPage: DesignBlock,
) {
    val sourceBlocks = visuallySortedBlocks(
        engine = engine,
        rootPage = fromPage,
        blocks = collectChildrenTree(engine = engine, parent = fromPage),
    )
    val targetBlocks = visuallySortedBlocks(
        engine = engine,
        rootPage = toPage,
        blocks = collectChildrenTree(engine = engine, parent = toPage),
    )

    val sourceImages = sourceBlocks.filter { isImageBlock(engine = engine, designBlock = it) }
    val targetImages = targetBlocks.filter { isImageBlock(engine = engine, designBlock = it) }
    sourceImages.zip(targetImages).forEach { (sourceImage, targetImage) ->
        copyImageContent(engine = engine, sourceImage = sourceImage, targetImage = targetImage)
    }

    val sourceTexts = sourceBlocks.filter { engine.block.getType(it) == DesignBlockType.Text.key }
    val targetTexts = targetBlocks.filter { engine.block.getType(it) == DesignBlockType.Text.key }
    sourceTexts.zip(targetTexts).forEach { (sourceText, targetText) ->
        copyTextContent(engine = engine, sourceText = sourceText, targetText = targetText)
    }
}
// highlight-android-transfer-content

// highlight-android-visual-sort
private fun collectChildrenTree(
    engine: Engine,
    parent: DesignBlock,
): List<DesignBlock> = engine.block.getChildren(parent).flatMap { child ->
    listOf(child) + collectChildrenTree(engine = engine, parent = child)
}

private data class UntransformedPagePosition(
    val x: Float,
    val y: Float,
)

private data class PositionedBlock(
    val block: DesignBlock,
    val position: UntransformedPagePosition,
)

private fun visuallySortedBlocks(
    engine: Engine,
    rootPage: DesignBlock,
    blocks: List<DesignBlock>,
): List<DesignBlock> = blocks
    .map { designBlock ->
        PositionedBlock(
            block = designBlock,
            position = untransformedPagePosition(engine = engine, rootPage = rootPage, designBlock = designBlock),
        )
    }
    .sortedWith(
        compareBy<PositionedBlock> { it.position.y.roundToInt() }
            .thenBy { it.position.x.roundToInt() },
    )
    .map { it.block }

private fun untransformedPagePosition(
    engine: Engine,
    rootPage: DesignBlock,
    designBlock: DesignBlock,
): UntransformedPagePosition {
    var x = engine.block.getPositionX(designBlock)
    var y = engine.block.getPositionY(designBlock)
    var parent = engine.block.getParent(designBlock)

    // This local-offset sort is for unrotated and unscaled layout slots.
    while (parent != null && parent != rootPage) {
        x += engine.block.getPositionX(parent)
        y += engine.block.getPositionY(parent)
        parent = engine.block.getParent(parent)
    }

    return UntransformedPagePosition(x = x, y = y)
}
// highlight-android-visual-sort

// highlight-android-copy-images
private fun copyImageContent(
    engine: Engine,
    sourceImage: DesignBlock,
    targetImage: DesignBlock,
) {
    val sourceFill = engine.block.getFill(sourceImage)
    val targetFill = engine.block.getFill(targetImage)

    // Image fills use a generic property key, but Android keeps the value typed as Uri.
    engine.block.setUri(
        block = targetFill,
        property = "fill/image/imageFileURI",
        value = engine.block.getUri(sourceFill, property = "fill/image/imageFileURI"),
    )
    engine.block.setSourceSet(
        block = targetFill,
        property = "fill/image/sourceSet",
        sourceSet = engine.block.getSourceSet(sourceFill, property = "fill/image/sourceSet"),
    )
    if (engine.block.supportsPlaceholderBehavior(sourceImage)) {
        engine.block.setPlaceholderBehaviorEnabled(
            block = targetImage,
            enabled = engine.block.isPlaceholderBehaviorEnabled(sourceImage),
        )
    }
    engine.block.resetCrop(targetImage)
}
// highlight-android-copy-images

// highlight-android-copy-text
private fun copyTextContent(
    engine: Engine,
    sourceText: DesignBlock,
    targetText: DesignBlock,
) {
    // Reading plain text still uses property access; replaceText keeps the write type-safe.
    engine.block.replaceText(
        block = targetText,
        text = engine.block.getString(sourceText, property = "text/text"),
    )
    runCatching {
        engine.block.setFont(
            block = targetText,
            fontFileUri = engine.block.getUri(sourceText, property = "text/fontFileUri"),
            typeface = engine.block.getTypeface(sourceText),
        )
    }
    engine.block.getTextColors(sourceText).firstOrNull()?.let { color ->
        engine.block.setTextColor(block = targetText, color = color)
    }
}
// highlight-android-copy-text

// highlight-android-image-slot-check
private fun isImageBlock(
    engine: Engine,
    designBlock: DesignBlock,
): Boolean = engine.block.supportsFill(designBlock) && engine.block.getType(engine.block.getFill(designBlock)) == FillType.Image.key
// highlight-android-image-slot-check

// highlight-android-create-page-helper
private fun createCollagePage(
    engine: Engine,
    width: Float,
    height: Float,
): DesignBlock {
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = width)
    engine.block.setHeight(page, value = height)
    return page
}
// highlight-android-create-page-helper

// highlight-android-image-slot-helper
private fun addImageSlot(
    engine: Engine,
    page: DesignBlock,
    uri: Uri? = null,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
): DesignBlock {
    val image = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(image, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(image, value = x)
    engine.block.setPositionY(image, value = y)
    engine.block.setWidth(image, value = width)
    engine.block.setHeight(image, value = height)

    val fill = engine.block.createFill(FillType.Image)
    if (uri != null) {
        // Image fills use a generic property key, but Android keeps the value typed as Uri.
        engine.block.setUri(block = fill, property = "fill/image/imageFileURI", value = uri)
    }
    engine.block.setFill(block = image, fill = fill)
    engine.block.appendChild(parent = page, child = image)
    return image
}
// highlight-android-image-slot-helper

// highlight-android-text-block-helper
private fun addTextBlock(
    engine: Engine,
    page: DesignBlock,
    text: String,
    x: Float,
    y: Float,
): DesignBlock {
    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = textBlock, text = text)
    engine.block.setPositionX(textBlock, value = x)
    engine.block.setPositionY(textBlock, value = y)
    engine.block.setWidth(textBlock, value = 640F)
    engine.block.setHeight(textBlock, value = 80F)
    engine.block.setTextColor(
        block = textBlock,
        color = Color.fromRGBA(r = 0.08F, g = 0.08F, b = 0.08F, a = 1F),
    )
    engine.block.appendChild(parent = page, child = textBlock)
    return textBlock
}
// highlight-android-text-block-helper
