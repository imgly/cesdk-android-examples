package ly.img.editor.configuration.contentmoderation.moderation

import ly.img.engine.DesignBlock

/**
 * The severity of a moderation category for a given block.
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
 * Images already hosted on a public URL are passed as [ImageUrl] so the service can fetch them
 * itself. Images the service cannot reach — gallery uploads, bundled assets, buffer fills — are
 * rendered by the engine and passed as [ImageBytes]. The text of a text block is passed as [Text].
 */
sealed interface ModerationContent {
    /**
     * An image the moderation service can fetch itself.
     *
     * @param url a publicly reachable `http(s)` URL.
     */
    data class ImageUrl(
        val url: String,
    ) : ModerationContent

    /**
     * An image the moderation service cannot reach, handed over as encoded bytes.
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
 * A single moderation category together with the [state] a provider assigned to it for one block.
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
 * The moderation outcome of a single [ModerationCategory] for a single [block] in the scene.
 *
 * @param block the design block that was checked.
 * @param blockLabel the heading the results panel groups this block under: a short quote of the
 * text for a text block, otherwise the block name, falling back to "Image".
 * @param category the category and its resolved [ModerationState].
 */
data class ModerationResult(
    val block: DesignBlock,
    val blockLabel: String,
    val category: ModerationCategory,
)
