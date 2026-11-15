package ly.img.editor.configuration.videomoderation.moderation

/**
 * The single extension point for video moderation.
 *
 * The default implementation is [GatewayModerationProvider], which talks to the IMG.LY gateway. To
 * use your own moderation service, implement this interface and pass it to
 * [ly.img.editor.configuration.videomoderation.VideoModerationConfigurationBuilder] in
 * [ly.img.editor.configuration.videomoderation.EditorActivity].
 *
 * The rest of the starter kit (the scene scan, the moderation button, the results panel and the
 * export warning) depends only on this interface and on [ModerationFrame], so swapping the
 * backend never requires touching the UI or the engine glue.
 */
// highlight-starter-kit-provider-interface
fun interface ContentModerationProvider {
    /**
     * Classifies a single video, image or text into moderation categories.
     *
     * A video arrives as a [ModerationContent.VideoUrl] the service can fetch itself, or as a
     * [ModerationContent.VideoFile] when the file is on the device. An image arrives as a
     * [ModerationContent.ImageUrl] or as rendered [ModerationContent.ImageBytes], and a text as
     * [ModerationContent.Text]. Implementations decide their own categories, descriptions,
     * thresholds and sampling interval.
     *
     * @param content the video, image or text to classify.
     * @return for a video, the sampled frames of the whole file, each with the [ModerationState] of
     * every category. The kit keeps only the frames inside the part of the clip that plays, so return
     * frames for the whole file, not for one trimmed range. For an image or a text, a single frame at
     * position `0`.
     */
    suspend fun moderate(content: ModerationContent): List<ModerationFrame>
}
// highlight-starter-kit-provider-interface
