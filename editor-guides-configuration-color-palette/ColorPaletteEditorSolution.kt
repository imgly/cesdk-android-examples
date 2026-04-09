import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun ColorPaletteEditorSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-configuration-colorPalette
                colorPalette = {
                    remember {
                        listOf(
                            Color(0xFF4A67FF),
                            Color(0xFFFFD333),
                            Color(0xFFC41230),
                            Color(0xFF000000),
                            Color(0xFFFFFFFF),
                        )
                    }
                }
                // highlight-configuration-colorPalette
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
