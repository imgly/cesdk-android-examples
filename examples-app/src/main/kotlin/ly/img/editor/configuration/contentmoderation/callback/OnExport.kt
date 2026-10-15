package ly.img.editor.configuration.contentmoderation.callback

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import ly.img.editor.configuration.contentmoderation.ContentModerationConfigurationBuilder
import ly.img.editor.configuration.contentmoderation.ModerationExportPrompt
import ly.img.editor.configuration.contentmoderation.moderation.ModerationState
import ly.img.engine.MimeType
import java.nio.ByteBuffer

/**
 * The callback that is invoked when the export button is clicked.
 *
 * Before exporting, the last moderation check is consulted. If it flagged anything, or if the scene
 * has changes it never covered, the user is asked to confirm via a dialog and can either export
 * anyway or cancel.
 */
suspend fun ContentModerationConfigurationBuilder.onExport(
    preExport: suspend ContentModerationConfigurationBuilder.() -> Unit = {
        onPreExport()
    },
    moderate: suspend ContentModerationConfigurationBuilder.() -> Boolean = {
        onModerateBeforeExport()
    },
    exportByteBuffer: suspend ContentModerationConfigurationBuilder.() -> ByteBuffer = {
        onExportByteBuffer()
    },
    postExport: suspend ContentModerationConfigurationBuilder.(ByteBuffer) -> Unit = {
        onPostExport(it)
    },
    error: suspend ContentModerationConfigurationBuilder.(Exception) -> Unit = {
        onExportError(it)
    },
    finally: suspend ContentModerationConfigurationBuilder.() -> Unit = {
        onExportFinally()
    },
) {
    try {
        preExport()
        // Gate the export on content moderation; bail out if the user declines.
        if (!moderate()) return
        val result = exportByteBuffer()
        postExport(result)
    } catch (exception: Exception) {
        error(exception)
    } finally {
        finally()
    }
}

/**
 * The callback that is invoked before the export is started.
 */
fun ContentModerationConfigurationBuilder.onPreExport() {
    showLoading = true
}

/**
 * Consults the last check and asks for confirmation when the scene is flagged or has unchecked
 * changes. This does not call the moderation service — it only reads the cached report, which is
 * present only when a previous run covered the whole, unchanged scene.
 *
 * @return `true` to proceed with the export, `false` to cancel it. When content is flagged, this
 * suspends until the user resolves the confirmation dialog rendered by the overlay.
 */
suspend fun ContentModerationConfigurationBuilder.onModerateBeforeExport(): Boolean {
    // A cached report exists only when a previous run covered the whole, unchanged scene, so
    // "moderated" means exactly that: the current scene has a complete, still-valid result.
    val cached = cachedResults.takeIf { resultsFresh }
    val moderated = cached != null
    val flagged = cached?.results?.filter { it.category.state != ModerationState.SUCCESS }.orEmpty()

    // Skip the warning ONLY when we know the current scene is clean: a fresh check with no issues
    // and nothing changed since. Otherwise warn — either the last check found issues, or the scene
    // has changes that were never checked (a freshly loaded scene may already contain restricted
    // content, so an empty history is not assumed to be safe).
    if (moderated && flagged.isEmpty()) return true

    // Hide the export spinner while the confirmation dialog is visible.
    showLoading = false
    val decision = CompletableDeferred<Boolean>()
    moderationExportPrompt = ModerationExportPrompt(flagged = flagged, moderated = moderated, decision = decision)
    val proceed = try {
        decision.await()
    } finally {
        moderationExportPrompt = null
    }
    if (proceed) showLoading = true
    return proceed
}

/**
 * The callback that exports the content of the editor into [ByteBuffer].
 */
// highlight-starter-kit-on-export-byte-buffer
suspend fun ContentModerationConfigurationBuilder.onExportByteBuffer(): ByteBuffer = export(
    block = requireNotNull(editorContext.engine.scene.get()),
    mimeType = MimeType.PDF,
)
// highlight-starter-kit-on-export-byte-buffer

/**
 * The callback that is invoked after [onExportByteBuffer] and handles its output.
 */
// highlight-starter-kit-on-post-export
suspend fun ContentModerationConfigurationBuilder.onPostExport(byteBuffer: ByteBuffer) {
    val file = writeToFile(byteBuffer = byteBuffer, mimeType = MimeType.PDF)
    shareFile(file = file, mimeType = MimeType.PDF)
}
// highlight-starter-kit-on-post-export

/**
 * The callback that is invoked in case any of the export functions throw an exception.
 */
fun ContentModerationConfigurationBuilder.onExportError(error: Exception) {
    if (error is CancellationException) {
        throw error
    }
    this.error = error
}

/**
 * The callback that is invoked as the last step of [onExport].
 * It always runs, no matter success or failure on previous steps.
 */
fun ContentModerationConfigurationBuilder.onExportFinally() {
    showLoading = false
}
