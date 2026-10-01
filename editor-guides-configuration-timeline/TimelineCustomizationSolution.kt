import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.Editor
import ly.img.editor.core.component.Button
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.HorizontalListBuilder
import ly.img.editor.core.component.Timeline
import ly.img.editor.core.component.UnalignedListBuilder
import ly.img.editor.core.component.data.TimelineHeight
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberAddAudio
import ly.img.editor.core.component.rememberAddClip
import ly.img.editor.core.component.rememberAudiosLibrary
import ly.img.editor.core.component.rememberCamera
import ly.img.editor.core.component.rememberGallery
import ly.img.editor.core.component.rememberImglyCamera
import ly.img.editor.core.component.rememberLibrary
import ly.img.editor.core.component.rememberLoop
import ly.img.editor.core.component.rememberMusic
import ly.img.editor.core.component.rememberOverlaysLibrary
import ly.img.editor.core.component.rememberPlayPause
import ly.img.editor.core.component.rememberResizeAll
import ly.img.editor.core.component.rememberStickersAndShapesLibrary
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.component.rememberTimecode
import ly.img.editor.core.component.rememberToggleExpanded
import ly.img.editor.core.component.rememberVoiceoverRecord
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.iconpack.Music
import ly.img.editor.core.iconpack.VolumeHigh
import ly.img.editor.core.state.EditorViewMode

// highlight-android-custom-event
object AddStockFootage : EditorEvent
// highlight-android-custom-event

// Add this composable to your NavHost
@Composable
fun TimelineCustomizationSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // Note that the value is reset on configuration changes.
    // Default implementation uses editorContext.mutableStateOf which survives configuration changes.
    val isTimelineExpanded = remember { mutableStateOf(false) }

    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-bottom-panel
                bottomPanel = { rememberCustomizedTimeline(expandedState = isTimelineExpanded) }
                // highlight-android-bottom-panel

                // Demo scaffolding, not part of the lesson: the dock a video editor usually offers,
                // so there is media to arrange on the timeline.
                dock = { rememberVideoDock() }
            }
        },
        onClose = onClose,
    )
}

@Composable
private fun rememberVideoDock() = Dock.remember {
    listBuilder = {
        Dock.ListBuilder.remember {
            add { Dock.Button.rememberSystemGallery() }
            add { Dock.Button.rememberImglyCamera() }
            add { Dock.Button.rememberOverlaysLibrary() }
            add { Dock.Button.rememberTextLibrary() }
            add { Dock.Button.rememberStickersAndShapesLibrary() }
            add { Dock.Button.rememberAudiosLibrary() }
            add { Dock.Button.rememberVoiceoverRecord() }
            add { Dock.Button.rememberResizeAll() }
        }
    }
}

// highlight-android-timeline-builder
@Composable
private fun rememberCustomizedTimeline(expandedState: MutableState<Boolean>) = Timeline.remember {
    // highlight-android-timeline-builder
    // highlight-android-expanded
    // Your own state decides whether the timeline starts expanded. The expand/collapse toggle
    // writes back to it, so this value keeps matching what is on screen.
    scope = {
        remember(this) {
            Timeline.Scope(parentScope = this, expandedState = expandedState)
        }
    }
    // highlight-android-expanded

    // highlight-android-add-clip
    addClipButton = {
        Timeline.Button.rememberAddClip {
            optionsBuilder = {
                UnalignedListBuilder.remember {
                    add { Timeline.AddClipOption.rememberCamera() }
                    add { Timeline.AddClipOption.rememberLibrary() }
                    add {
                        Timeline.AddClipOption.remember {
                            id = { EditorComponentId("my.company.timeline.addClip.stockFootage") }
                            vectorIcon = { IconPack.LibraryElements }
                            textString = { "Stock Footage" }
                            onClick = { editorContext.eventHandler.send(AddStockFootage) }
                        }
                    }
                }
            }
        }
    }
    // highlight-android-add-clip

    // highlight-android-add-audio
    addAudioButton = {
        Timeline.Button.rememberAddAudio {
            optionsBuilder = {
                UnalignedListBuilder.remember {
                    add { Timeline.AddAudioOption.rememberMusic() }
                }
            }
        }
    }
    // highlight-android-add-audio

    // highlight-android-modify-header
    // The timeline renders tracks alone, so the header is declared rather than modified.
    headerListBuilder = {
        Timeline.HeaderListBuilder.remember {
            aligned(alignment = Alignment.Start) {
                add { Timeline.Label.rememberTimecode() }
            }
            aligned(alignment = Alignment.CenterHorizontally) {
                add { Timeline.Button.rememberPlayPause() }
            }
            aligned(alignment = Alignment.End) {
                add { rememberMuteButton() }
                add { Timeline.Button.rememberToggleExpanded() }
            }
        }
    }
    // highlight-android-modify-header

    // highlight-android-height
    // Grow to at most three overlay tracks. This is the default.
    height = { TimelineHeight.Dynamic(maximumTracks = 3) }
    // Or pin the timeline to exactly two overlay tracks:
    // height = { TimelineHeight.Fixed(tracks = 2) }
    // highlight-android-height
}

