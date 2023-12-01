import kotlinx.coroutines.*
import ly.img.engine.*

fun usingEffects(license: String, userId: String) = CoroutineScope(Dispatchers.Main).launch {
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
	engine.block.setPositionX(block, value = 100F)
	engine.block.setPositionY(block, value = 50F)
	engine.block.setWidth(block, value = 300F)
	engine.block.setHeight(block, value = 300F)
	engine.block.appendChild(parent = page, child = block)
	val fill = engine.block.createFill(FillType.Image)
	engine.block.setString(
		block = fill,
		property = "fill/image/imageFileURI",
		value = "https://img.ly/static/ubq_samples/sample_1.jpg"
	)
	engine.block.setFill(block, fill = fill)
	// highlight-setup

	// highlight-hasEffects
	engine.block.hasEffects(scene) // Returns false
	engine.block.hasEffects(block) // Returns true
	// highlight-hasEffects

	// highlight-createEffect
	val pixelize = engine.block.createEffect(type = EffectType.Pixelize)
	val adjustments = engine.block.createEffect(type = EffectType.Adjustments)
	// highlight-createEffect

	// highlight-addEffect
	engine.block.appendEffect(block, effectBlock = pixelize)
	engine.block.insertEffect(block, effectBlock = adjustments, index = 0)
	// engine.block.removeEffect(rect, index = 0)
	// highlight-addEffect

	// highlight-getEffects
	// This will return [adjustments, pixelize]
	val effectsList = engine.block.getEffects(block)
	// highlight-getEffects

	// highlight-destroyEffect
	val unusedEffect = engine.block.createEffect(type = EffectType.HalfTone)
	engine.block.destroy(unusedEffect)
	// highlight-destroyEffect

	// highlight-getProperties
	val allPixelizeProperties = engine.block.findAllProperties(pixelize)
	val allAdjustmentProperties = engine.block.findAllProperties(adjustments)
	// highlight-getProperties

	// highlight-modifyProperties
	engine.block.setInt(pixelize, property = "pixelize/horizontalPixelSize", value = 20)
	engine.block.setFloat(adjustments, property = "effect/adjustments/brightness", value = 0.2F)
	// highlight-modifyProperties

	// highlight-disableEffect
	engine.block.setEffectEnabled(pixelize, enabled = false)
	engine.block.setEffectEnabled(pixelize, !engine.block.isEffectEnabled(pixelize))
	// highlight-disableEffect

	engine.stop()
}
