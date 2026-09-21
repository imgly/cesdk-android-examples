import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.core.R
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.addSection
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.Asset
import ly.img.engine.AssetContext
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

// highlight-android-backend-setup
data class ServerSourceIds(
    val images: String = "my-server-images",
    val videos: String = "my-server-videos",
    val audio: String = "my-server-audio",
    val stickers: String = "my-server-stickers",
    val templates: String = "my-server-templates",
    val brandStickers: String = "my-server-brand-stickers",
) {
    val all: List<String>
        get() = listOf(images, videos, audio, stickers, templates, brandStickers)
}
// highlight-android-backend-setup

data class YourServerResult(
    val sourceIds: ServerSourceIds,
    val imageResults: FindAssetsResult,
    val secondImagePage: FindAssetsResult,
    val taggedImageResults: FindAssetsResult,
    val videoResults: FindAssetsResult,
    val audioResults: FindAssetsResult,
    val stickerResults: FindAssetsResult,
    val templateResults: FindAssetsResult,
    val secondTemplatePage: FindAssetsResult,
    val fetchedTemplateAsset: Asset,
    val appliedTemplateBlock: DesignBlock?,
    val staticJsonSourceId: String,
    val staticJsonResults: FindAssetsResult,
    val assetLibraryTabs: Int,
)

// highlight-android-backend-contract
data class ServerAssetPage(
    val assets: List<ServerAsset>,
    val currentPage: Int,
    val nextPage: Int,
    val total: Int,
)

interface ServerAssetBackend {
    suspend fun searchAssets(
        sourceId: String,
        query: FindAssetsQuery,
    ): ServerAssetPage

    suspend fun getAsset(
        sourceId: String,
        assetId: String,
    ): ServerAsset?

    suspend fun getGroups(sourceId: String): List<String>?
}
// highlight-android-backend-contract

suspend fun yourServer(
    engine: Engine,
    assetBaseUri: Uri,
): YourServerResult {
    // highlight-android-create-backend
    val sourceIds = ServerSourceIds()
    // Replace this factory with your app's API client.
    val backend: ServerAssetBackend = createAppServerBackend(sourceIds, assetBaseUri)
    // highlight-android-create-backend

    registerServerAssetSources(
        engine = engine,
        sourceIds = sourceIds,
        backend = backend,
    )
    val staticJsonSourceId = loadStaticJsonAssetSource(engine)
    val imageResults = engine.asset.findAssets(
        sourceId = sourceIds.images,
        query = FindAssetsQuery(perPage = 1, page = 0),
    )
    val secondImagePage = engine.asset.findAssets(
        sourceId = sourceIds.images,
        query = FindAssetsQuery(perPage = 1, page = 1),
    )
    val taggedImageResults = engine.asset.findAssets(
        sourceId = sourceIds.images,
        query = FindAssetsQuery(perPage = 10, page = 0, tags = listOf("sunset")),
    )
    val videoResults = engine.asset.findAssets(
        sourceId = sourceIds.videos,
        query = FindAssetsQuery(perPage = 10, page = 0, query = "surf"),
    )
    val audioResults = engine.asset.findAssets(
        sourceId = sourceIds.audio,
        query = FindAssetsQuery(perPage = 10, page = 0),
    )
    val stickerResults = engine.asset.findAssets(
        sourceId = sourceIds.stickers,
        query = FindAssetsQuery(perPage = 10, page = 0),
    )
    val templateResults = engine.asset.findAssets(
        sourceId = sourceIds.templates,
        query = FindAssetsQuery(perPage = 1, page = 0),
    )
    val secondTemplatePage = engine.asset.findAssets(
        sourceId = sourceIds.templates,
        query = FindAssetsQuery(perPage = 1, page = 1),
    )
    val fetchedTemplateAsset = engine.asset.fetchAsset(
        sourceId = sourceIds.templates,
        assetId = "template-001",
    ) ?: error("Template asset not found.")
    val appliedTemplateBlock = engine.asset.applyAssetSourceAsset(
        sourceId = sourceIds.templates,
        asset = fetchedTemplateAsset,
    )
    val staticJsonResults = engine.asset.findAssets(
        sourceId = staticJsonSourceId,
        query = FindAssetsQuery(perPage = 10, page = 0),
    )
    val assetLibrary = createServerAssetLibrary(sourceIds)

    return YourServerResult(
        sourceIds = sourceIds,
        imageResults = imageResults,
        secondImagePage = secondImagePage,
        taggedImageResults = taggedImageResults,
        videoResults = videoResults,
        audioResults = audioResults,
        stickerResults = stickerResults,
        templateResults = templateResults,
        secondTemplatePage = secondTemplatePage,
        fetchedTemplateAsset = fetchedTemplateAsset,
        appliedTemplateBlock = appliedTemplateBlock,
        staticJsonSourceId = staticJsonSourceId,
        staticJsonResults = staticJsonResults,
        assetLibraryTabs = assetLibrary.tabs().size,
    )
}

