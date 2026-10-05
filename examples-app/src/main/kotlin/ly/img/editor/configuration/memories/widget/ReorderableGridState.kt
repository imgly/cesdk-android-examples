package ly.img.editor.configuration.memories.widget

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * Holds the live drag state. Kept outside the composable so the gesture callbacks can mutate it
 * without re-running composition until something observable actually changes.
 *
 * Consumed by [ImageGrid] via [rememberReorderableGridState]: it drives the drag-and-drop
 * reordering of the media grid built on Compose Foundation / AndroidX APIs (no third-party
 * reorder libraries).
 */
class ReorderableGridState internal constructor(
    private val gridState: LazyGridState,
    private val scrollThresholdPx: Float,
    private val maxScrollPerFramePx: Float,
    private val scrollActivationSlopPx: Float,
    private val onMove: (Int, Int) -> Unit,
) {
    // Current index of the dragged item. Changes as it swaps past neighbours.
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    val isDragging: Boolean get() = draggingItemIndex != null

    // Auto-scroll speed in px/frame, set during onDrag and consumed by the LaunchedEffect.
    var autoScrollSpeed by mutableFloatStateOf(0f)
        private set

    // Layout offset of the item at pick-up (viewport coords) plus the finger's accumulated travel.
    private var initialOffset by mutableStateOf(Offset.Zero)
    private var draggedDelta by mutableStateOf(Offset.Zero)

    private val draggingItemLayoutInfo: LazyGridItemInfo?
        get() = gridState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == draggingItemIndex }

    /**
     * The translation to apply to the dragged cell's [graphicsLayer]. Drawing the cell at
     * `currentLayoutOffset + draggingItemOffset` always lands it at `initialOffset + draggedDelta`
     * — i.e. exactly under the finger — no matter where its layout slot currently sits. That is the
     * re-anchor: when a swap changes the item's slot (or auto-scroll shifts the layout), this value
     * compensates automatically instead of snapping back.
     */
    val draggingItemOffset: Offset
        get() = draggingItemLayoutInfo?.let { item ->
            initialOffset + draggedDelta - item.offset.toOffset()
        } ?: Offset.Zero

    fun onDragStart(offset: Offset) {
        // detectDragGesturesAfterLongPress reports the touch relative to the grid node's top-left
        // (the viewport, which includes the content-padding region). visibleItemsInfo offsets live in
        // the item coordinate space whose origin sits at viewportStartOffset (= -beforeContentPadding
        // on the main axis). Shift the pointer onto that space before hit-testing, or a large top
        // content padding makes us pick a cell below the one actually touched. Vertical grid → only
        // the main (y) axis is affected.
        val pointer = offset.copy(y = offset.y + gridState.layoutInfo.viewportStartOffset)
        gridState.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> pointer.inBounds(item) }
            ?.also {
                draggingItemIndex = it.index
                initialOffset = it.offset.toOffset()
                draggedDelta = Offset.Zero
            }
    }

    fun onDrag(dragAmount: Offset) {
        draggedDelta += dragAmount
        val dragged = draggingItemLayoutInfo ?: return
        val from = draggingItemIndex ?: return

        // Visual top-left then centre of the dragged cell, in viewport coords.
        val topLeft = dragged.offset.toOffset() + draggingItemOffset
        val center = topLeft + Offset(dragged.size.width / 2f, dragged.size.height / 2f)

        // Hit-test the centre against every other visible cell; swap into the first that contains it.
        val target = gridState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            item.index != from && center.inBounds(item)
        }
        if (target != null) {
            onMove(from, target.index)
            // Re-point to the new index; draggingItemOffset re-anchors so the cell stays put.
            draggingItemIndex = target.index
        }

        autoScrollSpeed = computeAutoScroll(topLeft.y, dragged.size.height.toFloat())
    }

    fun onDragInterrupted() {
        draggingItemIndex = null
        initialOffset = Offset.Zero
        draggedDelta = Offset.Zero
        autoScrollSpeed = 0f
    }

    /**
     * Positive = scroll down, negative = scroll up, 0 = no auto-scroll.
     *
     * Being inside an edge zone is not enough: a cell picked up while already sitting in the top (or
     * bottom) zone must not yank the list the instant the gesture moves. So we also require the user
     * to have dragged *toward* that edge past [scrollActivationSlopPx]. [draggedDelta] is cumulative
     * finger travel since pick-up (auto-scroll does not change it), so a horizontal-only drag keeps
     * `draggedDelta.y ≈ 0` and never triggers a scroll.
     */
    private fun computeAutoScroll(
        top: Float,
        height: Float,
    ): Float {
        val bottom = top + height
        val viewStart = gridState.layoutInfo.viewportStartOffset.toFloat()
        val viewEnd = gridState.layoutInfo.viewportEndOffset.toFloat()
        return when {
            bottom > viewEnd - scrollThresholdPx && draggedDelta.y > scrollActivationSlopPx ->
                (bottom - (viewEnd - scrollThresholdPx)).coerceAtMost(maxScrollPerFramePx)
            top < viewStart + scrollThresholdPx && draggedDelta.y < -scrollActivationSlopPx ->
                (top - (viewStart + scrollThresholdPx)).coerceAtLeast(-maxScrollPerFramePx)
            else -> 0f
        }
    }

    private fun Offset.inBounds(item: LazyGridItemInfo): Boolean = x >= item.offset.x &&
        x <= item.offset.x + item.size.width &&
        y >= item.offset.y &&
        y <= item.offset.y + item.size.height

    private fun IntOffset.toOffset() = Offset(x.toFloat(), y.toFloat())
}

@Composable
fun rememberReorderableGridState(
    gridState: LazyGridState,
    onMove: (from: Int, to: Int) -> Unit,
): ReorderableGridState {
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { 72.dp.toPx() }
    val maxScrollPerFramePx = with(density) { 16.dp.toPx() }
    // How far the user must drag toward an edge before auto-scroll kicks in (prevents an instant
    // jump when a cell is picked up already sitting in an edge zone).
    val scrollActivationSlopPx = with(density) { 24.dp.toPx() }
    // Always route to the latest onMove even though the state object itself is remembered.
    val onMoveState: State<(Int, Int) -> Unit> = rememberUpdatedState(onMove)
    return remember(gridState, scrollThresholdPx, maxScrollPerFramePx, scrollActivationSlopPx) {
        ReorderableGridState(
            gridState = gridState,
            scrollThresholdPx = scrollThresholdPx,
            maxScrollPerFramePx = maxScrollPerFramePx,
            scrollActivationSlopPx = scrollActivationSlopPx,
            onMove = { from, to -> onMoveState.value(from, to) },
        )
    }
}

/*
 * Edge cases this minimal version intentionally does not cover:
 *
 *  - Auto-scroll alone does not re-run hit-testing. Swaps happen in onDrag, which only fires on
 *    finger movement. If you hold the finger still in an edge zone the grid scrolls but no new
 *    swaps occur until you nudge the finger. To fix, also call the swap logic from the scroll loop.
 *
 *  - GridCells.Adaptive / variable-span items are untested. Hit-testing assumes uniform cells laid
 *    out left-to-right, top-to-bottom; spanned items can produce surprising targets.
 *
 *  - No drop animation. When released, the cell jumps from its dragged position to its final slot.
 *    Animate draggingItemOffset toward zero on release for a settle effect.
 *
 *  - No haptics on pick-up — add a LocalHapticFeedback.performHapticFeedback in onDragStart.
 *
 *  - A null key falls back to position-based identity, which breaks animateItem() move animations
 *    and can mis-track the dragged item across data changes. Provide a stable key in real use.
 */
