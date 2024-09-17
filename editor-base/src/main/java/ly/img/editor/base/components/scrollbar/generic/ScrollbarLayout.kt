package ly.img.editor.base.components.scrollbar.generic

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ly.img.editor.base.components.scrollbar.foundation.HorizontalScrollbarLayout
import ly.img.editor.base.components.scrollbar.foundation.ScrollbarLayoutSettings
import ly.img.editor.base.components.scrollbar.foundation.VerticalScrollbarLayout

@Composable
internal fun ScrollbarLayout(
    orientation: Orientation,
    thumbSizeNormalized: Float,
    thumbOffsetNormalized: Float,
    thumbIsInAction: Boolean,
    settings: ScrollbarLayoutSettings,
    modifier: Modifier = Modifier,
) {
    when (orientation) {
        Orientation.Vertical ->
            VerticalScrollbarLayout(
                thumbSizeNormalized = thumbSizeNormalized,
                thumbOffsetNormalized = thumbOffsetNormalized,
                thumbIsInAction = thumbIsInAction,
                settings = settings,
                modifier = modifier,
            )

        Orientation.Horizontal ->
            HorizontalScrollbarLayout(
                thumbSizeNormalized = thumbSizeNormalized,
                thumbOffsetNormalized = thumbOffsetNormalized,
                thumbIsInAction = thumbIsInAction,
                settings = settings,
                modifier = modifier,
            )
    }
}
