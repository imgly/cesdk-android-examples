package ly.img.editor.base.ui

import android.net.Uri
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
import ly.img.editor.base.dock.options.shapeoptions.createShapeOptionsUiState
import ly.img.editor.base.engine.CROP_EDIT_MODE
import ly.img.editor.base.engine.TEXT_EDIT_MODE
import ly.img.editor.base.engine.TOUCH_ACTION_SCALE
import ly.img.editor.base.engine.TOUCH_ACTION_ZOOM
import ly.img.editor.base.engine.TRANSFORM_EDIT_MODE
import ly.img.editor.base.engine.addOutline
import ly.img.editor.base.engine.isPlaceholder
import ly.img.editor.base.engine.resetHistory
import ly.img.editor.base.engine.setRoleButPreserveGlobalScopes
import ly.img.editor.base.engine.showOutline
import ly.img.editor.base.engine.showPage
import ly.img.editor.base.engine.zoomToPage
import ly.img.editor.base.engine.zoomToSelectedText
import ly.img.editor.base.ui.handler.appearanceEvents
import ly.img.editor.base.ui.handler.blockEvents
import ly.img.editor.base.ui.handler.blockFillEvents
import ly.img.editor.base.ui.handler.cropEvents
import ly.img.editor.base.ui.handler.shapeOptionEvents
import ly.img.editor.base.ui.handler.strokeEvents
import ly.img.editor.base.ui.handler.textBlockEvents
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.dpToCanvasUnit
import ly.img.editor.core.ui.engine.getCamera
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.getScene
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.library.AppearanceLibraryCategory
import ly.img.editor.core.ui.register
import ly.img.engine.Engine
import ly.img.engine.GlobalScope
import ly.img.engine.UnstableEngineApi
import kotlin.math.abs

internal const val PAGE_MARGIN = 16f

