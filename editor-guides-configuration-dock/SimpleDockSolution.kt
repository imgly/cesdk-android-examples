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

@Composable
fun SimpleDockSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            // highlight-android-dock-configuration
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        scope = {
                            remember(this) { Dock.Scope(parentScope = this) }
                        }
                        modifier = { Modifier }
                        visible = { true }
                        enterTransition = { EnterTransition.None }
                        exitTransition = { ExitTransition.None }
                        decoration = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface1.copy(alpha = 0.95f))
                                    .padding(vertical = 10.dp),
                            ) {
                                it()
                            }
                        }
                        listBuilder = { Dock.ListBuilder.remember { /* Add items */ } }
                        horizontalArrangement = { Arrangement.SpaceEvenly }
                        itemDecoration = {
                            Box(modifier = Modifier.padding(2.dp)) {
                                it()
                            }
                        }
                    }
                }
            }
            // highlight-android-dock-configuration
        },
        onClose = onClose,
    )
}
