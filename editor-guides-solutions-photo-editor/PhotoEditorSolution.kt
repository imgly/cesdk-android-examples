import android.net.Uri
import android.util.SizeF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration

// Add this composable to your NavHost
@Composable
fun PhotoEditorSolution(navController: NavHostController) {
    // highlight-engine-configuration
    val engineConfiguration =
        remember {
            EngineConfiguration.getForPhoto(
                license = "<your license here>",
                imageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_4.jpg"),
                imageSize = SizeF(1080F, 1920F),
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
