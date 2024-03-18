/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.img.editor.compose.foundation.lazy

import androidx.compose.foundation.checkScrollableContainerConstraints
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.offset
import ly.img.editor.compose.foundation.gestures.scrollable
import ly.img.editor.compose.foundation.lazy.layout.LazyLayout
import ly.img.editor.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import ly.img.editor.compose.foundation.lazy.layout.lazyLayoutSemantics
import ly.img.editor.compose.foundation.overscroll
import ly.img.editor.compose.foundation.rememberOverscrollEffect

@Composable
internal fun LazyList(
    /** Modifier to be applied for the inner layout */
    modifier: Modifier,
    /** State controlling the scroll position */
    state: LazyListState,
    /** The inner padding to be added for the whole content(not for each individual item) */
    contentPadding: PaddingValues,
    /** reverse the direction of scrolling and layout */
    reverseLayout: Boolean,
    /** The layout orientation of the list */
    isVertical: Boolean,
    /** fling behavior to be used for flinging */
    flingBehavior: FlingBehavior,
    /** Whether scrolling via the user gestures is allowed. */
    userScrollEnabled: Boolean,
    /** Number of items to layout before and after the visible items */
    beyondBoundsItemCount: Int = 0,
    /** The alignment to align items horizontally. Required when isVertical is true */
    horizontalAlignment: Alignment.Horizontal? = null,
    /** The vertical arrangement for items. Required when isVertical is true */
    verticalArrangement: Arrangement.Vertical? = null,
    /** The alignment to align items vertically. Required when isVertical is false */
    verticalAlignment: Alignment.Vertical? = null,
    /** The horizontal arrangement for items. Required when isVertical is false */
    horizontalArrangement: Arrangement.Horizontal? = null,
    /** The content of the list */
    content: LazyListScope.() -> Unit,
) {
    val overscrollEffect = rememberOverscrollEffect()
    val itemProvider = rememberLazyListItemProvider(state, content)
    val semanticState = rememberLazyListSemanticState(state, isVertical)
    val beyondBoundsInfo = remember { LazyListBeyondBoundsInfo() }
    val scope = rememberCoroutineScope()
    val placementAnimator =
        remember(state, isVertical) {
            LazyListItemPlacementAnimator(scope, isVertical)
        }
    state.placementAnimator = placementAnimator

    val measurePolicy =
        rememberLazyListMeasurePolicy(
            itemProvider,
            state,
            beyondBoundsInfo,
            contentPadding,
            reverseLayout,
            isVertical,
            beyondBoundsItemCount,
            horizontalAlignment,
            verticalAlignment,
            horizontalArrangement,
            verticalArrangement,
            placementAnimator,
        )

    ScrollPositionUpdater(itemProvider, state)

    val orientation = if (isVertical) Orientation.Vertical else Orientation.Horizontal
    LazyLayout(
        modifier =
            modifier
                .then(state.remeasurementModifier)
                .then(state.awaitLayoutModifier)
                .lazyLayoutSemantics(
                    itemProvider = itemProvider,
                    state = semanticState,
                    orientation = orientation,
                    userScrollEnabled = userScrollEnabled,
                    reverseScrolling = reverseLayout,
                )
                .clipScrollableContainer(orientation)
                .lazyListBeyondBoundsModifier(state, beyondBoundsInfo, reverseLayout, orientation)
                .overscroll(overscrollEffect)
                .scrollable(
                    orientation = orientation,
                    reverseDirection =
                        ScrollableDefaults.reverseDirection(
                            LocalLayoutDirection.current,
                            orientation,
                            reverseLayout,
                        ),
                    interactionSource = state.internalInteractionSource,
                    flingBehavior = flingBehavior,
                    state = state,
                    overscrollEffect = overscrollEffect,
                    enabled = userScrollEnabled,
                ),
        prefetchState = state.prefetchState,
        measurePolicy = measurePolicy,
        itemProvider = itemProvider,
    )
}

/** Extracted to minimize the recomposition scope */
@Composable
private fun ScrollPositionUpdater(
    itemProvider: LazyListItemProvider,
    state: LazyListState,
) {
    if (itemProvider.itemCount > 0) {
        state.updateScrollPositionIfTheFirstItemWasMoved(itemProvider)
    }
}

