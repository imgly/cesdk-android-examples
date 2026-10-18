package ly.img.editor.configuration.memories.scene

import android.util.Log
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.model.TimelineImage
import ly.img.editor.configuration.memories.util.AnimationPair
import ly.img.editor.configuration.memories.util.Animations
import ly.img.editor.configuration.memories.util.IMAGE_DURATION
import ly.img.editor.configuration.memories.util.OVERLAP_DURATION
import ly.img.editor.configuration.memories.util.TITLE_DURATION
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.math.abs
import kotlin.math.min

/**
 * The tracks are the single source of truth for the slideshow. Each photo/clip lives alone on its
 * own track, positioned in time by the track's time offset (see [slotStartTime]); tracks are
 * appended to the page in slot order, so a later slide always renders **above** the earlier one and
 * every crossfade reads as "the incoming clip fades in over the outgoing one".
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
// [findTracks]. Each media clip gets its own track tagged [TRACK_ROLE_MEDIA].
private const val TRACK_ROLE_KEY = "trackRole"
private const val TRACK_ROLE_MEDIA = "media"
private const val TRACK_ROLE_TEXT = "text"
private const val TRACK_ROLE_BACKGROUND = "background"
private const val TRACK_ROLE_MATTE = "matte"

private const val GRAPHIC_TYPE = "//ly.img.ubq/graphic"
private const val TEXT_TYPE = "//ly.img.ubq/text"
private const val TRACK_TYPE = "//ly.img.ubq/track"
private const val IMAGE_FILL_URI = "fill/image/imageFileURI"
private const val VIDEO_FILL_URI = "fill/video/fileURI"

// ---- Timing ------------------------------------------------------------------------------------

/** Seconds the montage is pushed back so it starts after the title card (0 when there's no title). */
private fun titleOffset(hasTitle: Boolean): Double = if (hasTitle) TITLE_DURATION - 1.0 else 0.0

/**
 * The crossfade overlap between two consecutive clips. Two photos crossfade, so they overlap by
 * [OVERLAP_DURATION]; anything next to a video hard-cuts (no animation), so there is no overlap —
 * the clips play back-to-back.
 */
private fun overlapBetween(
    a: ImageItem,
    b: ImageItem,
): Double = if (a.isVideo || b.isVideo) 0.0 else OVERLAP_DURATION

/**
 * Absolute page time at which each slot appears. Each clip advances by its own on-timeline duration
 * ([durations], one per slot — a photo is always [IMAGE_DURATION]; a short video is only as long as
 * its source) minus the overlap with the previous clip, so photo→photo boundaries overlap for the
 * crossfade while video-adjacent boundaries are back-to-back with no gap.
 */
internal fun slotStartTimes(
    media: List<ImageItem>,
    durations: List<Double>,
    hasTitle: Boolean,
): List<Double> {
    val starts = ArrayList<Double>(media.size)
    var t = titleOffset(hasTitle)
    for (i in media.indices) {
        if (i > 0) t += durations.getOrElse(i - 1) { IMAGE_DURATION } - overlapBetween(media[i - 1], media[i])
        starts.add(t)
    }
    return starts
}

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

/** The single media block that lives on a media [track], or null if it has none yet. */
private fun mediaBlockOf(
    engine: Engine,
    track: Int,
): Int? = engine.block.getChildren(track).firstOrNull { isImageBlock(engine, it) }

/** The media clips across all media tracks, in slot order (matches z / render order). */
internal fun imageBlocksInOrder(
    engine: Engine,
    tracks: SlideshowTracks,
): List<Int> = tracks.mediaTracks.mapNotNull { mediaBlockOf(engine, it) }

// ---- Track location by metadata ----------------------------------------------------------------

/**
 * The named tracks that make up the slideshow, located by their [TRACK_ROLE_KEY] metadata.
 * [mediaTracks] holds one track per slide, ordered by slot index (which equals their z order);
 * [textTrack] is null when the slideshow has no title.
 */
