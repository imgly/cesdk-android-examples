package ly.img.editor.base.engine

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.deselectAllBlocks
import ly.img.editor.core.ui.engine.dpToCanvasUnit
import ly.img.editor.core.ui.engine.getCamera
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.getScene
import ly.img.editor.core.ui.engine.getStackOrNull
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.editor.core.ui.engine.toRGBColor
import ly.img.engine.BlendMode
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.PositionMode
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.StrokeStyle

fun Engine.setClearColor(color: Color) {
    editor.setSettingColor("clearColor", color.toEngineColor())
}

fun Engine.addOutline(
    designBlock: DesignBlock,
    parent: DesignBlock,
) {
    val outline = block.create(DesignBlockType.Graphic)
    block.setShape(outline, block.createShape(ShapeType.Rect))
    val width = block.getWidth(parent)
    val height = block.getHeight(parent)
    block.setName(outline, OUTLINE_BLOCK_NAME)
    block.setHeightMode(outline, SizeMode.ABSOLUTE)
    block.setHeight(outline, height)
    block.setWidthMode(outline, SizeMode.ABSOLUTE)
    block.setWidth(outline, width)
    block.appendChild(designBlock, outline)

    block.setFillEnabled(outline, false)
    block.setStrokeEnabled(outline, true)
    block.setStrokeColor(outline, Color.White.toEngineColor())
    block.setStrokeStyle(outline, StrokeStyle.DOTTED)
    block.setStrokeWidth(outline, 1.0f)
    block.setBlendMode(outline, BlendMode.DIFFERENCE)
    block.setScopeEnabled(outline, Scope.EditorSelect, false)
}

fun Engine.showOutline(
    show: Boolean,
    name: String = OUTLINE_BLOCK_NAME,
) {
    val outline = block.findByName(name).firstOrNull() ?: return
    block.setVisible(outline, show)
    block.setOpacity(outline, if (show) 1f else 0f)
}

fun Engine.resetHistory() {
    val oldHistory = editor.getActiveHistory()
    val newHistory = editor.createHistory()
    editor.setActiveHistory(newHistory)
    editor.destroyHistory(oldHistory)
    editor.addUndoStep()
}

fun Engine.isPlaceholder(designBlock: DesignBlock): Boolean {
    if (!block.supportsPlaceholderControls(designBlock)) return false
    val isPlaceholderEnabled = block.isPlaceholderEnabled(designBlock)
    val showsPlaceholderButton = block.isPlaceholderControlsButtonEnabled(designBlock)
    val showsPlaceholderOverlay = block.isPlaceholderControlsOverlayEnabled(designBlock)
    return isPlaceholderEnabled && (showsPlaceholderButton || showsPlaceholderOverlay)
}

fun Engine.canBringForward(designBlock: DesignBlock): Boolean {
    val parent = block.getParent(designBlock)
    parent ?: return false
    val children = getReorderableChildren(parent, designBlock)
    return children.last() != designBlock
}

fun Engine.canSendBackward(designBlock: DesignBlock): Boolean {
    val parent = block.getParent(designBlock)
    parent ?: return false
    val children = getReorderableChildren(parent, designBlock)
    return children.first() != designBlock
}

/**
 * Get all reorderable children for a given parent and contained child.
 * This method filters out children that are not reorderable with the given child.
 */
private fun Engine.getReorderableChildren(
    parent: DesignBlock,
    child: DesignBlock,
): List<DesignBlock> {
    val childIsAlwaysOnTop = block.isAlwaysOnTop(child)
    val childIsAlwaysOnBottom = block.isAlwaysOnBottom(child)
    val childType = block.getType(child)

    val children = block.getChildren(parent)

    return children.filter { childToCompare ->
        val matchingIsAlwaysOnTop = childIsAlwaysOnTop == block.isAlwaysOnTop(childToCompare)
        val matchingIsAlwaysOnBottom = childIsAlwaysOnBottom == block.isAlwaysOnBottom(childToCompare)
        val matchingType =
            when (childType) {
                DesignBlockType.Audio.key -> block.getType(childToCompare) == DesignBlockType.Audio.key
                else -> block.getType(childToCompare) != DesignBlockType.Audio.key
            }
        matchingIsAlwaysOnTop && matchingIsAlwaysOnBottom && matchingType
    }
}

fun Engine.duplicate(designBlock: DesignBlock) {
    val duplicateBlock = block.duplicate(designBlock)
    if (!block.isTransformLocked(designBlock)) {
        val positionModeX = block.getPositionXMode(designBlock)
        val positionModeY = block.getPositionYMode(designBlock)
        overrideAndRestore(designBlock, Scope.LayerMove) {
            block.setPositionXMode(it, PositionMode.ABSOLUTE)
            val x = block.getPositionX(it)
            block.setPositionYMode(it, PositionMode.ABSOLUTE)
            val y = block.getPositionY(it)
            block.setPositionXMode(duplicateBlock, PositionMode.ABSOLUTE)
            block.setPositionX(duplicateBlock, x + 5)
            block.setPositionYMode(duplicateBlock, PositionMode.ABSOLUTE)
            block.setPositionY(duplicateBlock, y - 5)

            // Restore values
            block.setPositionXMode(it, positionModeX)
            block.setPositionYMode(it, positionModeY)
        }
    }
    block.setSelected(designBlock, false)
    block.setSelected(duplicateBlock, true)
    editor.addUndoStep()
}

