package ly.img.editor.configuration.video

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.configuration.video.callback.onExport
import ly.img.editor.configuration.video.callback.onLoaded
import ly.img.editor.configuration.video.component.rememberBottomPanel
import ly.img.editor.configuration.video.component.rememberCanvasMenu
import ly.img.editor.configuration.video.component.rememberDock
import ly.img.editor.configuration.video.component.rememberInspectorBar
import ly.img.editor.configuration.video.component.rememberNavigationBar
import ly.img.editor.configuration.video.component.rememberOverlay
import ly.img.editor.configuration.video.model.ExportStatus
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ScopedProperty
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.event.EditorEvent

/**
 * Configuration builder class for video editor starter kit.
 */
@Stable
class VideoConfigurationBuilder : BasicConfigurationBuilder() {
    /**
     * Current export status of the editor.
     */
    var exportStatus: ExportStatus? by editorContext.mutableStateOf(
        key = "ly.img.editor.configuration.video.state.exportStatus",
        initial = null,
    )

    /**
     * Whether export cancellation confirmation dialog should be displayed.
     */
    var showCancelExportConfirmationDialog: Boolean by editorContext.mutableStateOf(
        key = "ly.img.editor.configuration.video.state.showCancelExportConfirmationDialog",
        initial = false,
    )

    /**
     * The callback that is invoked when the editor is created.
     * Check parent class declaration for more details.
     */
    override var onCreate: (suspend EditorScope.() -> Unit)? = {
        onCreate()
    }

    /**
     * The callback that is invoked when the editor is loaded and ready to be used.
     * Check parent class declaration for more details.
     */
    override var onLoaded: (suspend EditorScope.() -> Unit)? = {
        onLoaded()
    }

    /**
     * The callback that is invoked when the export button is clicked.
     * Check parent class declaration for more details.
     */
    override var onExport: (suspend EditorScope.() -> Unit)? = {
        onExport()
    }

    /**
     * The callback that is invoked after [EditorEvent.OnClose] event is triggered or when the system back button is clicked
     * and editor cannot handle the event internally.
     * Check parent class declaration for more details.
     */
    override var onClose: (suspend EditorScope.() -> Unit)? = {
        showConfirmationOrCloseEditor()
    }

    /**
     * The callback that is invoked after the editor captures an error.
     * Check parent class declaration for more details.
     */
    override var onError: (suspend EditorScope.(Throwable) -> Unit)? = {
        error = it
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items at the bottom of the editor.
     * Check parent class declaration for more details.
     */
    override var dock: ScopedProperty<EditorScope, EditorComponent<*>>? = {
        rememberDock()
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items at the top of the editor.
     * Check parent class declaration for more details.
     */
    override var navigationBar: ScopedProperty<EditorScope, EditorComponent<*>>? = {
        rememberNavigationBar()
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items at the
     * bottom of the editor when a design block is selected.
     * Check parent class declaration for more details.
     */
    override var inspectorBar: ScopedProperty<EditorScope, EditorComponent<*>>? = {
        rememberInspectorBar()
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items next to
     * the selected design block.
     * Check parent class declaration for more details.
     */
    override var canvasMenu: ScopedProperty<EditorScope, EditorComponent<*>>? = {
        rememberCanvasMenu()
    }

    /**
     * The configuration of the component that is displayed over the editor. It is useful if you want to display a popup dialog or anything in the
     * overlay.
     * Check parent class declaration for more details.
     */
    override var bottomPanel: ScopedProperty<EditorScope, EditorComponent<*>>? = {
        rememberBottomPanel()
    }

    /**
     * The configuration of the component that is displayed over the editor. It is useful if you want to display a popup dialog or anything in the
     * overlay.
     * Check parent class declaration for more details.
     */
    override var overlay: ScopedProperty<EditorScope, EditorComponent<*>>? = {
        rememberOverlay()
    }
}
