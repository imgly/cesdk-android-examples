import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.core.engine.EngineRenderTarget

// Add this composable to your NavHost
@Composable
fun BasicEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        // highlight-configuration-license
        license = license, // pass null or empty for evaluation mode with watermark
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
        onClose = onClose,
    )
}
