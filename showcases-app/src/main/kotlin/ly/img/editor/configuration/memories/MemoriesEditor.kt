package ly.img.editor.configuration.memories

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import ly.img.editor.Editor
import ly.img.editor.configuration.memories.screen.LoadingScreen
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

/**
 * Hosts the slideshow [Editor] for the images staged in [viewModel], with the loading overlay
 * shown while the scene is built and back handling for preview / image-grid overlay.
 *
 * @param license CE.SDK license key, or null for evaluation mode (adds a watermark).
 */
@Composable
fun MemoriesEditor(
    license: String?,
    viewModel: MemoriesViewModel,
    onCloseEditor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedImages by viewModel.selectedImages.collectAsState()
    val isLoading by viewModel.isEditorLoading.collectAsState()
    val showImageGrid by viewModel.showImageGrid.collectAsState()
    val isPreviewMode by viewModel.isPreviewMode.collectAsState()

    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        label = "editor_alpha",
    )

    // Release the editor context when the editor leaves composition (back, close, process death),
    // so the ViewModel doesn't keep acting on a disposed editor afterwards.
    DisposableEffect(viewModel) {
        onDispose { viewModel.clearEditorContext() }
    }

    // Factory typed as Function0 so it binds to the generic remember(builder, block) overload.
    val configurationBuilder: () -> MemoriesConfiguration = { MemoriesConfiguration(viewModel) }

    Box(modifier = modifier.fillMaxSize().alpha(alpha)) {
        Editor(
            license = license,
            configuration = { EditorConfiguration.remember(configurationBuilder) },
            onClose = { onCloseEditor() },
        )
    }

    if (isLoading) {
        LoadingScreen(
            selectedImages = selectedImages,
            modifier = modifier.fillMaxSize(),
        )
    }

    // Back handling (registered after the Editor, so these intercept first).
    // In fullscreen/preview: exit preview (applies nothing).
    BackHandler(enabled = isPreviewMode) {
        viewModel.togglePreviewMode()
    }
    // Image grid overlay open: close it, discarding staged edits.
    BackHandler(enabled = !isPreviewMode && showImageGrid) {
        viewModel.cancelOverlay()
    }
    BackHandler(enabled = !isPreviewMode && !showImageGrid) {
        onCloseEditor()
    }
    // Otherwise the editor handles back internally (closing sheets), falling back to onClose,
    // which returns to the image selection screen.
}
