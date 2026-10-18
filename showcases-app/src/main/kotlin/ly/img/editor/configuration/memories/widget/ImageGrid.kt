package ly.img.editor.configuration.memories.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ly.img.editor.configuration.memories.model.ImageItem

@Composable
fun ImageGrid(
    modifier: Modifier = Modifier,
    images: List<ImageItem>,
    onImagesChanged: (List<ImageItem>) -> Unit,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 72.dp),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    selectedForDeletion: Set<Int> = emptySet(),
    onToggleSelection: ((Int) -> Unit)? = null,
) {
    // Empty state is owned by the caller (e.g. ImageSelectionScreen); render nothing here.
    if (images.isEmpty()) return

    val hapticFeedback = LocalHapticFeedback.current

    // Hand-written drag-reorder engine (no third-party reorder library). The slop gate inside
    // ReorderableGridState is what keeps the list from snapping when a top/bottom cell is picked up.
    val dragState = rememberReorderableGridState(lazyGridState) { from, to ->
        val newImages = images.toMutableList()
        newImages.add(to, newImages.removeAt(from))
        onImagesChanged(newImages)

        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Continuous edge auto-scroll while dragging (only engages once the slop gate is satisfied).
    LaunchedEffect(dragState.isDragging) {
        if (!dragState.isDragging) return@LaunchedEffect
        while (isActive) {
            val speed = dragState.autoScrollSpeed
            if (speed != 0f) lazyGridState.scrollBy(speed)
            delay(16)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = lazyGridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxHeight()
            .pointerInput(dragState) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        dragState.onDragStart(offset)
                        if (dragState.isDragging) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragState.onDrag(dragAmount)
                    },
                    onDragEnd = dragState::onDragInterrupted,
                    onDragCancel = dragState::onDragInterrupted,
                )
            },
    ) {
        items(images.size, key = { images[it].id }) { index ->
            val isDragging = index == dragState.draggingItemIndex
            val elevation by animateDpAsState(
                targetValue = if (isDragging) 8.dp else 0.dp,
                label = "elevation",
            )

            val imageId = images[index].id
            val itemModifier = if (isDragging) {
                // Dragged cell is placed manually; raise it and translate it under the finger.
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationX = dragState.draggingItemOffset.x
                        translationY = dragState.draggingItemOffset.y
                    }
            } else {
                // Non-dragged cells keep their default placement. A sliding reorder animation would
                // need Modifier.animateItem() (Compose 1.7+); this module targets the older BOM.
                Modifier
            }

            Box(itemModifier) {
                DraggableImageItem(
                    image = images[index],
                    elevation = elevation,
                    index = index,
                    isSelectedForDeletion = imageId in selectedForDeletion,
                    onToggleSelection = onToggleSelection?.let { toggle -> { toggle(imageId) } },
                )
            }
        }
    }
}
