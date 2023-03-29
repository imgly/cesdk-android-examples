package ly.img.cesdk.apparel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.components.EditingTextCard
import ly.img.cesdk.core.components.FullscreenLoader
import ly.img.cesdk.core.components.InternetErrorDialog
import ly.img.cesdk.core.components.UnsavedChangesAlertDialog
import ly.img.cesdk.core.components.actionmenu.CanvasActionMenu
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetLayout
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.core.iconpack.Add
import ly.img.cesdk.core.iconpack.Arrowback
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.theme.surface1
import ly.img.cesdk.core.theme.surface2
import ly.img.cesdk.core.utils.toPx
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.Dock
import ly.img.cesdk.dock.options.fillstroke.FillStrokeOptionsSheet
import ly.img.cesdk.dock.options.format.FormatOptionsSheet
import ly.img.cesdk.dock.options.layer.LayerOptionsSheet
import ly.img.cesdk.dock.options.shapeoptions.ShapeOptionsSheet
import ly.img.cesdk.engine.EngineCanvasView
import ly.img.cesdk.library.AddLibrarySheet
import ly.img.cesdk.library.ReplaceLibrarySheet
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, FlowPreview::class)
@Composable
fun ApparelUi(
    navigationIcon: ImageVector = IconPack.Arrowback,
    sceneUri: Uri,
    goBack: () -> Boolean,
    viewModel: ApparelUiViewModel = viewModel()
) {
    val uiScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val bottomSheetContent by viewModel.bottomSheetContent.collectAsState()
    val canvasActionMenuUiState by viewModel.canvasActionMenuUiState.collectAsState()

    BackHandler(true) {
        viewModel.onEvent(Event.OnBackPress)
    }

    LaunchedEffect(bottomSheetContent?.getType()) {
        val sheetState = uiState.bottomSheetState
        if (bottomSheetContent == null && sheetState.isVisible) sheetState.snapTo(ModalBottomSheetValue.Hidden)
        bottomSheetContent ?: return@LaunchedEffect
        if (bottomSheetContent is BottomSheetContent.Replace) {
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
            } else if (bottomSheetContent is BottomSheetContent.Library) {
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
            if (uiState.isLoading || uiState.isInPreviewMode || offset == null) return@collectLatest
            val bottomSheetHeight = (swipeableState.maxOffset - offset).coerceAtMost(0.7f * swipeableState.maxOffset)
            val bottomSheetHeightInDp = (bottomSheetHeight / oneDpInPx).roundToInt()
            viewModel.onEvent(Event.OnBottomSheetHeightChange(bottomSheetHeightInDp))
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
            }
        }
    }

    if (uiState.showExitDialog) {
        UnsavedChangesAlertDialog(
            onDismiss = { viewModel.onEvent(Event.OnBack) },
            onConfirm = { viewModel.onEvent(Event.OnExit) }
        )
    }

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
                        BottomSheetContent.Library -> AddLibrarySheet(
                            swipeableState = uiState.bottomSheetState.swipeableState,
                            viewModel::onEvent
                        )
                        is BottomSheetContent.Replace -> ReplaceLibrarySheet(content.designBlock, content.blockType, viewModel::onEvent)
                        is BottomSheetContent.Layer -> LayerOptionsSheet(content.uiState, viewModel::onEvent)
                        is BottomSheetContent.FillStroke -> FillStrokeOptionsSheet(content.uiState, viewModel::onEvent)
                        is BottomSheetContent.Options -> ShapeOptionsSheet(content.uiState, viewModel::onEvent)
                        is BottomSheetContent.Format -> FormatOptionsSheet(content.uiState, viewModel::onEvent)
                    }
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    navigationIcon = navigationIcon,
                    onEvent = viewModel::onEvent,
                    isLoading = uiState.isLoading,
                    isInPreviewMode = uiState.isInPreviewMode,
                    isUndoEnabled = uiState.isUndoEnabled,
                    isRedoEnabled = uiState.isRedoEnabled
                )
            }
        ) {
            BoxWithConstraints {
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
                        viewModel.onEvent(Event.OnLoadScene(sceneUri.toString(), this.maxHeight.value, insets))
                    }
                )
                canvasActionMenuUiState?.let {
                    CanvasActionMenu(
                        uiState = it,
                        onEvent = viewModel::onEvent
                    )
                }
                if (!uiState.isInPreviewMode && uiState.selectedBlock == null) {
                    FloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        onClick = {
                            uiScope.launch {
                                viewModel.onEvent(Event.OnAddLibraryClick)
                                // FIXME: For reasons not completely clear, without this delay,
                                //  the bottom sheet doesn't completely expands and only half expands.
                                delay(16)
                                uiState.bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    ) {
                        Icon(IconPack.Add, stringResource(R.string.cesdk_library))
                    }
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
