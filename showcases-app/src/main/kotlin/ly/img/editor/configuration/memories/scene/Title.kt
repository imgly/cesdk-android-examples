package ly.img.editor.configuration.memories.scene

import android.content.Context
import android.util.Log
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.style.VideoStyles
import ly.img.editor.configuration.memories.util.BURST_IMAGE_COUNT
import ly.img.editor.configuration.memories.util.BURST_TITLE_FRACTION
import ly.img.editor.configuration.memories.util.TITLE_DURATION
import ly.img.editor.configuration.memories.util.TITLE_FONT_SIZE
import ly.img.editor.configuration.memories.widget.videoThumbnailFileUri
import ly.img.engine.AnimationType
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.ShapeType
import kotlin.random.Random

private const val TAG = "MemoriesTitle"

/** Create the centered, blur-animated title card on [textTrack]. Returns whether a title was added. */
internal suspend fun createTitleBlock(
    engine: Engine,
    page: Int,
    title: String,
    textTrack: Int,
): Boolean {
    if (title.isEmpty()) return false

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(textBlock, title)
    engine.block.setDuration(textBlock, TITLE_DURATION)
    engine.block.setTextColor(textBlock, Color.fromRGBA(1f, 1f, 1f, 1f))
    engine.block.setTextFontSize(textBlock, fontSize = TITLE_FONT_SIZE, from = 0, to = title.length)
    engine.block.setMetadata(textBlock, "blockType", "title")

    applyTitleTypeface(engine, textBlock, VideoStyles.DEFAULT.typeface)

    engine.block.setInAnimation(textBlock, blurAnimation(engine, 0.8))
    engine.block.setOutAnimation(textBlock, blurAnimation(engine, 0.6))

    engine.block.appendChild(parent = textTrack, child = textBlock)
    engine.block.setEnum(textBlock, "text/horizontalAlignment", "Center")
    engine.block.setEnum(textBlock, "text/verticalAlignment", "Center")
    return true
}

private fun blurAnimation(
    engine: Engine,
    duration: Double,
): Int {
    val animation = engine.block.createAnimation(AnimationType.Blur)
    engine.block.setDuration(animation, duration)
    engine.block.setEnum(animation, "textAnimationWritingStyle", "Character")
    return animation
}

private suspend fun applyTitleTypeface(
    engine: Engine,
    textBlock: Int,
    typefaceName: String,
) {
    try {
        val assets = engine.asset.findAssets(
            sourceId = "ly.img.typeface",
            query = FindAssetsQuery(query = typefaceName, page = 0, perPage = 100),
        )
        val typeface = assets.assets.firstOrNull()?.payload?.typeface ?: return
        // Match the weight preference the default style applies (see StyleApplier.resolveFont), so
        // the first style application doesn't visibly re-weight the title from a different initial pick.
        val font = VideoStyles.DEFAULT.fontWeights
            .firstNotNullOfOrNull { weight -> typeface.fonts.firstOrNull { it.subFamily == weight } }
            ?: typeface.fonts.firstOrNull()
            ?: return
        engine.block.setFont(textBlock, font.uri, typeface)
    } catch (e: Exception) {
        Log.w(TAG, "Could not load title typeface '$typefaceName'", e)
    }
}

/**
 * Flash a handful of random photos behind the title for a lively opening. Videos can't be shown
 * through an image fill, so each picked video is flashed as its extracted thumbnail instead (skipped
 * if a frame can't be decoded, or if [context] is unavailable).
 */
internal suspend fun createBurstImages(
    engine: Engine,
    page: Int,
    context: Context?,
    images: List<ImageItem>,
    hasTitle: Boolean,
) {
    if (images.isEmpty() || !hasTitle) return

    // Resolve each picked item to a renderable image URI: photos use their url, videos a thumbnail.
    val urls = pickBurstImages(images, BURST_IMAGE_COUNT).mapNotNull { item ->
        when {
            !item.isVideo -> item.url
            context != null -> videoThumbnailFileUri(context, item.url)
            else -> null
        }
    }
    if (urls.isEmpty()) return

    val burstTracks = List(urls.size) {
        engine.block.create(DesignBlockType.Track).also { engine.block.appendChild(page, it) }
    }

    val burstDuration = TITLE_DURATION * BURST_TITLE_FRACTION
    val flashDuration = burstDuration / 6.0
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    val imageSize = minOf(pageWidth, pageHeight) * 0.6f

    urls.forEachIndexed { index, url ->
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setScopeEnabled(block, "editor/select", false)
        engine.block.setWidth(block, imageSize)
        engine.block.setHeight(block, imageSize)
        engine.block.setPositionX(block, randomIn(-imageSize / 2f, pageWidth - imageSize / 2f))
        engine.block.setPositionY(block, randomIn(-imageSize / 2f, pageHeight - imageSize / 2f))

        val fill = engine.block.createFill(FillType.Image)
        engine.block.setFill(block, fill)
        engine.block.setString(fill, "fill/image/imageFileURI", url)
        engine.block.setShape(block, engine.block.createShape(ShapeType.Rect))
        engine.block.setDuration(block, flashDuration)
        engine.block.setInAnimation(block, fadeAnimation(engine, 0.1))
        engine.block.setOutAnimation(block, fadeAnimation(engine, 0.15))

        val startDelay = index * (flashDuration * 0.25)
        if (startDelay > 0) {
            engine.block.appendChild(burstTracks[index], createSpacerBlock(engine, page, startDelay))
        }
        engine.block.appendChild(burstTracks[index], block)
        val remaining = TITLE_DURATION - (startDelay + flashDuration)
        if (remaining > 0) {
            engine.block.appendChild(burstTracks[index], createSpacerBlock(engine, page, remaining))
        }
    }
}

private fun pickBurstImages(
    images: List<ImageItem>,
    count: Int,
): List<ImageItem> {
    val shuffled = images.shuffled(Random(System.currentTimeMillis()))
    if (shuffled.size >= count) return shuffled.take(count)
    return generateSequence { shuffled }.flatten().take(count).toList()
}

private fun fadeAnimation(
    engine: Engine,
    duration: Double,
): Int = engine.block.createAnimation(AnimationType.Fade).also { engine.block.setDuration(it, duration) }

private fun randomIn(
    min: Float,
    max: Float,
): Float = min + Random.nextFloat() * (max - min)
