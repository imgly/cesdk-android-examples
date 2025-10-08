import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.rememberForDesign

@Composable
fun DefaultPanelSolution(navController: NavHostController) {
    val engineConfig = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )

    val editorConfig = EditorConfiguration.rememberForDesign(
        dock = {
            Dock.rememberForDesign(
                listBuilder = Dock.ListBuilder.rememberForDesign().modify {
                    addFirst {
                        openPanelDockButton
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

val openPanelDockButton
    @Composable get() = Dock.Button.remember(
        id = EditorComponentId("open_library_panel"),
        text = { Text("Open Library") },
        icon = { Icon(Icons.Rounded.KeyboardArrowUp, null) },
        onClick = {
            // highlight-open-panel
            editorContext.eventHandler.send(
                EditorEvent.Sheet.Open(
                    SheetType.LibraryAdd(),
                ),
            )
            // highlight-open-panel
        },
    )
