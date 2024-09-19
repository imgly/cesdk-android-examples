package ly.img.editor.base.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.base.components.actionmenu.CanvasActionMenuUiState
import ly.img.editor.base.components.actionmenu.createCanvasActionMenuUiState
import ly.img.editor.base.components.color_picker.fillAndStrokeColors
import ly.img.editor.base.dock.AdjustmentSheetContent
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.EffectSheetContent
import ly.img.editor.base.dock.FillStrokeBottomSheetContent
import ly.img.editor.base.dock.FormatBottomSheetContent
import ly.img.editor.base.dock.LayerBottomSheetContent
import ly.img.editor.base.dock.LibraryBottomSheetContent
import ly.img.editor.base.dock.LibraryCategoryBottomSheetContent
import ly.img.editor.base.dock.OptionType
import ly.img.editor.base.dock.OptionsBottomSheetContent
import ly.img.editor.base.dock.ReplaceBottomSheetContent
import ly.img.editor.base.dock.options.adjustment.AdjustmentUiState
import ly.img.editor.base.dock.options.crop.CropBottomSheetContent
import ly.img.editor.base.dock.options.crop.createCropUiState
import ly.img.editor.base.dock.options.effect.EffectUiState
import ly.img.editor.base.dock.options.fillstroke.FillStrokeUiState
import ly.img.editor.base.dock.options.format.createFormatUiState
import ly.img.editor.base.dock.options.layer.createLayerUiState
import ly.img.editor.base.dock.options.reorder.ReorderBottomSheetContent
import ly.img.editor.base.dock.options.shapeoptions.createShapeOptionsUiState
import ly.img.editor.base.dock.options.volume.VolumeBottomSheetContent
import ly.img.editor.base.dock.options.volume.VolumeUiState
import ly.img.editor.base.engine.CROP_EDIT_MODE
import ly.img.editor.base.engine.TEXT_EDIT_MODE
import ly.img.editor.base.engine.TOUCH_ACTION_SCALE
import ly.img.editor.base.engine.TOUCH_ACTION_ZOOM
import ly.img.editor.base.engine.TRANSFORM_EDIT_MODE
import ly.img.editor.base.engine.addOutline
import ly.img.editor.base.engine.delete
import ly.img.editor.base.engine.duplicate
import ly.img.editor.base.engine.isPlaceholder
import ly.img.editor.base.engine.resetHistory
import ly.img.editor.base.engine.setFillType
import ly.img.editor.base.engine.showOutline
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.zoomToPage
import ly.img.editor.base.engine.zoomToSelectedText
import ly.img.editor.base.migration.EditorMigrationHelper
import ly.img.editor.base.rootdock.RootDockItemData
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.ui.handler.appearanceEvents
import ly.img.editor.base.ui.handler.blockEvents
import ly.img.editor.base.ui.handler.blockFillEvents
import ly.img.editor.base.ui.handler.cropEvents
import ly.img.editor.base.ui.handler.shapeOptionEvents
import ly.img.editor.base.ui.handler.strokeEvents
import ly.img.editor.base.ui.handler.textBlockEvents
import ly.img.editor.base.ui.handler.timelineEvents
import ly.img.editor.base.ui.handler.volumeEvents
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.dpToCanvasUnit
import ly.img.editor.core.ui.engine.getCamera
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.getScene
import ly.img.editor.core.ui.engine.isSceneModeVideo
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.library.AppearanceLibraryCategory
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.UnstableEngineApi
import kotlin.math.abs

