import android.net.Uri
import androidx.annotation.MainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.Source
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap

// highlight-android-models
enum class ModerationSeverity(
    val rank: Int,
) {
    SUCCESS(rank = 0),
    WARNING(rank = 1),
    FAILED(rank = 2),
}

data class ModerationCategory(
    val name: String,
    val confidence: Float,
    val state: ModerationSeverity,
)

data class ModerationImageBlock(
    val block: DesignBlock,
    val uri: Uri,
    val name: String,
)

data class ModerationTextBlock(
    val block: DesignBlock,
    val text: String,
    val name: String,
)

data class ModerationResult(
    val id: String,
    val block: DesignBlock,
    val blockName: String,
    val contentKind: String,
    val state: ModerationSeverity,
    val categories: List<ModerationCategory>,
)

data class ModerationCache(
    val imageCategoriesByUri: MutableMap<String, List<ModerationCategory>> = ConcurrentHashMap(),
    val textCategoriesByContent: MutableMap<String, List<ModerationCategory>> = ConcurrentHashMap(),
)
// highlight-android-models

data class ModerationSummary(
    val results: List<ModerationResult>,
    val groupedResults: Map<ModerationSeverity, List<ModerationResult>>,
    val detectedImageUris: List<Uri>,
    val selectedBlock: DesignBlock?,
    val selectedAfterInteractiveSelect: Boolean,
    val exportedImage: ByteBuffer?,
)

// highlight-android-get-image-url
@MainThread
fun getImageUrl(
    engine: Engine,
    block: DesignBlock,
): Uri? {
    if (!engine.block.supportsFill(block)) return null

    val fill = runCatching { engine.block.getFill(block) }.getOrNull() ?: return null
    if (engine.block.getType(fill) != FillType.Image.key) return null

    val sourceSetUri = runCatching {
        engine.block.getSourceSet(block = fill, property = "fill/image/sourceSet")
            .firstOrNull { it.uri.toString().isNotBlank() }
            ?.uri
    }.getOrNull()

    return sourceSetUri ?: runCatching {
        engine.block.getUri(block = fill, property = "fill/image/imageFileURI")
    }.getOrNull()?.takeIf { it.toString().isNotBlank() }
}
// highlight-android-get-image-url

// highlight-android-check-all-images
suspend fun checkAllImages(
    engine: Engine,
    moderationCache: ModerationCache,
): List<ModerationResult> = withContext(engine.dispatcher) {
    val imageBlocks = engine.block.findByType(DesignBlockType.Graphic).mapNotNull { block ->
        val uri = getImageUrl(engine = engine, block = block) ?: return@mapNotNull null
        ModerationImageBlock(
            block = block,
            uri = uri,
            name = engine.block.getName(block).ifBlank { "Image $block" },
        )
    }

    val categoriesByUri = imageBlocks
        .distinctBy { it.uri.toString() }
        .map { imageBlock ->
            async {
                imageBlock.uri.toString() to checkImageContentAPI(
                    image = imageBlock,
                    imageCache = moderationCache.imageCategoriesByUri,
                )
            }
        }
        .awaitAll()
        .toMap()

    imageBlocks.map { imageBlock ->
        val categories = categoriesByUri.getValue(imageBlock.uri.toString())
        ModerationResult(
            id = "image:${imageBlock.block}",
            block = imageBlock.block,
            blockName = imageBlock.name,
            contentKind = "image",
            state = categories.maxByOrNull { it.state.rank }?.state ?: ModerationSeverity.SUCCESS,
            categories = categories,
        )
    }
}
// highlight-android-check-all-images

// highlight-android-get-text-content
@MainThread
fun getTextContent(
    engine: Engine,
    block: DesignBlock,
): String? = engine.block
    .getString(block = block, property = "text/text")
    .trim()
    .takeIf(String::isNotEmpty)
// highlight-android-get-text-content

