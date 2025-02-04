package ly.img.cesdk.engine

import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.BlendMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.MimeType
import ly.img.engine.PositionMode
import ly.img.engine.SizeMode
import ly.img.engine.StrokeStyle
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

fun Engine.setClearColor(color: Color) {
    editor.setSettingColor("clearColor", color.toEngineColor())
}

fun Engine.enableEditMode() {
    overrideAndRestore(getPage(), "design/style") {
        editor.setSettingBoolean("ubq://page/dimOutOfPageAreas", false)
        block.setClipped(it, true)
        block.setBoolean(it, "fill/enabled", false)
        showOutline(show = false)
    }
}

fun Engine.enablePreviewMode() {
    deselectAllBlocks()
    overrideAndRestore(getPage(), "design/style") {
        editor.setSettingBoolean("ubq://page/dimOutOfPageAreas", false)
        block.setClipped(it, true)
        block.setBoolean(it, "fill/enabled", false)
        showOutline(show = false)
    }
}

fun Engine.addOutline(designBlock: DesignBlock, parent: DesignBlock = getPage()) {
    val outline = block.create(DesignBlockType.RECT_SHAPE)
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
    block.setScopeEnabled(outline, "editor/select", false)
}

fun Engine.showOutline(name: String = OUTLINE_BLOCK_NAME, show: Boolean) {
    val outline = block.findByName(name).firstOrNull() ?: return
    block.setVisible(outline, show)
    block.setOpacity(outline, if (show) 1f else 0f)
}

suspend fun Engine.exportScene(): File {
    val page = getPage()
    lateinit var file: File
    overrideAndRestoreAsync(page, "design/style") {
        val prevPageFill = block.getBoolean(page, "fill/enabled")
        block.setBoolean(page, "fill/enabled", true)
        val byteArray = block.export(page, MimeType.PDF)
        withContext(Dispatchers.IO) {
            file = File.createTempFile("export", ".pdf")
            val fos = FileOutputStream(file)
            fos.write(byteArray)
            fos.close()
        }
        block.setBoolean(page, "fill/enabled", prevPageFill)
    }
    return file
}

fun Engine.deselectAllBlocks() {
    block.findAllSelected().forEach {
        block.setSelected(it, false)
    }
}

fun Engine.overrideAndRestore(designBlock: DesignBlock, vararg scopes: String, action: (DesignBlock) -> Unit) {
    val disabledScopes = getDisabledScopes(designBlock, *scopes)
    action(designBlock)
    restoreScopes(designBlock, disabledScopes)
}

suspend fun Engine.overrideAndRestoreAsync(designBlock: DesignBlock, vararg scopes: String, action: suspend (DesignBlock) -> Unit) {
    val disabledScopes = getDisabledScopes(designBlock, *scopes)
    action(designBlock)
    restoreScopes(designBlock, disabledScopes)
}

private fun Engine.getDisabledScopes(designBlock: DesignBlock, vararg scopes: String): Set<String> {
    val disabledScopes = hashSetOf<String>()
    scopes.forEach {
        val wasEnabled = block.isScopeEnabled(designBlock, it)
        if (!wasEnabled) {
            block.setScopeEnabled(designBlock, it, true)
            disabledScopes.add(it)
        }
    }
    return disabledScopes
}

private fun Engine.restoreScopes(designBlock: DesignBlock, scopes: Set<String>) {
    scopes.forEach {
        block.setScopeEnabled(designBlock, it, false)
    }
}

fun Engine.isPlaceholder(designBlock: DesignBlock): Boolean {
    if (!block.hasPlaceholderControls(designBlock)) return false
    val isPlaceholderEnabled = block.isPlaceholderEnabled(designBlock)
    val showsPlaceholderButton = block.isPlaceholderControlsButtonEnabled(designBlock)
    val showsPlaceholderOverlay = block.isPlaceholderControlsOverlayEnabled(designBlock)
    return isPlaceholderEnabled && (showsPlaceholderButton || showsPlaceholderOverlay)
}

fun Engine.canBringForward(designBlock: DesignBlock): Boolean {
    val parent = block.getParent(designBlock)
    parent ?: return false
    val children = block.getChildren(parent)
    return children.last() != designBlock
}

fun Engine.canSendBackward(designBlock: DesignBlock): Boolean {
    val parent = block.getParent(designBlock)
    parent ?: return false
    val children = block.getChildren(parent)
    return children.first() != designBlock
}

fun Engine.bringForward(designBlock: DesignBlock) {
    val parent = block.getParent(designBlock) ?: return
    val children = block.getChildren(parent)
    val index = children.indexOf(designBlock)
    block.insertChild(parent, designBlock, index + 1)
    editor.addUndoStep()
}

fun Engine.bringToTop(designBlock: DesignBlock) {
    val parent = block.getParent(designBlock) ?: return
    block.appendChild(parent, designBlock)
    editor.addUndoStep()
}

fun Engine.sendBackward(designBlock: DesignBlock) {
    val parent = block.getParent(designBlock) ?: return
    val children = block.getChildren(parent)
    val index = children.indexOf(designBlock)
    block.insertChild(parent, designBlock, index - 1)
    editor.addUndoStep()
}

