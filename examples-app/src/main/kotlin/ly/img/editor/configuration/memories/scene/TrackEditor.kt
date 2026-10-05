package ly.img.editor.configuration.memories.scene

import android.util.Log
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.model.TimelineImage
import ly.img.editor.configuration.memories.util.AnimationPair
import ly.img.editor.configuration.memories.util.Animations
import ly.img.editor.configuration.memories.util.IMAGE_DURATION
import ly.img.editor.configuration.memories.util.TITLE_DURATION
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.math.abs
import kotlin.math.min

/**
 * The tracks are the single source of truth for the slideshow. Every photo/clip is a sibling on the
 * one media track, in slot order, and each blends into the next through the transition its style
 * defines (see [TransitionSpec]). The engine places the clips and applies the overlaps, so the only
 * time this file sets is the track's own offset, which holds the title gap.
 *
 * Read the current media back from the tracks with [readImagesFromTracks] / [readTimelineImages];
 * stage an edit in-place with [applyDraftToTracks]. Block ids are never retained (the engine
 * invalidates them) — everything is re-located by the role/index metadata written here.
 */

private const val TAG = "MemoriesTracks"

// ---- Metadata keys -----------------------------------------------------------------------------

const val IMAGE_INDEX_KEY = "index" // slot order, also used by filter/animation code
private const val IMG_ID_KEY = "imgId" // stable UI id so the grid survives reordering
private const val CREATION_DATE_KEY = "creationDate"
private const val MEDIA_TYPE_KEY = "mediaType" // "image" (default) or "video"
private const val MEDIA_TYPE_VIDEO = "video"

const val SPACER_TYPE_KEY = "spacerType"
const val SPACER_BETWEEN = "between" // generic spacer (used by the burst-image intro)

// Track roles, tagged on the slideshow tracks so they can be re-located by metadata instead of
// retaining (engine-invalidated) block ids. See [tagSlideshowTracks] / [createMediaTrack] /
// [findTracks]. One track carries every clip, tagged [TRACK_ROLE_MEDIA].
private const val TRACK_ROLE_KEY = "trackRole"
private const val TRACK_ROLE_MEDIA = "media"
private const val TRACK_ROLE_TEXT = "text"
private const val TRACK_ROLE_BACKGROUND = "background"

private const val GRAPHIC_TYPE = "//ly.img.ubq/graphic"
private const val TEXT_TYPE = "//ly.img.ubq/text"
private const val TRACK_TYPE = "//ly.img.ubq/track"
private const val IMAGE_FILL_URI = "fill/image/imageFileURI"
private const val VIDEO_FILL_URI = "fill/video/fileURI"

// ---- Timing ------------------------------------------------------------------------------------

/** Seconds the montage is pushed back so it starts after the title card (0 when there's no title). */
internal fun titleOffset(hasTitle: Boolean): Double = if (hasTitle) TITLE_DURATION - 1.0 else 0.0

// ---- Spacer (burst-image intro only) -----------------------------------------------------------

/**
 * An empty, full-page graphic block used purely for timing (only the title's burst intro uses these
 * now — the main slides are positioned by their track's time offset instead).
 */
internal fun createSpacerBlock(
    engine: Engine,
    page: Int,
    duration: Double,
    type: String = SPACER_BETWEEN,
): Int {
    val spacer = engine.block.create(DesignBlockType.Graphic)
    engine.block.setWidth(spacer, engine.block.getWidth(page))
    engine.block.setHeight(spacer, engine.block.getHeight(page))
    engine.block.setDuration(spacer, duration)
    engine.block.setScopeEnabled(spacer, "editor/select", false)
    engine.block.setMetadata(spacer, SPACER_TYPE_KEY, type)
    return spacer
}

// ---- Block classification ----------------------------------------------------------------------

/** A media slot (image or video): a graphic block carrying [IMAGE_INDEX_KEY] and no spacer tag. */
private fun isImageBlock(
    engine: Engine,
    block: Int,
): Boolean = try {
    engine.block.getType(block) == GRAPHIC_TYPE &&
        engine.block.hasMetadata(block, IMAGE_INDEX_KEY) &&
        !engine.block.hasMetadata(block, SPACER_TYPE_KEY)
} catch (e: Exception) {
    false
}

