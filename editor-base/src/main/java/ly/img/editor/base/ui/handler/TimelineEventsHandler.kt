package ly.img.editor.base.ui.handler

import ly.img.editor.base.R
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.clip.ClipType
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.editor.base.timeline.state.TimelineState
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.getBackgroundTrack
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Register events related to Timeline.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.timelineEvents(
    engine: () -> Engine,
    timelineState: () -> TimelineState,
    showError: (Int) -> Unit,
) {
    val engine by inject(engine)
    val timelineState by inject(timelineState)

    register<BlockEvent.OnToggleBackgroundTrackAttach> {
        timelineState.selectedClip?.let { clip ->
            val id = clip.id
            if (clip.isInBackgroundTrack) {
                engine.block.appendChild(parent = checkNotNull(engine.scene.getCurrentPage()), child = id)
            } else {
                val insertedBlockTimeOffset = engine.block.getTimeOffset(id)
                val backgroundTrack = checkNotNull(engine.block.getBackgroundTrack())
                val backgroundTrackChildren = engine.block.getChildren(backgroundTrack)

                // Find the slot in the background track closest to the current time offset.
                var insertionIndex = backgroundTrackChildren.size
                for ((index, child) in backgroundTrackChildren.withIndex()) {
                    val timeOffset = engine.block.getTimeOffset(child)
                    val duration = engine.block.getDuration(child)
                    if (insertedBlockTimeOffset < timeOffset + duration / 2) {
                        insertionIndex = index
                        break
                    }
                }

                engine.block.insertChild(parent = backgroundTrack, child = id, index = insertionIndex)
                if (engine.block.isScopeEnabled(id, Scope.LayerCrop) && engine.block.getKindEnum(id) != BlockKind.Sticker) {
                    engine.block.resetCrop(id)
                    engine.block.fillParent(id)
                }
            }
            engine.editor.addUndoStep()
        }
    }

    register<BlockEvent.OnDeselect> {
        timelineState.selectedClip?.let { clip ->
            engine.block.setSelected(clip.id, false)
        }
    }

    register<BlockEvent.OnToggleSelectBlock> {
        // deselect previously selected block before selecting new one
        val previouslySelectedBlock = timelineState.selectedClip?.id
        if (previouslySelectedBlock != null && it.block != previouslySelectedBlock) {
            engine.block.setSelected(previouslySelectedBlock, false)
        }

        engine.block.setSelected(it.block, !engine.block.isSelected(it.block))
    }

    fun setDuration(
        clip: Clip,
        duration: Duration,
    ) {
        val durationInSeconds = duration.toDouble(DurationUnit.SECONDS)
        engine.block.setDuration(clip.id, durationInSeconds)
        if (clip.clipType == ClipType.Audio || clip.clipType == ClipType.Video) {
            engine.block.setTrimLength(clip.trimmableId, durationInSeconds)
        }
    }

    fun setTimeOffset(
        designBlock: DesignBlock,
        timeOffset: Duration,
    ) {
        engine.block.setTimeOffset(designBlock, timeOffset.toDouble(DurationUnit.SECONDS))
    }

    fun setTrimOffset(
        designBlock: DesignBlock,
        trimOffset: Duration,
    ) {
        engine.block.setTrimOffset(designBlock, trimOffset.toDouble(DurationUnit.SECONDS))
    }

    register<BlockEvent.OnUpdateTrim> {
        val selectedClip = checkNotNull(timelineState.selectedClip)
        val selectedBlock = selectedClip.id
        setTimeOffset(selectedBlock, it.timeOffset)
        // `OnUpdateTrim` is called in general for updating timeOffset, trimOffset, and duration simultaneously
        // Don't update trimOffset if the clip doesn't support trimming
        if (selectedClip.allowsTrimming) {
            setTrimOffset(selectedClip.trimmableId, it.trimOffset)
        }
        setDuration(selectedClip, it.duration)
        engine.editor.addUndoStep()
    }

    register<BlockEvent.OnUpdateTimeOffset> {
        val selectedBlock = checkNotNull(timelineState.selectedClip?.id)
        setTimeOffset(selectedBlock, it.timeOffset)
        engine.editor.addUndoStep()
    }

    register<BlockEvent.OnUpdateDuration> {
        val selectedClip = checkNotNull(timelineState.selectedClip)
        setDuration(selectedClip, it.duration)
        engine.editor.addUndoStep()
    }

    register<BlockEvent.OnSplit> {
        val playheadPosition = timelineState.playerState.playheadPosition
        val selectedClip = checkNotNull(timelineState.selectedClip)
        val originalClipDuration = selectedClip.duration
        val absoluteStartTime = selectedClip.timeOffset
        val minClipDuration = TimelineConfiguration.minClipDuration
        val absoluteEndTime = absoluteStartTime + originalClipDuration

        if (playheadPosition < absoluteStartTime || playheadPosition > absoluteEndTime) {
            showError(R.string.ly_img_editor_error_split_out_of_range)
            return@register
        }

        if (playheadPosition < absoluteStartTime + minClipDuration ||
            (playheadPosition > absoluteEndTime - minClipDuration)
        ) {
            showError(R.string.ly_img_editor_error_split_short_duration)
            return@register
        }

        val secondClipId = engine.block.duplicate(selectedClip.id)
        // We need to set the trim to the fill on videos but to the block itself for everything else.
        val secondClipTrimmableId =
            if (selectedClip.clipType == ClipType.Video) {
                engine.block.getFill(secondClipId)
            } else {
                secondClipId
            }

        val firstClipDuration = playheadPosition - absoluteStartTime
        val secondClipDuration = (originalClipDuration - firstClipDuration).toDouble(DurationUnit.SECONDS)
        engine.block.setDuration(secondClipId, secondClipDuration)

        if (selectedClip.allowsTrimming) {
            // Get the existing trim offset and add the duration of the first clip, then assign it to the second clip.
            val oldTrimOffset = selectedClip.trimOffset
            engine.block.setTrimOffset(
                block = secondClipTrimmableId,
                offset = (oldTrimOffset + firstClipDuration).toDouble(DurationUnit.SECONDS),
            )
        }

        // Adjust the first clip
        engine.block.setDuration(selectedClip.id, firstClipDuration.toDouble(DurationUnit.SECONDS))

        if (!selectedClip.isInBackgroundTrack) {
            engine.block.setTimeOffset(
                block = secondClipId,
                offset = (selectedClip.timeOffset + firstClipDuration).toDouble(DurationUnit.SECONDS),
            )
        }

        engine.block.setSelected(selectedClip.id, false)
        engine.block.setSelected(secondClipId, true)
        engine.editor.addUndoStep()
    }

    register<BlockEvent.OnReorder> {
        // optimistically reorder manually
        val backgroundClips = timelineState.dataSource.backgroundTrack.clips
        val oldIndex =
            backgroundClips.indexOfFirst { clip ->
                clip.id == it.block
            }
        backgroundClips.add(it.newIndex, backgroundClips.removeAt(oldIndex))

        engine.block.insertChild(
            parent = checkNotNull(engine.block.getBackgroundTrack()),
            child = it.block,
            index = it.newIndex,
        )
        engine.editor.addUndoStep()
    }
}
