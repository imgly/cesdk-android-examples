package ly.img.cesdk.editorui.handler

import ly.img.cesdk.core.ui.EventsHandler
import ly.img.cesdk.core.ui.inject
import ly.img.cesdk.core.ui.register
import ly.img.cesdk.engine.bringForward
import ly.img.cesdk.engine.bringToFront
import ly.img.cesdk.engine.delete
import ly.img.cesdk.engine.duplicate
import ly.img.cesdk.engine.sendBackward
import ly.img.cesdk.engine.sendToBack
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.cesdk.editorui.BlockEvent.*

/**
 * Register events related to DesignBlocks.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 * @param fontFamilyMap Lambda returning the font family map
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.blockEvents(
    engine: () -> Engine,
    block: () -> DesignBlock
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnDelete> { engine.delete(block) }

    register<OnBackward> { engine.sendBackward(block) }

    register<OnDuplicate> { engine.duplicate(block) }

    register<OnForward> { engine.bringForward(block) }

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


