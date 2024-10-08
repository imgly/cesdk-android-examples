package ly.img.editor.postcard

import android.app.Activity
import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.components.LibraryButton
import ly.img.editor.base.ui.EditorUi
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.utils.activity
import ly.img.editor.postcard.bottomsheet.message_color.MessageColorBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_color.MessageColorSheet
import ly.img.editor.postcard.bottomsheet.message_font.MessageFontBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_font.MessageFontSheet
import ly.img.editor.postcard.bottomsheet.message_size.MessageSizeBottomSheetContent
import ly.img.editor.postcard.bottomsheet.message_size.MessageSizeSheet
import ly.img.editor.postcard.bottomsheet.template_colors.TemplateColorsBottomSheetContent
import ly.img.editor.postcard.bottomsheet.template_colors.TemplateColorsSheet
import ly.img.editor.postcard.rootbar.RootBarItem
import ly.img.editor.postcard.rootbar.tab_icons.PostcardUiTabIconMappings
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine

@Composable
fun PostcardUi(
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
            PostcardUiViewModel(
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
        Environment.tabIconMappings = PostcardUiTabIconMappings()
        Environment.assetLibrary = assetLibrary
        Environment.onUpload = { engine, assetSourceType ->
            onUpload(this, engine, viewModel, assetSourceType)
        }
        mutableStateOf(Unit)
    }

    val uiState by viewModel.uiState.collectAsState()

    EditorUi(
        initialExternalState = initialExternalState,
        license = license,
        userId = userId,
        renderTarget = renderTarget,
        uiState = uiState.editorUiViewState,
        overlay = overlay,
        onEvent = onEvent,
        topBar = {
            PostcardUiToolbar(
                navigationIcon = navigationIcon,
                onEvent = viewModel::onEvent,
                postcardMode = uiState.postcardMode,
                isInPreviewMode = uiState.editorUiViewState.isInPreviewMode,
                isUndoEnabled = uiState.editorUiViewState.isUndoEnabled,
                isRedoEnabled = uiState.editorUiViewState.isRedoEnabled,
            )
        },
        canvasOverlay = {
            if (!uiState.editorUiViewState.isInPreviewMode) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .height(84.dp),
                    color = MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f),
                ) {
                    if (uiState.postcardMode == PostcardMode.Design) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LibraryButton(
                                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                                onEvent = viewModel::onEvent,
                            )

                            Divider(
                                modifier =
                                    Modifier
                                        .height(32.dp)
                                        .width(1.dp),
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            uiState.rootBarItems.forEach {
                                RootBarItem(data = it, onEvent = viewModel::onEvent)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            uiState.rootBarItems.forEach {
                                RootBarItem(data = it, onEvent = viewModel::onEvent)
                            }
                        }
                    }
                }
            }
        },
        viewModel = viewModel,
        bottomSheetLayout = {
            when (it) {
                is MessageFontBottomSheetContent -> MessageFontSheet(uiState = it.uiState, onEvent = viewModel::onEvent)
                is MessageSizeBottomSheetContent -> MessageSizeSheet(messageSize = it.messageSize, onEvent = viewModel::onEvent)
                is MessageColorBottomSheetContent -> MessageColorSheet(color = it.color, onEvent = viewModel::onEvent)
                is TemplateColorsBottomSheetContent -> TemplateColorsSheet(uiState = it.uiState, onEvent = viewModel::onEvent)
            }
        },
        close = close,
    )
}
