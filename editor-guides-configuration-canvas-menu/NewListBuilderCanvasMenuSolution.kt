import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberBringForward
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberSelectGroup
import ly.img.editor.core.component.rememberSendBackward
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun NewListBuilderCanvasMenuSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                canvasMenu = {
                    CanvasMenu.remember {
                        listBuilder = {
                            // highlight-newListBuilder
                            CanvasMenu.ListBuilder.remember {
                                add {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("my.package.canvasMenu.button.custom") }
                                        onClick = {}
                                        vectorIcon = null
                                        textString = { "Custom Button" }
                                    }
                                }
                                add { CanvasMenu.Button.rememberSelectGroup() }
                                if (editorContext.isSelectionInGroup) {
                                    add {
                                        CanvasMenu.Divider.remember()
                                    }
                                }
                                add { CanvasMenu.Button.rememberSendBackward() }
                                add { CanvasMenu.Button.rememberBringForward() }
                                if (editorContext.canSelectionMove) {
                                    add {
                                        CanvasMenu.Divider.remember()
                                    }
                                }
                                add { CanvasMenu.Button.rememberDuplicate() }
                                add { CanvasMenu.Button.rememberDelete() }
                            }
                            // highlight-newListBuilder
                        }
                    }
                }
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
