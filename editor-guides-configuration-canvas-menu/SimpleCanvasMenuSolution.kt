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
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.CanvasMenu.Companion.DefaultDecoration
import ly.img.editor.rememberForDesign
import ly.img.engine.DesignBlockType

// Add this composable to your NavHost
@Composable
fun SimpleCanvasMenuSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
        )

    // highlight-canvasMenuConfiguration
    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            canvasMenu = {
                CanvasMenu.remember(
                    // highlight-canvasMenuConfiguration-scope
                    // Implementation is too large, check the implementation of CanvasMenu.defaultScope
                    scope = CanvasMenu.defaultScope,
                    // highlight-canvasMenuConfiguration-scope
                    // highlight-canvasMenuConfiguration-visible
                    visible = {
                        val editorState by editorContext.state.collectAsState()
                        remember(this, editorState) {
                            editorState.isTouchActive.not() &&
                                editorState.activeSheet == null &&
                                editorContext.safeSelection != null &&
                                editorContext.isSelectionInGroup.not() &&
                                editorContext.selection.type != DesignBlockType.Page &&
                                editorContext.selection.type != DesignBlockType.Audio &&
                                editorContext.engine.editor.getEditMode() != "Text" &&
                                editorContext.isScenePlaying.not() &&
                                editorContext.selection.isVisibleAtCurrentPlaybackTime
                        }
                    },
                    // highlight-canvasMenuConfiguration-visible
                    // highlight-canvasMenuConfiguration-enterTransition
                    enterTransition = { EnterTransition.None },
                    // highlight-canvasMenuConfiguration-exitTransition
                    exitTransition = { ExitTransition.None },
                    // highlight-canvasMenuConfiguration-decoration
                    // Implementation is too large, check the implementation of CanvasMenu.DefaultDecoration
                    decoration = { DefaultDecoration { it() } },
                    // highlight-canvasMenuConfiguration-decoration
                    // highlight-canvasMenuConfiguration-listBuilder
                    listBuilder = CanvasMenu.ListBuilder.remember(),
                    // highlight-canvasMenuConfiguration-itemDecoration
                    // default value is { it() }
                    itemDecoration = {
                        Box(modifier = Modifier.padding(2.dp)) {
                            it()
                        }
                    },
                    // highlight-canvasMenuConfiguration-itemDecoration
                )
            },
        )
    // highlight-canvasMenuConfiguration
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
