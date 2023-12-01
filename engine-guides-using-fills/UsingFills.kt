import kotlinx.coroutines.*
import ly.img.engine.*

fun usingFills(license: String, userId: String) = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.getInstance(id = "ly.img.engine.example")
	engine.start(license = license, userId = userId)
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.Page)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)

	engine.scene.zoomToBlock(page, paddingLeft = 40F, paddingTop = 40F, paddingRight = 40F, paddingBottom = 40F)

	val block = engine.block.create(DesignBlockType.Graphic)
	engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
	engine.block.setWidth(block, value = 100F)
	engine.block.setHeight(block, value = 100F)
	engine.block.setFill(block, fill = engine.block.createFill(FillType.Color))
	engine.block.appendChild(parent = page, child = block)
	// highlight-setup

	// highlight-hasFill
	engine.block.hasFill(scene) // Returns false
	engine.block.hasFill(block) // Returns true
	// highlight-hasFill

	// highlight-getFill
	val colorFill = engine.block.getFill(block)
	val defaultRectFillType = engine.block.getType(colorFill)
	// highlight-getFill
	// highlight-getProperties
	val allFillProperties = engine.block.findAllProperties(colorFill)
	// highlight-getProperties
	// highlight-modifyProperties
	engine.block.setColor(
		block = colorFill,
		property = "fill/color/value",
		value = Color.fromRGBA(r =  1.0F, g = 0.0F, b = 0.0F, a = 1.0F)
	)
	// highlight-modifyProperties

	// highlight-disableFill
	engine.block.setFillEnabled(block, enabled = false)
	engine.block.setFillEnabled(block, enabled = !engine.block.isFillEnabled(block))
	// highlight-disableFill

	// highlight-createFill
	val imageFill = engine.block.createFill(FillType.Image)
	engine.block.setString(
		block = imageFill,
		property = "fill/image/imageFileURI",
		value = "https://img.ly/static/ubq_samples/sample_1.jpg"
	)
	// highlight-createFill

	// highlight-replaceFill
	engine.block.destroy(colorFill)
	engine.block.setFill(block, fill = imageFill)

	/*
	// The following line would also destroy imageFill
	engine.block.destroy(circle)
	*/
	// highlight-replaceFill

	// highlight-duplicateFill
	val duplicateBlock = engine.block.duplicate(block)
	engine.block.setPositionX(duplicateBlock, value = 450F)
	val autoDuplicateFill = engine.block.getFill(duplicateBlock)
	engine.block.setString(
		block = autoDuplicateFill,
		property = "fill/image/imageFileURI",
		value = "https://img.ly/static/ubq_samples/sample_2.jpg"
	)

	/*
	// We could now assign this fill to another block.
	val manualDuplicateFill = engine.block.duplicate(autoDuplicateFill)
	engine.block.destroy(manualDuplicateFill)
	*/
	// highlight-duplicateFill

	// highlight-sharedFill
	val sharedFillBlock = engine.block.create(DesignBlockType.Graphic)
	engine.block.setShape(sharedFillBlock, shape = engine.block.createShape(ShapeType.Rect))
	engine.block.setPositionX(sharedFillBlock, value = 350F)
	engine.block.setPositionY(sharedFillBlock, value = 400F)
	engine.block.setWidth(sharedFillBlock, value = 100F)
	engine.block.setHeight(sharedFillBlock, value = 100F)
	engine.block.appendChild(parent = page, child = sharedFillBlock)
	engine.block.setFill(sharedFillBlock, fill = engine.block.getFill(block))
	// highlight-sharedFill

	engine.stop()
}
