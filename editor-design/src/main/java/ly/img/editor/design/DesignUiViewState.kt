package ly.img.editor.design

import ly.img.editor.base.ui.EditorUiViewState

data class DesignUiViewState(
    val editorUiViewState: EditorUiViewState,
    val pageIndex: Int = 0,
    val isZoomedIn: Boolean = false,
)
