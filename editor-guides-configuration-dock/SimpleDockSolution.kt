import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.theme.surface1
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun SimpleDockSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
        )

    // highlight-dockConfiguration
    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            dock = {
                Dock.remember(
                    // highlight-dockConfiguration-scope
                    scope =
                        LocalEditorScope.current.run {
                            remember(this) { Dock.Scope(parentScope = this) }
                        },
                    // highlight-dockConfiguration-scope
                    // highlight-dockConfiguration-visible
                    visible = { true },
                    // highlight-dockConfiguration-enterTransition
                    enterTransition = { EnterTransition.None },
                    // highlight-dockConfiguration-exitTransition
                    exitTransition = { ExitTransition.None },
                    // highlight-dockConfiguration-decoration
                    decoration = {
                        // Also available via Dock.DefaultDecoration
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f))
                                    .padding(vertical = 10.dp),
                        ) {
                            it()
                        }
                    },
                    // highlight-dockConfiguration-decoration
                    // highlight-dockConfiguration-listBuilder
                    listBuilder = Dock.ListBuilder.remember { },
                    // highlight-dockConfiguration-horizontalArrangement
                    horizontalArrangement = { Arrangement.SpaceEvenly },
                    // highlight-dockConfiguration-itemDecoration
                    // default value is { it() }
                    itemDecoration = {
                        Box(modifier = Modifier.padding(2.dp)) {
                            it()
                        }
                    },
                    // highlight-dockConfiguration-itemDecoration
                )
            },
        )
    // highlight-dockConfiguration
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
