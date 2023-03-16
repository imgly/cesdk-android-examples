package ly.img.cesdk.apparel

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ly.img.cesdk.Environment
import ly.img.cesdk.core.components.actionmenu.CanvasActionMenuUiState
import ly.img.cesdk.core.components.actionmenu.createCanvasActionMenuUiState
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.OptionType
import ly.img.cesdk.dock.options.fillstroke.createFillStrokeUiState
import ly.img.cesdk.dock.options.format.createFormatUiState
import ly.img.cesdk.dock.options.layer.createLayerUiState
import ly.img.cesdk.dock.options.shapeoptions.createShapeOptionsUiState
import ly.img.cesdk.engine.Block
import ly.img.cesdk.engine.BlockType
import ly.img.cesdk.engine.TEXT_EDIT_MODE
import ly.img.cesdk.engine.TRANSFORM_EDIT_MODE
import ly.img.cesdk.engine.addOutline
import ly.img.cesdk.engine.createBlock
import ly.img.cesdk.engine.deselectAllBlocks
import ly.img.cesdk.engine.enableEditMode
import ly.img.cesdk.engine.enablePreviewMode
import ly.img.cesdk.engine.exportScene
import ly.img.cesdk.engine.isPlaceholder
import ly.img.cesdk.engine.showOutline
import ly.img.cesdk.engine.zoomToBackdrop
import ly.img.cesdk.engine.zoomToPage
import ly.img.cesdk.engine.zoomToSelectedText

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
class ApparelUiViewModel : ViewModel() {

    private var firstLoad = true
    private val engine = Environment.getEngine()

    private val _isSceneLoaded = MutableStateFlow(false)
    private val _isPreviewMode = MutableStateFlow(false)
    private val _isExporting = MutableStateFlow(false)
    private val _updateBlock = MutableStateFlow(false)
    private val _enableHistory = MutableStateFlow(true)
    private val _selectedBlock = MutableStateFlow<Block?>(null)
    private val _errorLoadingAssets = MutableStateFlow(false)
    private val _isKeyboardShowing = MutableStateFlow(false)

    private val assetsRepo = Environment.getAssetsRepo()

    private var defaultInsets = Rect.Zero
    private var canvasHeight = 0f
    private var currentInsets = Rect.Zero

