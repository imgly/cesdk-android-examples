package ly.img.editor.showcase

import UnsplashAssetSource
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ly.img.editor.ApparelEditor
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.PostcardEditor
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.theme.EditorTheme

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val unsplashAssetSource = UnsplashAssetSource(Secrets.unsplashHost)
        val unsplashSection =
            LibraryContent.Section(
                titleRes = R.string.unsplash,
                sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
                assetType = AssetType.Image,
            )
        val assetLibrary =
            AssetLibrary.getDefault(
                images = LibraryCategory.Images.addSection(unsplashSection),
            )
        val editorConfiguration =
            EditorConfiguration.getDefault(
                assetLibrary = assetLibrary,
                colorPalette =
                    listOf(
                        Color(0xFF000000),
                        Color(0xFFFFFFFF),
                        Color(0xFF4932D1),
                        Color(0xFFFE6755),
                        Color(0xFF606060),
                        Color(0xFF696969),
                        Color(0xFF999999),
                    ),
            )

        fun getEngineConfiguration(sceneUri: Uri) =
            EngineConfiguration(
                license = Secrets.license,
                onCreate = { engine, eventHandler ->
                    EditorDefaults.onCreate(engine = engine, sceneUri = sceneUri, eventHandler = eventHandler)
                    engine.asset.addSource(unsplashAssetSource)
                },
            )

        setContent {
            EditorTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Showcases.routeScheme) {
                    composable(route = Screen.Showcases.routeScheme) {
                        Showcases(navigateTo = {
                            navController.navigate(it)
                        })
                    }
                    composable(
                        route = Screen.ApparelUi.routeScheme,
                    ) {
                        val scene = requireNotNull(it.arguments?.getString("0"))
                        if (scene == "default") {
                            ApparelEditor(
                                engineConfiguration = EngineConfiguration.getForApparel(license = Secrets.license),
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val sceneUri = Uri.parse("file:///android_asset/scenes/$scene.scene")
                            ApparelEditor(
                                engineConfiguration = getEngineConfiguration(sceneUri),
                                editorConfiguration = editorConfiguration,
                            ) {
                                navController.popBackStack()
                            }
                        }
                    }
                    composable(
                        route = Screen.PostcardUi.routeScheme,
                    ) {
                        val scene = requireNotNull(it.arguments?.getString("0"))
                        if (scene == "default") {
                            PostcardEditor(
                                engineConfiguration = EngineConfiguration.getForPostcard(license = Secrets.license),
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val sceneUri = Uri.parse("file:///android_asset/scenes/$scene.scene")
                            PostcardEditor(
                                engineConfiguration = getEngineConfiguration(sceneUri),
                                editorConfiguration = editorConfiguration,
                            ) { navController.popBackStack() }
                        }
                    }
                    composable(
                        route = Screen.DesignUi.routeScheme,
                    ) {
                        val scene = requireNotNull(it.arguments?.getString("0"))
                        if (scene == "default") {
                            DesignEditor(
                                engineConfiguration = EngineConfiguration.getForDesign(license = Secrets.license),
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val sceneUri = Uri.parse("file:///android_asset/scenes/$scene.scene")
                            DesignEditor(
                                engineConfiguration = getEngineConfiguration(sceneUri),
                                editorConfiguration = editorConfiguration,
                            ) { navController.popBackStack() }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val routeScheme: String) {
    fun getRoute(vararg args: Any): String {
        return args.foldIndexed(routeScheme) { index, acc, arg ->
            acc.replace("{$index}", arg.toString())
        }
    }

    data object Showcases : Screen("showcases")

    data object DesignUi : Screen("design-ui?scene={0}")

    data object ApparelUi : Screen("apparel-ui?scene={0}")

    data object PostcardUi : Screen("post-greeting-cards?scene={0}")
}
