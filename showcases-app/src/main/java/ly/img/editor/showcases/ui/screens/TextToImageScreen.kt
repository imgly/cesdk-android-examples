package ly.img.editor.showcases.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.rememberForDesign
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ShowcasesBuildConfig
import ly.img.editor.showcases.plugin.imagegen.rememberCreateWithAIDockButton
import ly.img.editor.showcases.plugin.imagegen.rememberEditWithAIInspectorBarButton

@Composable
fun TextToImageScreen(
    sceneUri: Uri,
    onBack: () -> Unit,
) {
    val editorConfiguration = EditorConfiguration.rememberForDesign(
        dock = {
            Dock.rememberForDesign(
                listBuilder = Dock.ListBuilder.rememberForDesign().modify {
                    addFirst {
                        Dock.Button.rememberCreateWithAIDockButton(apiConfig = ShowcasesBuildConfig.FAL_PROXY_URL)
                    }
                },
            )
        },
        inspectorBar = {
            InspectorBar.remember(
                listBuilder = InspectorBar.ListBuilder.remember().modify {
                    addFirst {
                        InspectorBar.Button.rememberEditWithAIInspectorBarButton(apiConfig = ShowcasesBuildConfig.FAL_PROXY_URL)
                    }
                },
            )
        },
    )

    DesignEditor(
        engineConfiguration = EngineConfiguration.rememberForDesign(
            license = Secrets.license,
            sceneUri = sceneUri,
        ),
        editorConfiguration = editorConfiguration,
        onClose = { onBack() },
    )
}
