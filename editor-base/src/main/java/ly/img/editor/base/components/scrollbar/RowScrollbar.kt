package ly.img.editor.base.components.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ly.img.editor.base.components.scrollbar.controller.rememberScrollStateController
import ly.img.editor.base.components.scrollbar.generic.ElementScrollbar

@Composable
fun RowScrollbar(
    state: ScrollState,
    modifier: Modifier = Modifier,
    settings: ScrollbarSettings = ScrollbarSettings.Default,
    visibleLengthDp: Dp,
) {
    val stateController =
        rememberScrollStateController(
            state = state,
            visibleLengthDp = visibleLengthDp,
            thumbMinLength = settings.thumbMinLength,
            thumbMaxLength = settings.thumbMaxLength,
            alwaysShowScrollBar = settings.alwaysShowScrollbar,
        )

    ElementScrollbar(
        orientation = Orientation.Horizontal,
        stateController = stateController,
        modifier = modifier,
        settings = settings,
    )
}
