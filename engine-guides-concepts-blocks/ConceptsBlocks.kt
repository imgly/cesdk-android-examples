import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.HorizontalAlignment
import ly.img.engine.ShapeType

suspend fun conceptsBlocks(engine: Engine) = withContext(engine.dispatcher) {
    var selectionObserver: Job? = null
    var stateObserver: Job? = null

    try {
        val scene = engine.scene.create()

        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        engine.scene.zoomToBlock(
            page,
            paddingLeft = 40F,
            paddingTop = 40F,
            paddingRight = 40F,
            paddingBottom = 40F,
        )
        // highlight-android-block-types
        val pages = engine.block.findByType(DesignBlockType.Page)
        val firstPage = pages.first()

        val pageType = engine.block.getType(firstPage)
        println("Page block type: $pageType")
        // highlight-android-block-types

        // highlight-android-type-vs-kind
        engine.block.setKind(firstPage, kind = "main-canvas")
        val pageKind = engine.block.getKind(firstPage)
        println("Page kind: $pageKind")

        val mainCanvasBlocks = engine.block.findByKind("main-canvas")
        println("Blocks with kind 'main-canvas': ${mainCanvasBlocks.size}")
        // highlight-android-type-vs-kind

        // highlight-android-block-lifecycle
        val graphic = engine.block.create(DesignBlockType.Graphic)

        val graphicCopy = engine.block.duplicate(graphic)
        engine.block.destroy(graphicCopy)

        val isOriginalValid = engine.block.isValid(graphic)
        val isCopyValid = engine.block.isValid(graphicCopy)
        println("Original valid: $isOriginalValid")
        println("Copy valid after destroy: $isCopyValid")
        // highlight-android-block-lifecycle

        // highlight-android-shape
        val rectShape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(graphic, shape = rectShape)

        engine.block.setPositionX(graphic, value = 200F)
        engine.block.setPositionY(graphic, value = 100F)
        engine.block.setWidth(graphic, value = 400F)
        engine.block.setHeight(graphic, value = 300F)
        // highlight-android-shape

        // highlight-android-fill
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setString(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = "https://img.ly/static/ubq_samples/sample_1.jpg",
        )
        engine.block.setFill(graphic, fill = imageFill)

        engine.block.setContentFillMode(graphic, ContentFillMode.COVER)
        // highlight-android-fill

        // highlight-android-block-hierarchy
        engine.block.appendChild(parent = page, child = graphic)

        val graphicParent = engine.block.getParent(graphic)
        println("Graphic parent is page: ${graphicParent == page}")

        val pageChildren = engine.block.getChildren(page)
        println("Page has children: ${pageChildren.size}")
        // highlight-android-block-hierarchy

        // highlight-android-text-block
        val textBlock = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = textBlock)

        engine.block.setPositionX(textBlock, value = 200F)
        engine.block.setPositionY(textBlock, value = 450F)
        engine.block.setWidth(textBlock, value = 400F)
        engine.block.setHeight(textBlock, value = 80F)

        engine.block.setString(
            block = textBlock,
            property = "text/text",
            value = "Blocks are the building units of CE.SDK designs",
        )
        engine.block.setTextFontSize(textBlock, fontSize = 24F)
        engine.block.setTextHorizontalAlignment(textBlock, alignment = HorizontalAlignment.Center)

        val textType = engine.block.getType(textBlock)
        println("Text block type: $textType")
        // highlight-android-text-block

        // highlight-android-block-properties
        val graphicProperties = engine.block.findAllProperties(graphic)
        println("Graphic block has ${graphicProperties.size} properties")

        val opacityType = engine.block.getPropertyType("opacity")
        println("Opacity property type: $opacityType")

        val isOpacityReadable = engine.block.isPropertyReadable("opacity")
        val isOpacityWritable = engine.block.isPropertyWritable("opacity")
        println("Opacity readable: $isOpacityReadable writable: $isOpacityWritable")
        // highlight-android-block-properties

        // highlight-android-property-accessors
        engine.block.setFloat(block = graphic, property = "opacity", value = 0.9F)
        val opacity = engine.block.getFloat(block = graphic, property = "opacity")
        println("Graphic opacity: $opacity")

        engine.block.setBoolean(block = page, property = "page/marginEnabled", value = false)
        val marginEnabled = engine.block.getBoolean(block = page, property = "page/marginEnabled")
        println("Page margin enabled: $marginEnabled")

        val blendModes = engine.block.getEnumValues("blend/mode")
        println("Available blend modes: ${blendModes.take(3).joinToString()} ...")

        engine.block.setEnum(block = graphic, property = "blend/mode", value = "Multiply")
        val blendMode = engine.block.getEnum(block = graphic, property = "blend/mode")
        println("Graphic blend mode: $blendMode")
        // highlight-android-property-accessors

        // highlight-android-uuid-identity
        val graphicUUID = engine.block.getUUID(graphic)
        println("Graphic UUID: $graphicUUID")

        engine.block.setName(graphic, name = "Hero Image")
        engine.block.setName(textBlock, name = "Caption")

        val graphicName = engine.block.getName(graphic)
        println("Graphic name: $graphicName")

        val namedBlocks = engine.block.findByName("Hero Image")
        println("Blocks named Hero Image: ${namedBlocks.size}")
        // highlight-android-uuid-identity

        // highlight-android-selection
        selectionObserver = launch {
            engine.block.onSelectionChanged().collect {
                val selected = engine.block.findAllSelected()
                println("Selection changed, now selected: ${selected.size} blocks")
            }
        }

        engine.block.select(graphic)
        val isGraphicSelected = engine.block.isSelected(graphic)
        println("Graphic is selected: $isGraphicSelected")

        engine.block.setSelected(textBlock, selected = true)
        val selectedBlocks = engine.block.findAllSelected()
        println("Selected blocks count: ${selectedBlocks.size}")
        // highlight-android-selection

        // highlight-android-visibility
        engine.block.setVisible(graphic, visible = true)
        val isVisible = engine.block.isVisible(graphic)
        println("Graphic is visible: $isVisible")

        engine.block.setIncludedInExport(graphic, enabled = true)
        val inExport = engine.block.isIncludedInExport(graphic)
        println("Graphic included in export: $inExport")
        // highlight-android-visibility

        // highlight-android-clipping
        engine.block.setClipped(graphic, clipped = false)
        val isClipped = engine.block.isClipped(graphic)
        println("Graphic is clipped: $isClipped")
        // highlight-android-clipping

        // highlight-android-block-state
        val graphicState = engine.block.getState(graphic)
        println("Graphic state: $graphicState")

        stateObserver = launch {
            engine.block.onStateChanged(listOf(graphic)).collect { changedBlocks ->
                changedBlocks.forEach { changedBlock ->
                    val state = engine.block.getState(changedBlock)
                    println("Block $changedBlock state changed to: $state")
                }
            }
        }
        // highlight-android-block-state

        // highlight-android-serialization
        val savedString = engine.block.saveToString(blocks = listOf(graphic, textBlock))
        println("Blocks saved to string, length: ${savedString.length}")

        // Alternatively, blocks can be saved with their assets in an archive:
        // val savedArchive = engine.block.saveToArchive(blocks = listOf(graphic, textBlock))

        val loadedBlocks = engine.block.loadFromString(savedString)
        println("Loaded blocks from string: ${loadedBlocks.size}")

        // Alternatively, blocks can be loaded from an archive or an extracted archive directory:
        // val loadedArchiveBlocks = engine.block.loadFromArchive(Uri.parse("file:///path/to/blocks.zip"))
        // val loadedUrlBlocks = engine.block.loadFromURL(Uri.parse("file:///path/to/blocks.blocks"))

        loadedBlocks.forEach { loadedBlock ->
            engine.block.destroy(loadedBlock)
        }
        // highlight-android-serialization

        engine.block.forceLoadResources(listOf(graphic, textBlock))

        println("Blocks guide initialized successfully.")
        println("Created graphic and text blocks, then exercised hierarchy and state APIs.")
    } finally {
        selectionObserver?.cancel()
        stateObserver?.cancel()
    }
}
