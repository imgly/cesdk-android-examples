package ly.img.editor.base.components.scrollbar.controller

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
internal fun rememberLazyListStateController(
    state: LazyListState,
    thumbMinLength: Float,
    thumbMaxLength: Float,
    alwaysShowScrollBar: Boolean,
): LazyListStateController {
    val thumbMinLengthUpdated = rememberUpdatedState(thumbMinLength)
    val thumbMaxLengthUpdated = rememberUpdatedState(thumbMaxLength)
    val alwaysShowScrollBarUpdated = rememberUpdatedState(alwaysShowScrollBar)
    val reverseLayout = remember { derivedStateOf { state.layoutInfo.reverseLayout } }

    val realFirstVisibleItem =
        remember {
            derivedStateOf {
                state.layoutInfo.visibleItemsInfo.firstOrNull {
                    it.index == state.firstVisibleItemIndex
                }
            }
        }

    val isStickyHeaderInAction =
        remember {
            derivedStateOf {
                val realIndex = realFirstVisibleItem.value?.index ?: return@derivedStateOf false
                val firstVisibleIndex =
                    state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                        ?: return@derivedStateOf false
                realIndex != firstVisibleIndex
            }
        }

    fun LazyListItemInfo.fractionHiddenTop(firstItemOffset: Int) = if (size == 0) 0f else firstItemOffset / size.toFloat()

    fun LazyListItemInfo.fractionVisibleBottom(viewportEndOffset: Int) =
        if (size == 0) 0f else (viewportEndOffset - offset).toFloat() / size.toFloat()

    val thumbSizeNormalizedReal =
        remember {
            derivedStateOf {
                state.layoutInfo.let {
                    if (it.totalItemsCount == 0) {
                        return@let 0f
                    }

                    val firstItem = realFirstVisibleItem.value ?: return@let 0f
                    val firstPartial =
                        firstItem.fractionHiddenTop(state.firstVisibleItemScrollOffset)
                    val lastPartial =
                        1f -
                            it.visibleItemsInfo.last().fractionVisibleBottom(
                                it.viewportEndOffset - it.afterContentPadding,
                            )

                    val realSize = it.visibleItemsInfo.size - if (isStickyHeaderInAction.value) 1 else 0
                    val realVisibleSize = realSize.toFloat() - firstPartial - lastPartial
                    realVisibleSize / it.totalItemsCount.toFloat()
                }
            }
        }

    val thumbSizeNormalized =
        remember {
            derivedStateOf {
                thumbSizeNormalizedReal.value.coerceIn(
                    thumbMinLengthUpdated.value,
                    thumbMaxLengthUpdated.value,
                )
            }
        }

    fun offsetCorrection(top: Float): Float {
        val topRealMax = (1f - thumbSizeNormalizedReal.value).coerceIn(0f, 1f)
        if (thumbSizeNormalizedReal.value >= thumbMinLengthUpdated.value) {
            return when {
                reverseLayout.value -> topRealMax - top
                else -> top
            }
        }

        val topMax = 1f - thumbMinLengthUpdated.value
        return when {
            reverseLayout.value -> (topRealMax - top) * topMax / topRealMax
            else -> top * topMax / topRealMax
        }
    }

    val thumbOffsetNormalized =
        remember {
            derivedStateOf {
                state.layoutInfo.let {
                    if (it.totalItemsCount == 0 || it.visibleItemsInfo.isEmpty()) {
                        return@let 0f
                    }

                    val firstItem = realFirstVisibleItem.value ?: return@let 0f
                    val top =
                        firstItem
                            .run {
                                index.toFloat() +
                                    fractionHiddenTop(
                                        state.firstVisibleItemScrollOffset,
                                    )
                            } / it.totalItemsCount.toFloat()
                    offsetCorrection(top)
                }
            }
        }

    val thumbIsInAction =
        remember {
            derivedStateOf { state.isScrollInProgress || alwaysShowScrollBarUpdated.value }
        }

    return remember {
        LazyListStateController(
            thumbSizeNormalized = thumbSizeNormalized,
            thumbOffsetNormalized = thumbOffsetNormalized,
            thumbIsInAction = thumbIsInAction,
        )
    }
}

internal class LazyListStateController(
    override val thumbSizeNormalized: State<Float>,
    override val thumbOffsetNormalized: State<Float>,
    override val thumbIsInAction: State<Boolean>,
) : StateController