@OptIn(FlowPreview::class)
abstract class EditorUiViewModel(
    private val baseUri: Uri,
    private val onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    private val onExport: suspend (Engine, EditorEventHandler) -> Unit,
    private val onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit,
    private val onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit,
    protected val colorPalette: List<Color> = fillAndStrokeColors,
    private val scrollablePreview: Boolean = false,
) : ViewModel(), EditorEventHandler {
    val engine = Environment.getEngine()
    protected val assetsRepo = Environment.getAssetsRepo()

    private var firstLoad = true
    private var defaultInsets = Rect.Zero
    private var canvasHeight = 0f

    private var isStraighteningOrRotating = false

    private val _isPreviewMode = MutableStateFlow(false)
    private val _isExporting = MutableStateFlow(false)
    private val _updateBlock = MutableStateFlow(false)
    private val _enableHistory = MutableStateFlow(true)

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

    private val _bottomSheetContent = MutableStateFlow<BottomSheetContent?>(null)
    val bottomSheetContent = _bottomSheetContent.asStateFlow()

    private val _canvasActionMenuUiState = MutableStateFlow<CanvasActionMenuUiState?>(null)
    val canvasActionMenuUiState = _canvasActionMenuUiState.asStateFlow()

    private val _historyChangeTrigger = Channel<Unit>()
    protected val historyChangeTrigger = _historyChangeTrigger.receiveAsFlow()

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
                block = ::getBlockForEvents,
            )
            blockEvents(
                engine = ::engine,
                block = ::getBlockForEvents,
            )
            textBlockEvents(
                engine = ::engine,
                block = ::getBlockForEvents,
                fontFamilyMap = { checkNotNull(assetsRepo.fontFamilies.value) },
            )
            strokeEvents(
                engine = ::engine,
                block = ::getBlockForEvents,
            )
            blockFillEvents(
                engine = ::engine,
                block = ::getBlockForEvents,
            )
            shapeOptionEvents(
                engine = ::engine,
                block = ::getBlockForEvents,
            )
            appearanceEvents(
                engine = ::engine,
                block = ::getBlockForEvents,
            )
            editorEvents()
        }

    private fun EventsHandler.editorEvents() {
        register<Event.OnNextPage> { setPage(pageIndex.value + 1) }
        register<Event.OnPreviousPage> { setPage(pageIndex.value - 1) }
        register<Event.OnError> { onError(it.throwable) }
        register<Event.OnBackPress> { onBackPress() }
        register<Event.OnBack> { onBack() }
        register<Event.OnCloseDock> { engine.deselectAllBlocks() }
        register<Event.OnAddLibraryClick> { openLibrarySheet() }
        register<Event.OnExpandSheet> { sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Expanded)) }
        register<Event.OnHideSheet> { sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Hidden)) }
        register<Event.OnExportClick> { exportScene() }
        register<Event.OnRedoClick> { engine.editor.redo() }
        register<Event.OnUndoClick> { engine.editor.undo() }
        register<Event.OnTogglePreviewMode> { togglePreviewMode(it.isChecked) }
        register<Event.OnOptionClick> { setOptionType(it.optionType) }
        register<Event.OnCanvasMove> { onCanvasMove(it.move) }
        register<Event.OnCanvasTouch> { updateZoomState() }
        register<Event.OnKeyboardClose> { onKeyboardClose() }
        register<Event.OnLoadScene> { loadScene(it.height, it.insets, it.inPortraitMode) }
        register<Event.EnableHistory> { event -> _enableHistory.update { event.enable } }
        register<Event.OnBottomSheetHeightChange> { onBottomSheetHeightChange(it.heightInDp) }
        register<Event.OnKeyboardHeightChange> { onKeyboardHeightChange(it.heightInDp) }
    }

    private fun updateVisiblePageState() {
        val pages = engine.scene.getPages()
        val page = engine.scene.getCurrentPage()
        val currentVisible = pages.indexOfFirst { page == it }
        if (pageIndex.value != currentVisible) {
            pageIndex.value = currentVisible
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

    override fun sendCloseEditorEvent() {
        sendSingleEvent(SingleEvent.Exit)
    }

    protected open fun getBlockForEvents() = checkNotNull(selectedBlock.value).designBlock

    protected open fun onCanvasMove(move: Boolean) {
        setCanvasActionMenuState(show = !move)
    }

    protected open fun hasUnsavedChanges(): Boolean = _uiState.value.isUndoEnabled

    protected open fun updateBottomSheetUiState() {
        val block = selectedBlock.value ?: return
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
                        createFormatUiState(designBlock, engine, checkNotNull(assetsRepo.fontFamilies.value)),
                    )

                is CropBottomSheetContent -> {
                    val useOldScaleRatio = isStraighteningOrRotating
                    isStraighteningOrRotating = false
                    CropBottomSheetContent(
                        createCropUiState(designBlock, engine, if (useOldScaleRatio) it.uiState.cropScaleRatio else null),
                    )
                }

                else -> {
                    it
                }
            }
        }
    }

    open fun setPage(index: Int) {
        if (index == pageIndex.value) return
        pageIndex.update { index }
        engine.showPage(index)
        setBottomSheetContent { null }
    }

    protected open fun handleBackPress(): Boolean {
        return if ((_uiState.value.bottomSheetState.swipeableState.offset ?: Float.MAX_VALUE)
            < _uiState.value.bottomSheetState.swipeableState.maxOffset
        ) {
            sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Hidden))
            true
        } else if (selectedBlock.value != null) {
            engine.deselectAllBlocks()
            true
        } else {
            false
        }
    }

    protected fun setBottomSheetContent(function: (BottomSheetContent?) -> BottomSheetContent?) {
        _bottomSheetContent.update(function)
        setCanvasActionMenuState(show = bottomSheetContent.value == null)
    }

    private fun onSheetDismiss() {
        setOptionType(null)
        if (engine.isEngineRunning().not()) return
        if (engine.editor.getEditMode() == CROP_EDIT_MODE) engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
    }

    private fun onZoomFinish() {
        setCanvasActionMenuState()
        updateZoomState()
    }

    private fun loadScene(
        height: Float,
        insets: Rect,
        inPortraitMode: Boolean,
    ) {
        defaultInsets = insets.translate(PAGE_MARGIN, PAGE_MARGIN)
        canvasHeight = height
        this.inPortraitMode = inPortraitMode
        val isConfigChange = !firstLoad
        firstLoad = false
        if (isConfigChange) {
            if (_isPreviewMode.value) {
                enablePreviewMode()
            } else {
                enableEditMode()
            }
        } else {
            setSettings()
            viewModelScope.launch {
                runCatching {
                    // Temporary invocation before fonts move to the asset source
                    assetsRepo.loadFonts(basePath = baseUri.toString())
                    onCreate(engine, this@EditorUiViewModel)
                    val scene = requireNotNull(engine.scene.get()) { "onCreate body must contain scene creation." }
                    engine.addOutline(scene, engine.getPage(pageIndex.value))
                    engine.showOutline(false)
                    engine.showPage(pageIndex.value)
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
        setBottomSheetContent { LibraryBottomSheetContent }
    }

    private fun onBottomSheetHeightChange(heightInDp: Float) {
        // we don't want to change zoom level for Library
        if (_isPreviewMode.value || !_isSceneLoaded.value || bottomSheetContent.value is LibraryBottomSheetContent) return
        zoom(heightInDp)
    }

    private fun onKeyboardClose() {
        engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
    }

    private fun onKeyboardHeightChange(heightInDp: Float) {
        zoom(heightInDp)
    }

    private fun zoom(bottomInset: Float) {
        val realBottomInset = bottomInset + PAGE_MARGIN
        if (realBottomInset <= defaultInsets.bottom && currentInsets.bottom == defaultInsets.bottom) return
        zoom(currentInsets.copy(bottom = realBottomInset.coerceAtLeast(defaultInsets.bottom)))
    }

    private var zoomJob: Job? = null
    private var fitToPageZoomLevel = 0f

    @OptIn(UnstableEngineApi::class)
    // todo bug: some sheets do not zoom content correctly for Design Editor, i.e. Effects.Recolor
    private fun zoom(
        insets: Rect = defaultInsets,
        zoomToPage: Boolean = false,
        clampOnly: Boolean = false,
    ): Job {
        zoomJob?.cancel()
        return viewModelScope.launch {
            currentInsets = insets
            if (_uiState.value.isInPreviewMode) {
                preEnterPreviewMode()
                if (scrollablePreview) {
                    val pages = engine.scene.getPages()
                    val firstPage = listOf(pages.first())
                    engine.scene.enableCameraZoomClamping(
                        firstPage,
                        minZoomLimit = 1.0f,
                        maxZoomLimit = 1.0f,
                        paddingLeft = defaultInsets.left,
                        paddingTop = defaultInsets.top,
                        paddingRight = defaultInsets.right,
                        paddingBottom = defaultInsets.bottom,
                    )
                    engine.scene.enableCameraPositionClamping(
                        pages,
                        paddingLeft = defaultInsets.left - PAGE_MARGIN,
                        paddingTop = defaultInsets.top - PAGE_MARGIN,
                        paddingRight = defaultInsets.right - PAGE_MARGIN,
                        paddingBottom = defaultInsets.bottom - PAGE_MARGIN,
                        scaledPaddingLeft = PAGE_MARGIN,
                        scaledPaddingTop = PAGE_MARGIN,
                        scaledPaddingRight = PAGE_MARGIN,
                        scaledPaddingBottom = PAGE_MARGIN,
                    )
                } else {
                    val scene = engine.getScene()
                    if (engine.scene.isCameraPositionClampingEnabled(scene)) {
                        engine.scene.disableCameraPositionClamping()
                    }
                    if (engine.scene.isCameraZoomClampingEnabled(scene)) {
                        engine.scene.disableCameraZoomClamping()
                    }
                }
                enterPreviewMode()
            } else {
                val page = engine.getPage(pageIndex.value)
                val selectedBlock = selectedBlock.value
                val shouldZoomToPage =
                    (!clampOnly && (zoomToPage || selectedBlock == null)) || engine.scene.getZoomLevel() == fitToPageZoomLevel
                val blocks =
                    buildList {
                        add(page)
                        if (selectedBlock?.type == BlockType.Text) {
                            add(selectedBlock.designBlock)
                        }
                    }

                if (shouldZoomToPage) {
                    engine.zoomToPage(pageIndex.value, currentInsets)
                    fitToPageZoomLevel = engine.scene.getZoomLevel()
                    engine.scene.enableCameraZoomClamping(
                        blocks,
                        minZoomLimit = 1.0f,
                        maxZoomLimit = 5.0f,
                        paddingLeft = currentInsets.left,
                        paddingTop = currentInsets.top,
                        paddingRight = currentInsets.right,
                        paddingBottom = currentInsets.bottom,
                    )
                }
                engine.scene.enableCameraPositionClamping(
                    blocks,
                    paddingLeft = currentInsets.left - PAGE_MARGIN,
                    paddingTop = currentInsets.top - PAGE_MARGIN,
                    paddingRight = currentInsets.right - PAGE_MARGIN,
                    paddingBottom = currentInsets.bottom - PAGE_MARGIN,
                    scaledPaddingLeft = PAGE_MARGIN,
                    scaledPaddingTop = PAGE_MARGIN,
                    scaledPaddingRight = PAGE_MARGIN,
                    scaledPaddingBottom = PAGE_MARGIN,
                )

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

    private fun setOptionType(optionType: OptionType?) {
        if (optionType == null) {
            setBottomSheetContent { null }
            return
        }
        val block = checkNotNull(selectedBlock.value)
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
                        createFormatUiState(designBlock, engine, checkNotNull(assetsRepo.fontFamilies.value)),
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
                    engine.editor.setEditMode(CROP_EDIT_MODE)
                    null
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
                assetsRepo.fontFamilies,
                selectedBlock,
                isKeyboardShowing,
            ).collect {
                val pageCount = if (_isSceneLoaded.value) engine.scene.getPages().size else 0
                _uiState.update {
                    it.copy(
                        isCanvasVisible = _isSceneLoaded.value,
                        isInPreviewMode = _isPreviewMode.value,
                        allowEditorInteraction = scrollablePreview || !_isPreviewMode.value,
                        isUndoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canUndo(),
                        isRedoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canRedo(),
                        selectedBlock = selectedBlock.value,
                        isEditingText = isKeyboardShowing.value,
                        pageCount = pageCount,
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
        viewModelScope.launch {
            engine.event.subscribe().collect {
                if (engine.editor.getEditMode() != TEXT_EDIT_MODE) return@collect
                val textCursorPositionInScreenSpaceY = engine.editor.getTextCursorPositionInScreenSpaceY()
                if (textCursorPositionInScreenSpaceY != cursorPos) {
                    cursorPos = textCursorPositionInScreenSpaceY
                    zoomToText()
                }
            }
        }
    }

    private fun observeEditorStateChange() {
        var flag = false
        viewModelScope.launch {
            engine.editor.onStateChanged().map { engine.editor.getEditMode() }.distinctUntilChanged().collect { editMode ->
                when (editMode) {
                    TEXT_EDIT_MODE -> setBottomSheetContent { null }
                    CROP_EDIT_MODE -> {
                        engine.editor.setSettingEnum("touch/pinchAction", TOUCH_ACTION_SCALE)
                        setBottomSheetContent {
                            CropBottomSheetContent(createCropUiState(engine.block.findAllSelected().single(), engine))
                        }
                    }

                    else -> {
                        setBottomSheetContent { null }
                        engine.editor.setSettingEnum("touch/pinchAction", TOUCH_ACTION_ZOOM)
                        if (!flag) {
                            flag = true
                            zoom(zoomToPage = true)
                        }
                    }
                }
                isKeyboardShowing.update { editMode == TEXT_EDIT_MODE }
            }
        }
    }

    private fun observeSelectedBlock() {
        viewModelScope.launch {
            // filter is added, because page dock becomes visible while postcard UI is still loading
            merge(engine.block.onSelectionChanged(), _updateBlock).filter { _isSceneLoaded.value }.collect {
                val block = engine.block.findAllSelected().firstOrNull()?.let { createBlock(it, engine) }
                val oldBlock = selectedBlock.value?.designBlock
                if (oldBlock != null && block == null) {
                    zoom(clampOnly = true)
                }
                var isBlockDifferent = false
                selectedBlock.update {
                    isBlockDifferent = it?.designBlock != block?.designBlock
                    block
                }
                if (isBlockDifferent) {
                    setOptionType(
                        if (block?.type == BlockType.Image &&
                            engine.isPlaceholder(
                                block.designBlock,
                            )
                        ) {
                            OptionType.Replace
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            engine.editor.onHistoryUpdated().collect {
                _historyChangeTrigger.trySend(Unit)
                updateVisiblePageState()
                updateBottomSheetUiState()
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
        engine.editor.setGlobalScope(Scope.EditorSelect, GlobalScope.DEFER)
        engine.editor.setRoleButPreserveGlobalScopes("Adopter")
        enterEditMode()
        return zoom(zoomToPage = true)
    }

    private fun enablePreviewMode() {
        _isPreviewMode.update { true }
        setBottomSheetContent { null }
        engine.editor.setGlobalScope(Scope.EditorSelect, GlobalScope.DENY)
        engine.editor.setRoleButPreserveGlobalScopes("Creator")
        enterPreviewMode()
        zoom(defaultInsets.copy(bottom = PAGE_MARGIN))
    }

    private fun togglePreviewMode(previewMode: Boolean) {
        if (previewMode) {
            enablePreviewMode()
        } else {
            enableEditMode()
        }
    }

    private fun exportScene() {
        if (_isExporting.compareAndSet(expect = false, update = true)) {
            viewModelScope.launch {
                onPreExport()
                onExport(engine, this@EditorUiViewModel)
                onPostExport()
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
                createCanvasActionMenuUiState(block, engine)
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

    open fun preEnterPreviewMode() {}

    open fun setSettings() {
        setSettingsForEditorUi(engine, baseUri)
    }

    abstract fun enterPreviewMode()

    abstract suspend fun onPreExport()

    abstract suspend fun onPostExport()
}
