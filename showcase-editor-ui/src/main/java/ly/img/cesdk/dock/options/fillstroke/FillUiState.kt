package ly.img.cesdk.dock.options.fillstroke

import androidx.compose.ui.graphics.Color
import ly.img.cesdk.core.engine.BlockType
import ly.img.cesdk.editorui.R
import ly.img.cesdk.engine.Block
import ly.img.cesdk.engine.Fill
import ly.img.cesdk.engine.getFillInfo
import ly.img.cesdk.engine.getFillType
import ly.img.cesdk.engine.getGradientFillType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientType

data class FillUiState(
    val isFillEnabled: Boolean,
    val supportFillTypes: Boolean,
    val isGradientEnabled: Boolean,
    val colorPalette: List<Color>,
    val fillTypeRes: Int,
    val fillState: Fill?,
)

internal fun createFillUiState(block: Block, engine: Engine, colorPalette: List<Color>): FillUiState {
    val designBlock = block.designBlock
    val isEnabled = engine.block.isFillEnabled(designBlock)
    val fillType = engine.block.getFillType(designBlock)
    val gradientType = engine.block.getGradientFillType(designBlock)
    val isGradientEnabled = true
    val supportFillTypes = block.type != BlockType.Text
    return FillUiState(
        colorPalette = colorPalette,
        isFillEnabled = isEnabled,
        supportFillTypes = supportFillTypes,
        isGradientEnabled = isGradientEnabled,
        fillState = checkNotNull(engine.block.getFillInfo(designBlock)),
        fillTypeRes = getFillTypeRes(fillType.takeIf { isEnabled }, gradientType)
    )
}

private fun getFillTypeRes(fileType: FillType?, gradientType: GradientType?): Int {
    return if (fileType == null) {
        R.string.cesdk_fill_none
    } else when (fileType) {
        FillType.SOLID -> R.string.cesdk_fill_solid
        FillType.GRADIENT -> when (gradientType) {
            GradientType.RADIAL -> R.string.cesdk_fill_type_gradient_radial
            GradientType.LINEAR -> R.string.cesdk_fill_type_gradient_linear
            GradientType.CONICAL -> R.string.cesdk_fill_type_gradient_conical
            else -> throw IllegalArgumentException("Unknown gradient fill type: $gradientType")
        }

        else -> throw IllegalArgumentException("Unknown fill type: $fileType")
    }
}