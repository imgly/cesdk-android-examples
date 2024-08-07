package ly.img.editor.postcard

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import ly.img.editor.base.engine.LayoutAxis
import ly.img.editor.base.engine.resetHistory
import ly.img.editor.base.engine.setFillType
import ly.img.editor.base.engine.showAllPages
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.toComposeColor
import ly.img.editor.base.engine.toEngineColor
import ly.img.editor.base.engine.zoomToScene
import ly.img.editor.base.ui.Block
import ly.img.editor.base.ui.EditorUiViewModel
import ly.img.editor.base.ui.Event
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.postcard.bottomsheet.message_color.MessageColorBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_font.MessageFontBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_font.createMessageFontUiState
import ly.img.editor.postcard.bottomsheet.message_size.MessageSize
import ly.img.editor.postcard.bottomsheet.message_size.MessageSizeBottomSheetContent
import ly.img.editor.postcard.bottomsheet.template_colors.TemplateColorsBottomSheetContent
import ly.img.editor.postcard.bottomsheet.template_colors.TemplateColorsUiState
import ly.img.editor.postcard.rootbar.RootBarItemType
import ly.img.editor.postcard.rootbar.rootBarItems
import ly.img.editor.postcard.util.ColorType
import ly.img.editor.postcard.util.SelectionColors
import ly.img.editor.postcard.util.getPageSelectionColors
import ly.img.editor.postcard.util.requirePinnedBlock
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.Typeface

