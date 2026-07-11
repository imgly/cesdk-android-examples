import android.net.Uri
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
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetLicense
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FetchAssetOptions
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

// highlight-android-source-types
private const val BRAND_IMAGE_SOURCE_ID = "ly.img.example.brand.images"
private const val BRAND_UPLOAD_SOURCE_ID = "ly.img.example.brand.uploads"
private const val CAMPAIGN_GROUP = "campaign"
private const val PRODUCT_GROUP = "product"

private val brandImageSourceType = AssetSourceType(sourceId = BRAND_IMAGE_SOURCE_ID)
private val brandUploadSourceType = UploadAssetSourceType(
    sourceId = BRAND_UPLOAD_SOURCE_ID,
    mimeTypeFilter = "image/*",
)
// highlight-android-source-types

@Composable
fun UserInterfaceAssetLibraryEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    if (editorContext.engine.scene.get() == null) {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(block = page, value = 1080F)
                        editorContext.engine.block.setHeight(block = page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)
                    }

                    // highlight-android-register-sources
                    val engine = editorContext.engine
                    if (BRAND_IMAGE_SOURCE_ID !in engine.asset.findAllSources()) {
                        engine.asset.addSource(
                            BrandImageAssetSource(
                                engine = engine,
                                assetBaseUri = editorContext.baseUri.buildUpon()
                                    .appendPath("ly.img.image")
                                    .build(),
                            ),
                        )
                    }
                    if (BRAND_UPLOAD_SOURCE_ID !in engine.asset.findAllSources()) {
                        engine.asset.addLocalSource(
                            sourceId = BRAND_UPLOAD_SOURCE_ID,
                            supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key),
                        )
                    }
                    // highlight-android-register-sources
                }

                // highlight-android-upload-callback
                onUpload = { assetDefinition, uploadSource ->
                    uploadTransientResource(
                        assetDefinition = assetDefinition,
                        uploadSource = uploadSource,
                    )
                }
                // highlight-android-upload-callback

                // highlight-android-library-category
                assetLibrary = {
                    remember {
                        val brandImages = LibraryCategory.Images.copy(
                            content = LibraryContent.Sections(
                                titleRes = R.string.ly_img_editor_asset_library_title_images,
                                sections = listOf(
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_asset_library_section_images,
                                        sourceTypes = listOf(brandImageSourceType),
                                        groups = listOf(CAMPAIGN_GROUP),
                                        assetType = AssetType.Image,
                                        expandContent = LibraryContent.Grid(
                                            titleRes = R.string.ly_img_editor_asset_library_section_images,
                                            sourceType = brandImageSourceType,
                                            groups = listOf(CAMPAIGN_GROUP),
                                            assetType = AssetType.Image,
                                        ),
                                    ),
                                    LibraryContent.Section(
                                        titleRes = R.string.ly_img_editor_asset_library_section_image_uploads,
                                        sourceTypes = listOf(brandUploadSourceType),
                                        assetType = AssetType.Image,
                                    ),
                                ),
                            ),
                        )

                        AssetLibrary.getDefault(
                            tabs = listOf(AssetLibrary.Tab.IMAGES),
                            images = brandImages,
                        )
                    }
                }
                // highlight-android-library-category
            }
        },
        onClose = onClose,
    )
}

