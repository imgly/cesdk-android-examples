package ly.img.editor.configuration.videomoderation.moderation

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import java.io.File
import java.io.IOException

/**
 * The outcome of one moderation run over the scene.
 *
 * @param results one entry per (block, category) pair that the service returned.
 * @param unchecked the blocks whose check failed. A run that leaves any block unchecked is not proof
 * that the scene is clean, so the caller must not cache it or skip the export warning.
 * @param uncheckedReason the error message of the first failed block, or `null` when every block was
 * checked.
 */
data class ModerationReport(
    val results: List<ModerationResult>,
    val unchecked: List<DesignBlock>,
    val uncheckedReason: String? = null,
)

/**
 * Scans every video clip, image, text and caption in the scene and runs it through [provider].
 *
 * Clips whose video fill resolves to a remote `http(s)` URL are passed on as that URL, so the
 * moderation service can fetch the file itself. A gallery clip, a camera recording or a bundled
 * asset is passed on as a file the provider uploads. An image with a remote URL is passed on as
 * that URL; any other image is rendered by the engine and passed on as encoded bytes. Text blocks
 * and captions are passed on as their text, and blank text is skipped.
 *
 * The provider scores the whole source file of a clip. Each clip then keeps only the frames inside
 * the part that plays, so a frame the user trimmed away does not flag the clip. A split clip, or the
 * same file on two tracks, is checked once.
 *
 * A block whose file cannot load or whose request fails is reported in [ModerationReport.unchecked]
 * rather than dropped: a partial run must never read as "checked and clean". If *every* request
 * fails — for example the service is unreachable — the first error is rethrown so the caller can
 * show an error state instead of an empty result list.
 *
 * @param engine the editor engine holding the current scene.
 * @param provider the moderation backend to classify each video, image and text with.
 * @param frameCache the frames already returned for each video source URI. A trim or a move changes
 * only which frames count, so a check after such an edit sends no video again. New results are added.
 */
suspend fun checkContent(
    engine: Engine,
    provider: ContentModerationProvider,
    frameCache: MutableMap<String, List<ModerationFrame>>,
): ModerationReport = coroutineScope {
    val graphics = engine.block.findByType(DesignBlockType.Graphic)
    // A clip whose file cannot load is reported as unchecked; it does not end the whole check.
    val loadedClips = graphics.mapNotNull { block -> clipOf(engine, block)?.let { block to it } }
    val clips = loadedClips.mapNotNull { (_, clip) -> clip.getOrNull() }
    val unloadedClips = loadedClips.filter { (_, clip) -> clip.isFailure }
    // Any graphic with an image fill, whatever its kind, so a sticker or a swapped fill is checked too.
    val images = graphics.filter { block -> fillTypeOf(engine, block) == FillType.Image.key }
    val stills = resolveImages(engine, images) + resolveTexts(engine)
    if (loadedClips.isEmpty() && stills.isEmpty()) {
        return@coroutineScope ModerationReport(results = emptyList(), unchecked = emptyList())
    }

    // A scene with many text labels would otherwise open one connection per block at once and be
    // rate-limited. Keep a handful in flight.
    val inFlight = Semaphore(MAX_CONCURRENT_STILL_REQUESTS)
    val stillOutcomes = stills
        .map { (block, content) ->
            async { resultOf { inFlight.withPermit { provider.moderate(content.getOrThrow()) } } }
        }
        .awaitAll()

    // The gateway answers a second video request that overlaps any other request with "busy", so
    // the videos go one at a time, after the images and texts.
    val framesByUri = mutableMapOf<String, Result<List<ModerationFrame>>>()
    clips.map { it.uri }.distinct().forEach { uri ->
        framesByUri[uri] = frameCache[uri]?.let { Result.success(it) } ?: resultOf {
            // Reading a file's size touches the disk or a content provider, so it stays off the main thread.
            val content = withContext(Dispatchers.IO) { contentOf(engine.applicationContext, uri) }
            provider.moderate(content).also { frameCache[uri] = it }
        }
    }

    val outcomes = framesByUri.values + stillOutcomes + unloadedClips.map { (_, clip) -> clip.map { emptyList() } }
    if (outcomes.none { it.isSuccess }) {
        // Nothing could be checked at all — surface the error instead of an empty report.
        throw outcomes.first().exceptionOrNull() ?: IllegalStateException("Video moderation failed")
    }
    val clipResults = clips.flatMap { clip ->
        val frames = framesByUri.getValue(clip.uri).getOrNull() ?: return@flatMap emptyList()
        resultsOf(clip, frames)
    }
    val stillResults = stills.flatMapIndexed { index, (block, content) ->
        val frames = stillOutcomes[index].getOrNull() ?: return@flatMapIndexed emptyList()
        val label = labelOf(engine, block, content)
        // An image or a text is a single frame, so its categories are reported as they are.
        frames.flatMap { it.categories }.map { ModerationResult(block, label, it, flaggedRanges = emptyList()) }
    }
    ModerationReport(
        results = clipResults + stillResults,
        unchecked = unloadedClips.map { it.first } +
            clips.filter { framesByUri.getValue(it.uri).isFailure }.map { it.block } +
            stills.filterIndexed { index, _ -> stillOutcomes[index].isFailure }.map { it.first },
        uncheckedReason = outcomes.firstNotNullOfOrNull { it.exceptionOrNull() }?.message,
    )
}

