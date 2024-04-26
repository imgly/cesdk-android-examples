package ly.img.editor.base.dock.options.format

import androidx.annotation.StringRes
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.library.TypefaceLibraryCategory
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight

data class FormatUiState(
    val libraryCategory: LibraryCategory,
    val fontFamily: String,
    val canToggleBold: Boolean,
    val canToggleItalic: Boolean,
    val isBold: Boolean,
    val isItalic: Boolean,
    val horizontalAlignment: HorizontalAlignment,
    val verticalAlignment: VerticalAlignment,
    val fontSize: Float,
    val letterSpacing: Float,
    val lineHeight: Float,
    @StringRes val sizeModeRes: Int,
    val isArrangeResizeAllowed: Boolean,
)

internal fun createFormatUiState(
    designBlock: DesignBlock,
    engine: Engine,
): FormatUiState {
    val typeface = runCatching { engine.block.getTypeface(designBlock) }.getOrNull()
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
        sizeModeRes = engine.block.getHeightMode(designBlock).getText(),
        isArrangeResizeAllowed = engine.block.isAllowedByScope(designBlock, Scope.LayerResize),
    )
}
