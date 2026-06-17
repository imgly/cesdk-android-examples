import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.Timeline
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberVoiceover
import ly.img.editor.core.component.rememberVoiceoverRecord
import ly.img.editor.core.component.rememberVolume
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun RecordVoiceoverSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-editor
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                bottomPanel = { Timeline.remember() }
                dock = { rememberVoiceoverDock() }
                inspectorBar = { rememberVoiceoverInspectorBar() }
            }
        },
        onClose = onClose,
    )
    // highlight-android-editor
}

// highlight-android-dock
@Composable
private fun rememberVoiceoverDock() = Dock.remember {
    listBuilder = {
        Dock.ListBuilder.remember {
            add { Dock.Button.rememberVoiceoverRecord() }
        }
    }
}
// highlight-android-dock

// highlight-android-inspector
@Composable
private fun rememberVoiceoverInspectorBar() = InspectorBar.remember {
    listBuilder = {
        InspectorBar.ListBuilder.remember {
            add { InspectorBar.Button.rememberVoiceover() }
            add { InspectorBar.Button.rememberVolume() }
            add { InspectorBar.Button.rememberDelete() }
        }
    }
}
// highlight-android-inspector
