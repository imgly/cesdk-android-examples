package ly.img.editor.design

import android.app.Activity
import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.components.PageNavigation
import ly.img.editor.base.components.color_picker.LibraryButton
import ly.img.editor.base.ui.EditorUi
import ly.img.editor.base.ui.EditorUiTabIconMappings
import ly.img.editor.base.ui.Event
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.utils.activity
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignUi(
    initialExternalState: Parcelable,
    license: String,
    userId: String? = null,
    navigationIcon: ImageVector,
    baseUri: Uri,
    colorPalette: List<Color>,
    assetLibrary: AssetLibrary,
    onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    onExport: suspend (Engine, EditorEventHandler) -> Unit,
    onUpload: suspend AssetDefinition.(Engine, EditorEventHandler, UploadAssetSourceType) -> AssetDefinition,
    onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit,
    onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit,
    onEvent: (Activity, MutableState<out Parcelable>, EditorEvent) -> Unit,
    overlay: @Composable ((Parcelable, EditorEventHandler) -> Unit),
    close: () -> Unit,
) {
    val activity = requireNotNull(LocalContext.current.activity)
    remember {
        Environment.init(activity.application)
        mutableStateOf(Unit)
    }

    val viewModel =
        viewModel {
            DesignUiViewModel(
                baseUri = baseUri,
                onCreate = onCreate,
                onExport = onExport,
                onClose = onClose,
                onError = onError,
                colorPalette = colorPalette,
            )
        }

    // cannot combine remember blocks because onUpload requires viewModel instance
    remember {
        Environment.tabIconMappings = EditorUiTabIconMappings()
        Environment.assetLibrary = assetLibrary
        Environment.onUpload = { engine, assetSourceType ->
            onUpload(this, engine, viewModel, assetSourceType)
        }
        mutableStateOf(Unit)
    }

    val uiScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    EditorUi(
        initialExternalState = initialExternalState,
        license = license,
        userId = userId,
        uiState = uiState.editorUiViewState,
        overlay = overlay,
        onEvent = onEvent,
        close = close,
        topBar = {
            DesignUiToolbar(
                navigationIcon = navigationIcon,
                onEvent = viewModel::onEvent,
                isLoading = uiState.editorUiViewState.isLoading,
                isInPreviewMode = uiState.editorUiViewState.isInPreviewMode,
                isUndoEnabled = uiState.editorUiViewState.isUndoEnabled,
                isRedoEnabled = uiState.editorUiViewState.isRedoEnabled,
            )
        },
        canvasOverlay = {
            Column(
                Modifier.fillMaxWidth().align(Alignment.BottomStart),
            ) {
                if (uiState.editorUiViewState.pageCount > 1) {
                    PageNavigation(
                        visible = !uiState.editorUiViewState.isInPreviewMode && !uiState.isZoomedIn,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        pageIndex = uiState.pageIndex,
                        pageCount = uiState.editorUiViewState.pageCount,
                        onNextClick = { viewModel.onEvent(Event.OnNextPage) },
                        onPreviousClick = { viewModel.onEvent(Event.OnPreviousPage) },
                    )
                }
                if (!uiState.editorUiViewState.isInPreviewMode && uiState.editorUiViewState.selectedBlock == null) {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        color = MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LibraryButton(
                                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                                uiScope = uiScope,
                                bottomSheetState = uiState.editorUiViewState.bottomSheetState,
                                onEvent = viewModel::onEvent,
                            )
                        }
                    }
                }
            }
        },
        viewModel = viewModel,
    )
}
