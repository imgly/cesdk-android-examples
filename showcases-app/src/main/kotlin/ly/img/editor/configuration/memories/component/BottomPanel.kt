package ly.img.editor.configuration.memories.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.configuration.memories.scene.findTracks
import ly.img.editor.configuration.memories.scene.readTimelineImages
import ly.img.editor.configuration.memories.scene.readTitleClearTime
import ly.img.editor.configuration.memories.sheet.createVolumeSheetType
import ly.img.editor.configuration.memories.widget.ImageTimelineStrip
import ly.img.editor.configuration.memories.widget.VideoControlPanel
import ly.img.editor.configuration.memories.widget.buildStripItems
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorTrigger
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.state.EditorViewMode
import ly.img.editor.core.theme.surface1

fun bottomPanelConfiguration(viewModel: MemoriesViewModel): @Composable (EditorScope.() -> EditorComponent<*>) =
    @Composable {
        val isPlaying by viewModel.isPlaying.collectAsState()
        val shouldHideUIForPreview by viewModel.shouldHideUIForPreview.collectAsState()
        val isLooping by viewModel.isLooping.collectAsState()
        val volume by viewModel.volume.collectAsState()

        EditorComponent.remember {
            scope = {
                val historyTrigger by EditorTrigger.remember {
                    editorContext.engine.editor.onHistoryUpdated()
                }
                remember(this, historyTrigger) {
                    NavigationBar.Scope(parentScope = this)
                }
            }
            enterTransition = {
                expandVertically(animationSpec = tween(200)) + fadeIn()
            }
            exitTransition = {
                shrinkVertically(animationSpec = tween(200)) + fadeOut()
            }
            visible = {
                val state by editorContext.state.collectAsState()
                (state.viewMode !is EditorViewMode.Preview) && !shouldHideUIForPreview
            }
            decoration = {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 1.dp)
                        .background(
                            MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f),
                        ),
                ) {
                    val page = editorContext.engine.scene.getCurrentPage() ?: return@Box
                    val engine = editorContext.engine

                    // Current playback time, polled to drive the timeline strip cursor.
                    var currentTime by remember { mutableStateOf(0.0) }

                    LaunchedEffect(Unit) {
                        while (true) {
                            currentTime = engine.block.getPlaybackTime(page)
                            delay(100) // Update every 100ms for smoother display
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // The strip reads its items straight from the engine tracks and rebuilds
                        // itself on every history change (apply, undo, redo) — see ImageTimelineStrip.
                        ImageTimelineStrip(
                            engine = engine,
                            buildItems = {
                                val tracks = findTracks(engine, page)
                                if (tracks != null) {
                                    val images = readTimelineImages(engine, tracks)
                                    val titleClearTime = readTitleClearTime(engine, page, tracks)
                                    buildStripItems(images, titleClearTime)
                                } else {
                                    emptyList()
                                }
                            },
                            currentTime = currentTime,
                            onSeekToTime = { time ->
                                engine.block.setPlaybackTime(page, time)
                            },
                        )

                        VideoControlPanel(
                            isPlaying = isPlaying,
                            isLooping = isLooping,
                            onPlayPauseClick = {
                                viewModel.setPlaying(!isPlaying)
                                engine.block.setPlaying(
                                    block = page,
                                    enabled = !isPlaying,
                                )
                            },
                            onRestartClick = {
                                engine.block.setPlaybackTime(page, 0.0)
                                engine.block.setPlaying(page, false)
                                viewModel.setPlaying(false)
                            },
                            onLoopClick = {
                                viewModel.toggleLoop()
                            },
                            onVolumeClick = {
                                editorContext.eventHandler.send(
                                    EditorEvent.Sheet.Open(
                                        createVolumeSheetType(
                                            initialVolume = volume,
                                            onVolumeChange = { newVolume ->
                                                viewModel.setVolume(newVolume)
                                            },
                                        ),
                                    ),
                                )
                            },
                            onFullscreenClick = {
                                // Enter preview/fullscreen and start playing (the bottom panel is only
                                // visible outside preview, so this always enters).
                                viewModel.togglePreviewMode()
                                engine.block.setPlaying(page, true)
                                viewModel.setPlaying(true)
                            },
                        )
                    }
                }
            }
        }
    }
