package ly.img.editor.design

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.LayoutAxis
import ly.img.editor.base.engine.resetHistory
import ly.img.editor.base.engine.setRoleButPreserveGlobalScopes
import ly.img.editor.base.engine.showAllPages
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.zoomToPage
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.engine.Engine
import ly.img.engine.GlobalScope

class DesignUiViewModel(
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
        scrollablePreview = true,
    ) {
    val uiState =
        merge(_uiState, pageIndex, isZoomedIn, historyChangeTrigger).map {
            DesignUiViewState(
                editorUiViewState = _uiState.value,
                pageIndex = pageIndex.value,
                isZoomedIn = isZoomedIn.value,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DesignUiViewState(_uiState.value),
        )

    override fun enterEditMode() {
        engine.showPage(pageIndex.value)
    }

    override fun preEnterPreviewMode() {
        super.preEnterPreviewMode()
        engine.deselectAllBlocks()
        showAllPages()
    }

    override fun setPage(index: Int) {
        super.setPage(index)
        // We need this because the current visible page is saved in the history state.
        if (!engine.editor.canUndo() && !engine.editor.canRedo()) {
            engine.resetHistory()
        }
    }

    override fun enterPreviewMode() {
        viewModelScope.launch {
            engine.zoomToPage(pageIndex.value, currentInsets)
        }
    }

    override suspend fun onPreExport() = Unit

    override suspend fun onPostExport() = Unit

    private fun showAllPages() {
        engine.showAllPages(if (inPortraitMode) LayoutAxis.Vertical else LayoutAxis.Horizontal)
    }
}
