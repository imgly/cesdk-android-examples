import android.widget.Toast
import androidx.compose.runtime.Composable
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
fun NewListBuilderCanvasMenuSolution(
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
                            // highlight-android-declare-items
                            CanvasMenu.ListBuilder.remember {
                                add {
                                    CanvasMenu.Button.remember {
                                        id = { EditorComponentId("com.example.canvasMenu.button.review") }
                                        onClick = {
                                            Toast
                                                .makeText(editorContext.activity, "Review action", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                        vectorIcon = null
                                        textString = { "Review" }
                                        contentDescription = { "Review selected block" }
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
                            // highlight-android-declare-items
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
