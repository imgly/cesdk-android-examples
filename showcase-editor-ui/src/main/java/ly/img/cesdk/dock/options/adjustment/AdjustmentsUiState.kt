package ly.img.cesdk.dock.options.adjustment

import ly.img.cesdk.editorui.Block
import ly.img.cesdk.engine.AdjustmentState
import ly.img.cesdk.engine.EffectAndBlurOptions
import ly.img.engine.EffectType
import ly.img.engine.Engine

data class AdjustmentUiState (
    val adjustments: List<AdjustmentState>
) {
    companion object Factory {
        fun create(block: Block, engine: Engine): AdjustmentUiState {
            return AdjustmentUiState(EffectAndBlurOptions.getEffectAdjustments(engine, block.designBlock, EffectType.Adjustments))
        }
    }
}
