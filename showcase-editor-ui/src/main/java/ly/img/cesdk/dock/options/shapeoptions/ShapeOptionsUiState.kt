package ly.img.cesdk.dock.options.shapeoptions

import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
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
    return when (engine.block.getType(designBlock)) {
        DesignBlockType.STAR_SHAPE.key -> StarShapeOptionsUiState(
            points = engine.block.getInt(designBlock, "shapes/star/points").toFloat(),
            innerDiameter = engine.block.getFloat(designBlock, "shapes/star/innerDiameter")
        )

        DesignBlockType.POLYGON_SHAPE.key -> PolygonShapeOptionsUiState(
            sides = engine.block.getInt(designBlock, "shapes/polygon/sides").toFloat()
        )

        DesignBlockType.LINE_SHAPE.key -> LineShapeOptionsUiState(
            width = engine.block.getFrameHeight(designBlock)
        )

        else -> throw IllegalArgumentException("Options are only supported for star & polygon shapes.")
    }
}