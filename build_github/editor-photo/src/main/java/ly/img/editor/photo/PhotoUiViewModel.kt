package ly.img.editor.photo

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.editor.base.components.VectorIcon
import ly.img.editor.base.dock.OptionType
import ly.img.editor.base.engine.CROP_EDIT_MODE
import ly.img.editor.base.engine.TRANSFORM_EDIT_MODE
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.zoomToPage
import ly.img.editor.base.rootdock.RootDockItemActionType
import ly.img.editor.base.rootdock.RootDockItemData
import ly.img.editor.base.ui.Block
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.base.ui.EditorUiViewState
import ly.img.editor.base.ui.Event
import ly.img.editor.core.R
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.iconpack.Addshape
import ly.img.editor.core.ui.iconpack.Addsticker
import ly.img.editor.core.ui.iconpack.Addtext
import ly.img.editor.core.ui.iconpack.Adjustments
import ly.img.editor.core.ui.iconpack.Blur
import ly.img.editor.core.ui.iconpack.Croprotate
import ly.img.editor.core.ui.iconpack.Effect
import ly.img.editor.core.ui.iconpack.Filter
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.SceneMode

class PhotoUiViewModel(
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
    val uiState: StateFlow<EditorUiViewState> = _uiState

    init {
        viewModelScope.launch {
            historyChangeTrigger
                .filter { engine.editor.getEditMode() != CROP_EDIT_MODE }
                .onEach { tryUnselectPage() }
                .collect()
        }
    }

    private fun getPage() =
        engine.scene
            .getPages()
            .let {
                require(it.size == 1) { REQUIREMENT_ERROR_MESSAGE }
                it.first()
            }

    override val horizontalPageInset: Float
        get() = if (engine.editor.getEditMode() == CROP_EDIT_MODE) CROP_MODE_INSET else 0F

    override val verticalPageInset: Float
        get() = if (engine.editor.getEditMode() == CROP_EDIT_MODE) CROP_MODE_INSET else 0F

    override fun getBlockForEvents(): Block {
        return super.getBlockForEvents() ?: Block(
            designBlock = getPage(),
            type = BlockType.Image,
        )
    }

    override fun getSelectedBlock(): DesignBlock {
        return super.getSelectedBlock() ?: getPage()
    }

    override fun setSelectedBlock(block: Block?) {
        val updatedBlock =
            if (block?.designBlock == getPage()) {
                null
            } else {
                block
            }
        super.setSelectedBlock(updatedBlock)
    }

    override fun setSettings() {
        super.setSettings()
        val page = getPage()
        listOf(
            Scope.AppearanceAdjustment,
            Scope.AppearanceFilter,
            Scope.AppearanceEffect,
            Scope.AppearanceBlur,
            Scope.LayerCrop,
        ).forEach {
            engine.block.setScopeEnabled(page, it, enabled = true)
        }
        listOf(
            Scope.EditorSelect,
            Scope.LayerMove,
            Scope.LayerResize,
            Scope.LayerRotate,
        ).forEach {
            engine.block.setScopeEnabled(page, it, enabled = false)
        }
        engine.editor.setSettingBoolean(keypath = "page/allowCropInteraction", value = true)
        engine.editor.setSettingBoolean(keypath = "page/allowMoveInteraction", value = false)
        engine.editor.setSettingBoolean(keypath = "page/allowResizeInteraction", value = false)
        engine.editor.setSettingBoolean(keypath = "page/restrictResizeInteractionToFixedAspectRatio", value = false)
        engine.editor.setSettingBoolean(keypath = "page/allowRotateInteraction", value = false)
    }

    override fun getRootDockItems(assetLibrary: AssetLibrary): List<RootDockItemData> {
        fun getType(libraryCategory: LibraryCategory): RootDockItemActionType {
            return RootDockItemActionType.OnEvent(Event.OnAddLibraryCategoryClick(libraryCategory))
        }
        return listOf(
            RootDockItemData(
                type = RootDockItemActionType.OnEvent(Event.OnOptionClick(OptionType.Adjustments)),
                labelStringRes = ly.img.editor.base.R.string.ly_img_editor_adjustment,
                icon = VectorIcon(IconPack.Adjustments),
            ),
            RootDockItemData(
                type = RootDockItemActionType.OnEvent(Event.OnOptionClick(OptionType.Filter)),
                labelStringRes = ly.img.editor.base.R.string.ly_img_editor_filter,
                icon = VectorIcon(IconPack.Filter),
            ),
            RootDockItemData(
                type = RootDockItemActionType.OnEvent(Event.OnOptionClick(OptionType.Effect)),
                labelStringRes = ly.img.editor.base.R.string.ly_img_editor_effect,
                icon = VectorIcon(IconPack.Effect),
            ),
            RootDockItemData(
                type = RootDockItemActionType.OnEvent(Event.OnOptionClick(OptionType.Blur)),
                labelStringRes = ly.img.editor.base.R.string.ly_img_editor_blur,
                icon = VectorIcon(IconPack.Blur),
            ),
            RootDockItemData(
                type = RootDockItemActionType.OnEvent(Event.OnOptionClick(OptionType.Crop)),
                labelStringRes = ly.img.editor.base.R.string.ly_img_editor_crop,
                icon = VectorIcon(IconPack.Croprotate),
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
        val page = getPage()
        engine.overrideAndRestore(page, Scope.LayerClipping) {
            engine.block.setClipped(page, clipped = false)
        }
    }

    override fun enterPreviewMode() {
        val page = getPage()
        engine.overrideAndRestore(page, Scope.LayerClipping) {
            engine.block.setClipped(page, clipped = true)
        }
        engine.deselectAllBlocks()
        engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
        engine.zoomToPage(pageIndex.value, currentInsets)
    }

    override fun onEditModeChanged(editMode: String) {
        if (editMode != CROP_EDIT_MODE) {
            tryUnselectPage()
        }
    }

    private fun tryUnselectPage() {
        val page = getPage()
        if (engine.block.isSelected(page) && getBlockForEvents().designBlock == page) {
            engine.overrideAndRestore(page, Scope.EditorSelect) {
                engine.block.setSelected(page, selected = false)
            }
            engine.block.setScopeEnabled(page, Scope.EditorSelect, enabled = false)
        }
    }

    override suspend fun onPreExport() = Unit

    override suspend fun onPostExport() = Unit

    private companion object {
        const val CROP_MODE_INSET = 24F
        const val REQUIREMENT_ERROR_MESSAGE = "Photo Editor scene should should contain a single page with image fill."
    }
}
