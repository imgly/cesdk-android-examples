package ly.img.editor.base.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ly.img.editor.base.components.EditingTextCard
import ly.img.editor.base.components.actionmenu.CanvasActionMenu
import ly.img.editor.base.dock.AdjustmentSheetContent
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.ConfigurableHeightContainer
import ly.img.editor.base.dock.CustomBottomSheetContent
import ly.img.editor.base.dock.CustomFullScreenBottomSheetContent
import ly.img.editor.base.dock.EffectSheetContent
import ly.img.editor.base.dock.FillStrokeBottomSheetContent
import ly.img.editor.base.dock.FormatBottomSheetContent
import ly.img.editor.base.dock.InspectorBar
import ly.img.editor.base.dock.LayerBottomSheetContent
import ly.img.editor.base.dock.LibraryBottomSheetContent
import ly.img.editor.base.dock.LibraryCategoryBottomSheetContent
import ly.img.editor.base.dock.OptionsBottomSheetContent
import ly.img.editor.base.dock.ReplaceBottomSheetContent
import ly.img.editor.base.dock.options.adjustment.AdjustmentOptionsSheet
import ly.img.editor.base.dock.options.crop.CropBottomSheetContent
import ly.img.editor.base.dock.options.crop.CropSheet
import ly.img.editor.base.dock.options.effect.EffectSelectionSheet
import ly.img.editor.base.dock.options.fillstroke.FillStrokeOptionsSheet
import ly.img.editor.base.dock.options.format.FormatOptionsSheet
import ly.img.editor.base.dock.options.layer.LayerOptionsSheet
import ly.img.editor.base.dock.options.reorder.ReorderBottomSheetContent
import ly.img.editor.base.dock.options.reorder.ReorderSheet
import ly.img.editor.base.dock.options.shapeoptions.ShapeOptionsSheet
import ly.img.editor.base.dock.options.volume.VolumeBottomSheetContent
import ly.img.editor.base.dock.options.volume.VolumeSheet
import ly.img.editor.base.engine.EngineCanvasView
import ly.img.editor.base.timeline.view.TimelineView
import ly.img.editor.compose.bottomsheet.ModalBottomSheetDefaults
import ly.img.editor.compose.bottomsheet.ModalBottomSheetLayout
import ly.img.editor.compose.bottomsheet.ModalBottomSheetState
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue
import ly.img.editor.compose.bottomsheet.rememberModalBottomSheetState
import ly.img.editor.core.EditorContext
import ly.img.editor.core.EditorScope
import ly.img.editor.core.R
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.navbar.SystemNavBar
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.theme.surface2
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.AddLibrarySheet
import ly.img.editor.core.ui.library.AddLibraryTabsSheet
import ly.img.editor.core.ui.library.ReplaceLibrarySheet
import ly.img.editor.core.ui.permissions.PermissionManager.Companion.hasCameraPermission
import ly.img.editor.core.ui.permissions.PermissionManager.Companion.hasCameraPermissionInManifest
import ly.img.editor.core.ui.permissions.PermissionsView
import ly.img.editor.core.ui.scope.EditorContextImpl
import ly.img.editor.core.ui.utils.activity
import ly.img.editor.core.ui.utils.lifecycle.LifecycleEventEffect
import ly.img.editor.core.ui.utils.toPx

