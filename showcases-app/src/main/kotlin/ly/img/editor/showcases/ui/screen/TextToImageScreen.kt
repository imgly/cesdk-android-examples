package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.configuration.design.callback.onCreate
import ly.img.editor.configuration.design.component.rememberDock
import ly.img.editor.configuration.design.component.rememberInspectorBar
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.modify
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ShowcasesBuildConfig
import ly.img.editor.showcases.plugin.ShowcasesPlugin
import ly.img.editor.showcases.plugin.imagegen.rememberCreateWithAIDockButton
import ly.img.editor.showcases.plugin.imagegen.rememberEditWithAIInspectorBarButton

@Composable
fun TextToImageScreen(
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
                dock = {
                    val dock = rememberDock()
                    val updatedListBuilder = dock.listBuilder.modify {
                        addFirst {
                            Dock.Button.rememberCreateWithAIDockButton(apiConfig = ShowcasesBuildConfig.FAL_PROXY_URL)
                        }
                    }
                    remember(dock, updatedListBuilder) {
                        dock.copy(listBuilder = updatedListBuilder)
                    }
                }
                inspectorBar = {
                    val inspectorBar = rememberInspectorBar()
                    val updatedListBuilder = inspectorBar.listBuilder.modify {
                        addFirst {
                            InspectorBar.Button.rememberEditWithAIInspectorBarButton(apiConfig = ShowcasesBuildConfig.FAL_PROXY_URL)
                        }
                    }
                    remember(inspectorBar, updatedListBuilder) {
                        inspectorBar.copy(listBuilder = updatedListBuilder)
                    }
                }
            }.then(::ShowcasesPlugin) {
                this.sceneUri = sceneUri
            }
        },
    ) {
        onBack()
    }
}
