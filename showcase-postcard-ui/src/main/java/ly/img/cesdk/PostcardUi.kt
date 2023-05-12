package ly.img.cesdk

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.bottomsheet.message_color.MessageColorBottomSheetContent
import ly.img.cesdk.bottomsheet.message_color.MessageColorSheet
import ly.img.cesdk.bottomsheet.message_font.MessageFontBottomSheetContent
import ly.img.cesdk.bottomsheet.message_font.MessageFontSheet
import ly.img.cesdk.bottomsheet.message_size.MessageSizeBottomSheetContent
import ly.img.cesdk.bottomsheet.message_size.MessageSizeSheet
import ly.img.cesdk.bottomsheet.template_colors.TemplateColorsBottomSheetContent
import ly.img.cesdk.bottomsheet.template_colors.TemplateColorsSheet
import ly.img.cesdk.core.components.color_picker.LibraryButton
import ly.img.cesdk.core.iconpack.Arrowback
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.theme.surface1
import ly.img.cesdk.editorui.EditorUi
import ly.img.cesdk.rootbar.RootBarItem
import ly.img.cesdk.rootbar.tab_icons.PostcardUiTabIconMappings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostcardUi(
    navigationIcon: ImageVector = IconPack.Arrowback,
    sceneUri: Uri,
    goBack: () -> Boolean,
    viewModel: PostcardUiViewModel = viewModel()
) {
    val initSetup by remember {
        Environment.tabIconMappings = PostcardUiTabIconMappings()
        mutableStateOf(Unit)
    }

    val uiScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    EditorUi(
        sceneUri = sceneUri,
        goBack = goBack,
        uiState = uiState.editorUiViewState,
        topBar = {
            PostcardUiToolbar(
                navigationIcon = navigationIcon,
                onEvent = viewModel::onEvent,
                postcardMode = uiState.postcardMode,
                isLoading = uiState.editorUiViewState.isLoading,
                isInPreviewMode = uiState.editorUiViewState.isInPreviewMode,
                isUndoEnabled = uiState.editorUiViewState.isUndoEnabled,
                isRedoEnabled = uiState.editorUiViewState.isRedoEnabled
            )
        },
        canvasOverlay = {
            if (!uiState.editorUiViewState.isInPreviewMode && uiState.editorUiViewState.selectedBlock == null) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .height(84.dp),
                    color = MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f)
                ) {
                    if (uiState.postcardMode == PostcardMode.Design) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LibraryButton(
                                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                                uiScope = uiScope,
                                bottomSheetState = uiState.editorUiViewState.bottomSheetState,
                                onEvent = viewModel::onEvent
                            )

                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            uiState.rootBarItems.forEach {
                                RootBarItem(data = it, onEvent = viewModel::onEvent)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
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
        }
    )
}