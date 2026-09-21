import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.duplicate
import ly.img.editor.core.component.layer
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Plus
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Color as EngineColor

private const val DEMO_BLOCK_NAME = "com.example.guides.addButton.selection"
private const val LIFECYCLE_DUPLICATE_SCOPE = "lifecycle/duplicate"

@Composable
fun AddButtonEditorSolution(
    license: String,
    baseUri: Uri,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::DesignConfigurationBuilder).then {
                onLoaded = onLoaded@{
                    val engine = editorContext.engine
                    val scene = engine.scene.get() ?: engine.scene.create()
                    val page = engine.scene.getCurrentPage() ?: engine.scene.getPages().firstOrNull() ?: engine.block
                        .create(DesignBlockType.Page)
                        .also {
                            engine.block.setWidth(block = it, value = 1080F)
                            engine.block.setHeight(block = it, value = 1080F)
                            engine.block.appendChild(parent = scene, child = it)
                        }
                    val block = engine.block.findByName(DEMO_BLOCK_NAME).firstOrNull() ?: engine.block
                        .create(DesignBlockType.Graphic)
                        .also {
                            engine.block.setName(block = it, name = DEMO_BLOCK_NAME)
                            engine.block.setShape(
                                block = it,
                                shape = engine.block.createShape(ShapeType.Rect),
                            )
                            val fill = engine.block.createFill(FillType.Color)
                            engine.block.setFill(block = it, fill = fill)
                            engine.block.setFillSolidColor(
                                block = it,
                                color = EngineColor.fromRGBA(r = 0.32F, g = 0.58F, b = 0.94F, a = 1F),
                            )
                            engine.block.setWidth(block = it, value = 320F)
                            engine.block.setHeight(block = it, value = 220F)
                            engine.block.setPositionX(block = it, value = 380F)
                            engine.block.setPositionY(block = it, value = 360F)
                            engine.block.appendChild(parent = page, child = it)
                        }
                    engine.block.bringToFront(block = block)
                    engine.block.setSelected(block = block, selected = true)
                    engine.scene.zoomToBlock(page)

                    parentConfiguration?.onLoaded?.invoke(this)
                }

                // highlight-android-dock
                dock = dockComponent@{
                    val sourceDock = parentConfiguration?.dock as? Dock ?: return@dockComponent null
                    val updatedListBuilder = sourceDock.listBuilder.modify {
                        addFirst {
                            Dock.Button.remember {
                                id = { EditorComponentId("com.example.guides.addButton.dock.create") }
                                vectorIcon = { IconPack.Plus }
                                textString = { "Create" }
                                contentDescription = { "Create item" }
                                onClick = {
                                    Toast
                                        .makeText(editorContext.activity, "Create item", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                    remember(sourceDock, updatedListBuilder) {
                        sourceDock.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-dock

                // highlight-android-canvas-menu
                canvasMenu = canvasMenuComponent@{
                    val sourceCanvasMenu = parentConfiguration?.canvasMenu as? CanvasMenu ?: return@canvasMenuComponent null
                    val updatedListBuilder = sourceCanvasMenu.listBuilder.modify {
                        addAfter(id = CanvasMenu.Button.Id.duplicate, failIfNotFound = false) {
                            CanvasMenu.Button.remember {
                                id = { EditorComponentId("com.example.guides.addButton.canvasMenu.note") }
                                vectorIcon = { IconPack.Plus }
                                contentDescription = { "Add note" }
                                visible = { editorContext.safeSelection != null }
                                enabled = {
                                    editorContext.safeSelection?.designBlock?.let {
                                        editorContext.engine.block.isAllowedByScope(it, LIFECYCLE_DUPLICATE_SCOPE)
                                    } == true
                                }
                                onClick = {
                                    Toast
                                        .makeText(editorContext.activity, "Add note", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                    remember(sourceCanvasMenu, updatedListBuilder) {
                        sourceCanvasMenu.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-canvas-menu

                // highlight-android-inspector-bar
                inspectorBar = inspectorBarComponent@{
                    val sourceInspectorBar = parentConfiguration?.inspectorBar as? InspectorBar ?: return@inspectorBarComponent null
                    val updatedListBuilder = sourceInspectorBar.listBuilder.modify {
                        addBefore(id = InspectorBar.Button.Id.layer, failIfNotFound = false) {
                            InspectorBar.Button.remember {
                                id = { EditorComponentId("com.example.guides.addButton.inspector.review") }
                                vectorIcon = { IconPack.Plus }
                                textString = { "Review" }
                                contentDescription = { "Review selection" }
                                visible = { editorContext.safeSelection != null }
                                enabled = {
                                    editorContext.safeSelection?.designBlock?.let {
                                        editorContext.engine.block.isAllowedByScope(it, LIFECYCLE_DUPLICATE_SCOPE)
                                    } == true
                                }
                                onClick = {
                                    Toast
                                        .makeText(editorContext.activity, "Review selection", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                    remember(sourceInspectorBar, updatedListBuilder) {
                        sourceInspectorBar.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-inspector-bar

                // highlight-android-navigation-bar
                navigationBar = navigationBarComponent@{
                    val sourceNavigationBar =
                        parentConfiguration?.navigationBar as? NavigationBar ?: return@navigationBarComponent null
                    val updatedListBuilder = sourceNavigationBar.listBuilder.modify {
                        addLast(alignment = Alignment.End) {
                            NavigationBar.Button.remember {
                                id = { EditorComponentId("com.example.guides.addButton.navigation.create") }
                                vectorIcon = { IconPack.Plus }
                                contentDescription = { "Create draft" }
                                onClick = {
                                    Toast
                                        .makeText(editorContext.activity, "Create draft", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                    remember(sourceNavigationBar, updatedListBuilder) {
                        sourceNavigationBar.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-navigation-bar
            }
        },
        onClose = onClose,
    )
}
