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
import ly.img.editor.core.component.bringForward
import ly.img.editor.core.component.duplicate
import ly.img.editor.core.component.imglyCamera
import ly.img.editor.core.component.layer
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.redo
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.core.component.selectGroup
import ly.img.editor.core.component.sendBackward
import ly.img.editor.core.component.textLibrary
import ly.img.editor.core.component.undo
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then

@Composable
fun RearrangeButtonsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember(::DesignConfigurationBuilder).then {
                // highlight-android-navigation-bar
                navigationBar = navigationBarComponent@{
                    val sourceNavigationBar =
                        parentConfiguration?.navigationBar as? NavigationBar ?: return@navigationBarComponent null
                    val updatedListBuilder = sourceNavigationBar.listBuilder.modify {
                        remove(id = NavigationBar.Button.Id.undo, failIfNotFound = true)
                        remove(id = NavigationBar.Button.Id.redo, failIfNotFound = true)
                        addFirst(alignment = Alignment.Start) {
                            NavigationBar.Button.rememberUndo()
                        }
                        addFirst(alignment = Alignment.Start) {
                            NavigationBar.Button.rememberRedo()
                        }
                    }
                    remember(sourceNavigationBar, updatedListBuilder) {
                        sourceNavigationBar.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-navigation-bar
                // highlight-android-canvas-menu
                canvasMenu = canvasMenuComponent@{
                    val sourceCanvasMenu =
                        parentConfiguration?.canvasMenu as? CanvasMenu ?: return@canvasMenuComponent null
                    val updatedListBuilder = sourceCanvasMenu.listBuilder.modify {
                        // These divider IDs come from the Design Editor Starter Kit.
                        // Give custom dividers stable IDs and remove those IDs instead.
                        val designCanvasMenuDivider1 = EditorComponentId("ly.img.component.canvasMenu.divider1")
                        val designCanvasMenuDivider2 = EditorComponentId("ly.img.component.canvasMenu.divider2")
                        remove(id = designCanvasMenuDivider1, failIfNotFound = false)
                        remove(id = CanvasMenu.Button.Id.bringForward, failIfNotFound = false)
                        remove(id = CanvasMenu.Button.Id.selectGroup, failIfNotFound = false)
                        remove(id = CanvasMenu.Button.Id.sendBackward, failIfNotFound = false)
                        remove(id = designCanvasMenuDivider2, failIfNotFound = false)
                    }
                    remember(sourceCanvasMenu, updatedListBuilder) {
                        sourceCanvasMenu.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-canvas-menu
                // highlight-android-dock
                dock = dockComponent@{
                    val sourceDock = parentConfiguration?.dock as? Dock ?: return@dockComponent null
                    val updatedListBuilder = sourceDock.listBuilder.modify {
                        remove(id = Dock.Button.Id.textLibrary, failIfNotFound = true)
                        addFirst {
                            Dock.Button.rememberTextLibrary()
                        }
                        remove(id = Dock.Button.Id.imglyCamera, failIfNotFound = true)
                    }
                    remember(sourceDock, updatedListBuilder) {
                        sourceDock.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-dock
                // highlight-android-inspector-bar
                inspectorBar = inspectorBarComponent@{
                    val sourceInspectorBar =
                        parentConfiguration?.inspectorBar as? InspectorBar ?: return@inspectorBarComponent null
                    val updatedListBuilder = sourceInspectorBar.listBuilder.modify {
                        remove(id = InspectorBar.Button.Id.duplicate, failIfNotFound = true)
                        addBefore(id = InspectorBar.Button.Id.layer, failIfNotFound = true) {
                            InspectorBar.Button.rememberDuplicate()
                        }
                    }
                    remember(sourceInspectorBar, updatedListBuilder) {
                        sourceInspectorBar.copy(listBuilder = updatedListBuilder)
                    }
                }
                // highlight-android-inspector-bar
            }
        },
        onClose = onClose,
    )
}