/** True if the block carries the video media-type tag. */
internal fun isVideoBlock(
    engine: Engine,
    block: Int,
): Boolean = try {
    engine.block.hasMetadata(block, MEDIA_TYPE_KEY) &&
        engine.block.getMetadata(block, MEDIA_TYPE_KEY) == MEDIA_TYPE_VIDEO
} catch (e: Exception) {
    false
}

/** The montage's clips, in slot order — which is also the order they play. */
internal fun imageBlocksInOrder(
    engine: Engine,
    tracks: SlideshowTracks,
): List<Int> = tracks.mediaTrack
    ?.let { track -> engine.block.getChildren(track).filter { isImageBlock(engine, it) } }
    .orEmpty()

// ---- Track location by metadata ----------------------------------------------------------------

/**
 * The named tracks that make up the slideshow, located by their [TRACK_ROLE_KEY] metadata.
 * [mediaTrack] carries every clip; it is null before the first media is added. [textTrack] is null
 * when the slideshow has no title.
 */
data class SlideshowTracks(
    val mediaTrack: Int?,
    val textTrack: Int?,
    val backgroundTrack: Int? = null,
)

/**
 * Tag the persistent single-purpose tracks with their role so [findTracks] can re-locate them.
 * The media track is tagged in [createMediaTrack], which creates it on demand.
 */
fun tagSlideshowTracks(
    engine: Engine,
    textTrack: DesignBlock,
    backgroundTrack: DesignBlock,
) {
    engine.block.setMetadata(textTrack, TRACK_ROLE_KEY, TRACK_ROLE_TEXT)
    engine.block.setMetadata(backgroundTrack, TRACK_ROLE_KEY, TRACK_ROLE_BACKGROUND)
}

/**
 * The persistent full-page backdrop block (the single child of the background track), or null when
 * the background track or its block isn't present. The Styles application paints this block per
 * style (hidden for Default, white for Noir, a looping video for Hologram/Bubblegum).
 */
fun backgroundBlock(
    engine: Engine,
    page: Int,
): Int? {
    val track = findTracks(engine, page)?.backgroundTrack ?: return null
    return engine.block.getChildren(track).firstOrNull()
}

/**
 * Locate the slideshow tracks on [page] by their [TRACK_ROLE_KEY] metadata, ignoring burst tracks
 * (which carry no role). Returns null only when the scene scaffold itself is absent. Resolving the
 * tracks fresh on each use avoids retaining block ids, which the engine can invalidate over time
 * (e.g. on undo/redo or a scene reload).
 */
fun findTracks(
    engine: Engine,
    page: Int,
): SlideshowTracks? {
    var mediaTrack: Int? = null
    var textTrack: Int? = null
    var backgroundTrack: Int? = null
    // Search the whole scene by type rather than walking page children: the video timeline nests
    // tracks under intermediate blocks, so they aren't always direct children of the page.
    for (t in engine.block.findByType(DesignBlockType.Track)) {
        val role = try {
            if (engine.block.hasMetadata(t, TRACK_ROLE_KEY)) engine.block.getMetadata(t, TRACK_ROLE_KEY) else null
        } catch (e: Exception) {
            null
        }
        when (role) {
            TRACK_ROLE_MEDIA -> mediaTrack = t
            TRACK_ROLE_TEXT -> textTrack = t
            TRACK_ROLE_BACKGROUND -> backgroundTrack = t
        }
    }
    // Return the scene's tracks even when there is no media (e.g. after "Clear all") so media can
    // still be re-added; only bail when the scene scaffold itself hasn't been created yet. Otherwise
    // clearing every clip would leave findTracks returning null, and the media dock / next apply
    // would bail early with no way back except restarting the editor.
    if (mediaTrack == null && textTrack == null && backgroundTrack == null) return null
    return SlideshowTracks(mediaTrack, textTrack, backgroundTrack)
}

// ---- Metadata read/write -----------------------------------------------------------------------

