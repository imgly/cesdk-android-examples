package ly.img.editor.base.timeline.clip

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import ly.img.editor.base.timeline.clip.trim.ClipDragType
import ly.img.editor.base.timeline.clip.trim.ClipSelectionView
import ly.img.editor.base.timeline.clip.trim.ClipTrimHandleIconView
import ly.img.editor.base.timeline.clip.trim.ClipTrimOutlineView
import ly.img.editor.base.timeline.clip.trim.IconStyle
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.utils.almostEquals
import ly.img.editor.core.ui.utils.formatForClip
import ly.img.editor.core.ui.utils.toDp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun ClipView(
    modifier: Modifier = Modifier,
    clip: Clip,
    timelineState: TimelineState,
    onEvent: (Event) -> Unit,
) {
    val isSelected by remember(clip.id) {
        derivedStateOf {
            clip.id == timelineState.selectedClip?.id
        }
    }

    Box(modifier = modifier.zIndex(if (isSelected) 1f else 0f)) {
        val zoomState = timelineState.zoomState

        var offset by remember(clip.timeOffset, zoomState.zoomLevel, clip.duration) {
            mutableStateOf(zoomState.toPx(clip.timeOffset))
        }

        var width by remember(clip.duration, zoomState.zoomLevel) {
            mutableStateOf(zoomState.toPx(clip.duration))
        }

        val totalDurationWidth by remember(timelineState.totalDuration, zoomState.zoomLevel) {
            mutableStateOf(zoomState.toPx(timelineState.totalDuration))
        }

        BoxWithConstraints(
            Modifier
                .offset {
                    IntOffset(x = offset.roundToInt(), y = 0)
                }
                .height(TimelineConfiguration.clipHeight)
                .width(width.toDp())
                .zIndex(if (isSelected) 1f else 0f)
                .absolutePadding(right = 1.dp)
                .pointerInput(clip.id, clip.allowsSelecting) {
                    if (clip.allowsSelecting) {
                        detectTapGestures {
                            onEvent(BlockEvent.OnToggleSelectBlock(clip.id))
                        }
                    }
                },
        ) {
            var clipDurationText by remember(isSelected, clip.duration) {
                mutableStateOf(
                    if (isSelected) {
                        clip.duration.formatForClip()
                    } else {
                        ""
                    },
                )
            }

            var clipDragType: ClipDragType? by remember { mutableStateOf(null) }
            val draggingColor = LocalExtendedColorScheme.current.yellow.color
            val selectedColor = MaterialTheme.colorScheme.primary
            val draggingIconColor = LocalExtendedColorScheme.current.yellow.onColor
            val selectedIconColor = MaterialTheme.colorScheme.onPrimary
            val selectionColor by remember(clipDragType) {
                mutableStateOf(if (clipDragType == null) selectedColor else draggingColor)
            }
            val handleIconColor by remember(clipDragType) {
                mutableStateOf(if (clipDragType == null) selectedIconColor else draggingIconColor)
            }

            ClipTrimOutlineView(
                clip = clip,
                zoomState = zoomState,
                clipDragType = clipDragType,
                dashColor = draggingColor,
                realtimeWidth = width,
                height = maxHeight,
            )

            ClipBackgroundView(
                clip = clip,
                timelineState = timelineState,
                inTimeline = true,
            )

            val overlayWidth = width + offset - totalDurationWidth
            ClipForegroundView(
                clip = clip,
                isSelected = isSelected,
                zoomState = zoomState,
                clipDurationText = clipDurationText,
                overlayWidth = overlayWidth.toDp(),
                overlayShape =
                    if (overlayWidth <= 0) {
                        null
                    } else {
                        if (offset > totalDurationWidth) {
                            MaterialTheme.shapes.small
                        } else {
                            MaterialTheme.shapes.small.copy(topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp))
                        }
                    },
            )

            if (isSelected) {
                val handleWidth = 20.dp
                val verticalInset = 2.dp

                val leadingTrimHandleOvershoot = remember { mutableStateOf(0f) }
                val trailingTrimHandleOvershoot = remember { mutableStateOf(0f) }

                val animatedLeadingTrimHandleOvershoot by animateOvershoot(
                    overshootValue = leadingTrimHandleOvershoot,
                    shouldBounce = clipDragType == null,
                )
                val animatedTrailingTrimHandleOvershoot by animateOvershoot(
                    overshootValue = trailingTrimHandleOvershoot,
                    shouldBounce = clipDragType == null,
                )

                HapticFeedbackOnOvershoot(overshootValue = leadingTrimHandleOvershoot, clipDragType = clipDragType)
                HapticFeedbackOnOvershoot(overshootValue = trailingTrimHandleOvershoot, clipDragType = clipDragType)

                Box(
                    modifier =
                        Modifier
                            .wrapContentSize(
                                unbounded = true,
                                align =
                                    when {
                                        animatedLeadingTrimHandleOvershoot != 0f -> Alignment.CenterEnd
                                        animatedTrailingTrimHandleOvershoot != 0f -> Alignment.CenterStart
                                        else -> Alignment.Center
                                    },
                            )
                            .offset(
                                x =
                                    handleWidth *
                                        when {
                                            animatedLeadingTrimHandleOvershoot != 0f -> 1
                                            animatedTrailingTrimHandleOvershoot != 0f -> -1
                                            else -> 0
                                        },
                            )
                            .size(
                                width =
                                    maxWidth + (handleWidth * 2) +
                                        (animatedLeadingTrimHandleOvershoot + animatedTrailingTrimHandleOvershoot).toDp(),
                                height = maxHeight + (verticalInset * 2),
                            ),
                ) {
                    ClipSelectionView(
                        modifier = Modifier.fillMaxSize(),
                        handleWidth = handleWidth,
                        color = selectionColor,
                    )

                    fun determineLeadingTrimIconStyle(hasReachedMaxWidth: Boolean? = null): IconStyle {
                        if (!clip.hasLoaded) return IconStyle.Neutral
                        if (!clip.allowsTrimming) return IconStyle.Left
                        if (hasReachedMaxWidth != null) {
                            return if (hasReachedMaxWidth) IconStyle.Neutral else IconStyle.Left
                        }
                        return if (clip.trimOffset.almostEquals(0.seconds)) IconStyle.Neutral else IconStyle.Left
                    }

                    fun determineTrailingTrimIconStyle(hasReachedMaxWidth: Boolean? = null): IconStyle {
                        if (!clip.hasLoaded) return IconStyle.Neutral
                        if (!clip.allowsTrimming) return IconStyle.Right
                        if (hasReachedMaxWidth != null) {
                            return if (hasReachedMaxWidth) IconStyle.Neutral else IconStyle.Right
                        }
                        return if ((
                                (
                                    clip.footageDuration
                                        ?: 0.seconds
                                ) - clip.trimOffset - clip.duration
                            ).almostEquals(0.seconds)
                        ) {
                            IconStyle.Neutral
                        } else {
                            IconStyle.Right
                        }
                    }

                    var playerWasPlayingBeforeDragStart by remember { mutableStateOf(false) }
                    var leadingTrimIconStyle by remember(clip) { mutableStateOf(determineLeadingTrimIconStyle()) }
                    var trailingTrimIconStyle by remember(clip) { mutableStateOf(determineTrailingTrimIconStyle()) }

                    fun onDragStart(type: ClipDragType) {
                        clipDragType = type
                        val playerState = timelineState.playerState
                        playerWasPlayingBeforeDragStart = playerState.isPlaying
                        playerState.pause()
                    }

                    fun onDragEnd() {
                        clipDragType = null
                        if (playerWasPlayingBeforeDragStart) {
                            timelineState.playerState.play()
                        }
                    }

                    fun onDrag(newWidth: Float) {
                        clipDurationText = zoomState.toSeconds(newWidth).formatForClip()
                    }

                    // Leading trim handle
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .width(handleWidth)
                                .align(Alignment.CenterStart)
                                .pointerInput(clip, zoomState.zoomLevel) {
                                    if (!clip.hasLoaded) return@pointerInput
                                    val minWidth = zoomState.toPx(minOf(clip.duration, TimelineConfiguration.minClipDuration))
                                    val maxWidth =
                                        if (clip.footageDuration != null) {
                                            zoomState.toPx(clip.duration + clip.trimOffset)
                                        } else if (clip.isInBackgroundTrack) {
                                            Float.POSITIVE_INFINITY
                                        } else {
                                            zoomState.toPx(clip.duration + clip.timeOffset)
                                        }
                                    var initialWidth = 0f
                                    detectHorizontalDragGestures(
                                        onDragStart = {
                                            initialWidth = width
                                            onDragStart(type = ClipDragType.Leading)
                                        },
                                        onHorizontalDrag = { _, drag ->
                                            val oldWidth = width
                                            val newProposedWidth = oldWidth - drag
                                            if (leadingTrimHandleOvershoot.value == 0f) {
                                                width = newProposedWidth.coerceIn(minWidth, maxWidth)
                                                onDrag(width)
                                            }
                                            if (width != newProposedWidth) {
                                                with(leadingTrimHandleOvershoot) {
                                                    value = (value + drag).coerceAtMost(0f)
                                                }
                                            }

                                            leadingTrimIconStyle = determineLeadingTrimIconStyle(width == maxWidth)

                                            // we only want to consume as much drag that doesn't
                                            // move the trailing handle at all from its original position
                                            val consumeDragAmount = oldWidth - width
                                            offset += consumeDragAmount
                                        },
                                        onDragEnd = {
                                            onDragEnd()
                                            leadingTrimHandleOvershoot.value = 0f

                                            var trimOffset = clip.trimOffset
                                            var timeOffset = clip.timeOffset
                                            var duration = clip.duration

                                            val delta = zoomState.toSeconds(initialWidth - width)

                                            if (!clip.isInBackgroundTrack) {
                                                timeOffset = (timeOffset + delta).coerceAtLeast(0.seconds)
                                            }
                                            trimOffset += delta
                                            duration -= delta

                                            onEvent(
                                                BlockEvent.OnUpdateTrim(
                                                    trimOffset = trimOffset,
                                                    timeOffset = timeOffset,
                                                    duration = duration,
                                                ),
                                            )
                                        },
                                    )
                                },
                    ) {
                        ClipTrimHandleIconView(
                            style = leadingTrimIconStyle,
                            modifier = Modifier.align(Alignment.Center),
                            color = handleIconColor,
                        )
                    }

                    // Trim clip body
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                                .padding(horizontal = handleWidth)
                                .pointerInput(clip, zoomState.zoomLevel) {
                                    if (!clip.isInBackgroundTrack) {
                                        detectHorizontalDragGestures(
                                            onDragStart = {
                                                onDragStart(type = ClipDragType.Move)
                                            },
                                            onHorizontalDrag = { _, drag ->
                                                // we don't want our clips to have a negative time offset
                                                offset = (offset + drag).coerceAtLeast(0f)
                                            },
                                            onDragEnd = {
                                                onDragEnd()
                                                onEvent(BlockEvent.OnUpdateTimeOffset(zoomState.toSeconds(offset)))
                                            },
                                        )
                                    }
                                },
                    )

                    // Trailing trim handle
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .width(handleWidth)
                                .align(Alignment.CenterEnd)
                                .pointerInput(clip, zoomState.zoomLevel) {
                                    if (!clip.hasLoaded) return@pointerInput
                                    val minWidth = zoomState.toPx(minOf(clip.duration, TimelineConfiguration.minClipDuration))
                                    val maxWidth =
                                        if (clip.footageDuration != null) {
                                            zoomState.toPx(clip.footageDuration - clip.trimOffset)
                                        } else {
                                            Float.POSITIVE_INFINITY
                                        }
                                    detectHorizontalDragGestures(
                                        onDragStart = {
                                            onDragStart(ClipDragType.Trailing)
                                        },
                                        onHorizontalDrag = { _, drag ->
                                            val newProposedWidth = width + drag

                                            if (trailingTrimHandleOvershoot.value == 0f) {
                                                width = newProposedWidth.coerceIn(minWidth, maxWidth)
                                                onDrag(width)
                                            }
                                            if (width != newProposedWidth) {
                                                with(trailingTrimHandleOvershoot) {
                                                    value = (value + drag).coerceAtLeast(0f)
                                                }
                                            }

                                            trailingTrimIconStyle = determineTrailingTrimIconStyle(width == maxWidth)
                                        },
                                        onDragEnd = {
                                            trailingTrimHandleOvershoot.value = 0f
                                            onDragEnd()
                                            onEvent(BlockEvent.OnUpdateDuration(zoomState.toSeconds(width)))
                                        },
                                    )
                                },
                    ) {
                        ClipTrimHandleIconView(
                            style = trailingTrimIconStyle,
                            modifier = Modifier.align(Alignment.Center),
                            color = handleIconColor,
                        )
                    }
                }
            }
        }

        // overlay
        if (!clip.isInBackgroundTrack) {
            ClipOverlay(
                modifier =
                    Modifier
                        .offset(totalDurationWidth.toDp())
                        .height(TimelineConfiguration.clipHeight)
                        .fillMaxWidth()
                        .align(Alignment.TopEnd),
            )
        }
    }
}

// Adds a bouncy animation when trim handles are released after being overshot
@Composable
private fun animateOvershoot(
    overshootValue: State<Float>,
    shouldBounce: Boolean,
): State<Float> =
    animateFloatAsState(
        targetValue = overshootValue.value.absoluteValue / 2.45f,
        animationSpec =
            if (shouldBounce) {
                spring(
                    dampingRatio = 0.35f,
                    stiffness = 800f,
                )
            } else {
                snap()
            },
        label = "animatedTrimHandleOvershoot",
    )

// Adds some vibration when the trim handles are dragged beyond their limits
@Composable
private fun HapticFeedbackOnOvershoot(
    overshootValue: State<Float>,
    clipDragType: ClipDragType?,
) {
    val viewForHapticFeedback = LocalView.current
    LaunchedEffect(Unit) {
        snapshotFlow { overshootValue.value }
            .drop(1)
            .map { it == 0f }
            .distinctUntilChanged()
            .collect { overshootAtZero ->
                if (clipDragType == null && overshootAtZero) {
                    viewForHapticFeedback.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                    )
                } else if (!overshootAtZero) {
                    viewForHapticFeedback.performHapticFeedback(
                        HapticFeedbackConstants.CLOCK_TICK,
                    )
                }
            }
    }
}
