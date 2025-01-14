import android.net.Uri
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

    // highlight-setTextCase
    engine.block.setTextCase(text, textCase = TextCase.TITLE_CASE)
    // highlight-setTextCase

    // highlight-getTextCases
    val textCases = engine.block.getTextCases(text)
    // highlight-getTextCases

    // highlight-setFont
    val typeface =
        Typeface(
            name = "Roboto",
            fonts =
                listOf(
                    Font(
                        uri = Uri.parse("https://cdn.img.ly/assets/v2/ly.img.typeface/fonts/Roboto/Roboto-Bold.ttf"),
                        subFamily = "Bold",
                        weight = FontWeight.BOLD,
                        style = FontStyle.NORMAL,
                    ),
                    Font(
                        uri = Uri.parse("https://cdn.img.ly/assets/v2/ly.img.typeface/fonts/Roboto/Roboto-BoldItalic.ttf"),
                        subFamily = "Bold Italic",
                        weight = FontWeight.BOLD,
                        style = FontStyle.ITALIC,
                    ),
                    Font(
                        uri = Uri.parse("https://cdn.img.ly/assets/v2/ly.img.typeface/fonts/Roboto/Roboto-Italic.ttf"),
                        subFamily = "Italic",
                        weight = FontWeight.BOLD,
                        style = FontStyle.NORMAL,
                    ),
                    Font(
                        uri = Uri.parse("https://cdn.img.ly/assets/v2/ly.img.typeface/fonts/Roboto/Roboto-Regular.ttf"),
                        subFamily = "Regular",
                        weight = FontWeight.NORMAL,
                        style = FontStyle.NORMAL,
                    ),
                ),
        )
    engine.block.setFont(text, typeface.fonts[3].uri, typeface)
    // highlight-setFont

    // highlight-setTypeface
    engine.block.setTypeface(text, typeface, from = 1, to = 4)
    engine.block.setTypeface(text, typeface)
    // highlight-setTypeface

    // highlight-getTypeface
    val currentDefaultTypeface = engine.block.getTypeface(text)
    // highlight-getTypeface

    // highlight-getTypefaces
    val currentTypefaces = engine.block.getTypefaces(text)
    val currentTypefacesOfRange = engine.block.getTypefaces(text, from = 1, to = 4)
    // highlight-getTypefaces

    // highlight-toggleBold
    if (engine.block.canToggleBoldFont(text)) {
        engine.block.toggleBoldFont(text)
    }
    if (engine.block.canToggleBoldFont(text, from = 1, to = 4)) {
        engine.block.toggleBoldFont(text, from = 1, to = 4)
    }
    // highlight-toggleBold

    // highlight-toggleItalic
    if (engine.block.canToggleItalicFont(text)) {
        engine.block.toggleItalicFont(text)
    }
    if (engine.block.canToggleItalicFont(text, from = 1, to = 4)) {
        engine.block.toggleItalicFont(text, from = 1, to = 4)
    }
    // highlight-toggleItalic

    // highlight-getTextFontWeights
    val fontWeights = engine.block.getTextFontWeights(text)
    // highlight-getTextFontWeights

    // highlight-getTextFontStyles
    val fontStyles = engine.block.getTextFontStyles(text)
    // highlight-getTextFontStyles

    engine.stop()
}
