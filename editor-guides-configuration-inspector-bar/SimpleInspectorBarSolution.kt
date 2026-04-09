import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.DefaultDecoration
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberDefaultScope
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun SimpleInspectorBarSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            // highlight-inspectorBarConfiguration
            EditorConfiguration.remember {
                inspectorBar = {
                    InspectorBar.remember {
                        // Implementation is too large, check the implementation of InspectorBar.rememberDefaultScope
                        scope = {
                            InspectorBar.rememberDefaultScope(parentScope = this)
                        }
                        modifier = { Modifier }
                        visible = { editorContext.safeSelection != null }
                        // Also available via InspectorBar.defaultEnterTransition
                        enterTransition = {
                            remember {
                                slideInVertically(
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f),
                                    ),
                                    initialOffsetY = { it },
                                )
                            }
                        }
                        // Also available via InspectorBar.defaultExitTransition
                        exitTransition = {
                            remember {
                                slideOutVertically(
                                    animationSpec = tween(
                                        durationMillis = 150,
                                        easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f),
                                    ),
                                    targetOffsetY = { it },
                                )
                            }
                        }
                        // Implementation is too large, check the implementation of InspectorBar.DefaultDecoration
                        decoration = { InspectorBar.DefaultDecoration(scope = this) { it() } }
                        listBuilder = { InspectorBar.ListBuilder.remember { /* Add items */ } }
                        horizontalArrangement = { Arrangement.Start }
                        // Also available via InspectorBar.defaultItemsRowEnterTransition
                        itemsRowEnterTransition = {
                            remember {
                                slideInHorizontally(
                                    animationSpec = tween(400, easing = CubicBezierEasing(0.05F, 0.7F, 0.1F, 1F)),
                                    initialOffsetX = { it / 3 },
                                )
                            }
                        }
                        // Also available via InspectorBar.defaultItemsRowExitTransition
                        itemsRowExitTransition = { ExitTransition.None }
                        // Default value is { it() }
                        itemDecoration = {
                            Box(modifier = Modifier.padding(2.dp)) {
                                it()
                            }
                        }
                    }
                }
            }
            // highlight-inspectorBarConfiguration
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
