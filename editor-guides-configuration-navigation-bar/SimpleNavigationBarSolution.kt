import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.Editor
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun SimpleNavigationBarSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            // highlight-navigationBarConfiguration
            EditorConfiguration.remember {
                navigationBar = {
                    NavigationBar.remember {
                        scope = {
                            remember(this) { NavigationBar.Scope(parentScope = this) }
                        }
                        visible = { true }
                        modifier = { Modifier }
                        enterTransition = { EnterTransition.None }
                        exitTransition = { ExitTransition.None }
                        decoration = {
                            // Also available via NavigationBar.DefaultDecoration
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 64.dp)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(PaddingValues(horizontal = 4.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                it()
                            }
                        }
                        listBuilder = { NavigationBar.ListBuilder.remember { /* Add items */ } }
                        horizontalArrangement = { Arrangement.SpaceEvenly }
                        // Default value is { it() }
                        itemDecoration = {
                            Box(modifier = Modifier.padding(2.dp)) {
                                it()
                            }
                        }
                    }
                }
            }
            // highlight-navigationBarConfiguration
        },
        onClose = onClose,
    )
}
