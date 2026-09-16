import ly.img.editor.defaultBaseUri
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.MimeType
import ly.img.engine.SizeMode
import ly.img.engine.TextCase
import java.nio.ByteBuffer

suspend fun editText(engine: Engine): ByteBuffer {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-text
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = text)
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)
    engine.block.setPositionX(text, value = 80F)
    engine.block.setPositionY(text, value = 120F)
    // highlight-android-create-text

    val typefaceSourceId = "ly.img.typeface"
    if (!engine.asset.findAllSources().contains(typefaceSourceId)) {
        engine.asset.addLocalSourceFromJSON(
            contentUri = defaultBaseUri.buildUpon()
                .appendPath(typefaceSourceId)
                .appendPath("content.json")
                .build(),
        )
    }

    // highlight-android-set-typeface
    val typeface = engine.asset.findAssets(
        sourceId = "ly.img.typeface",
        query = FindAssetsQuery(query = "", page = 0, perPage = 100),
    ).assets
        .mapNotNull { asset -> asset.payload.typeface }
        .firstOrNull { candidate ->
            val fonts = candidate.fonts
            fonts.any { font -> font.weight == FontWeight.NORMAL && font.style == FontStyle.NORMAL } &&
                fonts.any { font -> font.weight == FontWeight.BOLD && font.style == FontStyle.NORMAL } &&
                fonts.any { font -> font.weight == FontWeight.NORMAL && font.style == FontStyle.ITALIC } &&
                fonts.any { font -> font.weight == FontWeight.BOLD && font.style == FontStyle.ITALIC }
        }
        ?: error("Typeface with regular, bold, italic, and bold italic fonts not found")
    val regularFont = typeface.fonts.first { font ->
        font.weight == FontWeight.NORMAL && font.style == FontStyle.NORMAL
    }
    engine.block.setFont(block = text, fontFileUri = regularFont.uri, typeface = typeface)
    engine.block.setTypeface(block = text, typeface = typeface)
    val blockTypeface = engine.block.getTypeface(text)
    // highlight-android-set-typeface

    // highlight-android-replace-remove-text
    engine.block.replaceText(text, text = "Winter Sale")
    engine.block.replaceText(text, text = " Design", from = 6, to = 6)
    engine.block.replaceText(text, text = "Campaign", from = 14, to = 18)
    engine.block.removeText(text, from = 0, to = 7)
    // highlight-android-replace-remove-text

    // highlight-android-format-ranges
    val campaignStart = 7
    val campaignEnd = 15
    engine.block.setTextColor(
        block = text,
        color = Color.fromRGBA(r = 0.1F, g = 0.32F, b = 0.78F, a = 1F),
        from = campaignStart,
        to = campaignEnd,
    )
    engine.block.setTextFontWeight(text, fontWeight = FontWeight.BOLD, from = campaignStart, to = campaignEnd)
    engine.block.setTextFontStyle(text, fontStyle = FontStyle.ITALIC, from = campaignStart, to = campaignEnd)
    engine.block.setTextFontSize(text, fontSize = 24F)
    engine.block.setTextFontSize(text, fontSize = 32F, from = campaignStart, to = campaignEnd)
    engine.block.setTextCase(text, textCase = TextCase.TITLE_CASE)
    // highlight-android-format-ranges

    // highlight-android-toggle-formatting
    val designEnd = 6
    if (engine.block.canToggleBoldFont(text, from = 0, to = designEnd)) {
        engine.block.toggleBoldFont(text, from = 0, to = designEnd)
    }
    if (engine.block.canToggleItalicFont(text, from = 0, to = designEnd)) {
        engine.block.toggleItalicFont(text, from = 0, to = designEnd)
    }
    // highlight-android-toggle-formatting

    // highlight-android-query-formatting
    val colors = engine.block.getTextColors(text)
    val fontWeights = engine.block.getTextFontWeights(text)
    val fontStyles = engine.block.getTextFontStyles(text)
    val fontSizes = engine.block.getTextFontSizes(text)
    val textCases = engine.block.getTextCases(text)
    val typefaces = engine.block.getTypefaces(text)
    // highlight-android-query-formatting

    // highlight-android-line-info
    engine.block.forceLoadResources(blocks = listOf(text))
    val lineCount = engine.block.getTextVisibleLineCount(text)
    val firstLine = engine.block.getTextVisibleLineContent(text, lineIndex = 0)
    val firstLineBounds = engine.block.getTextLineBoundingBoxRect(text, index = 0)
    // highlight-android-line-info

    // highlight-android-font-metrics
    val fontMetrics = engine.editor.getFontMetrics(fontFileUri = regularFont.uri.toString())
    // highlight-android-font-metrics

    check(colors.isNotEmpty())
    check(fontWeights.contains(FontWeight.BOLD))
    check(fontStyles.contains(FontStyle.ITALIC))
    check(fontSizes.contains(24F) && fontSizes.contains(32F))
    check(textCases == listOf(TextCase.TITLE_CASE))
    check(blockTypeface.name == typeface.name)
    check(typefaces.isNotEmpty())
    check(lineCount > 0)
    check(firstLine.isNotBlank())
    check(firstLineBounds.width() > 0F)
    check(fontMetrics.unitsPerEm > 0F)

    return engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 1280F, targetHeight = 720F),
    )
}
