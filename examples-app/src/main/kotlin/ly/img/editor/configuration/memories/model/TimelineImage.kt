package ly.img.editor.configuration.memories.model

/**
 * An image as it sits on the video timeline, used by the bottom-panel thumbnail strip.
 *
 * @param url the image fill URI
 * @param startTime when the image block starts on the timeline (seconds)
 * @param endTime when the image block ends (seconds)
 * @param fullyVisibleTime the middle of the window in which this clip is alone on screen, i.e.
 *   clear of the transitions it shares with its neighbours. A clip too short to have such a
 *   window keeps its own midpoint, which is the least-blended moment it has.
 */
data class TimelineImage(
    val url: String,
    val startTime: Double,
    val endTime: Double,
    val fullyVisibleTime: Double,
    val isVideo: Boolean = false,
)
