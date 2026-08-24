package ly.img.editor.configuration.memories.callback

import kotlinx.coroutines.CancellationException
import ly.img.editor.configuration.memories.MemoriesConfiguration
import ly.img.editor.configuration.memories.model.ExportStatus
import ly.img.engine.MimeType
import java.nio.ByteBuffer

/**
 * Render the slideshow page to an MP4, reporting progress through [MemoriesConfiguration.exportStatus]
 * so the overlay can show a progress circle, then a share button on success.
 */
suspend fun MemoriesConfiguration.onExport() {
    try {
        exportStatus = ExportStatus.Loading(progress = 0f)
        val buffer = exportSlideshow()
        val file = writeToFile(byteBuffer = buffer, mimeType = MimeType.MP4)
        exportStatus = ExportStatus.Success(file = file, mimeType = MimeType.MP4)
    } catch (cancellation: CancellationException) {
        exportStatus = null
        throw cancellation
    } catch (exception: Exception) {
        exportStatus = ExportStatus.Error(exception)
    }
}

private suspend fun MemoriesConfiguration.exportSlideshow(): ByteBuffer {
    val engine = editorContext.engine
    val page = requireNotNull(engine.scene.getCurrentPage())
    return engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(page),
        mimeType = MimeType.MP4,
        progressCallback = { progress ->
            if (progress.totalFrames > 0) {
                val fraction = progress.encodedFrames.toFloat() / progress.totalFrames
                val current = exportStatus
                // Only push a new state on a visible (~1%) change to avoid churning recomposition.
                if (current !is ExportStatus.Loading || fraction >= current.progress + 0.01f) {
                    exportStatus = ExportStatus.Loading(progress = fraction)
                }
            }
        },
    )
}
