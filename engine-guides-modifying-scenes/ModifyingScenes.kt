import kotlinx.coroutines.*
import ly.img.engine.*

fun modifyingScenes() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.getInstance(id = "ly.img.engine.example")
	engine.start()
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-scene-get-create
	// In engine only mode we have to create our own scene and page.
	if (engine.scene.get() == null) {
		val scene = engine.scene.create()
		// highlight-scene-get-create
		// highlight-page-get-create
		val page = engine.block.create(DesignBlockType.PAGE)
		engine.block.appendChild(parent = scene, child = page)
	}

	// Find all pages in our scene.
	val pages = engine.block.findByType(DesignBlockType.PAGE)
	// Use the first page we found.
	val page = pages.first()
	// highlight-page-get-create

	// highlight-create-image
	// Create an image block and add it to the scene's page.
	val image = engine.block.create(DesignBlockType.IMAGE)
	// highlight-create-image

	// highlight-image-properties
	engine.block.setString(
		block = image,
		property = "image/imageFileURI",
		value = "https://img.ly/static/ubq_samples/imgly_logo.jpg"
	)

	// The content fill mode 'Contain' ensures the entire image is visible.
	engine.block.setEnum(
		block = image,
		property = "contentFill/mode",
		value = "Contain"
	)
	// highlight-image-properties

	// highlight-image-append
	engine.block.appendChild(parent = page, child = image)
	// highlight-image-append

	// highlight-zoom-page
	// Zoom the scene's camera on our page.
	engine.scene.zoomToBlock(page)
	// highlight-zoom-page

	engine.stop()
}
