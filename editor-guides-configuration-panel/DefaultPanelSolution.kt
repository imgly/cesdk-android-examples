import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.Editor
import ly.img.editor.core.EditorContext
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType

@Composable
fun DefaultPanelSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { openPanelDockButton }
                                add { openCustomPanelDockButton }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}

val openPanelDockButton
    @Composable get() = Dock.Button.remember {
        id = { EditorComponentId("open_library_panel") }
        text = { Text("Open Library") }
        icon = { Icon(Icons.Rounded.KeyboardArrowUp, null) }
        onClick = {
            // highlight-android-open-panel
            editorContext.eventHandler.send(
                EditorEvent.Sheet.Open(
                    SheetType.LibraryAdd(),
                ),
            )
            // highlight-android-open-panel
        }
    }

val openCustomPanelDockButton
    @Composable get() = Dock.Button.remember {
        id = { EditorComponentId("open_custom_panel") }
        text = { Text("Custom Panel") }
        icon = { Icon(Icons.Rounded.KeyboardArrowUp, null) }
        onClick = {
            // highlight-android-open-custom-panel-event
            editorContext.eventHandler.send(
                EditorEvent.Sheet.Open(
                    customPanelSheetType(),
                ),
            )
            // highlight-android-open-custom-panel-event
        }
    }

fun customPanelSheetType(): SheetType {
    // highlight-android-open-custom-panel
    return SheetType.Custom(
        style = SheetStyle(
            isFloating = false,
            minHeight = Height.Exactly(0.dp),
            maxHeight = Height.Fraction(0.5F),
            isHalfExpandingEnabled = false,
            isHalfExpandedInitially = false,
            animateInitialValue = true,
        ),
        content = {
            SimpleCustomPanelContent(editorContext = editorContext)
        },
    )
    // highlight-android-open-custom-panel
}

@Composable
private fun SimpleCustomPanelContent(editorContext: EditorContext) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text("Custom panel")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Render native Compose content here.")
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                // highlight-android-close-panel
                editorContext.eventHandler.send(EditorEvent.Sheet.Close(animate = true))
                // highlight-android-close-panel
            },
        ) {
            Text("Done")
        }
    }
}
