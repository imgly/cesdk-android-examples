import kotlinx.coroutines.*
import ly.img.engine.*

fun modifyingScenes(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-scene-get-create
    // In engine only mode we have to create our own scene and page.
    if (engine.scene.get() == null) {
        val scene = engine.scene.create()
        // highlight-scene-get-create
        // highlight-page-get-create
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)
    }

    // Find all pages in our scene.
    val pages = engine.block.findByType(DesignBlockType.Page)
    // Use the first page we found.
    val page = pages.first()
    // highlight-page-get-create

    // highlight-create-image
    // Create a graphic block and add it to the scene's page.
    val block = engine.block.create(DesignBlockType.Graphic)
    val fill = engine.block.createFill(FillType.Image)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = block, fill = fill)
    // highlight-create-image

    // highlight-image-properties
    engine.block.setString(
        block = fill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/imgly_logo.jpg",
    )

    // The content fill mode 'Contain' ensures the entire image is visible.
    engine.block.setEnum(
        block = block,
        property = "contentFill/mode",
        value = "Contain",
    )
    // highlight-image-properties

    // highlight-image-append
    engine.block.appendChild(parent = page, child = block)
    // highlight-image-append

    // highlight-zoom-page
    // Zoom the scene's camera on our page.
    engine.scene.zoomToBlock(page)
    // highlight-zoom-page

    engine.stop()
}
