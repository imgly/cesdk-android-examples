import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.Editor
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberLayer
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Color as EngineColor

// Add this composable to your NavHost.
@Composable
fun QuickActionsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    if (editorContext.engine.scene.get() == null) {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(page, value = 1080F)
                        editorContext.engine.block.setHeight(page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)

                        val block = editorContext.engine.block.create(DesignBlockType.Graphic)
                        editorContext.engine.block.setShape(
                            block = block,
                            shape = editorContext.engine.block.createShape(ShapeType.Rect),
                        )
                        val fill = editorContext.engine.block.createFill(FillType.Color)
                        editorContext.engine.block.setFill(block = block, fill = fill)
                        editorContext.engine.block.setFillSolidColor(
                            block = block,
                            color = EngineColor.fromRGBA(r = 0.96F, g = 0.6F, b = 0.12F, a = 1F),
                        )
                        editorContext.engine.block.setWidth(block, value = 540F)
                        editorContext.engine.block.setHeight(block, value = 540F)
                        editorContext.engine.block.setPositionX(block, value = 270F)
                        editorContext.engine.block.setPositionY(block, value = 270F)
                        editorContext.engine.block.appendChild(parent = page, child = block)
                        editorContext.engine.scene.zoomToBlock(page)
                        editorContext.engine.block.setSelected(block, selected = true)
                    }
                }
                canvasMenu = { rememberQuickActionsCanvasMenu() }
                inspectorBar = { rememberQuickActionsInspectorBar() }
            }
        },
        onClose = onClose,
    )
}

// highlight-android-canvas-menu
@Composable
private fun rememberQuickActionsCanvasMenu() = CanvasMenu.remember {
    listBuilder = {
        CanvasMenu.ListBuilder.remember {
            add { CanvasMenu.Button.rememberDuplicate() }
            add { rememberFlipSelectionButton() }
            add { rememberResetOrientationButton() }
            add { CanvasMenu.Button.rememberDelete() }
        }
    }
}
// highlight-android-canvas-menu

// highlight-android-custom-button
@Composable
private fun rememberFlipSelectionButton() = CanvasMenu.Button.remember {
    id = { EditorComponentId("ly.img.guide.quickActions.canvasMenu.flip") }
    visible = {
        remember(this) {
            val selectedBlock = editorContext.safeSelection?.designBlock
            selectedBlock != null &&
                editorContext.engine.block.supportsFill(selectedBlock) &&
                editorContext.engine.block.isAllowedByScope(selectedBlock, key = "layer/flip")
        }
    }
    textString = { "Flip" }
    contentDescription = { "Flip selected block horizontally" }
    onClick = {
        flipSelectedBlockHorizontally()
    }
}
// highlight-android-custom-button

// highlight-android-compound-action
@Composable
private fun rememberResetOrientationButton() = CanvasMenu.Button.remember {
    id = { EditorComponentId("ly.img.guide.quickActions.canvasMenu.resetOrientation") }
    visible = {
        remember(this) {
            val selectedBlock = editorContext.safeSelection?.designBlock
            selectedBlock != null &&
                editorContext.engine.block.isAllowedByScope(selectedBlock, key = "layer/rotate") &&
                editorContext.engine.block.isAllowedByScope(selectedBlock, key = "layer/flip")
        }
    }
    textString = { "Reset" }
    contentDescription = { "Reset selected block orientation" }
    onClick = {
        val selectedBlock = editorContext.selection.designBlock
        editorContext.engine.block.setRotation(block = selectedBlock, radians = 0F)
        editorContext.engine.block.setFlipHorizontal(block = selectedBlock, flip = false)
        editorContext.engine.editor.addUndoStep()
    }
}
// highlight-android-compound-action

// highlight-android-inspector-bar
@Composable
private fun rememberQuickActionsInspectorBar() = InspectorBar.remember {
    listBuilder = {
        InspectorBar.ListBuilder.remember {
            add { InspectorBar.Button.rememberLayer() }
            add {
                InspectorBar.Button.remember {
                    id = { EditorComponentId("ly.img.guide.quickActions.inspectorBar.flip") }
                    visible = {
                        remember(this) {
                            val selectedBlock = editorContext.safeSelection?.designBlock
                            selectedBlock != null &&
                                editorContext.engine.block.supportsFill(selectedBlock) &&
                                editorContext.engine.block.isAllowedByScope(selectedBlock, key = "layer/flip")
                        }
                    }
                    textString = { "Flip" }
                    contentDescription = { "Flip selected block horizontally" }
                    onClick = {
                        val selectedBlock = editorContext.selection.designBlock
                        val flipped = editorContext.engine.block.isFlipHorizontal(selectedBlock)
                        editorContext.engine.block.setFlipHorizontal(block = selectedBlock, flip = !flipped)
                        editorContext.engine.editor.addUndoStep()
                    }
                }
            }
            add { InspectorBar.Button.rememberDelete() }
        }
    }
}
// highlight-android-inspector-bar

// highlight-android-shared-helper
private fun CanvasMenu.ItemScope.flipSelectedBlockHorizontally() {
    val selectedBlock = editorContext.selection.designBlock
    val flipped = editorContext.engine.block.isFlipHorizontal(selectedBlock)
    editorContext.engine.block.setFlipHorizontal(block = selectedBlock, flip = !flipped)
    editorContext.engine.editor.addUndoStep()
}
// highlight-android-shared-helper
