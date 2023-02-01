import kotlinx.coroutines.*
import ly.img.engine.*

fun usingEffects() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.also { it.start() }
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.PAGE)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)

	engine.scene.zoomToBlock(page, paddingLeft = 40F, paddingTop = 40F, paddingRight = 40F, paddingBottom = 40F)

	val rect = engine.block.create(DesignBlockType.RECT_SHAPE)
	engine.block.setPositionX(rect, value = 100F)
	engine.block.setPositionY(rect, value = 50F)
	engine.block.setWidth(rect, value = 300F)
	engine.block.setHeight(rect, value = 300F)
	engine.block.appendChild(parent = page, child = rect)
	val imageFill = engine.block.createFill("image")
	engine.block.destroy(engine.block.getFill(rect))
	engine.block.setString(
		block = imageFill,
		property = "fill/image/imageFileURI",
		value = "https://img.ly/static/ubq_samples/sample_1.jpg"
	)
	engine.block.setFill(rect, fill = imageFill)
	// highlight-setup

	// highlight-hasEffects
	engine.block.hasEffects(scene) // Returns false
	engine.block.hasEffects(rect) // Returns true
	// highlight-hasEffects

	// highlight-createEffect
	val pixelize = engine.block.createEffect(type = "pixelize")
	val adjustments = engine.block.createEffect(type = "adjustments")
	// highlight-createEffect

	// highlight-addEffect
	engine.block.appendEffect(rect, effectBlock = pixelize)
	engine.block.insertEffect(rect, effectBlock = adjustments, index = 0)
	// engine.block.removeEffect(rect, index = 0)
	// highlight-addEffect

	// highlight-getEffects
	// This will return [adjustments, pixelize]
	val effectsList = engine.block.getEffects(rect)
	// highlight-getEffects

	// highlight-destroyEffect
	val unusedEffect = engine.block.createEffect(type = "half_tone")
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
