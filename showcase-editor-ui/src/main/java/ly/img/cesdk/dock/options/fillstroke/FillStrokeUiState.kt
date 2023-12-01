package ly.img.cesdk.dock.options.fillstroke

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import ly.img.engine.FillType
import ly.img.cesdk.editorui.Block
import ly.img.cesdk.editorui.R
import ly.img.cesdk.engine.getFillType
import ly.img.engine.Engine
import kotlin.reflect.KProperty

data class FillStrokeUiState(
    @StringRes val titleRes: Int,
    val fillUiState: FillUiState?,
    val strokeUiState: StrokeUiState?
) {
    companion object Factory {
        internal fun getFillStrokeTitleRes(hasSolidOrGradientFill: Boolean, hasStroke: Boolean) = when {
            hasSolidOrGradientFill && hasStroke -> {
                R.string.cesdk_fill_and_stroke
            }
            hasSolidOrGradientFill -> {
                R.string.cesdk_fill
            }
            hasStroke -> {
                R.string.cesdk_stroke
            }
            else -> {
                throw IllegalArgumentException("getFillStrokeTitleRes() should not be called when hasFill and hasStroke both are false.")
            }
        }
        fun create(block: Block, engine: Engine, colorPalette: List<Color>) : FillStrokeUiState {
            val designBlock = block.designBlock
            val fillType = engine.block.getFillType(designBlock)
            val hasSolidOrGradientFill = (
                fillType == FillType.Color
                    || fillType == FillType.LinearGradient
                    || fillType == FillType.RadialGradient
                    || fillType == FillType.ConicalGradient
                )
            val hasStroke = engine.block.hasStroke(designBlock)
            val palette = colorPalette.take(6)
            return FillStrokeUiState(
                titleRes = getFillStrokeTitleRes(hasSolidOrGradientFill, hasStroke),
                fillUiState = if (hasSolidOrGradientFill) createFillUiState(block, engine, palette) else null,
                strokeUiState = if (hasStroke) createStrokeUiState(block, engine, palette) else null
            )
        }
    }
}