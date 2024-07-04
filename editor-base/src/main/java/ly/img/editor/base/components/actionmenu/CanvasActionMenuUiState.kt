package ly.img.editor.base.components.actionmenu

import android.graphics.RectF
import ly.img.editor.base.engine.canBringForward
import ly.img.editor.base.engine.canSendBackward
import ly.img.editor.base.engine.isDeleteAllowed
import ly.img.editor.base.engine.isDuplicateAllowed
import ly.img.editor.base.engine.isGrouped
import ly.img.editor.base.engine.isMoveAllowed
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

data class CanvasActionMenuUiState(
    val selectedBlock: DesignBlock,
    val selectedBlockRect: RectF,
    val selectedBlockRotation: Float,
    val canBringForward: Boolean?,
    val canBringBackward: Boolean?,
    val isDuplicateAllowed: Boolean,
    val isDeleteAllowed: Boolean,
    val show: Boolean = true,
) {
    fun firstPageExists() = isDuplicateAllowed || isDeleteAllowed

    fun secondPageExists() = canBringBackward ?: false || canBringForward ?: false
}

internal fun createCanvasActionMenuUiState(
    designBlock: DesignBlock,
    engine: Engine,
): CanvasActionMenuUiState? {
    // This can happen if the block was just deleted
    if (!engine.block.isValid(designBlock)) return null

    // Don't show if the block is grouped
    if (engine.isGrouped(designBlock)) return null
    if (engine.block.getType(designBlock) == DesignBlockType.Page.key) return null
    val isMoveAllowed = engine.isMoveAllowed(designBlock)
    val isDuplicateAllowed = engine.isDuplicateAllowed(designBlock)
    val isDeleteAllowed = engine.isDeleteAllowed(designBlock)

    // Don't show the canvas action menu if none of the actions are allowed
    if (!isMoveAllowed && !isDuplicateAllowed && !isDeleteAllowed) return null

    val boundingBoxRect = engine.block.getScreenSpaceBoundingBoxRect(listOf(designBlock))
    // After process death, the bounding box isn't calculated correctly until the surface has been created.
    if (boundingBoxRect.width().isNaN() || boundingBoxRect.height().isNaN()) return null
    return CanvasActionMenuUiState(
        selectedBlock = designBlock,
        selectedBlockRect = boundingBoxRect,
        selectedBlockRotation = engine.block.getRotation(designBlock),
        canBringForward = if (isMoveAllowed) engine.canBringForward(designBlock) else null,
        canBringBackward = if (isMoveAllowed) engine.canSendBackward(designBlock) else null,
        isDuplicateAllowed = isDuplicateAllowed,
        isDeleteAllowed = isDeleteAllowed,
    )
}
