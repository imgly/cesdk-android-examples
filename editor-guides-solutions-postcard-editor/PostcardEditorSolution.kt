import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.PostcardEditor
import ly.img.editor.rememberForPostcard

// Add this composable to your NavHost
@Composable
fun PostcardEditorSolution(navController: NavHostController) {
    // highlight-engine-configuration
    val engineConfiguration = EngineConfiguration.rememberForPostcard(
        license = "<your license here>",
        userId = "<your unique user id>",
    )
    // highlight-engine-configuration
    // highlight-editor-configuration
    val editorConfiguration = EditorConfiguration.rememberForPostcard()
    // highlight-editor-configuration
    // highlight-editor-initialization
    PostcardEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
    // highlight-editor-initialization
}
