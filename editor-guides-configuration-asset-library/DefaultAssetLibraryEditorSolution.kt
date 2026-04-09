import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.dropSection
import ly.img.editor.core.library.replaceSection
import ly.img.editor.smoketests.R
import ly.img.engine.DesignBlockType

// Add this composable to your NavHost
@Composable
fun DefaultAssetLibraryEditorSolution(navController: NavHostController) {
    // highlight-configuration-custom-asset-source
    val unsplashAssetSource = remember {
        UnsplashAssetSource(baseUrl = "<your Unsplash API host endpoint here>")
    }
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            // highlight-configuration-custom-asset-source
            EditorConfiguration.remember {
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(block = page, value = 1080F)
                    editorContext.engine.block.setHeight(block = page, value = 1080F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)

                    editorContext.engine.asset.addSource(unsplashAssetSource)
                }
                // highlight-configuration-default-asset-library
                assetLibrary = {
                    remember {
                        val unsplashSection = LibraryContent.Section(
                            titleRes = R.string.unsplash,
                            sourceTypes = listOf(AssetSourceType(sourceId = unsplashAssetSource.sourceId)),
                            assetType = AssetType.Image,
                        )
                        AssetLibrary.getDefault(
                            tabs = listOf(
                                AssetLibrary.Tab.IMAGES,
                                AssetLibrary.Tab.SHAPES,
                                AssetLibrary.Tab.STICKERS,
                                AssetLibrary.Tab.TEXT,
                            ),
                            images = LibraryCategory.Images
                                .replaceSection(index = 0) {
                                    // We replace the title: "Image Uploads" -> "Uploads"
                                    copy(titleRes = R.string.uploads)
                                }
                                .dropSection(index = 1)
                                .addSection(unsplashSection),
                        )
                    }
                }
                // highlight-configuration-default-asset-library
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
