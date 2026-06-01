// highlight-text-adjust-spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.SizeMode

fun textAdjustSpacing(
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
    engine.block.replaceText(text, text = "Hello\nWorld\nCE.SDK")

    // highlight-letter-spacing
    // Set letter spacing — positive values spread characters, negative values tighten them
    engine.block.setFloat(text, property = "text/letterSpacing", value = 0.1F)

    // Read the current letter spacing
    engine.block.getFloat(text, property = "text/letterSpacing")
    // highlight-letter-spacing

    // highlight-line-height
    // Set the block-level line height multiplier — applies to all paragraphs by default
    engine.block.setFloat(text, property = "text/lineHeight", value = 1.5F)

    // Read the current block-level line height
    engine.block.getFloat(text, property = "text/lineHeight")
    // highlight-line-height

    // highlight-paragraph-line-height
    // Set a per-paragraph line height override for paragraph 0 (first paragraph)
    engine.block.setTextLineHeight(text, lineHeight = 2.0F, paragraphIndex = 0)

    // Read the line height for a specific paragraph
    // Returns the override if set, otherwise falls back to the block-level value
    engine.block.getTextLineHeight(text, paragraphIndex = 0)
    engine.block.getTextLineHeight(text, paragraphIndex = 1)

    // Clear a paragraph's override by passing null — it reverts to the block-level value
    engine.block.setTextLineHeight(text, lineHeight = null, paragraphIndex = 0)

    // Set the block-level line height and clear all paragraph overrides at once
    engine.block.setTextLineHeight(text, lineHeight = 1.8F)
    // highlight-paragraph-line-height

    // highlight-paragraph-spacing
    // Set paragraph spacing — adds space after each paragraph break
    engine.block.setFloat(text, property = "text/paragraphSpacing", value = 20F)

    // Read the current paragraph spacing
    engine.block.getFloat(text, property = "text/paragraphSpacing")
    // highlight-paragraph-spacing

    engine.stop()
}
// highlight-text-adjust-spacing
