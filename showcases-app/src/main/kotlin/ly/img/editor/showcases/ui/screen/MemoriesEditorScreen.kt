package ly.img.editor.showcases.ui.screen

import androidx.compose.runtime.Composable
import ly.img.editor.configuration.memories.MemoriesApp
import ly.img.editor.showcases.Secrets

/**
 * Opens the Memories starter kit through its full picker → editor flow (unified with the
 * standalone app and the examples app).
 */
@Composable
fun MemoriesEditorScreen(onBack: () -> Unit) {
    MemoriesApp(
        license = Secrets.license,
        onExit = onBack,
    )
}
