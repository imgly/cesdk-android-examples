import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

suspend fun pages(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): PagesGuideSummary = withContext(Dispatchers.Main.immediate) {
    val engine = Engine.getInstance(id = "ly.img.engine.example.pages")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    try {
        // highlight-pages-createScene
        // Create a scene with VerticalStack layout for multi-page designs.
        val scene = engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)

        val stack = engine.block.findByType(DesignBlockType.Stack).first()
        engine.block.setFloat(block = stack, property = "stack/spacing", value = 20F)
        engine.block.setBoolean(
            block = stack,
            property = "stack/spacingInScreenspace",
            value = true,
        )
        // highlight-pages-createScene

        // highlight-pages-setDimensions
        // Set page dimensions at the scene level so new pages share the same size.
        engine.block.setFloat(
            block = scene,
            property = "scene/pageDimensions/width",
            value = 800F,
        )
        engine.block.setFloat(
            block = scene,
            property = "scene/pageDimensions/height",
            value = 600F,
        )
        // highlight-pages-setDimensions

        // highlight-pages-createPages
        val firstPage = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(block = firstPage, value = 800F)
        engine.block.setHeight(block = firstPage, value = 600F)
        engine.block.appendChild(parent = stack, child = firstPage)

        val secondPage = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(block = secondPage, value = 800F)
        engine.block.setHeight(block = secondPage, value = 600F)
        engine.block.appendChild(parent = stack, child = secondPage)
        // highlight-pages-createPages

        // highlight-pages-addContent
        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.appendChild(parent = firstPage, child = imageBlock)

        val rectShape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(block = imageBlock, shape = rectShape)
        engine.block.setWidth(block = imageBlock, value = 400F)
        engine.block.setHeight(block = imageBlock, value = 300F)
        engine.block.setPositionX(block = imageBlock, value = 200F)
        engine.block.setPositionY(block = imageBlock, value = 150F)

        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setString(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = "https://img.ly/static/ubq_samples/sample_1.jpg",
        )
        engine.block.setFill(block = imageBlock, fill = imageFill)

        val textBlock = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = secondPage, child = textBlock)
        engine.block.replaceText(textBlock, text = "Page 2")
        engine.block.setTextFontSize(block = textBlock, fontSize = 48F)
        engine.block.setTextColor(
            block = textBlock,
            color = Color.fromRGBA(r = 0.2F, g = 0.2F, b = 0.2F, a = 1F),
        )
        engine.block.setWidthMode(block = textBlock, mode = SizeMode.AUTO)
        engine.block.setHeightMode(block = textBlock, mode = SizeMode.AUTO)

        val textWidth = engine.block.getFrameWidth(textBlock)
        val textHeight = engine.block.getFrameHeight(textBlock)
        engine.block.setPositionX(block = textBlock, value = (800F - textWidth) / 2F)
        engine.block.setPositionY(block = textBlock, value = (600F - textHeight) / 2F)
        // highlight-pages-addContent

        // highlight-pages-pageMargins
        engine.block.setBoolean(
            block = firstPage,
            property = "page/marginEnabled",
            value = true,
        )
        engine.block.setFloat(block = firstPage, property = "page/margin/top", value = 10F)
        engine.block.setFloat(block = firstPage, property = "page/margin/bottom", value = 10F)
        engine.block.setFloat(block = firstPage, property = "page/margin/left", value = 10F)
        engine.block.setFloat(block = firstPage, property = "page/margin/right", value = 10F)
        // highlight-pages-pageMargins

        // highlight-pages-titleTemplate
        engine.block.setString(
            block = firstPage,
            property = "page/titleTemplate",
            value = "Cover",
        )
        engine.block.setString(
            block = secondPage,
            property = "page/titleTemplate",
            value = "Content",
        )
        // highlight-pages-titleTemplate

        // highlight-pages-pageBackground
        engine.block.setFillSolidColor(
            block = secondPage,
            color = Color.fromRGBA(r = 0.95F, g = 0.95F, b = 1F, a = 1F),
        )
        // highlight-pages-pageBackground

        // highlight-pages-findPages
        val allPages = engine.scene.getPages()
        val currentPage = engine.scene.getCurrentPage()
        val pagesByType = engine.block.findByType(DesignBlockType.Page)
        val nearestPages = engine.scene.findNearestToViewPortCenterByType(DesignBlockType.Page)
        // highlight-pages-findPages

        PagesGuideSummary(
            pageCount = allPages.size,
            currentPageTitle = currentPage?.let {
                engine.block.getString(block = it, property = "page/titleTemplate")
            },
            pageTitles = pagesByType.map {
                engine.block.getString(block = it, property = "page/titleTemplate")
            },
            nearestPageCount = nearestPages.size,
        )
    } finally {
        engine.stop()
    }
}