    private val _uiState = MutableStateFlow(
        ApparelUiViewState(
            isLoading = false,
            isUndoEnabled = false,
            isRedoEnabled = false,
            isInPreviewMode = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SingleEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _bottomSheetContent = MutableStateFlow<BottomSheetContent?>(null)
    val bottomSheetContent = _bottomSheetContent.asStateFlow()

    // TODO: Consolidate all states into one state
    private val _canvasActionMenuUiState = MutableStateFlow<CanvasActionMenuUiState?>(null)
    val canvasActionMenuUiState = _canvasActionMenuUiState.asStateFlow()

    init {
        with(viewModelScope) {
            launch {
                assetsRepo.loadFonts()
            }
            launch {
                // Reset bottomSheetContent
                // The debounce is added to avoid the situation when another block is selected
                // This is needed because there is no other way currently to figure out when the bottom sheet has been dismissed by dragging
                val swipeableState = uiState.value.bottomSheetState.swipeableState
                snapshotFlow { swipeableState.offset }.debounce(16).collect {
                    if (swipeableState.offset == swipeableState.maxOffset) {
                        setOptionType(null)
                    }
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is BlockEvent -> handleBlockEvent(
                engine,
                checkNotNull(_selectedBlock.value).designBlock,
                checkNotNull(assetsRepo.fontFamilies.value).getOrThrow(),
                event
            )
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
            is Event.OnLoadScene -> loadScene(event.sceneUriString, event.height, event.insets)
            is Event.EnableHistory -> _enableHistory.update { event.enable }
            is Event.OnBottomSheetHeightChange -> onBottomSheetHeightChange(event.heightInDp)
            is Event.OnKeyboardHeightChange -> onKeyboardHeightChange(event.heightInDp)
        }
    }

    private fun loadScene(uriString: String, height: Float, insets: Rect) {
        defaultInsets = insets
        canvasHeight = height
        setSettingsForApparelUi(engine)
        val isFirstLoad = firstLoad
        firstLoad = false
        viewModelScope.launch {
            val scene = engine.scene.get()
            if (scene == null) {
                engine.addOutline(engine.scene.load(Uri.parse(uriString)))
                enableEditMode().join()
                engine.editor.addUndoStep()
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
        }
        if (isFirstLoad) {
            observeSelectedBlock()
            observeHistory()
            observeUiStateChanges()
            observeEvents()
            observeEditorStateChange()
        }
    }

    private fun onCanvasMove(move: Boolean) {
        engine.showOutline(show = move)
        setCanvasActionMenuState(show = !move)
    }

    private fun openLibrarySheet() {
        setBottomSheetContent { BottomSheetContent.Library }
    }

    private fun onBottomSheetHeightChange(heightInDp: Int) {
        val bottomInset = heightInDp + 16f
        zoom(bottomInset)
    }

    private fun onKeyboardClose() {
        engine.editor.setEditMode(TRANSFORM_EDIT_MODE)
        zoom()
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
            if (uiState.value.isInPreviewMode) {
                currentInsets = insets.copy(bottom = 16f)
                engine.zoomToBackdrop(currentInsets)
            } else {
                currentInsets = insets
                engine.zoomToPage(currentInsets)
                if (engine.editor.getEditMode() == TEXT_EDIT_MODE) {
                    zoomToText()
                }
            }
            setCanvasActionMenuState()
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
        val block = checkNotNull(_selectedBlock.value)
        val designBlock = block.designBlock
        setBottomSheetContent {
            when (optionType) {
                OptionType.Replace -> BottomSheetContent.Replace(designBlock, block.type)
                OptionType.Layer -> BottomSheetContent.Layer(createLayerUiState(designBlock, engine))
                OptionType.Edit -> {
                    engine.editor.setEditMode(TEXT_EDIT_MODE)
                    null
                }
                OptionType.Format -> BottomSheetContent.Format(
                    createFormatUiState(designBlock, engine, checkNotNull(assetsRepo.fontFamilies.value).getOrThrow())
                )
                OptionType.ShapeOptions -> BottomSheetContent.Options(
                    createShapeOptionsUiState(designBlock, engine)
                )
                OptionType.FillStroke -> BottomSheetContent.FillStroke(
                    createFillStrokeUiState(block, engine)
                )
                OptionType.EnterGroup -> {
                    engine.block.enterGroup(designBlock)
                    null
                }
                OptionType.SelectGroup -> {
                    engine.block.exitGroup(designBlock)
                    null
                }
            }
        }
    }

    private fun setBottomSheetContent(function: (BottomSheetContent?) -> BottomSheetContent?) {
        _bottomSheetContent.update(function)
        setCanvasActionMenuState(show = bottomSheetContent.value == null)
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
                _selectedBlock,
                _errorLoadingAssets,
                _isKeyboardShowing
            ).collect {
                _uiState.update {
                    it.copy(
                        isLoading = !_isSceneLoaded.value || assetsRepo.fontFamilies.value == null || _isExporting.value,
                        isInPreviewMode = _isPreviewMode.value,
                        isUndoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canUndo(),
                        isRedoEnabled = _isSceneLoaded.value && _enableHistory.value && engine.editor.canRedo(),
                        errorLoading = _errorLoadingAssets.value || (assetsRepo.fontFamilies.value?.isFailure ?: false),
                        selectedBlock = _selectedBlock.value,
                        isEditingText = _isKeyboardShowing.value
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
                if (editMode == TEXT_EDIT_MODE) {
                    setBottomSheetContent { null }
                }
                _isKeyboardShowing.update { editMode == TEXT_EDIT_MODE }
            }
        }
    }

    private fun observeSelectedBlock() {
        viewModelScope.launch {
            merge(engine.block.onSelectionChanged(), _updateBlock).collect {
                val block = engine.block.findAllSelected().firstOrNull()?.let { createBlock(it, engine) }
                var isBlockDifferent = false
                _selectedBlock.update {
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

    private fun setCanvasActionMenuState(show: Boolean = true) {
        val block = _selectedBlock.value?.designBlock
        val isVisible = !_isKeyboardShowing.value && bottomSheetContent.value == null && show
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

    private fun updateBottomSheetUiState() {
        val block = _selectedBlock.value ?: return
        val designBlock = block.designBlock
        // In the case when a block is deleted, the block is unselected after receiving the delete event
        if (!engine.block.isValid(designBlock)) return
        setBottomSheetContent {
            when (it) {
                is BottomSheetContent.Layer -> BottomSheetContent.Layer(createLayerUiState(designBlock, engine))
                is BottomSheetContent.Options -> BottomSheetContent.Options(
                    createShapeOptionsUiState(designBlock, engine)
                )
                is BottomSheetContent.FillStroke -> {
                    _updateBlock.update { !it }
                    BottomSheetContent.FillStroke(
                        createFillStrokeUiState(block, engine)
                    )
                }
                is BottomSheetContent.Format -> BottomSheetContent.Format(
                    createFormatUiState(designBlock, engine, checkNotNull(assetsRepo.fontFamilies.value).getOrThrow())
                )
                else -> {
                    it
                }
            }
        }
    }

    private fun onBack() {
        if (_uiState.value.isUndoEnabled) {
            _uiState.update {
                it.copy(showExitDialog = !it.showExitDialog)
            }
        } else {
            sendSingleEvent(SingleEvent.Exit)
        }
    }

    private fun onBackPress() {
        if ((_uiState.value.bottomSheetState.swipeableState.offset ?: Float.MAX_VALUE)
            < _uiState.value.bottomSheetState.swipeableState.maxOffset
        ) {
            sendSingleEvent(SingleEvent.ChangeSheetState(ModalBottomSheetValue.Hidden))
        } else if (_selectedBlock.value != null) {
            engine.deselectAllBlocks()
        } else {
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
        engine.enableEditMode()
        return zoom()
    }

    private fun enablePreviewMode() {
        _isPreviewMode.update { true }
        setBottomSheetContent { null }
        engine.enablePreviewMode()
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
                val file = engine.exportScene()
                _uiEvent.send(SingleEvent.ShareExport(file))
                _isExporting.update { false }
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
}