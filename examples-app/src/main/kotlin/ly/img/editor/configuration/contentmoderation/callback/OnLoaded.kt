package ly.img.editor.configuration.contentmoderation.callback

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.contentmoderation.ContentModerationConfigurationBuilder

/**
 * The callback that is invoked when the editor is loaded and ready to be used.
 */
suspend fun ContentModerationConfigurationBuilder.onLoaded() = coroutineScope {
    // Invalidate the cached results whenever the scene changes, so the next check reflects
    // the current content. Runs concurrently with the edit-mode observer.
    launch { observeSceneChanges() }
    observeEditorEditMode()
}

/**
 * Marks the cached results as stale on any undo/redo/edit. A subsequent check will then
 * call the moderation service again instead of reusing the previous results.
 */
private suspend fun ContentModerationConfigurationBuilder.observeSceneChanges() {
    editorContext.engine.editor.onHistoryUpdated().collect {
        resultsFresh = false
        editGeneration += 1
    }
}
