package ly.img.editor.configuration.memories.model

import ly.img.engine.MimeType
import java.io.File

/** The state of the MP4 export, surfaced as a progress overlay. */
sealed interface ExportStatus {
    data class Loading(
        val progress: Float,
    ) : ExportStatus

    data class Success(
        val file: File,
        val mimeType: MimeType = MimeType.MP4,
    ) : ExportStatus

    data class Error(
        val exception: Exception,
    ) : ExportStatus
}
