import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun NewListBuilderNavigationBarSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                navigationBar = {
                    NavigationBar.remember {
                        listBuilder = {
                            // highlight-newListBuilder
                            NavigationBar.ListBuilder.remember {
                                aligned(alignment = Alignment.Start) {
                                    add { NavigationBar.Button.rememberCloseEditor() }
                                }
                                aligned(alignment = Alignment.CenterHorizontally) {
                                    add {
                                        NavigationBar.Button.remember {
                                            id = { EditorComponentId("my.package.navigationBar.button.custom") }
                                            vectorIcon = null
                                            textString = { "Custom Button" }
                                            onClick = {}
                                        }
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
                            }
                            // highlight-newListBuilder
                        }
                    }
                }
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