// highlight-android-source-metadata
private class BrandImageAssetSource(
    private val engine: Engine,
    private val assetBaseUri: Uri,
) : AssetSource(sourceId = BRAND_IMAGE_SOURCE_ID) {
    override val credits = AssetCredits(
        name = "Brand Media Team",
        uri = Uri.parse("https://img.ly/"),
    )

    override val license = AssetLicense(
        name = "Internal use",
        uri = null,
    )

    override val supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key)
    // highlight-android-source-metadata

    override suspend fun getGroups(): List<String> = assets.flatMap { it.groups }.distinct()

    // highlight-android-find-assets
    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val filteredAssets = assets.filter { it.matches(query) }
        val page = query.page.coerceAtLeast(0)
        val perPage = query.perPage.coerceAtLeast(1)
        val start = page * perPage
        val pageAssets = filteredAssets
            .drop(start)
            .take(perPage)
            .map { it.toAsset(sourceId = sourceId, locale = query.locale ?: "en") }

        return FindAssetsResult(
            assets = pageAssets,
            currentPage = page,
            nextPage = if (start + perPage < filteredAssets.size) page + 1 else -1,
            total = filteredAssets.size,
        )
    }
    // highlight-android-find-assets

    override suspend fun fetchAsset(
        id: String,
        options: FetchAssetOptions,
    ): Asset? = assets
        .firstOrNull { it.id == id }
        ?.toAsset(sourceId = sourceId, locale = options.locale ?: "en")

    // highlight-android-apply-asset
    override suspend fun applyAsset(asset: Asset): DesignBlock? {
        val block = engine.asset.defaultApplyAsset(asset) ?: return null
        engine.block.setMetadata(block = block, key = "asset/source", value = sourceId)
        engine.block.setMetadata(block = block, key = "asset/externalId", value = asset.id)
        return block
    }
    // highlight-android-apply-asset

    private fun BrandImage.matches(query: FindAssetsQuery): Boolean {
        val groupMatches = query.groups?.all(groups::contains) ?: true
        val excludedGroupMatches = query.excludeGroups?.none(groups::contains) ?: true
        val tagMatches = query.tags?.all { requestedTag ->
            tags.any { it.equals(requestedTag, ignoreCase = true) }
        } ?: true
        val searchTerms = query.query
            ?.trim()
            ?.lowercase()
            ?.split(Regex("\\s+"))
            ?.filter { it.isNotEmpty() }
            .orEmpty()
        val queryMatches = searchTerms.all { term ->
            label.contains(term, ignoreCase = true) ||
                tags.any { it.contains(term, ignoreCase = true) }
        }

        return groupMatches && excludedGroupMatches && tagMatches && queryMatches
    }

    // highlight-android-asset-metadata
    private fun BrandImage.toAsset(
        sourceId: String,
        locale: String,
    ) = Asset(
        id = id,
        context = AssetContext(sourceId = sourceId),
        label = label,
        locale = locale,
        tags = tags,
        groups = groups,
        meta = mapOf(
            "uri" to assetUri("images", fileName),
            "thumbUri" to assetUri("thumbnails", fileName),
            "mimeType" to MimeType.JPEG.key,
            "kind" to "image",
            "blockType" to DesignBlockType.Graphic.key,
            "fillType" to FillType.Image.key,
            "shapeType" to ShapeType.Rect.key,
            "width" to width.toString(),
            "height" to height.toString(),
        ),
        credits = credits,
        license = license,
    )
    // highlight-android-asset-metadata

    private data class BrandImage(
        val id: String,
        val label: String,
        val tags: List<String>,
        val groups: List<String>,
        val fileName: String,
        val width: Int,
        val height: Int,
    )

    private fun assetUri(vararg pathSegments: String): String {
        val builder = assetBaseUri.buildUpon()
        pathSegments.forEach { builder.appendPath(it) }
        return builder.build().toString()
    }

    private companion object {
        private val assets = listOf(
            BrandImage(
                id = "brand-campaign-hero",
                label = "Campaign Hero",
                tags = listOf("campaign", "beach", "blue"),
                groups = listOf(CAMPAIGN_GROUP),
                fileName = "sample_1.jpg",
                width = 2500,
                height = 1667,
            ),
            BrandImage(
                id = "brand-product-backdrop",
                label = "Product Backdrop",
                tags = listOf("product", "mountain", "green"),
                groups = listOf(PRODUCT_GROUP),
                fileName = "sample_10.jpg",
                width = 2500,
                height = 1667,
            ),
            BrandImage(
                id = "brand-campaign-texture",
                label = "Campaign Texture",
                tags = listOf("campaign", "nature", "warm"),
                groups = listOf(CAMPAIGN_GROUP),
                fileName = "sample_12.jpg",
                width = 2500,
                height = 1667,
            ),
        )
    }
}

// highlight-android-upload-helper
private suspend fun uploadTransientResource(
    assetDefinition: AssetDefinition,
    uploadSource: UploadAssetSourceType,
): AssetDefinition {
    val meta = assetDefinition.meta ?: return assetDefinition
    val localUri = meta["uri"] ?: return assetDefinition
    val permanentUri = uploadToPermanentStorage(
        uri = localUri,
        sourceId = uploadSource.sourceId,
    )
    val permanentMeta = meta.toMutableMap()
    permanentMeta["uri"] = permanentUri
    meta["thumbUri"]?.let { thumbnailUri ->
        permanentMeta["thumbUri"] = if (thumbnailUri == localUri) {
            permanentUri
        } else {
            uploadToPermanentStorage(
                uri = thumbnailUri,
                sourceId = uploadSource.sourceId,
            )
        }
    }
    return assetDefinition.copy(meta = permanentMeta)
}

private suspend fun uploadToPermanentStorage(
    uri: String,
    sourceId: String,
): String {
    check(sourceId.isNotBlank()) { "Upload source id is required." }
    // Replace this with your app's storage client and return its permanent URI.
    return Uri.parse("https://assets.example.com")
        .buildUpon()
        .appendPath(sourceId)
        .appendQueryParameter("sourceUri", uri)
        .build()
        .toString()
}
// highlight-android-upload-helper
