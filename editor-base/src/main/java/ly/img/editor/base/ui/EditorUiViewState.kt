package ly.img.editor.base.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import ly.img.editor.core.ui.bottomsheet.ModalBottomSheetState
import ly.img.editor.core.ui.bottomsheet.ModalBottomSheetValue

data class EditorUiViewState
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        val isLoading: Boolean = true,
        val isInPreviewMode: Boolean = false,
        val allowEditorInteraction: Boolean = false,
        val isUndoEnabled: Boolean = false,
        val isRedoEnabled: Boolean = false,
        val selectedBlock: Block? = null,
        val isEditingText: Boolean = false,
        val isCanvasVisible: Boolean = false,
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
        val pageCount: Int = 0,
    )
