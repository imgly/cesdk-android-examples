package ly.img.editor.base.timeline.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.times
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.isParentBackgroundTrack
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.clip.ClipType
import ly.img.editor.base.timeline.thumbnail.ThumbnailsManager
import ly.img.editor.base.timeline.track.Track
import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.getBackgroundTrack
import ly.img.editor.core.ui.engine.getCurrentPage
import ly.img.editor.core.ui.engine.getFillType
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.editor.core.ui.utils.EPS_DURATION
import ly.img.editor.core.ui.utils.formatForPlayer
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockEvent
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class TimelineState(
    private val engine: Engine,
    private val coroutineScope: CoroutineScope,
) {
    var selectedClip: Clip? by mutableStateOf(null)
        private set

    var totalDuration: Duration by mutableStateOf(0.seconds)
        private set

    val formattedTotalDuration by derivedStateOf {
        totalDuration.formatForPlayer()
    }

    val timelineViewHeight by derivedStateOf {
        val visibleTracksCount = dataSource.tracks.size.toFloat().coerceAtMost(2.5f)
        with(TimelineConfiguration) {
            val backgroundTrackHeight = clipHeight + clipPadding * 2
            val addAudioButtonHeight = backgroundTrackHeight
            backgroundTrackDividerHeight + rulerHeight + backgroundTrackHeight + addAudioButtonHeight +
                visibleTracksCount * clipHeight + visibleTracksCount.toInt() * clipPadding
        }
    }

    val playerState = PlayerState(engine)
    val dataSource = TimelineDataSource()
    val zoomState = TimelineZoomState()
    private val thumbnailsManager = ThumbnailsManager(engine, coroutineScope)

    private val page = engine.getCurrentPage()
    private val backgroundTrack = engine.block.getBackgroundTrack() ?: createBackgroundTrack()

    private var pageChildren: List<DesignBlock> = emptyList()

    fun getThumbnailProvider(designBlock: DesignBlock) = thumbnailsManager.getProvider(designBlock)

    fun refresh(events: List<DesignBlockEvent>) {
        val blocks = engine.block.getChildren(page)

        // If the block order has changed OR a block was destroyed or created, we refresh the entire timeline
        if (blocks != pageChildren || events.find {
                it.type == DesignBlockEvent.Type.CREATED || it.type == DesignBlockEvent.Type.DESTROYED
            } != null
        ) {
            pageChildren = blocks
            refresh()
        } else {
            // filter blocks that were updated into a set and then refresh them
            events.mapNotNull { event ->
                event.block.takeIf { event.type == DesignBlockEvent.Type.UPDATED }
            }.toSet().forEach { block ->
                dataSource.findClip(block)?.let { refresh(it.id, it) }
            }
        }

        updateSelection(engine.block.findAllSelected().firstOrNull())
        updateDuration()
        playerState.refresh()
    }

    fun refreshThumbnails() {
        dataSource.allClips().forEach { clip ->
            val width = zoomState.toDp(clip.duration)
            thumbnailsManager.refreshThumbnails(clip, width)
        }
    }

    fun onHistoryUpdated() {
        refreshThumbnails()
    }

    fun clampPlayheadPositionToSelectedClip() {
        val clip = selectedClip ?: return
        if (engine.block.isVisibleAtCurrentPlaybackTime(clip.id)) return
        val currentPlayheadPosition = playerState.playheadPosition
        val startTime = clip.timeOffset
        val clipIn = startTime + EPS_DURATION
        // Go back a tiny bit so that we’re at the end of this clip and not at the beginning of the next.
        val clipOut = startTime + clip.duration - EPS_DURATION

        val clampedTime =
            if (currentPlayheadPosition < clipIn) {
                clipIn
            } else if (currentPlayheadPosition > clipOut) {
                clipOut
            } else {
                // No need to clamp if clip is already within range
                return
            }

        playerState.setPlaybackTime(clampedTime)
    }

    private fun updateSelection(designBlock: DesignBlock?) {
        val selection =
            if (designBlock == null) {
                null
            } else {
                dataSource.findClip(designBlock)
            }

        if (selection != null && selection != selectedClip) {
            playerState.pause()
        }

        selectedClip = selection
    }

    private fun refresh() {
        dataSource.reset()
        pageChildren.forEach(::refresh)
    }

    private fun refresh(
        designBlock: DesignBlock,
        existingClip: Clip? = null,
    ) {
        if (!engine.block.isValid(designBlock)) {
            thumbnailsManager.destroyProvider(designBlock)
            return
        }

        if (designBlock == backgroundTrack) {
            engine.block.getChildren(designBlock).forEach {
                refresh(it, dataSource.findClip(it))
            }
            return
        }

        val blockType = engine.block.getType(designBlock)
        val fillType = engine.block.getFillType(designBlock)

        val clipType: ClipType
        var title = ""

        when {
            fillType == FillType.Video -> {
                clipType = ClipType.Video
            }

            fillType == FillType.Image -> {
                clipType =
                    when (engine.block.getKindEnum(designBlock)) {
                        BlockKind.Sticker -> ClipType.Sticker
                        else -> ClipType.Image
                    }
            }

            blockType == DesignBlockType.Graphic.key -> {
                clipType = ClipType.Shape
            }

            blockType == DesignBlockType.Text.key -> {
                clipType = ClipType.Text
            }

            blockType == DesignBlockType.Audio.key -> {
                clipType = ClipType.Audio
                title = runCatching { engine.block.getMetadata(designBlock, "name") }.getOrDefault("")
            }

            else -> {
                // Not every block in the engine has a timeline representation, so we just ignore other block types.
                return
            }
        }

        val fillId =
            if (engine.block.hasFill(designBlock)) {
                engine.block.getFill(designBlock)
            } else {
                null
            }
        val trimmableId = fillId ?: designBlock

        val shapeId =
            if (engine.block.hasShape(designBlock)) {
                engine.block.getShape(designBlock)
            } else {
                null
            }

        val blurId =
            if (engine.block.hasBlur(designBlock)) {
                engine.block.getBlur(designBlock)
            } else {
                null
            }

        val effectIds =
            if (engine.block.hasEffects(designBlock)) {
                engine.block.getEffects(designBlock)
            } else {
                null
            }

        val isInBackgroundTrack = engine.block.isParentBackgroundTrack(designBlock)

        val duration = engine.block.getDuration(designBlock).seconds
        val timeOffset = engine.block.getTimeOffset(designBlock).seconds

        val allowsTrimming = engine.block.hasTrim(trimmableId)

        val trimOffset =
            if (allowsTrimming) {
                // The trimOffset isn't known until the resource has loaded
                try {
                    engine.block.getTrimOffset(trimmableId).seconds
                } catch (ex: EngineException) {
                    0.seconds
                }
            } else {
                0.seconds
            }

        var footageDuration: Duration? = null
        val isMuted: Boolean
        val volume: Float
        var hasLoaded = true

        if (clipType == ClipType.Audio || clipType == ClipType.Video) {
            // The total duration isn't known until the resource has loaded
            runCatching {
                engine.block.getAVResourceTotalDuration(trimmableId).seconds
            }.onSuccess {
                footageDuration = it
                hasLoaded = true
            }.onFailure {
                footageDuration = duration
                hasLoaded = false
                // Currently, engine doesn't trigger an event when the resource is loaded. So, we force load and refresh explicitly
                // after waiting for it to load.
                coroutineScope.launch {
                    runCatching {
                        engine.block.forceLoadAVResource(trimmableId)
                        refresh(designBlock, dataSource.findClip(designBlock))
                    }
                }
            }
            isMuted = engine.block.isMuted(trimmableId)
            volume = engine.block.getVolume(trimmableId)
        } else {
            isMuted = false
            volume = 1.0f
        }

        val allowsSelecting = engine.block.isAllowedByScope(designBlock, Scope.EditorSelect)

        val clip =
            Clip(
                id = designBlock,
                clipType = clipType,
                trimmableId = trimmableId,
                fillId = fillId,
                shapeId = shapeId,
                blurId = blurId,
                effectIds = effectIds,
                title = title,
                duration = duration,
                footageDuration = footageDuration,
                timeOffset = timeOffset,
                allowsTrimming = allowsTrimming,
                allowsSelecting = allowsSelecting,
                trimOffset = trimOffset,
                isMuted = isMuted,
                volume = volume,
                isInBackgroundTrack = isInBackgroundTrack,
                hasLoaded = hasLoaded,
            )

        // Find which track the clip will go to
        val track =
            if (clip.isInBackgroundTrack) {
                dataSource.backgroundTrack
            } else if (existingClip != null) {
                dataSource.findTrack(existingClip)
            } else {
                Track()
            }

        updateClip(track, clip, existingClip)

        // If this is a freshly created non-background clip, we need to add the newly created track
        if (existingClip == null && !clip.isInBackgroundTrack) {
            if (clipType == ClipType.Audio) {
                dataSource.addAudioTrack(track)
            } else {
                dataSource.addTrack(track)
            }
        }
    }

    private fun updateClip(
        track: Track,
        newClip: Clip,
        existingClip: Clip?,
    ) {
        if (existingClip != null) {
            val index = track.clips.indexOf(existingClip)
            track.clips[index] = newClip
            if (existingClip.duration != newClip.duration) {
                // This means that the duration of the clip has been updated.
                // Currently, the thumbnails are refreshed when a history event is created.
                // In certain scenarios, it may happen that the duration of the clip is updated after the call to onHistoryUpdated(),
                // this results in thumbnails being generated for the previous known duration.
                // So, we force a refresh again here.
                thumbnailsManager.refreshThumbnails(clip = newClip, width = zoomState.toDp(newClip.duration))
            }
        } else {
            track.clips.add(newClip)
        }
    }

    private fun updateDuration() {
        totalDuration =
            if (dataSource.backgroundTrack.clips.isNotEmpty()) {
                engine.block.getDuration(backgroundTrack).seconds
            } else {
                // If background track is empty, iterate over all clips and make make sure they’re all accessible.
                dataSource.tracks.fold(0.seconds) { partialResult, track ->
                    // non video tracks only have one clip
                    val clip = track.clips.first()
                    maxOf(partialResult, clip.timeOffset + clip.duration)
                }
            }

        val oldDuration = engine.block.getDuration(page)
        // This check is important to avoid an infinite loop through the update events.
        if (totalDuration != oldDuration.seconds) {
            engine.block.setDuration(page, totalDuration.toDouble(DurationUnit.SECONDS))
        }
    }

    private fun createBackgroundTrack(): DesignBlock {
        val backgroundTrack = engine.block.create(DesignBlockType.Track)
        engine.block.appendChild(parent = page, child = backgroundTrack)
        engine.block.setAlwaysOnBottom(backgroundTrack, true)
        engine.block.fillParent(backgroundTrack)
        engine.block.setScopeEnabled(backgroundTrack, Scope.EditorSelect, false)
        return backgroundTrack
    }
}
