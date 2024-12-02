package ly.img.editor.base.dock

import androidx.compose.runtime.Composable
import ly.img.editor.base.dock.options.adjustment.AdjustmentUiState
import ly.img.editor.base.dock.options.effect.EffectUiState
import ly.img.editor.base.dock.options.fillstroke.FillStrokeUiState
import ly.img.editor.base.dock.options.format.FormatUiState
import ly.img.editor.base.dock.options.layer.LayerUiState
import ly.img.editor.base.dock.options.shapeoptions.ShapeOptionsUiState
import ly.img.editor.core.EditorScope
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlock

interface BottomSheetContent {
    val type: SheetType

    val isFloating: Boolean
        get() = type.style.isFloating

    val isHalfExpandingEnabled: Boolean
        get() = type.style.isHalfExpandingEnabled

    val isInitialExpandHalf: Boolean
        get() = type.style.isHalfExpandedInitially
}

class CustomBottomSheetContent(
    override val type: SheetType,
    val content: @Composable EditorScope.() -> Unit,
) : BottomSheetContent

class LibraryTabsBottomSheetContent(
    override val type: SheetType,
) : BottomSheetContent

class LibraryAddBottomSheetContent(
    override val type: SheetType,
    val libraryCategory: LibraryCategory,
    val addToBackgroundTrack: Boolean,
) : BottomSheetContent

class LibraryReplaceBottomSheetContent(
    override val type: SheetType,
    val libraryCategory: LibraryCategory,
    val designBlock: DesignBlock,
) : BottomSheetContent

class LayerBottomSheetContent(
    override val type: SheetType,
    val uiState: LayerUiState,
) : BottomSheetContent

class OptionsBottomSheetContent(
    override val type: SheetType,
    val uiState: ShapeOptionsUiState,
) : BottomSheetContent

class FillStrokeBottomSheetContent(
    override val type: SheetType,
    val uiState: FillStrokeUiState,
) : BottomSheetContent

class FormatBottomSheetContent(
    override val type: SheetType,
    val uiState: FormatUiState,
) : BottomSheetContent

class AdjustmentSheetContent(
    override val type: SheetType,
    val uiState: AdjustmentUiState,
) : BottomSheetContent

class EffectSheetContent(
    override val type: SheetType,
    val uiState: EffectUiState,
) : BottomSheetContent
