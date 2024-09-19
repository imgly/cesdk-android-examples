import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.R
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Libraryelements
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryCategory.Companion.sourceTypes
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.showcase.Secrets
import ly.img.editor.showcase.R as ShowcaseR

// Add this composable to your NavHost
@Composable
fun CustomAssetLibraryEditorSolution(navController: NavHostController) {
    val unsplashAssetSource = UnsplashAssetSource(Secrets.unsplashHost)
    val engineConfiguration =
        remember {
            EngineConfiguration(
                license = "<your license here>",
                onCreate = { engine, eventHandler ->
                    EditorDefaults.onCreate(
                        engine = engine,
                        sceneUri = EngineConfiguration.defaultDesignSceneUri,
                        eventHandler = eventHandler,
                    ) { _, _ ->
                        engine.asset.addSource(unsplashAssetSource)
                    }
                },
            )
        }
    // highlight-configuration-custom-asset-library
    // We create a custom tab with title "My Assets" that contains 2 sections:
    // 1. Stickers - expanding it opens the default stickers content
    // 2. Text - expanding it opens the default text content. Note that the title is skipped.
    val myAssetsCategory =
        LibraryCategory(
            tabTitleRes = ShowcaseR.string.my_assets,
            tabSelectedIcon = IconPack.Libraryelements,
            tabUnselectedIcon = IconPack.Libraryelements,
            content =
                LibraryContent.Sections(
                    titleRes = ShowcaseR.string.my_assets,
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
    val assetLibrary =
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
                        titleRes = ShowcaseR.string.unsplash,
                        sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
                        assetType = AssetType.Image,
                    )
                // See how the images is different in tabs and here
                LibraryCategory.Images.addSection(unsplashSection)
            },
        )
    // highlight-configuration-custom-asset-library
    val editorConfiguration =
        EditorConfiguration.getDefault(
            assetLibrary = assetLibrary,
        )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
