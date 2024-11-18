import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EngineConfiguration

// Add this composable to your NavHost
@Composable
fun EditorComposable(navController: NavHostController) {
    // highlight-engine-configuration
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
            userId = "<your unique user id>",
        )
    // highlight-engine-configuration
    // highlight-editor-invoke
    DesignEditor(engineConfiguration = engineConfiguration) {
        // You can set result here
        navController.popBackStack()
    }
    // highlight-editor-invoke
}
