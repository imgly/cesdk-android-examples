import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.library.AssetLibrary

// Add this composable to your NavHost
@Composable
fun AssetLibraryEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        remember {
            EngineConfiguration.getForDesign(license = "<your license here>")
        }
    // highlight-configuration-asset-library
    val editorConfiguration =
        EditorConfiguration.getDefault(
            assetLibrary = AssetLibrary.getDefault(),
        )
    // highlight-configuration-asset-library
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
