package ly.img.editor.configuration.videomoderation.callback

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import ly.img.editor.configuration.videomoderation.ModerationExportPrompt
import ly.img.editor.configuration.videomoderation.VideoModerationConfigurationBuilder
import ly.img.editor.configuration.videomoderation.model.ExportStatus
import ly.img.editor.configuration.videomoderation.moderation.ModerationState
import ly.img.engine.ExportVideoOptions
import ly.img.engine.MimeType
import ly.img.engine.VideoBitrate
import java.nio.ByteBuffer

/**
 * The callback that is invoked when the export button is clicked.
 *
 * Before exporting, the last moderation check is consulted. If it flagged anything, or if the scene
 * has changes it never covered, the user is asked to confirm via a dialog and can either export
 * anyway or cancel.
 */
suspend fun VideoModerationConfigurationBuilder.onExport(
    moderate: suspend VideoModerationConfigurationBuilder.() -> Boolean = {
        onModerateBeforeExport()
    },
    preExport: suspend VideoModerationConfigurationBuilder.() -> Unit = {
        onPreExport()
    },
    exportByteBuffer: suspend VideoModerationConfigurationBuilder.() -> ByteBuffer = {
        onExportByteBuffer()
    },
    postExport: suspend VideoModerationConfigurationBuilder.(ByteBuffer) -> Unit = {
        onPostExport(it)
    },
    error: suspend VideoModerationConfigurationBuilder.(Exception) -> Unit = {
        onExportError(it)
    },
    finally: suspend VideoModerationConfigurationBuilder.() -> Unit = {
        onExportFinally()
    },
) {
    try {
        // Gate the export on video moderation before the progress overlay opens; bail out if the
        // user declines.
        if (!moderate()) return
        preExport()
        val result = exportByteBuffer()
        postExport(result)
    } catch (exception: Exception) {
        error(exception)
    } finally {
        finally()
    }
}

/**
 * Consults the last check and asks for confirmation when the scene is flagged or has unchecked
 * changes. This does not call the moderation service — it only reads the cached report, which is
 * present only when a previous run covered the whole, unchanged scene.
 *
 * @return `true` to proceed with the export, `false` to cancel it. When content is flagged, this
 * suspends until the user resolves the confirmation dialog rendered by the overlay.
 */
suspend fun VideoModerationConfigurationBuilder.onModerateBeforeExport(): Boolean {
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

    val decision = CompletableDeferred<Boolean>()
    moderationExportPrompt = ModerationExportPrompt(flagged = flagged, moderated = moderated, decision = decision)
    return try {
        decision.await()
    } finally {
        moderationExportPrompt = null
    }
}

/**
 * The callback that is invoked before the export is started.
 */
fun VideoModerationConfigurationBuilder.onPreExport() {
    exportStatus = ExportStatus.Loading(progress = 0F)
}

/**
 * The callback that exports the content of the editor into [ByteBuffer].
 */
// highlight-starter-kit-on-export-byte-buffer
suspend fun VideoModerationConfigurationBuilder.onExportByteBuffer(): ByteBuffer {
    val targetDesignBlock = requireNotNull(editorContext.engine.scene.getCurrentPage())
    return editorContext.engine.block.exportVideo(
        block = targetDesignBlock,
        timeOffset = 0.0,
        duration = editorContext.engine.block.getDuration(targetDesignBlock),
        mimeType = MimeType.MP4,
        progressCallback = { progress ->
            val lastExportStatus = exportStatus
            val newProgress = progress.encodedFrames.toFloat() / progress.totalFrames
            // Update export UI whenever the progress changes
            if (lastExportStatus !is ExportStatus.Loading || newProgress >= lastExportStatus.progress + 0.01F) {
                exportStatus = ExportStatus.Loading(progress = newProgress)
            }
        },
        // videoBitrate: VideoBitrate.Auto derives a bounded bitrate from the resolution/framerate.
        options = ExportVideoOptions(videoBitrate = VideoBitrate.Auto),
    )
}
// highlight-starter-kit-on-export-byte-buffer

/**
 * The callback that is invoked after [onExportByteBuffer] and handles its output.
 */
// highlight-starter-kit-on-post-export
suspend fun VideoModerationConfigurationBuilder.onPostExport(byteBuffer: ByteBuffer) {
    val file = writeToFile(byteBuffer = byteBuffer, mimeType = MimeType.MP4)
    exportStatus = ExportStatus.Success(file = file, mimeType = MimeType.MP4)
}
// highlight-starter-kit-on-post-export

/**
 * The callback that is invoked in case any of the export functions throw an exception.
 */
fun VideoModerationConfigurationBuilder.onExportError(error: Exception) {
    if (error is CancellationException) {
        exportStatus = null
        throw error
    } else {
        exportStatus = ExportStatus.Error(exception = error)
    }
}

/**
 * The callback that is invoked as the last step of [onExport].
 * It always runs, no matter success or failure on previous steps.
 */
fun VideoModerationConfigurationBuilder.onExportFinally() {
    showCancelExportConfirmationDialog = false
}
