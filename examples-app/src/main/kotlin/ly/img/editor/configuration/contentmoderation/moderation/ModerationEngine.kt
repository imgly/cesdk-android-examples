package ly.img.editor.configuration.contentmoderation.moderation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.MimeType

/**
 * The outcome of one moderation run over the scene.
 *
 * @param results one entry per (block, category) pair that the service returned.
 * @param unchecked the blocks whose check failed. A run that leaves any block unchecked is not
 * proof that the scene is clean, so the caller must not cache it or skip the export warning.
 */
data class ModerationReport(
    val results: List<ModerationResult>,
    val unchecked: List<DesignBlock>,
)

/**
 * Scans every image and text block in the scene and runs it through [provider].
 *
 * Blocks whose image fill already resolves to a remote `http(s)` URL are passed on as that URL, so
 * the moderation service can fetch them itself. Every other fill — a gallery upload, a bundled
 * asset, a buffer — is rendered by the engine and passed on as encoded bytes. Text blocks are
 * passed on as their text. Blocks without a resolvable image, and blocks whose text is blank, are
 * skipped.
 *
 * A block whose request fails is reported in [ModerationReport.unchecked] rather than dropped: a
 * partial run must never read as "checked and clean". If *every* request fails — for example the
 * service is unreachable — the first error is rethrown so the caller can show an error state
 * instead of an empty result list.
 *
 * @param engine the editor engine holding the current scene.
 * @param provider the moderation backend to classify each block with.
 */
suspend fun checkContent(
    engine: Engine,
    provider: ContentModerationProvider,
): ModerationReport = coroutineScope {
    val targets = resolveImages(engine, engine.block.findByKind("image")) + resolveTexts(engine)
    if (targets.isEmpty()) return@coroutineScope ModerationReport(results = emptyList(), unchecked = emptyList())

    // A scene with many text labels would otherwise open one connection per block at once and be
    // rate-limited. Keep a handful in flight.
    val inFlight = Semaphore(MAX_CONCURRENT_REQUESTS)
    val outcomes = targets
        .map { (block, content) ->
            async {
                val label = labelOf(engine, block, content)
                try {
                    val categories = inFlight.withPermit { provider.moderate(content.getOrThrow()) }
                    Result.success(
                        categories.map { ModerationResult(block = block, blockLabel = label, category = it) },
                    )
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (throwable: Throwable) {
                    Result.failure(throwable)
                }
            }
        }
        .awaitAll()

    if (outcomes.none { it.isSuccess }) {
        // Nothing could be checked at all — surface the error instead of an empty report.
        throw outcomes.first().exceptionOrNull() ?: IllegalStateException("Content moderation failed")
    }
    ModerationReport(
        results = outcomes.filter { it.isSuccess }.flatMap { it.getOrThrow() },
        unchecked = targets.map { it.first }.filterIndexed { index, _ -> outcomes[index].isFailure },
    )
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
 * Collects the text of every text block in the scene. Blank text is skipped: there is nothing to
 * classify, and checking it would spend a request per empty placeholder.
 */
private fun resolveTexts(engine: Engine): List<Pair<DesignBlock, Result<ModerationContent>>> = engine.block
    .findByType(DesignBlockType.Text)
    .mapNotNull { block ->
        val text = textOf(engine, block)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        block to Result.success(ModerationContent.Text(text))
    }

/**
 * Selects [block] on the canvas, deselecting everything else.
 *
 * The results sheet floats over a canvas the user can still edit, so a flagged block may already be
 * gone by the time its row is tapped. Selecting a destroyed block would throw on the main thread
 * and take the app down with it.
 */
fun selectBlock(
    engine: Engine,
    block: DesignBlock,
) {
    if (!engine.block.isValid(block)) return
    if (!engine.block.isAllowedByScope(block, "editor/select")) return
    engine.block.select(block)
}

/**
 * The heading the results panel groups a block under.
 *
 * Text is always quoted rather than named. A text block's name defaults to "Text", which tells the
 * user nothing when several captions are flagged; the words themselves identify it. Everything else
 * this kit checks is an image block, so an unnamed one falls back to "Image".
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

private fun textOf(
    engine: Engine,
    block: DesignBlock,
): String? = runCatching { engine.block.getString(block, "text/text") }.getOrNull()

private fun String.isRemoteUrl(): Boolean = startsWith("http://") || startsWith("https://")

private const val MAX_CONCURRENT_REQUESTS = 4
private const val RENDER_QUALITY = 0.85F
private const val RENDER_TARGET_SIZE = 1024F
private const val MAX_QUOTED_TEXT = 40
