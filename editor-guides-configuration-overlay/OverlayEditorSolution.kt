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
    // highlight-android-state
    var isLoading by remember { mutableStateOf(false) }
    // highlight-android-state
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-loading-state
                onCreate = {
                    isLoading = true
                    try {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(block = page, value = 1080F)
                        editorContext.engine.block.setHeight(block = page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)
                        // Replace this delay with the startup work that should block editor interaction.
                        delay(3000)
                    } finally {
                        isLoading = false
                    }
                }
                // highlight-android-loading-state
                // highlight-android-overlay
                overlay = {
                    EditorComponent.remember {
                        decoration = {
                            if (isLoading) {
                                AlertDialog(
                                    onDismissRequest = { },
                                    title = {
                                        Text(text = "Loading editor")
                                    },
                                    text = {
                                        Text(text = "The editor is preparing the scene. You can close it if you need to cancel.")
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
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
                // highlight-android-overlay
            }
        },
        onClose = onClose,
    )
}
