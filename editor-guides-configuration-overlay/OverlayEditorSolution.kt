import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.engine.DesignBlockType

// Add this composable to your NavHost
@Composable
fun OverlayEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    isLoading = true
                    try {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(block = page, value = 1080F)
                        editorContext.engine.block.setHeight(block = page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)
                        delay(3000)
                    } finally {
                        isLoading = false
                    }
                }
                overlay = {
                    EditorComponent.remember {
                        decoration = {
                            if (isLoading) {
                                AlertDialog(
                                    onDismissRequest = { },
                                    title = {
                                        Text(text = "Please wait. If you want to close the editor, click the button.")
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                isLoading = false
                                                editorContext.eventHandler.send(EditorEvent.CloseEditor())
                                            },
                                        ) {
                                            Text(text = "Close Editor")
                                        }
                                    },
                                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                                )
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
