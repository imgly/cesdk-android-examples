package ly.img.cesdk.dock.options.shapeoptions

import ly.img.cesdk.engine.ShapeType
import ly.img.cesdk.engine.getShapeType
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

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
    return when (engine.block.getShapeType(designBlock)) {
        ShapeType.Star -> StarShapeOptionsUiState(
            points = engine.block.getInt(shape, "shapes/star/points").toFloat(),
            innerDiameter = engine.block.getFloat(shape, "shapes/star/innerDiameter")
        )

        ShapeType.Polygon -> PolygonShapeOptionsUiState(
            sides = engine.block.getInt(shape, "shapes/polygon/sides").toFloat()
        )

        ShapeType.Line -> LineShapeOptionsUiState(
            width = engine.block.getFrameHeight(designBlock)
        )

        ShapeType.Other -> throw IllegalArgumentException("Options are only supported for star, polygon, and line shape types.")
    }
}