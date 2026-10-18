package ly.img.editor.configuration.apparel.callback

import kotlinx.coroutines.CancellationException
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.engine.MimeType
import java.nio.ByteBuffer

/**
 * The callback that is invoked when the export button is clicked.
 */
suspend fun ApparelConfigurationBuilder.onExport(
    preExport: suspend ApparelConfigurationBuilder.() -> Unit = {
        onPreExport()
    },
    exportByteBuffer: suspend ApparelConfigurationBuilder.() -> ByteBuffer = {
        onExportByteBuffer()
    },
    postExport: suspend ApparelConfigurationBuilder.(ByteBuffer) -> Unit = {
        onPostExport(it)
    },
    error: suspend ApparelConfigurationBuilder.(Exception) -> Unit = {
        onExportError(it)
    },
    finally: suspend ApparelConfigurationBuilder.() -> Unit = {
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
fun ApparelConfigurationBuilder.onPreExport() {
    showLoading = true
}

/**
 * The callback that exports the content of the editor into [ByteBuffer].
 */
// highlight-starter-kit-on-export-byte-buffer
suspend fun ApparelConfigurationBuilder.onExportByteBuffer(): ByteBuffer = export(
    block = requireNotNull(editorContext.engine.scene.get()),
    mimeType = MimeType.PDF,
)
// highlight-starter-kit-on-export-byte-buffer

/**
 * The callback that is invoked after [onExportByteBuffer] and handles its output.
 */
// highlight-starter-kit-on-post-export
suspend fun ApparelConfigurationBuilder.onPostExport(byteBuffer: ByteBuffer) {
    val file = writeToFile(byteBuffer = byteBuffer, mimeType = MimeType.PDF)
    shareFile(file = file, mimeType = MimeType.PDF)
}
// highlight-starter-kit-on-post-export

/**
 * The callback that is invoked in case any of the export functions throw an exception.
 */
fun ApparelConfigurationBuilder.onExportError(error: Exception) {
    if (error is CancellationException) {
        throw error
    }
    this.error = error
}

/**
 * The callback that is invoked as the last step of [onExportByteBuffer].
 * It always runs, no matter success or failure on previous steps.
 */
fun ApparelConfigurationBuilder.onExportFinally() {
    showLoading = false
}
