package ly.img.cesdk.editorui

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.cesdk.Environment
import ly.img.cesdk.core.components.actionmenu.CanvasActionMenuUiState
import ly.img.cesdk.core.components.actionmenu.createCanvasActionMenuUiState
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.core.components.color_picker.fillAndStrokeColors
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.FillStrokeBottomSheetContent
import ly.img.cesdk.dock.FormatBottomSheetContent
import ly.img.cesdk.dock.LayerBottomSheetContent
import ly.img.cesdk.dock.LibraryBottomSheetContent
import ly.img.cesdk.dock.OptionType
import ly.img.cesdk.dock.OptionsBottomSheetContent
import ly.img.cesdk.dock.ReplaceBottomSheetContent
import ly.img.cesdk.dock.options.crop.CropBottomSheetContent
import ly.img.cesdk.dock.options.crop.createCropUiState
import ly.img.cesdk.dock.options.fillstroke.createFillStrokeUiState
import ly.img.cesdk.dock.options.format.createFormatUiState
import ly.img.cesdk.dock.options.layer.createLayerUiState
import ly.img.cesdk.dock.options.shapeoptions.createShapeOptionsUiState
import ly.img.cesdk.engine.Block
import ly.img.cesdk.engine.BlockType
import ly.img.cesdk.engine.CROP_EDIT_MODE
import ly.img.cesdk.engine.TEXT_EDIT_MODE
import ly.img.cesdk.engine.TRANSFORM_EDIT_MODE
import ly.img.cesdk.engine.addOutline
import ly.img.cesdk.engine.createBlock
import ly.img.cesdk.engine.deselectAllBlocks
import ly.img.cesdk.engine.getPage
import ly.img.cesdk.engine.isPlaceholder
import ly.img.cesdk.engine.resetHistory
import ly.img.cesdk.engine.showOutline
import ly.img.cesdk.engine.showPage
import ly.img.cesdk.engine.zoomToPage
import ly.img.cesdk.engine.zoomToSelectedText
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
abstract class EditorUiViewModel : ViewModel() {

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
    private val _errorLoadingAssets = MutableStateFlow(false)

    protected val selectedBlock = MutableStateFlow<Block?>(null)
    protected val isKeyboardShowing = MutableStateFlow(false)
    protected val pageIndex = MutableStateFlow(0)
    protected val _isSceneLoaded = MutableStateFlow(false)

    protected var inPortraitMode = true
    protected var currentInsets = Rect.Zero

    protected val _uiState = MutableStateFlow(EditorUiViewState())

    private val _uiEvent = Channel<SingleEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _bottomSheetContent = MutableStateFlow<BottomSheetContent?>(null)
    val bottomSheetContent = _bottomSheetContent.asStateFlow()

    private val _canvasActionMenuUiState = MutableStateFlow<CanvasActionMenuUiState?>(null)
    val canvasActionMenuUiState = _canvasActionMenuUiState.asStateFlow()

    protected var colorPalette = fillAndStrokeColors

