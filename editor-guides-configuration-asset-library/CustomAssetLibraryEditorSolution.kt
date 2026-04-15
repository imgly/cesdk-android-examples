import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.R
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryCategory.Companion.sourceTypes
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.DesignBlockType
import ly.img.editor.smoketests.R as SmokeTestR

// Add this composable to your NavHost
@Composable
fun CustomAssetLibraryEditorSolution(navController: NavHostController) {
    val unsplashAssetSource = remember {
        UnsplashAssetSource(baseUrl = "<your Unsplash API host endpoint here>")
    }
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(block = page, value = 1080F)
                    editorContext.engine.block.setHeight(block = page, value = 1080F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)

                    editorContext.engine.asset.addSource(unsplashAssetSource)
                }
                // highlight-configuration-custom-asset-library
                assetLibrary = {
                    remember {
                        // We create a custom tab with title "My Assets" that contains 2 sections:
                        // 1. Stickers - expanding it opens the default stickers content
                        // 2. Text - expanding it opens the default text content. Note that the title is skipped.
                        val myAssetsCategory = LibraryCategory(
                            tabTitleRes = SmokeTestR.string.my_assets,
                            tabSelectedIcon = IconPack.LibraryElements,
                            tabUnselectedIcon = IconPack.LibraryElements,
                            content = LibraryContent.Sections(
                                titleRes = SmokeTestR.string.my_assets,
                                sections = listOf(
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_asset_library_title_stickers,
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
                                val unsplashSection = LibraryContent.Section(
                                    titleRes = SmokeTestR.string.unsplash,
                                    sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
                                    assetType = AssetType.Image,
                                )
                                // See how the images is different in tabs and here
                                LibraryCategory.Images.addSection(unsplashSection)
                            },
                        )
                    }
                }
                // highlight-configuration-custom-asset-library
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
