package ly.img.cesdk.editorui.handler

import ly.img.cesdk.core.ui.EventsHandler
import ly.img.cesdk.core.ui.inject
import ly.img.cesdk.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.cesdk.editorui.BlockEvent.*

@Suppress("NAME_SHADOWING")
fun EventsHandler.shapeOptionEvents(
    engine: () -> Engine,
    block: () -> DesignBlock
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnChangeStarInnerDiameter> { engine.block.setFloat(engine.block.getShape(block), "shape/star/innerDiameter", it.diameter) }
    register<OnChangeStarPoints> { engine.block.setInt(engine.block.getShape(block), "shape/star/points", it.points.toInt()) }
    register<OnChangePolygonSides> { engine.block.setInt(engine.block.getShape(block), "shape/polygon/sides", it.sides.toInt())}
}