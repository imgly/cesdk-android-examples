package ly.img.cesdk.dock.options.shapeoptions

import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.ShapeType

sealed interface ShapeOptionsUiState

data class StarShapeOptionsUiState(
    val points: Float,
    val innerDiameter: Float
) : ShapeOptionsUiState

data class PolygonShapeOptionsUiState(
    val sides: Float
) : ShapeOptionsUiState

data class LineShapeOptionsUiState(
    val width: Float
) : ShapeOptionsUiState

internal fun createShapeOptionsUiState(designBlock: DesignBlock, engine: Engine): ShapeOptionsUiState {
    val shape = engine.block.getShape(designBlock)
    return when (ShapeType.get(engine.block.getType(shape))) {
        ShapeType.Star -> StarShapeOptionsUiState(
            points = engine.block.getInt(shape, "shape/star/points").toFloat(),
            innerDiameter = engine.block.getFloat(shape, "shape/star/innerDiameter")
        )

        ShapeType.Polygon -> PolygonShapeOptionsUiState(
            sides = engine.block.getInt(shape, "shape/polygon/sides").toFloat()
        )

        ShapeType.Line -> LineShapeOptionsUiState(
            width = engine.block.getFrameHeight(designBlock)
        )

        else -> throw IllegalArgumentException("Options are only supported for star, polygon, and line shape types.")
    }
}