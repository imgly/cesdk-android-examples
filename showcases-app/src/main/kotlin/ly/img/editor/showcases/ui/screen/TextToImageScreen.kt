package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.configuration.design.callback.onCreate
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.ai.core.gateway.AIGatewayConfig
import ly.img.editor.plugin.ai.imageGeneration.AIImageGenerationPlugin
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.decodeBase64
import ly.img.editor.showcases.plugin.ShowcasesPlugin

@Composable
fun TextToImageScreen(
    gatewayApiKey: String?,
    baseUri: Uri,
    sceneUri: Uri,
    onBack: () -> Unit,
) {
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::DesignConfigurationBuilder) {
                onCreate = {
                    onCreate(createScene = { getOrLoadScene(sceneUri = sceneUri) })
                }
            }.then(::ShowcasesPlugin) {
                this.sceneUri = sceneUri
            }.then(::AIImageGenerationPlugin) {
                this.aiGatewayConfig = AIGatewayConfig(
                    apiKey = gatewayApiKey?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX) ?: "",
                )
            }
        },
    ) {
        onBack()
    }
}
