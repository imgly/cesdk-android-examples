import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.rememberForDesign
import ly.img.editor.smoketests.Secrets

@Composable
fun UploadsOnlyAssetLibraryEditorSolution(navController: NavHostController) {
    // highlight-uploads-only-asset-library
    val uploadsOnlyImages = remember {
        LibraryCategory.Images.copy(
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
    }
    val assetLibrary = remember {
        AssetLibrary.getDefault(images = uploadsOnlyImages)
    }
    // highlight-uploads-only-asset-library

    val engineConfiguration = EngineConfiguration.remember(
        license = Secrets.license,
        onCreate = {},
    )
    val editorConfiguration = EditorConfiguration.rememberForDesign(
        assetLibrary = assetLibrary,
    )

    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        navController.popBackStack()
    }
}
