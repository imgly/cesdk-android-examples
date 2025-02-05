import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.rememberBringForward
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberSendBackward
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun NewListBuilderCanvasMenuSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
        )

    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            canvasMenu = {
                CanvasMenu.remember(
                    // highlight-newListBuilder
                    listBuilder =
                        CanvasMenu.ListBuilder.remember {
                            add {
                                CanvasMenu.Button.remember(
                                    id = EditorComponentId("my.package.canvasMenu.button.custom"),
                                    onClick = {},
                                    vectorIcon = null,
                                    text = { "Custom Button" },
                                )
                            }
                            add { CanvasMenu.Button.rememberSendBackward() }
                            add { CanvasMenu.Button.rememberBringForward() }
                            add { CanvasMenu.Divider.remember(visible = { editorContext.canSelectionMove }) }
                            add { CanvasMenu.Button.rememberDuplicate() }
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
