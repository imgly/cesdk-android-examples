package ly.img.editor.configuration.design.callback

import ly.img.editor.configuration.design.DesignConfigurationBuilder

/**
 * The callback that is invoked when the editor is loaded and ready to be used.
 */
suspend fun DesignConfigurationBuilder.onLoaded() {
    observeEditorEditMode()
}
