package ly.img.cesdk

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ly.img.cesdk.core.engine.deselectAllBlocks
import ly.img.cesdk.core.engine.getPage
import ly.img.cesdk.editorui.EditorUiViewModel
import ly.img.cesdk.engine.overrideAndRestore
import ly.img.cesdk.engine.overrideAndRestoreAsync
import ly.img.cesdk.engine.showOutline
import ly.img.cesdk.engine.zoomToBackdrop
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
        engine.overrideAndRestoreAsync(page, "design/style") {
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
            overrideAndRestore(getPage(pageIndex.value), "design/style") {
                editor.setSettingBoolean("ubq://page/dimOutOfPageAreas", false)
                block.setClipped(it, true)
                block.setBoolean(it, "fill/enabled", false)
                showOutline(false)
            }
        }
    }
}