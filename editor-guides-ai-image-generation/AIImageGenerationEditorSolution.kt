import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.ai.core.gateway.AIGatewayConfig
import ly.img.editor.plugin.ai.core.gateway.AIGatewayImageModel
import ly.img.editor.plugin.ai.imageGeneration.AIImageGenerationPlugin
import ly.img.editor.plugin.ai.imageGeneration.rememberAIImageGeneration
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// Add this composable to your NavHost.
@Composable
fun AIImageGenerationEditorSolution(
    license: String,
    aiGatewayApiKey: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-plugin
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::DesignConfigurationBuilder)
                .then(::AIImageGenerationPlugin) {
                    // Required configuration
                    aiGatewayConfig = AIGatewayConfig(
                        apiKey = aiGatewayApiKey,
                    )
                    // Optional configurations here.
                }
        },
        onClose = onClose,
    )
    // highlight-android-plugin
}

@Composable
private fun AIImageGenerationEditorSolutionWithConfigurations(
    license: String,
    aiGatewayApiKey: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::DesignConfigurationBuilder)
                .then(::AIImageGenerationPlugin) {
                    // highlight-android-gateway
                    aiGatewayConfig = AIGatewayConfig(
                        apiKey = aiGatewayApiKey,
                        model = AIGatewayImageModel.FluxV2,
                        gatewayUrl = "https://gateway.img.ly",
                        httpClient = OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .build(),
                    )
                    // highlight-android-gateway
                    // highlight-android-dock-modifier
                    dockModifier = {
                        addFirst { Dock.Button.rememberAIImageGeneration(aiGatewayConfig = it) }
                    }
                    // highlight-android-dock-modifier
                    // highlight-android-inspector-bar-modifier
                    inspectorBarModifier = {
                        addFirst { InspectorBar.Button.rememberAIImageGeneration(aiGatewayConfig = it) }
                    }
                    // highlight-android-inspector-bar-modifier
                }
        },
        onClose = onClose,
    )
}
