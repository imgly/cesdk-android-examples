import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.editor.configuration.photo.callback.onCreate
import ly.img.editor.core.component.Dock
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.backgroundRemoval.BackgroundRemovalPlugin
import ly.img.editor.plugin.backgroundRemoval.rememberBackgroundRemoval

// Add this composable to your NavHost.
@Composable
fun BackgroundRemovalEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-plugin
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                editorContext.engine.scene.createFromImage(
                                    imageUri = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80".toUri(),
                                )
                            },
                        )
                    }
                }
                .then(::BackgroundRemovalPlugin) {
                    // Optional configurations here.
                }
        },
        onClose = onClose,
    )
    // highlight-android-plugin
}

@Composable
private fun BackgroundRemovalEditorSolutionWithConfigurations(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                editorContext.engine.scene.createFromImage(
                                    imageUri = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80".toUri(),
                                )
                            },
                        )
                    }
                }
                .then(::BackgroundRemovalPlugin) {
                    // highlight-android-dock-modifier
                    dockModifier = {
                        addFirst { Dock.Button.rememberBackgroundRemoval() }
                    }
                    // highlight-android-dock-modifier
                }
        },
        onClose = onClose,
    )
}
