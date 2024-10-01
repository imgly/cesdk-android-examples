package ly.img.editor.base.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ly.img.editor.base.components.EditingTextCard
import ly.img.editor.base.components.actionmenu.CanvasActionMenu
import ly.img.editor.base.dock.AdjustmentSheetContent
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.Dock
import ly.img.editor.base.dock.EffectSheetContent
import ly.img.editor.base.dock.FillStrokeBottomSheetContent
import ly.img.editor.base.dock.FormatBottomSheetContent
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
import ly.img.editor.base.ui.lifecycle.LifecycleEventEffect
import ly.img.editor.compose.bottomsheet.ModalBottomSheetLayout
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue
import ly.img.editor.compose.bottomsheet.rememberModalBottomSheetState
import ly.img.editor.core.R
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.theme.surface2
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.AddLibrarySheet
import ly.img.editor.core.ui.library.AddLibraryTabsSheet
import ly.img.editor.core.ui.library.ReplaceLibrarySheet
import ly.img.editor.core.ui.utils.activity
import ly.img.editor.core.ui.utils.toPx

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditorUi(
    initialExternalState: Parcelable,
    license: String,
    userId: String?,
    renderTarget: EngineRenderTarget,
    uiState: EditorUiViewState,
    onEvent: (Activity, Parcelable, EditorEvent) -> Parcelable,
    overlay: @Composable ((Parcelable, EditorEventHandler) -> Unit),
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

    BackHandler(true) {
        viewModel.onEvent(Event.OnBackPress)
    }

    LaunchedEffect(bottomSheetContent?.getType()) {
        val sheetState = uiState.bottomSheetState
        if (bottomSheetContent == null && sheetState.isVisible) sheetState.snapTo(ModalBottomSheetValue.Hidden)
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
            if (sheetContent.isInitialExpandHalf()) {
                sheetState.halfExpand()
            } else {
                sheetState.expand()
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_PAUSE) {
        viewModel.onEvent(Event.OnPause)
    }

    val view = LocalView.current
    val surfaceColor = colorScheme.surface.toArgb()
    val canvasColor = colorScheme.surface1.toArgb()
    val libraryColor = colorScheme.surface2.toArgb()
    LaunchedEffect(bottomSheetContent, uiState.selectedBlock, uiState.pagesState) {
        val window = (view.context as Activity).window
        window.navigationBarColor =
            when (bottomSheetContent) {
                null -> {
                    when {
                        uiState.pagesState != null -> libraryColor
                        uiState.selectedBlock != null -> surfaceColor
                        else -> canvasColor
                    }
                }
                is LibraryBottomSheetContent -> {
                    libraryColor
                }
                else -> {
                    surfaceColor
                }
            }
    }

    val oneDpInPx = 1.dp.toPx()
    LaunchedEffect(Unit) {
        val swipeableState = uiState.bottomSheetState.swipeableState
        snapshotFlow { swipeableState.offset }.collectLatest { offset ->
            if (offset == null) return@collectLatest
            val bottomSheetHeight = (swipeableState.maxOffset - offset).coerceAtMost(0.7f * swipeableState.maxOffset)
            val bottomSheetHeightInDp = (bottomSheetHeight / oneDpInPx)
            viewModel.onEvent(Event.OnBottomSheetHeightChange(bottomSheetHeightInDp, showTimeline = timelineExpanded))
        }
    }

    val scrimBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var anyComposable: AnyComposable? by remember {
        mutableStateOf(null)
    }

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

    val activity =
        requireNotNull(LocalContext.current.activity) {
            "Unable to find the activity. This is an internal error. Please report this issue."
        }

    LaunchedEffect(Unit) {
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
                        val bottomSheetState = uiState.bottomSheetState
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
            externalState.value = onEvent(activity, externalState.value, it)
        }
    }
    var contentPadding by remember {
        mutableStateOf<PaddingValues?>(null)
    }
    ModalBottomSheetLayout(
        sheetState = scrimBottomSheetState,
        dismissContentDescription = stringResource(id = R.string.ly_img_editor_close),
        sheetContent = {
            anyComposable?.let {
                it.Content()
                val window = (view.context as Activity).window
                DisposableEffect(Unit) {
                    window.navigationBarColor = surfaceColor
                    onDispose {
                        window.navigationBarColor = libraryColor
                    }
                }
            }
        },
        scrimEnabled = true,
        modifier = Modifier.systemBarsPadding(),
        sheetShape =
            RoundedCornerShape(
                topStart = 28.0.dp,
                topEnd = 28.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp,
            ),
    ) {
        val cornerRadius = if (uiState.bottomSheetState.swipeableState.offset != 0f) 28.dp else 0.dp
        ModalBottomSheetLayout(
            sheetState = uiState.bottomSheetState,
            modifier = Modifier.systemBarsPadding(),
            dismissContentDescription = stringResource(id = R.string.ly_img_editor_close),
            sheetContent = {
                val content = bottomSheetContent
                if (content != null) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        when (content) {
                            LibraryBottomSheetContent ->
                                AddLibraryTabsSheet(
                                    swipeableState = uiState.bottomSheetState.swipeableState,
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
                            else -> bottomSheetLayout(content)
                        }
                    }
                }
            },
            sheetShape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        ) {
            Scaffold(
                topBar = topBar,
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },
            ) {
                contentPadding = it
                BoxWithConstraints {
                    val orientation = LocalConfiguration.current.orientation
                    EngineCanvasView(
                        license = license,
                        userId = userId,
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
                                canvasOverlay(it)
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
                    Dock(
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
        overlay(externalState.value, viewModel)
    }
}