// highlight-android-custom-header-item
@Composable
private fun rememberMuteButton(): Button<Timeline.ItemScope> {
    var muted by remember { mutableStateOf(false) }
    return Button.remember(::timelineHeaderButtonBuilder) {
        id = { EditorComponentId("my.company.timeline.button.mute") }
        vectorIcon = { IconPack.VolumeHigh }
        contentDescription = { if (muted) "Unmute" else "Mute" }
        onClick = { muted = muted.not() }
    }
}

private fun timelineHeaderButtonBuilder() = Timeline.ButtonBuilder()
// highlight-android-custom-header-item

// highlight-android-add-clip-restated
@Composable
fun rememberRestatedAddClipButton() = Timeline.Button.rememberAddClip {
    optionsBuilder = {
        UnalignedListBuilder.remember {
            add {
                // Reordered, and the gallery source keeps its icon while its label changes.
                Timeline.AddClipOption.rememberGallery {
                    textString = { "From Device" }
                }
            }
            add {
                // A built-in source keeps its label and icon while its behavior is replaced.
                Timeline.AddClipOption.rememberCamera {
                    onClick = { editorContext.eventHandler.send(AddStockFootage) }
                }
            }
            add {
                Timeline.AddClipOption.rememberLibrary()
            }
        }
    }
}
// highlight-android-add-clip-restated

// highlight-android-add-clip-conditional
@Composable
fun rememberConditionalAddClipButton() = Timeline.Button.rememberAddClip {
    optionsBuilder = {
        val state by editorContext.state.collectAsState()
        UnalignedListBuilder.remember {
            // The options lambda is re-evaluated on recomposition, so read state inside it
            // rather than capturing a value from the caller.
            if (state.viewMode is EditorViewMode.Edit) {
                add { Timeline.AddClipOption.rememberCamera() }
            }
            add { Timeline.AddClipOption.rememberLibrary() }
        }
    }
}
// highlight-android-add-clip-conditional

// highlight-android-add-clip-single
@Composable
fun rememberSingleSourceAddClipButton() = Timeline.Button.rememberAddClip {
    optionsBuilder = {
        UnalignedListBuilder.remember {
            add { Timeline.AddClipOption.rememberLibrary() }
        }
    }
}
// highlight-android-add-clip-single

// highlight-android-add-audio-custom
// The lane sizes a replacement to one track row, so drop the header button's square touch target.
@Composable
fun rememberCustomAddAudioButton() = Button.remember(Timeline::ButtonBuilder) {
    modifier = { Modifier.size(width = 96.dp, height = 40.dp) }
    id = { EditorComponentId("my.company.timeline.button.soundtrack") }
    vectorIcon = { IconPack.Music }
    textString = { "Soundtrack" }
    onClick = { editorContext.eventHandler.send(AddStockFootage) }
}
// highlight-android-add-audio-custom

// highlight-android-header
@Composable
fun rememberDeclaredHeader(): HorizontalListBuilder<EditorComponent<*>> = Timeline.HeaderListBuilder.remember {
    aligned(alignment = Alignment.Start) {
        add { Timeline.Label.rememberTimecode() }
    }
    aligned(alignment = Alignment.CenterHorizontally) {
        add { Timeline.Button.rememberPlayPause() }
    }
    aligned(alignment = Alignment.End) {
        add { Timeline.Button.rememberLoop() }
        add { Timeline.Button.rememberToggleExpanded() }
    }
}
// highlight-android-header

// highlight-android-remove
// The defaults are already bare: no header, and no lane buttons. An empty header removes the
// player bar with it, and the tracks keep rendering.
@Composable
fun rememberBareTimeline() = Timeline.remember()
// highlight-android-remove
