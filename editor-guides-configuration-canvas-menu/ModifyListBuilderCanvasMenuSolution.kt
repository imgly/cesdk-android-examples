import androidx.compose.runtime.Composable
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
fun ModifyListBuilderCanvasMenuSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                canvasMenu = {
                    CanvasMenu.remember {
                        listBuilder = {
                            val existingListBuilder = CanvasMenu.ListBuilder.remember {
                                add { CanvasMenu.Button.rememberBringForward() }
                                add { CanvasMenu.Button.rememberSendBackward() }
                                add { CanvasMenu.Button.rememberDuplicate() }
                                add { CanvasMenu.Button.rememberDelete() }
                            }
                            // highlight-android-modify-items
                            existingListBuilder.modify {
                                addFirst {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.canvasMenu.button.first") }
                                        vectorIcon = null
                                        textString = { "First" }
                                        onClick = {}
                                    }
                                }
                                addLast {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.canvasMenu.button.last") }
                                        vectorIcon = null
                                        textString = { "Last" }
                                        onClick = {}
                                    }
                                }
                                addAfter(id = CanvasMenu.Button.Id.bringForward, failIfNotFound = true) {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.canvasMenu.button.afterBringForward") }
                                        vectorIcon = null
                                        textString = { "After Forward" }
                                        onClick = {}
                                    }
                                }
                                addBefore(id = CanvasMenu.Button.Id.sendBackward, failIfNotFound = true) {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.canvasMenu.button.beforeSendBackward") }
                                        vectorIcon = null
                                        textString = { "Before Backward" }
                                        onClick = {}
                                    }
                                }
                                replace(id = CanvasMenu.Button.Id.duplicate, failIfNotFound = true) {
                                    CanvasMenu.Button.rememberDuplicate {
                                        vectorIcon = { IconPack.Music }
                                    }
                                }
                                remove(id = CanvasMenu.Button.Id.delete, failIfNotFound = true)
                            }
                            // highlight-android-modify-items
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
