package ly.img.cesdk.editorui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ly.img.cesdk.components.EditingTextCard
import ly.img.cesdk.components.FullscreenLoader
import ly.img.cesdk.components.InternetErrorDialog
import ly.img.cesdk.components.UnsavedChangesAlertDialog
import ly.img.cesdk.components.actionmenu.CanvasActionMenu
import ly.img.cesdk.core.library.AddLibrarySheet
import ly.img.cesdk.core.library.ReplaceLibrarySheet
import ly.img.cesdk.core.theme.surface1
import ly.img.cesdk.core.theme.surface2
import ly.img.cesdk.core.ui.AnyComposable
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetLayout
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.core.ui.bottomsheet.rememberModalBottomSheetState
import ly.img.cesdk.core.ui.utils.toPx
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.Dock
import ly.img.cesdk.dock.FillStrokeBottomSheetContent
import ly.img.cesdk.dock.FormatBottomSheetContent
import ly.img.cesdk.dock.LayerBottomSheetContent
import ly.img.cesdk.dock.LibraryBottomSheetContent
import ly.img.cesdk.dock.OptionsBottomSheetContent
import ly.img.cesdk.dock.ReplaceBottomSheetContent
import ly.img.cesdk.dock.options.crop.CropBottomSheetContent
import ly.img.cesdk.dock.options.crop.CropSheet
import ly.img.cesdk.dock.options.fillstroke.FillStrokeOptionsSheet
import ly.img.cesdk.dock.options.format.FormatOptionsSheet
import ly.img.cesdk.dock.options.layer.LayerOptionsSheet
import ly.img.cesdk.dock.options.shapeoptions.ShapeOptionsSheet
import ly.img.cesdk.engine.EngineCanvasView
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorUi(
    sceneUri: Uri,
    goBack: () -> Boolean,
    uiState: EditorUiViewState,
    topBar: @Composable () -> Unit,
    canvasOverlay: @Composable BoxScope.() -> Unit,
    bottomSheetLayout: @Composable ColumnScope.(BottomSheetContent) -> Unit = {},
    viewModel: EditorUiViewModel
) {
    val uiScope = rememberCoroutineScope()
    val bottomSheetContent by viewModel.bottomSheetContent.collectAsState()
    val canvasActionMenuUiState by viewModel.canvasActionMenuUiState.collectAsState()

    BackHandler(true) {
        viewModel.onEvent(Event.OnBackPress)
    }

    LaunchedEffect(bottomSheetContent?.getType()) {
        val sheetState = uiState.bottomSheetState
        if (bottomSheetContent == null && sheetState.isVisible) sheetState.snapTo(ModalBottomSheetValue.Hidden)
        bottomSheetContent ?: return@LaunchedEffect
        if (bottomSheetContent is ReplaceBottomSheetContent) {
            sheetState.halfExpand()
        } else {
            sheetState.expand()
        }
    }

    val view = LocalView.current
    val surfaceColor = colorScheme.surface.toArgb()
    val canvasColor = colorScheme.surface1.toArgb()
    val libraryColor = colorScheme.surface2.toArgb()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = if (bottomSheetContent == null) {
                if (uiState.selectedBlock == null) {
                    canvasColor
                } else {
                    surfaceColor
                }
            } else if (bottomSheetContent is LibraryBottomSheetContent) {
                libraryColor
            } else {
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
            val bottomSheetHeightInDp = (bottomSheetHeight / oneDpInPx).roundToInt()
            viewModel.onEvent(Event.OnBottomSheetHeightChange(bottomSheetHeightInDp))
        }
    }

    val scrimBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var anyComposable: AnyComposable? by remember {
        mutableStateOf(null)
    }

    val showScrimBottomSheet = remember {
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

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is SingleEvent.ShareExport -> {
                    val file = it.file
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "application/pdf"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }

                is SingleEvent.Exit -> {
                    goBack()
                }

                is SingleEvent.ChangeSheetState -> {
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
            }
        }
    }

    if (uiState.showExitDialog) {
        UnsavedChangesAlertDialog(
            onDismiss = { viewModel.onEvent(Event.OnBack) },
            onConfirm = { viewModel.onEvent(Event.OnExit) }
        )
    }

    ModalBottomSheetLayout(
        sheetState = scrimBottomSheetState,
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
        sheetShape = RoundedCornerShape(
            topStart = 28.0.dp,
            topEnd = 28.0.dp,
            bottomEnd = 0.0.dp,
            bottomStart = 0.0.dp
        )
    ) {
        val cornerRadius = if (uiState.bottomSheetState.swipeableState.offset != 0f) 28.dp else 0.dp
        ModalBottomSheetLayout(
            sheetState = uiState.bottomSheetState,
            modifier = Modifier.systemBarsPadding(),
            sheetContent = {
                val content = bottomSheetContent
                if (content != null) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        when (content) {
                            LibraryBottomSheetContent -> AddLibrarySheet(
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
                                }
                            )

                            is ReplaceBottomSheetContent -> ReplaceLibrarySheet(
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
                                }
                            )

                            is LayerBottomSheetContent -> LayerOptionsSheet(content.uiState, viewModel::onEvent)
                            is FillStrokeBottomSheetContent -> FillStrokeOptionsSheet(content.uiState, viewModel::onEvent)
                            is OptionsBottomSheetContent -> ShapeOptionsSheet(content.uiState, viewModel::onEvent)
                            is FormatBottomSheetContent -> FormatOptionsSheet(content.uiState, viewModel::onEvent)
                            is CropBottomSheetContent -> CropSheet(content.uiState, viewModel::onEvent)
                            else -> bottomSheetLayout(content)
                        }
                    }
                }
            },
            sheetShape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        ) {
            Scaffold(
                topBar = topBar
            ) {
                BoxWithConstraints {
                    val orientation = LocalConfiguration.current.orientation
                    EngineCanvasView(
                        engine = viewModel.engine,
                        passTouches = !uiState.isInPreviewMode,
                        onMoveStart = { viewModel.onEvent(Event.OnCanvasMove(true)) },
                        onMoveEnd = { viewModel.onEvent(Event.OnCanvasMove(false)) },
                        loadScene = {
                            val topInsets = 64f + 16f // 64 for toolbar
                            val bottomInsets = 132f + 16f // 132 for dock
                            val sideInsets = 16f

                            val insets = Rect(
                                left = sideInsets,
                                top = topInsets,
                                right = sideInsets,
                                bottom = bottomInsets
                            )
                            viewModel.onEvent(
                                Event.OnLoadScene(
                                    sceneUri.toString(),
                                    maxHeight.value,
                                    insets,
                                    orientation != Configuration.ORIENTATION_LANDSCAPE
                                )
                            )
                        }
                    )
                    canvasOverlay()
                    canvasActionMenuUiState?.let {
                        CanvasActionMenu(
                            uiState = it,
                            onEvent = viewModel::onEvent
                        )
                    }
                    if (uiState.isEditingText) {
                        EditingTextCard(modifier = Modifier
                            .align(Alignment.BottomStart)
                            .onGloballyPositioned {
                                viewModel.onEvent(Event.OnKeyboardHeightChange((it.size.height / oneDpInPx).roundToInt()))
                            },
                            onClose = { viewModel.onEvent(Event.OnKeyboardClose) }
                        )
                    }
                    uiState.selectedBlock?.let {
                        Dock(
                            selectedBlock = it,
                            onClose = { viewModel.onEvent(Event.OnCloseDock) },
                            onClick = { viewModel.onEvent(Event.OnOptionClick(it)) },
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            FullscreenLoader()
        }

        if (uiState.errorLoading) {
            InternetErrorDialog {
                viewModel.onEvent(Event.OnExit)
            }
        }
    }
}
