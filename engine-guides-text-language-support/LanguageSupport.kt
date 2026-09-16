import android.net.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.HorizontalAlignment
import ly.img.engine.Typeface

private object LanguageLayout {
    const val PAGE_WIDTH = 800F
    const val PAGE_HEIGHT = 1200F
    const val TEXT_X = 50F
    const val TEXT_WIDTH = 700F
    const val FONT_SIZE = 42F
    const val GAP = 24F
}

private const val GUIDE_ASSET_BASE_URI = "file:///android_asset/imgly-assets"
private const val GUIDE_FONT_BASE_URI = "$GUIDE_ASSET_BASE_URI/fonts"
private val BRAND_LABEL_FONT_URI = Uri.parse("$GUIDE_FONT_BASE_URI/font-10.ttf")
private val ARABIC_SUPPORT_FONT_URI = Uri.parse("$GUIDE_FONT_BASE_URI/font-6.ttf")
private val KOREAN_SUPPORT_FONT_URI = Uri.parse("$GUIDE_FONT_BASE_URI/font-30.ttf")

// highlight-android-configure-fonts
private data class MultilingualTypefaces(
    val label: Typeface,
    val rtl: Typeface,
    val korean: Typeface,
)

private fun multilingualTypefaces() = MultilingualTypefaces(
    label = Typeface(
        name = "BrandLabel",
        fonts = listOf(
            Font(
                uri = BRAND_LABEL_FONT_URI,
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
        ),
    ),
    rtl = Typeface(
        name = "ArabicSupport",
        fonts = listOf(
            Font(
                uri = ARABIC_SUPPORT_FONT_URI,
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
        ),
    ),
    korean = Typeface(
        name = "KoreanSupport",
        fonts = listOf(
            Font(
                uri = KOREAN_SUPPORT_FONT_URI,
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
        ),
    ),
)
// highlight-android-configure-fonts

// highlight-android-helper-function
private data class LanguageTextElement(
    val text: String,
    val typeface: Typeface,
    val fontWeight: FontWeight,
    val fontStyle: FontStyle = FontStyle.NORMAL,
    val fontSubFamily: String? = null,
    val height: Float = 140F,
    val alignment: HorizontalAlignment = HorizontalAlignment.Auto,
)

private fun createTextBlock(
    engine: Engine,
    page: DesignBlock,
    element: LanguageTextElement,
    yPosition: Float,
): DesignBlock {
    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = textBlock, text = element.text)
    engine.block.setPositionX(textBlock, value = LanguageLayout.TEXT_X)
    engine.block.setPositionY(textBlock, value = yPosition)
    engine.block.setWidth(textBlock, value = LanguageLayout.TEXT_WIDTH)
    engine.block.setHeight(textBlock, value = element.height)
    engine.block.setTextFontSize(textBlock, fontSize = LanguageLayout.FONT_SIZE)
    val font = element.typeface.fonts.firstOrNull { font ->
        font.weight == element.fontWeight &&
            font.style == element.fontStyle &&
            (element.fontSubFamily == null || font.subFamily == element.fontSubFamily)
    } ?: error(
        "Missing ${element.fontWeight}/${element.fontStyle}" +
            " font variant for ${element.typeface.name}",
    )
    engine.block.setFont(
        block = textBlock,
        fontFileUri = font.uri,
        typeface = element.typeface,
    )
    engine.block.setTextHorizontalAlignment(block = textBlock, alignment = element.alignment)
    engine.block.appendChild(parent = page, child = textBlock)
    return textBlock
}
// highlight-android-helper-function

private suspend fun waitForResolvedRtlAlignment(
    engine: Engine,
    block: DesignBlock,
    initialAlignment: HorizontalAlignment,
): HorizontalAlignment {
    var effectiveAlignment = initialAlignment
    var layoutPasses = 0
    while (effectiveAlignment != HorizontalAlignment.Right && layoutPasses < 10) {
        // Offscreen text layout may resolve on the next frame after fonts load.
        yield()
        delay(16)
        effectiveAlignment = engine.block.getTextEffectiveHorizontalAlignment(block)
        layoutPasses += 1
    }
    return effectiveAlignment
}

suspend fun textLanguageSupport(engine: Engine): LanguageSupportResult {
    val typefaces = multilingualTypefaces()

    // highlight-android-create-scene
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = LanguageLayout.PAGE_WIDTH)
    engine.block.setHeight(page, value = LanguageLayout.PAGE_HEIGHT)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-create-scene

    // highlight-android-apply-fonts
    val textElements = listOf(
        LanguageTextElement(
            text = "RTL Arabic",
            typeface = typefaces.label,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
        ),
        LanguageTextElement(
            text = "هذا مثال.",
            typeface = typefaces.rtl,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
            height = 160F,
        ),
        LanguageTextElement(
            text = "Korean",
            typeface = typefaces.label,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
        ),
        LanguageTextElement(
            text = "이는 한 예입니다.",
            typeface = typefaces.korean,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
        ),
    )

    val textBlocks = mutableListOf<DesignBlock>()
    var currentY = 50F
    for (element in textElements) {
        textBlocks.add(createTextBlock(engine, page, element, currentY))
        currentY += element.height + LanguageLayout.GAP
    }
    // highlight-android-apply-fonts

    val rtlBlock = textBlocks[1]

    // highlight-android-rtl-alignment
    engine.block.setTextHorizontalAlignment(
        block = rtlBlock,
        alignment = HorizontalAlignment.Auto,
    )
    engine.block.forceLoadResources(listOf(rtlBlock))
    val effectiveRtlAlignment = engine.block.getTextEffectiveHorizontalAlignment(rtlBlock)
    // highlight-android-rtl-alignment

    val verifiedEffectiveRtlAlignment = waitForResolvedRtlAlignment(
        engine = engine,
        block = rtlBlock,
        initialAlignment = effectiveRtlAlignment,
    )

    // highlight-android-range-typeface
    val mixedText = "Campaign: مرحبا"
    val mixedBlock = createTextBlock(
        engine = engine,
        page = page,
        element = LanguageTextElement(
            text = mixedText,
            typeface = typefaces.label,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
        ),
        yPosition = currentY,
    )
    val rtlStart = mixedText.indexOf("مرحبا")
    engine.block.setTypeface(
        block = mixedBlock,
        typeface = typefaces.rtl,
        from = rtlStart,
        to = mixedText.length,
    )
    val mixedTypefaces = engine.block.getTypefaces(mixedBlock)
    // highlight-android-range-typeface

    currentY += 140F + LanguageLayout.GAP

    // highlight-android-paragraph-alignment
    val paragraphBlock = createTextBlock(
        engine = engine,
        page = page,
        element = LanguageTextElement(
            text = "English headline\nمرحبا بالعالم",
            typeface = typefaces.rtl,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
            height = 180F,
        ),
        yPosition = currentY,
    )
    val paragraphIndices = engine.block.getTextParagraphIndices(paragraphBlock)
    val rtlParagraphIndex = paragraphIndices.last()

    engine.block.setTextHorizontalAlignment(
        block = paragraphBlock,
        alignment = HorizontalAlignment.Right,
        paragraphIndex = rtlParagraphIndex,
    )
    val paragraphOverride = engine.block.getTextHorizontalAlignment(
        block = paragraphBlock,
        paragraphIndex = rtlParagraphIndex,
    )

    engine.block.setTextHorizontalAlignment(
        block = paragraphBlock,
        alignment = null,
        paragraphIndex = rtlParagraphIndex,
    )
    val clearedParagraphOverride = engine.block.getTextHorizontalAlignment(
        block = paragraphBlock,
        paragraphIndex = rtlParagraphIndex,
    )
    // highlight-android-paragraph-alignment

    currentY += 180F + LanguageLayout.GAP

    // highlight-android-variables
    engine.variable.set(key = "localized_headline", value = "عنوان الحملة")
    createTextBlock(
        engine = engine,
        page = page,
        element = LanguageTextElement(
            text = "{{localized_headline}}",
            typeface = typefaces.rtl,
            fontWeight = FontWeight.NORMAL,
            fontSubFamily = "Regular",
            height = 120F,
        ),
        yPosition = currentY,
    )
    val localizedHeadline = engine.variable.get(key = "localized_headline")
    // highlight-android-variables

    return LanguageSupportResult(
        textValues = textBlocks.map { block ->
            engine.block.getString(block = block, property = "text/text")
        },
        effectiveRtlAlignment = verifiedEffectiveRtlAlignment,
        paragraphIndices = paragraphIndices,
        paragraphOverride = paragraphOverride,
        clearedParagraphOverride = clearedParagraphOverride,
        mixedTypefaceNames = mixedTypefaces.map { it.name },
        localizedHeadline = localizedHeadline,
    )
}
