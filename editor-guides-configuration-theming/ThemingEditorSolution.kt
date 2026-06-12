import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ly.img.editor.Editor
import ly.img.editor.EditorUiMode
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Replace

// Add this composable to your NavHost
@Composable
fun ThemingEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-theme-state
    var uiMode by remember { mutableStateOf(EditorUiMode.LIGHT) }
    // highlight-android-theme-state

    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        // highlight-android-editor-uimode
        uiMode = uiMode,
        // highlight-android-editor-uimode
        // highlight-android-theme-toggle
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        horizontalArrangement = { Arrangement.Center }
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add {
                                    Dock.Button.remember {
                                        textString = {
                                            if (uiMode == EditorUiMode.DARK) {
                                                "Use Light Theme"
                                            } else {
                                                "Use Dark Theme"
                                            }
                                        }
                                        vectorIcon = { IconPack.Replace }
                                        tint = { MaterialTheme.colorScheme.onSecondaryContainer }
                                        containerColor = { MaterialTheme.colorScheme.secondaryContainer }
                                        onClick = {
                                            uiMode = if (uiMode == EditorUiMode.DARK) {
                                                EditorUiMode.LIGHT
                                            } else {
                                                EditorUiMode.DARK
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        // highlight-android-theme-toggle
        onClose = onClose,
    )
}
