import kotlinx.coroutines.*
import ly.img.engine.*

fun usingBoolOps() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.getInstance(id = "ly.img.engine.example")
	engine.start()
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.PAGE)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)
	// highlight-setup

	// highlight-combine-union
	val circle1 = engine.block.create("shapes/ellipse")
	engine.block.setPositionX(circle1, value = 30F)
	engine.block.setPositionY(circle1, value = 30F)
	engine.block.setWidth(circle1, value = 40F)
	engine.block.setHeight(circle1, value = 40F)
	engine.block.appendChild(parent = page, child = circle1)

	val circle2 = engine.block.create("shapes/ellipse")
	engine.block.setPositionX(circle2, value = 80F)
	engine.block.setPositionY(circle2, value = 30F)
	engine.block.setWidth(circle2, value = 40F)
	engine.block.setHeight(circle2, value = 40F)
	engine.block.appendChild(parent = page, child = circle2)

	val circle3 = engine.block.create("shapes/ellipse")
	engine.block.setPositionX(circle3, value = 50F)
	engine.block.setPositionY(circle3, value = 50F)
	engine.block.setWidth(circle3, value = 50F)
	engine.block.setHeight(circle3, value = 50F)
	engine.block.appendChild(parent = page, child = circle3)

	engine.block.combine(listOf(circle1, circle2, circle3), op = BooleanOperation.UNION);
	// highlight-combine-unions

	// highlight-combine-difference
	var text = engine.block.create("text")
	engine.block.replaceText(text, "Removed text")
	engine.block.setPositionX(text, 10F)
	engine.block.setPositionY(text, 40F)
	engine.block.setWidth(text, 80F)
	engine.block.setHeight(text, 10F)
	engine.block.appendChild(parent = page, child = text)

	var image = engine.block.create("image")
	engine.block.setPositionX(image, 0F)
	engine.block.setPositionY(image, 0F)
	engine.block.setWidth(image, 100F)
	engine.block.setHeight(image, 100F)
	engine.block.setString(
		block.getFill(image),
		"fill/image/imageFileURI",
		"https://img.ly/static/ubq_samples/sample_1.jpg"
	)
	engine.block.appendChild(parent = page, child = image)

	engine.block.sendToBack(image)
	var difference = engine.block.combine(listOf(image, text), op = BooleanOperation.DIFFERENCE)
	// highlight-combine-difference

	engine.stop()
}
