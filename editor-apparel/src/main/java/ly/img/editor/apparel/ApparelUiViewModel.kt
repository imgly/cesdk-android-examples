package ly.img.editor.apparel

import ly.img.editor.base.engine.showOutline
import ly.img.editor.base.engine.zoomToBackdrop
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.library.LibraryViewModel

class ApparelUiViewModel(
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
        pageSetup()
    }

    override fun enterPreviewMode() {
        engine.zoomToBackdrop(currentInsets)
        engine.deselectAllBlocks()
        pageSetup()
    }

    override fun onCanvasMove(move: Boolean) {
        super.onCanvasMove(move)
        engine.showOutline(show = move)
    }

    private fun pageSetup() {
        with(engine) {
            overrideAndRestore(getPage(pageIndex.value), Scope.LayerClipping, Scope.FillChange) {
                editor.setSettingBoolean("ubq://page/dimOutOfPageAreas", false)
                block.setClipped(it, true)
                block.setBoolean(it, "fill/enabled", false)
                showOutline(false)
            }
        }
    }
}
