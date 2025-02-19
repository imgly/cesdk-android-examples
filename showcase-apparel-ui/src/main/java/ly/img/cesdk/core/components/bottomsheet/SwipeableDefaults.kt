package ly.img.cesdk.core.components.bottomsheet

import androidx.compose.animation.core.SpringSpec

/**
 * Contains useful defaults for [swipeable] and [SwipeableState].
 */
object SwipeableDefaults {
    /**
     * The default animation used by [SwipeableState].
     */
    val AnimationSpec = SpringSpec<Float>()
}