    init {
        with(viewModelScope) {
            launch {
                assetsRepo.loadFonts()
            }
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

    open fun onEvent(event: Event) {
        when (event) {
            is BlockEvent -> {
                if (bottomSheetContent.value is CropBottomSheetContent && event != BlockEvent.OnResetCrop) {
                    isStraighteningOrRotating = true
                }
                handleBlockEvent(
                    engine,
                    getBlockForEvents(),
                    checkNotNull(assetsRepo.fontFamilies.value).getOrThrow(),
                    event
                )
            }

            Event.OnBackPress -> onBackPress()
            Event.OnBack -> onBack()
            Event.OnCloseDock -> engine.deselectAllBlocks()
            Event.OnExit -> onExit()
            Event.OnAddLibraryClick -> openLibrarySheet()
            Event.ExpandSheet -> sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Expanded))
            Event.HideSheet -> sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Hidden))
            Event.OnExportClick -> exportScene()
            Event.OnRedoClick -> engine.editor.redo()
            is Event.OnTogglePreviewMode -> togglePreviewMode(event.isChecked)
            Event.OnUndoClick -> engine.editor.undo()
            is Event.OnOptionClick -> setOptionType(event.optionType)
            is Event.OnCanvasMove -> onCanvasMove(event.move)
            is Event.OnKeyboardClose -> onKeyboardClose()
            is Event.OnLoadScene -> loadScene(event.sceneUriString, event.height, event.insets, event.inPortraitMode)
            is Event.EnableHistory -> _enableHistory.update { event.enable }
            is Event.OnBottomSheetHeightChange -> onBottomSheetHeightChange(event.heightInDp)
            is Event.OnKeyboardHeightChange -> onKeyboardHeightChange(event.heightInDp)
        }
    }

    protected open fun getBlockForEvents() = checkNotNull(selectedBlock.value).designBlock

    protected open fun onCanvasMove(move: Boolean) {
        setCanvasActionMenuState(show = !move)
    }

    protected open fun onSceneLoad() {
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
                is OptionsBottomSheetContent -> OptionsBottomSheetContent(
                    createShapeOptionsUiState(designBlock, engine)
                )

                is FillStrokeBottomSheetContent -> {
                    _updateBlock.update { !it }
                    FillStrokeBottomSheetContent(
                        createFillStrokeUiState(block, engine, colorPalette)
                    )
                }

                is FormatBottomSheetContent -> FormatBottomSheetContent(
                    createFormatUiState(designBlock, engine, checkNotNull(assetsRepo.fontFamilies.value).getOrThrow())
                )

                is CropBottomSheetContent -> {
                    val useOldScaleRatio = isStraighteningOrRotating
                    isStraighteningOrRotating = false
                    CropBottomSheetContent(
                        createCropUiState(designBlock, engine, if (useOldScaleRatio) it.uiState.cropScaleRatio else null)
                    )
                }

                else -> {
                    it
                }
            }
        }
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
        } else false
    }

    protected fun setBottomSheetContent(function: (BottomSheetContent?) -> BottomSheetContent?) {
        _bottomSheetContent.update(function)
        setCanvasActionMenuState(show = bottomSheetContent.value == null)
    }

    private fun onSheetDismiss() {
        setOptionType(null)
        if (engine.editor.getEditMode() == CROP_EDIT_MODE) engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
    }

    private fun onZoomFinish() {
        setCanvasActionMenuState()
    }

    private fun loadScene(uriString: String, height: Float, insets: Rect, inPortraitMode: Boolean) {
        defaultInsets = insets
        canvasHeight = height
        this.inPortraitMode = inPortraitMode
        setSettingsForEditorUi(engine)
        val isFirstLoad = firstLoad
        firstLoad = false
        viewModelScope.launch {
            if (engine.scene.get() == null) {
                val scene = engine.scene.load(Uri.parse(uriString))
                engine.addOutline(scene, engine.getPage(pageIndex.value))
                engine.showOutline(false)
                engine.showPage(pageIndex.value)
                enableEditMode().join()
                engine.resetHistory()
            } else {
                // we came here after a configuration change
                if (_isPreviewMode.value) {
                    enablePreviewMode()
                } else {
                    enableEditMode()
                }
            }
            if (isFirstLoad) {
                // asset sources are not restored after process death
                try {
                    assetsRepo.loadAssetSources(engine)
                } catch (ex: Exception) {
                    _errorLoadingAssets.update { true }
                }
            }
            _isSceneLoaded.update { true }
            onSceneLoad()
        }

        if (isFirstLoad) {
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

    private fun onBottomSheetHeightChange(heightInDp: Int) {
        if (_isPreviewMode.value || !_isSceneLoaded.value) return
        val bottomInset = heightInDp + 16f
        zoom(bottomInset)
    }

    private fun onKeyboardClose() {
        engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
    }

    private fun onKeyboardHeightChange(heightInDp: Int) {
        val bottomInset = heightInDp + 16f // 16dp padding from edge
        zoom(bottomInset)
    }

    private fun zoom(bottomInset: Float) {
        if (bottomInset <= defaultInsets.bottom && currentInsets.bottom == defaultInsets.bottom) return
        zoom(currentInsets.copy(bottom = bottomInset.coerceAtLeast(defaultInsets.bottom)))
    }

    private var zoomJob: Job? = null
    private fun zoom(insets: Rect = defaultInsets): Job {
        zoomJob?.cancel()
        return viewModelScope.launch {
            if (_uiState.value.isInPreviewMode) {
                currentInsets = insets.copy(bottom = 16f)
                enterPreviewMode()
            } else {
                currentInsets = insets
                engine.zoomToPage(pageIndex.value, currentInsets)
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

                OptionType.Format -> FormatBottomSheetContent(
                    createFormatUiState(designBlock, engine, checkNotNull(assetsRepo.fontFamilies.value).getOrThrow())
                )

                OptionType.ShapeOptions -> OptionsBottomSheetContent(
                    createShapeOptionsUiState(designBlock, engine)
                )

                OptionType.FillStroke -> FillStrokeBottomSheetContent(
                    createFillStrokeUiState(block, engine, colorPalette)
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
            }
        }
    }

    private fun observeUiStateChanges() {
        viewModelScope.launch {
            merge(
                engine.editor.onHistoryUpdated(),
                _isSceneLoaded,
                _isPreviewMode,
                _isExporting,
                _enableHistory,
                assetsRepo.fontFamilies,
                selectedBlock,
                _errorLoadingAssets,
                isKeyboardShowing
            ).collect {
                _uiState.update {
                    it.copy(
                        isLoading = !_isSceneLoaded.value || assetsRepo.fontFamilies.value == null || _isExporting.value,
                        isInPreviewMode = _isPreviewMode.value,
                        isUndoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canUndo(),
                        isRedoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canRedo(),
                        errorLoading = _errorLoadingAssets.value || (assetsRepo.fontFamilies.value?.isFailure ?: false),
                        selectedBlock = selectedBlock.value,
                        isEditingText = isKeyboardShowing.value
                    )
                }
            }
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
        viewModelScope.launch {
            engine.editor.onStateChanged().map { engine.editor.getEditMode() }.distinctUntilChanged().collect { editMode ->
                when (editMode) {
                    TEXT_EDIT_MODE -> setBottomSheetContent { null }
                    CROP_EDIT_MODE -> setBottomSheetContent {
                        CropBottomSheetContent(createCropUiState(engine.block.findAllSelected().single(), engine))
                    }

                    else -> {
                        setBottomSheetContent { null }
                        zoom()
                    }
                }
                isKeyboardShowing.update { editMode == TEXT_EDIT_MODE }
            }
        }
    }

    private fun observeSelectedBlock() {
        viewModelScope.launch {
            merge(engine.block.onSelectionChanged(), _updateBlock).collect {
                val block = engine.block.findAllSelected().firstOrNull()?.let { createBlock(it, engine) }
                var isBlockDifferent = false
                selectedBlock.update {
                    isBlockDifferent = it?.designBlock != block?.designBlock
                    block
                }
                if (isBlockDifferent) {
                    setOptionType(if (block?.type == BlockType.Image && engine.isPlaceholder(block.designBlock)) OptionType.Replace else null)
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            engine.editor.onHistoryUpdated().collect {
                updateBottomSheetUiState()
            }
        }
    }

    private fun onBack() {
        if (hasUnsavedChanges()) {
            _uiState.update {
                it.copy(showExitDialog = !it.showExitDialog)
            }
        } else {
            sendSingleEvent(SingleEvent.Exit)
        }
    }

    private fun onBackPress() {
        if (!handleBackPress()) {
            onBack()
        }
    }

    private fun onExit() {
        _uiState.update {
            it.copy(showExitDialog = false)
        }
        sendSingleEvent(SingleEvent.Exit)
    }

    private fun enableEditMode(): Job {
        _isPreviewMode.update { false }
        enterEditMode()
        return zoom()
    }

    private fun enablePreviewMode() {
        _isPreviewMode.update { true }
        setBottomSheetContent { null }
        enterPreviewMode()
        zoom()
    }

    private fun togglePreviewMode(previewMode: Boolean) {
        if (previewMode) {
            enablePreviewMode()
        } else {
            enableEditMode()
        }
    }

    private fun exportScene() {
        if (_isExporting.compareAndSet(false, true)) {
            viewModelScope.launch {
                val byteArray = exportSceneAsByteArray()
                withContext(Dispatchers.IO) {
                    val file = File.createTempFile("export", ".pdf")
                    val fos = FileOutputStream(file)
                    fos.write(byteArray)
                    fos.close()
                    _uiEvent.send(SingleEvent.ShareExport(file))
                    _isExporting.update { false }
                }
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
    abstract fun enterPreviewMode()
    abstract suspend fun exportSceneAsByteArray(): ByteArray
}