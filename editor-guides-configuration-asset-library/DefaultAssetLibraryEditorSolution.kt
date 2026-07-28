import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.Editor
import ly.img.editor.core.R
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

@Composable
fun DefaultAssetLibraryEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-custom-asset-source
    val remoteImageAssetSource = remember {
        RemoteImageAssetSource(assetBaseUri = "<your image asset base URI>")
    }
    // highlight-android-custom-asset-source

    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-register-asset-source
                onLoaded = {
                    editorContext.engine.asset.addSource(remoteImageAssetSource)
                }
                // highlight-android-register-asset-source

                // highlight-android-default-asset-library
                assetLibrary = {
                    remember {
                        val remoteImageSection = LibraryContent.Section(
                            titleRes = R.string.ly_img_editor_asset_library_title_images,
                            sourceTypes = listOf(AssetSourceType(sourceId = remoteImageAssetSource.sourceId)),
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
                                    copy(count = 6)
                                }
                                .dropSection(index = 1)
                                .addSection(remoteImageSection),
                        )
                    }
                }
                // highlight-android-default-asset-library
            }
        },
        onClose = onClose,
    )
}