data class SlideshowTracks(
    val mediaTracks: List<Int>,
    val textTrack: Int?,
    val backgroundTrack: Int? = null,
    val matteTrack: Int? = null,
)

/**
 * Tag the persistent single-purpose tracks with their role so [findTracks] can re-locate them.
 * Media tracks are tagged individually in [createMediaTrack].
 */
fun tagSlideshowTracks(
    engine: Engine,
    textTrack: DesignBlock,
    backgroundTrack: DesignBlock,
    matteTrack: DesignBlock,
) {
    engine.block.setMetadata(textTrack, TRACK_ROLE_KEY, TRACK_ROLE_TEXT)
    engine.block.setMetadata(backgroundTrack, TRACK_ROLE_KEY, TRACK_ROLE_BACKGROUND)
    engine.block.setMetadata(matteTrack, TRACK_ROLE_KEY, TRACK_ROLE_MATTE)
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
 * The persistent black matte block (the single child of the matte track), sized to the media so a
 * crossfade between clips fades to black instead of revealing the moving video backdrop below it.
 * Null when the matte track or its block isn't present.
 */
fun matteBlock(
    engine: Engine,
    page: Int,
): Int? {
    val track = findTracks(engine, page)?.matteTrack ?: return null
    return engine.block.getChildren(track).firstOrNull()
}

/**
 * The slot index tagged on a media track's clip, used to order the tracks. Missing/garbled metadata
 * sorts last so a half-built track never jumps to the front.
 */
private fun mediaTrackIndex(
    engine: Engine,
    track: Int,
): Int {
    val block = mediaBlockOf(engine, track) ?: return Int.MAX_VALUE
    return runCatching { engine.block.getMetadata(block, IMAGE_INDEX_KEY).toInt() }.getOrDefault(Int.MAX_VALUE)
}

/**
 * Locate the slideshow tracks on [page] by their [TRACK_ROLE_KEY] metadata, ignoring burst tracks
 * (which carry no role). The media tracks are returned in slot order. Returns null if no media
 * tracks are present yet (the page hasn't been set up); the text track is optional and absent when
 * the slideshow has no title. Resolving the tracks fresh on each use avoids retaining block ids,
 * which the engine can invalidate over time (e.g. on undo/redo or a scene reload).
 */
fun findTracks(
    engine: Engine,
    page: Int,
): SlideshowTracks? {
    val mediaTracks = mutableListOf<Int>()
    var textTrack: Int? = null
    var backgroundTrack: Int? = null
    var matteTrack: Int? = null
    // Search the whole scene by type rather than walking page children: the video timeline nests
    // tracks under intermediate blocks, so they aren't always direct children of the page.
    for (t in engine.block.findByType(DesignBlockType.Track)) {
        val role = try {
            if (engine.block.hasMetadata(t, TRACK_ROLE_KEY)) engine.block.getMetadata(t, TRACK_ROLE_KEY) else null
        } catch (e: Exception) {
            null
        }
        when (role) {
            TRACK_ROLE_MEDIA -> mediaTracks.add(t)
            TRACK_ROLE_TEXT -> textTrack = t
            TRACK_ROLE_BACKGROUND -> backgroundTrack = t
            TRACK_ROLE_MATTE -> matteTrack = t
        }
    }
    // Return the scene's tracks even when there is no media (e.g. after "Clear all") so media can
    // still be re-added; only bail when the scene scaffold itself hasn't been created yet. Otherwise
    // clearing every clip would leave findTracks returning null, and the media dock / next apply
    // would bail early with no way back except restarting the editor.
    if (mediaTracks.isEmpty() && textTrack == null && backgroundTrack == null && matteTrack == null) return null
    mediaTracks.sortBy { mediaTrackIndex(engine, it) }
    return SlideshowTracks(mediaTracks, textTrack, backgroundTrack, matteTrack)
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
 * Compute each clip's window on the timeline from its track's time offset. Each media track holds a
 * single clip of [IMAGE_DURATION], so the window is simply [offset, offset + IMAGE_DURATION].
 * Returned in slot order.
 */
fun readTimelineImages(
    engine: Engine,
    tracks: SlideshowTracks,
): List<TimelineImage> = tracks.mediaTracks.mapNotNull { track ->
    val block = mediaBlockOf(engine, track) ?: return@mapNotNull null
    val (url, isVideo) = readMediaUrl(engine, block) ?: return@mapNotNull null
    val start = runCatching { engine.block.getTimeOffset(track) }.getOrDefault(0.0)
    val duration = runCatching { engine.block.getDuration(block) }.getOrDefault(IMAGE_DURATION)
    TimelineImage(
        url = url,
        startTime = start,
        endTime = start + duration,
        // Each clip has a symmetric in/out crossfade, so it is fully opaque and the only one on
        // screen at its midpoint.
        fullyVisibleTime = start + duration / 2.0,
        isVideo = isVideo,
    )
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
    val hasTitle = engine.block.getChildren(textTrack).any {
        runCatching { engine.block.getType(it) == TEXT_TYPE }.getOrDefault(false)
    }
    if (!hasTitle) return null

    // Exclude every non-burst track: media, text, and the full-length backdrop/matte tracks. Without
    // the last two, they are mistaken for burst tracks and their slideshow-length duration becomes
    // maxBurstEnd, so tapping the title chip seeks to the very end instead of just past the intro.
    val known = (tracks.mediaTracks + textTrack + listOfNotNull(tracks.backgroundTrack, tracks.matteTrack)).toSet()
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

// ---- Media tracks ------------------------------------------------------------------------------

/**
 * Create a full-page media track appended on top of the current stack (so it renders above earlier
 * slides) and positioned at [startTime] via its time offset. Callers add the single media block.
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

/** Position a media track at [startTime] on the page timeline. */
internal fun positionMediaTrack(
    engine: Engine,
    track: Int,
    startTime: Double,
) {
    engine.block.setTimeOffset(track, startTime)
}

/**
 * Each media track's on-timeline clip length (the block's own duration), in slot order. A photo (and
 * a video at least as long as the slot) is [IMAGE_DURATION]; a short video is only as long as its
 * source (see [placeVideoBlock]). Falls back to [IMAGE_DURATION] for any track missing its clip.
 */
private fun slotDurations(
    engine: Engine,
    mediaTracks: List<Int>,
): List<Double> = mediaTracks.map { track ->
    val block = mediaBlockOf(engine, track)
    block?.let { runCatching { engine.block.getDuration(it) }.getOrNull() } ?: IMAGE_DURATION
}

/**
 * Re-position every media track on [page] by the start time its slot occupies, derived from the
 * clips' actual durations (so a short video takes up only its own length, with no gap after it) and
 * the video-aware overlaps. Returns the per-slot durations so the caller can size the page/backdrops.
 * Call after all clip blocks for [media] have been created/edited so their durations are final.
 */
internal fun repositionTracks(
    engine: Engine,
    page: Int,
    media: List<ImageItem>,
    hasTitle: Boolean,
): List<Double> {
    val mediaTracks = findTracks(engine, page)?.mediaTracks ?: return emptyList()
    val durations = slotDurations(engine, mediaTracks)
    val starts = slotStartTimes(media, durations, hasTitle)
    mediaTracks.forEachIndexed { k, track ->
        positionMediaTrack(engine, track, starts.getOrElse(k) { 0.0 })
    }
    return durations
}

// ---- Animation ---------------------------------------------------------------------------------

/** True if the neighbour before slot [index] is a video (so this clip must hard-cut in). */
private fun hardCutIn(
    media: List<ImageItem>,
    index: Int,
): Boolean = index > 0 && media[index - 1].isVideo

/** True if the neighbour after slot [index] is a video (so this clip must hard-cut out). */
private fun hardCutOut(
    media: List<ImageItem>,
    index: Int,
): Boolean = index < media.size - 1 && media[index + 1].isVideo

/**
 * Apply a random slide animation to an image [block], honouring hard-cut boundaries: a photo that
 * sits next to a video drops its fade on that side ([suppressIn]/[suppressOut]) so the photo↔video
 * cut is clean instead of a fade fighting the video's hard edge. Suppressing a side simply leaves it
 * unset (no in/out animation = a hard cut), so callers must only use this on a freshly-created block
 * or one that already has no animation on the suppressed side.
 */
internal fun applySlideAnimation(
    engine: Engine,
    block: Int,
    suppressIn: Boolean,
    suppressOut: Boolean,
    animationPairs: List<AnimationPair>,
) {
    if (animationPairs.isEmpty()) return
    val pair = Animations.getRandomAnimationPair(animationPairs)

    // Engine animations are single-owner (1:1 with a design block), so each slide gets its own fresh
    // animation blocks — never a shared/pooled one. First drop any animation this block already owns
    // (re-applying on repaint would otherwise leak it: setIn/OutAnimation don't destroy the old one).
    clearSlideAnimations(engine, block)

    if (!suppressIn) {
        val inAnimation = pair.createIn(engine)
        engine.block.setInAnimation(block, inAnimation)
        // A Ken Burns zoom-out must be pre-scaled so its pan never reveals the canvas edges; other
        // animations (e.g. blur) have no such property, so the read returns 0 and nothing happens.
        val zoom = runCatching {
            engine.block.getFloat(inAnimation, "animation/ken_burns/zoomIntensity")
        }.getOrDefault(0f)
        if (zoom < 0f) engine.block.scale(block, 1f + abs(zoom), anchorX = 0.5f, anchorY = 0.5f)
    }
    if (!suppressOut) {
        val outAnimation = pair.createOut(engine)
        engine.block.setOutAnimation(block, outAnimation)
    }
}

/**
 * Destroy the in/out animation blocks [block] currently owns. The engine's setIn/OutAnimation do not
 * auto-destroy the animation they replace, so re-applying a slide animation without this would leak an
 * orphaned animation block on every edit. Destroying the animation also clears the block's reference
 * to it, leaving a clean base for [applySlideAnimation] to set fresh (or leave a suppressed side bare).
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
    media: List<ImageItem>,
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

    // Set animations before the index metadata is used elsewhere; a photo next to a video hard-cuts
    // on that side so the fade never fights the video's hard edge.
    applySlideAnimation(engine, block, hardCutIn(media, index), hardCutOut(media, index), animationPairs)

    writeImageMetadata(engine, block, image, index)
    return block
}

/**
 * Place a video slot and trim it for the [IMAGE_DURATION] window: create the [FillType.Video] block,
 * no animation (videos hard-cut — no fade/Ken Burns/zoom), metadata, then `forceLoadAVResource`
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

    // No animation on videos — they hard-cut in/out.
    writeImageMetadata(engine, block, image, index)
    return block
}

/** Create the media block for slot [index] on the right kind of block (image vs video). */
internal suspend fun createMediaBlock(
    engine: Engine,
    page: Int,
    index: Int,
    media: List<ImageItem>,
    animationPairs: List<AnimationPair>,
): Int {
    val image = media[index]
    return if (image.isVideo) {
        placeVideoBlock(engine, page, image, index)
    } else {
        createImageBlock(engine, page, image, index, media, animationPairs)
    }
}

// ---- Total duration ----------------------------------------------------------------------------

/**
 * Total page duration for [media] laid out by [slotStartTimes] with the given per-slot [durations]
 * (overlaps vary with video adjacency). The last clip ends its own duration after its start; a title
 * keeps a 1s tail after the last clip.
 */
fun calculateTotalDuration(
    media: List<ImageItem>,
    durations: List<Double>,
    hasTitle: Boolean,
): Double {
    if (media.isEmpty() || durations.isEmpty()) return if (hasTitle) TITLE_DURATION else IMAGE_DURATION
    val lastStart = slotStartTimes(media, durations, hasTitle).last()
    return lastStart + durations.last() + if (hasTitle) 1.0 else 0.0
}

// ---- Applying a staged edit in-place -----------------------------------------------------------

/**
 * Write [staged] back into the tracks in-place:
 *  - reuse an existing media track when both it and the staged item are the same media kind — an
 *    image slot is repainted (preserving per-block effects/filters) + re-animated; otherwise the
 *    slot's clip is rebuilt in the **same** track (keeping its z position) — e.g. image↔video, any
 *    video (trim can't just be repainted), or a photo that must gain/lose a hard-cut edge,
 *  - create a new track for each added item (appended on top → highest slot index → correct z),
 *  - destroy surplus tracks for deleted items,
 *  - then re-position every track by its slot's start time and recompute the page duration.
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
) {
    val existing = tracks.mediaTracks
    val m = existing.size
    val n = staged.size
    val common = min(m, n)

    // Reuse or rebuild the clip on each retained track.
    for (k in 0 until common) {
        val track = existing[k]
        val block = mediaBlockOf(engine, track)
        val stagedIsVideo = staged[k].isVideo
        val canRepaint = block != null &&
            !stagedIsVideo &&
            !isVideoBlock(engine, block) &&
            !needsAnimationRebuild(engine, block, staged, k)
        if (canRepaint && block != null) {
            // image -> image: repaint in place (keeps effects/filters), re-pick the slide animation.
            // The `block != null` above is redundant with canRepaint but lets the compiler smart-cast
            // block to non-null inside this branch.
            val fill = engine.block.getFill(block)
            engine.block.setString(fill, IMAGE_FILL_URI, staged[k].url)
            applySlideAnimation(engine, block, hardCutIn(staged, k), hardCutOut(staged, k), animationPairs)
            writeImageMetadata(engine, block, staged[k], k)
        } else {
            // Rebuild the clip inside the same track so its z position (slot order) is preserved.
            block?.let { engine.block.destroy(it) }
            engine.block.appendChild(track, createMediaBlock(engine, page, k, staged, animationPairs))
        }
    }

    // Append a new track per addition (k is the largest index so appending keeps z == slot order).
    // Positioned at 0 for now; every track is repositioned below once all clip durations are final.
    for (k in common until n) {
        val track = createMediaTrack(engine, page, 0.0)
        engine.block.appendChild(track, createMediaBlock(engine, page, k, staged, animationPairs))
    }

    // Destroy surplus tracks (and their clips) for deletions.
    for (k in n until m) {
        engine.block.destroy(existing[k])
    }

    // Re-locate the surviving tracks fresh (now re-indexed 0..n-1) and re-position each by its slot,
    // using the clips' final durations (a short video only takes up its own length). Recomputing from
    // `staged` means a reorder that moves a clip next to (or away from) a video gets both the right
    // overlap and the right animation (via the reuse/rebuild above).
    val durations = repositionTracks(engine, page, staged, hasTitle)
    engine.block.setDuration(page, calculateTotalDuration(staged, durations, hasTitle))

    // Checkpoint the post-edit state so this whole batch of track edits is undoable as one step.
    // Safe now that features/removeForegroundTracksOnSceneLoad is forced false in onCreate — without
    // that, restoring this snapshot would flatten the multi-track slideshow.
    engine.editor.addUndoStep()
}

/**
 * True if a repaint can't correctly set slot [k]'s animation because the clip currently carries an
 * animation on a side that must now hard-cut (photo became adjacent to a video). Rebuilding the clip
 * gives it a clean, animation-free base to which [applySlideAnimation] adds only the wanted sides.
 */
private fun needsAnimationRebuild(
    engine: Engine,
    block: Int,
    staged: List<ImageItem>,
    k: Int,
): Boolean {
    val hasIn = runCatching { engine.block.getInAnimation(block) }.getOrDefault(-1) != -1
    val hasOut = runCatching { engine.block.getOutAnimation(block) }.getOrDefault(-1) != -1
    return (hardCutIn(staged, k) && hasIn) || (hardCutOut(staged, k) && hasOut)
}
