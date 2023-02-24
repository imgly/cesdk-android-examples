import kotlinx.coroutines.*
import ly.img.engine.*

fun textWithEmojis() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.also { it.start() }
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-change-default-emoji-font
	val uri = engine.editor.getSettingString(keypath = "ubq://defaultEmojiFontFileUri")
	// From a bundle
	engine.editor.setSettingString(
		keypath = "ubq://defaultEmojiFontFileUri",
		value =	"file:///android_asset/ly.img.cesdk/fonts/NotoColorEmoji.ttf"
	)
	// From a URL
	engine.editor.setSettingString(
		keypath = "ubq://defaultEmojiFontFileUri",
		value = "https://cdn.img.ly/assets/v1/emoji/NotoColorEmoji.ttf"
	)
	// highlight-change-default-emoji-font

	// highlight-setup
	val scene = engine.scene.create()

	val page = engine.block.create(DesignBlockType.PAGE)
	engine.block.setWidth(page, value = 800F)
	engine.block.setHeight(page, value = 600F)
	engine.block.appendChild(parent = scene, child = page)

	engine.scene.zoomToBlock(page, paddingLeft = 40F, paddingTop = 40F, paddingRight = 40F, paddingBottom = 40F)
	// highlight-setup

	// highlight-add-text-with-emoji
	val text = engine.block.create(DesignBlockType.TEXT)
	engine.block.setString(text, property = "text/text", value = "Text with an emoji 🧐")
	engine.block.setWidth(text, value = 50F)
	engine.block.setHeight(text, value = 10F)
	engine.block.appendChild(parent = page, child = text)
	// highlight-add-text-with-emoji

	engine.stop()
}
