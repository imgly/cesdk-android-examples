// highlight-textEnumerations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ListStyle
import ly.img.engine.SizeMode

fun textEnumerations(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-textEnumerations-setup
    val scene = engine.scene.create()
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = scene, child = text)
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)
    engine.block.replaceText(text, text = "First item\nSecond item\nThird item")
    // highlight-textEnumerations-setup

    // highlight-textEnumerations-applyListStyles
    // Apply ordered list style to all paragraphs (paragraphIndex defaults to -1 = all)
    engine.block.setTextListStyle(text, listStyle = ListStyle.ORDERED)

    // Override the third paragraph (index 2) to unordered
    engine.block.setTextListStyle(text, listStyle = ListStyle.UNORDERED, paragraphIndex = 2)
    // highlight-textEnumerations-applyListStyles

    // highlight-textEnumerations-manageNesting
    // Set the second paragraph (index 1) to nesting level 1 (one indent deep)
    engine.block.setTextListLevel(text, listLevel = 1, paragraphIndex = 1)

    // Read back the nesting level to confirm
    val level = engine.block.getTextListLevel(text, paragraphIndex = 1)
    println("Second paragraph nesting level: $level")
    // highlight-textEnumerations-manageNesting

    // highlight-textEnumerations-atomic
    // Atomically set both list style and nesting level in one call
    // Sets paragraph 0 to ordered style at nesting level 0 (outermost)
    engine.block.setTextListStyle(
        text,
        listStyle = ListStyle.ORDERED,
        paragraphIndex = 0,
        listLevel = 0,
    )
    // highlight-textEnumerations-atomic

    // highlight-textEnumerations-paragraphIndices
    // Get all paragraph indices in the text block
    val allIndices = engine.block.getTextParagraphIndices(text)
    println("All paragraph indices: $allIndices")

    // Get indices overlapping a specific grapheme range
    val rangeIndices = engine.block.getTextParagraphIndices(text, from = 0, to = 10)
    println("Indices for range [0, 10): $rangeIndices")
    // highlight-textEnumerations-paragraphIndices

    // highlight-textEnumerations-queryListStyles
    // Read back the list style and nesting level for each paragraph
    val styles = allIndices.map { index -> engine.block.getTextListStyle(text, paragraphIndex = index) }
    val levels = allIndices.map { index -> engine.block.getTextListLevel(text, paragraphIndex = index) }
    println("Paragraph styles: $styles")
    println("Paragraph levels: $levels")
    // highlight-textEnumerations-queryListStyles

    engine.stop()
}

// highlight-textEnumerations