internal fun writeImageMetadata(
    engine: Engine,
    block: Int,
    image: ImageItem,
    index: Int,
) {
    engine.block.setMetadata(block, IMAGE_INDEX_KEY, "$index")
    engine.block.setMetadata(block, IMG_ID_KEY, "${image.id}")
    engine.block.setMetadata(block, MEDIA_TYPE_KEY, if (image.isVideo) MEDIA_TYPE_VIDEO else "image")
    image.creationDate?.let { engine.block.setMetadata(block, CREATION_DATE_KEY, "$it") }
}

private fun readCreationDate(
    engine: Engine,
    block: Int,
): Long? = if (engine.block.hasMetadata(block, CREATION_DATE_KEY)) {
    engine.block.getMetadata(block, CREATION_DATE_KEY).toLongOrNull()
} else {
    null
}

/** Read the media URL from a slot's fill (image or video) plus whether it is a video. */
private fun readMediaUrl(
    engine: Engine,
    block: Int,
): Pair<String, Boolean>? {
    val fill = try {
        engine.block.getFill(block)
    } catch (e: Exception) {
        return null
    }
    runCatching { engine.block.getString(fill, IMAGE_FILL_URI) }.getOrNull()?.let { return it to false }
    runCatching { engine.block.getString(fill, VIDEO_FILL_URI) }.getOrNull()?.let { return it to true }
    return null
}

// ---- Reading the grid from the tracks (source of truth) ----------------------------------------

/** Rebuild the editable media list from the actual track blocks. */
fun readImagesFromTracks(
    engine: Engine,
    tracks: SlideshowTracks,
): List<ImageItem> {
    return imageBlocksInOrder(engine, tracks).mapIndexedNotNull { position, block ->
        val (url, isVideo) = readMediaUrl(engine, block) ?: run {
            Log.w(TAG, "Media slot $block at position $position has no readable fill; skipping")
            return@mapIndexedNotNull null
        }

        val id = if (engine.block.hasMetadata(block, IMG_ID_KEY)) {
            engine.block.getMetadata(block, IMG_ID_KEY).toIntOrNull() ?: (position + 1)
        } else {
            position + 1
        }

        ImageItem(
            id = id,
            url = url,
            isVideo = isVideo,
            creationDate = readCreationDate(engine, block),
        )
    }
}

/**
 * Each clip's window on the page timeline, in slot order. A clip's own offset is relative to the
 * media track, which itself sits at the title offset, so the two add up to page time.
 *
 * [TimelineImage.fullyVisibleTime] is derived from the neighbours' actual windows, not from the
 * requested transition duration, because the engine clamps every overlap (see [TransitionSpec]).
 */
fun readTimelineImages(
    engine: Engine,
    tracks: SlideshowTracks,
): List<TimelineImage> {
    val track = tracks.mediaTrack ?: return emptyList()
    val trackOffset = runCatching { engine.block.getTimeOffset(track) }.getOrDefault(0.0)
    val clips = imageBlocksInOrder(engine, tracks).mapNotNull { block ->
        val (url, isVideo) = readMediaUrl(engine, block) ?: return@mapNotNull null
        val start = trackOffset + runCatching { engine.block.getTimeOffset(block) }.getOrDefault(0.0)
        val duration = runCatching { engine.block.getDuration(block) }.getOrDefault(IMAGE_DURATION)
        TimelineImage(
            url = url,
            startTime = start,
            endTime = start + duration,
            // The midpoint, refined below once the neighbours are known.
            fullyVisibleTime = start + duration / 2.0,
            isVideo = isVideo,
        )
    }

    // A clip is alone on screen only between its neighbours' overlaps. A clip shorter than twice
    // the transition is blended end to end and has no such window, so it keeps its midpoint —
    // the least-blended moment it has.
    return clips.mapIndexed { index, clip ->
        val clearFrom = maxOf(clip.startTime, clips.getOrNull(index - 1)?.endTime ?: clip.startTime)
        val clearTo = minOf(clip.endTime, clips.getOrNull(index + 1)?.startTime ?: clip.endTime)
        if (clearFrom >= clearTo) clip else clip.copy(fullyVisibleTime = (clearFrom + clearTo) / 2.0)
    }
}

/**
 * If a title is present, returns the time at which the burst images have finished and the title is
 * visible unobstructed (the latest burst-image end across the burst tracks). Returns null when
 * there is no title.
 */