// highlight-android-backend-client
private fun createAppServerBackend(
    sourceIds: ServerSourceIds,
    assetBaseUri: Uri,
): ServerAssetBackend {
    // Replace this demo backend with your app's authenticated API client.
    return DemoServerBackend(sourceIds, assetBaseUri)
}
// highlight-android-backend-client

// highlight-android-register-sources
fun registerServerAssetSources(
    engine: Engine,
    sourceIds: ServerSourceIds,
    backend: ServerAssetBackend,
) {
    sourceIds.all
        .filter(engine.asset.findAllSources()::contains)
        .forEach(engine.asset::removeSource)

    val mediaSources = listOf(
        ServerMediaAssetSource(
            sourceId = sourceIds.images,
            supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key),
            backend = backend,
        ),
        ServerMediaAssetSource(
            sourceId = sourceIds.videos,
            supportedMimeTypes = listOf(MimeType.MP4.key),
            backend = backend,
        ),
        ServerMediaAssetSource(
            sourceId = sourceIds.audio,
            supportedMimeTypes = listOf("audio/mpeg", "audio/x-m4a"),
            backend = backend,
        ),
        ServerMediaAssetSource(
            sourceId = sourceIds.stickers,
            supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key, MimeType.SVG.key),
            backend = backend,
        ),
    )

    mediaSources.forEach(engine.asset::addSource)
    engine.asset.addSource(
        ServerTemplateAssetSource(
            engine = engine,
            sourceId = sourceIds.templates,
            backend = backend,
        ),
    )
}
// highlight-android-register-sources

// highlight-android-editor-configuration
@Composable
fun rememberYourServerEditorConfiguration(assetBaseUri: Uri): EditorConfiguration {
    val sourceIds = remember { ServerSourceIds() }
    val backend = remember(sourceIds, assetBaseUri) { createAppServerBackend(sourceIds, assetBaseUri) }

    return EditorConfiguration.remember {
        onLoaded = {
            registerServerAssetSources(
                engine = editorContext.engine,
                sourceIds = sourceIds,
                backend = backend,
            )
            loadStaticJsonAssetSource(editorContext.engine)
        }
        assetLibrary = {
            createServerAssetLibrary(sourceIds)
        }
    }
}
// highlight-android-editor-configuration

// highlight-android-json-source
suspend fun loadStaticJsonAssetSource(engine: Engine): String = engine.asset.addLocalSourceFromJSON(
    contentJSON = STATIC_BRAND_STICKERS_JSON,
    basePath = "https://img.ly/static/ubq_samples",
)
// highlight-android-json-source

// highlight-android-dynamic-source
private class ServerMediaAssetSource(
    sourceId: String,
    override val supportedMimeTypes: List<String>,
    private val backend: ServerAssetBackend,
) : AssetSource(sourceId = sourceId) {
    override suspend fun getGroups(): List<String>? = withContext(Dispatchers.IO) {
        backend.getGroups(sourceId)
    }

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
        val page = backend.searchAssets(sourceId = sourceId, query = query)
        FindAssetsResult(
            assets = page.assets.map { it.toAsset(sourceId = sourceId) },
            currentPage = page.currentPage,
            nextPage = page.nextPage,
            total = page.total,
        )
    }

    override suspend fun fetchAsset(
        id: String,
        options: FetchAssetOptions,
    ): Asset? = withContext(Dispatchers.IO) {
        backend.getAsset(sourceId = sourceId, assetId = id)?.toAsset(sourceId = sourceId)
    }
}
// highlight-android-dynamic-source

