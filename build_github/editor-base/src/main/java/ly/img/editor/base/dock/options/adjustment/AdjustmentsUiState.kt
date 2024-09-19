package ly.img.editor.base.dock.options.adjustment

import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.engine.EffectAndBlurOptions
import ly.img.editor.base.ui.Block
import ly.img.engine.EffectType
import ly.img.engine.Engine

data class AdjustmentUiState(
    val adjustments: List<AdjustmentState>,
) {
    companion object Factory {
        fun create(
            block: Block,
            engine: Engine,
        ): AdjustmentUiState {
            return AdjustmentUiState(
                EffectAndBlurOptions.getEffectAdjustments(
                    engine = engine,
                    designBlock = block.designBlock,
                    effectType = EffectType.Adjustments,
                ),
            )
        }
    }
}
