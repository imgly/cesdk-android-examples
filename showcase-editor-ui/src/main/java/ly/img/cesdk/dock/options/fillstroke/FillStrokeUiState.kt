package ly.img.cesdk.dock.options.fillstroke

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.cesdk.editorui.R
import ly.img.cesdk.engine.Block
import ly.img.engine.Engine

data class FillStrokeUiState(
    @StringRes val titleRes: Int,
    val fillUiState: FillUiState?,
    val strokeUiState: StrokeUiState?
)

internal fun createFillStrokeUiState(block: Block, engine: Engine, colorPalette: List<Color>): FillStrokeUiState {
    val designBlock = block.designBlock
    val hasFill = engine.block.hasFill(designBlock)
    val hasStroke = engine.block.hasStroke(designBlock)
    val palette = colorPalette.take(6)
    return FillStrokeUiState(
        titleRes = getFillStrokeTitleRes(hasFill, hasStroke),
        fillUiState = if (hasFill) createFillUiState(designBlock, engine, palette) else null,
        strokeUiState = if (hasStroke) createStrokeUiState(block, engine, palette) else null
    )
}

fun getFillStrokeTitleRes(hasFill: Boolean, hasStroke: Boolean): Int {
    return if (hasFill && hasStroke) {
        R.string.cesdk_fill_and_stroke
    } else if (hasFill) {
        R.string.cesdk_fill
    } else if (hasStroke) {
        R.string.cesdk_stroke
    } else {
        throw IllegalArgumentException("getFillStrokeTitleRes() should not be called when hasFill and hasStroke both are false.")
    }
}