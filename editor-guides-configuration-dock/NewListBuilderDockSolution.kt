import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.rememberElementsLibrary
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.component.rememberStickersLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun NewListBuilderDockSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
        )

    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            dock = {
                Dock.remember(
                    // highlight-newListBuilder
                    listBuilder =
                        Dock.ListBuilder.remember {
                            add {
                                val buttonScope =
                                    LocalEditorScope.current.run {
                                        remember(this) { Dock.ButtonScope(parentScope = this) }
                                    }
                                Dock.Button.remember(
                                    id = EditorComponentId("my.package.dock.button.custom"),
                                    vectorIcon = null,
                                    text = { "Custom Button" },
                                    onClick = {},
                                    scope = buttonScope,
                                )
                            }
                            add { Dock.Button.rememberSystemGallery() }
                            add { Dock.Button.rememberSystemCamera() }
                            add { Dock.Button.rememberElementsLibrary() }
                            add { Dock.Button.rememberStickersLibrary() }
                            add { Dock.Button.rememberImagesLibrary() }
                            add { Dock.Button.rememberTextLibrary() }
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
