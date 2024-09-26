package ly.img.editor.core.ui.swappable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.editor.compose.foundation.lazy.LazyListState

internal class Scroller(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val scrollSpeed: Float,
    private val swapItems: (draggingItem: LazyListItemInfo, targetItem: LazyListItemInfo) -> Unit,
) {
    enum class ScrollDirection {
        Backward,
        Forward,
    }

    private data class ScrollJobInfo(
        val direction: ScrollDirection,
        val speedMultiplier: Float,
    )

    private var scrollJobInfo: ScrollJobInfo? = null
    private var scrollJob: Job? = null
    val isScrolling: Boolean
        get() = scrollJobInfo != null

    fun start(
        draggingItemProvider: () -> LazyListItemInfo?,
        direction: ScrollDirection,
        speedMultiplier: Float = 1f,
    ) {
        val scrollJobInfo = ScrollJobInfo(direction, speedMultiplier)

        if (this.scrollJobInfo == scrollJobInfo) return

        val viewportSize = state.layoutInfo.viewportSize.width
        val multipliedScrollOffset = viewportSize * scrollSpeed * speedMultiplier

        scrollJob?.cancel()
        this.scrollJobInfo = null

        if (!canScroll(direction)) return

        this.scrollJobInfo = scrollJobInfo
        scrollJob =
            scope.launch {
                while (true) {
                    try {
                        if (!canScroll(direction)) break

                        val duration = 100L
                        val diff =
                            when (direction) {
                                ScrollDirection.Backward -> -multipliedScrollOffset
                                ScrollDirection.Forward -> multipliedScrollOffset
                            }
                        launch {
                            state.animateScrollBy(
                                diff,
                                tween(durationMillis = duration.toInt(), easing = LinearEasing),
                            )
                        }

                        launch {
                            // keep dragging item in visible area to prevent it from disappearing
                            swapDraggingItemToEndIfNecessary(draggingItemProvider, direction)
                        }

                        delay(duration)
                    } catch (e: Exception) {
                        break
                    }
                }
            }
    }

    private fun canScroll(direction: ScrollDirection): Boolean {
        return when (direction) {
            ScrollDirection.Backward -> state.canScrollBackward
            ScrollDirection.Forward -> state.canScrollForward
        }
    }

    private fun swapDraggingItemToEndIfNecessary(
        draggingItemProvider: () -> LazyListItemInfo?,
        direction: ScrollDirection,
    ) {
        val draggingItem = draggingItemProvider() ?: return
        val itemsInContentArea = state.layoutInfo.getItemsInContentArea()
        val draggingItemIsAtTheEnd =
            when (direction) {
                ScrollDirection.Backward -> itemsInContentArea.firstOrNull()?.index?.let { draggingItem.index < it }
                ScrollDirection.Forward -> itemsInContentArea.lastOrNull()?.index?.let { draggingItem.index > it }
            } ?: false

        if (draggingItemIsAtTheEnd) return

        val targetItem =
            itemsInContentArea.let {
                when (direction) {
                    ScrollDirection.Backward -> it.firstOrNull()
                    ScrollDirection.Forward -> it.lastOrNull()
                }
            }
        if (targetItem != null && targetItem.index != draggingItem.index) {
            swapItems(draggingItem, targetItem)
        }
    }

    fun stop() {
        scrollJob?.cancel()
        scrollJobInfo = null
    }

    private fun LazyListLayoutInfo.getItemsInContentArea(): List<LazyListItemInfo> {
        val (contentStartOffset, contentEndOffset) = getContentOffset()
        return visibleItemsInfo.filter { item ->
            item.offset >= contentStartOffset && item.offset + item.size <= contentEndOffset
        }
    }
}
