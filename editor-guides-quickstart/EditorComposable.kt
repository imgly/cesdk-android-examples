import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.DesignBlockType

// Add this composable to your Activity, Fragment, NavHost etc.
@Composable
fun EditorComposable(onClose: (Throwable?) -> Unit) {
    val context = LocalContext.current
    Editor(
        // Get your license from https://img.ly/forms/free-trial
        // Keep this null for evaluation mode with watermark.
        // Replace it with your license key for production use.
        license = null,
        configuration = {
            // highlight-engine-configuration
            EditorConfiguration.remember {
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(block = page, value = 1080F)
                    editorContext.engine.block.setHeight(block = page, value = 1080F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)
                }
                onError = {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
            // highlight-engine-configuration
        },
        onClose = onClose,
    )
}
