package ly.img.editor.design

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.LayoutAxis
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.getStackOrNull
import ly.img.editor.core.ui.library.LibraryViewModel

class DesignUiViewModel(
    editorScope: EditorScope,
    onCreate: suspend EditorScope.() -> Unit,
    onExport: suspend EditorScope.() -> Unit,
    onClose: suspend EditorScope.(Boolean) -> Unit,
    onError: suspend EditorScope.(Throwable) -> Unit,
    libraryViewModel: LibraryViewModel,
) : EditorUiViewModel(
        editorScope = editorScope,
        onCreate = onCreate,
        onExport = onExport,
        onClose = onClose,
        onError = onError,
        libraryViewModel = libraryViewModel,
    ) {
    val uiState = baseUiState

    override fun enterEditMode() {
        engine.deselectAllBlocks()
    }

    override fun onSceneLoaded() {
        super.onSceneLoaded()
        engine.scene.get() ?: return
        engine.getStackOrNull()?.let {
            engine.block.setEnum(it, "stack/axis", LayoutAxis.Horizontal.name)
        }
        engine.editor.setSettingBoolean(keypath = "features/pageCarouselEnabled", value = true)
        viewModelScope.launch {
            engine.editor.onCarouselPageChanged()
                .onEach {
                    engine.scene.getPages()
                        .indexOf(it)
                        .let(::setPageIndex)
                }
                .collect()
        }
    }

    override fun showPage(index: Int) {
        zoom(insets = currentInsets, zoomToPage = true)
    }

    override fun enterPreviewMode() = Unit
}
