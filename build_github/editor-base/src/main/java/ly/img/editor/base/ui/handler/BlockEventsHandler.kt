package ly.img.editor.base.ui.handler

import ly.img.editor.base.engine.delete
import ly.img.editor.base.engine.duplicate
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

    fun onBackward(designBlock: DesignBlock) {
        engine.block.sendBackward(designBlock)
        engine.editor.addUndoStep()
    }

    fun onForward(designBlock: DesignBlock) {
        engine.block.bringForward(designBlock)
        engine.editor.addUndoStep()
    }

    register<OnDelete> { engine.delete(block) }

    register<OnDeleteNonSelected> { engine.delete(it.block) }

    register<OnDuplicate> { engine.duplicate(block) }

    register<OnDuplicateNonSelected> {
        engine.block.duplicate(it.block)
        engine.editor.addUndoStep()
    }

    register<OnBackward> {
        onBackward(block)
    }

    register<OnBackwardNonSelected> {
        onBackward(it.block)
    }

    register<OnForward> {
        onForward(block)
    }

    register<OnForwardNonSelected> {
        onForward(it.block)
    }

    register<ToBack> {
        engine.block.sendToBack(block)
        engine.editor.addUndoStep()
    }

    register<ToFront> {
        engine.block.bringToFront(block)
        engine.editor.addUndoStep()
    }

    register<OnChangeFinish> { engine.editor.addUndoStep() }

    register<OnChangeBlendMode> {
        if (engine.block.getBlendMode(block) != it.blendMode) {
            engine.block.setBlendMode(block, it.blendMode)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeOpacity> { engine.block.setOpacity(block, it.opacity) }
}