@OptIn(FlowPreview::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditorUi(
    initialExternalState: Parcelable,
    renderTarget: EngineRenderTarget,
    uiState: EditorUiViewState,
    editorScope: EditorScope,
    editorContext: EditorContext,
    onEvent: EditorScope.(Parcelable, EditorEvent) -> Parcelable,
    overlay: @Composable (EditorScope.(Parcelable) -> Unit),
    topBar: @Composable () -> Unit,
    canvasOverlay: @Composable BoxScope.(PaddingValues) -> Unit,
    bottomSheetLayout: @Composable ColumnScope.(BottomSheetContent) -> Unit = {},
    pagesOverlay: @Composable BoxScope.(PaddingValues) -> Unit = {},
    viewModel: EditorUiViewModel,
    close: (Throwable?) -> Unit,
) {
    val externalState = rememberSaveable { mutableStateOf(initialExternalState) }
    val uiScope = rememberCoroutineScope()
    val bottomSheetContent by viewModel.bottomSheetContent.collectAsState()
    val canvasActionMenuUiState by viewModel.canvasActionMenuUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var timelineExpanded by remember { mutableStateOf(true) }
    var hideTimeline: Boolean by remember { mutableStateOf(false) }
    val isSkipHalfExpanded = bottomSheetContent?.isHalfExpandingEnabled()?.not() ?: false
    val bottomSheetState =
        remember(isSkipHalfExpanded) {
            ModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                isSkipHalfExpanded = isSkipHalfExpanded,
            )
        }

    BackHandler(true) {
        viewModel.onEvent(
            Event.OnBackPress(
                bottomSheetOffset = bottomSheetState.swipeableState.offset ?: Float.MAX_VALUE,
                bottomSheetMaxOffset = bottomSheetState.swipeableState.maxOffset,
            ),
        )
    }
    LaunchedEffect(bottomSheetContent?.getType()) {
        if (bottomSheetContent == null && bottomSheetState.isVisible) bottomSheetState.snapTo(ModalBottomSheetValue.Hidden)
        if (bottomSheetContent != null &&
            bottomSheetContent !is ReplaceBottomSheetContent &&
            bottomSheetContent !is LibraryBottomSheetContent &&
            bottomSheetContent !is LibraryCategoryBottomSheetContent &&
            timelineExpanded
        ) {
            hideTimeline = true
            timelineExpanded = false
        } else if (bottomSheetContent == null && hideTimeline) {
            hideTimeline = false
            timelineExpanded = true
        }
        val sheetContent = bottomSheetContent
        if (sheetContent != null) {
            if (sheetContent.isHalfExpandingEnabled() && sheetContent.isInitialExpandHalf()) {
                bottomSheetState.halfExpand()
            } else {
                bottomSheetState.expand()
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_PAUSE) {
        viewModel.onEvent(Event.OnPause)
    }

    var anyComposable: AnyComposable? by remember {
        mutableStateOf(null)
    }

    val surfaceColor = colorScheme.surface
    val canvasColor = colorScheme.surface1
    val libraryColor = colorScheme.surface2
    var navigationBarColor by remember {
        mutableStateOf(surfaceColor)
    }
    LaunchedEffect(bottomSheetContent, uiState.selectedBlock, uiState.pagesState, anyComposable) {
        navigationBarColor =
            when {
                anyComposable != null -> surfaceColor
                bottomSheetContent == null -> {
                    when {
                        uiState.pagesState != null -> libraryColor
                        uiState.selectedBlock != null -> surfaceColor
                        else -> canvasColor
                    }
                }
                bottomSheetContent is LibraryBottomSheetContent -> {
                    libraryColor
                }
                else -> {
                    surfaceColor
                }
            }
    }

    var navigationBarHeightPx by remember {
        mutableStateOf(0F)
    }
    val navigationBarHeight =
        WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()
    navigationBarHeightPx = navigationBarHeight.toPx()
    val activity =
        requireNotNull(LocalContext.current.activity) {
            "Unable to find the activity. This is an internal error. Please report this issue."
        }
    val oneDpInPx = 1.dp.toPx()
    remember {
        (editorContext as EditorContextImpl).init(
            activity = activity,
            eventHandler = viewModel,
        )
        mutableStateOf(Unit)
    }
    LaunchedEffect(bottomSheetState) {
        val swipeableState = bottomSheetState.swipeableState
        launch {
            // Reset bottomSheetContent
            // The debounce is added to avoid the situation when another block is selected
            // This is needed because there is no other way currently to figure out when the bottom sheet has been dismissed by dragging
            snapshotFlow { swipeableState.offset }
                .debounce(16)
                .collect {
                    if (swipeableState.offset == swipeableState.maxOffset && swipeableState.targetValue == ModalBottomSheetValue.Hidden) {
                        viewModel.onEvent(Event.OnSheetDismissed)
                    }
                }
        }
        launch {
            snapshotFlow { swipeableState.offset }.collectLatest { offset ->
                if (offset == null) return@collectLatest
                val bottomSheetHeight =
                    (
                        (swipeableState.maxOffset - offset).coerceAtMost(
                            0.7f * swipeableState.maxOffset,
                        ) - navigationBarHeightPx
                    ).coerceAtLeast(0F)
                val bottomSheetHeightInDp = (bottomSheetHeight / oneDpInPx)
                viewModel.onEvent(Event.OnBottomSheetHeightChange(bottomSheetHeightInDp, showTimeline = timelineExpanded))
            }
        }
    }

    val scrimBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val showScrimBottomSheet =
        remember {
            { composable: AnyComposable ->
                anyComposable = composable
                uiScope.launch {
                    // FIXME: Without the delay, the bottom sheet state change does not animate. My hypothesis is that setting the
                    //  anyComposable causes a re-composition in the middle of the animation which cancels the animation(?)
                    delay(16)
                    scrimBottomSheetState.show()
                }
            }
        }

    LaunchedEffect(Unit) {
        snapshotFlow { scrimBottomSheetState.isVisible }.collectLatest {
            if (!it) anyComposable = null
        }
    }

    var showCameraPermissionsView by remember { mutableStateOf(false) }
    LaunchedEffect(bottomSheetState) {
        viewModel.uiEvent.collect {
            when (it) {
                is SingleEvent.Exit -> {
                    close(it.throwable)
                }

                is SingleEvent.ChangeSheetState -> {
                    if (it.state == ModalBottomSheetValue.Hidden && hideTimeline) {
                        hideTimeline = false
                        timelineExpanded = true
                    }
                    uiScope.launch {
                        if (it.animate) {
                            bottomSheetState.animateTo(it.state)
                        } else {
                            bottomSheetState.snapTo(it.state)
                        }
                    }
                }

                SingleEvent.HideScrimSheet -> {
                    uiScope.launch {
                        scrimBottomSheetState.hide()
                    }
                }

                is SingleEvent.Error -> {
                    uiScope.launch {
                        snackbarHostState.showSnackbar(
                            // stringResource() doesn't work inside of a LaunchedEffect as it is not a composable
                            message = activity.resources.getString(it.text),
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.externalEvent.collect {
            externalState.value = onEvent(editorScope, externalState.value, it)
        }
    }
    var contentPadding by remember {
        mutableStateOf<PaddingValues?>(null)
    }
    val openContract =
        rememberSaveable(
            inputs = arrayOf(uiState.openContract),
            saver =
                Saver(
                    save = {
                        EditorEvent.LaunchContract.current = it
                        true
                    },
                    restore = {
                        EditorEvent.LaunchContract.current.also {
                            EditorEvent.LaunchContract.current = null
                        }
                    },
                ),
        ) { uiState.openContract }
    val contract = openContract.contract

    @Suppress("UNCHECKED_CAST")
    val launcher =
        rememberLauncherForActivityResult(openContract.contract) { result ->
            (openContract.onOutput as? EditorScope.(Any?) -> Unit)?.let {
                viewModel.onEvent(
                    Event.OnLaunchContractResult(
                        onResult = it,
                        editorScope = editorScope,
                        result = result,
                    ),
                )
            }
        } as ManagedActivityResultLauncher<Any?, Any?>

    fun launchContract() {
        if (contract is DummyContract) return
        if (openContract.launched) return
        openContract.launched = true
        launcher.launch(openContract.input)
    }
    LaunchedEffect(contract) {
        val isSystemCameraContract =
            contract is ActivityResultContracts.CaptureVideo ||
                contract is ActivityResultContracts.TakePicture
        if (isSystemCameraContract && activity.hasCameraPermissionInManifest() && !activity.hasCameraPermission()) {
            showCameraPermissionsView = true
        } else {
            launchContract()
        }
    }

    Box(modifier = Modifier.background(colorScheme.surface)) {
        ModalBottomSheetLayout(
            sheetState = scrimBottomSheetState,
            dismissContentDescription = stringResource(id = R.string.ly_img_editor_close),
            sheetContent = {
                anyComposable?.Content()
            },
            scrimEnabled = true,
            sheetShape =
                RoundedCornerShape(
                    topStart = 28.0.dp,
                    topEnd = 28.0.dp,
                    bottomEnd = 0.0.dp,
                    bottomStart = 0.0.dp,
                ),
        ) {
            val cornerRadius = if (bottomSheetState.swipeableState.offset != 0f) 28.dp else 0.dp
            val sheetElevation = if (bottomSheetState.swipeableState.offset != 0f) ModalBottomSheetDefaults.Elevation else 0.dp
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                modifier = Modifier.statusBarsPadding(),
                dismissContentDescription = stringResource(id = R.string.ly_img_editor_close),
                sheetElevation = sheetElevation,
                sheetContent = {
                    val content = bottomSheetContent
                    if (content != null) {
                        Column {
                            if (content !is CustomBottomSheetContent) {
                                Spacer(Modifier.height(8.dp))
                            }
                            when (content) {
                                LibraryBottomSheetContent ->
                                    AddLibraryTabsSheet(
                                        swipeableState = bottomSheetState.swipeableState,
                                        onClose = {
                                            viewModel.onEvent(Event.OnHideSheet)
                                        },
                                        onCloseAssetDetails = {
                                            viewModel.onEvent(Event.OnHideScrimSheet)
                                        },
                                        onSearchFocus = {
                                            viewModel.onEvent(Event.OnExpandSheet)
                                        },
                                        showAnyComposable = {
                                            showScrimBottomSheet(it)
                                        },
                                        launchGetContent = { mimeType, uploadAssetSourceType, designBlock ->
                                            viewModel.onEvent(
                                                Event.OnLaunchGetContent(mimeType, uploadAssetSourceType, designBlock),
                                            )
                                        },
                                        launchCamera = { captureVideo, designBlock ->
                                            viewModel.onEvent(Event.OnSystemCameraClick(captureVideo, designBlock))
                                        },
                                    )
                                is LibraryCategoryBottomSheetContent ->
                                    AddLibrarySheet(
                                        libraryCategory = content.libraryCategory,
                                        addToBackgroundTrack = content.addToBackgroundTrack,
                                        onClose = {
                                            viewModel.onEvent(Event.OnHideSheet)
                                        },
                                        onCloseAssetDetails = {
                                            viewModel.onEvent(Event.OnHideScrimSheet)
                                        },
                                        onSearchFocus = {
                                            viewModel.onEvent(Event.OnExpandSheet)
                                        },
                                        showAnyComposable = {
                                            showScrimBottomSheet(it)
                                        },
                                        launchGetContent = { mimeType, uploadAssetSourceType, designBlock ->
                                            Event.OnLaunchGetContent(
                                                mimeType = mimeType,
                                                uploadAssetSourceType = uploadAssetSourceType,
                                                designBlock = designBlock,
                                                addToBackgroundTrack = content.addToBackgroundTrack,
                                            ).let(viewModel::onEvent)
                                        },
                                        launchCamera = { captureVideo, designBlock ->
                                            Event.OnSystemCameraClick(
                                                captureVideo = captureVideo,
                                                designBlock = designBlock,
                                                addToBackgroundTrack = content.addToBackgroundTrack,
                                            ).let(viewModel::onEvent)
                                        },
                                    )

                                is ReplaceBottomSheetContent ->
                                    ReplaceLibrarySheet(
                                        designBlock = content.designBlock,
                                        type = content.blockType,
                                        onClose = {
                                            viewModel.onEvent(Event.OnHideSheet)
                                        },
                                        onCloseAssetDetails = {
                                            viewModel.onEvent(Event.OnHideScrimSheet)
                                        },
                                        onSearchFocus = {
                                            viewModel.onEvent(Event.OnExpandSheet)
                                        },
                                        showAnyComposable = {
                                            showScrimBottomSheet(it)
                                        },
                                        launchGetContent = { mimeType, uploadAssetSourceType, designBlock ->
                                            Event.OnLaunchGetContent(
                                                mimeType = mimeType,
                                                uploadAssetSourceType = uploadAssetSourceType,
                                                designBlock = designBlock,
                                            ).let(viewModel::onEvent)
                                        },
                                        launchCamera = { captureVideo, designBlock ->
                                            Event.OnSystemCameraClick(
                                                captureVideo = captureVideo,
                                                designBlock = designBlock,
                                            ).let(viewModel::onEvent)
                                        },
                                    )

                                is LayerBottomSheetContent -> LayerOptionsSheet(content.uiState, viewModel::onEvent)
                                is FillStrokeBottomSheetContent -> FillStrokeOptionsSheet(content.uiState, viewModel::onEvent)
                                is OptionsBottomSheetContent -> ShapeOptionsSheet(content.uiState, viewModel::onEvent)
                                is FormatBottomSheetContent -> FormatOptionsSheet(content.uiState, viewModel::onEvent)
                                is CropBottomSheetContent -> CropSheet(content.uiState, viewModel::onEvent)
                                is AdjustmentSheetContent -> AdjustmentOptionsSheet(content.uiState, viewModel::onEvent)
                                is EffectSheetContent -> EffectSelectionSheet(content.uiState, viewModel::onEvent)
                                is VolumeBottomSheetContent -> VolumeSheet(content.uiState, viewModel::onEvent)
                                is ReorderBottomSheetContent -> ReorderSheet(content.timelineState, viewModel::onEvent)
                                is CustomBottomSheetContent -> {
                                    ConfigurableHeightContainer(maxHeightProvider = { editorHeight ->
                                        when (content.maxHeight) {
                                            is Height.Exactly -> content.maxHeight.size
                                            is Height.Fraction -> editorHeight * content.maxHeight.fraction
                                            null -> Dp.Unspecified
                                        }
                                    }) {
                                        Box {
                                            content.content(this, viewModel.engine)
                                        }
                                    }
                                }
                                is CustomFullScreenBottomSheetContent ->
                                    Box(modifier = Modifier.fillMaxHeight(fraction = content.heightFraction)) {
                                        content.content(this, viewModel.engine)
                                    }
                                else -> bottomSheetLayout(content)
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                },
                sheetShape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
            ) {
                Scaffold(
                    modifier = Modifier.navigationBarsPadding(),
                    topBar = topBar,
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                ) { paddingValues ->
                    contentPadding = paddingValues
                    BoxWithConstraints {
                        val orientation = LocalConfiguration.current.orientation
                        EngineCanvasView(
                            license = editorContext.license,
                            userId = editorContext.userId,
                            renderTarget = renderTarget,
                            engine = viewModel.engine,
                            isCanvasVisible = uiState.isCanvasVisible,
                            passTouches = uiState.allowEditorInteraction,
                            onLicenseValidationError = {
                                viewModel.onEvent(Event.OnError(it))
                            },
                            onMoveStart = { viewModel.onEvent(Event.OnCanvasMove(true)) },
                            onMoveEnd = { viewModel.onEvent(Event.OnCanvasMove(false)) },
                            onTouch = { viewModel.onEvent(Event.OnCanvasTouch) },
                            onSizeChanged = { viewModel.onEvent(Event.OnResetZoom) },
                            loadScene = {
                                val topInsets = 64f // 64 for toolbar
                                val bottomInsets = 84f // 84 for dock
                                val sideInsets = 0f
                                val insets =
                                    Rect(
                                        left = sideInsets,
                                        top = topInsets,
                                        right = sideInsets,
                                        bottom = bottomInsets,
                                    )
                                viewModel.onEvent(
                                    Event.OnLoadScene(
                                        height = maxHeight.value,
                                        insets = insets,
                                        inPortraitMode = orientation != Configuration.ORIENTATION_LANDSCAPE,
                                    ),
                                )
                            },
                            onDisposed = {
                                (editorContext as EditorContextImpl).clear()
                            },
                        )
                        if (uiState.isCanvasVisible) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .onSizeChanged {
                                        viewModel.onEvent(
                                            Event.OnUpdateBottomInset(
                                                it.height / oneDpInPx,
                                                zoom = !hideTimeline,
                                                isExpanding = timelineExpanded,
                                            ),
                                        )
                                    },
                            ) {
                                uiState.timelineState?.let { timelineState ->
                                    TimelineView(
                                        timelineState = timelineState,
                                        expanded = timelineExpanded,
                                        onToggleExpand = {
                                            timelineExpanded = timelineExpanded.not()
                                        },
                                        onEvent = viewModel::onEvent,
                                    )
                                }
                                Box {
                                    canvasOverlay(paddingValues)
                                }
                            }
                        }
                        canvasActionMenuUiState?.let {
                            CanvasActionMenu(
                                uiState = it,
                                onEvent = viewModel::onEvent,
                            )
                        }
                        if (uiState.isEditingText) {
                            EditingTextCard(
                                modifier =
                                    Modifier
                                        .align(Alignment.BottomStart)
                                        .onGloballyPositioned {
                                            viewModel.onEvent(Event.OnKeyboardHeightChange(it.size.height / oneDpInPx))
                                        },
                                onClose = { viewModel.onEvent(Event.OnKeyboardClose) },
                            )
                        }
                        InspectorBar(
                            selectedBlock = uiState.selectedBlock,
                            modifier = Modifier.align(Alignment.BottomStart),
                            onClose = { viewModel.onEvent(Event.OnCloseDock) },
                            onClick = { viewModel.onEvent(Event.OnOptionClick(it)) },
                        )
                    }
                }
            }
            contentPadding?.let {
                Box {
                    pagesOverlay(it)
                }
            }
        }
        SystemNavBar(navigationBarColor)
        Surface(
            color = navigationBarColor,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(navigationBarHeight),
        ) {}
        overlay(editorScope, externalState.value)

        if (showCameraPermissionsView) {
            Column(
                modifier =
                    Modifier
                        .systemBarsPadding()
                        .background(canvasColor)
                        .fillMaxSize()
                        .align(Alignment.TopCenter)
                        .requiredWidth(264.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PermissionsView(requestOnlyCameraPermission = true) {
                    showCameraPermissionsView = false
                    launchContract()
                }
            }
            BackHandler(showCameraPermissionsView) {
                showCameraPermissionsView = false
            }
        }
    }
}
