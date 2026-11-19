package ly.img.editor.configuration.videomoderation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.configuration.videomoderation.callback.onCreate
import ly.img.editor.configuration.videomoderation.callback.onExport
import ly.img.editor.configuration.videomoderation.callback.onLoaded
import ly.img.editor.configuration.videomoderation.component.rememberBottomPanel
import ly.img.editor.configuration.videomoderation.component.rememberCanvasMenu
import ly.img.editor.configuration.videomoderation.component.rememberDock
import ly.img.editor.configuration.videomoderation.component.rememberInspectorBar
import ly.img.editor.configuration.videomoderation.component.rememberNavigationBar
import ly.img.editor.configuration.videomoderation.component.rememberOverlay
import ly.img.editor.configuration.videomoderation.model.ExportStatus
import ly.img.editor.configuration.videomoderation.moderation.ContentModerationProvider
import ly.img.editor.configuration.videomoderation.moderation.ModerationFrame
import ly.img.editor.configuration.videomoderation.moderation.ModerationReport
import ly.img.editor.configuration.videomoderation.moderation.ModerationResult
import ly.img.editor.configuration.videomoderation.moderation.ModerationState
import ly.img.editor.configuration.videomoderation.moderation.checkContent
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ScopedProperty
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.event.EditorEvent

/**
 * Configuration builder class for the video moderation editor starter kit.
 *
 * On top of a standard video editor it adds:
 * - a "Video moderation" navigation bar button that checks every video clip, image and text and opens the
 *   results in a bottom sheet, and
 * - an export warning that asks for confirmation when the last check flagged a block, or when the
 *   scene changed since the last check.
 *
 * @param moderationProvider the moderation backend. Swap in your own [ContentModerationProvider] to
 * use a different service; everything else stays the same.
 */
