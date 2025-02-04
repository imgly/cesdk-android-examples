package ly.img.cesdk.core.components.actionmenu

import android.graphics.RectF
import ly.img.cesdk.engine.canBringForward
import ly.img.cesdk.engine.canSendBackward
import ly.img.cesdk.engine.isDeleteAllowed
import ly.img.cesdk.engine.isDuplicateAllowed
import ly.img.cesdk.engine.isGrouped
import ly.img.cesdk.engine.isMoveAllowed
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class CanvasActionMenuUiState(
    val selectedBlock: DesignBlock,
    val selectedBlockRect: RectF,
    val selectedBlockRotation: Float,
    val canBringForward: Boolean?,
    val canBringBackward: Boolean?,
    val isDuplicateAllowed: Boolean,
    val isDeleteAllowed: Boolean,
    val show: Boolean = true
)

internal fun createCanvasActionMenuUiState(designBlock: DesignBlock, engine: Engine): CanvasActionMenuUiState? {
    if (engine.isGrouped(designBlock)) return null
    val isMoveAllowed = engine.isMoveAllowed(designBlock)
    return CanvasActionMenuUiState(
        selectedBlock = designBlock,
        selectedBlockRect = engine.block.getScreenSpaceBoundingBoxRect(listOf(designBlock)),
        selectedBlockRotation = engine.block.getRotation(designBlock),
        canBringForward = if (isMoveAllowed) engine.canBringForward(designBlock) else null,
        canBringBackward = if (isMoveAllowed) engine.canSendBackward(designBlock) else null,
        isDuplicateAllowed = engine.isDuplicateAllowed(designBlock),
        isDeleteAllowed = engine.isDeleteAllowed(designBlock),
    )
}