package ly.img.editor.base.components.scrollbar.controller

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun rememberScrollStateController(
    state: ScrollState,
    visibleLengthDp: Dp,
    thumbMinLength: Float,
    thumbMaxLength: Float,
    alwaysShowScrollBar: Boolean,
): ScrollStateController {
    val visibleLengthDpUpdated = rememberUpdatedState(visibleLengthDp)
    val thumbMinLengthUpdated = rememberUpdatedState(thumbMinLength)
    val thumbMaxLengthUpdated = rememberUpdatedState(thumbMaxLength)
    val alwaysShowScrollBarUpdated = rememberUpdatedState(alwaysShowScrollBar)

    val fullLengthDp =
        with(LocalDensity.current) {
            remember {
                derivedStateOf {
                    state.maxValue.toDp() + visibleLengthDpUpdated.value
                }
            }
        }

    val thumbSizeNormalizedReal =
        remember {
            derivedStateOf {
                if (fullLengthDp.value == 0.dp) {
                    1f
                } else {
                    val normalizedDp = visibleLengthDpUpdated.value / fullLengthDp.value
                    normalizedDp.coerceIn(0f, 1f)
                }
            }
        }

    val thumbSizeNormalized =
        remember {
            derivedStateOf {
                thumbSizeNormalizedReal.value.coerceIn(
                    thumbMinLengthUpdated.value,
                    thumbMaxLengthUpdated.value,
                )
            }
        }

    fun offsetCorrection(top: Float): Float {
        val topRealMax = 1f
        val topMax = (1f - thumbSizeNormalized.value).coerceIn(0f, 1f)
        return top * topMax / topRealMax
    }

    val thumbOffsetNormalized =
        remember {
            derivedStateOf {
                if (state.maxValue == 0) return@derivedStateOf 0f
                val normalized = state.value.toFloat() / state.maxValue.toFloat()
                offsetCorrection(normalized)
            }
        }

    val thumbIsInAction =
        remember {
            derivedStateOf {
                state.isScrollInProgress || alwaysShowScrollBarUpdated.value
            }
        }

    return remember {
        ScrollStateController(
            thumbSizeNormalized = thumbSizeNormalized,
            thumbOffsetNormalized = thumbOffsetNormalized,
            thumbIsInAction = thumbIsInAction,
        )
    }
}

internal class ScrollStateController(
    override val thumbSizeNormalized: State<Float>,
    override val thumbOffsetNormalized: State<Float>,
    override val thumbIsInAction: State<Boolean>,
) : StateController
