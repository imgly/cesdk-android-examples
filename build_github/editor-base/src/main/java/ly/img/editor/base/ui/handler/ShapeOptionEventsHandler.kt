package ly.img.editor.base.ui.handler

import ly.img.editor.base.engine.getSmallerSide
import ly.img.editor.base.ui.BlockEvent.*
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import kotlin.math.roundToInt

@Suppress("NAME_SHADOWING")
fun EventsHandler.shapeOptionEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnChangeStarInnerDiameter> {
        engine.block.setFloat(engine.block.getShape(block), "shape/star/innerDiameter", it.diameter)
    }
    register<OnChangeStarPoints> {
        engine.block.setInt(engine.block.getShape(block), "shape/star/points", it.points.toInt())
    }
    register<OnChangePolygonSides> {
        engine.block.setInt(engine.block.getShape(block), "shape/polygon/sides", it.sides.toInt())
    }
    register<OnChangePolygonCornerRadius> {
        engine.block.setInt(
            engine.block.getShape(block),
            "shape/polygon/cornerRadius",
            (it.sides * engine.block.getSmallerSide(block) / 2f).roundToInt(),
        )
    }

    register<OnChangeRectCornerRadius> {
        val shape = engine.block.getShape(block)
        val maxRadius = engine.block.getSmallerSide(block) / 2f
        engine.block.setInt(shape, "shape/rect/cornerRadiusTL", (it.topLeft * maxRadius).roundToInt())
        engine.block.setInt(shape, "shape/rect/cornerRadiusTR", (it.topRight * maxRadius).roundToInt())
        engine.block.setInt(shape, "shape/rect/cornerRadiusBL", (it.bottomLeft * maxRadius).roundToInt())
        engine.block.setInt(shape, "shape/rect/cornerRadiusBR", (it.bottomRight * maxRadius).roundToInt())
    }
}
