package ly.img.editor.configuration.memories.scene

import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.util.AnimationPair
import ly.img.engine.Engine

/**
 * Lay the photos/clips out, one per track. Each track is appended on top of the previous one so a
 * later slide always renders above the earlier one; the clips (videos included) are created first so
 * their final durations are known, then every track is positioned in time by [repositionTracks] —
 * consecutive slides overlap for the crossfade, a short video takes up only its own length, and
 * photos next to a video hard-cut on that side (handled per-clip in [createMediaBlock]). Returns the
 * per-slot durations so the caller can size the page and backdrops to match.
 */
internal suspend fun createMainImageSequence(
    engine: Engine,
    page: Int,
    images: List<ImageItem>,
    hasTitle: Boolean,
    animationPairs: List<AnimationPair>,
): List<Double> {
    images.forEachIndexed { index, _ ->
        val track = createMediaTrack(engine, page, startTime = 0.0)
        engine.block.appendChild(parent = track, child = createMediaBlock(engine, page, index, images, animationPairs))
    }
    return repositionTracks(engine, page, images, hasTitle)
}