fun readTitleClearTime(
    engine: Engine,
    page: Int,
    tracks: SlideshowTracks,
): Double? {
    val textTrack = tracks.textTrack ?: return null
    if (!hasTitleCard(engine, tracks)) return null

    // Exclude every non-burst track: media, text, and the full-length backdrop track. Without the
    // last one, it is mistaken for a burst track and its slideshow-length duration becomes
    // maxBurstEnd, so tapping the title chip seeks to the very end instead of just past the intro.
    val known = listOfNotNull(tracks.mediaTrack, textTrack, tracks.backgroundTrack).toSet()
    var maxBurstEnd = 0.0
    for (child in engine.block.getChildren(page)) {
        if (child in known) continue
        if (runCatching { engine.block.getType(child) }.getOrNull() != TRACK_TYPE) continue
        // Burst track: a burst image is a graphic with no spacer tag and no slot index.
        var t = 0.0
        for (b in engine.block.getChildren(child)) {
            val d = runCatching { engine.block.getDuration(b) }.getOrDefault(0.0)
            t += d
            val isBurstImage = runCatching {
                engine.block.getType(b) == GRAPHIC_TYPE &&
                    !engine.block.hasMetadata(b, SPACER_TYPE_KEY) &&
                    !engine.block.hasMetadata(b, IMAGE_INDEX_KEY)
            }.getOrDefault(false)
            if (isBurstImage) maxBurstEnd = maxOf(maxBurstEnd, t)
        }
    }
    return maxBurstEnd
}

/**
 * True when the slideshow has a title card. The card pushes the montage back by [titleOffset] and
 * keeps a 1s tail after the last clip, so anything that re-derives the timeline needs to know.
 */
internal fun hasTitleCard(
    engine: Engine,
    tracks: SlideshowTracks,
): Boolean {
    val textTrack = tracks.textTrack ?: return false
    return engine.block.getChildren(textTrack).any {
        runCatching { engine.block.getType(it) == TEXT_TYPE }.getOrDefault(false)
    }
}

// ---- Media track -------------------------------------------------------------------------------

/**
 * Create the full-page media track that carries every clip, positioned at [startTime] via its time
 * offset so the montage starts after the title card. Callers append the clips.
 */
internal fun createMediaTrack(
    engine: Engine,
    page: Int,
    startTime: Double,
): Int {
    val track = engine.block.create(DesignBlockType.Track)
    engine.block.setMetadata(track, TRACK_ROLE_KEY, TRACK_ROLE_MEDIA)
    engine.block.appendChild(page, track)
    engine.block.fillParent(track)
    positionMediaTrack(engine, track, startTime)
    return track
}

/** Position the media track at [startTime] on the page timeline. */
internal fun positionMediaTrack(
    engine: Engine,
    track: Int,
    startTime: Double,
) {
    engine.block.setTimeOffset(track, startTime)
}

// ---- Animation ---------------------------------------------------------------------------------

/**
 * Give an image [block] a fresh slide animation, picked at random from [animationPairs]. A recipe
 * may leave the out-animation unset, which is the normal case now the transition does the leaving.
 *
 * Every side is a pure transform. The transition owns the blend between two clips, so an animation
 * that also ramped the clip's alpha would leave the pair semi-transparent for the whole overlap and
 * show the backdrop through it — see [Animations].
 */
internal fun applySlideAnimation(
    engine: Engine,
    block: Int,
    animationPairs: List<AnimationPair>,
) {
    if (animationPairs.isEmpty()) return
    val pair = Animations.getRandomAnimationPair(animationPairs)

    // Engine animations are single-owner (1:1 with a design block), so each slide gets its own fresh
    // animation blocks — never a shared/pooled one. First drop any animation this block already owns
    // (re-applying on repaint would otherwise leak it: setIn/OutAnimation don't destroy the old one).
    clearSlideAnimations(engine, block)

    val inAnimation = pair.createIn(engine)
    engine.block.setInAnimation(block, inAnimation)
    // A Ken Burns zoom-out must be pre-scaled so its pan never reveals the canvas edges; an
    // animation without that property reads 0, so nothing happens.
    val zoom = runCatching {
        engine.block.getFloat(inAnimation, "animation/ken_burns/zoomIntensity")
    }.getOrDefault(0f)
    if (zoom < 0f) engine.block.scale(block, 1f + abs(zoom), anchorX = 0.5f, anchorY = 0.5f)

    pair.createOut?.let { engine.block.setOutAnimation(block, it(engine)) }
}

