import kotlinx.coroutines.*
import ly.img.engine.*

fun textProperties(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    val scene = engine.scene.create()
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = scene, child = text)
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)

    // highlight-replaceText
    engine.block.replaceText(text, text = "Hello World")
    // highlight-replaceText
    // highlight-replaceText-single-index
    // Add a "!" at the end of the text
    engine.block.replaceText(text, text = "!", from = 11)
    // highlight-replaceText-single-index
    // highlight-replaceText-range
    // Replace "World" with "Alex"
    engine.block.replaceText(text, text = "Alex", from = 6, to = 11)
    // highlight-replaceText-range

    engine.scene.zoomToBlock(
        block = text,
        paddingLeft = 100F,
        paddingTop = 100F,
        paddingRight = 100F,
        paddingBottom = 100F,
    )

    // highlight-removeText
    // Remove the "Hello "
    engine.block.removeText(text, from = 0, to = 6)
    // highlight-removeText

    // highlight-setTextColor
    engine.block.setTextColor(text, color = Color.fromHex("#FFFF0000"))
    // highlight-setTextColor
    // highlight-setTextColor-range
    engine.block.setTextColor(text, color = Color.fromHex("#FFFF0000"), from = 1, to = 4)
    // highlight-setTextColor-range
    // highlight-getTextColors
    val allColors = engine.block.getTextColors(text)
    // highlight-getTextColors
    // highlight-getTextColors-range
    val colorsInRange = engine.block.getTextColors(text, from = 2, to = 5)
    // highlight-getTextColors-range

    // highlight-getTextFontWeights
    val fontWeights = engine.block.getTextFontWeights(text)
    // highlight-getTextFontWeights

    // highlight-getTextFontStyles
    val fontStyles = engine.block.getTextFontStyles(text)
    // highlight-getTextFontStyles

    engine.stop()
}
