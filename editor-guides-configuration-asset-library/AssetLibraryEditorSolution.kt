import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary

// Add this composable to your NavHost
@Composable
fun AssetLibraryEditorSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            // highlight-configuration-asset-library
            EditorConfiguration.remember {
                assetLibrary = {
                    remember { AssetLibrary.getDefault() }
                }
            }
            // highlight-configuration-asset-library
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