/**
 * Destroy the in/out animation blocks [block] currently owns. The engine's setIn/OutAnimation do not
 * auto-destroy the animation they replace, so re-applying a slide animation without this would leak an
 * orphaned animation block on every edit. Destroying the animation also clears the block's reference
 * to it, leaving a clean base for [applySlideAnimation] to set fresh.
 */
private fun clearSlideAnimations(
    engine: Engine,
    block: Int,
) {
    listOf(
        runCatching { engine.block.getInAnimation(block) }.getOrDefault(-1),
        runCatching { engine.block.getOutAnimation(block) }.getOrDefault(-1),
    ).forEach { animation ->
        if (animation != -1 && runCatching { engine.block.isValid(animation) }.getOrDefault(false)) {
            engine.block.destroy(animation)
        }
    }
}

// ---- Media block creation ----------------------------------------------------------------------

private fun createImageBlock(
    engine: Engine,
    page: Int,
    image: ImageItem,
    index: Int,
    animationPairs: List<AnimationPair>,
): Int {
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setScopeEnabled(block, "editor/select", false)
    engine.block.setWidth(block, engine.block.getWidth(page))
    engine.block.setHeight(block, engine.block.getHeight(page))

    val fill = engine.block.createFill(FillType.Image)
    val shape = engine.block.createShape(ShapeType.Rect)
    engine.block.setFill(block, fill)
    engine.block.setString(fill, IMAGE_FILL_URI, image.url)
    engine.block.setShape(block, shape)
    engine.block.setDuration(block, IMAGE_DURATION)

    applySlideAnimation(engine, block, animationPairs)

    writeImageMetadata(engine, block, image, index)
    return block
}

/**
 * Place a video slot and trim it for the [IMAGE_DURATION] window: create the [FillType.Video] block,
 * no slide animation (a video carries its own motion), metadata, then `forceLoadAVResource`
 * (cheap, ~100ms) and trim by source length:
 *  - shorter than the slot → play the whole source once at its natural length and shrink the block
 *    to match, so the clip is only as long as the video (no looping, no blank tail); the layout
 *    starts the next clip right after,
 *  - longer than the slot → take the middle [IMAGE_DURATION] (centered on the clip so the window
 *    starts a little after the source start and ends before its end, landing on the action),
 *  - unknown duration → trim [IMAGE_DURATION] from the start and loop.
 */
suspend fun placeVideoBlock(
    engine: Engine,
    page: Int,
    image: ImageItem,
    index: Int,
): Int {
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setScopeEnabled(block, "editor/select", false)
    engine.block.setWidth(block, engine.block.getWidth(page))
    engine.block.setHeight(block, engine.block.getHeight(page))

    val fill = engine.block.createFill(FillType.Video)
    val shape = engine.block.createShape(ShapeType.Rect)
    engine.block.setFill(block, fill)
    engine.block.setString(fill, VIDEO_FILL_URI, image.url)
    engine.block.setShape(block, shape)
    engine.block.setDuration(block, IMAGE_DURATION)

    // forceLoadAVResource is cheap (~100ms); the video needs it to render and to query its duration.
    engine.block.forceLoadAVResource(fill)
    val sourceDuration = runCatching { engine.block.getAVResourceTotalDuration(fill) }.getOrDefault(0.0)

    when {
        sourceDuration in 0.0..IMAGE_DURATION && sourceDuration > 0.0 -> {
            // Shorter than the slot: play the source once at its natural length and shrink the block
            // to match, so the clip occupies only as much of the timeline as the video is long — no
            // looping and no blank tail. The slot layout picks up this shorter block duration.
            engine.block.setTrimOffset(fill, 0.0)
            engine.block.setTrimLength(fill, sourceDuration)
            engine.block.setLooping(fill, false)
            engine.block.setDuration(block, sourceDuration)
        }

        sourceDuration > IMAGE_DURATION -> {
            // Longer than the slot: take the middle IMAGE_DURATION so the window is centered on the
            // clip (starts after the source start, ends before its end) rather than the lead-in.
            engine.block.setTrimOffset(fill, (sourceDuration - IMAGE_DURATION) / 2.0)
            engine.block.setTrimLength(fill, IMAGE_DURATION)
            engine.block.setLooping(fill, false)
        }

        else -> {
            // Unknown duration: trim IMAGE_DURATION from the start and loop to be safe.
            engine.block.setTrimOffset(fill, 0.0)
            engine.block.setTrimLength(fill, IMAGE_DURATION)
            engine.block.setLooping(fill, true)
        }
    }

    // No slide animation on a video: it already moves.
    writeImageMetadata(engine, block, image, index)
    return block
}