// highlight-android-check-all-text
suspend fun checkAllText(
    engine: Engine,
    moderationCache: ModerationCache,
): List<ModerationResult> = withContext(engine.dispatcher) {
    val textBlocks = engine.block.findByType(DesignBlockType.Text).mapNotNull { block ->
        val text = getTextContent(engine = engine, block = block) ?: return@mapNotNull null
        ModerationTextBlock(
            block = block,
            text = text,
            name = engine.block.getName(block).ifBlank { "Text $block" },
        )
    }

    val categoriesByText = textBlocks
        .distinctBy(ModerationTextBlock::text)
        .map { textBlock ->
            async {
                textBlock.text to checkTextContentAPI(
                    text = textBlock,
                    textCache = moderationCache.textCategoriesByContent,
                )
            }
        }
        .awaitAll()
        .toMap()

    textBlocks.map { textBlock ->
        val categories = categoriesByText.getValue(textBlock.text)
        ModerationResult(
            id = "text:${textBlock.block}",
            block = textBlock.block,
            blockName = textBlock.name,
            contentKind = "text",
            state = categories.maxByOrNull { it.state.rank }?.state ?: ModerationSeverity.SUCCESS,
            categories = categories,
        )
    }
}
// highlight-android-check-all-text

// highlight-android-image-moderation-api
suspend fun checkImageContentAPI(
    image: ModerationImageBlock,
    imageCache: MutableMap<String, List<ModerationCategory>>,
): List<ModerationCategory> {
    val cacheKey = image.uri.toString()
    imageCache[cacheKey]?.let { cachedCategories -> return cachedCategories }

    val categories = withContext(Dispatchers.IO) {
        // Replace this block with a request to your backend moderation endpoint.
        listOf(
            ModerationCategory(
                name = "adult-content",
                confidence = 0.16F,
                state = severityFor(confidence = 0.16F),
            ),
            ModerationCategory(
                name = "violence",
                confidence = 0.08F,
                state = severityFor(confidence = 0.08F),
            ),
        )
    }
    imageCache[cacheKey] = categories

    return categories
}
// highlight-android-image-moderation-api

// highlight-android-text-moderation-api
suspend fun checkTextContentAPI(
    text: ModerationTextBlock,
    textCache: MutableMap<String, List<ModerationCategory>>,
): List<ModerationCategory> {
    textCache[text.text]?.let { cachedCategories -> return cachedCategories }

    val categories = withContext(Dispatchers.IO) {
        // Replace this block with a request to your backend moderation endpoint.
        listOf(
            ModerationCategory(
                name = "profanity",
                confidence = 0.12F,
                state = severityFor(confidence = 0.12F),
            ),
            ModerationCategory(
                name = "policy-review",
                confidence = if (text.text.contains("review", ignoreCase = true)) 0.46F else 0.18F,
                state = severityFor(
                    confidence = if (text.text.contains("review", ignoreCase = true)) 0.46F else 0.18F,
                ),
            ),
        )
    }
    textCache[text.text] = categories

    return categories
}
// highlight-android-text-moderation-api

// highlight-android-threshold-mapping
fun severityFor(confidence: Float): ModerationSeverity = when {
    confidence > 0.8F -> ModerationSeverity.FAILED
    confidence > 0.4F -> ModerationSeverity.WARNING
    else -> ModerationSeverity.SUCCESS
}
// highlight-android-threshold-mapping

// highlight-android-display-results
fun summarizeModerationResults(results: List<ModerationResult>): Map<ModerationSeverity, List<ModerationResult>> =
    results.groupBy(ModerationResult::state)

fun resultsByPriority(results: List<ModerationResult>): List<ModerationResult> = results.sortedWith(
    compareByDescending<ModerationResult> { it.state.rank }
        .thenBy { it.contentKind }
        .thenBy { it.blockName },
)
// highlight-android-display-results

