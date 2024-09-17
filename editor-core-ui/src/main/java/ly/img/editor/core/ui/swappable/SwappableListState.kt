package ly.img.editor.core.ui.swappable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.compose.foundation.lazy.LazyListState

@Composable
fun rememberSwappableListState(
    lazyListState: LazyListState,
    // The threshold for scrolling the list when dragging an item
    scrollThreshold: Dp = 60.dp,
    scrollSpeed: Float = 0.05f,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
): SwappableListState {
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { scrollThreshold.toPx() }

    val scope = rememberCoroutineScope()
    val onMoveState = rememberUpdatedState(onMove)
    val state =
        remember(
            scope,
            lazyListState,
            scrollThreshold,
            scrollSpeed,
        ) {
            SwappableListState(
                state = lazyListState,
                scope = scope,
                onMoveState = onMoveState,
                scrollThreshold = scrollThresholdPx,
                scrollSpeed = scrollSpeed,
            )
        }
    return state
}

internal fun LazyListLayoutInfo.getContentOffset(): Pair<Int, Int> = 0 to viewportEndOffset + afterContentPadding

class SwappableListState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMoveState: State<(from: LazyListItemInfo, to: LazyListItemInfo) -> Unit>,
    private val scrollThreshold: Float,
    scrollSpeed: Float,
) {
    private var draggingItemKey by mutableStateOf<Any?>(null)
    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggingItemKey }
    private val draggingItemIndex: Int?
        get() = draggingItemLayoutInfo?.index

    private var draggingItemDraggedDelta by mutableStateOf(0f)

    // from the beginning of the row
    private var draggingItemInitialOffset by mutableStateOf(0)

    private var draggingItemTargetIndex: Int? = null
    private var predictedDraggingItemOffset: Int? = null

    internal val draggingItemOffset: Float
        get() =
            draggingItemLayoutInfo?.let { item ->
                val offset =
                    if (item.index == draggingItemTargetIndex) {
                        predictedDraggingItemOffset = null
                        item.offset
                    } else {
                        predictedDraggingItemOffset ?: item.offset
                    }
                draggingItemInitialOffset + draggingItemDraggedDelta - offset
            } ?: 0f

    // the offset of the handle center from the top or left of the dragging item when dragging starts
    private var draggingItemHandleOffset = 0f

    internal val swappableKeys = HashSet<Any?>()

    private val scroller =
        Scroller(
            state,
            scope,
            scrollSpeed,
            this::swapItems,
        )

    internal var previousDraggingItemKey by mutableStateOf<Any?>(null)
        private set
    internal var previousDraggingItemOffset = Animatable(0f)
        private set

    internal suspend fun onDragStart(
        key: Any,
        handleOffset: Float,
    ) {
        state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            item.key == key
        }?.also {
            if (it.offset < 0) {
                // if item is not fully in view, scroll to it
                state.animateScrollBy(it.offset.toFloat(), spring())
            }

            draggingItemKey = key
            draggingItemInitialOffset = it.offset
            draggingItemHandleOffset = handleOffset
        }
    }

    internal fun onDragStop() {
        if (draggingItemIndex != null) {
            previousDraggingItemKey = draggingItemKey
            val startOffset = draggingItemOffset
            scope.launch {
                previousDraggingItemOffset.snapTo(startOffset)
                previousDraggingItemOffset.animateTo(
                    0f,
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = 1f,
                    ),
                )
                previousDraggingItemKey = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemKey = null
        draggingItemInitialOffset = 0
        scroller.stop()
        draggingItemTargetIndex = null
        predictedDraggingItemOffset = null
    }

    internal fun onDrag(offset: Float) {
        draggingItemDraggedDelta += offset

        val draggingItem = draggingItemLayoutInfo ?: return
        // draggingItem.offset is the distance of the start of the dragging item from the left of the list
        // state.layoutInfo.mainAxisItemSpacing is included in the offset
        // (e.g. the first item's offset is 0, despite being state.layoutInfo.mainAxisItemSpacing pixels from the left of the list)
        val startOffset = draggingItem.offset + draggingItemOffset
        val (contentStartOffset, contentEndOffset) = state.layoutInfo.getContentOffset()

        if (!scroller.isScrolling) {
            val endOffset = startOffset + draggingItem.size
            // find a target item to swap with
            val targetItem =
                state.layoutInfo.visibleItemsInfo.find { item ->
                    item.offsetMiddle in startOffset..endOffset && draggingItem.index != item.index && item.key in swappableKeys && item.offset >= contentStartOffset && item.offset + item.size <= contentEndOffset
                }
            if (targetItem != null) {
                swapItems(draggingItem, targetItem)
            }
        }

        // the actual distance from the left of the list to the top or left of the dragging item
        val startOffsetWithSpacing = startOffset + state.layoutInfo.mainAxisItemSpacing
        // the distance from the left of the list to the center of the dragging item handle
        val handleOffset = startOffsetWithSpacing + draggingItemHandleOffset

        // check if the handle center is in the scroll threshold
        val distanceFromStart = handleOffset - contentStartOffset
        val distanceFromEnd = contentEndOffset - handleOffset

        if (distanceFromStart < scrollThreshold) {
            scroller.start(
                { draggingItemLayoutInfo },
                Scroller.ScrollDirection.Backward,
                getScrollSpeedMultiplier(distanceFromStart),
            )
        } else if (distanceFromEnd < scrollThreshold) {
            scroller.start(
                { draggingItemLayoutInfo },
                Scroller.ScrollDirection.Forward,
                getScrollSpeedMultiplier(distanceFromEnd),
            )
        } else {
            scroller.stop()
        }
    }

    private fun swapItems(
        draggingItem: LazyListItemInfo,
        targetItem: LazyListItemInfo,
    ) {
        if (draggingItem.index == targetItem.index) return

        predictedDraggingItemOffset =
            if (targetItem.index > draggingItem.index) {
                targetItem.size + targetItem.offset - draggingItem.size
            } else {
                targetItem.offset
            }
        draggingItemTargetIndex = targetItem.index

        val scrollToIndex =
            if (targetItem.index == state.firstVisibleItemIndex) {
                draggingItem.index
            } else if (draggingItem.index == state.firstVisibleItemIndex) {
                targetItem.index
            } else {
                null
            }
        if (scrollToIndex != null) {
            scope.launch {
                // this is needed to neutralize automatic keeping the first item first.
                state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                onMoveState.value(draggingItem, targetItem)
            }
        } else {
            onMoveState.value(draggingItem, targetItem)
        }
    }

    internal fun isAnItemDragging(): State<Boolean> {
        return derivedStateOf {
            draggingItemKey != null
        }
    }

    internal fun isItemDragging(key: Any): State<Boolean> {
        return derivedStateOf {
            key == draggingItemKey
        }
    }

    private fun getScrollSpeedMultiplier(distance: Float): Float {
        // map distance in scrollThreshold..-scrollThreshold to 1..10
        return (1 - ((distance + scrollThreshold) / (scrollThreshold * 2)).coerceIn(0f, 1f)) * 10
    }

    private val LazyListItemInfo.offsetMiddle: Float
        get() = offset + size / 2f
}
