package ly.img.editor.base.ui

import ly.img.editor.base.rootdock.RootDockItemData
import ly.img.editor.compose.bottomsheet.ModalBottomSheetState
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue

data class EditorUiViewState(
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
    val rootDockItems: List<RootDockItemData> = emptyList(),
)
