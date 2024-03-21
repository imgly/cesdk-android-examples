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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import ly.img.editor.compose.foundation.lazy.layout.findIndexByKey

/**
 * Contains the current scroll position represented by the first visible item index and the first
 * visible item scroll offset.
 */
internal class LazyListScrollPosition(
    initialIndex: Int = 0,
    initialScrollOffset: Int = 0,
) {
    var index by mutableStateOf(DataIndex(initialIndex))

    var scrollOffset by mutableStateOf(initialScrollOffset)
        private set

    private var hadFirstNotEmptyLayout = false

    /** The last know key of the item at [index] position. */
    private var lastKnownFirstItemKey: Any? = null

    /**
     * Updates the current scroll position based on the results of the last measurement.
     */
    fun updateFromMeasureResult(measureResult: LazyListMeasureResult) {
        lastKnownFirstItemKey = measureResult.firstVisibleItem?.key
        // we ignore the index and offset from measureResult until we get at least one
        // measurement with real items. otherwise the initial index and scroll passed to the
        // state would be lost and overridden with zeros.
        if (hadFirstNotEmptyLayout || measureResult.totalItemsCount > 0) {
            hadFirstNotEmptyLayout = true
            val scrollOffset = measureResult.firstVisibleItemScrollOffset
            check(scrollOffset >= 0f) { "scrollOffset should be non-negative ($scrollOffset)" }

            Snapshot.withoutReadObservation {
                update(
                    DataIndex(measureResult.firstVisibleItem?.index ?: 0),
                    scrollOffset,
                )
            }
        }
    }

    /**
     * Updates the scroll position - the passed values will be used as a start position for
     * composing the items during the next measure pass and will be updated by the real
     * position calculated during the measurement. This means that there is no guarantee that
     * exactly this index and offset will be applied as it is possible that:
     * a) there will be no item at this index in reality
     * b) item at this index will be smaller than the asked scrollOffset, which means we would
     * switch to the next item
     * c) there will be not enough items to fill the viewport after the requested index, so we
     * would have to compose few elements before the asked index, changing the first visible item.
     */
    fun requestPosition(
        index: DataIndex,
        scrollOffset: Int,
    ) {
        update(index, scrollOffset)
        // clear the stored key as we have a direct request to scroll to [index] position and the
        // next [checkIfFirstVisibleItemWasMoved] shouldn't override this.
        lastKnownFirstItemKey = null
    }

    /**
     * In addition to keeping the first visible item index we also store the key of this item.
     * When the user provided custom keys for the items this mechanism allows us to detect when
     * there were items added or removed before our current first visible item and keep this item
     * as the first visible one even given that its index has been changed.
     */
    fun updateScrollPositionIfTheFirstItemWasMoved(itemProvider: LazyListItemProvider) {
        Snapshot.withoutReadObservation {
            update(
                DataIndex(itemProvider.findIndexByKey(lastKnownFirstItemKey, index.value)),
                scrollOffset,
            )
        }
    }

    private fun update(
        index: DataIndex,
        scrollOffset: Int,
    ) {
        require(index.value >= 0f) { "Index should be non-negative (${index.value})" }
        if (index != this.index) {
            this.index = index
        }
        if (scrollOffset != this.scrollOffset) {
            this.scrollOffset = scrollOffset
        }
    }
}
