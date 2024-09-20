package ly.img.editor.base.dock.options.layer

import androidx.annotation.StringRes
import ly.img.editor.base.engine.canBringForward
import ly.img.editor.base.engine.canSendBackward
import ly.img.editor.base.engine.isDeleteAllowed
import ly.img.editor.base.engine.isDuplicateAllowed
import ly.img.editor.base.engine.isMoveAllowed
import ly.img.editor.core.ui.engine.Scope
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class LayerUiState(
    val opacity: Float? = null,
    @StringRes val blendMode: Int? = null,
    val isMoveAllowed: Boolean,
    val isDuplicateAllowed: Boolean,
    val isDeleteAllowed: Boolean,
    val canBringForward: Boolean,
    val canSendBackward: Boolean,
)

internal fun createLayerUiState(
    designBlock: DesignBlock,
    engine: Engine,
): LayerUiState {
    return LayerUiState(
        opacity =
            if (engine.block.isAllowedByScope(designBlock, Scope.LayerOpacity) && engine.block.supportsOpacity(designBlock)) {
                engine.block.getOpacity(designBlock)
            } else {
                null
            },
        blendMode =
            if (engine.block.isAllowedByScope(designBlock, Scope.LayerBlendMode) &&
                engine.block.supportsBlendMode(designBlock)
            ) {
                getBlendModeStringResource(engine.block.getBlendMode(designBlock))
            } else {
                null
            },
        isMoveAllowed = engine.isMoveAllowed(designBlock),
        isDuplicateAllowed = engine.isDuplicateAllowed(designBlock),
        isDeleteAllowed = engine.isDeleteAllowed(designBlock),
        canBringForward = engine.canBringForward(designBlock),
        canSendBackward = engine.canSendBackward(designBlock),
    )
}
