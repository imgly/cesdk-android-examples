import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
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
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

// highlight-android-source-id
private const val EXTERNAL_IMAGE_SOURCE_ID = "my-external-images"
// highlight-android-source-id

// highlight-android-editor-configuration
@Composable
fun RefreshAssetsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    val externalSource = remember { createExternalImageAssetSource() }

    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onLoaded = {
                    val engine = editorContext.engine
                    if (externalSource.sourceId !in engine.asset.findAllSources()) {
                        engine.asset.addSource(externalSource)
                    }
                }

                assetLibrary = {
                    remember {
                        val externalSourceType = AssetSourceType(sourceId = EXTERNAL_IMAGE_SOURCE_ID)
                        val externalImagesSection = LibraryContent.Section(
                            sourceTypes = listOf(externalSourceType),
                            assetType = AssetType.Image,
                            expandContent = LibraryContent.Grid(
                                titleRes = R.string.ly_img_editor_asset_library_section_images,
                                sourceType = externalSourceType,
                                assetType = AssetType.Image,
                                title = "External images",
                            ),
                        )
                        AssetLibrary.getDefault(
                            images = LibraryCategory.Images.addSection(externalImagesSection),
                        )
                    }
                }
            }
        },
        onClose = onClose,
    )
}
// highlight-android-editor-configuration

suspend fun refreshAssets(
    engine: Engine,
    awaitEngineEvents: suspend () -> Unit = {},
): RefreshAssetsResult = coroutineScope {
    // highlight-android-create-source
    val externalSource = createExternalImageAssetSource()
    // highlight-android-create-source

    if (externalSource.sourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = externalSource.sourceId)
    }

    try {
        // highlight-android-register-source
        engine.asset.addSource(externalSource)
        // highlight-android-register-source
        // The smoke test pumps the offscreen engine so native asset events are delivered deterministically.
        awaitEngineEvents()

        val initialAssets = engine.asset.findAssets(
            sourceId = externalSource.sourceId,
            query = FindAssetsQuery(page = 0, perPage = 20),
        )
        check(initialAssets.total == 2)

        // highlight-android-observe-refresh
        val sourceUpdates = engine.asset.onAssetSourceUpdated()
        // highlight-android-observe-refresh
        val refreshEvents = async(start = CoroutineStart.UNDISPATCHED) {
            sourceUpdates
                .take(1)
                .toList()
        }
        // Give callbackFlow a turn to register the native subscription before the first smoke-test refresh.
        yield()

        // highlight-android-refresh-upload
        externalSource.assets = externalSource.assets + listOf(
            externalImageAsset(
                id = "winter-lookbook",
                label = "Winter Lookbook",
                tags = listOf("lookbook", "winter"),
                uri = "https://img.ly/static/ubq_samples/sample_3.jpg",
            ),
        )
        engine.asset.assetSourceContentsChanged(sourceId = externalSource.sourceId)
        // highlight-android-refresh-upload
        // Pump the offscreen smoke-test engine after each refresh notification.
        awaitEngineEvents()

        val afterUploadAssets = engine.asset.findAssets(
            sourceId = externalSource.sourceId,
            query = FindAssetsQuery(page = 0, perPage = 20),
        )

        // highlight-android-refresh-modification
        externalSource.assets = externalSource.assets.map { asset ->
            if (asset.id == "spring-poster") {
                asset.copy(label = "Spring Launch Poster")
            } else {
                asset
            }
        }
        engine.asset.assetSourceContentsChanged(sourceId = externalSource.sourceId)
        // highlight-android-refresh-modification
        // Pump the offscreen smoke-test engine after each refresh notification.
        awaitEngineEvents()

        val renamedAssetLabel = engine.asset.findAssets(
            sourceId = externalSource.sourceId,
            query = FindAssetsQuery(page = 0, perPage = 20),
        ).assets.firstOrNull { asset -> asset.id == "spring-poster" }?.label

        // highlight-android-refresh-deletion
        externalSource.assets = externalSource.assets.filterNot { asset ->
            asset.id == "winter-lookbook"
        }
        engine.asset.assetSourceContentsChanged(sourceId = externalSource.sourceId)
        // highlight-android-refresh-deletion
        // Pump the offscreen smoke-test engine after each refresh notification.
        awaitEngineEvents()

        val remainingAssetIds = engine.asset.findAssets(
            sourceId = externalSource.sourceId,
            query = FindAssetsQuery(page = 0, perPage = 20),
        ).assets.map { asset -> asset.id }

        val refreshEventSourceIds = awaitRefreshEvents(refreshEvents, awaitEngineEvents)

        RefreshAssetsResult(
            sourceId = externalSource.sourceId,
            initialTotal = initialAssets.total,
            afterUploadTotal = afterUploadAssets.total,
            renamedAssetLabel = renamedAssetLabel,
            remainingAssetIds = remainingAssetIds,
            refreshEventSourceIds = refreshEventSourceIds,
        )
    } finally {
        if (externalSource.sourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId = externalSource.sourceId)
        }
    }
}

private fun createExternalImageAssetSource() = ExternalImageAssetSource(
    initialAssets = listOf(
        externalImageAsset(
            id = "spring-poster",
            label = "Spring Poster",
            tags = listOf("poster", "spring"),
            uri = "https://img.ly/static/ubq_samples/sample_1.jpg",
        ),
        externalImageAsset(
            id = "summer-poster",
            label = "Summer Poster",
            tags = listOf("poster", "summer"),
            uri = "https://img.ly/static/ubq_samples/sample_2.jpg",
        ),
    ),
)

// highlight-android-asset-helper
private fun externalImageAsset(
    id: String,
    label: String,
    tags: List<String>,
    uri: String,
) = Asset(
    id = id,
    context = AssetContext(sourceId = EXTERNAL_IMAGE_SOURCE_ID),
    label = label,
    locale = "en",
    tags = tags,
    groups = listOf("campaign-assets"),
    meta = mapOf(
        "uri" to uri,
        "thumbUri" to uri,
        "mimeType" to MimeType.JPEG.key,
        "kind" to "image",
        "blockType" to DesignBlockType.Graphic.key,
        "fillType" to FillType.Image.key,
        "shapeType" to ShapeType.Rect.key,
        "width" to "1080",
        "height" to "1080",
    ),
)
// highlight-android-asset-helper

// highlight-android-custom-source
private class ExternalImageAssetSource(
    initialAssets: List<Asset>,
) : AssetSource(sourceId = EXTERNAL_IMAGE_SOURCE_ID) {
    var assets = initialAssets

    override val supportedMimeTypes = listOf(MimeType.JPEG.key)

    override suspend fun getGroups(): List<String>? = listOf("campaign-assets")

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = FindAssetsResult(
        assets = assets,
        currentPage = query.page,
        nextPage = -1,
        total = assets.size,
    )
}
// highlight-android-custom-source

private suspend fun awaitRefreshEvents(
    refreshEvents: Deferred<List<String>>,
    awaitEngineEvents: suspend () -> Unit,
): List<String> {
    // The offscreen smoke test drives engine updates manually until the native event flow emits a refresh signal.
    withTimeout(5_000) {
        while (!refreshEvents.isCompleted) {
            awaitEngineEvents()
            yield()
        }
    }
    return refreshEvents.await()
}
