import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.Editor
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.DefaultDecoration
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDefaultScope
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.DesignBlockType

// Add this composable to your NavHost
@Composable
fun SimpleCanvasMenuSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            // highlight-canvasMenuConfiguration
            EditorConfiguration.remember {
                canvasMenu = {
                    CanvasMenu.remember {
                        // Implementation is too large, check the implementation of CanvasMenu.rememberDefaultScope
                        scope = {
                            CanvasMenu.rememberDefaultScope(parentScope = this)
                        }
                        modifier = { Modifier }
                        visible = {
                            val editorState by editorContext.state.collectAsState()
                            remember(this, editorState) {
                                editorState.isTouchActive.not() &&
                                    editorState.activeSheet == null &&
                                    editorContext.safeSelection != null &&
                                    editorContext.selection.type != DesignBlockType.Page &&
                                    editorContext.selection.type != DesignBlockType.Audio &&
                                    editorContext.engine.editor.getEditMode() != "Text" &&
                                    editorContext.isScenePlaying.not() &&
                                    editorContext.selection.isVisibleAtCurrentPlaybackTime
                            }
                        }
                        enterTransition = { EnterTransition.None }
                        exitTransition = { ExitTransition.None }
                        // Implementation is too large, check the implementation of CanvasMenu.DefaultDecoration
                        decoration = { CanvasMenu.DefaultDecoration(scope = this) { it() } }
                        listBuilder = { CanvasMenu.ListBuilder.remember { /* Add items */ } }
                        // Default value is { it() }
                        itemDecoration = {
                            Box(modifier = Modifier.padding(2.dp)) {
                                it()
                            }
                        }
                    }
                }
            }
            // highlight-canvasMenuConfiguration
        },
        onClose = onClose,
    )
}
