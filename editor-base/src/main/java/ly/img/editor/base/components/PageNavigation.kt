package ly.img.editor.base.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.compose.foundation.pager.HorizontalPager
import ly.img.editor.compose.foundation.pager.rememberPagerState
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.PageNextArrow
import ly.img.editor.core.ui.iconpack.PagePrevArrow
import ly.img.editor.core.ui.utils.ifTrue

@Composable
fun PageNavigation(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    pageIndex: Int,
    pageCount: Int,
    maxPageIndicators: Int = 7,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
) {
    Row(modifier.ifTrue(!visible) { alpha(0f) }, verticalAlignment = Alignment.CenterVertically) {
        // Show IconButtons for previous and next page, with Icon PageNextArrow and PagePreviousArrow
        IconButton(
            onClick = onPreviousClick,
            enabled = visible && pageIndex > 0,
        ) {
            Icon(IconPack.PagePrevArrow, contentDescription = stringResource(R.string.ly_img_editor_previous_page))
        }
        Spacer(modifier = Modifier.weight(1f))
        PageIndicators(
            pageIndex = pageIndex,
            pageCount = pageCount,
            maxPageIndicators = maxPageIndicators,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onNextClick,
            enabled = visible && pageIndex < pageCount - 1,
        ) {
            Icon(IconPack.PageNextArrow, contentDescription = stringResource(R.string.ly_img_editor_next_page))
        }
    }
}

@Composable
private fun PageIndicators(
    modifier: Modifier = Modifier,
    pageIndex: Int,
    pageCount: Int,
    maxPageIndicators: Int = 7,
) {
    val visiblePageIndicators = maxPageIndicators.coerceAtMost(pageCount)
    if (pageCount > 0) {
        val pagerState = rememberPagerState()

        val indexOffset = visiblePageIndicators / 2
        val middleIndex =
            pageIndex
                .coerceAtMost(pageCount - indexOffset - 1)
                .coerceAtLeast(indexOffset)

        val firstVisibleIndex = (middleIndex - indexOffset).coerceAtLeast(0)
        val lastVisibleIndex = (middleIndex + indexOffset).coerceAtMost(pageCount - 1)
        // Track the initial load
        val initialLoad = remember { mutableStateOf(true) }

        LaunchedEffect(pageIndex) {
            if (initialLoad.value || pageIndex != middleIndex) {
                // Directly jump to the page without animation if it's the first scroll or a fast scroll to the end / start.
                // If the user navigate to fast to the beginning or end of the pages, we'll need a direct jump to the target page
                // without any scrolling animation. This approach is necessary because when scrolling quickly,
                // the `animateScrollToPage` might be called again for the same page while the animation is happening.
                // But if that happens, it interrupts the ongoing scroll animation and also blocks any further attempts
                // to scroll to that page again.
                // This seems an issue with the HorizontalPager component.
                pagerState.scrollToPage(middleIndex)
                initialLoad.value = false
            } else {
                // Animate to the page on subsequent changes.
                pagerState.animateScrollToPage(middleIndex)
            }
        }

        HorizontalPager(
            modifier = modifier.width(112.dp),
            pageCount = pageCount,
            beyondBoundsPageCount = indexOffset,
            contentPadding = PaddingValues(horizontal = 48.dp),
            state = pagerState,
        ) { index ->
            key(index) {
                PageCircle(
                    selected = index == pageIndex,
                    moreIndicator =
                        (index <= middleIndex - indexOffset && firstVisibleIndex > 0) ||
                            (index >= middleIndex + indexOffset && lastVisibleIndex < pageCount - 1),
                )
            }
        }
    }
}

@Composable
private fun PageCircle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    moreIndicator: Boolean,
): Unit =
    PageCircle(
        modifier = modifier,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        canvasSize = 16.dp,
        dotSize = if (moreIndicator) 6.dp else 8.dp,
    )

@Composable
private fun PageCircle(
    modifier: Modifier = Modifier,
    color: Color,
    canvasSize: Dp,
    dotSize: Dp,
) = Box(
    modifier.size(canvasSize),
) {
    Box(
        modifier =
            Modifier
                .clip(CircleShape)
                .background(color)
                .align(Alignment.Center)
                .size(dotSize),
    )
}
