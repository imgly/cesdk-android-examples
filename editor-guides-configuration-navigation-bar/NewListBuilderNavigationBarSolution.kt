import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun NewListBuilderNavigationBarSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )

    val editorConfiguration = EditorConfiguration.rememberForDesign(
        navigationBar = {
            NavigationBar.remember(
                // highlight-newListBuilder
                listBuilder = NavigationBar.ListBuilder.remember {
                    aligned(alignment = Alignment.Start) {
                        add { NavigationBar.Button.rememberCloseEditor() }
                    }
                    aligned(alignment = Alignment.CenterHorizontally) {
                        add {
                            NavigationBar.Button.remember(
                                id = EditorComponentId("my.package.navigationBar.button.custom"),
                                vectorIcon = null,
                                text = { "Custom Button" },
                                onClick = {},
                            )
                        }
                    }
                    aligned(
                        alignment = Alignment.End,
                        arrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        add { NavigationBar.Button.rememberExport() }
                        add { NavigationBar.Button.rememberUndo() }
                        add { NavigationBar.Button.rememberRedo() }
                    }
                },
                // highlight-newListBuilder
            )
        },
    )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
