package ly.img.editor.showcases.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.PhotoEditor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.rememberForPhoto
import ly.img.editor.rememberForPhoto
import ly.img.editor.showcases.plugin.backgroundremoval.rememberBackgroundRemovalDockButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundRemovalScreen(
    engineConfiguration: EngineConfiguration,
    onBack: () -> Unit,
) {
    val editorConfiguration = EditorConfiguration.rememberForPhoto(
        dock = {
            Dock.rememberForPhoto(
                listBuilder = Dock.ListBuilder.rememberForPhoto().modify {
                    addFirst {
                        Dock.rememberBackgroundRemovalDockButton()
                    }
                },
            )
        },
    )

    PhotoEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
        onClose = { onBack() },
    )
}
