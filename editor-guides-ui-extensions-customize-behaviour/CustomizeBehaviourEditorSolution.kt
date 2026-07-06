import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ly.img.editor.Editor
import ly.img.editor.EditorUiMode
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.Export
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.PlayBox
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType

// highlight-android-state
data class CustomizeBehaviourState(
    val latestEvent: String = "No scene changes observed yet.",
    val dialogMessage: String? = null,
)
// highlight-android-state

// highlight-android-custom-events
object ValidateSelection : EditorEvent

object OpenWorkflowSheet : EditorEvent
// highlight-android-custom-events

@Composable
fun CustomizeBehaviourEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    var uiMode by remember { mutableStateOf(EditorUiMode.LIGHT) }

    Editor(
        license = license,
        // highlight-android-theme
        uiMode = uiMode,
        // highlight-android-theme
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-editor-state
                var state by editorContext.mutableStateOf(
                    key = "customize_behaviour_state",
                    initial = CustomizeBehaviourState(),
                )
                // highlight-android-editor-state

                // highlight-android-listen-events
                onLoaded = {
                    editorContext.engine.event
                        .subscribe(blocks = emptyList())
                        .onEach { events ->
                            if (events.isNotEmpty()) {
                                state = state.copy(
                                    latestEvent = "Received ${events.size} block event(s).",
                                )
                            }
                        }
                        .launchIn(editorContext.coroutineScope)
                }
                // highlight-android-listen-events

                // highlight-android-handle-events
                onEvent = { event ->
                    when (event) {
                        is ValidateSelection -> {
                            val selectedCount = editorContext.engine.block.findAllSelected().size
                            state = state.copy(
                                dialogMessage = if (selectedCount == 0) {
                                    "Select a block before running this workflow."
                                } else {
                                    "Workflow can run for $selectedCount selected block(s)."
                                },
                            )
                        }

                        is OpenWorkflowSheet -> {
                            editorContext.eventHandler.send(
                                EditorEvent.Sheet.Open(
                                    type = workflowSheet(latestEvent = state.latestEvent),
                                ),
                            )
                        }
                    }
                }
                // highlight-android-handle-events

                // highlight-android-dock-buttons
                dock = {
                    Dock.remember {
                        horizontalArrangement = { Arrangement.Center }
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("customize_behaviour_validate") }
                                        textString = { "Validate" }
                                        vectorIcon = { IconPack.PlayBox }
                                        enabled = { state.dialogMessage == null }
                                        onClick = {
                                            editorContext.eventHandler.send(ValidateSelection)
                                        }
                                    }
                                }
                                add {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("customize_behaviour_panel") }
                                        textString = { "Status" }
                                        vectorIcon = { IconPack.Export }
                                        onClick = {
                                            editorContext.eventHandler.send(OpenWorkflowSheet)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // highlight-android-dock-buttons

                // highlight-android-overlay
                overlay = {
                    EditorComponent.remember {
                        decoration = {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Button(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp),
                                    onClick = {
                                        uiMode = if (uiMode == EditorUiMode.DARK) {
                                            EditorUiMode.LIGHT
                                        } else {
                                            EditorUiMode.DARK
                                        }
                                    },
                                ) {
                                    Text("Toggle theme")
                                }
                            }

                            state.dialogMessage?.let { message ->
                                AlertDialog(
                                    onDismissRequest = {
                                        state = state.copy(dialogMessage = null)
                                    },
                                    title = { Text("Workflow update") },
                                    text = { Text(message) },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                state = state.copy(dialogMessage = null)
                                            },
                                        ) {
                                            Text("OK")
                                        }
                                    },
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

// highlight-android-custom-sheet
private fun workflowSheet(latestEvent: String): SheetType = SheetType.Custom(
    style = SheetStyle(
        isFloating = false,
        minHeight = Height.Exactly(0.dp),
        maxHeight = Height.Fraction(0.45F),
        isHalfExpandingEnabled = false,
        isHalfExpandedInitially = true,
    ),
    content = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Latest editor event")
            Spacer(modifier = Modifier.height(12.dp))
            Text(latestEvent)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    editorContext.eventHandler.send(EditorEvent.Sheet.Close(animate = true))
                },
            ) {
                Text("Close")
            }
        }
    },
)
// highlight-android-custom-sheet
