import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import ly.img.editor.Editor
import ly.img.editor.core.R
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryCategory.Companion.sourceTypes
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.assetBaseUri
import ly.img.engine.populateAssetSource

@Composable
fun CustomAssetLibraryEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    val remoteImageAssetSource = remember {
        RemoteImageAssetSource(assetBaseUri = demoImageAssetBaseUri)
    }
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onLoaded = {
                    editorContext.engine.asset.addSource(remoteImageAssetSource)
                }

                assetLibrary = {
                    remember {
                        // highlight-android-custom-category
                        // This category appears as a tab in the asset library sheet.
                        val myAssetsCategory = LibraryCategory(
                            tabTitleRes = R.string.ly_img_editor_asset_library_title_elements,
                            tabSelectedIcon = IconPack.LibraryElements,
                            tabUnselectedIcon = IconPack.LibraryElements,
                            content = LibraryContent.Sections(
                                titleRes = R.string.ly_img_editor_asset_library_title_elements,
                                sections = listOf(
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_asset_library_title_stickers,
                                        sourceTypes = LibraryContent.Stickers.sourceTypes,
                                        assetType = AssetType.Sticker,
                                        expandContent = LibraryContent.Stickers,
                                    ),
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_asset_library_title_images,
                                        sourceTypes = listOf(AssetSourceType(sourceId = remoteImageAssetSource.sourceId)),
                                        assetType = AssetType.Image,
                                    ),
                                ),
                            ),
                        )
                        // highlight-android-custom-category

                        // highlight-android-custom-asset-library
                        AssetLibrary(
                            tabs = {
                                listOf(
                                    myAssetsCategory,
                                    LibraryCategory.Images,
                                )
                            },
                            images = {
                                val remoteImageSection = LibraryContent.Section(
                                    titleRes = R.string.ly_img_editor_asset_library_title_images,
                                    sourceTypes = listOf(AssetSourceType(sourceId = remoteImageAssetSource.sourceId)),
                                    assetType = AssetType.Image,
                                )
                                // Replacement sheets can use a different category than the Add tab.
                                LibraryCategory.Images.addSection(remoteImageSection)
                            },
                        )
                        // highlight-android-custom-asset-library
                    }
                }
            }
        },
        onClose = onClose,
    )
}

@Composable
fun CustomAssetLibraryPreviewSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    val remoteImageAssetSource = remember {
        RemoteImageAssetSource(assetBaseUri = demoImageAssetBaseUri)
    }
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(block = page, value = 1080F)
                    editorContext.engine.block.setHeight(block = page, value = 1080F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)
                }
                onLoaded = {
                    val defaultImageSourceId = "ly.img.image"
                    if (defaultImageSourceId !in editorContext.engine.asset.findAllSources()) {
                        @Suppress("DEPRECATION")
                        editorContext.engine.populateAssetSource(
                            id = defaultImageSourceId,
                            jsonUri = Uri.parse("$demoImageAssetBaseUri/$defaultImageSourceId/content.json"),
                            replaceBaseUri = Uri.parse(demoImageAssetBaseUri),
                        )
                    }
                    editorContext.engine.asset.addSource(remoteImageAssetSource)
                    // Let the editor apply the runtime asset-library configuration before opening the screenshot sheet.
                    delay(500)
                    val previewCategory = LibraryCategory(
                        tabTitleRes = R.string.ly_img_editor_asset_library_title_elements,
                        tabSelectedIcon = IconPack.LibraryElements,
                        tabUnselectedIcon = IconPack.LibraryElements,
                        content = LibraryContent.Sections(
                            titleRes = R.string.ly_img_editor_asset_library_title_elements,
                            sections = listOf(
                                LibraryContent.Section(
                                    titleRes = R.string.ly_img_editor_asset_library_title_images,
                                    sourceTypes = listOf(AssetSourceType(sourceId = remoteImageAssetSource.sourceId)),
                                    assetType = AssetType.Image,
                                ),
                            ),
                        ),
                    )
                    editorContext.eventHandler.send(
                        EditorEvent.Sheet.Open(SheetType.LibraryAdd(libraryCategory = previewCategory)),
                    )
                }
                assetLibrary = {
                    remember {
                        val previewCategory = LibraryCategory(
                            tabTitleRes = R.string.ly_img_editor_asset_library_title_elements,
                            tabSelectedIcon = IconPack.LibraryElements,
                            tabUnselectedIcon = IconPack.LibraryElements,
                            content = LibraryContent.Sections(
                                titleRes = R.string.ly_img_editor_asset_library_title_elements,
                                sections = listOf(
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_asset_library_title_images,
                                        sourceTypes = listOf(AssetSourceType(sourceId = remoteImageAssetSource.sourceId)),
                                        assetType = AssetType.Image,
                                    ),
                                ),
                            ),
                        )
                        AssetLibrary(
                            tabs = {
                                listOf(
                                    previewCategory,
                                    LibraryCategory.Images,
                                )
                            },
                        )
                    }
                }
            }
        },
        onClose = onClose,
    )
}

@Suppress("DEPRECATION")
private val demoImageAssetBaseUri: String
    get() = Engine.assetBaseUri.toString()
