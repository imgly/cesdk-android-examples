package ly.img.editor.configuration.memories.scene

import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.util.AnimationPair
import ly.img.engine.Engine

/**
 * Lay the photos/clips out as siblings on one media track, in slot order, and blend each into the
 * next with [transition]. The track's time offset holds the title gap; within it the engine places
 * the clips and applies every overlap, so there is no per-slot start-time arithmetic here.
 *
 * Videos are placed and trimmed first (see [placeVideoBlock]) so their durations are final before
 * the transitions reflow the timeline. Returns where the montage ends, for sizing the page.
 */
internal suspend fun createMainImageSequence(
    engine: Engine,
    page: Int,
    images: List<ImageItem>,
    hasTitle: Boolean,
    animationPairs: List<AnimationPair>,
    transition: TransitionSpec,
): Double {
    val track = createMediaTrack(engine, page, startTime = titleOffset(hasTitle))
    val clips = images.mapIndexed { index, image ->
        createMediaBlock(engine, page, index, image, animationPairs).also { clip ->
            engine.block.appendChild(parent = track, child = clip)
        }
    }
    wireTransitions(engine, clips, transition)

    return reflowedEndTime(engine, track, clips, hasTitle)
}
