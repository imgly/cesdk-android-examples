package ly.img.editor.base.dock.options.shapeoptions

import ly.img.editor.base.engine.getSmallerSide
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.ShapeType

sealed interface ShapeOptionsUiState

interface RoundableShapeUiState {
    val cornerRadius: Float
}

data class RectShapeUiState(
    val cornerRadiusTopLeft: Float,
    val cornerRadiusTopRight: Float,
    val cornerRadiusBottomLeft: Float,
    val cornerRadiusBottomRight: Float,
) : ShapeOptionsUiState

data class StarShapeOptionsUiState(
    val points: Float,
    val innerDiameter: Float,
) : ShapeOptionsUiState

data class PolygonShapeOptionsUiState(
    val sides: Float,
    override val cornerRadius: Float,
) : ShapeOptionsUiState,
    RoundableShapeUiState

data class LineShapeOptionsUiState(
    val width: Float,
) : ShapeOptionsUiState

internal fun createShapeOptionsUiState(
    designBlock: DesignBlock,
    engine: Engine,
): ShapeOptionsUiState {
    val shape = engine.block.getShape(designBlock)
    return when (ShapeType.get(engine.block.getType(shape))) {
        ShapeType.Star ->
            StarShapeOptionsUiState(
                points = engine.block.getInt(shape, "shape/star/points").toFloat(),
                innerDiameter = engine.block.getFloat(shape, "shape/star/innerDiameter"),
            )

        ShapeType.Polygon ->
            PolygonShapeOptionsUiState(
                sides = engine.block.getInt(shape, "shape/polygon/sides").toFloat(),
                cornerRadius =
                    engine.block.getFloat(
                        shape,
                        "shape/polygon/cornerRadius",
                    ) / (engine.block.getSmallerSide(designBlock) / 2f),
            )

        ShapeType.Line ->
            LineShapeOptionsUiState(
                width = engine.block.getFrameHeight(designBlock),
            )

        ShapeType.Rect -> {
            val maxRadius = engine.block.getSmallerSide(designBlock) / 2f
            RectShapeUiState(
                cornerRadiusTopLeft = engine.block.getFloat(shape, "shape/rect/cornerRadiusTL") / maxRadius,
                cornerRadiusTopRight = engine.block.getFloat(shape, "shape/rect/cornerRadiusTR") / maxRadius,
                cornerRadiusBottomLeft = engine.block.getFloat(shape, "shape/rect/cornerRadiusBL") / maxRadius,
                cornerRadiusBottomRight = engine.block.getFloat(shape, "shape/rect/cornerRadiusBR") / maxRadius,
            )
        }

        else -> throw IllegalArgumentException(
            "Options are only supported for star, polygon, line shape types and rects.",
        )
    }
}
