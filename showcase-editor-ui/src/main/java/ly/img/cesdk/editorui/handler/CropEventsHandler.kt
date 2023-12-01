package ly.img.cesdk.editorui.handler

import ly.img.cesdk.dock.options.crop.getNormalizedDegrees
import ly.img.cesdk.dock.options.crop.getRotationDegrees
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.core.ui.EventsHandler
import ly.img.cesdk.core.ui.inject
import ly.img.cesdk.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

/**
 * Register events related to Crop.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 * @param fontFamilyMap Lambda returning the font family map
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.cropEvents(
    engine: () -> Engine,
    block: () -> DesignBlock
) {
    val engine by inject(engine)
    val block by inject(block)

    fun onCropRotateDegrees(scaleRatio: Float, angle: Float, addUndo: Boolean = true) {
        val cropRotationRadians = angle * (Math.PI.toFloat() / 180f)
        engine.block.setCropRotation(block, cropRotationRadians)
        engine.block.adjustCropToFillFrame(block, scaleRatio)
        if (addUndo) {
            engine.editor.addUndoStep()
        }
    }

    register<BlockEvent.OnResetCrop> {
        engine.block.resetCrop(block)
        engine.editor.addUndoStep()
    }
    register<BlockEvent.OnFlipCropHorizontal> {
        engine.block.flipCropHorizontal(block)
        engine.editor.addUndoStep()
    }
    register<BlockEvent.OnCropRotate> {
        val normalizedDegrees = getNormalizedDegrees(engine, block, offset = -90)
        onCropRotateDegrees(it.scaleRatio, normalizedDegrees)
    }
    register<BlockEvent.OnCropStraighten> {
        val rotationDegrees = getRotationDegrees(engine, block) + it.angle
        onCropRotateDegrees(it.scaleRatio, rotationDegrees, addUndo = false)
    }
}