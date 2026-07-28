import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType

suspend fun multiPage(engine: Engine) = withContext(engine.dispatcher) {
    // highlight-android-create-scene
    // Create a scene with HorizontalStack layout.
    engine.scene.create(sceneLayout = SceneLayout.HORIZONTAL_STACK)

    // Get the stack container that owns pages in stack layouts.
    val stack = engine.block.findByType(DesignBlockType.Stack).first()

    // Create the first page.
    val firstPage = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(firstPage, value = 800F)
    engine.block.setHeight(firstPage, value = 600F)
    engine.block.appendChild(parent = stack, child = firstPage)
    // highlight-android-create-scene

    // highlight-android-stack-spacing
    // Add spacing between pages (20 pixels in screen space).
    engine.block.setFloat(stack, property = "stack/spacing", value = 20F)
    engine.block.setBoolean(stack, property = "stack/spacingInScreenspace", value = true)
    // highlight-android-stack-spacing

    // Add content to the first page.
    val imageBlock1 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock1, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(imageBlock1, value = 300F)
    engine.block.setHeight(imageBlock1, value = 200F)
    engine.block.setPositionX(imageBlock1, value = 250F)
    engine.block.setPositionY(imageBlock1, value = 200F)
    val imageFill1 = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = imageFill1,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(imageBlock1, fill = imageFill1)
    engine.block.appendChild(parent = firstPage, child = imageBlock1)

    // highlight-android-add-page
    // Create a second page with different content.
    val secondPage = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(secondPage, value = 800F)
    engine.block.setHeight(secondPage, value = 600F)
    engine.block.appendChild(parent = stack, child = secondPage)

    // Add a different image to the second page.
    val imageBlock2 = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock2, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(imageBlock2, value = 300F)
    engine.block.setHeight(imageBlock2, value = 200F)
    engine.block.setPositionX(imageBlock2, value = 250F)
    engine.block.setPositionY(imageBlock2, value = 200F)
    val imageFill2 = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = imageFill2,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_2.jpg",
    )
    engine.block.setFill(imageBlock2, fill = imageFill2)
    engine.block.appendChild(parent = secondPage, child = imageBlock2)
    // highlight-android-add-page

    // highlight-android-list-pages
    val pages = engine.scene.getPages()
    println("Pages: ${pages.size}")
    // highlight-android-list-pages

    // highlight-android-current-page
    val currentPage = engine.scene.getCurrentPage()
    println("Current page: $currentPage")
    // highlight-android-current-page

    // highlight-android-duplicate-page
    val duplicatedPage = engine.block.duplicate(firstPage)
    // highlight-android-duplicate-page

    // highlight-android-reorder-delete-pages
    engine.block.insertChild(parent = stack, child = secondPage, index = 0)

    if (engine.scene.getPages().size > 1) {
        engine.block.destroy(duplicatedPage)
    }
    // highlight-android-reorder-delete-pages

    // highlight-android-zoom-to-page
    engine.scene.zoomToBlock(
        block = firstPage,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
    )
    // highlight-android-zoom-to-page

    // highlight-android-nearest-page
    val nearestPage = engine.scene.findNearestToViewPortCenterByType(
        DesignBlockType.Page,
    ).firstOrNull()
    println("Nearest page: $nearestPage")
    // highlight-android-nearest-page

    engine.block.forceLoadResources(listOf(imageBlock1, imageBlock2))
}
