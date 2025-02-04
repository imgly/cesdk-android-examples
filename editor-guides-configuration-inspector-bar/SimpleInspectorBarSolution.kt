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
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.InspectorBar.Companion.DefaultDecoration
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun SimpleInspectorBarSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
        )

    // highlight-inspectorBarConfiguration
    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            inspectorBar = {
                InspectorBar.remember(
                    // highlight-inspectorBarConfiguration-scope
                    // Implementation is too large, check the implementation of InspectorBar.defaultScope
                    scope = InspectorBar.defaultScope,
                    // highlight-inspectorBarConfiguration-scope
                    // highlight-inspectorBarConfiguration-visible
                    visible = { editorContext.safeSelection != null },
                    // highlight-inspectorBarConfiguration-enterTransition
                    // Also available via InspectorBar.defaultEnterTransition
                    enterTransition = {
                        remember {
                            slideInVertically(
                                animationSpec =
                                    tween(
                                        durationMillis = 400,
                                        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f),
                                    ),
                                initialOffsetY = { it },
                            )
                        }
                    },
                    // highlight-inspectorBarConfiguration-enterTransition
                    // highlight-inspectorBarConfiguration-exitTransition
                    // Also available via InspectorBar.defaultExitTransition
                    exitTransition = {
                        remember {
                            slideOutVertically(
                                animationSpec =
                                    tween(
                                        durationMillis = 150,
                                        easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f),
                                    ),
                                targetOffsetY = { it },
                            )
                        }
                    },
                    // highlight-inspectorBarConfiguration-exitTransition
                    // highlight-inspectorBarConfiguration-decoration
                    // Implementation is too large, check the implementation of InspectorBar.DefaultDecoration
                    decoration = { DefaultDecoration { it() } },
                    // highlight-inspectorBarConfiguration-decoration
                    // highlight-inspectorBarConfiguration-listBuilder
                    listBuilder = InspectorBar.ListBuilder.remember(),
                    // highlight-inspectorBarConfiguration-horizontalArrangement
                    horizontalArrangement = { Arrangement.Start },
                    // highlight-inspectorBarConfiguration-itemsRowEnterTransition
                    // Also available via InspectorBar.defaultItemsRowEnterTransition
                    itemsRowEnterTransition = {
                        remember {
                            slideInHorizontally(
                                animationSpec = tween(400, easing = CubicBezierEasing(0.05F, 0.7F, 0.1F, 1F)),
                                initialOffsetX = { it / 3 },
                            )
                        }
                    },
                    // highlight-inspectorBarConfiguration-itemsRowEnterTransition
                    // highlight-inspectorBarConfiguration-itemsRowExitTransition
                    // Also available via InspectorBar.defaultItemsRowExitTransition
                    itemsRowExitTransition = { ExitTransition.None },
                    // highlight-inspectorBarConfiguration-itemsRowExitTransition
                    // highlight-inspectorBarConfiguration-itemDecoration
                    // default value is { it() }
                    itemDecoration = {
                        Box(modifier = Modifier.padding(2.dp)) {
                            it()
                        }
                    },
                    // highlight-inspectorBarConfiguration-itemDecoration
                )
            },
        )
    // highlight-inspectorBarConfiguration
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
