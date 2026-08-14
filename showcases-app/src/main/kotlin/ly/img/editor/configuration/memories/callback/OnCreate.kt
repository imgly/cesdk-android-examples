package ly.img.editor.configuration.memories.callback

import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import ly.img.editor.configuration.memories.MemoriesConfiguration
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.configuration.memories.extension.onLoadAssetSources
import ly.img.editor.configuration.memories.scene.calculateTotalDuration
import ly.img.editor.configuration.memories.scene.createBurstImages
import ly.img.editor.configuration.memories.scene.createMainImageSequence
import ly.img.editor.configuration.memories.scene.createSlideshowScene
import ly.img.editor.configuration.memories.scene.createTitleBlock
import ly.img.editor.configuration.memories.scene.setupEventSubscriptions
import ly.img.editor.configuration.memories.scene.setupTracks
import ly.img.editor.configuration.memories.style.loadStyleThumbnails
import ly.img.editor.configuration.memories.util.Animations
import ly.img.editor.configuration.memories.util.CANVAS_PADDING_DP
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.data.Insets
import ly.img.editor.core.event.EditorEvent

private fun EditorScope.applyCanvasPadding() {
    editorContext.eventHandler.send(EditorEvent.Insets.SetExtra(insets = Insets(CANVAS_PADDING_DP.dp)))
}

fun MemoriesConfiguration.onCreateConfiguration(viewModel: MemoriesViewModel): (suspend EditorScope.() -> Unit) = onCreate@{
    val engine = editorContext.engine
    showLoading = true
    viewModel.setEditorContext(editorContext)

    val page = createSlideshowScene(engine)
    onLoadAssetSources()
    // Resolve the style picker thumbnails from the custom asset source now that it is registered.
    viewModel.setStyleThumbnails(engine.loadStyleThumbnails())

    // Without this the engine flattens our multi-track crossfade timeline on snapshot-restore
    // (e.g. undo): it strips "foreground" tracks and reparents their clips to the page. Must be
    // set after the scene exists.
    engine.editor.setSettingBoolean("features/removeForegroundTracksOnSceneLoad", false)

    val tracks = setupTracks(engine, page)

    val animationPairs = Animations.createAnimationPairs()
    viewModel.setAnimationPairs(animationPairs)

    val images = viewModel.gridImages.first()
    val hasTitle = createTitleBlock(engine, page, viewModel.videoTitle.first(), tracks.textTrack)
    createBurstImages(engine, page, runCatching { viewModel.getContext() }.getOrNull(), images, hasTitle)

    // One track per slide, positioned by its time offset (which already includes the title
    // offset when a title is present), each appended on top of the previous for the crossfade.
    // Returns each slot's on-timeline duration (a short video is only as long as its source).
    val durations = createMainImageSequence(engine, page, images, hasTitle, animationPairs)

    val totalDuration = calculateTotalDuration(images, durations, hasTitle)
    engine.block.setDuration(page, duration = totalDuration)
    engine.block.setDuration(tracks.backgroundBlock, duration = totalDuration)
    engine.block.setDuration(tracks.matteBlock, duration = totalDuration)

    engine.block.fillParent(tracks.backgroundTrack)
    engine.block.fillParent(tracks.matteTrack)
    engine.block.fillParent(tracks.textTrack)

    viewModel.initializeEditorState()
    showLoading = false

    setupEventSubscriptions(engine, editorContext, viewModel)
    applyCanvasPadding()
}