@OptIn(FlowPreview::class)
abstract class EditorUiViewModel(
    private val baseUri: Uri,
    private val onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    private val onExport: suspend (Engine, EditorEventHandler) -> Unit,
    private val onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit,
    private val onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit,
    protected val colorPalette: List<Color> = fillAndStrokeColors,
) : ViewModel(),
    EditorEventHandler {
    private val migrationHelper = EditorMigrationHelper()
    val engine = Environment.getEngine()
    protected var timelineState: TimelineState? = null

    private var firstLoad = true
    private var uiInsets = Rect.Zero
    protected val defaultInsets: Rect
        get() {
            return uiInsets.translate(horizontalPageInset, verticalPageInset)
        }

    private var canvasHeight = 0f

    private var isStraighteningOrRotating = false

    private val _isPreviewMode = MutableStateFlow(false)
    private val _isExporting = MutableStateFlow(false)
    private val _enableHistory = MutableStateFlow(true)

    protected val _updateBlock = MutableStateFlow(false)
    protected val selectedBlock = MutableStateFlow<Block?>(null)
    protected val isKeyboardShowing = MutableStateFlow(false)
    protected val pageIndex = MutableStateFlow(0)
    protected val isZoomedIn = MutableStateFlow(false)
    protected val _isSceneLoaded = MutableStateFlow(false)

    protected var inPortraitMode = true
    protected var currentInsets = Rect.Zero

    protected val _uiState = MutableStateFlow(EditorUiViewState())

    private val _uiEvent = Channel<SingleEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val externalEventChannel = Channel<EditorEvent>()
    val externalEvent = externalEventChannel.receiveAsFlow()

    private var bottomSheetHeight: Float = 0F
    private var closingSheetContent: BottomSheetContent? = null
    private val _bottomSheetContent = MutableStateFlow<BottomSheetContent?>(null)
    val bottomSheetContent = _bottomSheetContent.asStateFlow()

    private val _canvasActionMenuUiState = MutableStateFlow<CanvasActionMenuUiState?>(null)
    val canvasActionMenuUiState = _canvasActionMenuUiState.asStateFlow()

    private val _historyChangeTrigger = MutableSharedFlow<Unit>()
    protected val historyChangeTrigger = _historyChangeTrigger

    private val thumbnailGenerationJobs: MutableMap<DesignBlock, Job?> = mutableMapOf()
    private var pagesSessionId = 0

    init {
        with(viewModelScope) {
            launch {
                // Reset bottomSheetContent
                // The debounce is added to avoid the situation when another block is selected
                // This is needed because there is no other way currently to figure out when the bottom sheet has been dismissed by dragging
                val swipeableState = _uiState.value.bottomSheetState.swipeableState
                snapshotFlow { swipeableState.offset }.debounce(16).collect {
                    if (swipeableState.offset == swipeableState.maxOffset) {
                        onSheetDismiss()
                    }
                }
            }
        }
    }

    private val eventHandler =
        EventsHandler {
            cropEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            blockEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            textBlockEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            strokeEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            blockFillEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            shapeOptionEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            appearanceEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            volumeEvents(
                engine = ::engine,
                block = ::requireDesignBlockForEvents,
            )
            timelineEvents(
                engine = ::engine,
                timelineState = {
                    requireNotNull(timelineState)
                },
                showError = {
                    sendSingleEvent(SingleEvent.Error(it))
                },
            )
            editorEvents()
        }

    private fun EventsHandler.editorEvents() {
        register<Event.OnError> { onError(it.throwable) }
        register<Event.OnBackPress> { onBackPress() }
        register<Event.OnBack> { onBack() }
        register<Event.OnCloseDock> { engine.deselectAllBlocks() }
        register<Event.OnAddLibraryClick> { openLibrarySheet() }
        register<Event.OnAddLibraryCategoryClick> { onAddLibraryCategoryClick(it.libraryCategory, it.addToBackgroundTrack) }
        register<Event.OnExpandSheet> { sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Expanded)) }
        register<Event.OnHideSheet> { sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Hidden)) }
        register<Event.OnExportClick> { exportScene() }
        register<Event.OnRedoClick> { engine.editor.redo() }
        register<Event.OnUndoClick> { engine.editor.undo() }
        register<Event.OnTogglePreviewMode> { togglePreviewMode(it.isChecked) }
        register<Event.OnOptionClick> { setOptionType(it.optionType) }
        register<Event.OnReorder> { onReorder() }
        register<Event.OnCanvasMove> { onCanvasMove(it.move) }
        register<Event.OnCanvasTouch> { updateZoomState() }
        register<Event.OnResetZoom> {
            if (_isSceneLoaded.value) {
                zoom(zoomToPage = true)
            }
        }
        register<Event.OnKeyboardClose> { onKeyboardClose() }
        register<Event.OnLoadScene> { loadScene(it.height, it.insets, it.inPortraitMode) }
        register<Event.OnUpdateBottomInset> { onUpdateBottomInset(it.bottomInset, it.zoom, it.isExpanding) }
        register<Event.EnableHistory> { event -> _enableHistory.update { event.enable } }
        register<Event.OnBottomSheetHeightChange> { onBottomSheetHeightChange(it.heightInDp, it.showTimeline) }
        register<Event.OnKeyboardHeightChange> { onKeyboardHeightChange(it.heightInDp) }
        register<Event.OnPause> { if (engine.isSceneModeVideo) timelineState?.playerState?.pause() }
        register<Event.OnPage> { setPage(it.page) }
        register<Event.OnNextPage> { setPage(pageIndex.value + 1) }
        register<Event.OnPreviousPage> { setPage(pageIndex.value - 1) }
        register<Event.OnAddPage> { onAddPage(it.index) }
        register<Event.OnTogglePagesMode> { onTogglePagesMode() }
        register<Event.OnPagesModePageSelectionChange> { onPagesSelectionChange(it.page) }
        register<Event.OnPagesModePageBind> { onPagesModePageBind(it.page, it.pageHeight) }
    }

    private fun updateVisiblePageState() {
        val pages = engine.scene.getPages()
        val page = engine.scene.getCurrentPage()
        val currentVisible = pages.indexOfFirst { page == it }
        if (pageIndex.value != currentVisible) {
            setPage(index = currentVisible)
        }
    }

    open fun onEvent(event: Event) {
        // TODO: remove this when a better solution is found.
        if (event is BlockEvent && bottomSheetContent.value is CropBottomSheetContent && event != BlockEvent.OnResetCrop) {
            isStraighteningOrRotating = true
        }
        eventHandler.handleEvent(event)
    }

    override fun send(event: EditorEvent) {
        viewModelScope.launch {
            externalEventChannel.send(event)
        }
    }

    override fun sendCloseEditorEvent(throwable: Throwable?) {
        sendSingleEvent(SingleEvent.Exit(throwable))
    }

    protected open fun getBlockForEvents(): Block? = selectedBlock.value

    private fun requireDesignBlockForEvents(): DesignBlock = checkNotNull(getBlockForEvents()?.designBlock)

    protected open fun getSelectedBlock(): DesignBlock? = engine.block.findAllSelected().firstOrNull()

    protected open fun setSelectedBlock(block: Block?) {
        selectedBlock.update { block }
    }

    override fun sendCancelExportEvent() {
        exportJob?.cancel()
    }

    protected open fun onCanvasMove(move: Boolean) {
        setCanvasActionMenuState(show = !move)
    }

    protected open fun hasUnsavedChanges(): Boolean = _uiState.value.isUndoEnabled

    protected open fun updateBottomSheetUiState() {
        _bottomSheetContent.value ?: return
        val block = getBlockForEvents() ?: return
        val designBlock = block.designBlock
        // In the case when a block is deleted, the block is unselected after receiving the delete event
        if (!engine.block.isValid(designBlock)) return
        setBottomSheetContent {
            when (it) {
                is LayerBottomSheetContent -> LayerBottomSheetContent(createLayerUiState(designBlock, engine))
                is OptionsBottomSheetContent ->
                    OptionsBottomSheetContent(
                        createShapeOptionsUiState(designBlock, engine),
                    )

                is FillStrokeBottomSheetContent -> {
                    _updateBlock.update { !it }
                    FillStrokeBottomSheetContent(
                        FillStrokeUiState.create(block, engine, colorPalette),
                    )
                }

                is AdjustmentSheetContent -> {
                    _updateBlock.update { !it }
                    AdjustmentSheetContent(
                        AdjustmentUiState.create(block, engine),
                    )
                }

                is EffectSheetContent -> {
                    _updateBlock.update { !it }
                    EffectSheetContent(
                        EffectUiState.create(block, engine, it.uiState.libraryCategory),
                    )
                }

                is FormatBottomSheetContent ->
                    FormatBottomSheetContent(
                        createFormatUiState(designBlock, engine),
                    )

                is CropBottomSheetContent -> {
                    val useOldScaleRatio = isStraighteningOrRotating
                    isStraighteningOrRotating = false
                    CropBottomSheetContent(
                        createCropUiState(designBlock, engine, if (useOldScaleRatio) it.uiState.cropScaleRatio else null),
                    )
                }

                is VolumeBottomSheetContent -> {
                    VolumeBottomSheetContent(
                        VolumeUiState.create(designBlock, engine),
                    )
                }

                else -> {
                    it
                }
            }
        }
    }

    protected open fun getRootDockItems(assetLibrary: AssetLibrary): List<RootDockItemData> = emptyList()

    protected open fun showPage(index: Int) {
        engine.showPage(index)
    }

    protected open fun setPage(index: Int) {
        if (index == pageIndex.value) return
        pageIndex.update { index }
        showPage(index)
        setBottomSheetContent { null }
    }

    protected fun setPageIndex(index: Int) {
        if (index == pageIndex.value) return
        pageIndex.update { index }
    }

    protected open fun handleBackPress(): Boolean =
        if ((_uiState.value.bottomSheetState.swipeableState.offset ?: Float.MAX_VALUE)
            < _uiState.value.bottomSheetState.swipeableState.maxOffset
        ) {
            sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Hidden))
            true
        } else if (selectedBlock.value != null) {
            engine.deselectAllBlocks()
            true
        } else if (_uiState.value.pagesState != null) {
            onTogglePagesMode()
            true
        } else {
            false
        }

    private var lastKnownBottomInsetBeforeSheetOp = 0f

    protected fun setBottomSheetContent(function: (BottomSheetContent?) -> BottomSheetContent?) {
        val currentInsetsBottom = currentInsets.bottom
        val oldBottomSheetContent = bottomSheetContent.value
        val newValue = function(oldBottomSheetContent)
        if (newValue == null && oldBottomSheetContent != null && bottomSheetHeight > 0F) {
            // Means it is closing
            closingSheetContent = _bottomSheetContent.value
        }
        _bottomSheetContent.value = newValue
        if (newValue == null) {
            lastKnownBottomInsetBeforeSheetOp = 0f
        } else if (oldBottomSheetContent == null) {
            lastKnownBottomInsetBeforeSheetOp = currentInsetsBottom
        }
        setCanvasActionMenuState(show = bottomSheetContent.value == null)
    }

    private fun onSheetDismiss() {
        setOptionType(null)
        if (engine.isEngineRunning().not()) return
        if (engine.editor.getEditMode() == CROP_EDIT_MODE) {
            engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
            zoom(bottomInset = 0F)
        }
    }

    private fun onZoomFinish() {
        setCanvasActionMenuState()
        updateZoomState()
    }

    protected open val horizontalPageInset: Float = DEFAULT_PAGE_INSET

    protected open val verticalPageInset: Float = DEFAULT_PAGE_INSET

    private var initiallySetEditorSelectGlobalScope = GlobalScope.DEFER

    private fun loadScene(
        height: Float,
        insets: Rect,
        inPortraitMode: Boolean,
    ) {
        uiInsets = insets
        canvasHeight = height
        this.inPortraitMode = inPortraitMode
        val isConfigChange = !firstLoad
        firstLoad = false
        if (isConfigChange) {
            lastKnownBottomInset = 0f
            if (_isPreviewMode.value) {
                enablePreviewMode()
            } else {
                enableEditMode()
            }
        } else {
            viewModelScope.launch {
                engine.scene
                    .onActiveChanged()
                    .onEach { onSceneLoaded() }
                    .collect()
            }
            viewModelScope.launch {
                runCatching {
                    migrationHelper.migrate()
                    onPreCreate()
                    // Make sure to set all settings before calling `onCreate` so that the consumer can change them if needed!
                    onCreate(engine, this@EditorUiViewModel)
                    initiallySetEditorSelectGlobalScope = engine.editor.getGlobalScope(Scope.EditorSelect)
                    val scene = requireNotNull(engine.scene.get()) { "onCreate body must contain scene creation." }
                    if (engine.isSceneModeVideo) {
                        timelineState = TimelineState(engine, viewModelScope)
                    }
                    engine.addOutline(scene, engine.getPage(pageIndex.value))
                    engine.showOutline(false)
                    showPage(pageIndex.value)
                    enableEditMode().join()
                    engine.resetHistory()
                }.onSuccess {
                    _isSceneLoaded.update { true }
                }.onFailure {
                    onError(it)
                }
            }

            observeSelectedBlock()
            observeHistory()
            observeUiStateChanges()
            observeEvents()
            observeEditorStateChange()
        }
    }

    private fun openLibrarySheet() {
        timelineState?.playerState?.pause()
        setBottomSheetContent { LibraryBottomSheetContent }
    }

    private fun onAddLibraryCategoryClick(
        libraryCategory: LibraryCategory,
        addToBackgroundTrack: Boolean?,
    ) {
        setBottomSheetContent { LibraryCategoryBottomSheetContent(libraryCategory, addToBackgroundTrack) }
    }

    private fun onBottomSheetHeightChange(
        heightInDp: Float,
        showTimeline: Boolean,
    ) {
        // we don't want to change zoom level for Library
        if (_isPreviewMode.value || !_isSceneLoaded.value) return
        bottomSheetHeight = heightInDp
        val closingSheetContent = this.closingSheetContent
        if (heightInDp == 0F && closingSheetContent != null) {
            this.closingSheetContent = null
        }
        val bottomSheetContent = bottomSheetContent.value
        if (bottomSheetContent is LibraryBottomSheetContent || bottomSheetContent is LibraryCategoryBottomSheetContent) return
        if (closingSheetContent is LibraryBottomSheetContent || closingSheetContent is LibraryCategoryBottomSheetContent) return
        zoom(heightInDp, showTimeline)
    }

    private fun onKeyboardClose() {
        engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
        zoom(bottomInset = 0F)
    }

    private fun onKeyboardHeightChange(heightInDp: Float) {
        zoom(heightInDp)
    }

    protected open fun onSceneLoaded() {
        engine.deselectAllBlocks()
    }

    private fun onAddPage(index: Int) {
        val currentPage = engine.scene.getCurrentPage() ?: return
        val parent = engine.block.getParent(currentPage) ?: return
        val newPage = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(newPage, engine.block.getWidth(currentPage))
        engine.block.setHeight(newPage, engine.block.getHeight(currentPage))
        engine.block.setFillType(newPage, FillType.Color)
        engine.block.insertChild(parent, newPage, index)
        engine.editor.addUndoStep()
    }

    private fun onTogglePagesMode() {
        if (_uiState.value.pagesState != null) {
            // Cancel all thumb generation jobs
            _uiState.update {
                it.copy(pagesState = null)
            }
            onStopGenerateAllPageThumbnails()
            setCanvasActionMenuState(show = true)
        } else {
            engine.deselectAllBlocks()
            setCanvasActionMenuState(show = false)
            updateEditorPagesState()
        }
    }

    private fun updateEditorPagesState() {
        _uiState.update {
            val pagesState =
                it.pagesState
                    ?.copy(engine, markThumbnails = true)
                    ?: createEditorPagesState(pagesSessionId++, engine, pageIndex.value)
            setPage(pagesState.selectedPageIndex)
            it.copy(pagesState = pagesState)
        }
    }

    private fun onPagesSelectionChange(page: EditorPagesState.Page) {
        _uiState.update {
            if (page == it.pagesState?.selectedPage) {
                it
            } else {
                it.copy(pagesState = it.pagesState?.copy(engine, selectedPage = page, markThumbnails = false))
            }
        }
    }

    private fun onPagesModePageBind(
        page: EditorPagesState.Page,
        pageHeight: Int,
    ) {
        if (page.mark.not()) return
        if (pageHeight <= 0) return
        thumbnailGenerationJobs[page.block] =
            viewModelScope.launch {
                val result =
                    runCatching {
                        engine.block
                            .generateVideoThumbnailSequence(
                                block = page.block,
                                thumbnailHeight = pageHeight,
                                timeBegin = 0.0,
                                timeEnd = 0.1,
                                numberOfFrames = 1,
                            ).firstOrNull()
                    }.getOrNull() ?: return@launch
                val bitmap =
                    withContext(Dispatchers.Default) {
                        Bitmap.createBitmap(result.width, result.height, Bitmap.Config.ARGB_8888).also {
                            it.copyPixelsFromBuffer(result.imageData)
                        }
                    }
                val newPage =
                    page.copy(mark = false).also {
                        it.thumbnail = bitmap
                    }
                _uiState.update {
                    it.copy(pagesState = it.pagesState?.copy(updatedPage = newPage))
                }
            }
    }

    private fun onStopGenerateAllPageThumbnails() {
        val iterator = thumbnailGenerationJobs.iterator()
        while (iterator.hasNext()) {
            iterator.next().value?.cancel()
            iterator.remove()
        }
    }

    private fun zoom(
        bottomInset: Float,
        showTimeline: Boolean = false,
    ) {
        val realBottomInset = bottomInset + verticalPageInset
        if (realBottomInset <= defaultInsets.bottom && currentInsets.bottom == defaultInsets.bottom) return
        val coercedBottomInset = if (showTimeline && engine.isSceneModeVideo) lastKnownBottomInsetBeforeSheetOp else defaultInsets.bottom
        zoom(defaultInsets.copy(bottom = realBottomInset.coerceAtLeast(coercedBottomInset)))
    }

    // FIXME: Saving the last known bottom inset is needed because currently there are excessive re-composition
    // An ideal fix would be to get rid of the unnecessary recompositions so this function isn't called unnecessarily
    private var lastKnownBottomInset = 0f

    private fun onUpdateBottomInset(
        bottomInset: Float,
        zoom: Boolean,
        isExpanding: Boolean,
    ) {
        if (bottomInset != lastKnownBottomInset) {
            lastKnownBottomInset = bottomInset
            val realBottomInset = bottomInset + verticalPageInset
            uiInsets =
                if (isExpanding) {
                    uiInsets.copy(bottom = realBottomInset.coerceAtLeast(lastKnownBottomInsetBeforeSheetOp))
                } else {
                    uiInsets.copy(bottom = realBottomInset)
                }
            if (zoom) {
                zoom(zoomToPage = true)
            }
        }
    }

    private var zoomJob: Job? = null
    private var fitToPageZoomLevel = 0f

    @OptIn(UnstableEngineApi::class)
    protected fun zoom(
        insets: Rect = defaultInsets,
        zoomToPage: Boolean = false,
        clampOnly: Boolean = false,
    ): Job {
        zoomJob?.cancel()
        currentInsets = insets
        return viewModelScope
            .launch {
                if (_uiState.value.isInPreviewMode) {
                    preEnterPreviewMode()
                    enterPreviewMode()
                } else {
                    val page = engine.getPage(pageIndex.value)
                    val selectedBlock = selectedBlock.value
                    val shouldZoomToPage =
                        engine.isSceneModeVideo ||
                            (!clampOnly && (zoomToPage || selectedBlock == null)) ||
                            engine.scene.getZoomLevel() == fitToPageZoomLevel
                    val blocks =
                        buildList {
                            add(page)
                            if (engine.editor.getEditMode() == TEXT_EDIT_MODE && selectedBlock?.type == BlockType.Text) {
                                add(selectedBlock.designBlock)
                            }
                        }

                    engine.scene.enableCameraPositionClamping(
                        blocks = blocks,
                        paddingLeft = currentInsets.left - horizontalPageInset,
                        paddingTop = currentInsets.top - verticalPageInset,
                        paddingRight = currentInsets.right - horizontalPageInset,
                        paddingBottom = currentInsets.bottom - verticalPageInset,
                        scaledPaddingLeft = horizontalPageInset,
                        scaledPaddingTop = verticalPageInset,
                        scaledPaddingRight = horizontalPageInset,
                        scaledPaddingBottom = verticalPageInset,
                    )

                    if (shouldZoomToPage) {
                        engine.scene.enableCameraZoomClamping(
                            blocks = blocks,
                            minZoomLimit = 1.0F,
                            maxZoomLimit = 5.0F,
                            paddingLeft = currentInsets.left,
                            paddingTop = currentInsets.top,
                            paddingRight = currentInsets.right,
                            paddingBottom = currentInsets.bottom,
                        )
                        engine.zoomToPage(pageIndex.value, currentInsets)
                        fitToPageZoomLevel = engine.scene.getZoomLevel()
                    }

                    val selectedDesignBlock = selectedBlock?.designBlock

                    if (selectedDesignBlock != null && !shouldZoomToPage && engine.editor.getEditMode() != TEXT_EDIT_MODE) {
                        // The delay acts as a debouncing mechanism.
                        delay(8)

                        val boundingBoxRect = engine.block.getScreenSpaceBoundingBoxRect(listOf(selectedDesignBlock))
                        val bottomSheetTop = canvasHeight - currentInsets.bottom
                        val camera = engine.getCamera()
                        val oldCameraPosX = engine.block.getPositionX(camera)
                        val oldCameraPosY = engine.block.getPositionY(camera)
                        var newCameraPosX = oldCameraPosX
                        var newCameraPosY = oldCameraPosY
                        val canvasWidthDp =
                            engine.block.getFloat(camera, "camera/resolution/width") /
                                engine.block.getFloat(camera, "camera/pixelRatio")
                        val selectedBlockCenterX = boundingBoxRect.centerX()

                        if (selectedBlockCenterX > canvasWidthDp) {
                            newCameraPosX = oldCameraPosX +
                                engine.dpToCanvasUnit(
                                    (canvasWidthDp / 2 - boundingBoxRect.width() / 2) + (boundingBoxRect.right - canvasWidthDp),
                                )
                        } else if (selectedBlockCenterX < 0) {
                            newCameraPosX = oldCameraPosX -
                                engine.dpToCanvasUnit(
                                    (canvasWidthDp / 2 - boundingBoxRect.width() / 2) - boundingBoxRect.left,
                                )
                        }

                        // bottom sheet is covering more than 50% of selected block
                        if (bottomSheetTop < boundingBoxRect.centerY()) {
                            newCameraPosY = oldCameraPosY + engine.dpToCanvasUnit(48 + boundingBoxRect.bottom - bottomSheetTop)
                        } else if (boundingBoxRect.centerY() < 64) {
                            newCameraPosY = oldCameraPosY - engine.dpToCanvasUnit(48 + bottomSheetTop - boundingBoxRect.bottom)
                        }

                        if (newCameraPosX != oldCameraPosX || newCameraPosY != oldCameraPosY) {
                            engine.overrideAndRestore(camera, Scope.LayerMove) {
                                engine.block.setPositionX(it, newCameraPosX)
                                engine.block.setPositionY(it, newCameraPosY)
                            }
                        }
                    }
                    if (engine.editor.getEditMode() == TEXT_EDIT_MODE) {
                        zoomToText()
                    }
                }
                onZoomFinish()
            }.also {
                zoomJob = it
            }
    }

    private fun zoomToText(insets: Rect = currentInsets) {
        engine.zoomToSelectedText(insets, canvasHeight)
    }

    // This will never get initialised for non video scenes.
    private val clampPlayheadExclusionSet by lazy {
        setOf(
            OptionType.EnterGroup,
            OptionType.Split,
            OptionType.Reorder,
            OptionType.Delete,
            OptionType.AttachBackground,
            OptionType.DetachBackground,
        )
    }

    private fun onReorder() {
        timelineState?.playerState?.pause()
        setBottomSheetContent {
            ReorderBottomSheetContent(
                checkNotNull(timelineState),
            )
        }
    }

    private fun setOptionType(optionType: OptionType?) {
        if (optionType == null) {
            setBottomSheetContent { null }
            return
        }
        timelineState?.playerState?.pause()

        if (engine.isSceneModeVideo && optionType !in clampPlayheadExclusionSet) {
            timelineState?.clampPlayheadPositionToSelectedClip()
        }

        val block = getBlockForEvents() ?: return
        val designBlock = block.designBlock
        setBottomSheetContent {
            when (optionType) {
                OptionType.Replace -> ReplaceBottomSheetContent(designBlock, block.type)
                OptionType.Layer -> LayerBottomSheetContent(createLayerUiState(designBlock, engine))
                OptionType.Edit -> {
                    engine.editor.setEditMode(TEXT_EDIT_MODE)
                    null
                }

                OptionType.Format ->
                    FormatBottomSheetContent(
                        createFormatUiState(designBlock, engine),
                    )

                OptionType.ShapeOptions ->
                    OptionsBottomSheetContent(
                        createShapeOptionsUiState(designBlock, engine),
                    )

                OptionType.FillStroke ->
                    FillStrokeBottomSheetContent(
                        FillStrokeUiState.create(block, engine, colorPalette),
                    )

                OptionType.EnterGroup -> {
                    engine.block.enterGroup(designBlock)
                    null
                }

                OptionType.SelectGroup -> {
                    engine.block.exitGroup(designBlock)
                    null
                }

                OptionType.Crop -> {
                    engine.block.setScopeEnabled(designBlock, Scope.EditorSelect, enabled = true)
                    engine.block.setSelected(designBlock, selected = true)
                    engine.editor.setEditMode(CROP_EDIT_MODE)
                    null
                }

                OptionType.Reorder -> {
                    ReorderBottomSheetContent(
                        checkNotNull(timelineState),
                    )
                }

                OptionType.Adjustments ->
                    AdjustmentSheetContent(
                        AdjustmentUiState.create(block, engine),
                    )

                OptionType.Filter ->
                    EffectSheetContent(
                        EffectUiState.create(block, engine, AppearanceLibraryCategory.Filters),
                    )

                OptionType.Effect ->
                    EffectSheetContent(
                        EffectUiState.create(block, engine, AppearanceLibraryCategory.FxEffects),
                    )

                OptionType.Blur ->
                    EffectSheetContent(
                        EffectUiState.create(block, engine, AppearanceLibraryCategory.Blur),
                    )

                OptionType.Volume ->
                    VolumeBottomSheetContent(
                        VolumeUiState.create(designBlock, engine),
                    )

                OptionType.Split -> {
                    eventHandler.handleEvent(BlockEvent.OnSplit)
                    null
                }

                OptionType.Delete -> {
                    engine.delete(designBlock)
                    null
                }

                OptionType.Duplicate -> {
                    engine.duplicate(designBlock)
                    null
                }

                OptionType.AttachBackground, OptionType.DetachBackground -> {
                    eventHandler.handleEvent(BlockEvent.OnToggleBackgroundTrackAttach)
                    // We need to update the option items of the selected block to change from attach BG to detach BG or vice-versa
                    _updateBlock.update { !it }
                    null
                }
            }
        }
    }

    private fun observeUiStateChanges() {
        viewModelScope.launch {
            merge(
                historyChangeTrigger,
                _isSceneLoaded,
                _isPreviewMode,
                _isExporting,
                _enableHistory,
                selectedBlock,
                isKeyboardShowing,
            ).collect {
                val pageCount = if (_isSceneLoaded.value) engine.scene.getPages().size else 0
                _uiState.update {
                    it.copy(
                        isCanvasVisible = _isSceneLoaded.value,
                        isInPreviewMode = _isPreviewMode.value,
                        allowEditorInteraction = !_isPreviewMode.value,
                        isUndoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canUndo(),
                        isRedoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canRedo(),
                        selectedBlock = selectedBlock.value,
                        isEditingText = isKeyboardShowing.value,
                        timelineState = timelineState,
                        pageCount = pageCount,
                        rootDockItems =
                            if (_isSceneLoaded.value) {
                                getRootDockItems(requireNotNull(Environment.assetLibrary))
                            } else {
                                emptyList()
                            },
                    )
                }
            }
        }
    }

    private fun updateZoomState() {
        val newZoomState = _isSceneLoaded.value && (abs(engine.scene.getZoomLevel() - fitToPageZoomLevel) > 0.001f)
        if (newZoomState != isZoomedIn.value) {
            isZoomedIn.update { newZoomState }
        }
    }

    private var cursorPos = 0f

    private fun observeEvents() {
        var visibleAtCurrentPlaybackTimeFlag: Boolean? = null
        viewModelScope.launch {
            engine.event.subscribe().collect {
                timelineState?.refresh(it)

                // update canvas action menu if visibility of block has changed
                selectedBlock.value?.designBlock?.let {
                    if (engine.block.isValid(it)) {
                        val visibleAtCurrentPlaybackTime = engine.block.isVisibleAtCurrentPlaybackTime(it)
                        if (visibleAtCurrentPlaybackTime != visibleAtCurrentPlaybackTimeFlag) {
                            visibleAtCurrentPlaybackTimeFlag = visibleAtCurrentPlaybackTime
                            setCanvasActionMenuState(show = visibleAtCurrentPlaybackTime)
                        }
                    } else {
                        visibleAtCurrentPlaybackTimeFlag = null
                    }
                } ?: {
                    visibleAtCurrentPlaybackTimeFlag = null
                }

                // text handling
                if (engine.editor.getEditMode() != TEXT_EDIT_MODE) return@collect
                val textCursorPositionInScreenSpaceY = engine.editor.getTextCursorPositionInScreenSpaceY()
                if (textCursorPositionInScreenSpaceY != cursorPos) {
                    cursorPos = textCursorPositionInScreenSpaceY
                    zoomToText()
                }
            }
        }
    }

    protected open fun onEditModeChanged(editMode: String) = Unit

    private fun observeEditorStateChange() {
        var flag = false
        viewModelScope.launch {
            engine.editor.onStateChanged().map { engine.editor.getEditMode() }.distinctUntilChanged().collect { editMode ->
                onEditModeChanged(editMode)
                when (editMode) {
                    TEXT_EDIT_MODE -> setBottomSheetContent { null }
                    CROP_EDIT_MODE -> {
                        engine.editor.setSettingEnum("touch/pinchAction", TOUCH_ACTION_SCALE)
                        setBottomSheetContent {
                            CropBottomSheetContent(createCropUiState(engine.block.findAllSelected().single(), engine))
                        }
                    }

                    else -> {
                        // Close crop bottom sheet if coming back from crop mode
                        if (bottomSheetContent.value is CropBottomSheetContent) {
                            setBottomSheetContent { null }
                        }

                        // restore pinchAction, for video scenes, it is already set to scale
                        if (!engine.isSceneModeVideo) {
                            engine.editor.setSettingEnum("touch/pinchAction", TOUCH_ACTION_ZOOM)
                        }

                        setCanvasActionMenuState(show = timelineState?.playerState?.isPlaying?.not() ?: true)
                        if (!flag) {
                            flag = true
                            zoom(zoomToPage = true)
                        }
                    }
                }
                val showKeyboard = editMode == TEXT_EDIT_MODE
                if (isKeyboardShowing.value && showKeyboard.not()) {
                    zoom(bottomInset = 0F)
                }
                isKeyboardShowing.update { showKeyboard }
            }
        }
    }

    private fun observeSelectedBlock() {
        viewModelScope.launch {
            // filter is added, because page dock becomes visible while postcard UI is still loading
            merge(engine.block.onSelectionChanged(), _updateBlock).filter { _isSceneLoaded.value }.collect {
                val block = getSelectedBlock()?.let { createBlock(it, engine) }
                val oldBlock = getBlockForEvents()?.designBlock
                if (oldBlock != null && block == null) {
                    zoom(clampOnly = true)
                }
                // Even if the block is the same, this will update the fill/stroke color in the dock option
                setSelectedBlock(block)
                if (oldBlock != block?.designBlock) {
                    if (block != null && engine.isPlaceholder(block.designBlock)) {
                        setOptionType(OptionType.Replace)
                    } else if (block == null || bottomSheetContent.value != null) {
                        setOptionType(null)
                    } else {
                        setCanvasActionMenuState()
                    }
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            engine.editor.onHistoryUpdated().collect {
                _historyChangeTrigger.emit(Unit)
                timelineState?.onHistoryUpdated()
                updateVisiblePageState()
                updateBottomSheetUiState()
                setCanvasActionMenuState()
                if (_uiState.value.pagesState != null) {
                    updateEditorPagesState()
                }
            }
        }
    }

    private fun onBack() {
        viewModelScope.launch {
            onClose(engine, hasUnsavedChanges(), this@EditorUiViewModel)
        }
    }

    private fun onError(throwable: Throwable) {
        viewModelScope.launch {
            onError(throwable, engine, this@EditorUiViewModel)
        }
    }

    private fun onBackPress() {
        if (!handleBackPress()) {
            onBack()
        }
    }

    private fun enableEditMode(): Job {
        _isPreviewMode.update { false }
        engine.editor.setGlobalScope(Scope.EditorSelect, initiallySetEditorSelectGlobalScope)
        enterEditMode()
        return zoom(zoomToPage = true)
    }

    private fun enablePreviewMode() {
        _isPreviewMode.update { true }
        setBottomSheetContent { null }
        engine.editor.setGlobalScope(Scope.EditorSelect, GlobalScope.DENY)
        enterPreviewMode()
        zoom(defaultInsets.copy(bottom = verticalPageInset))
    }

    private fun togglePreviewMode(previewMode: Boolean) {
        if (previewMode) {
            enablePreviewMode()
        } else {
            enableEditMode()
        }
    }

    private var exportJob: Job? = null

    private fun exportScene() {
        if (_isExporting.compareAndSet(expect = false, update = true)) {
            timelineState?.playerState?.pause()
            viewModelScope.launch {
                exportJob =
                    launch {
                        onExport(engine, this@EditorUiViewModel)
                    }
                exportJob?.join()
                _isExporting.update { false }
            }
        }
    }

    private fun setCanvasActionMenuState(show: Boolean = true) {
        val block = selectedBlock.value?.designBlock
        val isVisible = !isKeyboardShowing.value && bottomSheetContent.value == null && show
        _canvasActionMenuUiState.update {
            if (block == null) {
                null
            } else if (!isVisible) {
                it?.copy(show = false)
            } else {
                createCanvasActionMenuUiState(
                    designBlock = block,
                    currentInsets = currentInsets,
                    engine = engine,
                )
            }
        }
    }

    private fun sendSingleEvent(event: SingleEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    override fun onCleared() {
        engine.stop()
        Environment.clear()
    }

    abstract fun enterEditMode()

    abstract fun enterPreviewMode()

    @OptIn(UnstableEngineApi::class)
    open fun preEnterPreviewMode() {
        val scene = engine.getScene()
        if (engine.scene.isCameraPositionClampingEnabled(scene)) {
            engine.scene.disableCameraPositionClamping()
        }
        if (engine.scene.isCameraZoomClampingEnabled(scene)) {
            engine.scene.disableCameraZoomClamping()
        }
    }

    open fun onPreCreate() {
        setSettingsForEditorUi(engine, baseUri)
    }

    private companion object {
        const val DEFAULT_PAGE_INSET = 16F
    }
}
