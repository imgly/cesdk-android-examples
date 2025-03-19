import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun AssetLibraryEditorSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )
    // highlight-configuration-asset-library
    val editorConfiguration = EditorConfiguration.rememberForDesign(
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
