package ly.img.editor.base.components.scrollbar.foundation

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Stable
internal data class ScrollbarLayoutSettings(
    val scrollbarPadding: Dp,
    val thumbShape: Shape,
    val thumbThickness: Dp,
    val thumbColor: Color,
    val hideDelayMillis: Int,
    val hideEasingAnimation: Easing,
    val durationAnimationMillis: Int,
)
