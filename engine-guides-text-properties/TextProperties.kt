import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.AnimationType
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.SizeMode
import ly.img.engine.TextCase
import ly.img.engine.Typeface

fun textProperties(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

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

    // highlight-backgroundColor-enabled
    engine.block.setBoolean(text, property = "backgroundColor/enabled", value = true)

    // highlight-backgroundColor-get-set
    val color = engine.block.getColor(text, property = "backgroundColor/color")
    engine.block.setColor(text, property = "backgroundColor/color", value = Color.fromRGBA(r = 0, g = 0, b = 1, a = 1))

    // highlight-backgroundColor-padding
    engine.block.setFloat(text, property = "backgroundColor/paddingLeft", value = 1.0F)
    engine.block.setFloat(text, property = "backgroundColor/paddingTop", value = 2.0F)
    engine.block.setFloat(text, property = "backgroundColor/paddingRight", value = 3.0F)
    engine.block.setFloat(text, property = "backgroundColor/paddingBottom", value = 4.0F)

    // highlight-backgroundColor-cornerRadius
    engine.block.setFloat(text, property = "backgroundColor/cornerRadius", value = 4.0F)

    // highlight-backgroundColor-animation
    val animation = engine.block.createAnimation(AnimationType.Slide)
    engine.block.setEnum(animation, property = "textAnimationWritingStyle", value = "Block")

    engine.block.setInAnimation(text, animation = animation)
    engine.block.setOutAnimation(text, animation = animation)

    // highlight-setTextCase
    engine.block.setTextCase(text, textCase = TextCase.TITLE_CASE)
    // highlight-setTextCase

    // highlight-getTextCases
    val textCases = engine.block.getTextCases(text)
    // highlight-getTextCases

    // highlight-setFont
    val typeface = Typeface(
        name = "Roboto",
        fonts = listOf(
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

    // highlight-setTextFontStyle
    engine.block.setTextFontStyle(text, FontStyle.NORMAL)
    // highlight-setTextFontStyle

    // highlight-getTextFontStyles
    val fontStyles = engine.block.getTextFontStyles(text)
    // highlight-getTextFontStyles

    engine.stop()
}
