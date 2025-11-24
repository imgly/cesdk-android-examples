import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun ColorPaletteEditorSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>", // pass null or empty for evaluation mode with watermark
    )
    val editorConfiguration = EditorConfiguration.rememberForDesign(
        // highlight-configuration-colorPalette
        colorPalette = remember {
            listOf(
                Color(0xFF4A67FF),
                Color(0xFFFFD333),
                Color(0xFFC41230),
                Color(0xFF000000),
                Color(0xFFFFFFFF),
            )
        },
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
