package ly.img.cesdk

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ly.img.cesdk.bottomsheet.message_color.MessageColorBottomSheetContent
import ly.img.cesdk.bottomsheet.message_font.MessageFontBottomSheetContent
import ly.img.cesdk.bottomsheet.message_font.createMessageFontUiState
import ly.img.cesdk.bottomsheet.message_size.MessageSize
import ly.img.cesdk.bottomsheet.message_size.MessageSizeBottomSheetContent
import ly.img.cesdk.bottomsheet.template_colors.TemplateColorsBottomSheetContent
import ly.img.cesdk.bottomsheet.template_colors.TemplateColorsUiState
import ly.img.cesdk.core.data.font.FontData
import ly.img.cesdk.core.engine.FONT_BASE_PATH
import ly.img.cesdk.core.engine.deselectAllBlocks
import ly.img.cesdk.editorui.EditorUiViewModel
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.engine.LayoutAxis
import ly.img.cesdk.core.engine.Scope
import ly.img.cesdk.core.engine.getScene
import ly.img.cesdk.core.engine.overrideAndRestore
import ly.img.cesdk.engine.resetHistory
import ly.img.cesdk.engine.setFillType
import ly.img.cesdk.engine.showAllPages
import ly.img.cesdk.engine.showPage
import ly.img.cesdk.engine.toComposeColor
import ly.img.cesdk.engine.toEngineColor
import ly.img.cesdk.engine.zoomToScene
import ly.img.cesdk.rootbar.RootBarItemType
import ly.img.cesdk.rootbar.rootBarItems
import ly.img.cesdk.util.ColorType
import ly.img.cesdk.util.SelectionColors
import ly.img.cesdk.util.getPageSelectionColors
import ly.img.cesdk.util.getPinnedBlock
import ly.img.cesdk.util.requirePinnedBlock
import ly.img.engine.DesignBlock
import ly.img.engine.GlobalScope
import ly.img.engine.MimeType

class PostcardUiViewModel : EditorUiViewModel() {

    private var pageSelectionColors: SelectionColors? = null
    private var hasUnsavedChanges = false