class PostcardUiViewModel(
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
    private var pageSelectionColors: SelectionColors? = null
    private var hasUnsavedChanges = false

    val uiState =
        merge(_uiState, pageIndex, historyChangeTrigger).map {
            updatePageSelectionColors()
            PostcardUiViewState(
                editorUiViewState = _uiState.value,
                postcardMode = if (pageIndex.value == 0) PostcardMode.Design else PostcardMode.Write,
                rootBarItems = rootBarItems(engine, pageIndex.value, pageSelectionColors),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PostcardUiViewState(_uiState.value),
        )

    override fun onEvent(event: Event) {
        when (event) {
            is PostcardEvent -> {
                when (event) {
                    is PostcardEvent.OnRootBarItemClick -> onRootBarItemClick(event.itemType)
                    is PostcardEvent.OnChangeMessageSize -> onChangeMessageSize(event.messageSize)
                    is PostcardEvent.OnChangeMessageColor -> onChangeMessageColor(event.color)
                    is PostcardEvent.OnChangeFont -> onChangeMessageFont(event.fontUri, event.typeface)
                    is PostcardEvent.OnChangeTemplateColor -> onChangeTemplateColor(event.name, event.color)
                }
            }

            else -> super.onEvent(event)
        }
    }

    override fun getBlockForEvents(): Block {
        return super.getBlockForEvents() ?: Block(
            designBlock = engine.requirePinnedBlock(),
            type = BlockType.Text,
        )
    }

    override fun setSettings() {
        super.setSettings()
        engine.editor.setGlobalScope(Scope.EditorAdd, GlobalScope.DEFER)
    }

    private fun onChangeTemplateColor(
        name: String,
        color: Color,
    ) {
        val namedColor = checkNotNull(pageSelectionColors).getNamedColor(name)
        val engineColor = color.toEngineColor()
        namedColor.colorTypeBlocksMapping.forEach {
            val colorType = it.key
            val designBlockSet = it.value
            designBlockSet.forEach { block ->
                when (colorType) {
                    ColorType.Fill ->
                        engine.overrideAndRestore(block, Scope.FillChange) {
                            engine.block.setFillType(block, FillType.Color)
                            engine.block.setFillSolidColor(block, engineColor)
                        }

                    ColorType.Stroke ->
                        engine.overrideAndRestore(block, Scope.StrokeChange) {
                            engine.block.setStrokeColor(block, engineColor)
                        }
                }
            }
        }
    }

    override fun enterEditMode() {
        engine.showPage(pageIndex.value)
    }

    override fun enterPreviewMode() {
        engine.deselectAllBlocks()
        showAllPages()
        engine.zoomToScene(currentInsets)
    }

    override suspend fun onPreExport() = Unit

    override suspend fun onPostExport() = Unit

    override fun updateBottomSheetUiState() {
        super.updateBottomSheetUiState()
        setBottomSheetContent {
            when (it) {
                is MessageSizeBottomSheetContent ->
                    MessageSizeBottomSheetContent(
                        MessageSize.get(engine, engine.requirePinnedBlock()),
                    )

                is MessageColorBottomSheetContent ->
                    MessageColorBottomSheetContent(
                        engine.block.getFillSolidColor(engine.requirePinnedBlock()).toComposeColor(),
                    )

                is MessageFontBottomSheetContent ->
                    MessageFontBottomSheetContent(
                        createMessageFontUiState(designBlock = engine.requirePinnedBlock(), engine = engine),
                    )

                is TemplateColorsBottomSheetContent -> {
                    updatePageSelectionColors()
                    TemplateColorsBottomSheetContent(
                        TemplateColorsUiState(
                            colorPalette,
                            checkNotNull(pageSelectionColors).getColors(),
                        ),
                    )
                }

                else -> {
                    it
                }
            }
        }
    }

    override fun handleBackPress(): Boolean {
        return if (!super.handleBackPress()) {
            val page = pageIndex.value
            if (page > 0) {
                setPage(page - 1)
                true
            } else {
                false
            }
        } else {
            true
        }
    }

    override fun hasUnsavedChanges(): Boolean {
        return super.hasUnsavedChanges() || hasUnsavedChanges
    }

    private fun onRootBarItemClick(itemType: RootBarItemType) {
        setBottomSheetContent {
            when (itemType) {
                RootBarItemType.TemplateColors ->
                    TemplateColorsBottomSheetContent(
                        TemplateColorsUiState(colorPalette, checkNotNull(pageSelectionColors).getColors()),
                    )

                RootBarItemType.Font ->
                    MessageFontBottomSheetContent(
                        createMessageFontUiState(designBlock = engine.requirePinnedBlock(), engine = engine),
                    )

                RootBarItemType.Size -> MessageSizeBottomSheetContent(MessageSize.get(engine, engine.requirePinnedBlock()))
                RootBarItemType.Color ->
                    MessageColorBottomSheetContent(
                        engine.block.getFillSolidColor(engine.requirePinnedBlock()).toComposeColor(),
                    )
            }
        }
    }

    private fun updatePageSelectionColors() {
        if (_isSceneLoaded.value && pageIndex.value == 0) {
            pageSelectionColors =
                engine.getPageSelectionColors(
                    forPage = 0,
                    includeDisabled = true,
                    setDisabled = true,
                    ignoreScope = true,
                )
        }
    }

    override fun setPage(index: Int) {
        super.setPage(index)
        if (_uiState.value.isUndoEnabled) hasUnsavedChanges = true
        engine.resetHistory()
    }

    private fun showAllPages() {
        engine.showAllPages(if (inPortraitMode) LayoutAxis.Vertical else LayoutAxis.Horizontal)
    }

    private fun onChangeMessageSize(messageSize: MessageSize) {
        engine.block.setFloat(engine.requirePinnedBlock(), "text/fontSize", messageSize.size)
        engine.editor.addUndoStep()
    }

    private fun onChangeMessageColor(color: Color) {
        engine.overrideAndRestore(engine.requirePinnedBlock(), Scope.FillChange) {
            engine.block.setFillSolidColor(it, color.toEngineColor())
        }
    }

    private fun onChangeMessageFont(
        fontUri: Uri,
        typeface: Typeface,
    ) {
        val block = engine.requirePinnedBlock()
        engine.overrideAndRestore(
            designBlock = block,
            "text/character",
        ) {
            engine.block.setFont(
                block = block,
                fontFileUri = fontUri,
                typeface = typeface,
            )
        }
        engine.editor.addUndoStep()
    }
}
