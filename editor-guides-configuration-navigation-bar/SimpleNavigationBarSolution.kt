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
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun SimpleNavigationBarSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )
    // highlight-navigationBarConfiguration
    val editorConfiguration = EditorConfiguration.rememberForDesign(
        navigationBar = {
            NavigationBar.remember(
                // highlight-navigationBarConfiguration-scope
                scope = LocalEditorScope.current.run {
                    remember(this) { NavigationBar.Scope(parentScope = this) }
                },
                // highlight-navigationBarConfiguration-scope
                // highlight-navigationBarConfiguration-visible
                visible = { true },
                // highlight-navigationBarConfiguration-enterTransition
                enterTransition = { EnterTransition.None },
                // highlight-navigationBarConfiguration-exitTransition
                exitTransition = { ExitTransition.None },
                // highlight-navigationBarConfiguration-decoration
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
                },
                // highlight-navigationBarConfiguration-decoration
                // highlight-navigationBarConfiguration-listBuilder
                listBuilder = NavigationBar.ListBuilder.remember { },
                // highlight-navigationBarConfiguration-horizontalArrangement
                horizontalArrangement = { Arrangement.SpaceEvenly },
                // highlight-navigationBarConfiguration-itemDecoration
                // default value is { it() }
                itemDecoration = {
                    Box(modifier = Modifier.padding(2.dp)) {
                        it()
                    }
                },
                // highlight-navigationBarConfiguration-itemDecoration
            )
        },
    )
    // highlight-navigationBarConfiguration
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
