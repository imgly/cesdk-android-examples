package ly.img.editor.design

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.ui.EditorPagesDock
import ly.img.editor.base.ui.EditorPagesUi
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
fun DesignUi(
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
            DesignUiViewModel(
                editorScope = editorScope,
                onCreate = onCreate,
                onExport = onExport,
                onClose = onClose,
                onError = onError,
                libraryViewModel = libraryViewModel,
            )
        }

    val uiState by viewModel.uiState.collectAsState()
    val editorContext = editorScope.run { editorContext }
    EditorUi(
        initialExternalState = initialExternalState,
        renderTarget = renderTarget,
        uiState = uiState,
        editorScope = editorScope,
        editorContext = editorContext,
        onEvent = onEvent,
        close = close,
        topBar = {
            DesignUiToolbar(
                navigationIcon = editorContext.navigationIcon,
                onEvent = viewModel::send,
                pageCount = uiState.pageCount,
                isPagesScreenActive = uiState.pagesState != null,
                isUndoEnabled = uiState.isUndoEnabled,
                isRedoEnabled = uiState.isRedoEnabled,
            )
        },
        canvasOverlay = {
            if (uiState.isDockVisible) {
                editorContext.dock?.let {
                    Box(modifier = Modifier.align(Alignment.BottomStart)) {
                        EditorComponent(component = it(editorScope))
                    }
                }
            }
        },
        pagesOverlay = {
            EditorPagesUi(
                modifier =
                    Modifier
                        .statusBarsPadding()
                        .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding() + 84.dp)
                        .fillMaxSize(),
                state = uiState.pagesState,
                onEvent = viewModel::send,
            )
            EditorPagesDock(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = it.calculateBottomPadding())
                        .height(84.dp),
                state = uiState.pagesState,
                onEvent = viewModel::send,
            )
        },
        viewModel = viewModel,
    )
}
