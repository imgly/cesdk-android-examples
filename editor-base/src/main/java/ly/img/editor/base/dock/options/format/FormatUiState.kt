package ly.img.editor.base.dock.options.format

import androidx.annotation.StringRes
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.library.TypefaceLibraryCategory
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.SizeMode
import ly.img.engine.TextCase

data class FormatUiState(
    val libraryCategory: LibraryCategory,
    val fontFamily: String,
    val fontFamilyWeight: FontWeight,
    val fontFamilyStyle: FontStyle,
    val canToggleBold: Boolean,
    val canToggleItalic: Boolean,
    val isBold: Boolean,
    val isItalic: Boolean,
    val casing: TextCase,
    val horizontalAlignment: HorizontalAlignment,
    val verticalAlignment: VerticalAlignment,
    val fontSize: Float,
    val letterSpacing: Float,
    val paragraphSpacing: Float,
    val lineHeight: Float,
    val isClipped: Boolean,
    val hasClippingOption: Boolean,
    @StringRes val sizeModeRes: Int,
    val isArrangeResizeAllowed: Boolean,
    val availableWeights: List<FontData>,
)

internal fun createFormatUiState(
    designBlock: DesignBlock,
    engine: Engine,
): FormatUiState {
    val typeface = runCatching { engine.block.getTypeface(designBlock) }.getOrNull()
    val sizeMode = engine.block.getHeightMode(designBlock)
    return FormatUiState(
        libraryCategory = TypefaceLibraryCategory,
        fontFamily = typeface?.name ?: "Default",
        canToggleBold =
            typeface?.let {
                engine.block.canToggleBoldFont(designBlock)
            } ?: false,
        canToggleItalic =
            typeface?.let {
                engine.block.canToggleItalicFont(designBlock)
            } ?: false,
        isBold =
            typeface?.let {
                engine.block.getTextFontWeights(designBlock).contains(FontWeight.BOLD)
            } ?: false,
        isItalic =
            typeface?.let {
                engine.block.getTextFontStyles(designBlock).contains(FontStyle.ITALIC)
            } ?: false,
        horizontalAlignment =
            HorizontalAlignment.valueOf(
                engine.block.getEnum(designBlock, "text/horizontalAlignment"),
            ),
        verticalAlignment =
            VerticalAlignment.valueOf(
                engine.block.getEnum(designBlock, "text/verticalAlignment"),
            ),
        fontSize = engine.block.getFloat(designBlock, "text/fontSize"),
        letterSpacing = engine.block.getFloat(designBlock, "text/letterSpacing"),
        lineHeight = engine.block.getFloat(designBlock, "text/lineHeight"),
        sizeModeRes =
            when (sizeMode) {
                SizeMode.ABSOLUTE -> SizeModeUi.ABSOLUTE
                SizeMode.AUTO ->
                    when (engine.block.getWidthMode(designBlock)) {
                        SizeMode.AUTO -> SizeModeUi.AUTO_SIZE
                        SizeMode.ABSOLUTE -> SizeModeUi.AUTO_HEIGHT
                        SizeMode.PERCENT -> SizeModeUi.UNKNOWN
                    }

                SizeMode.PERCENT -> SizeModeUi.UNKNOWN
            }.getText(),
        hasClippingOption = sizeMode == SizeMode.ABSOLUTE,
        isClipped = engine.block.getBoolean(designBlock, "text/clipLinesOutsideOfFrame"),
        isArrangeResizeAllowed = engine.block.isAllowedByScope(designBlock, Scope.LayerResize),
        casing = engine.block.getTextCases(designBlock).firstOrNull() ?: TextCase.NORMAL,
        paragraphSpacing = engine.block.getFloat(designBlock, "text/paragraphSpacing"),
        fontFamilyWeight = engine.block.getTextFontWeights(designBlock).firstOrNull() ?: FontWeight.NORMAL,
        availableWeights =
            typeface?.fonts?.sortedBy { it.weight.value + if (it.style == FontStyle.ITALIC) 1000 else 0 }?.map {
                FontData(
                    typeface = typeface,
                    uri = it.uri,
                    weight =
                        androidx.compose.ui.text.font
                            .FontWeight(it.weight.value),
                    style = it.style,
                )
            } ?: emptyList(),
        fontFamilyStyle = engine.block.getTextFontStyles(designBlock).firstOrNull() ?: FontStyle.NORMAL,
    )
}
