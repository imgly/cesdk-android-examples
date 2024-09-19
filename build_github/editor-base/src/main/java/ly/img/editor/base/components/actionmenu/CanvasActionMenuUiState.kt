package ly.img.editor.base.components.actionmenu

import android.graphics.RectF
import androidx.compose.ui.geometry.Rect
import ly.img.editor.base.engine.canBringForward
import ly.img.editor.base.engine.canSendBackward
import ly.img.editor.base.engine.isDeleteAllowed
import ly.img.editor.base.engine.isDuplicateAllowed
import ly.img.editor.base.engine.isGrouped
import ly.img.editor.base.engine.isMoveAllowed
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EditorApi
import ly.img.engine.Engine

data class CanvasActionMenuUiState(
    val selectedBlock: DesignBlock,
    val selectedBlockRect: RectF,
    val selectedBlockRotation: Float,
    val currentInsets: Rect,
    val isGizmoPresent: Boolean,
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
    currentInsets: Rect,
    engine: Engine,
): CanvasActionMenuUiState? {
    // This can happen if the block was just deleted
    if (!engine.block.isValid(designBlock)) return null

    // Don't show if the block is grouped
    if (engine.isGrouped(designBlock)) return null

    // Don't show if block is not visible currently
    if (!engine.block.isVisibleAtCurrentPlaybackTime(designBlock)) return null

    val designBlockType = engine.block.getType(designBlock)

    // Don't show for audio blocks as they don't have any visual representation anyway
    if (designBlockType == DesignBlockType.Audio.key) return null

    if (designBlockType == DesignBlockType.Page.key) return null

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
        currentInsets = currentInsets,
        isGizmoPresent = engine.editor.isGizmoPresent(),
        canBringForward = if (isMoveAllowed) engine.canBringForward(designBlock) else null,
        canBringBackward = if (isMoveAllowed) engine.canSendBackward(designBlock) else null,
        isDuplicateAllowed = isDuplicateAllowed,
        isDeleteAllowed = isDeleteAllowed,
    )
}

private fun EditorApi.isGizmoPresent(): Boolean {
    return getSettingBoolean("controlGizmo/showRotateHandles") || getSettingBoolean("controlGizmo/showMoveHandles")
}
