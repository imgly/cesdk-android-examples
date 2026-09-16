import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDefaultScope
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun SimpleInspectorBarSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            // highlight-android-inspector-bar-configuration
            EditorConfiguration.remember {
                inspectorBar = {
                    InspectorBar.remember {
                        // Reuse the editor selection and edit mode as the inspector bar scope.
                        scope = {
                            InspectorBar.rememberDefaultScope(parentScope = this)
                        }
                        listBuilder = {
                            InspectorBar.ListBuilder.remember {
                                add { InspectorBar.Button.rememberDelete() }
                            }
                        }
                    }
                }
            }
            // highlight-android-inspector-bar-configuration
        },
        onClose = onClose,
    )
}
