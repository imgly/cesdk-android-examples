import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun textWithEmojis(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-change-default-emoji-font
    val uri = engine.editor.getSettingString(keypath = "ubq://defaultEmojiFontFileUri")
    // From a bundle
    engine.editor.setSettingString(
        keypath = "ubq://defaultEmojiFontFileUri",
        value = "file:///android_asset/ly.img.cesdk/fonts/NotoColorEmoji.ttf",
    )
    // From a URL
    engine.editor.setSettingString(
        keypath = "ubq://defaultEmojiFontFileUri",
        value = "https://cdn.img.ly/assets/v3/emoji/NotoColorEmoji.ttf",
    )
    // highlight-change-default-emoji-font

    // highlight-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    engine.scene.zoomToBlock(
        page,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-setup

    // highlight-add-text-with-emoji
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.setString(text, property = "text/text", value = "Text with an emoji 🧐")
    engine.block.setWidth(text, value = 50F)
    engine.block.setHeight(text, value = 10F)
    engine.block.appendChild(parent = page, child = text)
    // highlight-add-text-with-emoji

    engine.stop()
}
