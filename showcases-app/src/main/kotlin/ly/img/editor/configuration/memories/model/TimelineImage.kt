package ly.img.editor.configuration.memories.model

/**
 * An image as it sits on the video timeline, used by the bottom-panel thumbnail strip.
 *
 * @param url the image fill URI
 * @param startTime when the image block starts on the timeline (seconds)
 * @param endTime when the image block ends (seconds)
 * @param fullyVisibleTime the moment the image is fully visible — right after its enter
 *   (Ken Burns) animation completes — which is where tapping the thumbnail seeks to.
 */
data class TimelineImage(
    val url: String,
    val startTime: Double,
    val endTime: Double,
    val fullyVisibleTime: Double,
    val isVideo: Boolean = false,
)
