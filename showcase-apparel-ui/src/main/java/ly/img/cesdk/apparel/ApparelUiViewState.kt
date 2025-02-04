package ly.img.cesdk.apparel

import androidx.compose.material3.ExperimentalMaterial3Api
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetState
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.engine.Block

data class ApparelUiViewState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val isLoading: Boolean,
    val isInPreviewMode: Boolean,
    val isUndoEnabled: Boolean,
    val isRedoEnabled: Boolean,
    val errorLoading: Boolean = false,
    val showExitDialog: Boolean = false,
    val selectedBlock: Block? = null,
    val isEditingText: Boolean = false,
    val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
)