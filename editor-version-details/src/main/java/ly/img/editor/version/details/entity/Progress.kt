package ly.img.editor.version.details.entity

import java.io.File

internal sealed interface Progress {
    data class Ready(val file: File) : Progress

    data class Pending(val progress: Float) : Progress

    data class Error(val throwable: Throwable) : Progress
}
