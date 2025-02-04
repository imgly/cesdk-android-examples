package ly.img.cesdk.dock.options.layer

import androidx.annotation.StringRes
import ly.img.cesdk.engine.canBringForward
import ly.img.cesdk.engine.canSendBackward
import ly.img.cesdk.engine.isDeleteAllowed
import ly.img.cesdk.engine.isDuplicateAllowed
import ly.img.cesdk.engine.isMoveAllowed
import ly.img.cesdk.engine.isStylingAllowed
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class LayerUiState(
    val opacity: Float? = null,
    @StringRes val blendMode: Int? = null,
    val isMoveAllowed: Boolean,
    val isDuplicateAllowed: Boolean,
    val isDeleteAllowed: Boolean,
    val canBringForward: Boolean,
    val canSendBackward: Boolean
)

internal fun createLayerUiState(designBlock: DesignBlock, engine: Engine): LayerUiState {
    val isStylingAllowed = engine.isStylingAllowed(designBlock)
    return LayerUiState(
        opacity = if (isStylingAllowed && engine.block.hasOpacity(designBlock)) engine.block.getOpacity(designBlock) else null,
        blendMode = if (isStylingAllowed && engine.block.hasBlendMode(designBlock)) {
            getBlendModeStringResource(engine.block.getBlendMode(designBlock))
        } else null,
        isMoveAllowed = engine.isMoveAllowed(designBlock),
        isDuplicateAllowed = engine.isDuplicateAllowed(designBlock),
        isDeleteAllowed = engine.isDeleteAllowed(designBlock),
        canBringForward = engine.canBringForward(designBlock),
        canSendBackward = engine.canSendBackward(designBlock)
    )
}
