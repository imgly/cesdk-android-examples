package ly.img.cesdk.editorui.handler

import ly.img.cesdk.core.ui.EventsHandler
import ly.img.cesdk.core.ui.inject
import ly.img.cesdk.core.ui.register
import ly.img.cesdk.engine.toEngineColor
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.StrokeCornerGeometry
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle
import ly.img.cesdk.editorui.BlockEvent.*
import kotlin.math.exp

@Suppress("NAME_SHADOWING")
fun EventsHandler.strokeEvents(
    engine: () -> Engine,
    block: () -> DesignBlock
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnChangeStrokeJoin> {
        val strokeJoinEnum = StrokeCornerGeometry.valueOf(it.join)
        if (engine.block.getStrokeCornerGeometry(block) != strokeJoinEnum) {
            engine.block.setStrokeCornerGeometry(block, strokeJoinEnum)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeStrokePosition> {
        val strokePositionEnum = StrokePosition.valueOf(it.position)
        if (engine.block.getStrokePosition(block) != strokePositionEnum) {
            engine.block.setStrokePosition(block, strokePositionEnum)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeStrokeStyle> {
        val strokeStyleEnum = StrokeStyle.valueOf(it.style)
        if (engine.block.getStrokeStyle(block) != strokeStyleEnum) {
            engine.block.setStrokeStyle(block, strokeStyleEnum)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeStrokeWidth> {
        engine.block.setStrokeWidth(block, exp(it.width.toDouble()).toFloat())
    }

    register<OnChangeStrokeColor> {
        engine.block.setStrokeEnabled(block, true)
        engine.block.setStrokeColor(block, it.color.toEngineColor())
    }
    register<OnDisableStroke> {
        val isEnabled = engine.block.isStrokeEnabled(block)
        if (isEnabled) {
            engine.block.setStrokeEnabled(block, false)
            engine.editor.addUndoStep()
        }
    }
}