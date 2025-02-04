import kotlinx.coroutines.*
import ly.img.engine.*

fun usingFills() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine(id = "ly.img.engine.example")
	engine.start()
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.PAGE)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)

	engine.scene.zoomToBlock(page, paddingLeft = 40F, paddingTop = 40F, paddingRight = 40F, paddingBottom = 40F)

	val rect = engine.block.create(DesignBlockType.RECT_SHAPE)
	engine.block.setWidth(rect, value = 100F)
	engine.block.setHeight(rect, value = 100F)
	engine.block.appendChild(parent = page, child = rect)

	val circle = engine.block.create(DesignBlockType.ELLIPSE_SHAPE)
	engine.block.setPositionX(circle, value = 100F)
	engine.block.setPositionY(circle, value = 50F)
	engine.block.setWidth(circle, value = 300F)
	engine.block.setHeight(circle, value = 300F)
	engine.block.appendChild(parent = page, child = circle)
	// highlight-setup

	// highlight-hasFill
	engine.block.hasFill(scene) // Returns false
	engine.block.hasFill(rect) // Returns true
	// highlight-hasFill

	// highlight-getFill
	val rectFill = engine.block.getFill(rect)
	val defaultRectFillType = engine.block.getType(rectFill)
	// highlight-getFill
	// highlight-getProperties
	val allFillProperties = engine.block.findAllProperties(rectFill)
	// highlight-getProperties
	// highlight-modifyProperties
	engine.block.setColor(
		block = rectFill,
		property = "fill/color/value",
		value = Color.fromRGBA(r =  1.0F, g = 0.0F, b = 0.0F, a = 1.0F)
	)
	// highlight-modifyProperties

	// highlight-disableFill
	engine.block.setFillEnabled(rect, enabled = false)
	engine.block.setFillEnabled(rect, enabled = !engine.block.isFillEnabled(rect))
	// highlight-disableFill

	// highlight-createFill
	val imageFill = engine.block.createFill("image")
	engine.block.setString(
		block = imageFill,
		property = "fill/image/imageFileURI",
		value = "https://img.ly/static/ubq_samples/sample_1.jpg"
	)
	// highlight-createFill

	// highlight-replaceFill
	engine.block.destroy(engine.block.getFill(circle))
	engine.block.setFill(circle, fill = imageFill)

	/*
	// The following line would also destroy imageFill
	engine.block.destroy(circle)
	*/
	// highlight-replaceFill

	// highlight-duplicateFill
	val duplicateCircle = engine.block.duplicate(circle)
	engine.block.setPositionX(duplicateCircle, value = 450F)
	val autoDuplicateFill = engine.block.getFill(duplicateCircle)
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
	val star = engine.block.create(DesignBlockType.STAR_SHAPE)
	engine.block.setPositionX(star, value = 350F)
	engine.block.setPositionY(star, value = 400F)
	engine.block.setWidth(star, value = 100F)
	engine.block.setHeight(star, value = 100F)
	engine.block.appendChild(parent = page, child = star)

	engine.block.setFill(star, fill = engine.block.getFill (circle))
	// highlight-sharedFill

	engine.stop()
}
