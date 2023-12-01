package ly.img.cesdk.dock.options.fillstroke

import androidx.compose.ui.graphics.Color
import ly.img.cesdk.core.engine.BlockType
import ly.img.engine.FillType
import ly.img.cesdk.editorui.Block
import ly.img.cesdk.editorui.R
import ly.img.cesdk.engine.Fill
import ly.img.cesdk.engine.getFillInfo
import ly.img.cesdk.engine.getFillType
import ly.img.engine.Engine

data class FillUiState(
    val isFillEnabled: Boolean,
    val supportFillTypes: Boolean,
    val colorPalette: List<Color>,
    val fillTypeRes: Int,
    val fillState: Fill?,
)

internal fun createFillUiState(block: Block, engine: Engine, colorPalette: List<Color>): FillUiState {
    val designBlock = block.designBlock
    val isEnabled = engine.block.isFillEnabled(designBlock)
    val fillType = engine.block.getFillType(designBlock)
    val supportFillTypes = block.type != BlockType.Text
    return FillUiState(
        colorPalette = colorPalette,
        isFillEnabled = isEnabled,
        supportFillTypes = supportFillTypes,
        fillState = checkNotNull(engine.block.getFillInfo(designBlock)),
        fillTypeRes = getFillTypeRes(fillType.takeIf { isEnabled })
    )
}

private fun getFillTypeRes(fillType: FillType?): Int {
    return if (fillType == null) {
        R.string.cesdk_fill_none
    } else when (fillType) {
        FillType.Color -> R.string.cesdk_fill_solid
        FillType.RadialGradient -> R.string.cesdk_fill_type_gradient_radial
        FillType.LinearGradient -> R.string.cesdk_fill_type_gradient_linear
        FillType.ConicalGradient -> R.string.cesdk_fill_type_gradient_conical
        else -> throw IllegalArgumentException("Unknown fill type: $fillType")
    }
}