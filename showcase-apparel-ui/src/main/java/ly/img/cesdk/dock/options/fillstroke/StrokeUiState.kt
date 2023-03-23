package ly.img.cesdk.dock.options.fillstroke

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.cesdk.engine.Block
import ly.img.cesdk.engine.BlockType
import ly.img.cesdk.engine.getStrokeColor
import ly.img.engine.Engine
import kotlin.math.ln

data class StrokeUiState(
    val isStrokeEnabled: Boolean,
    val strokeColor: Color,
    val strokeWidth: Float,
    @StringRes val strokeStyleRes: Int,
    val isStrokePositionEnabled: Boolean,
    @StringRes val strokePositionRes: Int,
    @StringRes val strokeJoinRes: Int
)

internal fun createStrokeUiState(block: Block, engine: Engine): StrokeUiState {
    val designBlock = block.designBlock
    val isEnabled = engine.block.isStrokeEnabled(designBlock)
    return StrokeUiState(
        isStrokeEnabled = isEnabled,
        strokeColor = checkNotNull(engine.getStrokeColor(designBlock)),
        strokeWidth = engine.block.getStrokeWidth(designBlock).takeIf { it > 0 }?.let { ln(it) } ?: STROKE_WIDTH_LOWER_BOUND,
        strokeStyleRes = engine.block.getStrokeStyle(designBlock).getText(),
        isStrokePositionEnabled = block.type != BlockType.Text,
        strokePositionRes = engine.block.getStrokePosition(designBlock).getText(),
        strokeJoinRes = engine.block.getStrokeCornerGeometry(designBlock).getText()
    )
}

const val STROKE_WIDTH_UPPER_BOUND = 3f
const val STROKE_WIDTH_LOWER_BOUND = -3f