package ly.img.editor.configuration.video.callback

import androidx.compose.ui.unit.dp
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.component.data.Insets

/**
 * The callback that is invoked when the editor is loaded and ready to be used.
 */
suspend fun VideoConfigurationBuilder.onLoaded() {
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
