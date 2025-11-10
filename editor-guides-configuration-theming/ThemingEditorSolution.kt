import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorUiMode
import ly.img.editor.EngineConfiguration
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun ThemingEditorSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>", // pass null or empty for evaluation mode with watermark
    )
    val editorConfiguration = EditorConfiguration.rememberForDesign(
        // highlight-configuration-uiMode
        uiMode = EditorUiMode.DARK,
        // highlight-configuration-uiMode
    )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
