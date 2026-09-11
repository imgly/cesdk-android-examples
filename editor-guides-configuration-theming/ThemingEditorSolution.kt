import androidx.compose.foundation.layout.Arrangement
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
    var uiMode by remember { mutableStateOf(EditorUiMode.LIGHT) }
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        // highlight-configuration-uiMode
        uiMode = uiMode,
        // highlight-configuration-uiMode
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    rememberDock(
                        onSwitchClick = {
                            uiMode = if (uiMode == EditorUiMode.LIGHT) {
                                EditorUiMode.DARK
                            } else {
                                EditorUiMode.LIGHT
                            }
                        },
                    )
                }
            }
        },
        onClose = onClose,
    )
}

@Composable
private fun rememberDock(onSwitchClick: () -> Unit) = Dock.remember {
    horizontalArrangement = { Arrangement.Center }
    listBuilder = {
        Dock.ListBuilder.remember {
            add {
                Dock.Button.remember {
                    textString = { "Switch Theme" }
                    vectorIcon = { IconPack.Replace }
                    onClick = { onSwitchClick() }
                }
            }
        }
    }
}
