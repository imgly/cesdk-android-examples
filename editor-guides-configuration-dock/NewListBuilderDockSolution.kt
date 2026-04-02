import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberElementsLibrary
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.component.rememberStickersLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun NewListBuilderDockSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        listBuilder = {
                            // highlight-newListBuilder
                            Dock.ListBuilder.remember {
                                add {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.custom") }
                                        vectorIcon = null
                                        textString = { "Custom Button" }
                                        onClick = {}
                                    }
                                }
                                add { Dock.Button.rememberSystemGallery() }
                                add { Dock.Button.rememberSystemCamera() }
                                add { Dock.Button.rememberElementsLibrary() }
                                add { Dock.Button.rememberStickersLibrary() }
                                add { Dock.Button.rememberImagesLibrary() }
                                add { Dock.Button.rememberTextLibrary() }
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
