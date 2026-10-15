package ly.img.editor.configuration.contentmoderation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.configuration.contentmoderation.callback.onCreate
import ly.img.editor.configuration.contentmoderation.callback.onExport
import ly.img.editor.configuration.contentmoderation.callback.onLoaded
import ly.img.editor.configuration.contentmoderation.component.rememberCanvasMenu
import ly.img.editor.configuration.contentmoderation.component.rememberDock
import ly.img.editor.configuration.contentmoderation.component.rememberInspectorBar
import ly.img.editor.configuration.contentmoderation.component.rememberNavigationBar
import ly.img.editor.configuration.contentmoderation.component.rememberOverlay
import ly.img.editor.configuration.contentmoderation.moderation.ContentModerationProvider
import ly.img.editor.configuration.contentmoderation.moderation.ModerationReport
import ly.img.editor.configuration.contentmoderation.moderation.ModerationResult
import ly.img.editor.configuration.contentmoderation.moderation.ModerationState
import ly.img.editor.configuration.contentmoderation.moderation.checkContent
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ScopedProperty
import ly.img.editor.core.component.EditorComponent

/**
 * Configuration builder class for the content moderation editor starter kit.
 *
 * On top of a standard design editor it adds:
 * - a "Moderate" dock button that runs a content check and opens the results in a bottom sheet, and
 * - an export warning that runs the same check before exporting and asks for confirmation if
 *   content is flagged.
 *
 * @param moderationProvider the moderation backend. Swap in your own [ContentModerationProvider] to
 * use a different service; everything else stays the same.
 */
@Stable
class ContentModerationConfigurationBuilder(
    val moderationProvider: ContentModerationProvider,
) : BasicConfigurationBuilder() {
    /**
     * The pending export confirmation. Set while [onExport] waits for the user to decide whether to
     * export flagged content; rendered as a dialog by [rememberOverlay].
     */
    var moderationExportPrompt: ModerationExportPrompt? by editorContext.mutableStateOf(
        key = KEY_STATE_EXPORT_PROMPT,
        initial = null,
    )

    /**
     * The most recent complete check. Reused to avoid redundant moderation API calls
     * while the scene has not changed.
     */
    var cachedResults: ModerationReport? by editorContext.mutableStateOf(
        key = KEY_STATE_CACHED_RESULTS,
        initial = null,
    )

    /**
     * Whether [cachedResults] still reflect the current scene, i.e. no undo/redo/edit has happened
     * since the last check. When `false`, the next check calls the moderation service.
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

    override var onCreate: (suspend EditorScope.() -> Unit)? = {
        onCreate()
    }

    override var onLoaded: (suspend EditorScope.() -> Unit)? = {
        onLoaded()
    }

    override var onExport: (suspend EditorScope.() -> Unit)? = {
        onExport()
    }

    override var onClose: (suspend EditorScope.() -> Unit)? = {
        showConfirmationOrCloseEditor()
    }

    override var onError: (suspend EditorScope.(Throwable) -> Unit)? = {
        error = it
    }

    override var dock: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberDock()
    }

    override var navigationBar: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberNavigationBar()
    }

    override var inspectorBar: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberInspectorBar()
    }

    override var canvasMenu: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberCanvasMenu()
    }

    override var overlay: ScopedProperty<EditorScope, EditorComponent<*>?>? = {
        rememberOverlay()
    }

    private companion object {
        const val KEY_STATE_EXPORT_PROMPT = "contentModeration.exportPrompt"
        const val KEY_STATE_CACHED_RESULTS = "contentModeration.cachedResults"
        const val KEY_STATE_RESULTS_FRESH = "contentModeration.resultsFresh"
        const val KEY_STATE_EDIT_GENERATION = "contentModeration.editGeneration"
        const val KEY_STATE_LAST_HAD_ISSUES = "contentModeration.lastModerationHadIssues"
        const val KEY_STATE_SHEET_OPEN = "contentModeration.isModerationSheetOpen"
    }
}

/**
 * Runs content moderation on the scene, reusing [ContentModerationConfigurationBuilder.cachedResults]
 * when the scene has not changed since the last check. This saves moderation API calls when the
 * user runs it again without editing anything.
 *
 * @param force when `true`, always calls the moderation service even if a fresh cache exists (used
 * by the explicit "Re-check" action).
 */
suspend fun ContentModerationConfigurationBuilder.moderateContent(force: Boolean = false): ModerationReport {
    cachedResults?.let { cached ->
        if (!force && resultsFresh) return cached
    }
    val generation = editGeneration
    val report = checkContent(editorContext.engine, moderationProvider)
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
