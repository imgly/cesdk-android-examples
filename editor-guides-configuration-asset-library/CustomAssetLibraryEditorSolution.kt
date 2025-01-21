import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.R
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryCategory.Companion.sourceTypes
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.rememberForDesign
import ly.img.editor.smoketests.Secrets
import ly.img.editor.smoketests.R as SmokeTestR

// Add this composable to your NavHost
@Composable
fun CustomAssetLibraryEditorSolution(navController: NavHostController) {
    val unsplashAssetSource =
        remember {
            UnsplashAssetSource(Secrets.unsplashHost)
        }
    val engineConfiguration =
        EngineConfiguration.remember(
            license = "<your license here>",
            onCreate = {
                EditorDefaults.onCreate(
                    engine = editorContext.engine,
                    sceneUri = EngineConfiguration.defaultDesignSceneUri,
                    eventHandler = editorContext.eventHandler,
                ) { _, _ ->
                    editorContext.engine.asset.addSource(unsplashAssetSource)
                }
            },
        )
    // highlight-configuration-custom-asset-library
    val assetLibrary =
        remember {
            // We create a custom tab with title "My Assets" that contains 2 sections:
            // 1. Stickers - expanding it opens the default stickers content
            // 2. Text - expanding it opens the default text content. Note that the title is skipped.
            val myAssetsCategory =
                LibraryCategory(
                    tabTitleRes = SmokeTestR.string.my_assets,
                    tabSelectedIcon = IconPack.LibraryElements,
                    tabUnselectedIcon = IconPack.LibraryElements,
                    content =
                        LibraryContent.Sections(
                            titleRes = SmokeTestR.string.my_assets,
                            sections =
                                listOf(
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_stickers,
                                        sourceTypes = LibraryContent.Stickers.sourceTypes,
                                        assetType = AssetType.Sticker,
                                        expandContent = LibraryContent.Stickers,
                                    ),
                                    LibraryContent.Section(
                                        sourceTypes = LibraryContent.Text.sourceTypes,
                                        assetType = AssetType.Text,
                                        expandContent = LibraryContent.Text,
                                    ),
                                ),
                        ),
                )
            AssetLibrary(
                tabs = {
                    listOf(
                        myAssetsCategory,
                        LibraryCategory.Images,
                    )
                },
                images = {
                    val unsplashSection =
                        LibraryContent.Section(
                            titleRes = SmokeTestR.string.unsplash,
                            sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
                            assetType = AssetType.Image,
                        )
                    // See how the images is different in tabs and here
                    LibraryCategory.Images.addSection(unsplashSection)
                },
            )
        }
    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            assetLibrary = assetLibrary,
        )
    // highlight-configuration-custom-asset-library
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
