package ly.img.editor.core.ui.swappable

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import kotlinx.coroutines.launch

interface SwappableItemScope {
    fun Modifier.longPressDraggable(
        enabled: Boolean = true,
        onDragStarted: (key: Any) -> Unit = {},
        onDragStopped: () -> Unit = {},
    ): Modifier
}

internal class SwappableItemScopeImpl(
    private val swappableListState: SwappableListState,
    private val key: Any,
    private val itemPositionProvider: () -> Float,
) : SwappableItemScope {
    override fun Modifier.longPressDraggable(
        enabled: Boolean,
        onDragStarted: (key: Any) -> Unit,
        onDragStopped: () -> Unit,
    ): Modifier =
        composed {
            var handleOffset = remember { 0f }
            var handleSize = remember { 0 }

            val coroutineScope = rememberCoroutineScope()

            var dragStarted = remember { false }

            val draggingEnabled =
                enabled && (swappableListState.isItemDragging(key).value || !swappableListState.isAnItemDragging().value)

            onGloballyPositioned {
                handleOffset = it.positionInRoot().x
                handleSize = it.size.width
            }.pointerInput(draggingEnabled) {
                if (draggingEnabled) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            dragStarted = true

                            coroutineScope.launch {
                                val handleOffsetRelativeToItem = handleOffset - itemPositionProvider()
                                val handleCenter = handleOffsetRelativeToItem + handleSize / 2f
                                swappableListState.onDragStart(key, handleCenter)
                            }

                            onDragStarted(key)
                        },
                        onDragEnd = {
                            if (dragStarted) {
                                swappableListState.onDragStop()
                                onDragStopped()
                            }

                            dragStarted = false
                        },
                        onDragCancel = {
                            if (dragStarted) {
                                swappableListState.onDragStop()
                                onDragStopped()
                            }

                            dragStarted = false
                        },
                        onDrag = { _, dragAmount ->
                            swappableListState.onDrag(
                                offset = dragAmount.x,
                            )
                        },
                    )
                }
            }
        }
}
