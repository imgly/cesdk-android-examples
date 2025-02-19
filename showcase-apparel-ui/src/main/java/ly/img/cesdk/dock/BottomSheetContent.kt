package ly.img.cesdk.dock

import ly.img.cesdk.dock.options.fillstroke.FillStrokeUiState
import ly.img.cesdk.dock.options.format.FormatUiState
import ly.img.cesdk.dock.options.layer.LayerUiState
import ly.img.cesdk.dock.options.shapeoptions.ShapeOptionsUiState
import ly.img.cesdk.engine.BlockType
import ly.img.engine.DesignBlock

sealed interface BottomSheetContent {
    object Library : BottomSheetContent
    class Replace(val designBlock: DesignBlock, val blockType: BlockType) : BottomSheetContent
    class Layer(val uiState: LayerUiState) : BottomSheetContent
    class Options(val uiState: ShapeOptionsUiState) : BottomSheetContent
    class FillStroke(val uiState: FillStrokeUiState) : BottomSheetContent
    class Format(val uiState: FormatUiState) : BottomSheetContent

    fun getType(): String {
        return javaClass.name
    }
}