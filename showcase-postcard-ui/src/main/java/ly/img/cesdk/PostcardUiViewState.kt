package ly.img.cesdk

import ly.img.cesdk.editorui.EditorUiViewState
import ly.img.cesdk.rootbar.RootBarItemData

data class PostcardUiViewState(
    val editorUiViewState: EditorUiViewState,
    val postcardMode: PostcardMode = PostcardMode.Design,
    val rootBarItems: List<RootBarItemData> = listOf()
)

enum class PostcardMode {
    Design,
    Write
}