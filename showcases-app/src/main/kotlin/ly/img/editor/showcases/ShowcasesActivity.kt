package ly.img.editor.showcases

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import ly.img.camera.core.CameraResult
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.showcases.ui.screen.ApparelEditorScreen
import ly.img.editor.showcases.ui.screen.BackgroundRemovalScreen
import ly.img.editor.showcases.ui.screen.DesignEditorScreen
import ly.img.editor.showcases.ui.screen.EditCameraRecordingsScreen
import ly.img.editor.showcases.ui.screen.EditRecordedReactionScreen
import ly.img.editor.showcases.ui.screen.EditVideoFromUriScreen
import ly.img.editor.showcases.ui.screen.PhotoEditorScreen
import ly.img.editor.showcases.ui.screen.PostcardEditorScreen
import ly.img.editor.showcases.ui.screen.ShowcasesScreen
import ly.img.editor.showcases.ui.screen.TextToImageScreen
import ly.img.editor.showcases.ui.screen.VideoEditorScreen
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ShowcasesActivity : ComponentActivity() {
    private var navController: NavHostController? = null
    private var navControllerPopJob: Job? = null
    private var pendingDeeplinkIntent: Intent? = null
    private var touchEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            this.navController = navController
            LaunchedEffect(navController) {
                pendingDeeplinkIntent?.let {
                    pendingDeeplinkIntent = null
                    handleDeeplinkIntent(it)
                }
            }
            val viewModel = viewModel<ShowcasesViewModel>()
            val baseUri = AssetConfig.baseUri(this)
            EditorTheme {
                NavHost(navController = navController, startDestination = Screen.Catalog.routeScheme) {
                    composable(screen = Screen.Catalog) {
                        ShowcasesScreen(
                            viewModel = viewModel,
                            onResult = { key, value -> navController.currentBackStackEntry?.savedStateHandle?.set(key, value) },
                            navigateTo = { navController.navigate(it) },
                        )
                    }
                    composable(screen = Screen.ApparelUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "apparel")
                        ApparelEditorScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            sceneUri = sceneUri,
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.PostcardUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "postcard")
                        PostcardEditorScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            sceneUri = sceneUri,
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.DesignUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "design")
                        DesignEditorScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            sceneUri = sceneUri,
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.PhotoUi) {
                        PhotoEditorScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            imageUriAsString = it.arguments?.getString("image"),
                            sizeAsString = it.arguments?.getString("scene"),
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.VideoUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "video")
                        VideoEditorScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            sceneUri = sceneUri,
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.EditCameraRecordings) {
                        val arg = navController.getParcelable<CameraResult.Record>("recording")
                        val recording = remember { arg }
                        if (recording != null) {
                            EditCameraRecordingsScreen(
                                viewModel = viewModel,
                                baseUri = baseUri,
                                recording = recording,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(screen = Screen.EditRecordedReaction) {
                        val arg = navController.getParcelable<CameraResult.Reaction>("reaction")
                        val reaction = remember { arg }
                        if (reaction != null) {
                            EditRecordedReactionScreen(
                                viewModel = viewModel,
                                baseUri = baseUri,
                                reaction = reaction,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(screen = Screen.EditVideoFromUri) {
                        val arg = navController.getParcelable<Uri>("videoUri")
                        val videoUri = remember { arg }
                        if (videoUri != null) {
                            EditVideoFromUriScreen(
                                viewModel = viewModel,
                                baseUri = baseUri,
                                videoUri = videoUri,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(screen = Screen.TextToImage) {
                        val sceneUri = it.getSceneUri(defaultScene = "design")
                        TextToImageScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            sceneUri = sceneUri,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable(screen = Screen.BackgroundRemoval) {
                        BackgroundRemovalScreen(
                            viewModel = viewModel,
                            baseUri = baseUri,
                            imageUriAsString = it.arguments?.getString("image"),
                            sizeAsString = it.arguments?.getString("scene"),
                        ) { navController.popBackStack() }
                    }
                }
            }
        }
    }

    private fun NavBackStackEntry.getSceneUri(defaultScene: String): Uri {
        val arg = arguments?.getString("scene")?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX)
        return when {
            arg == null -> "file:///android_asset/scene/$defaultScene.scene".toUri()
            arg.startsWith("https") -> arg.toUri()
            arg.startsWith("content") -> arg.toUri()
            else -> {
                val scene = arg.takeIf {
                    this@ShowcasesActivity
                        .assets
                        .list("scene")
                        ?.contains("$arg.scene") == true
                } ?: defaultScene
                "file:///android_asset/scene/$scene.scene".toUri()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data == null) return
        // On Appetize, onNewIntent is fired very soon after onCreate returns. Because
        // setContent only schedules composition (it doesn't run synchronously), the
        // first composition hasn't yet assigned navController by the time this runs.
        // Queue the intent and replay it from a LaunchedEffect once navController is ready.
        if (navController == null) {
            pendingDeeplinkIntent = intent
            return
        }
        handleDeeplinkIntent(intent)
    }

    private fun handleDeeplinkIntent(intent: Intent) {
        touchEnabled = false
        navController?.popBackStack(route = Screen.Catalog.routeScheme, inclusive = false)
        // Pop backstack has animation, therefore we need to wait for it to finish before launching new screens
        navControllerPopJob?.cancel()
        navControllerPopJob = lifecycleScope.launch {
            navController
                ?.visibleEntries
                ?.takeWhile {
                    val found = it.firstOrNull()?.destination?.route == Screen.Catalog.routeScheme
                    if (found) {
                        try {
                            navController?.navigate(NavDeepLinkRequest(intent))
                        } catch (_: IllegalArgumentException) {
                            // Do nothing if we cannot handle this deeplink
                        }
                        touchEnabled = true
                    }
                    !found
                }?.collect()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (!touchEnabled) {
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}

fun NavGraphBuilder.composable(
    screen: Screen,
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(
        route = screen.routeScheme,
        deepLinks = listOf(navDeepLink { uriPattern = screen.deeplinkScheme }),
        arguments = screen.arguments,
        content = content,
    )
}

sealed class Screen(
    val routeScheme: String,
    val arguments: List<NamedNavArgument>,
) {
    val deeplinkScheme = "$BASE_URL/$routeScheme"

    fun getRoute(vararg args: Pair<String, Any?>): String = args.fold(routeScheme) { acc, arg ->
        val (key, value) = arg
        acc.replace(
            "{$key}",
            value
                .toString()
                .encodeBase64(withPrefix = BASE_64_URL_PREFIX),
        )
    }

    data object Catalog : Screen(
        routeScheme = "showcase",
        arguments = emptyList(),
    )

    data object ApparelUi : Screen(
        routeScheme = "apparel-ui?scene={scene}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    data object PostcardUi : Screen(
        routeScheme = "post-greeting-cards?scene={scene}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    data object DesignUi : Screen(
        routeScheme = "design-ui?scene={scene}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    data object PhotoUi : Screen(
        routeScheme = "photo-ui?scene={scene}&image={image}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
            navArgument("image") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    data object VideoUi : Screen(
        routeScheme = "video-ui?scene={scene}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    data object EditCameraRecordings : Screen(
        routeScheme = "edit-camera-recordings",
        arguments = listOf(),
    )

    data object EditRecordedReaction : Screen(
        routeScheme = "edit-recorded-reaction",
        arguments = listOf(),
    )

    data object EditVideoFromUri : Screen(
        routeScheme = "edit-video-from-uri",
        arguments = listOf(),
    )

    data object TextToImage : Screen(
        routeScheme = "text-to-image?scene={scene}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    data object BackgroundRemoval : Screen(
        routeScheme = "background-removal?scene={scene}&image={image}",
        arguments = listOf(
            navArgument("scene") {
                nullable = true
                defaultValue = null
            },
            navArgument("image") {
                nullable = true
                defaultValue = null
            },
        ),
    )

    companion object {
        private const val BASE_URL = "https://applink.img.ly"
        const val BASE_64_URL_PREFIX = "data:text/plain;base64,"
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun String.encodeBase64(withPrefix: String = ""): String = withPrefix + Base64.encode(this.toByteArray(Charset.forName("UTF-8")))

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeBase64(ifPrefixed: String = ""): String = if (ifPrefixed.isEmpty() || this.startsWith(ifPrefixed)) {
    Base64.decode(this.removePrefix(ifPrefixed)).decodeToString()
} else {
    this
}

@Composable
private fun <T> NavHostController.getParcelable(key: String): T? = previousBackStackEntry
    ?.savedStateHandle
    ?.remove(key)
