package ly.img.editor.configuration.memories.scene

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.core.EditorContext
import ly.img.engine.DesignBlockEvent
import ly.img.engine.Engine

private const val TAG = "MemoriesEvents"

private const val AUDIO_TYPE = "//ly.img.ubq/audio"
private const val MIN_LOAD_MILLIS = 2000L

internal fun setupEventSubscriptions(
    engine: Engine,
    editorContext: EditorContext,
    viewModel: MemoriesViewModel,
) {
    subscribeMinimumLoad(editorContext, viewModel)
    subscribeSingleAudioTrack(engine, editorContext)
}

/** Hold the loading screen for a minimum time so it doesn't flash. */
private fun subscribeMinimumLoad(
    editorContext: EditorContext,
    viewModel: MemoriesViewModel,
) {
    editorContext.coroutineScope.launch {
        val elapsed = System.currentTimeMillis() - viewModel.loadingStartTime.first()
        if (elapsed < MIN_LOAD_MILLIS) delay(MIN_LOAD_MILLIS - elapsed)
        viewModel.setEditorLoading(false)
    }
}

/** Keep a single audio track: destroy older audio blocks when a new one is added. */
private fun subscribeSingleAudioTrack(
    engine: Engine,
    editorContext: EditorContext,
) {
    editorContext.coroutineScope.launch {
        engine.event.subscribe(listOf()).collect { events ->
            events.filter { it.type == DesignBlockEvent.Type.CREATED }
                .filter { runCatching { engine.block.getType(it.block) == AUDIO_TYPE }.getOrDefault(false) }
                .forEach { event ->
                    try {
                        val parent = engine.block.getParent(event.block) ?: return@forEach
                        engine.block.getChildren(parent)
                            .filter { it != event.block && engine.block.getType(it) == AUDIO_TYPE }
                            .forEach { engine.block.destroy(it) }
                    } catch (e: Exception) {
                        Log.w(TAG, "Could not enforce single audio track", e)
                    }
                }
        }
    }
}
