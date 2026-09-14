package ly.img.editor.configuration.video.callback

import kotlinx.coroutines.CancellationException
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.model.ExportStatus
import ly.img.engine.MimeType
import java.nio.ByteBuffer

/**
 * The callback that is invoked when the export button is clicked.
 */
suspend fun VideoConfigurationBuilder.onExport(
    preExport: suspend VideoConfigurationBuilder.() -> Unit = {
        onPreExport()
    },
    exportByteBuffer: suspend VideoConfigurationBuilder.() -> ByteBuffer = {
        onExportByteBuffer()
    },
    postExport: suspend VideoConfigurationBuilder.(ByteBuffer) -> Unit = {
        onPostExport(it)
    },
    error: suspend VideoConfigurationBuilder.(Exception) -> Unit = {
        onExportError(it)
    },
    finally: suspend VideoConfigurationBuilder.() -> Unit = {
        onExportFinally()
    },
) {
    try {
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
 * The callback that is invoked before the export is started.
 */
fun VideoConfigurationBuilder.onPreExport() {
    exportStatus = ExportStatus.Loading(progress = 0F)
}

/**
 * The callback that exports the content of the editor into [ByteBuffer].
 */
// highlight-starter-kit-on-export-byte-buffer
suspend fun VideoConfigurationBuilder.onExportByteBuffer(): ByteBuffer {
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
    )
}
// highlight-starter-kit-on-export-byte-buffer

/**
 * The callback that is invoked after [onExportByteBuffer] and handles its output.
 */
// highlight-starter-kit-on-post-export
suspend fun VideoConfigurationBuilder.onPostExport(byteBuffer: ByteBuffer) {
    val file = writeToFile(byteBuffer = byteBuffer, mimeType = MimeType.MP4)
    exportStatus = ExportStatus.Success(file = file, mimeType = MimeType.MP4)
}
// highlight-starter-kit-on-post-export

/**
 * The callback that is invoked in case any of the export functions throw an exception.
 */
fun VideoConfigurationBuilder.onExportError(error: Exception) {
    if (error is CancellationException) {
        exportStatus = null
        throw error
    } else {
        exportStatus = ExportStatus.Error(exception = error)
    }
}

/**
 * The callback that is invoked as the last step of [onExportByteBuffer].
 * It always runs, no matter success or failure on previous steps.
 */
fun VideoConfigurationBuilder.onExportFinally() {
    showCancelExportConfirmationDialog = false
}
