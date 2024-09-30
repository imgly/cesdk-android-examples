package ly.img.editor.base.components.scrollbar

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @param thumbMinLength Thumb minimum length proportional to total scrollbar's length (eg: 0.1 -> 10% of total)
 */
@Stable
data class ScrollbarSettings(
    val enabled: Boolean = Default.enabled,
    val alwaysShowScrollbar: Boolean = Default.alwaysShowScrollbar,
    val scrollbarPadding: Dp = Default.scrollbarPadding,
    val thumbThickness: Dp = Default.thumbThickness,
    val thumbShape: Shape = Default.thumbShape,
    val thumbMinLength: Float = Default.thumbMinLength,
    val thumbMaxLength: Float = Default.thumbMaxLength,
    val thumbColor: Color = Default.thumbColor,
    val hideDelayMillis: Int = Default.hideDelayMillis,
    val hideEasingAnimation: Easing = Default.hideEasingAnimation,
    val durationAnimationMillis: Int = Default.durationAnimationMillis,
) {
    init {
        require(thumbMinLength <= thumbMaxLength) {
            "thumbMinLength ($thumbMinLength) must be less or equal to thumbMaxLength ($thumbMaxLength)"
        }
    }

    companion object {
        val Default =
            ScrollbarSettings(
                enabled = true,
                alwaysShowScrollbar = false,
                thumbThickness = 6.dp,
                scrollbarPadding = 8.dp,
                thumbMinLength = 0.1f,
                thumbMaxLength = 1.0f,
                thumbColor = Color(0xFF2A59B6),
                thumbShape = CircleShape,
                hideDelayMillis = 400,
                hideEasingAnimation = FastOutSlowInEasing,
                durationAnimationMillis = 500,
            )
    }
}
