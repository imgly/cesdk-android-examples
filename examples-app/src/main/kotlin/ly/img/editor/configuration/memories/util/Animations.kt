package ly.img.editor.configuration.memories.util

import ly.img.engine.AnimationType
import ly.img.engine.Engine

/**
 * The slide animations for the Memories slideshow — **this is the file to edit**.
 *
 * A slide's motion is one move that runs the whole clip: a slow Ken Burns drift the viewer barely
 * notices. The blend into the next slide is the style's transition, not an animation, so a slide
 * needs no out-animation to leave on — [AnimationPair.createOut] is null unless a recipe wants a
 * second move. The kit picks one pair at random per slide ([getRandomAnimationPair]).
 *
 * An [AnimationPair] is a *recipe*, not a pair of engine blocks: [AnimationPair.createIn] /
 * [AnimationPair.createOut] build a **fresh** engine animation each time they are called. Engine
 * animations are single-owner (1:1 with a design block), so every slide must own its own animation
 * instances — assigning one pooled animation block to several slides corrupts the scene (the engine
 * re-points the animation to the newest block only) and later crashes: destroying a slide auto-
 * destroys the shared animation, leaving every other slide holding a dangling id. [applySlideAnimation]
 * calls these builders per slide so each clip gets its own blocks.
 *
 * To change the motion, edit the list in [createAnimationPairs] — add, remove, or tweak entries, or
 * write a whole new recipe. The low-level builders below wrap the engine calls so the list stays
 * readable.
 */
data class AnimationPair(
    val createIn: (Engine) -> Int,
    /** A second move as the slide leaves. Null when the one move covers the whole clip. */
    val createOut: ((Engine) -> Int)? = null,
)

object Animations {
    /** The pool of animations a slide can use. Edit this list to change the slideshow's motion. */
    fun createAnimationPairs(): List<AnimationPair> = listOf(
        kenBurnsDrift(),
    )

    /** Pick a random pair for the next slide. */
    fun getRandomAnimationPair(animationPairs: List<AnimationPair>): AnimationPair = animationPairs.random()

    // ---- Low-level builders (you usually don't need to touch these) --------------------------

    /**
     * One slow Ken Burns across the whole clip: a gentle push in with a little drift, at a constant
     * rate. Linear is the Ken Burns choice — an eased curve spends its speed early or late, which
     * over a whole clip reads as the photo lurching and then crawling.
     *
     * Ken Burns is a pure transform while its `animation/ken_burns/fade` property stays at its
     * `false` default — leave it there. Blur, which this replaced, ramps the clip's alpha too, but
     * its `animation/blur/fade` defaults to `true`: set that to `false` and it is a pure transform
     * as well. Leaving it on is what made the incoming clip semi-transparent through the whole
     * transition overlap, so the backdrop showed through the blend.
     */
    // highlight-starter-kit-ken-burns-drift
    private fun kenBurnsDrift(): AnimationPair = AnimationPair(
        createIn = { engine ->
            engine.block.createAnimation(AnimationType.KenBurns).also { animation ->
                // The whole clip, so the move is slow enough to read as life rather than motion.
                engine.block.setDuration(animation, IMAGE_DURATION)
                engine.block.setFloat(animation, "animation/ken_burns/zoomIntensity", 0.2f)
                // A full crop-length of travel reads as the photo sliding past; a third of it reads
                // as drift.
                engine.block.setFloat(animation, "animation/ken_burns/travelDistanceRatio", 0.3f)
                engine.block.setEnum(animation, "animationEasing", "Linear")
            }
        },
    )
    // highlight-starter-kit-ken-burns-drift
}
