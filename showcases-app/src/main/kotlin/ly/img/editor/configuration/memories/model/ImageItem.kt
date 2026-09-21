package ly.img.editor.configuration.memories.model

data class ImageItem(
    val id: Int,
    val url: String,
    val creationDate: Long? = null, // Timestamp in milliseconds since epoch, null for stock images
    val isVideo: Boolean = false, // true when [url] points to a video; placed as a trimmed video clip
)
