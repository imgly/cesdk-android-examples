package ly.img.editor.base.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.R
import ly.img.editor.base.components.scrollbar.LazyColumnScrollbar
import ly.img.editor.base.components.scrollbar.RowScrollbar
import ly.img.editor.base.components.scrollbar.ScrollbarSettings
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.timeline.track.TrackView
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.AddAudio
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.theme.surface3
import ly.img.editor.core.ui.library.LibraryViewModel
import ly.img.editor.core.ui.utils.roundToPx

@Composable
fun TimelineContentView(
    timelineState: TimelineState,
    verticalScrollState: LazyListState,
    onEvent: (EditorEvent) -> Unit,
) {
    TimelineBaseView(
        timelineState = timelineState,
        onEvent = onEvent,
    ) { horizontalScrollState ->
        val overlayWidth = maxWidth / 2
        val overlayWidthPx = overlayWidth.roundToPx()

        val backgroundTrackGuideline = TimelineConfiguration.clipHeight + TimelineConfiguration.clipPadding * 2

        BackgroundTrackDivider(
            modifier =
                Modifier
                    .padding(bottom = backgroundTrackGuideline)
                    .align(Alignment.BottomStart),
        )

        val timelineRulerHeight = TimelineConfiguration.rulerHeight
        val libraryViewModel = viewModel<LibraryViewModel>()
        Row(
            modifier = Modifier.horizontalScroll(horizontalScrollState),
        ) {
            Box {
                val durationWidth = timelineState.zoomState.toDp(timelineState.totalDuration)
                Column {
                    Box(Modifier.offset(x = overlayWidth)) {
                        Box(
                            modifier =
                                Modifier
                                    .width(durationWidth)
                                    .height(timelineRulerHeight)
                                    .background(MaterialTheme.colorScheme.surface3),
                        )
                        TimelineRulerView(
                            duration = timelineState.totalDuration,
                            zoomState = timelineState.zoomState,
                            height = timelineRulerHeight,
                            extraWidth = overlayWidth,
                        )
                    }

                    LazyColumn(
                        modifier =
                            Modifier
                                .weight(1f)
                                .width(overlayWidth + durationWidth + overlayWidth),
                        state = verticalScrollState,
                        contentPadding =
                            PaddingValues(
                                start = overlayWidth,
                                top = TimelineConfiguration.clipPadding,
                                bottom = TimelineConfiguration.clipPadding,
                            ),
                        verticalArrangement = Arrangement.spacedBy(TimelineConfiguration.clipPadding),
                    ) {
                        val tracks = timelineState.dataSource.tracks

                        items(tracks) { track ->
                            TrackView(
                                track = track,
                                timelineState = timelineState,
                                onEvent = onEvent,
                            )
                        }

                        item {
                            val audioButtonOffset by remember {
                                derivedStateOf {
                                    (horizontalScrollState.value - overlayWidthPx).coerceAtLeast(0)
                                }
                            }

                            TimelineButton(
                                id = R.string.ly_img_editor_add_audio,
                                icon = IconPack.AddAudio,
                                modifier =
                                    Modifier
                                        .offset {
                                            IntOffset(x = audioButtonOffset, y = 0)
                                        }
                                        .padding(start = 1.dp),
                            ) {
                                onEvent(
                                    EditorEvent.Sheet.Open(
                                        SheetType.LibraryAdd(
                                            libraryCategory = libraryViewModel.assetLibrary.audios(libraryViewModel.sceneMode),
                                        ),
                                    ),
                                )
                            }
                        }
                    }
                    Box(
                        modifier =
                            Modifier
                                .offset(x = overlayWidth)
                                .padding(vertical = TimelineConfiguration.clipPadding),
                    ) {
                        val backgroundTrack = timelineState.dataSource.backgroundTrack
                        TrackView(
                            track = backgroundTrack,
                            timelineState = timelineState,
                            onEvent = onEvent,
                        )
                        AddClipButton(
                            modifier = Modifier.offset(x = if (backgroundTrack.clips.isEmpty()) 1.dp else durationWidth),
                            onEvent = onEvent,
                        )
                    }
                }
            }
        }

        val scrollbarSettings =
            ScrollbarSettings(
                thumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                alwaysShowScrollbar = false,
            )

        LazyColumnScrollbar(
            state = verticalScrollState,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(bottom = backgroundTrackGuideline + 2.dp, top = timelineRulerHeight + 2.dp),
            settings =
                scrollbarSettings.copy(
                    thumbThickness = 2.dp,
                    scrollbarPadding = 2.dp,
                ),
        )

        RowScrollbar(
            state = horizontalScrollState,
            settings =
                scrollbarSettings.copy(
                    thumbThickness = 2.dp,
                    scrollbarPadding = 1.dp,
                ),
            visibleLengthDp = maxWidth,
        )

        PlayheadView(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(top = timelineRulerHeight - 1.dp),
        )
    }
}
