package ly.img.editor.postcard

import ly.img.editor.base.ui.EditorUiViewState
import ly.img.editor.postcard.rootbar.RootBarItemData

data class PostcardUiViewState(
    val editorUiViewState: EditorUiViewState,
    val postcardMode: PostcardMode = PostcardMode.Design,
    val rootBarItems: List<RootBarItemData> = listOf(),
)

enum class PostcardMode {
    Design,
    Write,
}
