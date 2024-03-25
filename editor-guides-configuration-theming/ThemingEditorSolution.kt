import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorUiMode
import ly.img.editor.EngineConfiguration

// Add this composable to your NavHost
@Composable
fun ThemingEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        remember {
            EngineConfiguration.getForDesign(license = "<your license here>")
        }
    val editorConfiguration =
        EditorConfiguration.getDefault(
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
