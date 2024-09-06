package ly.img.editor.version.details.entity

internal data class Release(
    val releaseNotes: String,
    val buildVersion: Long,
    val downloadUrl: String,
) {
    companion object {
        val UpToDate =
            Release(
                releaseNotes = "",
                buildVersion = 0L,
                downloadUrl = "",
            )
    }
}
