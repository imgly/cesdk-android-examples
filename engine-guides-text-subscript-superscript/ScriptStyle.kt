// highlight-text-script-style
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ScriptStyle
import ly.img.engine.SizeMode

fun scriptStyle(
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
    engine.block.replaceText(text, text = "\$1999 H2O E=mc2")

    // highlight-superscript
    // Raise the cents of the price above the baseline.
    // Superscript text is scaled down and raised automatically.
    engine.block.setTextScriptStyle(text, scriptStyle = ScriptStyle.SUPERSCRIPT, from = 3, to = 5)

    // Raise the exponent of the formula
    engine.block.setTextScriptStyle(text, scriptStyle = ScriptStyle.SUPERSCRIPT, from = 14, to = 15)
    // highlight-superscript

    // highlight-subscript
    // Lower the "2" of the water formula below the baseline
    engine.block.setTextScriptStyle(text, scriptStyle = ScriptStyle.SUBSCRIPT, from = 7, to = 8)
    // highlight-subscript

    // highlight-query-scriptStyles
    // Query the unique script styles in a range, in document order
    val scriptStyles = engine.block.getTextScriptStyles(text)
    // e.g. [NORMAL, SUPERSCRIPT, SUBSCRIPT]

    // Query a specific range
    val centsScriptStyle = engine.block.getTextScriptStyles(text, from = 3, to = 5)
    // [SUPERSCRIPT]
    // highlight-query-scriptStyles

    // highlight-text-runs
    // Each text run reports its script style
    val runs = engine.block.getTextRuns(text)
    // e.g. runs[1].scriptStyle == ScriptStyle.SUPERSCRIPT
    // highlight-text-runs

    // highlight-adjust
    // Adjust the size and position of shifted text for the whole block.
    // The scale is a factor of the font size. The shift is a fraction of
    // the unscaled font size.
    engine.block.setFloat(text, property = "text/superscriptFontScale", value = 0.65F)
    engine.block.setFloat(text, property = "text/superscriptVerticalShift", value = 0.4F)
    engine.block.setFloat(text, property = "text/subscriptFontScale", value = 0.65F)
    engine.block.setFloat(text, property = "text/subscriptVerticalShift", value = 0.25F)
    // highlight-adjust

    // highlight-reset
    // Reset a range back to the normal baseline
    engine.block.setTextScriptStyle(text, scriptStyle = ScriptStyle.NORMAL, from = 14, to = 15)
    // highlight-reset

    engine.stop()
}

// highlight-text-script-style
