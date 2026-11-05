import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.configuration.design.callback.onCreate
import ly.img.editor.configuration.design.callback.onCreateScene
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
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
import ly.img.engine.FillType
import okhttp3.OkHttpClient
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

// Add this composable to your NavHost.
// The demo gives the first page a photo, so that the dock button has an image to operate on, and adds the inspector
// bar button for images on the page. The guide shows the minimal setup in BackgroundRemovalEditorSolutionMinimal.
@Composable
fun BackgroundRemovalEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::DesignConfigurationBuilder) {
                    onCreate = {
                        onCreate(
                            createScene = {
                                onCreateScene()
                                val engine = editorContext.engine
                                val page = engine.scene.getPages().first()
                                val colorFill = engine.block.getFill(page)
                                val imageFill = engine.block.createFill(FillType.Image)
                                engine.block.setUri(
                                    block = imageFill,
                                    property = "fill/image/imageFileURI",
                                    value = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80".toUri(),
                                )
                                engine.block.setFill(block = page, fill = imageFill)
                                engine.block.destroy(colorFill)
                            },
                        )
                    }
                }
                .then(::IMGLYBackgroundRemovalPlugin) {
                    inspectorBarModifier = {
                        addFirst { InspectorBar.Button.rememberBackgroundRemoval(config = it) }
                    }
                }
        },
        onClose = onClose,
    )
}

@Composable
private fun BackgroundRemovalEditorSolutionMinimal(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-imgly-minimal
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::DesignConfigurationBuilder)
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
                .remember(::DesignConfigurationBuilder)
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
                    inspectorBarModifier = {
                        addFirst { InspectorBar.Button.rememberBackgroundRemoval(config = it) }
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
                .remember(::DesignConfigurationBuilder)
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
                .remember(::DesignConfigurationBuilder)
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
                    inspectorBarModifier = {
                        addFirst { InspectorBar.Button.rememberBackgroundRemoval(config = it) }
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
                .remember(::DesignConfigurationBuilder)
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
                .remember(::DesignConfigurationBuilder)
                .then(::BackgroundRemovalPlugin) {
                    config = CustomBackgroundRemovalConfig()
                    dockModifier = {
                        addLast { Dock.Button.rememberBackgroundRemoval(config = it) }
                    }
                    inspectorBarModifier = {
                        addLast { InspectorBar.Button.rememberBackgroundRemoval(config = it) }
                    }
                }
        },
        onClose = onClose,
    )
    // highlight-android-custom-dock-modifier
}
