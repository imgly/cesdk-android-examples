package ly.img.editor.showcases

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
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
import ly.img.editor.ApparelEditor
import ly.img.editor.DesignEditor
import ly.img.editor.PhotoEditor
import ly.img.editor.PostcardEditor
import ly.img.editor.VideoEditor
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.showcases.ui.screens.EditCameraRecordings
import ly.img.editor.showcases.ui.screens.EditRecordedReaction
import ly.img.editor.showcases.ui.screens.EditVideoFromUri
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ShowcasesActivity : ComponentActivity() {
    private var navController: NavHostController? = null
    private var navControllerPopJob: Job? = null
    private var touchEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            this.navController = navController
            EditorTheme {
                val showcasesViewModel = viewModel<ShowcasesViewModel>()
                NavHost(navController = navController, startDestination = Screen.Catalog.routeScheme) {
                    composable(screen = Screen.Catalog) {
                        Showcases(
                            navController = navController,
                            navigateTo = { navController.navigate(it) },
                        )
                    }
                    composable(screen = Screen.ApparelUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "apparel")
                        ApparelEditor(
                            engineConfiguration = showcasesViewModel.rememberEngineConfigurationForScene(sceneUri),
                            editorConfiguration = showcasesViewModel.rememberEditorConfiguration(sceneUri, Screen.ApparelUi),
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.PostcardUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "postcard")
                        PostcardEditor(
                            engineConfiguration = showcasesViewModel.rememberEngineConfigurationForScene(sceneUri),
                            editorConfiguration = showcasesViewModel.rememberEditorConfiguration(sceneUri, Screen.PostcardUi),
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.DesignUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "generic")
                        DesignEditor(
                            engineConfiguration = showcasesViewModel.rememberEngineConfigurationForScene(sceneUri),
                            editorConfiguration = showcasesViewModel.rememberEditorConfiguration(sceneUri, Screen.DesignUi),
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.PhotoUi) {
                        val dimensionsAsString = it.arguments
                            ?.getString("scene")
                            ?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX)
                        val image = it.arguments
                            ?.getString("image")
                            ?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX)
                        PhotoEditor(
                            engineConfiguration = showcasesViewModel.rememberEngineConfigurationForImage(
                                imageUri = image?.let(Uri::parse),
                                dimensionsAsString = dimensionsAsString,
                            ),
                            editorConfiguration = showcasesViewModel.rememberEditorConfiguration(sceneUri = null, Screen.PhotoUi),
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.VideoUi) {
                        val sceneUri = it.getSceneUri(defaultScene = "video")
                        VideoEditor(
                            engineConfiguration = showcasesViewModel.rememberEngineConfigurationForScene(sceneUri),
                            editorConfiguration = showcasesViewModel.rememberEditorConfiguration(sceneUri, Screen.VideoUi),
                        ) { navController.popBackStack() }
                    }
                    composable(screen = Screen.EditCameraRecordings) {
                        navController.getParcelable<CameraResult.Record>("recording")?.let { recording ->
                            EditCameraRecordings(
                                recording = recording,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(screen = Screen.EditRecordedReaction) {
                        navController.getParcelable<CameraResult.Reaction>("reaction")?.let { reaction ->
                            EditRecordedReaction(
                                reaction = reaction,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                    composable(screen = Screen.EditVideoFromUri) {
                        navController.getParcelable<Uri>("videoUri")?.let { uri ->
                            EditVideoFromUri(
                                uri = uri,
                                onBack = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun NavBackStackEntry.getSceneUri(defaultScene: String): Uri {
        val arg = arguments?.getString("scene")?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX)
        return when {
            arg == null -> Uri.parse("file:///android_asset/scenes/$defaultScene.scene")
            arg.startsWith("https") -> Uri.parse(arg)
            arg.startsWith("content") -> Uri.parse(arg)
            else -> {
                val scene = arg.takeIf {
                    this@ShowcasesActivity
                        .assets
                        .list("scenes")
                        ?.contains("$arg.scene") == true
                } ?: defaultScene
                Uri.parse("file:///android_asset/scenes/$scene.scene")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data == null) return
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
                        } catch (ex: IllegalArgumentException) {
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

    companion object {
        private const val BASE_URL = "https://ubq.page.link"
        const val BASE_64_URL_PREFIX = "data:text/plain;base64,"
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun String.encodeBase64(withPrefix: String = ""): String = withPrefix + Base64.encode(this.toByteArray(Charset.forName("UTF-8")))

@OptIn(ExperimentalEncodingApi::class)
private fun String.decodeBase64(ifPrefixed: String = ""): String = if (ifPrefixed.isEmpty() || this.startsWith(ifPrefixed)) {
    Base64.decode(this.removePrefix(ifPrefixed)).decodeToString()
} else {
    this
}

@Composable
private fun <T> NavHostController.getParcelable(key: String): T? = previousBackStackEntry
    ?.savedStateHandle
    ?.get<T>(key)
