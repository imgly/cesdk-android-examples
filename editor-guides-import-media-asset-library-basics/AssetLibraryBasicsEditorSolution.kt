import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.Editor
import ly.img.editor.core.R
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

// highlight-android-source-id
private const val BRAND_IMAGE_SOURCE_ID = "ly.img.asset.source.brand.images"
// highlight-android-source-id

@Composable
fun AssetLibraryBasicsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val engine = editorContext.engine
                    if (engine.scene.get() == null) {
                        val scene = engine.scene.create()
                        val page = engine.block.create(DesignBlockType.Page)
                        engine.block.setWidth(block = page, value = 1080F)
                        engine.block.setHeight(block = page, value = 1080F)
                        engine.block.appendChild(parent = scene, child = page)
                    }

                    // highlight-android-asset-source
                    val assetEngine = editorContext.engine
                    if (BRAND_IMAGE_SOURCE_ID !in assetEngine.asset.findAllSources()) {
                        assetEngine.asset.addLocalSource(
                            sourceId = BRAND_IMAGE_SOURCE_ID,
                            supportedMimeTypes = listOf(MimeType.JPEG.key),
                        )
                        assetEngine.asset.addAsset(
                            sourceId = BRAND_IMAGE_SOURCE_ID,
                            asset = AssetDefinition(
                                id = "brand-background",
                                label = mapOf("en" to "Brand Background"),
                                tags = mapOf("en" to listOf("brand", "background")),
                                groups = listOf("campaign"),
                                meta = mapOf(
                                    "uri" to "https://img.ly/static/ubq_samples/sample_4.jpg",
                                    "thumbUri" to "https://img.ly/static/ubq_samples/sample_4.jpg",
                                    "mimeType" to MimeType.JPEG.key,
                                    "kind" to "image",
                                    "blockType" to DesignBlockType.Graphic.key,
                                    "fillType" to FillType.Image.key,
                                    "shapeType" to ShapeType.Rect.key,
                                    "width" to "1080",
                                    "height" to "720",
                                ),
                            ),
                        )
                        assetEngine.asset.assetSourceContentsChanged(sourceId = BRAND_IMAGE_SOURCE_ID)
                    }
                    // highlight-android-asset-source
                }

                // highlight-android-library-category
                assetLibrary = {
                    remember {
                        val brandSourceType = AssetSourceType(sourceId = BRAND_IMAGE_SOURCE_ID)
                        val brandImagesSection = LibraryContent.Section(
                            titleRes = R.string.ly_img_editor_asset_library_section_images,
                            sourceTypes = listOf(brandSourceType),
                            assetType = AssetType.Image,
                            expandContent = LibraryContent.Grid(
                                titleRes = R.string.ly_img_editor_asset_library_section_images,
                                sourceType = brandSourceType,
                                assetType = AssetType.Image,
                            ),
                        )
                        val brandImagesCategory = LibraryCategory.Images.copy(
                            content = LibraryContent.Sections(
                                titleRes = R.string.ly_img_editor_asset_library_title_images,
                                sections = listOf(brandImagesSection),
                            ),
                        )
                        AssetLibrary.getDefault(
                            tabs = listOf(
                                AssetLibrary.Tab.IMAGES,
                                AssetLibrary.Tab.TEXT,
                                AssetLibrary.Tab.SHAPES,
                            ),
                            images = brandImagesCategory,
                        )
                    }
                }
                // highlight-android-library-category

                // highlight-android-dock-button
                dock = {
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { Dock.Button.rememberImagesLibrary() }
                            }
                        }
                    }
                }
                // highlight-android-dock-button
            }
        },
        onClose = onClose,
    )
}
