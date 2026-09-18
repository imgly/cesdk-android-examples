import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.state.EditorViewMode

// highlight-android-event-state
data class UiEventsState(
    val isLoading: Boolean = false,
    val lastEvent: String = "Waiting for editor events",
)
// highlight-android-event-state

// highlight-android-custom-events
object ShowLoading : EditorEvent

object HideLoading : EditorEvent

data class WorkflowStepChanged(
    val label: String,
) : EditorEvent
// highlight-android-custom-events

// Add this composable to your NavHost
@Composable
fun UiEventsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-remember-state
    var state by remember { mutableStateOf(UiEventsState()) }
    // highlight-android-remember-state
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-send-custom-events
                onLoaded = {
                    editorContext.eventHandler.send(
                        WorkflowStepChanged(label = "Editor loaded"),
                    )
                }
                // highlight-android-send-custom-events
                // highlight-android-observe-events
                onEvent = { event ->
                    state = when (event) {
                        ShowLoading -> state.copy(
                            isLoading = true,
                            lastEvent = "Started custom loading state",
                        )

                        HideLoading -> state.copy(
                            isLoading = false,
                            lastEvent = "Finished custom loading state",
                        )

                        is WorkflowStepChanged -> state.copy(lastEvent = event.label)

                        is EditorEvent.SetViewMode -> {
                            val viewMode = when (event.viewMode) {
                                is EditorViewMode.Edit -> "Edit"
                                is EditorViewMode.Preview -> "Preview"
                                is EditorViewMode.Pages -> "Pages"
                            }
                            state.copy(lastEvent = "View mode changed to $viewMode")
                        }

                        is EditorEvent.Export.Start -> {
                            state.copy(lastEvent = "Export requested")
                        }

                        else -> state
                    }
                }
                // highlight-android-observe-events
                // highlight-android-overlay-actions
                overlay = {
                    EditorComponent.remember {
                        decoration = {
                            val syncScope = rememberCoroutineScope()
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = state.lastEvent,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 24.dp),
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Button(
                                        onClick = {
                                            editorContext.eventHandler.send(
                                                EditorEvent.SetViewMode(EditorViewMode.Preview()),
                                            )
                                        },
                                    ) {
                                        Text("Preview")
                                    }
                                    Button(
                                        onClick = {
                                            editorContext.eventHandler.send(EditorEvent.Export.Start())
                                        },
                                    ) {
                                        Text("Request Export")
                                    }
                                    Button(
                                        enabled = !state.isLoading,
                                        onClick = {
                                            syncScope.launch {
                                                editorContext.eventHandler.send(ShowLoading)
                                                editorContext.eventHandler.send(
                                                    WorkflowStepChanged(label = "Sync requested"),
                                                )
                                                // Simulate asynchronous app work before clearing the loading event.
                                                delay(1_200)
                                                editorContext.eventHandler.send(HideLoading)
                                            }
                                        },
                                    ) {
                                        Text("Run Sync")
                                    }
                                }
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }
                            }
                        }
                    }
                }
                // highlight-android-overlay-actions
            }
        },
        onClose = onClose,
    )
}
