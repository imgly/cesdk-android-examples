package ly.img.editor.design

import android.app.Activity
import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.rootdock.RootDockItem
import ly.img.editor.base.rootdock.RootDockItemActionType
import ly.img.editor.base.ui.EditorPagesDock
import ly.img.editor.base.ui.EditorPagesUi
import ly.img.editor.base.ui.EditorUi
import ly.img.editor.base.ui.EditorUiTabIconMappings
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.library.LibraryViewModel
import ly.img.editor.core.ui.library.resultcontract.GalleryMimeType
import ly.img.editor.core.ui.library.resultcontract.prepareUriForCameraLauncher
import ly.img.editor.core.ui.library.resultcontract.rememberCameraLauncherForActivityResult
import ly.img.editor.core.ui.library.resultcontract.rememberGalleryLauncherForActivityResult
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.editor.core.ui.utils.activity
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine

@Composable
fun DesignUi(
    initialExternalState: Parcelable,
    license: String,
    userId: String? = null,
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

    val libraryViewModel = viewModel<LibraryViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    EditorUi(
        initialExternalState = initialExternalState,
        license = license,
        userId = userId,
        renderTarget = renderTarget,
        uiState = uiState,
        overlay = overlay,
        onEvent = onEvent,
        close = close,
        topBar = {
            DesignUiToolbar(
                navigationIcon = navigationIcon,
                onEvent = viewModel::onEvent,
                pageCount = uiState.pageCount,
                isPagesScreenActive = uiState.pagesState != null,
                isUndoEnabled = uiState.isUndoEnabled,
                isRedoEnabled = uiState.isRedoEnabled,
            )
        },
        canvasOverlay = {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .height(84.dp),
                color = MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f),
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(top = 8.dp, bottom = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }
                    val rootBarItems = uiState.rootDockItems
                    val galleryLauncher = rememberGalleryLauncherForActivityResult(onEvent = libraryViewModel::onEvent)
                    val cameraLauncher =
                        rememberCameraLauncherForActivityResult(
                            captureVideo = false,
                            onCapture = {
                                libraryViewModel.onEvent(
                                    LibraryEvent.OnAddUri(
                                        assetSource = AssetSourceType.ImageUploads,
                                        uri = checkNotNull(uri),
                                    ),
                                )
                            },
                        )
                    rootBarItems.forEach {
                        RootDockItem(data = it) {
                            when (val actionType = it.type) {
                                RootDockItemActionType.OpenGallery -> {
                                    galleryLauncher.launch(GalleryMimeType.Image)
                                }

                                RootDockItemActionType.OpenCamera -> {
                                    uri = prepareUriForCameraLauncher(activity)
                                    cameraLauncher.launch(uri)
                                }

                                is RootDockItemActionType.OnEvent -> {
                                    viewModel.onEvent(actionType.event)
                                }
                            }
                        }
                    }
                }
            }
        },
        pagesOverlay = {
            EditorPagesUi(
                modifier =
                    Modifier
                        .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding() + 84.dp)
                        .fillMaxSize(),
                state = uiState.pagesState,
                onEvent = viewModel::onEvent,
            )
            EditorPagesDock(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = it.calculateBottomPadding())
                        .height(84.dp),
                state = uiState.pagesState,
                onEvent = viewModel::onEvent,
            )
        },
        viewModel = viewModel,
    )
}
