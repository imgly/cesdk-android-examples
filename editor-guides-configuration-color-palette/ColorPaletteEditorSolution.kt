import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import ly.img.editor.Editor
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberFillStroke
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.DesignBlockType

// Add this composable to your NavHost
@Composable
fun ColorPaletteEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                onLoaded = {
                    editorContext.engine.block
                        .findByType(DesignBlockType.Page)
                        .firstOrNull()
                        ?.let { editorContext.engine.block.setSelected(it, selected = true) }
                }
                // highlight-configuration-colorPalette
                colorPalette = {
                    remember {
                        listOf(
                            Color(0xFF4A67FF),
                            Color(0xFFFFD333),
                            Color(0xFFC41230),
                            Color(0xFF000000),
                            Color(0xFFFFFFFF),
                        )
                    }
                }
                // highlight-configuration-colorPalette
                inspectorBar = {
                    InspectorBar.remember {
                        listBuilder = {
                            InspectorBar.ListBuilder.remember {
                                add { InspectorBar.Button.rememberFillStroke() }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