// highlight-android-template-source
private class ServerTemplateAssetSource(
    private val engine: Engine,
    sourceId: String,
    private val backend: ServerAssetBackend,
) : AssetSource(sourceId = sourceId) {
    override val supportedMimeTypes: List<String> = listOf("application/ubq-template-string")

    override suspend fun getGroups(): List<String>? = withContext(Dispatchers.IO) {
        backend.getGroups(sourceId)
    }

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
        val page = backend.searchAssets(sourceId = sourceId, query = query)
        FindAssetsResult(
            assets = page.assets.map { it.toAsset(sourceId = sourceId) },
            currentPage = page.currentPage,
            nextPage = page.nextPage,
            total = page.total,
        )
    }

    override suspend fun fetchAsset(
        id: String,
        options: FetchAssetOptions,
    ): Asset? = withContext(Dispatchers.IO) {
        backend.getAsset(sourceId = sourceId, assetId = id)?.toAsset(sourceId = sourceId)
    }

    override suspend fun applyAsset(asset: Asset): DesignBlock? {
        val templateUri = asset.meta?.get("uri")?.let(Uri::parse) ?: return null
        engine.scene.load(sceneUri = templateUri)
        return null
    }
}
// highlight-android-template-source

// highlight-android-editor-library
fun createServerAssetLibrary(sourceIds: ServerSourceIds): AssetLibrary {
    val serverImageSection = LibraryContent.Section(
        titleRes = R.string.ly_img_editor_asset_library_section_images,
        sourceTypes = listOf(AssetSourceType(sourceId = sourceIds.images)),
        assetType = AssetType.Image,
    )
    val serverVideoSection = LibraryContent.Section(
        titleRes = R.string.ly_img_editor_asset_library_section_videos,
        sourceTypes = listOf(AssetSourceType(sourceId = sourceIds.videos)),
        assetType = AssetType.Video,
    )
    val serverAudioSection = LibraryContent.Section(
        titleRes = R.string.ly_img_editor_asset_library_section_audio,
        sourceTypes = listOf(AssetSourceType(sourceId = sourceIds.audio)),
        assetType = AssetType.Audio,
    )
    val serverStickerSection = LibraryContent.Section(
        titleRes = R.string.ly_img_editor_asset_library_title_stickers,
        sourceTypes = listOf(
            AssetSourceType(sourceId = sourceIds.stickers),
            AssetSourceType(sourceId = sourceIds.brandStickers),
        ),
        assetType = AssetType.Sticker,
    )

    return AssetLibrary.getDefault(
        includeAVResources = true,
        images = LibraryCategory.Images.addSection(serverImageSection),
        videos = LibraryCategory.Video.addSection(serverVideoSection),
        audios = LibraryCategory.Audio.addSection(serverAudioSection),
        stickers = LibraryCategory.Stickers.addSection(serverStickerSection),
    )
}
// highlight-android-editor-library

// highlight-android-media-metadata
data class ServerAsset(
    val id: String,
    val label: String,
    val uri: String,
    val thumbUri: String,
    val mimeType: String,
    val blockType: DesignBlockType,
    val fillType: FillType? = null,
    val shapeType: ShapeType? = null,
    val kind: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Double? = null,
    val tags: List<String> = emptyList(),
    val groups: List<String> = emptyList(),
) {
    fun toAsset(sourceId: String) = Asset(
        id = id,
        context = AssetContext(sourceId = sourceId),
        label = label,
        locale = "en",
        tags = tags,
        groups = groups,
        meta = buildMap {
            put("uri", uri)
            put("thumbUri", thumbUri)
            put("mimeType", mimeType)
            put("blockType", blockType.key)
            fillType?.let { put("fillType", it.key) }
            shapeType?.let { put("shapeType", it.key) }
            kind?.let { put("kind", it) }
            width?.let { put("width", it.toString()) }
            height?.let { put("height", it.toString()) }
            duration?.let { put("duration", it.toString()) }
        },
    )
}
// highlight-android-media-metadata

