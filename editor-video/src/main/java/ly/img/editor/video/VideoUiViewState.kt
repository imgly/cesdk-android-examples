package ly.img.editor.video

import ly.img.editor.base.ui.EditorUiViewState

data class VideoUiViewState(
    val editorUiViewState: EditorUiViewState,
    val canExport: Boolean = false,
)
