package ly.img.editor.apparel

import android.app.Activity
import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import ly.img.editor.base.components.color_picker.LibraryButton
import ly.img.editor.base.ui.EditorUi
import ly.img.editor.base.ui.EditorUiTabIconMappings
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.utils.activity
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine

@Composable
fun ApparelUi(
    initialExternalState: Parcelable,
    license: String,
    userId: String?,
    renderTarget: EngineRenderTarget,
    navigationIcon: ImageVector,
    baseUri: Uri,
    colorPalette: List<Color>,
    assetLibrary: AssetLibrary,
    onCreate: suspend (Engine, EditorEventHandler) -> Unit,
    onExport: suspend (Engine, EditorEventHandler) -> Unit,
    onUpload: suspend AssetDefinition.(Engine, EditorEventHandler, UploadAssetSourceType) -> AssetDefinition,
    onClose: suspend (Engine, Boolean, EditorEventHandler) -> Unit,
    onError: suspend (Throwable, Engine, EditorEventHandler) -> Unit,
    onEvent: (Activity, Parcelable, EditorEvent) -> Parcelable,
    overlay: @Composable ((Parcelable, EditorEventHandler) -> Unit),
    close: (Throwable?) -> Unit,
) {
    val activity = requireNotNull(LocalContext.current.activity)
    remember {
        Environment.init(activity.application)
        mutableStateOf(Unit)
    }

    val viewModel =
        viewModel {
            ApparelUiViewModel(
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
        renderTarget = renderTarget,
        uiState = uiState,
        overlay = overlay,
        onEvent = onEvent,
        topBar = {
            ApparelUiToolbar(
                navigationIcon = navigationIcon,
                onEvent = viewModel::onEvent,
                isInPreviewMode = uiState.isInPreviewMode,
                isUndoEnabled = uiState.isUndoEnabled,
                isRedoEnabled = uiState.isRedoEnabled,
            )
        },
        canvasOverlay = {
            if (!uiState.isInPreviewMode && uiState.selectedBlock == null) {
                LibraryButton(
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                    uiScope = uiScope,
                    bottomSheetState = uiState.bottomSheetState,
                    onEvent = viewModel::onEvent,
                )
            }
        },
        viewModel = viewModel,
        close = close,
    )
}
