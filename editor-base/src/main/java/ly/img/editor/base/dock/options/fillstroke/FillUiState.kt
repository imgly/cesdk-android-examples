package ly.img.editor.base.dock.options.fillstroke

import androidx.compose.ui.graphics.Color
import ly.img.editor.base.R
import ly.img.editor.base.ui.Block
import ly.img.editor.core.component.data.Fill
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.getFill
import ly.img.editor.core.ui.engine.getFillType
import ly.img.engine.Engine
import ly.img.engine.FillType

data class FillUiState(
    val isFillEnabled: Boolean,
    val supportFillTypes: Boolean,
    val colorPalette: List<Color>,
    val fillTypeRes: Int,
    val fillState: Fill?,
)

internal fun createFillUiState(
    block: Block,
    engine: Engine,
    colorPalette: List<Color>,
): FillUiState {
    val designBlock = block.designBlock
    val isEnabled = engine.block.isFillEnabled(designBlock)
    val fillType = engine.block.getFillType(designBlock)
    val supportFillTypes = block.type != BlockType.Text
    return FillUiState(
        colorPalette = colorPalette,
        isFillEnabled = isEnabled,
        supportFillTypes = supportFillTypes,
        fillState = checkNotNull(engine.getFill(designBlock)),
        fillTypeRes = getFillTypeRes(fillType.takeIf { isEnabled }),
    )
}

private fun getFillTypeRes(fillType: FillType?): Int {
    return if (fillType == null) {
        R.string.ly_img_editor_fill_none
    } else {
        when (fillType) {
            FillType.Color -> R.string.ly_img_editor_fill_solid
            FillType.RadialGradient -> R.string.ly_img_editor_fill_type_gradient_radial
            FillType.LinearGradient -> R.string.ly_img_editor_fill_type_gradient_linear
            FillType.ConicalGradient -> R.string.ly_img_editor_fill_type_gradient_conical
            else -> throw IllegalArgumentException("Unknown fill type: $fillType")
        }
    }
}
