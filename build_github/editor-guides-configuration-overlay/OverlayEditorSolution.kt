import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.HideLoading
import ly.img.editor.ShowLoading

// Add this composable to your NavHost
@Composable
fun OverlayEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        remember {
            EngineConfiguration.getForDesign(license = "<your license here>")
        }
    val editorConfiguration =
        EditorConfiguration(
            initialState = OverlayCustomState(),
            // highlight-configuration-on-event
            onEvent = { activity, state, event ->
                when (event) {
                    is ShowLoading -> {
                        state.copy(showCustomLoading = true)
                    }
                    is HideLoading -> {
                        state.copy(showCustomLoading = false)
                    }
                    else -> {
                        // handle other default events
                        state.copy(baseState = EditorDefaults.onEvent(activity, state.baseState, event))
                    }
                }
            },
            // highlight-configuration-on-event
            // highlight-configuration-overlay
            overlay = { state, eventHandler ->
                if (state.showCustomLoading) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = {
                            Text(text = "Please wait. If you want to close the editor, click the button.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    eventHandler.send(HideLoading)
                                    eventHandler.sendCloseEditorEvent()
                                },
                            ) {
                                Text(text = "Close")
                            }
                        },
                        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                    )
                }
                EditorDefaults.Overlay(state = state.baseState, eventHandler = eventHandler)
            },
            // highlight-configuration-overlay
        )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