private fun createServerAssets(
    sourceIds: ServerSourceIds,
    assetBaseUri: Uri,
): Map<String, List<ServerAsset>> = mapOf(
    sourceIds.images to imageServerAssets(),
    sourceIds.videos to videoServerAssets(),
    sourceIds.audio to audioServerAssets(assetBaseUri),
    sourceIds.stickers to stickerServerAssets(),
    sourceIds.templates to templateServerAssets(assetBaseUri),
)

private fun imageServerAssets(): List<ServerAsset> = listOf(
    // highlight-android-image-metadata
    ServerAsset(
        id = "img-001",
        label = "Mountain Landscape",
        uri = "https://img.ly/static/ubq_samples/sample_1.jpg",
        thumbUri = "https://img.ly/static/ubq_samples/sample_1.jpg",
        mimeType = MimeType.JPEG.key,
        blockType = DesignBlockType.Graphic,
        fillType = FillType.Image,
        shapeType = ShapeType.Rect,
        kind = "image",
        width = 1920,
        height = 1280,
        tags = listOf("mountain", "landscape"),
        groups = listOf("photos"),
    ),
    // highlight-android-image-metadata
    ServerAsset(
        id = "img-002",
        label = "Ocean Sunset",
        uri = "https://img.ly/static/ubq_samples/sample_2.jpg",
        thumbUri = "https://img.ly/static/ubq_samples/sample_2.jpg",
        mimeType = MimeType.JPEG.key,
        blockType = DesignBlockType.Graphic,
        fillType = FillType.Image,
        shapeType = ShapeType.Rect,
        kind = "image",
        width = 1920,
        height = 1280,
        tags = listOf("ocean", "sunset"),
        groups = listOf("photos"),
    ),
)

private fun videoServerAssets(): List<ServerAsset> = listOf(
    // highlight-android-video-metadata
    ServerAsset(
        id = "vid-001",
        label = "Surf Promo",
        uri = "https://img.ly/static/ubq_video_samples/bbb.mp4",
        thumbUri = "https://img.ly/static/ubq_samples/sample_1.jpg",
        mimeType = MimeType.MP4.key,
        blockType = DesignBlockType.Graphic,
        fillType = FillType.Video,
        shapeType = ShapeType.Rect,
        kind = "video",
        width = 640,
        height = 360,
        duration = 634.694,
        tags = listOf("surf", "promo"),
        groups = listOf("videos"),
    ),
    // highlight-android-video-metadata
)

private fun audioServerAssets(assetBaseUri: Uri): List<ServerAsset> = listOf(
    // highlight-android-audio-metadata
    ServerAsset(
        id = "audio-001",
        label = "Background Track",
        uri = assetBaseUri
            .buildUpon()
            .appendPath("ly.img.audio")
            .appendPath("audios")
            .appendPath("far_from_home.m4a")
            .build()
            .toString(),
        thumbUri = "https://img.ly/static/ubq_samples/sample_2.jpg",
        mimeType = "audio/x-m4a",
        blockType = DesignBlockType.Audio,
        duration = 98.716,
        tags = listOf("music", "background"),
        groups = listOf("audio"),
    ),
    // highlight-android-audio-metadata
)

private fun stickerServerAssets(): List<ServerAsset> = listOf(
    // highlight-android-sticker-metadata
    ServerAsset(
        id = "sticker-001",
        label = "Camera Badge",
        uri = "https://img.ly/static/ubq_samples/imgly_logo.jpg",
        thumbUri = "https://img.ly/static/ubq_samples/imgly_logo.jpg",
        mimeType = MimeType.JPEG.key,
        blockType = DesignBlockType.Graphic,
        fillType = FillType.Image,
        shapeType = ShapeType.Rect,
        kind = "sticker",
        width = 1980,
        height = 720,
        tags = listOf("camera", "badge"),
        groups = listOf("stickers"),
    ),
    // highlight-android-sticker-metadata
)

