package ly.img.editor.base.components.scrollbar.generic

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ly.img.editor.base.components.scrollbar.ScrollbarSettings
import ly.img.editor.base.components.scrollbar.controller.StateController
import ly.img.editor.base.components.scrollbar.foundation.ScrollbarLayoutSettings

@Composable
internal fun ElementScrollbar(
    orientation: Orientation,
    stateController: StateController,
    modifier: Modifier,
    settings: ScrollbarSettings,
) {
    val layoutSettings =
        remember(settings) {
            ScrollbarLayoutSettings(
                scrollbarPadding = settings.scrollbarPadding,
                thumbShape = settings.thumbShape,
                thumbThickness = settings.thumbThickness,
                thumbColor = settings.thumbColor,
                hideEasingAnimation = settings.hideEasingAnimation,
                hideDelayMillis = settings.hideDelayMillis,
                durationAnimationMillis = settings.durationAnimationMillis,
            )
        }

    Box(modifier) {
        ScrollbarLayout(
            orientation = orientation,
            thumbSizeNormalized = stateController.thumbSizeNormalized.value,
            thumbOffsetNormalized = stateController.thumbOffsetNormalized.value,
            thumbIsInAction = stateController.thumbIsInAction.value,
            settings = layoutSettings,
        )
    }
}
