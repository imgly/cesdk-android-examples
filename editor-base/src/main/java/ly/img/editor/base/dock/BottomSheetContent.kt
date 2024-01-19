package ly.img.editor.base.dock

import ly.img.editor.base.dock.options.adjustment.AdjustmentUiState
import ly.img.editor.base.dock.options.effect.EffectUiState
import ly.img.editor.base.dock.options.fillstroke.FillStrokeUiState
import ly.img.editor.base.dock.options.format.FormatUiState
import ly.img.editor.base.dock.options.layer.LayerUiState
import ly.img.editor.base.dock.options.shapeoptions.ShapeOptionsUiState
import ly.img.editor.core.ui.engine.BlockType
import ly.img.engine.DesignBlock

interface BottomSheetContent {
    fun getType(): String {
        return javaClass.name
    }
}

object LibraryBottomSheetContent : BottomSheetContent
class ReplaceBottomSheetContent(val designBlock: DesignBlock, val blockType: BlockType) : BottomSheetContent
class LayerBottomSheetContent(val uiState: LayerUiState) : BottomSheetContent
class OptionsBottomSheetContent(val uiState: ShapeOptionsUiState) : BottomSheetContent
class FillStrokeBottomSheetContent(val uiState: FillStrokeUiState) : BottomSheetContent
class FormatBottomSheetContent(val uiState: FormatUiState) : BottomSheetContent
class AdjustmentSheetContent(val uiState: AdjustmentUiState) : BottomSheetContent
class EffectSheetContent(val uiState: EffectUiState) : BottomSheetContent