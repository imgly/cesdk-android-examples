package ly.img.editor.configuration.videomoderation.moderation

import ly.img.engine.DesignBlock
import java.io.InputStream

/**
 * The severity of a moderation category for a given clip, image or text.
 *
 * - [SUCCESS]: the category is very unlikely to be present.
 * - [WARNING]: the category might be present and should be reviewed.
 * - [FAILED]: the category is very likely to be present.
 */
enum class ModerationState {
    SUCCESS,
    WARNING,
    FAILED,
}

/**
 * A single piece of content handed to a [ContentModerationProvider].
 *
 * Videos already hosted on a public URL are passed as [VideoUrl] so the service can fetch them
 * itself. Videos the service cannot reach — gallery clips, camera recordings, bundled assets — are
 * passed as [VideoFile], which the provider reads and uploads. Images follow the same split as
 * [ImageUrl] and [ImageBytes], and the text of a text block is passed as [Text].
 */
sealed interface ModerationContent {
    /**
     * A video the moderation service can fetch itself.
     *
     * @param url a publicly reachable `http(s)` URL.
     */
    data class VideoUrl(
        val url: String,
    ) : ModerationContent

    /**
     * A video the moderation service cannot reach, read from the device.
     *
     * @param uri the source URI of the video fill. It identifies the file, for example in a cache.
     * @param mimeType the container format, e.g. "video/mp4".
     * @param length the file size in bytes, or `-1` when the source does not report it.
     * @param openStream opens a new stream over the file. Gallery clips can be hundreds of
     * megabytes, so read the stream in chunks instead of loading it into memory.
     */
    class VideoFile(
        val uri: String,
        val mimeType: String,
        val length: Long,
        val openStream: () -> InputStream,
    ) : ModerationContent

    /**
     * An image the moderation service can fetch itself.
     *
     * @param url a publicly reachable `http(s)` URL.
     */
    data class ImageUrl(
        val url: String,
    ) : ModerationContent

    /**
     * An image the moderation service cannot reach, rendered by the engine and handed over as encoded
     * bytes.
     *
     * This is a plain class rather than a data class because [ByteArray] compares by identity.
     *
     * @param bytes the encoded image.
     * @param mimeType the encoding of [bytes], e.g. "image/jpeg".
     */
    class ImageBytes(
        val bytes: ByteArray,
        val mimeType: String,
    ) : ModerationContent

    /**
     * The text of a text block.
     *
     * @param text the text to classify.
     */
    data class Text(
        val text: String,
    ) : ModerationContent
}

/**
 * A single moderation category together with the [state] a provider assigned to it.
 *
 * A custom [ContentModerationProvider] is free to return its own set of categories, descriptions
 * and thresholds — the UI simply renders whatever is returned.
 *
 * @param id a stable identifier for the category, e.g. "weapon".
 * @param displayName the human readable name shown in the results panel.
 * @param description a short explanation of what the category covers.
 * @param state how likely the content violates this category.
 */
data class ModerationCategory(
    val id: String,
    val displayName: String,
    val description: String,
    val state: ModerationState,
)

/**
 * The categories a provider found in one sampled frame of a video, or in an image or a text.
 *
 * @param position the frame's position in the source file, in seconds from its start. This is
 * source time: it ignores the trim, the playback speed and the place of the clip on the timeline.
 * An image or a text is a single frame at `0`.
 * @param categories the categories scored for this frame.
 */
data class ModerationFrame(
    val position: Double,
    val categories: List<ModerationCategory>,
)

/**
 * The moderation outcome of a single [ModerationCategory] for a single [block] in the scene.
 *
 * For a video clip, only the frames inside the part of the clip that plays count: frames the user
 * trimmed away do not flag the clip.
 *
 * @param block the video clip, image or text block that was checked.
 * @param blockLabel the heading the results panel groups this block under: a short quote of the
 * text for a text block, otherwise the block name, falling back to "Video" or "Image".
 * @param category the category with the worst [ModerationState] of the frames that play.
 * @param flaggedRanges the parts of a clip where the category is not [ModerationState.SUCCESS], in
 * seconds from the start of the clip on the timeline. Empty when [category] is clean, and always
 * empty for an image or a text, which has no time of its own.
 */
data class ModerationResult(
    val block: DesignBlock,
    val blockLabel: String,
    val category: ModerationCategory,
    val flaggedRanges: List<ClosedFloatingPointRange<Double>>,
)