/** Create the clip for slot [index] on the right kind of block (image vs video). */
internal suspend fun createMediaBlock(
    engine: Engine,
    page: Int,
    index: Int,
    image: ImageItem,
    animationPairs: List<AnimationPair>,
): Int = if (image.isVideo) {
    placeVideoBlock(engine, page, image, index)
} else {
    createImageBlock(engine, page, image, index, animationPairs)
}

// ---- Applying a staged edit in-place -----------------------------------------------------------

/**
 * Write [staged] back into the media track in-place:
 *  - a retained photo slot is repainted, which keeps the effects the active style put on it and the
 *    fill it has already loaded,
 *  - any other retained slot is rebuilt at the same index — image↔video, or a video, whose trim
 *    cannot just be repainted,
 *  - additions are appended, surplus clips are destroyed,
 *  - then every boundary is re-wired with [transition] and the page resized to the reflowed end.
 *
 * Video clips are placed + loaded + trimmed here (forceLoad is cheap, so they render right away);
 * suspends for the cheap per-video `forceLoadAVResource`.
 */
suspend fun applyDraftToTracks(
    engine: Engine,
    page: Int,
    tracks: SlideshowTracks,
    staged: List<ImageItem>,
    animationPairs: List<AnimationPair>,
    hasTitle: Boolean,
    transition: TransitionSpec,
) {
    val track = tracks.mediaTrack ?: createMediaTrack(engine, page, titleOffset(hasTitle))
    val existing = engine.block.getChildren(track).filter { isImageBlock(engine, it) }

    // Detach every transition up front. Destroying a clip destroys the transition it owns, but one
    // left on a surviving clip would blend into whatever ends up next to it.
    clearTransitions(engine, existing)

    val clips = ArrayList<Int>(staged.size)
    for (k in 0 until min(existing.size, staged.size)) {
        val block = existing[k]
        val item = staged[k]
        if (!item.isVideo && !isVideoBlock(engine, block)) {
            // photo -> photo: repaint in place, so the style's effects on this block survive.
            engine.block.setString(engine.block.getFill(block), IMAGE_FILL_URI, item.url)
            applySlideAnimation(engine, block, animationPairs)
            writeImageMetadata(engine, block, item, k)
            clips.add(block)
        } else {
            // Destroying the old clip shifts its siblings left, so index k is where the replacement
            // goes to keep the montage in slot order.
            engine.block.destroy(block)
            val rebuilt = createMediaBlock(engine, page, k, item, animationPairs)
            engine.block.insertChild(parent = track, child = rebuilt, index = k)
            clips.add(rebuilt)
        }
    }
    for (k in existing.size until staged.size) {
        val added = createMediaBlock(engine, page, k, staged[k], animationPairs)
        engine.block.appendChild(parent = track, child = added)
        clips.add(added)
    }
    for (k in staged.size until existing.size) {
        engine.block.destroy(existing[k])
    }

    positionMediaTrack(engine, track, titleOffset(hasTitle))
    // Re-blend before the checkpoint below, otherwise Undo restores a montage of hard cuts on a
    // page that runs the un-overlapped length. The style pass re-wires the same spec afterwards,
    // which changes nothing, and this way a style that fails still leaves the montage blended.
    wireTransitions(engine, clips, transition)
    engine.block.setDuration(page, reflowedEndTime(engine, track, clips, hasTitle))

    // Checkpoint the post-edit state so this whole batch of track edits is undoable as one step.
    // Safe now that features/removeForegroundTracksOnSceneLoad is forced false in onCreate — without
    // that, restoring this snapshot would flatten the slideshow.
    engine.editor.addUndoStep()
}
