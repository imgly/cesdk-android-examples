import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration

// Add this composable to your NavHost
@Composable
fun ColorPaletteEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        remember {
            EngineConfiguration.getForDesign(license = "<your license here>")
        }
    val editorConfiguration =
        EditorConfiguration.getDefault(
            // highlight-configuration-colorPalette
            colorPalette =
                listOf(
                    Color(0xFF4A67FF),
                    Color(0xFFFFD333),
                    Color(0xFFC41230),
                    Color(0xFF000000),
                    Color(0xFFFFFFFF),
                ),
            // highlight-configuration-colorPalette
        )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
