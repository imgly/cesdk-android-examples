package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.autoCaptions.AutoCaptionsPlugin
import ly.img.editor.plugin.autoCaptions.gateway.GatewayTranscriptionProvider
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.decodeBase64

/**
 * Video Editor with automatic caption generation provided by the auto-captions plugin.
 *
 * Opens the empty video scene, so the user brings their own footage through Gallery — the bundled
 * demo clips are silent, and Generate Automatically needs speech to transcribe.
 *
 * The IMG.LY Gateway API key is collected by the showcase grid before this screen opens.
 */
@Composable
fun AutoCaptionsScreen(
    gatewayApiKey: String?,
    baseUri: Uri,
    onBack: () -> Unit,
) {
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder).then(::AutoCaptionsPlugin) {
                this.provider = GatewayTranscriptionProvider(
                    apiKey = gatewayApiKey?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX) ?: "",
                )
            }
        },
    ) {
        onBack()
    }
}
