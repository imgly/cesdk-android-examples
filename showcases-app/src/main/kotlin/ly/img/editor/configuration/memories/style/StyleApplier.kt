package ly.img.editor.configuration.memories.style

import android.util.Log
import ly.img.editor.configuration.memories.scene.backgroundBlock
import ly.img.editor.configuration.memories.scene.findTracks
import ly.img.editor.configuration.memories.scene.imageBlocksInOrder
import ly.img.editor.configuration.memories.scene.matteBlock
import ly.img.engine.Color
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.Font
import ly.img.engine.Typeface
import kotlin.math.abs

private const val TAG = "MemoriesStyles"

/**
 * Generic, data-driven application of a [VideoStyle] to the engine. These functions read
 * everything they need from the style, so they never have to change when styles are added,
 * removed, or tuned in [VideoStyles].
 */

/**
 * Apply [style] to the whole slideshow: filter + scale every media clip, paint the backdrop and
 * matte, restyle the title, and checkpoint it as one undo step. The tracks are the source of truth,
 * so this reads the clips back from them rather than holding block ids.
 */
suspend fun applyStyleToSlideshow(
    engine: Engine,
    page: Int,
    style: VideoStyle,
) {
    val tracks = findTracks(engine, page) ?: return
    val duration = engine.block.getDuration(page)

    imageBlocksInOrder(engine, tracks).forEach { block ->
        if (engine.block.supportsEffects(block)) {
            engine.block.getEffects(block).indices.reversed().forEach { engine.block.removeEffect(block, it) }
            applyStyleEffects(engine, block, style)
        }
        applyMediaScale(engine, block, page, style)
    }

    backgroundBlock(engine, page)?.let { applyStyleBackground(engine, it, style, duration) }
    matteBlock(engine, page)?.let {
        applyMediaScale(engine, it, page, style)
        engine.block.setDuration(it, duration)
    }

    tracks.textTrack?.let { textTrack ->
        engine.block.getChildren(textTrack)
            .filter { runCatching { engine.block.getMetadata(it, "blockType") }.getOrNull() == "title" }
            .forEach { updateTitleTypeface(engine, it, style) }
    }

    engine.editor.addUndoStep()
}

/** Set the title's font + color for [style] (Noir reads black on its white backdrop; others white). */
private suspend fun updateTitleTypeface(
    engine: Engine,
    textBlock: Int,
    style: VideoStyle,
) {
    try {
        engine.block.setTextColor(textBlock, Color.fromHex(style.titleTextColorHex))
        val assets = engine.asset.findAssets(
            sourceId = "ly.img.typeface",
            query = FindAssetsQuery(query = style.typeface, page = 0, perPage = 100),
        )
        val typeface = assets.assets.firstOrNull()?.payload?.typeface ?: return
        val font = resolveFont(typeface, style) ?: return
        engine.block.setFont(textBlock, font.uri, typeface)
    } catch (e: Exception) {
        Log.w(TAG, "Could not restyle title for '${style.id}'", e)
    }
}

private const val VIDEO_FILL_URI = "fill/video/fileURI"

/** Applies a style's image-filter adjustments to a single media [block]. */
fun applyStyleEffects(
    engine: Engine,
    block: Int,
    style: VideoStyle,
) {
    if (style.adjustments.isNotEmpty()) {
        val adjustments = engine.block.createEffect(EffectType.Adjustments)
        engine.block.appendEffect(block, adjustments)
        style.adjustments.forEach { (property, value) ->
            engine.block.setFloat(adjustments, property, value)
        }
    }
}

/**
 * Size + center a media [block] within the [page] for the style's [VideoStyle.mediaScale]:
 * full-bleed at 1.0, or scaled down and centered so the [VideoStyle.background] shows around it.
 *
 * This sets the block's **size and position** directly (rather than an `engine.block.scale` transform)
 * so it cooperates with the slideshow's Ken Burns pan/zoom. The Ken Burns code pre-scales zoom-out
 * clips (via `scale`, which mutates width/height) so the pan never reveals their edges; resetting the
 * size here would wipe that out, so after re-establishing the clean centered base frame we re-apply
 * the very same compensation on top. The base is reset first, so re-applying a style is idempotent
 * and never compounds the scale. Blocks without an in-animation (videos, the matte) just get sized.
 */