// highlight-android-interactive-results
suspend fun selectModerationResult(
    engine: Engine,
    result: ModerationResult,
) = withContext(engine.dispatcher) {
    if (!engine.block.isValid(result.block)) return@withContext

    engine.block.findAllSelected().forEach { selectedBlock ->
        engine.block.setSelected(block = selectedBlock, selected = false)
    }
    engine.block.setSelected(block = result.block, selected = true)
}
// highlight-android-interactive-results

// highlight-android-export-validation
suspend fun validateBeforeExport(
    engine: Engine,
    targetBlock: DesignBlock,
    moderationCache: ModerationCache,
): ByteBuffer? {
    val imageResults = checkAllImages(engine = engine, moderationCache = moderationCache)
    val textResults = checkAllText(engine = engine, moderationCache = moderationCache)
    val violations = (imageResults + textResults).filter { it.state == ModerationSeverity.FAILED }

    if (violations.isNotEmpty()) {
        return null
    }

    return withContext(engine.dispatcher) {
        if (!engine.block.isValid(targetBlock)) return@withContext null
        engine.block.export(block = targetBlock, mimeType = MimeType.PNG)
    }
}
// highlight-android-export-validation

suspend fun moderateContent(engine: Engine): ModerationSummary = withContext(engine.dispatcher) {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(imageBlock, name = "Campaign image")
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 80F)
    engine.block.setPositionY(imageBlock, value = 80F)
    engine.block.setWidth(imageBlock, value = 320F)
    engine.block.setHeight(imageBlock, value = 220F)
    engine.block.appendChild(parent = page, child = imageBlock)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)

    val sourceSetImageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(sourceSetImageBlock, name = "Responsive campaign image")
    engine.block.setShape(sourceSetImageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(sourceSetImageBlock, value = 430F)
    engine.block.setPositionY(sourceSetImageBlock, value = 80F)
    engine.block.setWidth(sourceSetImageBlock, value = 280F)
    engine.block.setHeight(sourceSetImageBlock, value = 220F)
    engine.block.appendChild(parent = page, child = sourceSetImageBlock)

    val sourceSetImageFill = engine.block.createFill(FillType.Image)
    engine.block.setSourceSet(
        block = sourceSetImageFill,
        property = "fill/image/sourceSet",
        sourceSet = listOf(
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_512x341.jpg"),
                width = 512,
                height = 341,
            ),
        ),
    )
    engine.block.setFill(block = sourceSetImageBlock, fill = sourceSetImageFill)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.setName(textBlock, name = "Campaign headline")
    engine.block.setPositionX(textBlock, value = 80F)
    engine.block.setPositionY(textBlock, value = 340F)
    engine.block.setWidth(textBlock, value = 520F)
    engine.block.setHeight(textBlock, value = 80F)
    engine.block.setString(
        block = textBlock,
        property = "text/text",
        value = "Needs manual review before publishing",
    )
    engine.block.appendChild(parent = page, child = textBlock)

    val moderationCache = ModerationCache()
    val imageResults = checkAllImages(engine = engine, moderationCache = moderationCache)
    val textResults = checkAllText(engine = engine, moderationCache = moderationCache)
    val results = resultsByPriority(imageResults + textResults)
    val selectedResult = results.firstOrNull()
    if (selectedResult != null) {
        selectModerationResult(engine = engine, result = selectedResult)
    }

    ModerationSummary(
        results = results,
        groupedResults = summarizeModerationResults(results),
        detectedImageUris = engine.block.findByType(DesignBlockType.Graphic).mapNotNull { block ->
            getImageUrl(engine = engine, block = block)
        },
        selectedBlock = selectedResult?.block,
        selectedAfterInteractiveSelect = selectedResult?.let { engine.block.findAllSelected().contains(it.block) } ?: false,
        exportedImage = validateBeforeExport(
            engine = engine,
            targetBlock = page,
            moderationCache = moderationCache,
        ),
    )
}
