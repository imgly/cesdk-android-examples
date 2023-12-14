package ly.img.cesdk.dock

import ly.img.cesdk.core.engine.BlockType
import ly.img.cesdk.dock.options.adjustment.AdjustmentUiState
import ly.img.cesdk.dock.options.fillstroke.FillStrokeUiState
import ly.img.cesdk.dock.options.effect.EffectUiState
import ly.img.cesdk.dock.options.format.FormatUiState
import ly.img.cesdk.dock.options.layer.LayerUiState
import ly.img.cesdk.dock.options.shapeoptions.ShapeOptionsUiState
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