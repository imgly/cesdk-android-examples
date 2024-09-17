package ly.img.editor.showcase

import RemoteAssetSource
import UnsplashAssetSource
import addRemoteAssetSources
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ly.img.editor.ApparelEditor
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EditorUiState
import ly.img.editor.EngineConfiguration
import ly.img.editor.PhotoEditor
import ly.img.editor.PostcardEditor
import ly.img.editor.VideoEditor
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryCategory.Companion.sourceTypes
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.replaceSection
import ly.img.editor.core.theme.EditorTheme
import ly.img.engine.SceneMode
import ly.img.engine.populateAssetSource

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stickerMiscId = "ly.img.sticker.misc"
        val unsplashAssetSource = UnsplashAssetSource(Secrets.unsplashHost)

        fun getEditorConfiguration(screen: Screen): EditorConfiguration<EditorUiState> {
            val unsplashSection =
                LibraryContent.Section(
                    titleRes = R.string.unsplash,
                    sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
                    assetType = AssetType.Image,
                )
            val pexelsImagesSection =
                LibraryContent.Section(
                    titleRes = R.string.pexels,
                    sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.image.pexels")),
                    assetType = AssetType.Image,
                )
            val pexelsVideoSection =
                LibraryContent.Section(
                    titleRes = R.string.pexels,
                    sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.pexels")),
                    assetType = AssetType.Video,
                )
            val giphyVideoSection =
                LibraryContent.Section(
                    titleRes = R.string.giphy,
                    sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.video.giphy")),
                    assetType = AssetType.Video,
                )

            val defaultImagesContent = LibraryCategory.Images.content as LibraryContent.Sections
            val defaultVideoContent = LibraryCategory.Video.content as LibraryContent.Sections

            val stickersMiscAssetSourceType = AssetSourceType(stickerMiscId)

            val videos =
                LibraryCategory.Video.copy(
                    content =
                        defaultVideoContent.copy(
                            sections = listOf(pexelsVideoSection, giphyVideoSection) + defaultVideoContent.sections,
                        ),
                )

            val images =
                LibraryCategory.Images.copy(
                    content =
                        defaultImagesContent.copy(
                            sections = listOf(pexelsImagesSection, unsplashSection) + defaultImagesContent.sections,
                        ),
                )

            return EditorConfiguration.getDefault(
                assetLibrary =
                    AssetLibrary.getDefault(
                        images = images,
                        videos = videos,
                        overlays =
                            LibraryCategory.Overlays
                                .replaceSection(0) {
                                    copy(
                                        sourceTypes = videos.content.sourceTypes,
                                        expandContent = videos.content,
                                    )
                                }.replaceSection(1) {
                                    copy(
                                        sourceTypes = images.content.sourceTypes,
                                        expandContent = images.content,
                                    )
                                },
                        clips =
                            LibraryCategory.Clips
                                .replaceSection(0) {
                                    copy(
                                        sourceTypes = videos.content.sourceTypes,
                                        expandContent = videos.content,
                                    )
                                }.replaceSection(1) {
                                    copy(
                                        sourceTypes = images.content.sourceTypes,
                                        expandContent = images.content,
                                    )
                                },
                        stickers =
                            if (screen != Screen.VideoUi) {
                                LibraryCategory.Stickers
                            } else {
                                LibraryCategory.Stickers.copy(
                                    content =
                                        LibraryContent.Stickers.copy(
                                            sections =
                                                listOf(
                                                    LibraryContent.Section(
                                                        titleRes = ly.img.editor.core.R.string.ly_img_editor_stickers_hands,
                                                        sourceTypes = listOf(AssetSourceType.Stickers),
                                                        groups = listOf("//ly.img.cesdk.stickers.hand/category/hand"),
                                                        assetType = AssetType.Sticker,
                                                    ),
                                                    LibraryContent.Section(
                                                        titleRes = R.string.ly_img_sticker_misc_sketches,
                                                        sourceTypes = listOf(stickersMiscAssetSourceType),
                                                        groups = listOf("sketches"),
                                                        assetType = AssetType.Sticker,
                                                    ),
                                                    LibraryContent.Section(
                                                        titleRes = R.string.ly_img_sticker_misc_tape,
                                                        sourceTypes = listOf(stickersMiscAssetSourceType),
                                                        groups = listOf("tape"),
                                                        assetType = AssetType.Sticker,
                                                    ),
                                                    LibraryContent.Section(
                                                        titleRes = R.string.ly_img_sticker_misc_marker,
                                                        sourceTypes = listOf(stickersMiscAssetSourceType),
                                                        groups = listOf("marker"),
                                                        assetType = AssetType.Sticker,
                                                    ),
                                                    LibraryContent.Section(
                                                        titleRes = R.string.ly_img_sticker_misc_3dstickers,
                                                        sourceTypes = listOf(stickersMiscAssetSourceType),
                                                        groups = listOf("3dstickers"),
                                                        assetType = AssetType.Sticker,
                                                    ),
                                                    LibraryContent.Section(
                                                        titleRes = ly.img.editor.core.R.string.ly_img_editor_shapes_basic,
                                                        sourceTypes = listOf(AssetSourceType.Shapes),
                                                        groups = listOf("//ly.img.cesdk.vectorpaths/category/vectorpaths"),
                                                        assetType = AssetType.Shape,
                                                    ),
                                                    LibraryContent.Section(
                                                        titleRes = ly.img.editor.core.R.string.ly_img_editor_shapes_abstract,
                                                        sourceTypes = listOf(AssetSourceType.Shapes),
                                                        groups = listOf("//ly.img.cesdk.vectorpaths.abstract/category/abstract"),
                                                        assetType = AssetType.Shape,
                                                    ),
                                                ),
                                        ),
                                )
                            },
                    ),
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
        }

        fun getEngineConfiguration(sceneUri: Uri) =
            EngineConfiguration(
                license = Secrets.license,
                onCreate = { engine, eventHandler ->
                    EditorDefaults.onCreate(engine, sceneUri, eventHandler) { scene, scope ->
                        engine.asset.addSource(unsplashAssetSource)
                        val isVideoScene = engine.scene.getMode() == SceneMode.VIDEO
                        val remoteAssetSourcesSet =
                            buildSet {
                                add(RemoteAssetSource.Path.ImagePexels)
                                if (isVideoScene) {
                                    add(RemoteAssetSource.Path.VideoPexels)
                                    add(RemoteAssetSource.Path.VideoGiphy)
                                }
                            }
                        scope.launch {
                            engine.addRemoteAssetSources(
                                host = Secrets.remoteAssetSourceHost,
                                paths = remoteAssetSourcesSet,
                            )
                        }
                        if (isVideoScene) {
                            scope.launch {
                                engine.populateAssetSource(
                                    id = stickerMiscId,
                                    jsonUri = Uri.parse("https://cdn.img.ly/assets/demo/v2/ly.img.sticker.misc/content.json"),
                                    replaceBaseUri = Uri.parse("https://cdn.img.ly/assets/demo/v2/ly.img.sticker.misc"),
                                )
                            }
                        }
                    }
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
                                editorConfiguration = getEditorConfiguration(Screen.ApparelUi),
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
                                editorConfiguration = getEditorConfiguration(Screen.PostcardUi),
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
                                editorConfiguration = getEditorConfiguration(Screen.DesignUi),
                            ) { navController.popBackStack() }
                        }
                    }
                    composable(
                        route = Screen.PhotoUi.routeScheme,
                    ) {
                        val image = requireNotNull(it.arguments?.getString("0"))
                        PhotoEditor(
                            engineConfiguration =
                                EngineConfiguration.getForPhoto(
                                    license = Secrets.license,
                                    imageUri = Uri.parse(image),
                                ),
                        ) {
                            navController.popBackStack()
                        }
                    }
                    composable(
                        route = Screen.VideoUi.routeScheme,
                    ) {
                        val scene = requireNotNull(it.arguments?.getString("0"))
                        if (scene == "default") {
                            VideoEditor(
                                engineConfiguration = EngineConfiguration.getForVideo(license = Secrets.license),
                            ) {
                                navController.popBackStack()
                            }
                        } else {
                            val sceneUri = Uri.parse("file:///android_asset/scenes/$scene.scene")
                            VideoEditor(
                                engineConfiguration = getEngineConfiguration(sceneUri),
                                editorConfiguration = getEditorConfiguration(Screen.VideoUi),
                            ) {
                                navController.popBackStack()
                            }
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

    data object PhotoUi : Screen("photo-ui?image={0}")

    data object DesignUi : Screen("design-ui?scene={0}")

    data object ApparelUi : Screen("apparel-ui?scene={0}")

    data object PostcardUi : Screen("post-greeting-cards?scene={0}")

    data object VideoUi : Screen("video-ui?scene={0}")
}
