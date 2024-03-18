import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.engine.EngineRenderTarget

// Add this composable to your NavHost
@Composable
fun BasicEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        remember {
            EngineConfiguration.getForDesign(
                // highlight-configuration-license
                license = "<your license here>",
                // highlight-configuration-license
                // highlight-configuration-userId
                userId = "<your unique user id>",
                // highlight-configuration-userId
                // highlight-configuration-baseUri
                baseUri = Uri.parse("file:///android_asset/"),
                // highlight-configuration-baseUri
                // highlight-configuration-sceneUri
                sceneUri = EngineConfiguration.defaultDesignSceneUri,
                // highlight-configuration-sceneUri
                // highlight-configuration-renderTarget
                renderTarget = EngineRenderTarget.SURFACE_VIEW,
                // highlight-configuration-renderTarget
            )
        }
    DesignEditor(engineConfiguration = engineConfiguration) {
        // You can set result here
        navController.popBackStack()
    }
}