/**
 * Runs one step of the check and keeps its failure as a value, so one failed block does not discard
 * the verdicts for the others. Cancellation still ends the whole check.
 */
private suspend fun <T> resultOf(request: suspend () -> T): Result<T> = try {
    Result.success(request())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    Result.failure(throwable)
}

private fun fillTypeOf(
    engine: Engine,
    block: DesignBlock,
): String? = runCatching { engine.block.getType(engine.block.getFill(block)) }.getOrNull()

/**
 * Selects [block] and moves the playhead to [clipTime] seconds after the block starts on the page.
 *
 * The results sheet floats over a timeline the user can still edit, so the clip may already be
 * gone or moved by the time its row is tapped. The playhead time uses the clip's current place on
 * the page, and a destroyed clip is skipped: selecting it would throw on the main thread and take
 * the app down with it.
 */
fun selectAt(
    engine: Engine,
    block: DesignBlock,
    clipTime: Double,
) {
    if (!engine.block.isValid(block)) return
    if (engine.block.isAllowedByScope(block, "editor/select")) {
        engine.block.select(block)
    }
    val page = engine.scene.getCurrentPage() ?: return
    engine.block.setPlaybackTime(page, pageTimeOffsetOf(engine, block) + clipTime)
}

/**
 * A video clip on the timeline, with the part of its source file that plays.
 *
 * @param played the source seconds the clip shows, from the trim offset to the end of the shorter
 * of the trim length and the block duration.
 * @param speed the playback speed of the fill. One second of the clip shows [speed] source seconds.
 */
private class Clip(
    val block: DesignBlock,
    val label: String,
    val uri: String,
    val played: ClosedFloatingPointRange<Double>,
    val speed: Double,
) {
    fun clipTimeOf(sourceTime: Double) = (sourceTime - played.start) / speed
}

/**
 * Reads [block] as a clip, or returns `null` when it has no video fill. A file that cannot load
 * comes back as a failure, so the caller reports the clip as unchecked.
 */
private suspend fun clipOf(
    engine: Engine,
    block: DesignBlock,
): Result<Clip>? {
    if (fillTypeOf(engine, block) != FillType.Video.key) return null
    val fill = engine.block.getFill(block)
    val uri = engine.block.getString(fill, "fill/video/fileURI").ifEmpty { return null }
    return resultOf { loadedClipOf(engine, block, fill, uri) }
}

private suspend fun loadedClipOf(
    engine: Engine,
    block: DesignBlock,
    fill: DesignBlock,
    uri: String,
): Clip {
    // The trim getters need the loaded resource.
    engine.block.forceLoadAVResource(fill)
    // Trim offset and length are timeline seconds, so playing at speed 2 covers twice the source.
    val speed = engine.block.getPlaybackSpeed(fill).toDouble()
    val trimOffset = engine.block.getTrimOffset(fill)
    val duration = engine.block.getDuration(block)
    val trimLength = engine.block.getTrimLength(fill).takeIf { it > 0 } ?: duration
    val start = trimOffset * speed
    val end = (trimOffset + minOf(trimLength, duration)) * speed
    val name = runCatching { engine.block.getName(block) }.getOrNull()
    return Clip(block = block, label = name?.ifEmpty { null } ?: "Video", uri = uri, played = start..end, speed = speed)
}

/**
 * Keeps the frames that cover [Clip.played] and reports, per category, the worst state among them
 * and the parts of the clip where it is flagged.
 *
 * A frame stands for the time from its position to the next frame's position, so a clip shorter
 * than the sampling interval still gets the frame that covers it. The interval is not assumed to be
 * fixed.
 */
