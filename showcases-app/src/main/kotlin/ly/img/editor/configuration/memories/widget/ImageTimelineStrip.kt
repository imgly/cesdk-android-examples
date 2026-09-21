@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package ly.img.editor.configuration.memories.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.iconPack.PlayArrow
import ly.img.editor.configuration.memories.iconPack.Title
import ly.img.editor.configuration.memories.model.TimelineImage
import ly.img.editor.core.iconpack.IconPack
import ly.img.engine.Engine
import kotlin.math.absoluteValue

private val ITEM_HEIGHT = 60.dp
private val ITEM_WIDTH = ITEM_HEIGHT * 9f / 16f // 9:16 portrait, not square

/**
 * A single thumbnail in the timeline pager. [url] is null for the title item (a "T" icon).
 * [seekTarget] is where tapping/scrolling seeks to. [currentThreshold] is the playback time at
 * which this item becomes the on-screen ("current") one and its played-overlay starts filling.
 */
data class StripItem(
    val url: String?,
    val startTime: Double,
    val endTime: Double,
    val seekTarget: Double,
    val currentThreshold: Double,
    val isVideo: Boolean = false,
)

/** Build the pager items: an optional leading title item, then the timeline images. */
fun buildStripItems(
    images: List<TimelineImage>,
    titleClearTime: Double?,
): List<StripItem> = buildList {
    if (titleClearTime != null) {
        val firstImageStart = images.firstOrNull()?.startTime ?: (titleClearTime + 1.0)
        add(
            StripItem(
                url = null,
                startTime = 0.0,
                endTime = firstImageStart,
                seekTarget = titleClearTime,
                currentThreshold = 0.0,
            ),
        )
    }
    images.forEachIndexed { index, it ->
        if (it.isVideo) {
            // Videos have no enter animation: they become current at their start, and tapping
            // seeks to the start of the clip (not into a non-existent fade).
            add(
                StripItem(
                    url = it.url,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    seekTarget = it.startTime,
                    currentThreshold = it.startTime,
                    isVideo = true,
                ),
            )
        } else {
            // A photo becomes "current" the moment it overtakes the previous clip. If it crossfades
            // in (photo→photo), that's the crossfade midpoint — halfway through the region where the
            // two clips are both on screen ([startTime, prevEnd]). If it hard-cuts in — the first
            // clip, or a photo right after a video, where there is no overlap — it is current at its
            // own start; using the crossfade midpoint there would lag the strip by OVERLAP/2 while
            // the photo is already full-screen. Tapping still seeks to the clearest (midpoint) frame.
            val prev = images.getOrNull(index - 1)
            val crossfadesIn = prev != null && prev.endTime > it.startTime
            val currentThreshold = if (crossfadesIn) (it.startTime + prev!!.endTime) / 2.0 else it.startTime
            add(
                StripItem(
                    url = it.url,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    seekTarget = it.fullyVisibleTime,
                    currentThreshold = currentThreshold,
                ),
            )
        }
    }
}

/**
 * Horizontal pager of every timeline image (plus an optional title item).
 *
 * - All items are the same size; the current item snaps to the center (centering content padding).
 * - Scrolling to / tapping an item seeks to it; playback scrolls the pager to follow.
 * - Each thumbnail's overlay grows left→right from 0% right after its enter animation finishes to
 *   100% when its exit animation finishes (already-played items stay fully covered).
 *
 * [buildItems] reads the current items from the engine; it is re-invoked on first composition and on
 * every [Engine.editor] history change (apply, undo, redo) so the strip stays in sync — in
 * particular it refreshes when the user taps undo.
 */
