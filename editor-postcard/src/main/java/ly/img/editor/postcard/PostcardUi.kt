package ly.img.editor.postcard

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.components.LibraryButton
import ly.img.editor.base.ui.EditorUi
import ly.img.editor.core.EditorScope
import ly.img.editor.core.engine.EngineRenderTarget
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.library.LibraryViewModel
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
import ly.img.engine.AssetDefinition

@Composable
fun PostcardUi(
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
            PostcardUiViewModel(
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
        uiState = uiState.editorUiViewState,
        editorScope = editorScope,
        editorContext = editorContext,
        onEvent = onEvent,
        topBar = {
            PostcardUiToolbar(
                navigationIcon = editorContext.navigationIcon,
                onEvent = viewModel::send,
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
                                onEvent = viewModel::send,
                            )

                            Divider(
                                modifier =
                                    Modifier
                                        .height(32.dp)
                                        .width(1.dp),
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            uiState.rootBarItems.forEach {
                                RootBarItem(data = it, onEvent = viewModel::send)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            uiState.rootBarItems.forEach {
                                RootBarItem(data = it, onEvent = viewModel::send)
                            }
                        }
                    }
                }
            }
        },
        viewModel = viewModel,
        bottomSheetLayout = { content, onColorPickerActiveChanged ->
            when (content) {
                is MessageFontBottomSheetContent ->
                    MessageFontSheet(
                        uiState = content.uiState,
                        onEvent = viewModel::send,
                    )
                is MessageSizeBottomSheetContent ->
                    MessageSizeSheet(
                        messageSize = content.messageSize,
                        onEvent = viewModel::send,
                    )
                is MessageColorBottomSheetContent ->
                    MessageColorSheet(
                        color = content.color,
                        onColorPickerActiveChanged = onColorPickerActiveChanged,
                        onEvent = viewModel::send,
                    )
                is TemplateColorsBottomSheetContent ->
                    TemplateColorsSheet(
                        uiState = content.uiState,
                        onColorPickerActiveChanged = onColorPickerActiveChanged,
                        onEvent = viewModel::send,
                    )
            }
        },
        close = close,
    )
}
