import kotlinx.coroutines.*
import ly.img.engine.*

fun cutouts() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine(id = "ly.img.engine.example")
	engine.start()
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.PAGE)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)
	// highlight-setup

	// highlight-create-cutouts
	val circle = engine.block.createCutoutFromPath("M 0,25 a 25,25 0 1,1 50,0 a 25,25 0 1,1 -50,0 Z");
	engine.block.setCutoutTypeOffset(circle, offset = 3F);
	engine.block.setCutoutType(circle, type = CutoutType.DASHED);

	val square = engine.block.createCutoutFromPath("M 0,0 H 50 V 50 H 0 Z");
	engine.block.setCutoutTypeOffset(square, offset = 6F);
	// highlight-create-cutouts

	// highlight-cutout-union
	val union = engine.block.createCutoutFromOperation(listOf(circle, square), op = CutoutOperation.UNION);
	engine.block.destroy(circle);
	engine.block.destroy(square);
	// highlight-cutout-union

	// highlight-spot-color-solid
	engine.editor.setSpotColorRGB("CutContour", r = 0F, g = 0F, b = 1F);
	// highlight-spot-color-solid

	engine.stop()
}
