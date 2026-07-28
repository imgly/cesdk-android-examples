import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary

@Composable
fun AssetLibraryEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            // highlight-android-asset-library-configuration
            EditorConfiguration.remember {
                assetLibrary = {
                    remember { AssetLibrary.getDefault() }
                }
            }
            // highlight-android-asset-library-configuration
        },
        onClose = onClose,
    )
}
