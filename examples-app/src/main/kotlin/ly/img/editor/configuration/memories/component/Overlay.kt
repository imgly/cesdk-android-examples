package ly.img.editor.configuration.memories.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import ly.img.editor.configuration.memories.MemoriesConfiguration
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.configuration.memories.widget.FullscreenVideoControls
import ly.img.editor.configuration.memories.widget.ImageGridOverlay
import ly.img.editor.core.EditorContext
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember

@Composable
fun MemoriesConfiguration.rememberOverlay(viewModel: MemoriesViewModel) = EditorComponent.remember {
    decoration = {
        Box(Modifier.fillMaxSize()) {
            Overlay(viewModel, editorContext)
            Overlay(loading = {})
            ExportOverlay()
        }
    }
}

@Composable
private fun Overlay(
    viewModel: MemoriesViewModel,
    editorContext: EditorContext,
) {
    val showImageGrid by viewModel.showImageGrid.collectAsState()
    val gridImages by viewModel.gridImages.collectAsState()
    val imagesToDelete by viewModel.imagesToDelete.collectAsState()
    val multiSelectMode by viewModel.multiSelectMode.collectAsState()
    val isPreviewMode by viewModel.isPreviewMode.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isSortEnabled by viewModel.overlaySortEnabled.collectAsState()
    val isSortAscending by viewModel.overlaySortAscending.collectAsState()

    if (showImageGrid) {
        ImageGridOverlay(
            images = gridImages,
            onImagesChanged = { viewModel.updateGridImages(it) },
            onAddImages = { viewModel.addImagesToOverlay(it) },
            onClose = { viewModel.cancelOverlay() },
            onApply = { viewModel.applyOverlayChanges(editorContext) },
            selectedForDeletion = imagesToDelete,
            onToggleSelection = { viewModel.toggleImageForDeletion(it) },
            isSortEnabled = isSortEnabled,
            isSortAscending = isSortAscending,
            onToggleSort = { viewModel.toggleOverlaySort() },
            onDeleteSelected = { viewModel.deleteSelectedFromOverlay() },
            onClearAll = { viewModel.clearOverlayImages() },
            multiSelectMode = multiSelectMode,
            onToggleMultiSelect = { viewModel.toggleMultiSelect() },
        )
    } else if (isPreviewMode) {
        var progress by remember { mutableStateOf(0f) }
        var totalDuration by remember { mutableStateOf(1f) }

        // Update progress from playback
        LaunchedEffect(isPlaying) {
            if (isPlaying) {
                while (isPlaying) {
                    try {
                        val engine = editorContext.engine
                        val page = engine.scene.getCurrentPage()
                        if (page != null) {
                            val currentTime = engine.block.getPlaybackTime(page).toFloat()
                            totalDuration = engine.block.getDuration(page).toFloat()
                            progress = if (totalDuration > 0) currentTime / totalDuration else 0f
                        }
                    } catch (e: Exception) {
                        // Transient (e.g. a scene/view-mode transition); values are re-polled in 100ms,
                        // so swallow rather than spam a log every frame.
                    }
                    delay(100) // Update every 100ms
                }
            }
        }

        // Full screen transparent overlay for preview mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable {
                    viewModel.togglePreviewMode()
                },
        ) {
            FullscreenVideoControls(
                isPlaying = isPlaying,
                progress = progress,
                currentTime = (progress * totalDuration).toDouble(),
                totalDuration = totalDuration.toDouble(),
                onPlayPauseClick = {
                    val newPlayingState = !isPlaying
                    viewModel.setPlaying(newPlayingState)
                    try {
                        val engine = editorContext.engine
                        val page = engine.scene.getCurrentPage()
                        if (page != null) {
                            engine.block.setPlaying(page, newPlayingState)
                        }
                    } catch (e: Exception) {
                        Log.w("MemoriesOverlay", "Could not toggle playback", e)
                    }
                },
                onSeek = { seekProgress ->
                    try {
                        val engine = editorContext.engine
                        val page = engine.scene.getCurrentPage()
                        if (page != null) {
                            val seekTime = (seekProgress * totalDuration).toDouble()
                            engine.block.setPlaybackTime(page, seekTime)
                            progress = seekProgress
                        }
                    } catch (e: Exception) {
                        Log.w("MemoriesOverlay", "Could not seek playback", e)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
