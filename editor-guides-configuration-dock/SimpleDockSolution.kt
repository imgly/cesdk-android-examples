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
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.theme.surface1

// Add this composable to your NavHost
@Composable
fun SimpleDockSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // evaluation mode with watermark
        configuration = {
            // highlight-dockConfiguration
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        // highlight-dockConfiguration-scope
                        scope = {
                            remember(this) { Dock.Scope(parentScope = this) }
                        }
                        // highlight-dockConfiguration-scope
                        // highlight-dockConfiguration-modifier
                        modifier = { Modifier }
                        // highlight-dockConfiguration-modifier
                        // highlight-dockConfiguration-visible
                        visible = { true }
                        // highlight-dockConfiguration-enterTransition
                        enterTransition = { EnterTransition.None }
                        // highlight-dockConfiguration-exitTransition
                        exitTransition = { ExitTransition.None }
                        // highlight-dockConfiguration-decoration
                        decoration = {
                            // Also available via Dock.DefaultDecoration
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f))
                                    .padding(vertical = 10.dp),
                            ) {
                                it()
                            }
                        }
                        // highlight-dockConfiguration-decoration
                        // highlight-dockConfiguration-listBuilder
                        listBuilder = { Dock.ListBuilder.remember { /* Add items */ } }
                        // highlight-dockConfiguration-horizontalArrangement
                        horizontalArrangement = { Arrangement.SpaceEvenly }
                        // highlight-dockConfiguration-itemDecoration
                        // Default value is { it() }
                        itemDecoration = {
                            Box(modifier = Modifier.padding(2.dp)) {
                                it()
                            }
                        }
                        // highlight-dockConfiguration-itemDecoration
                    }
                }
            }
            // highlight-dockConfiguration
        },
        onClose = onClose,
    )
}