@Composable
fun ImageTimelineStrip(
    engine: Engine,
    buildItems: () -> List<StripItem>,
    currentTime: Double,
    onSeekToTime: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val latestBuild by rememberUpdatedState(buildItems)
    var items by remember { mutableStateOf(latestBuild()) }
    LaunchedEffect(Unit) {
        engine.editor.onHistoryUpdated().collect {
            items = latestBuild()
        }
    }

    if (items.isEmpty()) return

    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { items.size })
    // True only while the user is dragging the pager with their finger (not fling/programmatic).
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // The item currently on screen: the last one that has reached its "becomes current" threshold
    // (crossfade midpoint for images, clip start for videos) — earlier than full visibility so the
    // strip tracks the incoming clip as it comes into view rather than lagging behind.
    val playbackIndex = items.indexOfLast { currentTime >= it.currentThreshold }.coerceAtLeast(0)

    // Seek live as the highlighted (centered) item changes under the user's finger, so playback
    // tracks the strip while dragging instead of only updating once they let go. A haptic marks
    // each change. Only while the finger is down (isDragged) — taps go through onClick instead.
    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (isDragged && page in items.indices) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onSeekToTime(items[page].seekTarget)
            }
        }
    }

    // Settle on the highlighted item whenever the strip is idle — not under the finger, not mid-scroll.
    // Follows normal playback, centers a tapped item, and after a fling pulls any momentum overshoot
    // back to the item highlighted when the finger lifted (where live-seek left playback).
    // One long-lived collector (NOT a re-keyed effect): re-keying on isScrollInProgress would cancel
    // and restart the in-flight animateScrollToPage every frame, making the strip jitter back and forth.
    // playbackIndex is a plain (recomposed) val, so wrap it in a State the snapshotFlow can observe —
    // otherwise the flow only reacts to drag/scroll changes and never to a tap/seek changing the target.
    val targetIndex by rememberUpdatedState(playbackIndex)
    LaunchedEffect(Unit) {
        snapshotFlow { Triple(targetIndex, isDragged, pagerState.isScrollInProgress) }
            .collect { (target, dragging, scrolling) ->
                if (!dragging && !scrolling && target != pagerState.currentPage) {
                    pagerState.animateScrollToPage(target)
                }
            }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Pad each side by half the leftover width so the current page always snaps to the center,
        // including the first page at the start and the last page at the end.
        val sidePadding = ((maxWidth - ITEM_WIDTH) / 2f).coerceAtLeast(0.dp)

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(ITEM_WIDTH),
            contentPadding = PaddingValues(horizontal = sidePadding),
            pageSpacing = 8.dp,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
                // Exclude the strip from the system back gesture so horizontal swipes scroll the
                // pager instead of accidentally navigating back.
                .systemGestureExclusion(),
            key = { items[it].startTime },
        ) { index ->
            val item = items[index]
            val overlayWindow = (item.endTime - item.currentThreshold).coerceAtLeast(0.0001)
            val playedFraction = when {
                currentTime >= item.endTime -> 1f
                currentTime <= item.currentThreshold -> 0f
                else -> ((currentTime - item.currentThreshold) / overlayWindow).toFloat().coerceIn(0f, 1f)
            }

            // Distance from the centered page (in pages), normalized so the edge item = 1f.
            val pageOffset =
                ((pagerState.currentPage - index) + pagerState.currentPageOffsetFraction).absoluteValue
            val itemsToEdge = ((maxWidth.value / 2f) / ITEM_WIDTH.value).coerceAtLeast(1f)
            val edgeFraction = (pageOffset / itemsToEdge).coerceIn(0f, 1f)

            ThumbnailItem(
                item = item,
                playedFraction = playedFraction,
                edgeFraction = edgeFraction,
                isCurrent = index == pagerState.currentPage,
                onClick = {
                    // Scroll to center the tapped item directly (don't rely on the playback-follow
                    // chain, which only reacts when the engine's playback time changes), and seek.
                    scope.launch { pagerState.animateScrollToPage(index) }
                    onSeekToTime(item.seekTarget)
                },
            )
        }
    }
}

@Composable
private fun ThumbnailItem(
    item: StripItem,
    playedFraction: Float,
    edgeFraction: Float,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    // Horizontal inset and fade scale with distance from center (0 at center → max at the edges).
    val horizontalPadding = 0.dp * edgeFraction
    val itemAlpha = 1f - (0.75f * edgeFraction) // 100% at center → 25% at the edges

    // Non-current items get a small animated vertical inset.
    val verticalPadding by animateDpAsState(
        targetValue = if (isCurrent) 0.dp else 8.dp,
        label = "thumb_v_padding",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(itemAlpha)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
    ) {
        if (item.url != null) {
            // For video, show a static middle frame; for images, load the URL directly.
            val model: Any? = if (item.isVideo) rememberVideoThumbnail(item.url) else item.url
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            // Title item: a "T" icon instead of a picture.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = IconPack.Title,
                    contentDescription = "Title",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        // "Played" overlay: grows left→right; fully covers items already played.
        if (playedFraction > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(playedFraction)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
            )
        }

        // Play badge to mark video items.
        if (item.isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(20.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = IconPack.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp),
                )
            }
        }
    }
}
