package ly.img.editor.configuration.photo.callback

import kotlinx.coroutines.CancellationException
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.engine.MimeType
import java.nio.ByteBuffer

/**
 * The callback that is invoked when the export button is clicked.
 */
suspend fun PhotoConfigurationBuilder.onExport(
    preExport: suspend PhotoConfigurationBuilder.() -> Unit = {
        onPreExport()
    },
    exportByteBuffer: suspend PhotoConfigurationBuilder.() -> ByteBuffer = {
        onExportByteBuffer()
    },
    postExport: suspend PhotoConfigurationBuilder.(ByteBuffer) -> Unit = {
        onPostExport(it)
    },
    error: suspend PhotoConfigurationBuilder.(Exception) -> Unit = {
        onExportError(it)
    },
    finally: suspend PhotoConfigurationBuilder.() -> Unit = {
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
fun PhotoConfigurationBuilder.onPreExport() {
    showLoading = true
}

/**
 * The callback that exports the content of the editor into [ByteBuffer].
 */
// highlight-starter-kit-on-export-byte-buffer
suspend fun PhotoConfigurationBuilder.onExportByteBuffer(): ByteBuffer = export(
    block = requireNotNull(editorContext.engine.scene.get()),
    mimeType = MimeType.PNG,
)
// highlight-starter-kit-on-export-byte-buffer

/**
 * The callback that is invoked after [onExportByteBuffer] and handles its output.
 */
// highlight-starter-kit-on-post-export
suspend fun PhotoConfigurationBuilder.onPostExport(byteBuffer: ByteBuffer) {
    val file = writeToFile(byteBuffer = byteBuffer, mimeType = MimeType.PNG)
    shareFile(file = file, mimeType = MimeType.PNG)
}
// highlight-starter-kit-on-post-export

/**
 * The callback that is invoked in case any of the export functions throw an exception.
 */
fun PhotoConfigurationBuilder.onExportError(error: Exception) {
    if (error is CancellationException) {
        throw error
    }
    this.error = error
}

/**
 * The callback that is invoked as the last step of [onExportByteBuffer].
 * It always runs, no matter success or failure on previous steps.
 */
fun PhotoConfigurationBuilder.onExportFinally() {
    showLoading = false
}
