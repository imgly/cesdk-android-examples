package ly.img.cesdk

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ly.img.cesdk.core.components.color_picker.LibraryButton
import ly.img.cesdk.core.iconpack.Arrowback
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.editorui.EditorUi
import ly.img.cesdk.editorui.EditorUiTabIconMappings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApparelUi(
    navigationIcon: ImageVector = IconPack.Arrowback,
    sceneUri: Uri,
    goBack: () -> Boolean,
    viewModel: ApparelUiViewModel = viewModel()
) {
    val initSetup by remember {
        Environment.tabIconMappings = EditorUiTabIconMappings()
        mutableStateOf(Unit)
    }

    val uiScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    EditorUi(
        sceneUri = sceneUri,
        goBack = goBack,
        uiState = uiState,
        topBar = {
            ApparelUiToolbar(
                navigationIcon = navigationIcon,
                onEvent = viewModel::onEvent,
                isLoading = uiState.isLoading,
                isInPreviewMode = uiState.isInPreviewMode,
                isUndoEnabled = uiState.isUndoEnabled,
                isRedoEnabled = uiState.isRedoEnabled
            )
        },
        canvasOverlay = {
            if (!uiState.isInPreviewMode && uiState.selectedBlock == null) {
                LibraryButton(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    uiScope = uiScope,
                    bottomSheetState = uiState.bottomSheetState,
                    onEvent = viewModel::onEvent
                )
            }
        },
        viewModel = viewModel
    )
}
