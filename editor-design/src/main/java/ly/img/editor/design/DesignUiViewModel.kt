package ly.img.editor.design

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ly.img.editor.base.components.VectorIcon
import ly.img.editor.base.engine.LayoutAxis
import ly.img.editor.base.engine.resetHistory
import ly.img.editor.base.engine.showAllPages
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.zoomToPage
import ly.img.editor.base.rootdock.RootDockItemActionType
import ly.img.editor.base.rootdock.RootDockItemData
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.base.ui.Event
import ly.img.editor.core.R
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.iconpack.Addcameraforegound
import ly.img.editor.core.ui.iconpack.Addgalleryforeground
import ly.img.editor.core.ui.iconpack.Addimageforeground
import ly.img.editor.core.ui.iconpack.Addshape
import ly.img.editor.core.ui.iconpack.Addsticker
import ly.img.editor.core.ui.iconpack.Addtext
import ly.img.editor.core.ui.iconpack.Elements
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.engine.Engine
import ly.img.engine.SceneMode

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

    override fun getRootDockItems(assetLibrary: AssetLibrary): List<RootDockItemData> {
        fun getType(libraryCategory: LibraryCategory): RootDockItemActionType {
            return RootDockItemActionType.OnEvent(Event.OnAddLibraryCategoryClick(libraryCategory))
        }
        return listOf(
            RootDockItemData(
                type = getType(assetLibrary.elements(SceneMode.DESIGN)),
                labelStringRes = R.string.ly_img_editor_elements,
                icon = VectorIcon(IconPack.Elements),
            ),
            RootDockItemData(
                type = RootDockItemActionType.OpenGallery,
                labelStringRes = R.string.ly_img_editor_gallery,
                icon = VectorIcon(IconPack.Addgalleryforeground),
            ),
            RootDockItemData(
                type = RootDockItemActionType.OpenCamera,
                labelStringRes = R.string.ly_img_editor_camera,
                icon = VectorIcon(IconPack.Addcameraforegound),
            ),
            RootDockItemData(
                type = getType(assetLibrary.images(SceneMode.DESIGN)),
                labelStringRes = R.string.ly_img_editor_image,
                icon = VectorIcon(IconPack.Addimageforeground),
            ),
            RootDockItemData(
                type = getType(assetLibrary.text(SceneMode.DESIGN)),
                labelStringRes = R.string.ly_img_editor_text,
                icon = VectorIcon(IconPack.Addtext),
            ),
            RootDockItemData(
                type = getType(assetLibrary.shapes(SceneMode.DESIGN)),
                labelStringRes = R.string.ly_img_editor_shape,
                icon = VectorIcon(IconPack.Addshape),
            ),
            RootDockItemData(
                type = getType(assetLibrary.stickers(SceneMode.DESIGN)),
                labelStringRes = R.string.ly_img_editor_sticker,
                icon = VectorIcon(IconPack.Addsticker),
            ),
        )
    }

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
