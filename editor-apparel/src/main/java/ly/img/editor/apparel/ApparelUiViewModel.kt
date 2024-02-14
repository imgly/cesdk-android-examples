package ly.img.editor.apparel

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.showOutline
import ly.img.editor.base.engine.zoomToBackdrop
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.engine.Engine

class ApparelUiViewModel(
    baseUri: Uri,
    onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    onExport: suspend (Engine, EditorEventHandler) -> Unit,
    onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit,
    onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit,
    colorPalette: List<Color>,
) : EditorUiViewModel(
        baseUri = baseUri,
        onCreate = onCreate,
        onExport = onExport,
        onClose = onClose,
        onError = onError,
        colorPalette = colorPalette,
    ) {
    val uiState = _uiState.asStateFlow()

    override fun enterEditMode() {
        pageSetup()
    }

    override fun enterPreviewMode() {
        viewModelScope.launch {
            engine.zoomToBackdrop(currentInsets)
            engine.deselectAllBlocks()
            pageSetup()
        }
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

    override suspend fun onPreExport() = Unit

    override suspend fun onPostExport() = Unit
}
