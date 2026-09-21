package ly.img.editor.configuration.memories.util

import ly.img.engine.AnimationType
import ly.img.engine.Engine

/**
 * The slide animations for the Memories slideshow — **this is the file to edit**.
 *
 * Every photo/clip gets an in-animation (as it appears) and an out-animation (as it leaves),
 * bundled together as an [AnimationPair]. The kit picks one pair at random per slide
 * ([getRandomAnimationPair]).
 *
 * An [AnimationPair] is a *recipe*, not a pair of engine blocks: [AnimationPair.createIn] /
 * [AnimationPair.createOut] build a **fresh** engine animation each time they are called. Engine
 * animations are single-owner (1:1 with a design block), so every slide must own its own animation
 * instances — assigning one pooled animation block to several slides corrupts the scene (the engine
 * re-points the animation to the newest block only) and later crashes: destroying a slide auto-
 * destroys the shared animation, leaving every other slide holding a dangling id. [applySlideAnimation]
 * calls these builders per slide so each clip gets its own blocks.
 *
 * To change the motion between slides, edit the list in [createAnimationPairs] — add, remove, or
 * tweak entries (e.g. the `blurZoom` zoom strength), or write a whole new pair from two engine
 * animations. The low-level builders below wrap the engine calls so the list stays readable.
 */
data class AnimationPair(
    val createIn: (Engine) -> Int,
    val createOut: (Engine) -> Int,
)

object Animations {
    /** The pool of animations a slide can use. Edit this list to change the slideshow's motion. */
    fun createAnimationPairs(): List<AnimationPair> = listOf(
        blurZoom(),
    )

    /** Pick a random pair for the next slide. */
    fun getRandomAnimationPair(animationPairs: List<AnimationPair>): AnimationPair = animationPairs.random()

    // ---- Low-level builders (you usually don't need to touch these) --------------------------

    /** Blur in, then crop-zoom out. [zoomScale] is how far the out-animation zooms (1.0 = none). */
    private fun blurZoom(zoomScale: Float = 1.7f): AnimationPair = AnimationPair(
        createIn = { engine ->
            engine.block.createAnimation(AnimationType.Blur).also { inAnimation ->
                engine.block.setDuration(inAnimation, IMAGE_DURATION * .5f)
                engine.block.setEnum(inAnimation, "animationEasing", "EaseOut")
            }
        },
        createOut = { engine ->
            engine.block.createAnimation(AnimationType.CropZoom).also { outAnimation ->
                engine.block.setDuration(outAnimation, IMAGE_DURATION * .5f)
                engine.block.setBoolean(outAnimation, "animation/crop_zoom/fade", true)
                engine.block.setEnum(outAnimation, "animationEasing", "EaseIn")
                engine.block.setFloat(outAnimation, "animation/crop_zoom/scale", zoomScale)
            }
        },
    )
}
