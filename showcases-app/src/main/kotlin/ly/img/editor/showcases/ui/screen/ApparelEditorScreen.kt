package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.configuration.apparel.callback.onCreate
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.plugin.ShowcasesPlugin

@Composable
fun ApparelEditorScreen(
    baseUri: Uri,
    sceneUri: Uri,
    onBack: () -> Unit,
) {
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::ApparelConfigurationBuilder) {
                onCreate = {
                    onCreate(createScene = { getOrLoadScene(sceneUri = sceneUri) })
                }
            }.then(::ShowcasesPlugin) {
                this.sceneUri = sceneUri
            }
        },
    ) {
        onBack()
    }
}
