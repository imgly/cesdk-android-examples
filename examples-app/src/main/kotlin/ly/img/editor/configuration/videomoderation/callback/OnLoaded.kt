package ly.img.editor.configuration.videomoderation.callback

import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ly.img.editor.configuration.videomoderation.VideoModerationConfigurationBuilder
import ly.img.editor.core.component.data.Insets
import ly.img.engine.HistoryUpdate

/**
 * The callback that is invoked when the editor is loaded and ready to be used.
 */
suspend fun VideoModerationConfigurationBuilder.onLoaded() = coroutineScope {
    // Invalidate the cached results whenever the scene changes, so the next check reflects
    // the current content. Runs concurrently with the edit-mode observer.
    launch { observeSceneChanges() }
    observeEditorEditMode(
        extraInsets = {
            if (it == "Crop") {
                Insets(value = 24.dp)
            } else {
                Insets(horizontal = 16.dp, vertical = 8.dp)
            }
        },
    )
}

/**
 * Marks the cached results as stale on any undo/redo/edit. A trim or a move changes which frames of
 * a clip play, so the next check scans the scene again.
 */
private suspend fun VideoModerationConfigurationBuilder.observeSceneChanges() {
    // Activating another history changes no scene content, so it keeps the results.
    editorContext.engine.editor.onHistoryUpdatedWithKind().filter { it == HistoryUpdate.UPDATED }.collect {
        resultsFresh = false
        editGeneration += 1
    }
}
