import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.NavHostController
import ly.img.editor.Editor
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
fun CustomPanelSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        horizontalArrangement = { Arrangement.Center }
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { openCustomPanelDockButton }
                            }
                        }
                    }
                }
            }
        },
    ) {
        navController.popBackStack()
    }
}

val openCustomPanelDockButton
    @Composable get() = Dock.Button.remember {
        id = { EditorComponentId("open_panel") }
        text = { Text("Open Panel") }
        icon = { Icon(Icons.Rounded.KeyboardArrowUp, null) }
        onClick = {
            editorContext.eventHandler.send(
                EditorEvent.Sheet.Open(customSheetType),
            )
        }
    }

val customSheetType: SheetType
    get() =
        // highlight-open-custom-panel
        SheetType.Custom(
            style = SheetStyle(
                isFloating = false,
                minHeight = Height.Exactly(0.dp),
                maxHeight = Height.Fraction(0.7F),
                isHalfExpandingEnabled = false,
                isHalfExpandedInitially = true,
                animateInitialValue = true,
            ),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Custom Panel")
                    Button(
                        onClick = {
                            // highlight-close-panel
                            editorContext.eventHandler.send(
                                EditorEvent.Sheet.Close(animate = true),
                            )
                            // highlight-close-panel
                        },
                    ) { Text("Close Panel") }
                }
            },
        )
// highlight-open-custom-panel
