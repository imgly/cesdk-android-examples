package ly.img.editor.creative

import ly.img.editor.base.ui.EditorUiViewState

data class CreativeUiViewState(
    val editorUiViewState: EditorUiViewState,
    val pageIndex: Int = 0,
    val isZoomedIn: Boolean = false,
)