private fun resultsOf(
    clip: Clip,
    frames: List<ModerationFrame>,
): List<ModerationResult> {
    val sorted = frames.sortedBy { it.position }
    val covering = sorted.mapIndexedNotNull { index, frame ->
        val coverEnd = sorted.getOrNull(index + 1)?.position ?: Double.POSITIVE_INFINITY
        val start = maxOf(frame.position, clip.played.start)
        val end = minOf(coverEnd, clip.played.endInclusive)
        if (start >= end) null else (start..end) to frame
    }
    val categoryIds = covering.flatMap { (_, frame) -> frame.categories.map { it.id } }.distinct()
    return categoryIds.map { id ->
        val scored = covering.mapNotNull { (range, frame) -> frame.categories.find { it.id == id }?.let { range to it } }
        val worst = scored.maxBy { (_, category) -> category.state.ordinal }.second
        ModerationResult(
            block = clip.block,
            blockLabel = clip.label,
            category = worst,
            flaggedRanges = mergeAdjacent(
                scored.filter { (_, category) -> category.state != ModerationState.SUCCESS }.map { it.first },
            ).map { clip.clipTimeOf(it.start)..clip.clipTimeOf(it.endInclusive) },
        )
    }
}

/**
 * Joins ranges that touch, so three flagged frames in a row read as one range in the results panel.
 */
private fun mergeAdjacent(ranges: List<ClosedFloatingPointRange<Double>>): List<ClosedFloatingPointRange<Double>> =
    ranges.fold(mutableListOf()) { merged, range ->
        val last = merged.lastOrNull()
        if (last != null && range.start <= last.endInclusive) {
            merged[merged.lastIndex] = last.start..maxOf(last.endInclusive, range.endInclusive)
        } else {
            merged += range
        }
        merged
    }

/**
 * The time at which [block] starts on the page. A time offset is relative to the parent, so a clip
 * inside a track adds the track's offset.
 */
private fun pageTimeOffsetOf(
    engine: Engine,
    block: DesignBlock,
): Double {
    var offset = 0.0
    var current: DesignBlock? = block
    while (current != null && engine.block.getType(current) != DesignBlockType.Page.key) {
        offset += engine.block.getTimeOffset(current)
        current = engine.block.getParent(current)
    }
    return offset
}

/**
 * Turns image blocks into the content a provider can classify.
 *
 * Rendering happens in a single batched export so the engine spins up one background worker for all
 * local fills instead of one per block. The rendered image reflects what the user sees, including
 * crop and effects. The batch is all-or-nothing: if it fails, every local image is reported as
 * unchecked, while images the service can fetch itself are still checked.
 */
private suspend fun resolveImages(
    engine: Engine,
    blocks: List<DesignBlock>,
): List<Pair<DesignBlock, Result<ModerationContent>>> {
    val remote = mutableListOf<Pair<DesignBlock, Result<ModerationContent>>>()
    val toRender = mutableListOf<DesignBlock>()
    blocks.forEach { block ->
        val uri = imageUrlOf(engine, block) ?: return@forEach
        if (uri.isRemoteUrl()) {
            remote += block to Result.success(ModerationContent.ImageUrl(uri))
        } else {
            toRender += block
        }
    }
    if (toRender.isEmpty()) return remote

    // JPEG keeps the upload small. It flattens transparency to black, which does not affect the
    // categories the moderation service reports.
    //
    // The target size caps a 12-megapixel gallery photo at something the classifier can still read,
    // instead of uploading the original over a phone connection. Note that the engine renders the
    // block large enough to *fill* that size, so a small block is scaled up to it rather than down.
    // Drop the target size if you would rather send the image at its natural resolution.
    val rendered = runCatching {
        engine.block.export(
            blocks = toRender,
            mimeType = MimeType.JPEG,
            options = ExportOptions(
                jpegQuality = RENDER_QUALITY,
                targetWidth = RENDER_TARGET_SIZE,
                targetHeight = RENDER_TARGET_SIZE,
            ),
        )
    }
    return remote +
        toRender.mapIndexed { index, block ->
            block to
                rendered.mapCatching { buffers ->
                    val buffer = buffers[index]
                    ModerationContent.ImageBytes(
                        bytes = ByteArray(buffer.remaining()).also(buffer::get),
                        mimeType = MimeType.JPEG.key,
                    )
                }
        }
}

/**
 * Collects the text of every text block and every caption in the scene. Blank text is skipped: there
 * is nothing to classify, and checking it would spend a request per empty placeholder.
 */
