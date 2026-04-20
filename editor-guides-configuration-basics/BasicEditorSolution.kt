import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.engine.EngineRenderTarget

// Add this composable to your NavHost
@Composable
fun BasicEditorSolution(navController: NavHostController) {
    Editor(
        // highlight-configuration-license
        license = null, // pass null or empty for evaluation mode with watermark
        // highlight-configuration-license
        // highlight-configuration-userId
        userId = "<your unique user id>",
        // highlight-configuration-userId
        // highlight-configuration-baseUri
        baseUri = "file:///android_asset/".toUri(),
        // highlight-configuration-baseUri
        // highlight-configuration-renderTarget
        engineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        // highlight-configuration-renderTarget
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