@Stable
class VideoModerationConfigurationBuilder(
    val moderationProvider: ContentModerationProvider,
) : BasicConfigurationBuilder() {
    /**
     * Current export status of the editor.
     */
    var exportStatus: ExportStatus? by editorContext.mutableStateOf(
        key = KEY_STATE_EXPORT_STATUS,
        initial = null,
    )

    /**
     * Whether export cancellation confirmation dialog should be displayed.
     */
    var showCancelExportConfirmationDialog: Boolean by editorContext.mutableStateOf(
        key = KEY_STATE_SHOW_CANCEL_EXPORT_CONFIRMATION_DIALOG,
        initial = false,
    )

    /**
     * The pending export confirmation. Set while [onExport] waits for the user to decide whether to
     * export flagged content; rendered as a dialog by [rememberOverlay].
     */
    var moderationExportPrompt: ModerationExportPrompt? by editorContext.mutableStateOf(
        key = KEY_STATE_EXPORT_PROMPT,
        initial = null,
    )

    /**
     * The most recent complete check. Reused while the scene has not changed.
     */
    var cachedResults: ModerationReport? by editorContext.mutableStateOf(
        key = KEY_STATE_CACHED_RESULTS,
        initial = null,
    )

    /**
     * Whether [cachedResults] still reflect the current scene, i.e. no undo/redo/edit has happened
     * since the last check. When `false`, the next check scans the scene again.
     */
    var resultsFresh: Boolean by editorContext.mutableStateOf(
        key = KEY_STATE_RESULTS_FRESH,
        initial = false,
    )

    /**
     * A counter that increments on every scene change (undo/redo/edit). Captured before a check
     * runs so that a change happening *during* the check does not mark stale results as fresh.
     */
    var editGeneration: Int by editorContext.mutableStateOf(
        key = KEY_STATE_EDIT_GENERATION,
        initial = 0,
    )

    /**
     * Whether the most recent completed check found any issues. This drives the navigation bar
     * icon: it stays a warning after edits and only returns to the shield once a check comes back
     * with nothing flagged.
     */
    var lastModerationHadIssues: Boolean by editorContext.mutableStateOf(
        key = KEY_STATE_LAST_HAD_ISSUES,
        initial = false,
    )

    /**
     * Whether the moderation sheet is currently open. Used to render the navigation bar icon filled
     * and to toggle the sheet closed when the icon is tapped again.
     */
    var isModerationSheetOpen: Boolean by editorContext.mutableStateOf(
        key = KEY_STATE_SHEET_OPEN,
        initial = false,
    )

    /**
     * The frames the provider returned for each source URI. A video request costs far more than an
     * image, and a trim or a move only changes which frames count, so a file is sent once.
     */
    val frameCache: MutableMap<String, List<ModerationFrame>> by editorContext.mutableStateOf(
        key = KEY_STATE_FRAME_CACHE,
        initial = mutableMapOf(),
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
    override var dock: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberDock()
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items at the top of the editor.
     * Check parent class declaration for more details.
     */
    override var navigationBar: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberNavigationBar()
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items at the
     * bottom of the editor when a design block is selected.
     * Check parent class declaration for more details.
     */
    override var inspectorBar: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberInspectorBar()
    }

    /**
     * The configuration of the component that is displayed as horizontal list of items next to
     * the selected design block.
     * Check parent class declaration for more details.
     */
    override var canvasMenu: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberCanvasMenu()
    }

    /**
     * The configuration of the component that is displayed below the canvas: the timeline.
     * Check parent class declaration for more details.
     */
    override var bottomPanel: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberBottomPanel()
    }

    /**
     * The configuration of the component that is displayed over the editor. It is useful if you want to display a popup dialog or anything in the
     * overlay.
     * Check parent class declaration for more details.
     */
    override var overlay: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberOverlay()
    }

    companion object {
        const val KEY_STATE_EXPORT_STATUS = "ly.img.editor.configuration.videomoderation.state.exportStatus"
        const val KEY_STATE_SHOW_CANCEL_EXPORT_CONFIRMATION_DIALOG =
            "ly.img.editor.configuration.videomoderation.state.showCancelExportConfirmationDialog"
        const val KEY_STATE_EXPORT_PROMPT = "ly.img.editor.configuration.videomoderation.state.exportPrompt"
        const val KEY_STATE_CACHED_RESULTS = "ly.img.editor.configuration.videomoderation.state.cachedResults"
        const val KEY_STATE_RESULTS_FRESH = "ly.img.editor.configuration.videomoderation.state.resultsFresh"
        const val KEY_STATE_EDIT_GENERATION = "ly.img.editor.configuration.videomoderation.state.editGeneration"
        const val KEY_STATE_LAST_HAD_ISSUES = "ly.img.editor.configuration.videomoderation.state.lastModerationHadIssues"
        const val KEY_STATE_SHEET_OPEN = "ly.img.editor.configuration.videomoderation.state.isModerationSheetOpen"
        const val KEY_STATE_FRAME_CACHE = "ly.img.editor.configuration.videomoderation.state.frameCache"
    }
}

/**
 * Runs video moderation on the scene, reusing [VideoModerationConfigurationBuilder.cachedResults]
 * when the scene has not changed since the last check.
 *
 * After an edit the scene is scanned again, but a source file that was already checked is not sent
 * again: its frames come from [VideoModerationConfigurationBuilder.frameCache].
 */
suspend fun VideoModerationConfigurationBuilder.moderateContent(): ModerationReport {
    cachedResults?.let { cached ->
        if (resultsFresh) return cached
    }
    val generation = editGeneration
    val report = checkContent(editorContext.engine, moderationProvider, frameCache)
    // The navigation bar icon reflects the last completed check and persists across edits. A block
    // that could not be checked counts as unresolved, not as clean.
    lastModerationHadIssues =
        report.unchecked.isNotEmpty() ||
        report.results.any { it.category.state != ModerationState.SUCCESS }
    // Only treat the report as current — for cache reuse and for skipping the export warning — when
    // the whole scene was checked AND it did not change while the check was running. Anything less
    // is not proof that the scene is clean.
    if (generation == editGeneration && report.unchecked.isEmpty()) {
        cachedResults = report
        resultsFresh = true
    }
    return report
}

/**
 * Holds the state that backs the export warning dialog.
 *
 * @param flagged the flagged results from the last check, or empty when the scene has unchecked
 * changes and therefore has no current results.
 * @param moderated whether the warning is backed by a fresh check of the current scene.
 * @param decision completed with `true` to export anyway, or `false` to cancel.
 */
data class ModerationExportPrompt(
    val flagged: List<ModerationResult>,
    val moderated: Boolean,
    val decision: CompletableDeferred<Boolean>,
)