fun Engine.sendToBottom(designBlock: DesignBlock) {
    val parent = block.getParent(designBlock) ?: return
    block.insertChild(parent, designBlock, 0)
    editor.addUndoStep()
}

fun Engine.duplicate(designBlock: DesignBlock) {
    val duplicateBlock = block.duplicate(designBlock)
    val positionModeX = block.getPositionXMode(designBlock)
    val positionModeY = block.getPositionYMode(designBlock)
    overrideAndRestore(designBlock, "design/arrange/move") {
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
    block.setSelected(designBlock, false)
    block.setSelected(duplicateBlock, true)
    editor.addUndoStep()
}

fun Engine.delete(designBlock: DesignBlock) {
    block.destroy(designBlock)
    editor.addUndoStep()
}

fun Engine.isStylingAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, "design/style")
}

fun Engine.isMoveAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, "editor/add") && !isGrouped(designBlock)
}

fun Engine.isDuplicateAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, "lifecycle/duplicate") && !isGrouped(designBlock)
}

fun Engine.isDeleteAllowed(designBlock: DesignBlock): Boolean {
    return block.isAllowedByScope(designBlock, "lifecycle/destroy") && !isGrouped(designBlock)
}

fun Engine.isGrouped(designBlock: DesignBlock): Boolean {
    val parent = block.getParent(designBlock) ?: return false
    return block.getType(parent) == DesignBlockType.GROUP.key
}

fun Engine.getFillColor(designBlock: DesignBlock): Color? {
    return if (!block.hasFill(designBlock)) null
    else block.getColor(designBlock, "fill/solid/color").toComposeColor()
}

fun Engine.getStrokeColor(designBlock: DesignBlock): Color? {
    return if (!block.hasStroke(designBlock)) null
    else block.getColor(designBlock, "stroke/color").toComposeColor()
}

fun Engine.replaceSticker(stickerBlock: DesignBlock, uri: String) {
    block.setString(stickerBlock, "sticker/imageFileURI", uri)
}

fun Engine.addText(path: String, size: Float) {
    val fontSize = (50.0f / 24.0f) * size
    val textBlock = block.create(DesignBlockType.TEXT)
    block.setString(textBlock, "text/fontFileUri", Uri.parse("$FONT_BASE_PATH/$path").toString())
    block.setFloat(textBlock, "text/fontSize", fontSize)
    block.setEnum(textBlock, "text/horizontalAlignment", "Center")
    block.setHeightMode(textBlock, SizeMode.AUTO)
    addBlockToPage(textBlock)
}

suspend fun Engine.zoomToPage(insets: Rect) {
    zoomToBlock(getPage(), insets)
}

suspend fun Engine.zoomToBackdrop(insets: Rect) {
    zoomToBlock(getBackdropImage(), insets)
}

fun Engine.zoomToSelectedText(insets: Rect, canvasHeight: Float) {
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
        overrideAndRestore(getCamera(), "design/arrange/move") {
            block.setPositionY(getCamera(), newCameraPosY)
        }
    }
}

private fun Engine.dpToCanvasUnit(dp: Float): Float {
    val sceneUnit = block.getEnum(getScene(), "scene/designUnit")
    val sceneDpi = block.getFloat(getScene(), "scene/dpi")
    val densityFactor = when (sceneUnit) {
        "Millimeter" -> sceneDpi / 25.4f
        "Inch" -> sceneDpi
        else -> 1f
    }
    val zoomLevel = scene.getZoomLevel()
    return dp / (densityFactor * zoomLevel)
}

private suspend fun Engine.zoomToBlock(designBlock: DesignBlock, insets: Rect) {
    scene.zoomToBlock(
        block = designBlock,
        paddingLeft = insets.left,
        paddingTop = insets.top,
        paddingRight = insets.right,
        paddingBottom = insets.bottom
    )
}

// Note: Backdrop Images are not officially supported yet.
// The backdrop image is the only image that is a direct child of the scene block.
private fun Engine.getBackdropImage(): DesignBlock {
    val children = block.getChildren(getScene())
    return children.first {
        block.getType(it) == DesignBlockType.IMAGE.key
    }
}

private fun Engine.getPage(): DesignBlock {
    return block.findByType(DesignBlockType.PAGE).first()
}

private fun Engine.getScene(): DesignBlock {
    return block.findByType(DesignBlockType.SCENE).first()
}

private fun Engine.getCamera(): DesignBlock {
    return block.findByType(DesignBlockType.CAMERA).first()
}

/**
 * Appends a block into the scene and positions it somewhat randomly.
 */
private fun Engine.addBlockToPage(designBlock: DesignBlock) {
    deselectAllBlocks()
    block.appendChild(getPage(), designBlock)
    block.setPositionXMode(designBlock, PositionMode.ABSOLUTE)
    block.setPositionX(designBlock, 15 + (Random.nextFloat() * 20))
    block.setPositionYMode(designBlock, PositionMode.ABSOLUTE)
    block.setPositionY(designBlock, 5 + (Random.nextFloat() * 20))
    block.setSelected(designBlock, true)
    editor.addUndoStep()
}
