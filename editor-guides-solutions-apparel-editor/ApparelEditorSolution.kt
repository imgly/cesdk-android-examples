import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.ApparelEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration

// Add this composable to your NavHost
@Composable
fun ApparelEditorSolution(navController: NavHostController) {
    // highlight-engine-configuration
    val engineConfiguration =
        EngineConfiguration.rememberForApparel(
            license = "<your license here>",
            userId = "<your unique user id>",
        )
    // highlight-engine-configuration
    // highlight-editor-configuration
    val editorConfiguration = EditorConfiguration.rememberForApparel()
    // highlight-editor-configuration
    // highlight-editor-initialization
    ApparelEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
    // highlight-editor-initialization
}