private fun resolveTexts(engine: Engine): List<Pair<DesignBlock, Result<ModerationContent>>> =
    textsOf(engine, DesignBlockType.Text, "text/text") + textsOf(engine, DesignBlockType.Caption, "caption/text")

private fun textsOf(
    engine: Engine,
    type: DesignBlockType,
    property: String,
): List<Pair<DesignBlock, Result<ModerationContent>>> = engine.block
    .findByType(type)
    .mapNotNull { block ->
        val text = runCatching { engine.block.getString(block, property) }.getOrNull()
            ?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        block to Result.success(ModerationContent.Text(text))
    }

/**
 * The heading the results panel groups an image or a text block under.
 *
 * Text is always quoted rather than named. A text block's name defaults to "Text", which tells the
 * user nothing when several captions are flagged; the words themselves identify it. An unnamed image
 * falls back to "Image".
 */
private fun labelOf(
    engine: Engine,
    block: DesignBlock,
    content: Result<ModerationContent>,
): String {
    val text = (content.getOrNull() as? ModerationContent.Text)?.text
    if (text != null) {
        val quoted = text.take(MAX_QUOTED_TEXT).trim()
        return "\"$quoted${if (text.length > MAX_QUOTED_TEXT) "…" else ""}\""
    }
    return runCatching { engine.block.getName(block) }.getOrNull()?.ifEmpty { null } ?: "Image"
}

/**
 * Resolves the image URI of a block's image fill, checking the direct file URI first and falling
 * back to the first entry of the source set.
 */
private fun imageUrlOf(
    engine: Engine,
    block: DesignBlock,
): String? {
    val fill = runCatching { engine.block.getFill(block) }.getOrNull() ?: return null
    val fileUri = runCatching { engine.block.getString(fill, "fill/image/imageFileURI") }.getOrNull()
    if (!fileUri.isNullOrEmpty()) return fileUri
    val sourceSet = runCatching { engine.block.getSourceSet(fill, "fill/image/sourceSet") }.getOrNull()
    return sourceSet?.firstOrNull()?.uri?.toString()
}

private fun String.isRemoteUrl(): Boolean = startsWith("http://") || startsWith("https://")

/**
 * Turns a video fill URI into the content a provider can classify.
 *
 * @throws IOException for a URI the device cannot read as a file, e.g. a `buffer://` fill.
 */
private fun contentOf(
    context: Context,
    uri: String,
): ModerationContent {
    if (uri.isRemoteUrl()) return ModerationContent.VideoUrl(uri)
    val parsed = Uri.parse(uri)
    val mimeType = context.contentResolver.getType(parsed)
        ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri))
        ?: DEFAULT_MIME_TYPE
    return when {
        parsed.scheme == "content" -> ModerationContent.VideoFile(
            uri = uri,
            mimeType = mimeType,
            length = context.contentResolver.openAssetFileDescriptor(parsed, "r")?.use { it.length }
                ?: AssetFileDescriptor.UNKNOWN_LENGTH,
            openStream = {
                context.contentResolver.openInputStream(parsed) ?: throw IOException("Cannot open $uri")
            },
        )

        parsed.scheme == "file" && parsed.path.orEmpty().startsWith(ANDROID_ASSET_PATH) -> {
            val assetPath = parsed.path.orEmpty().removePrefix(ANDROID_ASSET_PATH)
            ModerationContent.VideoFile(
                uri = uri,
                mimeType = mimeType,
                // A compressed asset has no file descriptor, so its length is unknown.
                length = runCatching { context.assets.openFd(assetPath).use { it.length } }
                    .getOrDefault(AssetFileDescriptor.UNKNOWN_LENGTH),
                openStream = { context.assets.open(assetPath) },
            )
        }

        parsed.scheme == "file" -> {
            val file = File(parsed.path.orEmpty())
            ModerationContent.VideoFile(
                uri = uri,
                mimeType = mimeType,
                length = file.length(),
                openStream = { file.inputStream() },
            )
        }

        else -> throw IOException("This video cannot be read from the device: $uri")
    }
}

private const val MAX_CONCURRENT_STILL_REQUESTS = 4
private const val RENDER_QUALITY = 0.85F
private const val RENDER_TARGET_SIZE = 1024F
private const val MAX_QUOTED_TEXT = 40
private const val DEFAULT_MIME_TYPE = "video/mp4"
private const val ANDROID_ASSET_PATH = "/android_asset/"
