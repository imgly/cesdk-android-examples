import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.rememberForDesign

@Composable
fun CustomPanelSolution(navController: NavHostController) {
    val engineConfig = EngineConfiguration.rememberForDesign(
        license = "<your license here>", // pass null or empty for evaluation mode with watermark
    )

    val editorConfig = EditorConfiguration.rememberForDesign(
        dock = {
            Dock.rememberForDesign(
                listBuilder = Dock.ListBuilder.rememberForDesign().modify {
                    addFirst {
                        openCustomPanelDockButton
                    }
                },
            )
        },
    )

    DesignEditor(
        engineConfiguration = engineConfig,
        editorConfiguration = editorConfig,
    ) {
        navController.popBackStack()
    }
}

val openCustomPanelDockButton
    @Composable get() = Dock.Button.remember(
        id = EditorComponentId("open_panel"),
        text = { Text("Open Panel") },
        icon = { Icon(Icons.Rounded.KeyboardArrowUp, null) },
        onClick = {
            editorContext.eventHandler.send(
                EditorEvent.Sheet.Open(
                    customSheetType,
                ),
            )
        },
    )

val customSheetType: SheetType
    get() =
        // highlight-open-custom-panel
        SheetType.Custom(
            style = SheetStyle(
                isFloating = false,
                minHeight = Height.Exactly(0.dp),
                maxHeight = Height.Fraction(0.7F),
                isHalfExpandingEnabled = true,
                isHalfExpandedInitially = true,
                animateInitialValue = true,
            ),
            content = {
                Column {
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