@Composable
private fun rememberLazyListMeasurePolicy(
    /** Items provider of the list. */
    itemProvider: LazyListItemProvider,
    /** The state of the list. */
    state: LazyListState,
    /** Keeps track of the number of items we measure and place that are beyond visible bounds. */
    beyondBoundsInfo: LazyListBeyondBoundsInfo,
    /** The inner padding to be added for the whole content(nor for each individual item) */
    contentPadding: PaddingValues,
    /** reverse the direction of scrolling and layout */
    reverseLayout: Boolean,
    /** The layout orientation of the list */
    isVertical: Boolean,
    /** Number of items to layout before and after the visible items */
    beyondBoundsItemCount: Int,
    /** The alignment to align items horizontally. Required when isVertical is true */
    horizontalAlignment: Alignment.Horizontal? = null,
    /** The alignment to align items vertically. Required when isVertical is false */
    verticalAlignment: Alignment.Vertical? = null,
    /** The horizontal arrangement for items. Required when isVertical is false */
    horizontalArrangement: Arrangement.Horizontal? = null,
    /** The vertical arrangement for items. Required when isVertical is true */
    verticalArrangement: Arrangement.Vertical? = null,
    /** Item placement animator. Should be notified with the measuring result */
    placementAnimator: LazyListItemPlacementAnimator,
) = remember<LazyLayoutMeasureScope.(Constraints) -> MeasureResult>(
    state,
    beyondBoundsInfo,
    contentPadding,
    reverseLayout,
    isVertical,
    horizontalAlignment,
    verticalAlignment,
    horizontalArrangement,
    verticalArrangement,
    placementAnimator,
) {
    { containerConstraints ->
        checkScrollableContainerConstraints(
            containerConstraints,
            if (isVertical) Orientation.Vertical else Orientation.Horizontal,
        )

        // resolve content paddings
        val startPadding =
            if (isVertical) {
                contentPadding.calculateLeftPadding(layoutDirection).roundToPx()
            } else {
                // in horizontal configuration, padding is reversed by placeRelative
                contentPadding.calculateStartPadding(layoutDirection).roundToPx()
            }

        val endPadding =
            if (isVertical) {
                contentPadding.calculateRightPadding(layoutDirection).roundToPx()
            } else {
                // in horizontal configuration, padding is reversed by placeRelative
                contentPadding.calculateEndPadding(layoutDirection).roundToPx()
            }
        val topPadding = contentPadding.calculateTopPadding().roundToPx()
        val bottomPadding = contentPadding.calculateBottomPadding().roundToPx()
        val totalVerticalPadding = topPadding + bottomPadding
        val totalHorizontalPadding = startPadding + endPadding
        val totalMainAxisPadding = if (isVertical) totalVerticalPadding else totalHorizontalPadding
        val beforeContentPadding =
            when {
                isVertical && !reverseLayout -> topPadding
                isVertical && reverseLayout -> bottomPadding
                !isVertical && !reverseLayout -> startPadding
                else -> endPadding // !isVertical && reverseLayout
            }
        val afterContentPadding = totalMainAxisPadding - beforeContentPadding
        val contentConstraints =
            containerConstraints.offset(-totalHorizontalPadding, -totalVerticalPadding)

        state.updateScrollPositionIfTheFirstItemWasMoved(itemProvider)

        // Update the state's cached Density
        state.density = this

        // this will update the scope used by the item composables
        itemProvider.itemScope.setMaxSize(
            width = contentConstraints.maxWidth,
            height = contentConstraints.maxHeight,
        )

        val spaceBetweenItemsDp =
            if (isVertical) {
                requireNotNull(verticalArrangement).spacing
            } else {
                requireNotNull(horizontalArrangement).spacing
            }
        val spaceBetweenItems = spaceBetweenItemsDp.roundToPx()

        val itemsCount = itemProvider.itemCount

        // can be negative if the content padding is larger than the max size from constraints
        val mainAxisAvailableSize =
            if (isVertical) {
                containerConstraints.maxHeight - totalVerticalPadding
            } else {
                containerConstraints.maxWidth - totalHorizontalPadding
            }
        val visualItemOffset =
            if (!reverseLayout || mainAxisAvailableSize > 0) {
                IntOffset(startPadding, topPadding)
            } else {
                // When layout is reversed and paddings together take >100% of the available space,
                // layout size is coerced to 0 when positioning. To take that space into account,
                // we offset start padding by negative space between paddings.
                IntOffset(
                    if (isVertical) startPadding else startPadding + mainAxisAvailableSize,
                    if (isVertical) topPadding + mainAxisAvailableSize else topPadding,
                )
            }

        val measuredItemProvider =
            LazyMeasuredItemProvider(
                contentConstraints,
                isVertical,
                itemProvider,
                this,
            ) { index, key, placeables ->
                // we add spaceBetweenItems as an extra spacing for all items apart from the last one so
                // the lazy list measuring logic will take it into account.
                val spacing = if (index.value == itemsCount - 1) 0 else spaceBetweenItems
                LazyMeasuredItem(
                    index = index.value,
                    placeables = placeables,
                    isVertical = isVertical,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    layoutDirection = layoutDirection,
                    reverseLayout = reverseLayout,
                    beforeContentPadding = beforeContentPadding,
                    afterContentPadding = afterContentPadding,
                    spacing = spacing,
                    visualOffset = visualItemOffset,
                    key = key,
                    placementAnimator = placementAnimator,
                )
            }
        state.premeasureConstraints = measuredItemProvider.childConstraints

        val firstVisibleItemIndex: DataIndex
        val firstVisibleScrollOffset: Int
        Snapshot.withoutReadObservation {
            firstVisibleItemIndex = DataIndex(state.firstVisibleItemIndex)
            firstVisibleScrollOffset = state.firstVisibleItemScrollOffset
        }

        measureLazyList(
            itemsCount = itemsCount,
            itemProvider = itemProvider,
            measuredItemProvider = measuredItemProvider,
            mainAxisAvailableSize = mainAxisAvailableSize,
            beforeContentPadding = beforeContentPadding,
            afterContentPadding = afterContentPadding,
            spaceBetweenItems = spaceBetweenItems,
            firstVisibleItemIndex = firstVisibleItemIndex,
            firstVisibleItemScrollOffset = firstVisibleScrollOffset,
            scrollToBeConsumed = state.scrollToBeConsumed,
            constraints = contentConstraints,
            isVertical = isVertical,
            headerIndexes = itemProvider.headerIndexes,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            reverseLayout = reverseLayout,
            density = this,
            placementAnimator = placementAnimator,
            beyondBoundsInfo = beyondBoundsInfo,
            beyondBoundsItemCount = beyondBoundsItemCount,
            pinnedItems = state.pinnedItems,
            layout = { width, height, placement ->
                layout(
                    containerConstraints.constrainWidth(width + totalHorizontalPadding),
                    containerConstraints.constrainHeight(height + totalVerticalPadding),
                    emptyMap(),
                    placement,
                )
            },
        ).also {
            state.applyMeasureResult(it)
        }
    }
}
