package ly.img.editor.configuration.memories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.configuration.memories.callback.onCreateConfiguration
import ly.img.editor.configuration.memories.callback.onExport
import ly.img.editor.configuration.memories.component.bottomPanelConfiguration
import ly.img.editor.configuration.memories.component.dockConfiguration
import ly.img.editor.configuration.memories.component.navigationBarConfiguration
import ly.img.editor.configuration.memories.component.rememberOverlay
import ly.img.editor.configuration.memories.model.ExportStatus

class MemoriesConfiguration(
    private val viewModel: MemoriesViewModel,
) : BasicConfigurationBuilder() {
    /** Drives the export progress overlay; null when no export is running. */
    var exportStatus: ExportStatus? by editorContext.mutableStateOf(key = KEY_EXPORT_STATUS, initial = null)

    init {
        onCreate = onCreateConfiguration(viewModel)
        dock = dockConfiguration(viewModel)
        bottomPanel = bottomPanelConfiguration(viewModel)
        navigationBar = navigationBarConfiguration(viewModel)
        overlay = { rememberOverlay(viewModel) }
        onExport = { onExport() }
        // If onCreate (or any editor step) fails, never leave the loading overlay stuck: dismiss it
        // and surface the failure through the standard error dialog (BasicConfigurationBuilder.Overlay).
        onError = {
            viewModel.setEditorLoading(false)
            error = it
        }
    }

    private companion object {
        const val KEY_EXPORT_STATUS = "ly.img.editor.configuration.memories.exportStatus"
    }
}