private fun templateServerAssets(assetBaseUri: Uri): List<ServerAsset> = listOf(
    // highlight-android-template-metadata
    ServerAsset(
        id = "template-001",
        label = "Launch Story",
        uri = assetBaseUri
            .buildUpon()
            .appendPath("ly.img.templates")
            .appendPath("templates")
            .appendPath("cesdk_postcard_1.scene")
            .build()
            .toString(),
        thumbUri = assetBaseUri
            .buildUpon()
            .appendPath("ly.img.templates")
            .appendPath("thumbnails")
            .appendPath("cesdk_postcard_1.jpg")
            .build()
            .toString(),
        mimeType = "application/ubq-template-string",
        blockType = DesignBlockType.Scene,
        kind = "scene",
        tags = listOf("launch", "story"),
        groups = listOf("templates"),
    ),
    // highlight-android-template-metadata
    ServerAsset(
        id = "template-002",
        label = "Product Promo",
        uri = assetBaseUri
            .buildUpon()
            .appendPath("ly.img.templates")
            .appendPath("templates")
            .appendPath("cesdk_postcard_2.scene")
            .build()
            .toString(),
        thumbUri = assetBaseUri
            .buildUpon()
            .appendPath("ly.img.templates")
            .appendPath("thumbnails")
            .appendPath("cesdk_postcard_2.jpg")
            .build()
            .toString(),
        mimeType = "application/ubq-template-string",
        blockType = DesignBlockType.Scene,
        kind = "scene",
        tags = listOf("product", "promo"),
        groups = listOf("templates"),
    ),
)

private class DemoServerBackend(
    sourceIds: ServerSourceIds,
    assetBaseUri: Uri,
) : ServerAssetBackend {
    private val assetsBySource = createServerAssets(sourceIds, assetBaseUri)

    override suspend fun searchAssets(
        sourceId: String,
        query: FindAssetsQuery,
    ): ServerAssetPage {
        val searchWords = query.query
            ?.split(" ")
            ?.map(String::trim)
            ?.filter(String::isNotEmpty)
            .orEmpty()
        val requestedTags = query.tags.orEmpty()
        val requestedGroups = query.groups.orEmpty()

        val matchingAssets = assetsBySource.getValue(sourceId)
            .filter { asset ->
                val matchesQuery =
                    searchWords.isEmpty() ||
                        searchWords.all { word ->
                            asset.label.contains(word, ignoreCase = true) ||
                                asset.tags.any { it.contains(word, ignoreCase = true) }
                        }
                val matchesGroups =
                    requestedGroups.isEmpty() ||
                        asset.groups.any(requestedGroups::contains)
                val matchesTags =
                    requestedTags.isEmpty() ||
                        requestedTags.all(asset.tags::contains)

                matchesQuery && matchesGroups && matchesTags
            }
        val startIndex = query.page * query.perPage
        val pageAssets = matchingAssets.drop(startIndex).take(query.perPage)
        val nextPage = if (startIndex + pageAssets.size < matchingAssets.size) query.page + 1 else -1

        return ServerAssetPage(
            assets = pageAssets,
            currentPage = query.page,
            nextPage = nextPage,
            total = matchingAssets.size,
        )
    }

    override suspend fun getAsset(
        sourceId: String,
        assetId: String,
    ): ServerAsset? = assetsBySource[sourceId]?.firstOrNull { it.id == assetId }

    override suspend fun getGroups(sourceId: String): List<String>? = assetsBySource[sourceId]
        ?.flatMap(ServerAsset::groups)
        ?.distinct()
}

private val STATIC_BRAND_STICKERS_JSON = """
{
  "version": "1.0.0",
  "id": "my-server-brand-stickers",
  "assets": [
    {
      "id": "brand-badge",
      "label": { "en": "Brand Badge" },
      "tags": { "en": ["brand", "badge"] },
      "groups": ["stickers"],
      "meta": {
        "uri": "{{base_url}}/imgly_logo.jpg",
        "thumbUri": "{{base_url}}/imgly_logo.jpg",
        "mimeType": "${MimeType.JPEG.key}",
        "blockType": "//ly.img.ubq/graphic",
        "fillType": "//ly.img.ubq/fill/image",
        "shapeType": "//ly.img.ubq/shape/rect",
        "kind": "sticker",
        "width": 1980,
        "height": 720
      }
    }
  ]
}
"""
