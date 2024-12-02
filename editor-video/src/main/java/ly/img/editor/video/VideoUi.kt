package ly.img.editor.video

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.ui.EditorUi
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.library.LibraryViewModel
import ly.img.editor.core.ui.utils.activity
import ly.img.engine.AssetDefinition

@Composable
fun VideoUi(
    initialExternalState: Parcelable,
    renderTarget: EngineRenderTarget,
    editorScope: EditorScope,
    onCreate: suspend EditorScope.() -> Unit,
    onExport: suspend EditorScope.() -> Unit,
    onUpload: suspend EditorScope.(AssetDefinition, UploadAssetSourceType) -> AssetDefinition,
    onClose: suspend EditorScope.(Boolean) -> Unit,
    onError: suspend EditorScope.(Throwable) -> Unit,
    onEvent: EditorScope.(Parcelable, EditorEvent) -> Parcelable,
    close: (Throwable?) -> Unit,
) {
    val activity = requireNotNull(LocalContext.current.activity)
    val editorContext = editorScope.run { editorContext }
    remember {
        Environment.init(activity.application)
        mutableStateOf(Unit)
    }

    val libraryViewModel =
        viewModel {
            LibraryViewModel(
                editorScope = editorScope,
                onUpload = onUpload,
            )
        }
    val viewModel =
        viewModel {
            VideoUiViewModel(
                editorScope = editorScope,
                onCreate = onCreate,
                onExport = onExport,
                onClose = onClose,
                onError = onError,
                libraryViewModel = libraryViewModel,
            )
        }

    val uiState by viewModel.uiState.collectAsState()
    EditorUi(
        initialExternalState = initialExternalState,
        renderTarget = renderTarget,
        uiState = uiState.editorUiViewState,
        editorScope = editorScope,
        editorContext = editorContext,
        onEvent = onEvent,
        topBar = {
            VideoUiToolbar(
                navigationIcon = editorContext.navigationIcon,
                onEvent = viewModel::send,
                isUndoEnabled = uiState.editorUiViewState.isUndoEnabled,
                isRedoEnabled = uiState.editorUiViewState.isRedoEnabled,
                isExportEnabled = uiState.canExport,
            )
        },
        canvasOverlay = {
            if (uiState.editorUiViewState.isDockVisible) {
                editorContext.dock?.let {
                    Box(modifier = Modifier.align(Alignment.BottomStart)) {
                        EditorComponent(component = it(editorScope))
                    }
                }
            }
        },
        viewModel = viewModel,
        close = close,
    )
}
