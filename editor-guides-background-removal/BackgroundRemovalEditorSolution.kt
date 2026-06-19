import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.editor.configuration.photo.callback.onCreate
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.backgroundRemoval.BackgroundRemovalConfig
import ly.img.editor.plugin.backgroundRemoval.BackgroundRemovalMask
import ly.img.editor.plugin.backgroundRemoval.BackgroundRemovalPlugin
import ly.img.editor.plugin.backgroundRemoval.GoogleBackgroundRemovalConfig
import ly.img.editor.plugin.backgroundRemoval.GoogleBackgroundRemovalPlugin
import ly.img.editor.plugin.backgroundRemoval.IMGLYBackgroundRemovalConfig
import ly.img.editor.plugin.backgroundRemoval.IMGLYBackgroundRemovalPlugin
import ly.img.editor.plugin.backgroundRemoval.rememberBackgroundRemoval
import ly.img.editor.plugin.backgroundRemoval.remover.BackgroundRemover
import okhttp3.OkHttpClient
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

// Add this composable to your NavHost.
@Composable
fun BackgroundRemovalEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-imgly-minimal
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
                .then(::IMGLYBackgroundRemovalPlugin)
        },
        onClose = onClose,
    )
    // highlight-android-imgly-minimal
}

@Composable
private fun BackgroundRemovalEditorSolutionWithIMGLYConfiguration(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-imgly-configuration
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder)
                .then(::IMGLYBackgroundRemovalPlugin) {
                    config = IMGLYBackgroundRemovalConfig(
                        model = IMGLYBackgroundRemovalConfig.Model.FP16,
                        modelBaseUri = "https://staticimgly.com/imgly/plugin-mobile-background-removal/1.0.0".toUri(),
                        loadMode = IMGLYBackgroundRemovalConfig.LoadMode.EAGER,
                        httpClient = OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .build(),
                    )
                    dockModifier = {
                        addFirst { Dock.Button.rememberBackgroundRemoval(config = it) }
                    }
                }
        },
        onClose = onClose,
    )
    // highlight-android-imgly-configuration
}

@Composable
private fun BackgroundRemovalEditorSolutionWithGoogle(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-google-minimal
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder)
                .then(::GoogleBackgroundRemovalPlugin)
        },
        onClose = onClose,
    )
    // highlight-android-google-minimal
}

@Composable
private fun BackgroundRemovalEditorSolutionWithGoogleConfiguration(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-google-configuration
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder)
                .then(::GoogleBackgroundRemovalPlugin) {
                    config = GoogleBackgroundRemovalConfig(
                        httpClient = OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .build(),
                    )
                    dockModifier = {
                        addFirst { Dock.Button.rememberBackgroundRemoval(config = it) }
                    }
                }
        },
        onClose = onClose,
    )
    // highlight-android-google-configuration
}

// highlight-android-custom-remover
private data class CustomBackgroundRemovalConfig(
    override val httpClient: OkHttpClient = OkHttpClient(),
) : BackgroundRemovalConfig {
    override val remover: BackgroundRemover<*> = CustomBackgroundRemover()
}

private class CustomBackgroundRemover : BackgroundRemover<CustomBackgroundRemovalConfig> {
    override fun EditorScope.initialize() {
        // Prepare local models, SDK clients, or service credentials here.
    }

    override suspend fun EditorScope.processImage(bitmap: Bitmap): BackgroundRemovalMask {
        val width = bitmap.width
        val height = bitmap.height
        val buffer = ByteBuffer
            .allocateDirect(width * height * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())

        // Dummy data filled with 1s.
        repeat(width * height) {
            buffer.putFloat(1f)
        }
        buffer.rewind()

        return BackgroundRemovalMask(
            buffer = buffer,
            width = width,
            height = height,
        )
    }
}
// highlight-android-custom-remover

@Composable
private fun BackgroundRemovalEditorSolutionWithCustomRemover(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-custom-plugin
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder)
                .then(::BackgroundRemovalPlugin) {
                    config = CustomBackgroundRemovalConfig()
                }
        },
        onClose = onClose,
    )
    // highlight-android-custom-plugin
}

@Composable
private fun BackgroundRemovalEditorSolutionWithCustomDockModifier(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-custom-dock-modifier
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::PhotoConfigurationBuilder)
                .then(::BackgroundRemovalPlugin) {
                    config = CustomBackgroundRemovalConfig()
                    dockModifier = {
                        addFirst { Dock.Button.rememberBackgroundRemoval(config = it) }
                    }
                }
        },
        onClose = onClose,
    )
    // highlight-android-custom-dock-modifier
}
