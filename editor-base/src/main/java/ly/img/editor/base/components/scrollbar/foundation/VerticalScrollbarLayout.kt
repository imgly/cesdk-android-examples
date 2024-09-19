package ly.img.editor.base.components.scrollbar.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
internal fun VerticalScrollbarLayout(
    thumbSizeNormalized: Float,
    thumbOffsetNormalized: Float,
    thumbIsInAction: Boolean,
    settings: ScrollbarLayoutSettings,
    modifier: Modifier = Modifier,
) {
    val state =
        rememberScrollbarLayoutState(
            thumbIsInAction = thumbIsInAction,
            settings = settings,
        )

    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight(thumbSizeNormalized)
                        .padding(
                            start = 0.dp,
                            end = settings.scrollbarPadding,
                        )
                        .alpha(state.hideAlpha.value)
                        .clip(settings.thumbShape)
                        .width(settings.thumbThickness)
                        .background(settings.thumbColor),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(settings.scrollbarPadding * 2 + settings.thumbThickness),
            )
        },
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }

            layout(constraints.maxWidth, constraints.maxHeight) {
                val placeableThumb = placeables[0]
                val placeableScrollbarArea = placeables[1]

                val offset = (constraints.maxHeight.toFloat() * thumbOffsetNormalized).toInt()

                placeableThumb.placeRelative(
                    x = constraints.maxWidth - placeableThumb.width,
                    y = offset,
                )

                placeableScrollbarArea.placeRelative(
                    x = constraints.maxWidth - placeableScrollbarArea.width,
                    y = 0,
                )
            }
        },
    )
}
