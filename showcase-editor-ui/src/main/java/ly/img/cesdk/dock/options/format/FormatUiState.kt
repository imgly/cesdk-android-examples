package ly.img.cesdk.dock.options.format

import androidx.annotation.StringRes
import ly.img.cesdk.core.data.font.FontData
import ly.img.cesdk.core.data.font.FontFamilyData
import ly.img.cesdk.engine.Scope
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class FormatUiState(
    val fontFamily: String,
    val isBold: Boolean?,
    val isItalic: Boolean?,
    val horizontalAlignment: HorizontalAlignment,
    val verticalAlignment: VerticalAlignment,
    val fontSize: Float,
    val letterSpacing: Float,
    val lineHeight: Float,
    @StringRes val sizeModeRes: Int,
    val fontFamilies: List<FontFamilyData>,
    val isArrangeResizeAllowed: Boolean
)

internal fun createFormatUiState(
    designBlock: DesignBlock,
    engine: Engine,
    families: Map<String, FontFamilyData>,
): FormatUiState {
    val fontPath = engine.block.getString(designBlock, "text/fontFileUri")

    var fontFamilyData: FontFamilyData? = null
    var fontData: FontData? = null

    for (value in families.values) {
        val data = value.getFontData(fontPath) ?: continue
        fontFamilyData = value
        fontData = data
        break
    }

    requireNotNull(fontFamilyData)
    requireNotNull(fontData)

    var isBold: Boolean? = null
    var isItalic: Boolean? = null

    if (fontFamilyData.hasRegular && fontFamilyData.hasBold && fontFamilyData.hasItalic && fontFamilyData.hasBoldItalic) {
        isBold = fontData.isBold()
        isItalic = fontData.isItalic()
    } else if (fontFamilyData.hasRegular && !fontFamilyData.hasBold && fontFamilyData.hasItalic && !fontFamilyData.hasBoldItalic) {
        isItalic = fontData.isItalic()
    } else if (fontFamilyData.hasRegular && fontFamilyData.hasBold && !fontFamilyData.hasItalic && !fontFamilyData.hasBoldItalic) {
        isBold = fontData.isBold()
    }
    return FormatUiState(
        fontFamily = fontFamilyData.name,
        isBold = isBold,
        isItalic = isItalic,
        horizontalAlignment = HorizontalAlignment.valueOf(engine.block.getEnum(designBlock, "text/horizontalAlignment")),
        verticalAlignment = VerticalAlignment.valueOf(engine.block.getEnum(designBlock, "text/verticalAlignment")),
        fontSize = engine.block.getFloat(designBlock, "text/fontSize"),
        letterSpacing = engine.block.getFloat(designBlock, "text/letterSpacing"),
        lineHeight = engine.block.getFloat(designBlock, "text/lineHeight"),
        sizeModeRes = engine.block.getHeightMode(designBlock).getText(),
        fontFamilies = families.values.toList(),
        isArrangeResizeAllowed = engine.block.isAllowedByScope(designBlock, Scope.LayerResize)
    )
}