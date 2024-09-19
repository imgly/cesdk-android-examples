package ly.img.editor.core.ui.utils

import androidx.compose.animation.core.CubicBezierEasing

object Easing {
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
}
