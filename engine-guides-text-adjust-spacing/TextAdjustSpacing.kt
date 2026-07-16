import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.SizeMode

data class TextAdjustSpacing(
    val letterSpacing: Float,
    val lineHeight: Float,
    val paragraph0LineHeight: Float,
    val paragraph1LineHeight: Float,
    val resetParagraph0LineHeight: Float,
    val resetBlockLineHeight: Float,
    val paragraphSpacing: Float,
)

fun textAdjustSpacing(engine: Engine): TextAdjustSpacing {
    val scene = engine.scene.create()
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = scene, child = text)
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)
    engine.block.replaceText(text, text = "Hello\nWorld\nCE.SDK")

    // highlight-android-letter-spacing
    engine.block.setFloat(text, property = "text/letterSpacing", value = 0.1F)
    val letterSpacing = engine.block.getFloat(text, property = "text/letterSpacing")
    // highlight-android-letter-spacing

    // highlight-android-line-height
    engine.block.setFloat(text, property = "text/lineHeight", value = 1.5F)
    val lineHeight = engine.block.getFloat(text, property = "text/lineHeight")
    // highlight-android-line-height

    // highlight-android-paragraph-line-height
    engine.block.setTextLineHeight(text, lineHeight = 2.0F, paragraphIndex = 0)
    val paragraph0LineHeight = engine.block.getTextLineHeight(text, paragraphIndex = 0)
    val paragraph1LineHeight = engine.block.getTextLineHeight(text, paragraphIndex = 1)

    engine.block.setTextLineHeight(text, lineHeight = null, paragraphIndex = 0)
    val resetParagraph0LineHeight = engine.block.getTextLineHeight(text, paragraphIndex = 0)

    engine.block.setTextLineHeight(text, lineHeight = 1.8F)
    val resetBlockLineHeight = engine.block.getTextLineHeight(text, paragraphIndex = 1)
    // highlight-android-paragraph-line-height

    // highlight-android-paragraph-spacing
    engine.block.setFloat(text, property = "text/paragraphSpacing", value = 1.2F)
    val paragraphSpacing = engine.block.getFloat(text, property = "text/paragraphSpacing")
    // highlight-android-paragraph-spacing

    check(letterSpacing == 0.1F)
    check(lineHeight == 1.5F)
    check(paragraph0LineHeight == 2.0F)
    check(paragraph1LineHeight == 1.5F)
    check(resetParagraph0LineHeight == 1.5F)
    check(resetBlockLineHeight == 1.8F)
    check(paragraphSpacing == 1.2F)

    return TextAdjustSpacing(
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        paragraph0LineHeight = paragraph0LineHeight,
        paragraph1LineHeight = paragraph1LineHeight,
        resetParagraph0LineHeight = resetParagraph0LineHeight,
        resetBlockLineHeight = resetBlockLineHeight,
        paragraphSpacing = paragraphSpacing,
    )
}
