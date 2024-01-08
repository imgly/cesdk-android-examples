package ly.img.editor.apparel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.showOutline
import ly.img.editor.base.engine.zoomToBackdrop
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.engine.overrideAndRestoreAsync
import ly.img.engine.MimeType

class ApparelUiViewModel : EditorUiViewModel() {

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

    override suspend fun exportSceneAsByteArray(): ByteArray {
        val page = engine.getPage(pageIndex.value)
        lateinit var byteArray: ByteArray
        engine.overrideAndRestoreAsync(page, Scope.FillChange) {
            val prevPageFill = engine.block.getBoolean(page, "fill/enabled")
            engine.block.setBoolean(page, "fill/enabled", true)
            byteArray = engine.block.export(page, MimeType.PDF)
            engine.block.setBoolean(page, "fill/enabled", prevPageFill)
        }
        return byteArray
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