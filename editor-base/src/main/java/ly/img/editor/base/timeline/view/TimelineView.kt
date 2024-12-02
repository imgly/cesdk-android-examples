package ly.img.editor.base.timeline.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.filterNotNull
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.utils.Easing

@Composable
fun TimelineView(
    modifier: Modifier = Modifier,
    timelineState: TimelineState,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onEvent: (EditorEvent) -> Unit,
) {
    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface1),
    ) {
        PlayerHeader(
            timelineState = timelineState,
            expanded = expanded,
            onToggleExpand = onToggleExpand,
        )

        val verticalScrollState = rememberLazyListState(initialFirstVisibleItemIndex = Int.MAX_VALUE)

        LaunchedEffect(Unit) {
            snapshotFlow { timelineState.selectedClip }
                .filterNotNull()
                .collect { clip ->
                    val index = timelineState.dataSource.indexOf(clip)
                    if (index == -1) return@collect
                    val isClipAlreadyVisible =
                        verticalScrollState.layoutInfo.visibleItemsInfo.find { it.index == index }?.let { itemInfo ->
                            itemInfo.offset >= verticalScrollState.layoutInfo.viewportStartOffset &&
                                (itemInfo.offset + itemInfo.size) <= verticalScrollState.layoutInfo.viewportEndOffset
                        } ?: false
                    if (!isClipAlreadyVisible) {
                        verticalScrollState.animateScrollToItem(index)
                    }
                }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = enterTransition(),
            exit = exitTransition(),
        ) {
            TimelineContentView(
                timelineState = timelineState,
                verticalScrollState = verticalScrollState,
                onEvent = onEvent,
            )
        }
    }
}

private fun enterTransition() =
    fadeIn(tween(durationMillis = 500, easing = Easing.EmphasizedDecelerate)) +
        expandVertically(tween(durationMillis = 500, easing = Easing.EmphasizedDecelerate))

private fun exitTransition() =
    fadeOut(tween(durationMillis = 250, easing = Easing.EmphasizedDecelerate)) +
        shrinkVertically(
            tween(durationMillis = 350, easing = Easing.EmphasizedDecelerate),
        )
