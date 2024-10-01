package ly.img.editor.base.components.scrollbar.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
internal fun HorizontalScrollbarLayout(
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
                        .fillMaxWidth(thumbSizeNormalized)
                        .padding(
                            top = 0.dp,
                            bottom = settings.scrollbarPadding,
                        )
                        .alpha(state.hideAlpha.value)
                        .clip(settings.thumbShape)
                        .height(settings.thumbThickness)
                        .background(settings.thumbColor),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(settings.scrollbarPadding * 2 + settings.thumbThickness),
            )
        },
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }

            layout(constraints.maxWidth, constraints.maxHeight) {
                val placeableThumb = placeables[0]
                val placeableScrollbarArea = placeables[1]

                val offset = (constraints.maxWidth.toFloat() * thumbOffsetNormalized).toInt()

                placeableThumb.placeRelative(
                    y = constraints.maxHeight - placeableThumb.height,
                    x = offset,
                )
                placeableScrollbarArea.placeRelative(
                    y = constraints.maxHeight - placeableScrollbarArea.height,
                    x = 0,
                )
            }
        },
    )
}
