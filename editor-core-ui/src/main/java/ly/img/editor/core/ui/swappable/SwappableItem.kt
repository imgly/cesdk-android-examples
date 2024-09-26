package ly.img.editor.core.ui.swappable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.zIndex
import ly.img.editor.compose.foundation.lazy.LazyItemScope

@Composable
fun LazyItemScope.SwappableItem(
    swappableListState: SwappableListState,
    key: Any,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable SwappableItemScope.(isDragging: Boolean) -> Unit,
) {
    val dragging by swappableListState.isItemDragging(key)
    var itemPosition = remember { 0f }
    val draggingModifier =
        if (dragging) {
            Modifier
                .zIndex(1f)
                .then(
                    Modifier.graphicsLayer {
                        translationX = swappableListState.draggingItemOffset
                    },
                )
        } else if (key == swappableListState.previousDraggingItemKey) {
            Modifier
                .zIndex(1f)
                .then(
                    Modifier.graphicsLayer {
                        translationX = swappableListState.previousDraggingItemOffset.value
                    },
                )
        } else {
            Modifier.animateItemPlacement()
        }.onGloballyPositioned {
            itemPosition = it.positionInRoot().x
        }

    Column(modifier = modifier.then(draggingModifier)) {
        SwappableItemScopeImpl(
            swappableListState,
            key,
        ) {
            itemPosition
        }.content(dragging)
    }

    LaunchedEffect(enabled) {
        if (enabled) {
            swappableListState.swappableKeys.add(key)
        } else {
            swappableListState.swappableKeys.remove(key)
        }
    }
}
