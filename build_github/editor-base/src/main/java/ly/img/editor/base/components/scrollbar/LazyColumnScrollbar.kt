package ly.img.editor.base.components.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ly.img.editor.base.components.scrollbar.controller.rememberLazyListStateController
import ly.img.editor.base.components.scrollbar.generic.ElementScrollbar

@Composable
fun LazyColumnScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier,
    settings: ScrollbarSettings = ScrollbarSettings.Default,
) {
    val controller =
        rememberLazyListStateController(
            state = state,
            thumbMinLength = settings.thumbMinLength,
            thumbMaxLength = settings.thumbMaxLength,
            alwaysShowScrollBar = settings.alwaysShowScrollbar,
        )

    ElementScrollbar(
        orientation = Orientation.Vertical,
        stateController = controller,
        modifier = modifier,
        settings = settings,
    )
}
