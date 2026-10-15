package ly.img.editor.configuration.video.model

import androidx.annotation.FloatRange
import ly.img.engine.MimeType
import java.io.File

/**
 * The export status of the starter kit.
 */
sealed interface ExportStatus {
    /**
     * State when the export is active.
     *
     * @param progress the progress of the export.
     */
    data class Loading(
        @FloatRange(from = 0.0, to = 1.0) val progress: Float,
    ) : ExportStatus

    /**
     * State when the export is successfully completed.
     *
     * @param file the file where the exported content is written to.
     * @param mimeType the mime type of the exported content.
     */
    data class Success(
        val file: File,
        val mimeType: MimeType = MimeType.MP4,
    ) : ExportStatus

    /**
     * State when the export is completed with an exception.
     *
     * @param exception the exception that was thrown during the export.
     */
    data class Error(
        val exception: Exception,
    ) : ExportStatus
}
