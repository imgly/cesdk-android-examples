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
import ly.img.editor.core.EditorScope
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.library.LibraryViewModel
import ly.img.editor.core.ui.register
import ly.img.editor.postcard.bottomsheet.PostcardSheetType
import ly.img.editor.postcard.bottomsheet.message_color.MessageColorBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_font.MessageFontBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_font.createMessageFontUiState
import ly.img.editor.postcard.bottomsheet.message_size.MessageSize
import ly.img.editor.postcard.bottomsheet.message_size.MessageSizeBottomSheetContent
import ly.img.editor.postcard.bottomsheet.template_colors.TemplateColorsBottomSheetContent
import ly.img.editor.postcard.bottomsheet.template_colors.TemplateColorsUiState
import ly.img.editor.postcard.rootbar.rootBarItems
import ly.img.editor.postcard.util.ColorType
import ly.img.editor.postcard.util.SelectionColors
import ly.img.editor.postcard.util.getPageSelectionColors
import ly.img.editor.postcard.util.requirePinnedBlock
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.Typeface

class PostcardUiViewModel(
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
    private var pageSelectionColors: SelectionColors? = null
    private var hasUnsavedChanges = false

    val uiState =
        merge(baseUiState, pageIndex, historyChangeTrigger).map {
            updatePageSelectionColors()
            PostcardUiViewState(
                editorUiViewState = baseUiState.value,
                postcardMode = if (pageIndex.value == 0) PostcardMode.Design else PostcardMode.Write,
                rootBarItems = rootBarItems(engine, pageIndex.value, pageSelectionColors),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PostcardUiViewState(baseUiState.value),
        )

    override fun EventsHandler.extraEvents() {
        register<PostcardEvent.OnChangeMessageSize> { onChangeMessageSize(it.messageSize) }
        register<PostcardEvent.OnChangeMessageColor> { onChangeMessageColor(it.color) }
        register<PostcardEvent.OnChangeFont> { onChangeMessageFont(it.fontUri, it.typeface) }
        register<PostcardEvent.OnChangeTypeface> { onChangeMessageTypeface(it.typeface) }
        register<PostcardEvent.OnChangeTemplateColor> { onChangeTemplateColor(it.name, it.color) }
    }

    override fun getBlockForEvents(): Block {
        return super.getBlockForEvents() ?: Block(
            designBlock = engine.requirePinnedBlock(),
            type = BlockType.Text,
        )
    }

    override fun onPreCreate() {
        super.onPreCreate()
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

    override fun openSheet(type: SheetType) {
        when (type) {
            // Cannot be invoked by customers
            is PostcardSheetType ->
                when (type) {
                    is PostcardSheetType.TemplateColors -> {
                        setBottomSheetContent {
                            TemplateColorsBottomSheetContent(
                                type = type,
                                uiState =
                                    TemplateColorsUiState(
                                        editor.colorPalette,
                                        checkNotNull(pageSelectionColors).getColors(),
                                    ),
                            )
                        }
                    }
                    is PostcardSheetType.Font -> {
                        setBottomSheetContent {
                            MessageFontBottomSheetContent(
                                type = type,
                                uiState = createMessageFontUiState(designBlock = engine.requirePinnedBlock(), engine = engine),
                            )
                        }
                    }
                    is PostcardSheetType.Size -> {
                        setBottomSheetContent {
                            MessageSizeBottomSheetContent(
                                type = type,
                                messageSize = MessageSize.get(engine, engine.requirePinnedBlock()),
                            )
                        }
                    }
                    is PostcardSheetType.Color -> {
                        setBottomSheetContent {
                            MessageColorBottomSheetContent(
                                type = type,
                                color = engine.block.getFillSolidColor(engine.requirePinnedBlock()).toComposeColor(),
                            )
                        }
                    }
                }
            else -> super.openSheet(type)
        }
    }

    override fun updateBottomSheetUiState() {
        super.updateBottomSheetUiState()
        setBottomSheetContent {
            when (it) {
                is MessageSizeBottomSheetContent ->
                    MessageSizeBottomSheetContent(
                        type = it.type,
                        messageSize = MessageSize.get(engine, engine.requirePinnedBlock()),
                    )

                is MessageColorBottomSheetContent ->
                    MessageColorBottomSheetContent(
                        type = it.type,
                        color = engine.block.getFillSolidColor(engine.requirePinnedBlock()).toComposeColor(),
                    )

                is MessageFontBottomSheetContent ->
                    MessageFontBottomSheetContent(
                        type = it.type,
                        uiState = createMessageFontUiState(designBlock = engine.requirePinnedBlock(), engine = engine),
                    )

                is TemplateColorsBottomSheetContent -> {
                    updatePageSelectionColors()
                    TemplateColorsBottomSheetContent(
                        type = it.type,
                        uiState =
                            TemplateColorsUiState(
                                editor.colorPalette,
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

    override fun handleBackPress(
        bottomSheetOffset: Float,
        bottomSheetMaxOffset: Float,
    ): Boolean {
        val handled =
            super.handleBackPress(
                bottomSheetOffset = bottomSheetOffset,
                bottomSheetMaxOffset = bottomSheetMaxOffset,
            )
        return if (handled.not()) {
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

    private fun updatePageSelectionColors() {
        if (isSceneLoaded.value && pageIndex.value == 0) {
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
        if (baseUiState.value.isUndoEnabled) hasUnsavedChanges = true
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

    private fun onChangeMessageTypeface(typeface: Typeface) {
        val block = engine.requirePinnedBlock()
        engine.overrideAndRestore(
            designBlock = block,
            "text/character",
        ) {
            engine.block.setTypeface(
                block = block,
                typeface = typeface,
            )
        }
        engine.editor.addUndoStep()
    }
}
