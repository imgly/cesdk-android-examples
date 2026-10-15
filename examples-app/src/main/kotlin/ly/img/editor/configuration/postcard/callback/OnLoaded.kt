package ly.img.editor.configuration.postcard.callback

import android.content.res.Configuration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ly.img.editor.configuration.postcard.PostcardConfigurationBuilder
import ly.img.editor.core.state.EditorViewMode
import ly.img.engine.DesignBlockType

/**
 * The callback that is invoked when the editor is loaded and ready to be used.
 */
suspend fun PostcardConfigurationBuilder.onLoaded() {
    coroutineScope {
        launch { observeEditorViewMode() }
        launch { observeEditorEditMode() }
    }
}

/**
 * Function that displays both pages vertically stacked and zooms out so that both of the pages are visible
 * when the [EditorViewMode] is preview.
 */
suspend fun PostcardConfigurationBuilder.observeEditorViewMode(stackSpacing: Float = 16F) {
    editorContext.state
        .distinctUntilChangedBy { it.viewMode to it.insets }
        .filter { it.viewMode is EditorViewMode.Preview }
        .collect { state ->
            val engine = editorContext.engine
            // Display pages vertically stacked
            val axis = if (editorContext.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                "Horizontal"
            } else {
                "Vertical"
            }
            engine.block
                .findByType(DesignBlockType.Stack)
                .firstOrNull()
                ?.let {
                    engine.block.setEnum(block = it, property = "stack/axis", value = axis)
                    engine.block.setFloat(block = it, property = "stack/spacing", value = stackSpacing)
                }
            // Display both pages in preview mode
            engine.scene.getPages().forEach {
                engine.block.setVisible(block = it, visible = true)
            }
            engine.scene.immediateZoomToBlock(
                block = requireNotNull(engine.scene.get()),
                paddingLeft = state.insets.left.value,
                paddingTop = state.insets.top.value,
                paddingRight = state.insets.right.value,
                paddingBottom = 0F,
                forceUpdate = true,
            )
        }
}
