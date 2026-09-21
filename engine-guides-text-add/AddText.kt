import android.net.Uri
import ly.img.editor.defaultBaseUri
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.HorizontalAlignment
import ly.img.engine.SizeMode
import ly.img.engine.TextCase
import ly.img.engine.Typeface

data class AddText(
    val titleText: String,
    val richTextColors: List<Color>,
    val richTextWeights: List<FontWeight>,
    val richTextStyles: List<FontStyle>,
    val richTextFontSizes: List<Float>,
    val richTextCases: List<TextCase>,
    val richTextTypefaceCount: Int,
    val richTextTypefaceNames: List<String>,
    val autoSizeWidthMode: SizeMode,
    val autoSizeHeightMode: SizeMode,
    val caseTransform: TextCase,
    val toggleCanBold: Boolean,
    val toggleCanItalic: Boolean,
    val toggledWeights: List<FontWeight>,
    val toggledStyles: List<FontStyle>,
    val modifiedText: String,
    val horizontalAlignment: HorizontalAlignment?,
    val lineHeight: Float,
    val letterSpacing: Float,
)

fun addText(engine: Engine): AddText {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 1100F)
    engine.block.appendChild(parent = scene, child = page)

    val margin = 40F
    val fontSize = 80F
    val lineSpacing = 100F
    val pageWidth = engine.block.getWidth(page)

    // highlight-android-create-text
    val titleBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = titleBlock)
    engine.block.replaceText(titleBlock, text = "Welcome to CE.SDK")

    engine.block.setWidthMode(titleBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(titleBlock, mode = SizeMode.AUTO)
    engine.block.setPositionX(titleBlock, value = margin)
    engine.block.setPositionY(titleBlock, value = margin)
    // highlight-android-create-text

    // highlight-android-set-font
    // Use a reachable location for the CE.SDK font assets.
    val cesdkTypefaceBaseUri = defaultBaseUri
        .buildUpon()
        .appendPath("ly.img.typeface")
        .appendPath("fonts")
        .build()

    fun fontUri(
        family: String,
        fileName: String,
    ): Uri = cesdkTypefaceBaseUri
        .buildUpon()
        .appendPath(family)
        .appendPath(fileName)
        .build()

    val caveatRegular = Font(
        uri = fontUri(family = "Caveat", fileName = "Caveat-Regular.ttf"),
        subFamily = "Regular",
        weight = FontWeight.NORMAL,
        style = FontStyle.NORMAL,
    )
    val caveatBold = Font(
        uri = fontUri(family = "Caveat", fileName = "Caveat-Bold.ttf"),
        subFamily = "Bold",
        weight = FontWeight.BOLD,
        style = FontStyle.NORMAL,
    )
    val caveatTypeface = Typeface(name = "Caveat", fonts = listOf(caveatRegular, caveatBold))

    engine.block.setFont(titleBlock, fontFileUri = caveatBold.uri, typeface = caveatTypeface)
    engine.block.setTextFontSize(titleBlock, fontSize = fontSize)
    // highlight-android-set-font

    // highlight-android-font-variants
    val robotoTypeface = Typeface(
        name = "Roboto",
        fonts = listOf(
            Font(
                uri = fontUri(family = "Roboto", fileName = "Roboto-Regular.ttf"),
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
            Font(
                uri = fontUri(family = "Roboto", fileName = "Roboto-Bold.ttf"),
                subFamily = "Bold",
                weight = FontWeight.BOLD,
                style = FontStyle.NORMAL,
            ),
            Font(
                uri = fontUri(family = "Roboto", fileName = "Roboto-Italic.ttf"),
                subFamily = "Italic",
                weight = FontWeight.NORMAL,
                style = FontStyle.ITALIC,
            ),
            Font(
                uri = fontUri(family = "Roboto", fileName = "Roboto-BoldItalic.ttf"),
                subFamily = "Bold Italic",
                weight = FontWeight.BOLD,
                style = FontStyle.ITALIC,
            ),
        ),
    )
    val robotoRegular = robotoTypeface.fonts.firstOrNull {
        it.weight == FontWeight.NORMAL && it.style == FontStyle.NORMAL
    } ?: error("Roboto regular font variant is required for text styling.")
    // highlight-android-font-variants

    val richTextBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = richTextBlock)
    engine.block.replaceText(richTextBlock, text = "Rich text with colors and styles")
    engine.block.setPositionX(richTextBlock, value = margin)
    engine.block.setPositionY(richTextBlock, value = margin + lineSpacing)
    engine.block.setWidthMode(richTextBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(richTextBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(richTextBlock, fontSize = fontSize)
    engine.block.setFont(
        block = richTextBlock,
        fontFileUri = robotoRegular.uri,
        typeface = robotoTypeface,
    )

    // highlight-android-rich-text-styling
    // "Rich" (UTF-16 range [0, 4)) in blue.
    engine.block.setTextColor(
        block = richTextBlock,
        color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 1F),
        from = 0,
        to = 4,
    )
    // "text" (UTF-16 range [5, 9)) in bold.
    engine.block.setTextFontWeight(
        block = richTextBlock,
        fontWeight = FontWeight.BOLD,
        from = 5,
        to = 9,
    )
    // "with" (UTF-16 range [10, 14)) in italic.
    engine.block.setTextFontStyle(
        block = richTextBlock,
        fontStyle = FontStyle.ITALIC,
        from = 10,
        to = 14,
    )
    // "colors" (UTF-16 range [15, 21)) in orange and larger type.
    engine.block.setTextColor(
        block = richTextBlock,
        color = Color.fromRGBA(r = 0.9F, g = 0.5F, b = 0.1F, a = 1F),
        from = 15,
        to = 21,
    )
    engine.block.setTextFontSize(
        block = richTextBlock,
        fontSize = 96F,
        from = 15,
        to = 21,
    )
    // "and" (UTF-16 range [22, 25)) uses another typeface.
    engine.block.setTypeface(
        block = richTextBlock,
        typeface = caveatTypeface,
        from = 22,
        to = 25,
    )
    // "styles" (UTF-16 range [26, 32)) in green uppercase.
    engine.block.setTextColor(
        block = richTextBlock,
        color = Color.fromRGBA(r = 0.2F, g = 0.7F, b = 0.3F, a = 1F),
        from = 26,
        to = 32,
    )
    engine.block.setTextCase(
        block = richTextBlock,
        textCase = TextCase.UPPER_CASE,
        from = 26,
        to = 32,
    )
    // highlight-android-rich-text-styling

    // highlight-android-auto-sizing
    val autoSizeBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = autoSizeBlock)
    engine.block.replaceText(autoSizeBlock, text = "Auto-sizing text block")
    engine.block.setPositionX(autoSizeBlock, value = margin)
    engine.block.setPositionY(autoSizeBlock, value = margin + lineSpacing * 2)

    engine.block.setWidth(autoSizeBlock, value = pageWidth - margin * 2)
    engine.block.setWidthMode(autoSizeBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(autoSizeBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(autoSizeBlock, fontSize = fontSize)
    // highlight-android-auto-sizing

    // highlight-android-text-case
    val caseBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = caseBlock)
    engine.block.replaceText(caseBlock, text = "uppercase text")
    engine.block.setPositionX(caseBlock, value = margin)
    engine.block.setPositionY(caseBlock, value = margin + lineSpacing * 3)
    engine.block.setWidthMode(caseBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(caseBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(caseBlock, fontSize = fontSize)

    engine.block.setTextCase(caseBlock, textCase = TextCase.UPPER_CASE)
    // highlight-android-text-case

    // highlight-android-text-alignment
    val alignedBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = alignedBlock)
    engine.block.replaceText(alignedBlock, text = "Centered Text\nWith Line Spacing")
    engine.block.setPositionX(alignedBlock, value = margin)
    engine.block.setPositionY(alignedBlock, value = margin + lineSpacing * 4)
    engine.block.setWidth(alignedBlock, value = pageWidth - margin * 2)
    engine.block.setWidthMode(alignedBlock, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(alignedBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(alignedBlock, fontSize = fontSize)

    engine.block.setTextHorizontalAlignment(alignedBlock, alignment = HorizontalAlignment.Center)
    engine.block.setTextLineHeight(alignedBlock, lineHeight = 1.5F)
    engine.block.setFloat(alignedBlock, property = "text/letterSpacing", value = 0.05F)

    val lineHeight = engine.block.getTextLineHeight(alignedBlock, paragraphIndex = 0)
    val letterSpacing = engine.block.getFloat(alignedBlock, property = "text/letterSpacing")
    // highlight-android-text-alignment

    val toggleBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = toggleBlock)
    engine.block.replaceText(toggleBlock, text = "Toggle Bold and Italic")
    engine.block.setPositionX(toggleBlock, value = margin)
    engine.block.setPositionY(toggleBlock, value = margin + lineSpacing * 7)
    engine.block.setWidthMode(toggleBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(toggleBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(toggleBlock, fontSize = fontSize)
    engine.block.setFont(toggleBlock, fontFileUri = robotoRegular.uri, typeface = robotoTypeface)

    // highlight-android-toggle-bold-italic
    val canToggleBold = engine.block.canToggleBoldFont(toggleBlock, from = 7, to = 11)
    if (canToggleBold) {
        engine.block.toggleBoldFont(toggleBlock, from = 7, to = 11)
    }

    val canToggleItalic = engine.block.canToggleItalicFont(toggleBlock, from = 16, to = 22)
    if (canToggleItalic) {
        engine.block.toggleItalicFont(toggleBlock, from = 16, to = 22)
    }
    // highlight-android-toggle-bold-italic

    // highlight-android-modify-text
    val modifyBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = modifyBlock)
    engine.block.replaceText(modifyBlock, text = "Hello World")
    engine.block.setPositionX(modifyBlock, value = margin)
    engine.block.setPositionY(modifyBlock, value = margin + lineSpacing * 8)
    engine.block.setWidthMode(modifyBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(modifyBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(modifyBlock, fontSize = fontSize)

    engine.block.replaceText(modifyBlock, text = "CE.SDK", from = 6, to = 11)
    // highlight-android-modify-text

    val richTextTypefaces = engine.block.getTypefaces(richTextBlock, from = 22, to = 25)

    return AddText(
        titleText = engine.block.getString(titleBlock, property = "text/text"),
        richTextColors = listOf(
            engine.block.getTextColors(richTextBlock, from = 0, to = 4).single(),
            engine.block.getTextColors(richTextBlock, from = 15, to = 21).single(),
            engine.block.getTextColors(richTextBlock, from = 26, to = 32).single(),
        ),
        richTextWeights = engine.block.getTextFontWeights(richTextBlock, from = 5, to = 9),
        richTextStyles = engine.block.getTextFontStyles(richTextBlock, from = 10, to = 14),
        richTextFontSizes = engine.block.getTextFontSizes(richTextBlock, from = 15, to = 21),
        richTextCases = engine.block.getTextCases(richTextBlock, from = 26, to = 32),
        richTextTypefaceCount = richTextTypefaces.size,
        richTextTypefaceNames = richTextTypefaces.map { it.name },
        autoSizeWidthMode = engine.block.getWidthMode(autoSizeBlock),
        autoSizeHeightMode = engine.block.getHeightMode(autoSizeBlock),
        caseTransform = engine.block.getTextCases(caseBlock).first(),
        toggleCanBold = canToggleBold,
        toggleCanItalic = canToggleItalic,
        toggledWeights = engine.block.getTextFontWeights(toggleBlock, from = 7, to = 11),
        toggledStyles = engine.block.getTextFontStyles(toggleBlock, from = 16, to = 22),
        modifiedText = engine.block.getString(modifyBlock, property = "text/text"),
        horizontalAlignment = engine.block.getTextHorizontalAlignment(alignedBlock),
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
    )
}
