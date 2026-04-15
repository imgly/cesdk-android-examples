import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.bringForward
import ly.img.editor.core.component.delete
import ly.img.editor.core.component.duplicate
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberBringForward
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberSendBackward
import ly.img.editor.core.component.sendBackward
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderCanvasMenuSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                canvasMenu = {
                    CanvasMenu.remember {
                        listBuilder = {
                            // highlight-modifyListBuilder
                            // Makes sense to use only with builders that are already available and cannot be modified by you directly.
                            val existingListBuilder = CanvasMenu.ListBuilder.remember {
                                add { CanvasMenu.Button.rememberBringForward() }
                                add { CanvasMenu.Button.rememberSendBackward() }
                                add { CanvasMenu.Button.rememberDuplicate() }
                                add { CanvasMenu.Button.rememberDelete() }
                            }
                            existingListBuilder.modify {
                                // highlight-modifyListBuilder-addFirst
                                addFirst {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("my.package.canvasMenu.button.first") }
                                        vectorIcon = null
                                        textString = { "First Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addFirst
                                // highlight-modifyListBuilder-addLast
                                addLast {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("my.package.canvasMenu.button.last") }
                                        vectorIcon = null
                                        textString = { "Last Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addLast
                                // highlight-modifyListBuilder-addAfter
                                addAfter(id = CanvasMenu.Button.Id.bringForward) {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("my.package.canvasMenu.button.afterBringForward") }
                                        vectorIcon = null
                                        textString = { "After Bring Forward" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addAfter
                                // highlight-modifyListBuilder-addBefore
                                addBefore(id = CanvasMenu.Button.Id.sendBackward) {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("my.package.canvasMenu.button.beforeSendBackward") }
                                        vectorIcon = null
                                        textString = { "Before Send Backward" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addBefore
                                // highlight-modifyListBuilder-replace
                                replace(id = CanvasMenu.Button.Id.duplicate) {
                                    // Note that it can be replaced with a component that has a different id.
                                    CanvasMenu.Button.rememberDuplicate {
                                        vectorIcon = { IconPack.Music }
                                    }
                                }
                                // highlight-modifyListBuilder-replace
                                // highlight-modifyListBuilder-remove
                                remove(id = CanvasMenu.Button.Id.delete)
                                // highlight-modifyListBuilder-remove
                            }
                            // highlight-modifyListBuilder
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
