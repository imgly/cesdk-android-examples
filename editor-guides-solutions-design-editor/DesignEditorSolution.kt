import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration

// Add this composable to your NavHost
@Composable
fun DesignEditorSolution(navController: NavHostController) {
    // highlight-engine-configuration
    val engineConfiguration =
        remember {
            EngineConfiguration.getForDesign(
                license = "<your license here>",
                userId = "<your unique user id>",
            )
        }
    // highlight-engine-configuration
    // highlight-editor-configuration
    val editorConfiguration = EditorConfiguration.getDefault()
    // highlight-editor-configuration
    // highlight-editor-initialization
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
    // highlight-editor-initialization
}
