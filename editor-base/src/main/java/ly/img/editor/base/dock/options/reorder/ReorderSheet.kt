package ly.img.editor.base.dock.options.reorder

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.compose.foundation.lazy.LazyRow
import ly.img.editor.compose.foundation.lazy.rememberLazyListState
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.swappable.SwappableItem
import ly.img.editor.core.ui.swappable.rememberSwappableListState
import ly.img.engine.DesignBlock
import ly.img.editor.core.R as CoreR

@Composable
fun ReorderSheet(
    timelineState: TimelineState,
    onEvent: (EditorEvent) -> Unit,
) {
    Column {
        SheetHeader(
            title = stringResource(id = CoreR.string.ly_img_editor_reorder),
            onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            val lazyListState = rememberLazyListState()
            val backgroundClips = timelineState.dataSource.backgroundTrack.clips
            val clipItems =
                remember(backgroundClips) {
                    backgroundClips.toMutableStateList()
                }
            val swappableListState =
                rememberSwappableListState(lazyListState = lazyListState) { from, to ->
                    clipItems.apply {
                        add(to.index - 1, removeAt(from.index - 1))
                    }
                }
            var dragStartedKey: Int? = remember { null }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                item(key = -1) {
                    // There are some placement animation issues with the first item in a LazyRow
                    // Having a fake first item helps in avoiding that situation
                    SwappableItem(swappableListState, key = -1, enabled = false) {
                    }
                }
                items(clipItems.size, key = { clipItems[it].id }) { index ->
                    SwappableItem(swappableListState, key = clipItems[index].id) { isDragging ->
                        Row(
                            Modifier.longPressDraggable(
                                onDragStarted = {
                                    dragStartedKey = it as Int
                                },
                                onDragStopped = {
                                    // Now that the drag has stopped, we need to check which clip was reordered
                                    // We do it here instead of onMove() because onMove() is called multiple times
                                    // and we only want to reorder when the drag has ended.
                                    val newIndex = clipItems.indexOfFirst { it.id == dragStartedKey }
                                    val oldIndex = backgroundClips.indexOfFirst { it.id == dragStartedKey }
                                    if (newIndex != oldIndex) {
                                        onEvent(BlockEvent.OnReorder(dragStartedKey as DesignBlock, newIndex))
                                    }
                                    dragStartedKey = null
                                },
                            ),
                        ) {
                            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                            val scale by animateFloatAsState(if (isDragging) 1.1f else 1f)

                            ReorderingThumbnailsView(
                                clip = clipItems[index],
                                timelineState = timelineState,
                                elevation = elevation,
                                modifier =
                                    Modifier
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        },
                            )

                            // Adding spacing manually because of the fake first item
                            if (index < clipItems.size) {
                                Spacer(Modifier.width(12.dp))
                            }
                        }
                    }
                }
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.ly_img_editor_reorder_label),
                style = MaterialTheme.typography.labelSmall,
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

class ReorderBottomSheetContent(
    override val type: SheetType,
    val timelineState: TimelineState,
) : BottomSheetContent
