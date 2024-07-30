package ly.img.editor.base.ui.handler

import ly.img.editor.base.engine.bringForward
import ly.img.editor.base.engine.bringToFront
import ly.img.editor.base.engine.delete
import ly.img.editor.base.engine.duplicate
import ly.img.editor.base.engine.sendBackward
import ly.img.editor.base.engine.sendToBack
import ly.img.editor.base.ui.BlockEvent.OnBackward
import ly.img.editor.base.ui.BlockEvent.OnBackwardNonSelected
import ly.img.editor.base.ui.BlockEvent.OnChangeBlendMode
import ly.img.editor.base.ui.BlockEvent.OnChangeFinish
import ly.img.editor.base.ui.BlockEvent.OnChangeOpacity
import ly.img.editor.base.ui.BlockEvent.OnDelete
import ly.img.editor.base.ui.BlockEvent.OnDeleteNonSelected
import ly.img.editor.base.ui.BlockEvent.OnDuplicate
import ly.img.editor.base.ui.BlockEvent.OnDuplicateNonSelected
import ly.img.editor.base.ui.BlockEvent.OnForward
import ly.img.editor.base.ui.BlockEvent.OnForwardNonSelected
import ly.img.editor.base.ui.BlockEvent.ToBack
import ly.img.editor.base.ui.BlockEvent.ToFront
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

/**
 * Register events related to DesignBlocks.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.blockEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnDelete> { engine.delete(block) }

    register<OnDeleteNonSelected> { engine.delete(it.block) }

    register<OnBackward> { engine.sendBackward(block) }

    register<OnBackwardNonSelected> { engine.sendBackward(it.block) }

    register<OnDuplicate> { engine.duplicate(block) }

    register<OnDuplicateNonSelected> {
        engine.block.duplicate(it.block)
        engine.editor.addUndoStep()
    }

    register<OnForward> { engine.bringForward(block) }

    register<OnForwardNonSelected> { engine.bringForward(it.block) }

    register<ToBack> { engine.sendToBack(block) }

    register<ToFront> { engine.bringToFront(block) }

    register<OnChangeFinish> { engine.editor.addUndoStep() }

    register<OnChangeBlendMode> {
        if (engine.block.getBlendMode(block) != it.blendMode) {
            engine.block.setBlendMode(block, it.blendMode)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeOpacity> { engine.block.setOpacity(block, it.opacity) }
}
