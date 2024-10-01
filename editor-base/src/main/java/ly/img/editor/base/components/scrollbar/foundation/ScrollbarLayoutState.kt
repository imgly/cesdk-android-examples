package ly.img.editor.base.components.scrollbar.foundation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
internal fun rememberScrollbarLayoutState(
    thumbIsInAction: Boolean,
    settings: ScrollbarLayoutSettings,
): ScrollbarLayoutState {
    val settingsUpdated by rememberUpdatedState(settings)
    val thumbIsInActionUpdated by rememberUpdatedState(thumbIsInAction)

    val currentDurationMillis =
        remember {
            derivedStateOf {
                val reductionRatio: Int = if (thumbIsInActionUpdated) 4 else 1
                settingsUpdated.durationAnimationMillis / reductionRatio
            }
        }

    val hideAlpha =
        animateFloatAsState(
            targetValue = if (thumbIsInActionUpdated) 1f else 0f,
            animationSpec =
                tween(
                    durationMillis = currentDurationMillis.value,
                    delayMillis = if (thumbIsInActionUpdated) 0 else settingsUpdated.hideDelayMillis,
                    easing = settingsUpdated.hideEasingAnimation,
                ),
            label = "scrollbar alpha value",
        )

    return remember {
        ScrollbarLayoutState(
            hideAlpha = hideAlpha,
        )
    }
}

internal data class ScrollbarLayoutState(
    val hideAlpha: State<Float>,
)
