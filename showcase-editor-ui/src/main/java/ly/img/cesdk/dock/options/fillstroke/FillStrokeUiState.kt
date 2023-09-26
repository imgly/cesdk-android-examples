package ly.img.cesdk.dock.options.fillstroke

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.cesdk.editorui.Block
import ly.img.cesdk.editorui.R
import ly.img.cesdk.engine.getFillType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

data class FillStrokeUiState(
    @StringRes val titleRes: Int,
    val fillUiState: FillUiState?,
    val strokeUiState: StrokeUiState?
)

internal fun createFillStrokeUiState(block: Block, engine: Engine, colorPalette: List<Color>): FillStrokeUiState {
    val designBlock = block.designBlock
    val fillType = engine.block.getFillType(designBlock)
    val hasSolidOrGradientFill =
        (fillType == DesignBlockType.COLOR_FILL || fillType == DesignBlockType.LINEAR_GRADIENT_FILL || fillType == DesignBlockType.RADIAL_GRADIENT_FILL || fillType == DesignBlockType.CONICAL_GRADIENT_FILL)
    val hasStroke = engine.block.hasStroke(designBlock)
    val palette = colorPalette.take(6)
    return FillStrokeUiState(
        titleRes = getFillStrokeTitleRes(hasSolidOrGradientFill, hasStroke),
        fillUiState = if (hasSolidOrGradientFill) createFillUiState(block, engine, palette) else null,
        strokeUiState = if (hasStroke) createStrokeUiState(block, engine, palette) else null
    )
}

fun getFillStrokeTitleRes(hasSolidOrGradientFill: Boolean, hasStroke: Boolean): Int {
    return if (hasSolidOrGradientFill && hasStroke) {
        R.string.cesdk_fill_and_stroke
    } else if (hasSolidOrGradientFill) {
        R.string.cesdk_fill
    } else if (hasStroke) {
        R.string.cesdk_stroke
    } else {
        throw IllegalArgumentException("getFillStrokeTitleRes() should not be called when hasFill and hasStroke both are false.")
    }
}