    val uiState = merge(_uiState, pageIndex, engine.editor.onHistoryUpdated()).map {
        updatePageSelectionColors()
        PostcardUiViewState(
            editorUiViewState = _uiState.value,
            postcardMode = if (pageIndex.value == 0) PostcardMode.Design else PostcardMode.Write,
            rootBarItems = rootBarItems(engine, pageIndex.value, pageSelectionColors)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PostcardUiViewState(_uiState.value)
    )

    override fun onEvent(event: Event) {
        when (event) {
            is PostcardEvent -> {
                when (event) {
                    is PostcardEvent.OnNextPage -> setPage(pageIndex.value + 1)
                    is PostcardEvent.OnPreviousPage -> setPage(pageIndex.value - 1)
                    is PostcardEvent.OnRootBarItemClick -> onRootBarItemClick(event.itemType)
                    is PostcardEvent.OnChangeMessageSize -> onChangeMessageSize(event.messageSize)
                    is PostcardEvent.OnChangeMessageColor -> onChangeMessageColor(event.color)
                    is PostcardEvent.OnChangeMessageFont -> onChangeMessageFont(event.fontData)
                    is PostcardEvent.OnChangeTemplateColor -> onChangeTemplateColor(event.name, event.color)
                }
            }

            else -> {
                if (event is Event.OnLoadScene) {
                    setColorPaletteColors(event.sceneUriString)
                }
                super.onEvent(event)
            }
        }
    }

    override fun getBlockForEvents(): DesignBlock {
        return try {
            super.getBlockForEvents()
        } catch (ex: IllegalStateException) {
            checkNotNull(engine.getPinnedBlock())
        }
    }

    private fun onChangeTemplateColor(name: String, color: Color) {
        val namedColor = checkNotNull(pageSelectionColors).getNamedColor(name)
        val engineColor = color.toEngineColor()
        namedColor.colorTypeBlocksMapping.forEach {
            val colorType = it.key
            val designBlockSet = it.value
            designBlockSet.forEach { block ->
                when (colorType) {
                    ColorType.Fill -> engine.overrideAndRestore(block, Scope.FillChange) {
                        engine.block.setFillType(block, "color")
                        engine.block.setFillSolidColor(block, engineColor)
                    }

                    ColorType.Stroke -> engine.overrideAndRestore(block, Scope.StrokeChange) {
                        engine.block.setStrokeColor(block, engineColor)
                    }
                }
            }
        }
    }

    override fun onSceneLoad() {
        engine.editor.setGlobalScope(Scope.EditorAdd, GlobalScope.DEFER)
    }

    override fun enterEditMode() {
        engine.showPage(pageIndex.value)
    }

    override fun enterPreviewMode() {
        viewModelScope.launch {
            engine.deselectAllBlocks()
            showAllPages()
            engine.zoomToScene(currentInsets)
        }
    }

    override suspend fun exportSceneAsByteArray(): ByteArray {
        engine.showAllPages(LayoutAxis.Vertical)
        val byteArray = engine.block.export(engine.getScene(), MimeType.PDF)
        if (_uiState.value.isInPreviewMode) {
            showAllPages()
        } else {
            engine.showPage(pageIndex.value)
        }
        return byteArray
    }

    override fun updateBottomSheetUiState() {
        super.updateBottomSheetUiState()
        setBottomSheetContent {
            when (it) {
                is MessageSizeBottomSheetContent -> MessageSizeBottomSheetContent(
                    MessageSize.get(engine, engine.requirePinnedBlock())
                )

                is MessageColorBottomSheetContent -> MessageColorBottomSheetContent(
                    engine.block.getFillSolidColor(engine.requirePinnedBlock()).toComposeColor()
                )

                is MessageFontBottomSheetContent -> MessageFontBottomSheetContent(
                    createMessageFontUiState(
                        engine.requirePinnedBlock(), engine, checkNotNull(assetsRepo.fontFamilies.value).getOrThrow()
                    )
                )

                is TemplateColorsBottomSheetContent -> {
                    updatePageSelectionColors()
                    TemplateColorsBottomSheetContent(
                        TemplateColorsUiState(
                            colorPalette, checkNotNull(pageSelectionColors).getColors()
                        )
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
            } else false
        } else true
    }

    override fun hasUnsavedChanges(): Boolean {
        return super.hasUnsavedChanges() || hasUnsavedChanges
    }

    private fun onRootBarItemClick(itemType: RootBarItemType) {
        setBottomSheetContent {
            when (itemType) {
                RootBarItemType.TemplateColors -> TemplateColorsBottomSheetContent(
                    TemplateColorsUiState(colorPalette, checkNotNull(pageSelectionColors).getColors())
                )

                RootBarItemType.Font -> MessageFontBottomSheetContent(
                    createMessageFontUiState(
                        engine.requirePinnedBlock(), engine, checkNotNull(assetsRepo.fontFamilies.value).getOrThrow()
                    )
                )

                RootBarItemType.Size -> MessageSizeBottomSheetContent(MessageSize.get(engine, engine.requirePinnedBlock()))
                RootBarItemType.Color -> MessageColorBottomSheetContent(
                    engine.block.getFillSolidColor(engine.requirePinnedBlock()).toComposeColor()
                )
            }
        }
    }

    private fun updatePageSelectionColors() {
        if (_isSceneLoaded.value && pageIndex.value == 0) {
            pageSelectionColors = engine.getPageSelectionColors(
                forPage = 0, includeDisabled = true, setDisabled = true, ignoreScope = true
            )
        }
    }

    private fun setPage(index: Int) {
        if (index == pageIndex.value) return
        pageIndex.update { index }
        engine.showPage(index)
        if (_uiState.value.isUndoEnabled) hasUnsavedChanges = true
        engine.resetHistory()
        setBottomSheetContent { null }
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

    private fun onChangeMessageFont(fontData: FontData) {
        engine.block.setString(
            engine.requirePinnedBlock(),
            "text/fontFileUri",
            Uri.parse("$FONT_BASE_PATH/${fontData.fontPath}").toString()
        )
        engine.editor.addUndoStep()
    }

    private fun setColorPaletteColors(sceneUri: String) {
        val scene = sceneUri.split("/").last().split(".").first()
        colorPalette = when (scene) {
            "bonjour_paris" -> listOf(
                Color(0xFF000000),
                Color(0xFFFFFFFF),
                Color(0xFF4932D1),
                Color(0xFFFE6755),
                Color(0xFF606060),
                Color(0xFF696969),
                Color(0xFF999999),
            )

            "merry_christmas" -> listOf(
                Color(0xFF536F1A),
                Color(0xFFFFFFFF),
                Color(0xFF6B2923),
                Color(0xFFF3AE2B),
                Color(0xFF051111),
                Color(0xFF696969),
                Color(0xFF999999),
            )

            "thank_you" -> listOf(
                Color(0xFFE09F96),
                Color(0xFFFFFFFF),
                Color(0xFF761E40),
                Color(0xFF7471A3),
                Color(0xFF20121F),
                Color(0xFF696969),
                Color(0xFF999999),
            )

            "wish_you_were_here" -> listOf(
                Color(0xFFE75050),
                Color(0xFFFFFFFF),
                Color(0xFF111111),
                Color(0xFF282929),
                Color(0xFF619888),
                Color(0xFF696969),
                Color(0xFF999999),
            )

            else -> listOf(
                Color.Blue, Color.Green, Color.Yellow, Color.Red, Color.Black, Color.White, Color.Gray
            )
        }
    }
}