fun Engine.delete(designBlock: DesignBlock) {
    block.destroy(designBlock)
    editor.addUndoStep()
}

/**
 * An extension function for checking whether the [designBlock] can be moved up/down.
 * IMPORTANT! When modifying this function also modify similar function in InspectorBarExt.kt.
 *
 * @return true if the [designBlock] can be moved, false otherwise.
 */
fun Engine.isMoveAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, Scope.EditorAdd) &&
        !isGrouped(designBlock) &&
        !block.isParentBackgroundTrack(designBlock)
}

/**
 * An extension function for checking whether the [designBlock] can be duplicated.
 * IMPORTANT! When modifying this function also modify similar function in InspectorBarExt.kt.
 *
 * @return true if the [designBlock] can be duplicated, false otherwise.
 */
fun Engine.isDuplicateAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, Scope.LifecycleDuplicate) && !isGrouped(designBlock)
}

/**
 * An extension function for checking whether the [designBlock] can be deleted.
 * IMPORTANT! When modifying this function also modify similar function in InspectorBarExt.kt.
 *
 * @return true if the [designBlock] can be deleted, false otherwise.
 */
fun Engine.isDeleteAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, Scope.LifecycleDestroy) && !isGrouped(designBlock)
}

/**
 * An extension function for checking whether the [designBlock] is part of a group.
 * IMPORTANT! When modifying this function also modify similar function in InspectorBarExt.kt.
 *
 * @return true if the [designBlock] is part of a group, false otherwise.
 */
fun Engine.isGrouped(designBlock: DesignBlock): Boolean {
    val parent = block.getParent(designBlock) ?: return false
    return DesignBlockType.get(block.getType(parent)) == DesignBlockType.Group
}

fun Engine.getFillColor(designBlock: DesignBlock): Color? {
    if (!block.supportsFill(designBlock)) return null
    return block.getColor(designBlock, "fill/solid/color")
        .toRGBColor(this)
        .toComposeColor()
}

fun Engine.canResetCrop(designBlock: DesignBlock) = block.getContentFillMode(designBlock) == ContentFillMode.CROP

fun Engine.zoomToPage(
    pageIndex: Int,
    insets: Rect,
) {
    zoomToBlock(getPage(pageIndex), insets)
}

fun Engine.zoomToScene(insets: Rect) {
    zoomToBlock(getScene(), insets)
}

fun Engine.zoomToBackdrop(insets: Rect) {
    zoomToBlock(getBackdropImage(), insets)
}

fun Engine.zoomToSelectedText(
    insets: Rect,
    canvasHeight: Float,
) {
    val paddingTop = insets.top
    val paddingBottom = insets.bottom

    val overlapTop = 50
    val overlapBottom = 50

    block.findAllSelected().singleOrNull() ?: return
    val pixelRatio = block.getFloat(getCamera(), "camera/pixelRatio")
    val cursorPosY = editor.getTextCursorPositionInScreenSpaceY() / pixelRatio
    // The first cursorPosY is 0 if no cursor has been layout yet. Then we ignore zoom commands.
    if (cursorPosY == 0f) return
    val visiblePageAreaY = canvasHeight - overlapBottom - paddingBottom
    val visiblePageAreaYCanvas = dpToCanvasUnit(visiblePageAreaY)
    val cursorPosYCanvas = dpToCanvasUnit(cursorPosY)
    val cameraPosY = block.getPositionY(getCamera())
    val newCameraPosY = cursorPosYCanvas + cameraPosY - visiblePageAreaYCanvas

    if (cursorPosY > visiblePageAreaY || cursorPosY < (overlapTop + paddingTop)) {
        overrideAndRestore(getCamera(), Scope.LayerMove) {
            block.setPositionY(getCamera(), newCameraPosY)
        }
    }
}

fun Engine.showAllPages(axis: LayoutAxis) {
    showPage(index = null, axis = axis, spacing = 16f)
}

fun Engine.showPage(
    index: Int?,
    axis: LayoutAxis = LayoutAxis.Depth,
    spacing: Float? = null,
) {
    deselectAllBlocks()

    getStackOrNull()?.let { stack ->
        block.setEnum(stack, "stack/axis", axis.name)
        spacing?.let {
            block.setFloat(stack, "stack/spacing", spacing)
        }
    }

    val pages = scene.getPages()
    val allPages = index == null
    pages.forEachIndexed { idx, page ->
        overrideAndRestore(page, Scope.LayerVisibility) {
            block.setVisible(block = it, visible = allPages || idx == index)
        }
    }
}

private fun Engine.zoomToBlock(
    designBlock: DesignBlock,
    insets: Rect,
) {
    scene.immediateZoomToBlock(
        block = designBlock,
        paddingLeft = insets.left,
        paddingTop = insets.top,
        paddingRight = insets.right,
        paddingBottom = insets.bottom,
        forceUpdate = true,
    )
}

// Note: Backdrop Images are not officially supported yet.
// The backdrop image is the only image that is a direct child of the scene block.
private fun Engine.getBackdropImage(): DesignBlock {
    val children = block.getChildren(getScene())
    return children.first {
        block.getKindEnum(it) == BlockKind.Image
    }
}
