package ly.img.editor.base.dock.options.fillstroke

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.editor.base.ui.Block
import ly.img.editor.core.R
import ly.img.editor.core.ui.engine.getFillType
import ly.img.engine.Engine
import ly.img.engine.FillType

data class FillStrokeUiState(
    @StringRes val titleRes: Int,
    val fillUiState: FillUiState?,
    val strokeUiState: StrokeUiState?,
) {
    companion object Factory {
        internal fun getFillStrokeTitleRes(
            hasSolidOrGradientFill: Boolean,
            hasStroke: Boolean,
        ) = when {
            hasSolidOrGradientFill && hasStroke -> {
                R.string.ly_img_editor_fill_and_stroke
            }

            hasSolidOrGradientFill -> {
                R.string.ly_img_editor_fill
            }

            hasStroke -> {
                R.string.ly_img_editor_stroke
            }

            else -> {
                throw IllegalArgumentException(
                    "getFillStrokeTitleRes() should not be called when hasFill and hasStroke both are false.",
                )
            }
        }

        fun create(
            block: Block,
            engine: Engine,
            colorPalette: List<Color>,
        ): FillStrokeUiState {
            val designBlock = block.designBlock
            val fillType = engine.block.getFillType(designBlock)
            val hasSolidOrGradientFill = (
                fillType == FillType.Color ||
                    fillType == FillType.LinearGradient ||
                    fillType == FillType.RadialGradient ||
                    fillType == FillType.ConicalGradient
            )
            val hasStroke = engine.block.supportsStroke(designBlock)
            val palette = colorPalette.take(6)
            return FillStrokeUiState(
                titleRes = getFillStrokeTitleRes(hasSolidOrGradientFill, hasStroke),
                fillUiState =
                    if (hasSolidOrGradientFill) {
                        createFillUiState(
                            block,
                            engine,
                            palette,
                        )
                    } else {
                        null
                    },
                strokeUiState = if (hasStroke) createStrokeUiState(block, engine, palette) else null,
            )
        }
    }
}
