import android.net.Uri
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

suspend fun textProperties(engine: Engine) {
    val scene = engine.scene.create()
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = scene, child = text)
    engine.block.setWidthMode(text, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(text, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(text, value = 520F)
    engine.block.setHeight(text, value = 220F)
    // Use a large font size so the styling changes are clearly visible.
    engine.block.setTextFontSize(text, fontSize = 160F)

    // highlight-android-edit-text
    engine.block.replaceText(text, text = "Hello World")

    // Insert a "!" at the UTF-16 offset after "Hello World".
    engine.block.replaceText(text, text = "!", from = 11)

    // Replace "World" with "Alex".
    engine.block.replaceText(text, text = "Alex", from = 6, to = 11)

    // Remove "Hello ".
    engine.block.removeText(text, from = 0, to = 6)
    // highlight-android-edit-text

    engine.scene.zoomToBlock(
        block = text,
        paddingLeft = 100F,
        paddingTop = 100F,
        paddingRight = 100F,
        paddingBottom = 100F,
    )

    // highlight-android-text-colors
    val yellow = Color.fromRGBA(r = 255, g = 230, b = 0)
    val black = Color.fromRGBA(r = 0, g = 0, b = 0)

    engine.block.setTextColor(text, color = yellow)
    engine.block.setTextColor(text, color = black, from = 1, to = 4)

    val allColors = engine.block.getTextColors(text)
    val colorsInRange = engine.block.getTextColors(text, from = 2, to = 5)
    // highlight-android-text-colors

    // highlight-android-text-background
    if (engine.block.supportsBackgroundColor(text)) {
        engine.block.setBackgroundColorEnabled(text, enabled = true)
        val backgroundColorEnabled = engine.block.isBackgroundColorEnabled(text)

        val backgroundColor = engine.block.getBackgroundColor(text)
        engine.block.setBackgroundColor(text, color = Color.fromRGBA(r = 242, g = 242, b = 242))

        engine.block.setFloat(text, property = "backgroundColor/paddingLeft", value = 10F)
        engine.block.setFloat(text, property = "backgroundColor/paddingRight", value = 10F)
        engine.block.setFloat(text, property = "backgroundColor/paddingTop", value = 8F)
        engine.block.setFloat(text, property = "backgroundColor/paddingBottom", value = 8F)
        engine.block.setFloat(text, property = "backgroundColor/cornerRadius", value = 8F)
    }
    // highlight-android-text-background

    // highlight-android-background-animation
    val animation = engine.block.createAnimation(AnimationType.Slide)
    engine.block.setEnum(animation, property = "textAnimationWritingStyle", value = "Block")
    engine.block.setInAnimation(text, animation = animation)
    engine.block.setOutAnimation(text, animation = animation)
    // highlight-android-background-animation

    // highlight-android-text-case
    engine.block.setTextCase(text, textCase = TextCase.TITLE_CASE)

    val textCases = engine.block.getTextCases(text)
    // highlight-android-text-case

    // highlight-android-typeface
    val bundledAssetsBaseUri = "file:///android_asset/imgly-assets"
    val typeface = Typeface(
        name = "Fira Sans",
        fonts = listOf(
            Font(
                uri = Uri.parse(
                    "$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-Regular.ttf",
                ),
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
            Font(
                uri = Uri.parse(
                    "$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-Bold.ttf",
                ),
                subFamily = "Bold",
                weight = FontWeight.BOLD,
                style = FontStyle.NORMAL,
            ),
            Font(
                uri = Uri.parse(
                    "$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-Italic.ttf",
                ),
                subFamily = "Italic",
                weight = FontWeight.NORMAL,
                style = FontStyle.ITALIC,
            ),
            Font(
                uri = Uri.parse(
                    "$bundledAssetsBaseUri/ly.img.typeface/fonts/FiraSans/FiraSans-BoldItalic.ttf",
                ),
                subFamily = "Bold Italic",
                weight = FontWeight.BOLD,
                style = FontStyle.ITALIC,
            ),
        ),
    )
    val regularFont = typeface.fonts.firstOrNull {
        it.weight == FontWeight.NORMAL && it.style == FontStyle.NORMAL
    } ?: error("Typeface must include a normal regular font.")

    engine.block.setFont(text, fontFileUri = regularFont.uri, typeface = typeface)
    engine.block.setTypeface(text, typeface = typeface, from = 1, to = 4)

    val currentDefaultTypeface = engine.block.getTypeface(text)
    val currentTypefaces = engine.block.getTypefaces(text)
    val currentTypefacesOfRange = engine.block.getTypefaces(text, from = 1, to = 4)
    // highlight-android-typeface

    // highlight-android-font-styles
    if (engine.block.canToggleBoldFont(text)) {
        engine.block.toggleBoldFont(text)
    }

    if (engine.block.canToggleItalicFont(text, from = 1, to = 4)) {
        engine.block.toggleItalicFont(text, from = 1, to = 4)
    }

    engine.block.setTextFontWeight(text, fontWeight = FontWeight.BOLD)
    val fontWeights = engine.block.getTextFontWeights(text)

    engine.block.setTextFontStyle(text, fontStyle = FontStyle.ITALIC)
    val fontStyles = engine.block.getTextFontStyles(text)
    // highlight-android-font-styles
}
