import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EditorUiState
import ly.img.editor.EngineConfiguration
import ly.img.editor.ShowLoading
import ly.img.editor.core.event.EditorEvent

// highlight-configuration-custom-event
data object OnCreateCustomEvent : EditorEvent
// highlight-configuration-custom-event

// Add this composable to your NavHost
@Composable
fun UiEventsEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        remember {
            EngineConfiguration(
                license = "<your license here>",
                // highlight-configuration-engine-callback
                onCreate = { engine, eventHandler ->
                    EditorDefaults.onCreate(
                        engine = engine,
                        sceneUri = EngineConfiguration.defaultDesignSceneUri,
                        eventHandler = eventHandler,
                    )
                    // highlight-configuration-send-event
                    eventHandler.send(OnCreateCustomEvent)
                    // highlight-configuration-send-event
                },
                // highlight-configuration-engine-callback
            )
        }
    val editorConfiguration =
        EditorConfiguration(
            initialState = EditorUiState(),
            // highlight-configuration-on-event
            onEvent = { activity, state, event ->
                when (event) {
                    // highlight-configuration-on-event-new
                    OnCreateCustomEvent -> {
                        Toast.makeText(activity, "Editor is created!", Toast.LENGTH_SHORT).show()
                        state
                    }
                    // highlight-configuration-on-event-new
                    // highlight-configuration-on-event-default-override
                    ShowLoading -> {
                        state.copy(showLoading = true)
                    }
                    // highlight-configuration-on-event-default-override
                    // highlight-configuration-on-event-default-remaining
                    else -> {
                        // handle other default events
                        EditorDefaults.onEvent(activity, state, event)
                    }
                    // highlight-configuration-on-event-default-remaining
                }
            },
            // highlight-configuration-on-event
        )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
