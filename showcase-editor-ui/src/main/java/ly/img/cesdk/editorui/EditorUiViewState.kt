package ly.img.cesdk.editorui

import androidx.compose.material3.ExperimentalMaterial3Api
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetState
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.engine.Block

data class EditorUiViewState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val isLoading: Boolean = true,
    val isInPreviewMode: Boolean = false,
    val isUndoEnabled: Boolean = false,
    val isRedoEnabled: Boolean = false,
    val errorLoading: Boolean = false,
    val showExitDialog: Boolean = false,
    val selectedBlock: Block? = null,
    val isEditingText: Boolean = false,
    val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
)