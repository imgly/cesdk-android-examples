package ly.img.editor.base.timeline.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

enum class OffsetDirection {
    Left,
    Right,
}

fun Modifier.offsetByWidth(offsetDirection: OffsetDirection) =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            val offset = placeable.width / 2
            val directionedOffset = if (offsetDirection == OffsetDirection.Left) -offset else offset
            placeable.placeRelative(directionedOffset, 0)
        }
    }
