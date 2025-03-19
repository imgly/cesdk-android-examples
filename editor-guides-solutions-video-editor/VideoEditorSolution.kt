import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.VideoEditor
import ly.img.editor.rememberForVideo

// Add this composable to your NavHost
@Composable
fun VideoEditorSolution(navController: NavHostController) {
    // highlight-engine-configuration
    val engineConfiguration = EngineConfiguration.rememberForVideo(
        license = "<your license here>",
        userId = "<your unique user id>",
    )
    // highlight-engine-configuration
    // highlight-editor-configuration
    val editorConfiguration = EditorConfiguration.rememberForVideo()
    // highlight-editor-configuration
    // highlight-editor-initialization
    VideoEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
    // highlight-editor-initialization
}
