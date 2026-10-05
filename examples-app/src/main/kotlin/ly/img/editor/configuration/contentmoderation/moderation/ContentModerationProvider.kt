package ly.img.editor.configuration.contentmoderation.moderation

/**
 * The single extension point for content moderation.
 *
 * The default implementation is [GatewayModerationProvider], which talks to the IMG.LY gateway. To
 * use your own moderation service, implement this interface and pass it to
 * [ly.img.editor.configuration.contentmoderation.ContentModerationConfigurationBuilder] in
 * [ly.img.editor.configuration.contentmoderation.EditorActivity].
 *
 * The rest of the starter kit (the scene scan, the moderation button, the results panel and the
 * export warning) depends only on this interface and on [ModerationCategory], so swapping the
 * backend never requires touching the UI or the engine glue.
 */
// highlight-starter-kit-provider-interface
fun interface ContentModerationProvider {
    /**
     * Classifies a single image or piece of text into moderation categories.
     *
     * The content arrives as a [ModerationContent.ImageUrl] the service can fetch itself, as
     * [ModerationContent.ImageBytes] when the fill is local, or as [ModerationContent.Text] for a
     * text block. Implementations decide their own categories, descriptions and thresholds.
     *
     * @param content the image or text to classify.
     * @return the list of categories with the [ModerationState] assigned to each.
     */
    suspend fun moderate(content: ModerationContent): List<ModerationCategory>
}
// highlight-starter-kit-provider-interface
