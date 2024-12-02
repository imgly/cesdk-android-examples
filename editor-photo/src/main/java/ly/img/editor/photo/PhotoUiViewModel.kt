package ly.img.editor.photo

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.CROP_EDIT_MODE
import ly.img.editor.base.engine.TRANSFORM_EDIT_MODE
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.zoomToPage
import ly.img.editor.base.ui.Block
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.library.LibraryViewModel
import ly.img.engine.DesignBlock
import ly.img.engine.UnstableEngineApi

class PhotoUiViewModel(
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

    override fun onPreCreate() {
        super.onPreCreate()
        engine.editor.setSettingBoolean(keypath = "page/allowCropInteraction", value = true)
        engine.editor.setSettingBoolean(keypath = "page/allowMoveInteraction", value = false)
        engine.editor.setSettingBoolean(keypath = "page/allowResizeInteraction", value = false)
        engine.editor.setSettingBoolean(keypath = "page/restrictResizeInteractionToFixedAspectRatio", value = false)
        engine.editor.setSettingBoolean(keypath = "page/allowRotateInteraction", value = false)
    }

    override fun onSceneLoaded() {
        super.onSceneLoaded()
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
    }

    override fun enterEditMode() {
        engine.showPage(pageIndex.value)
        val page = getPage()
        engine.overrideAndRestore(page, Scope.LayerClipping) {
            engine.block.setClipped(page, clipped = false)
        }
    }

    @OptIn(UnstableEngineApi::class)
    override fun preEnterPreviewMode() {
        val pages = engine.scene.getPages()
        val firstPage = listOf(pages.first())
        val defaultInsets = defaultInsets
        engine.scene.enableCameraZoomClamping(
            firstPage,
            minZoomLimit = 1.0F,
            maxZoomLimit = 1.0F,
            paddingLeft = defaultInsets.left,
            paddingTop = defaultInsets.top,
            paddingRight = defaultInsets.right,
            paddingBottom = defaultInsets.bottom,
        )
        engine.scene.enableCameraPositionClamping(
            pages,
            paddingLeft = defaultInsets.left - horizontalPageInset,
            paddingTop = defaultInsets.top - verticalPageInset,
            paddingRight = defaultInsets.right - horizontalPageInset,
            paddingBottom = defaultInsets.bottom - verticalPageInset,
            scaledPaddingLeft = horizontalPageInset,
            scaledPaddingTop = verticalPageInset,
            scaledPaddingRight = horizontalPageInset,
            scaledPaddingBottom = verticalPageInset,
        )
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

    private companion object {
        const val CROP_MODE_INSET = 24F
        const val REQUIREMENT_ERROR_MESSAGE = "Photo Editor scene should should contain a single page with image fill."
    }
}
