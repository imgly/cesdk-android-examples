import kotlinx.coroutines.*
import ly.img.engine.*

fun spotColors() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.also { it.start() }
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.PAGE)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)

	engine.scene.zoomToBlock(page, paddingLeft = 40F, paddingTop = 40F, paddingRight = 40F, paddingBottom = 40F)

	val text = engine.block.create(DesignBlockType.TEXT)
	engine.block.setPositionX(text, value = 350F)
	engine.block.setPositionY(text, value = 100F)

	val star = engine.block.create(DesignBlockType.STAR_SHAPE)
	engine.block.setPositionX(star, value = 350F)
	engine.block.setPositionY(star, value = 400F)
	engine.block.setWidth(star, value = 100F)
	engine.block.setHeight(star, value = 100F)
	// highlight-setup

	// highlight-create
	engine.editor.setSpotColor(name = "Crayola-Pink-Flamingo", Color.fromRGBA(r = 0.988F, g = 0.455F, b = 0.992F, a = 1F))
	engine.editor.setSpotColor(name = "Pantone-ColorOfTheYear-2022", Color.fromRGBA(r = 0.4F, g = 0.404F, b = 0.671F, a = 1F))
	engine.editor.setSpotColor(name = "Yellow", Color.fromRGBA(r = 1F, g = 1F, b = 0F, a = 1F))
	engine.editor.findAllSpotColors() // ["Crayola-Pink-Flamingo", "Pantone-ColorOfTheYear-2022", "Yellow"]
	// highlight-create

	// highlight-apply-star
	engine.block.setColorSpot(star, property = "fill/solid/color", name = "Crayola-Pink-Flamingo")
	engine.block.setColorSpot(star, property = "stroke/color", name = "Yellow", tint = 0.8F)
	engine.block.setStrokeEnabled(star, enabled = true)

	engine.block.getColorSpotName(star, property = "fill/solid/color") // "Crayola-Pink-Flamingo"
	engine.block.getColorSpotTint(star, property = "stroke/color") // 0.8
	// highlight-apply-star

	// highlight-apply-text
	engine.block.setColorSpot(text, property = "fill/solid/color", name = "Yellow")
	engine.block.setColorSpot(text, property = "stroke/color", name = "Crayola-Pink-Flamingo", tint = 0.5F)
	engine.block.setStrokeEnabled(text, enabled = true)

	engine.block.setColorSpot(text, property = "backgroundColor/color", name = "Pantone-ColorOfTheYear-2022", tint = 0.9F)
	engine.block.setBackgroundColorEnabled(text, enabled = true)
	// highlight-apply-text

	// highlight-change
	engine.editor.setSpotColor(name = "Yellow", Color.fromRGBA(r = 1F, g = 1F, b = 0.4F, a = 1F))
	// highlight-change

	// highlight-undefine
	engine.editor.removeSpotColor(name = "Yellow")
	// highlight-undefine

	engine.stop()
}
