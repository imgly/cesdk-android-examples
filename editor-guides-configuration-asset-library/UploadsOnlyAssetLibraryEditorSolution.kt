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
import ly.img.editor.core.library.data.AssetSourceType

@Composable
fun UploadsOnlyAssetLibraryEditorSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-uploads-only-asset-library
                assetLibrary = {
                    remember {
                        val uploadsOnlyImages = LibraryCategory.Images.copy(
                            content = LibraryContent.Sections(
                                titleRes = ly.img.editor.core.R.string.ly_img_editor_asset_library_title_images,
                                sections = listOf(
                                    LibraryContent.Section(
                                        titleRes = ly.img.editor.core.R.string.ly_img_editor_asset_library_section_image_uploads,
                                        sourceTypes = listOf(AssetSourceType.ImageUploads),
                                        assetType = AssetType.Image,
                                        expandContent = LibraryContent.Grid(
                                            titleRes = ly.img.editor.core.R.string.ly_img_editor_asset_library_section_image_uploads,
                                            sourceType = AssetSourceType.ImageUploads,
                                            assetType = AssetType.Image,
                                        ),
                                    ),
                                ),
                            ),
                        )
                        AssetLibrary.getDefault(images = uploadsOnlyImages)
                    }
                }
                // highlight-uploads-only-asset-library
            }
        },
    ) {
        navController.popBackStack()
    }
}
