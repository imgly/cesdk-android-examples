import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.EditorUiMode

// Add this composable to your NavHost
@Composable
fun ThemingEditorSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        // highlight-configuration-uiMode
        uiMode = EditorUiMode.DARK,
        // highlight-configuration-uiMode
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