fun applyMediaScale(
    engine: Engine,
    block: Int,
    page: Int,
    style: VideoStyle,
) {
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    val scale = style.mediaScale.coerceIn(0.1f, 1f)

    // Clean, centered base frame at the target size.
    engine.block.setWidth(block, pageWidth * scale)
    engine.block.setHeight(block, pageHeight * scale)
    engine.block.setPositionX(block, pageWidth * (1f - scale) / 2f)
    engine.block.setPositionY(block, pageHeight * (1f - scale) / 2f)

    // Re-coordinate Ken Burns: a zoom-out clip is pre-scaled around its center so its pan stays
    // framed within the new size instead of revealing the backdrop at its edges.
    val inAnimation = runCatching { engine.block.getInAnimation(block) }.getOrDefault(-1)
    if (inAnimation != -1) {
        val zoomIntensity = runCatching {
            engine.block.getFloat(inAnimation, "animation/ken_burns/zoomIntensity")
        }.getOrDefault(0f)
        if (zoomIntensity < 0f) {
            engine.block.scale(block, 1f + abs(zoomIntensity), anchorX = 0.5f, anchorY = 0.5f)
        }
    }
}

/**
 * Paint the persistent [backgroundBlock] for the style's [VideoStyle.background]: hidden for
 * [StyleBackground.None], a flat color for [StyleBackground.Solid], or a muted, looping video
 * spanning [pageDuration] for [StyleBackground.Video].
 */
suspend fun applyStyleBackground(
    engine: Engine,
    backgroundBlock: Int,
    style: VideoStyle,
    pageDuration: Double,
) {
    when (val background = style.background) {
        StyleBackground.None -> {
            engine.block.setVisible(backgroundBlock, false)
        }

        is StyleBackground.Solid -> {
            val fill = ensureFill(engine, backgroundBlock, FillType.Color)
            engine.block.setColor(fill, "fill/color/value", Color.fromHex(background.colorHex))
            engine.block.setVisible(backgroundBlock, true)
        }

        is StyleBackground.Video -> {
            // Resolve the backdrop clip from the custom local asset source rather than a hard-coded
            // path; if the asset is missing, hide the backdrop instead of setting an empty fill.
            val uri = engine.styleBackgroundVideoUri(background.assetId) ?: run {
                engine.block.setVisible(backgroundBlock, false)
                return
            }
            val fill = ensureFill(engine, backgroundBlock, FillType.Video)
            // Only (re)set the source + reload when it actually changed; re-applying a style (e.g.
            // after a track rebuild) shouldn't pay the decode cost again for the same backdrop.
            val current = runCatching { engine.block.getString(fill, VIDEO_FILL_URI) }.getOrNull()
            if (current != uri) {
                engine.block.setString(fill, VIDEO_FILL_URI, uri)
                engine.block.forceLoadAVResource(fill)
            }
            val sourceDuration = runCatching { engine.block.getAVResourceTotalDuration(fill) }.getOrDefault(0.0)
            engine.block.setLooping(fill, true)
            engine.block.setMuted(fill, true)
            engine.block.setTrimOffset(fill, 0.0)
            if (sourceDuration > 0.0) engine.block.setTrimLength(fill, sourceDuration)
            // Keep the backdrop the full length of the slideshow so the loop always covers it.
            engine.block.setDuration(backgroundBlock, pageDuration)
            engine.block.setVisible(backgroundBlock, true)
        }
    }
}

/**
 * Return the [block]'s fill, reusing it when it is already the requested [type], otherwise creating
 * a fresh fill of that type (and destroying the previous one). Lets us flip the persistent
 * background block between a color and a video without leaking the discarded fill.
 */
private fun ensureFill(
    engine: Engine,
    block: Int,
    type: FillType,
): Int {
    val existing = runCatching { engine.block.getFill(block) }.getOrDefault(-1)
    val existingValid = existing != -1 && runCatching { engine.block.isValid(existing) }.getOrDefault(false)
    if (existingValid && runCatching { engine.block.getType(existing) }.getOrNull() == type.key) {
        return existing
    }
    val fill = engine.block.createFill(type)
    engine.block.setFill(block, fill)
    if (existingValid) runCatching { engine.block.destroy(existing) }
    return fill
}

/**
 * Picks the best font for [style] from [typeface]: the first available sub-family in the
 * style's preference order, falling back to the typeface's first font.
 */
fun resolveFont(
    typeface: Typeface,
    style: VideoStyle,
): Font? {
    for (weight in style.fontWeights) {
        typeface.fonts.firstOrNull { it.subFamily == weight }?.let { return it }
    }
    return typeface.fonts.firstOrNull()
}
