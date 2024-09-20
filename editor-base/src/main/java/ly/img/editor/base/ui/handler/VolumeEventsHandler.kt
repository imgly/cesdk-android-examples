package ly.img.editor.base.ui.handler

import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

/**
 * Register events related to Volume.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.volumeEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
) {
    val engine by inject(engine)
    val block by inject(block)

    register<BlockEvent.OnVolumeChange> {
        val volume = it.volume
        val volumeBlock =
            if (DesignBlockType.get(engine.block.getType(block)) == DesignBlockType.Audio) {
                block
            } else {
                engine.block.getFill(block)
            }
        engine.block.setVolume(volumeBlock, volume)
        engine.block.setMuted(volumeBlock, volume == 0f)
    }

    register<BlockEvent.OnToggleMute> {
        val volumeBlock =
            if (DesignBlockType.get(engine.block.getType(block)) == DesignBlockType.Audio) {
                block
            } else {
                engine.block.getFill(block)
            }
        val volume = engine.block.getVolume(volumeBlock)
        val isMuted = engine.block.isMuted(volumeBlock)
        if (volume == 0f && isMuted) {
            engine.block.setVolume(volumeBlock, 0.2f)
        }
        engine.block.setMuted(volumeBlock, !isMuted)
        engine.editor.addUndoStep()
    }
}
