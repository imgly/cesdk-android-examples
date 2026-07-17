import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.ShapeType

private const val ASSET_BASE_URI = "https://cdn.img.ly/packages/imgly/cesdk-android/1.79.0-rc.0/assets"
private const val IMAGE_SOURCE_ID = "ly.img.guides.asset-library-thumbnails.images"
private const val AUDIO_SOURCE_ID = "ly.img.guides.asset-library-thumbnails.audio"
private const val REMOTE_SOURCE_ID = "ly.img.guides.asset-library-thumbnails.remote"

data class AssetLibraryThumbnailsSummary(
    val imageThumbUri: String,
    val audioThumbUri: String,
    val audioPreviewUri: String,
    val remoteAssetCount: Int,
    val remoteThumbUris: List<String>,
)

suspend fun assetLibraryThumbnails(engine: Engine): AssetLibraryThumbnailsSummary {
    listOf(IMAGE_SOURCE_ID, AUDIO_SOURCE_ID, REMOTE_SOURCE_ID).forEach { sourceId ->
        if (sourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId)
        }
    }

    val remoteSource = ThumbnailAssetSource()
    try {
        // highlight-android-basic-thumbnails
        engine.asset.addLocalSource(
            sourceId = IMAGE_SOURCE_ID,
            supportedMimeTypes = listOf("image/jpeg"),
        )

        val imageAsset = AssetDefinition(
            id = "sample-landscape",
            label = mapOf("en" to "Landscape Photo"),
            tags = mapOf("en" to listOf("landscape", "thumbnail")),
            meta = mapOf(
                "uri" to "$ASSET_BASE_URI/ly.img.image/images/sample_1.jpg",
                "thumbUri" to "$ASSET_BASE_URI/ly.img.image/thumbnails/sample_1.jpg",
                "mimeType" to "image/jpeg",
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1767",
                "height" to "1178",
            ),
        )
        engine.asset.addAsset(sourceId = IMAGE_SOURCE_ID, asset = imageAsset)
        engine.asset.assetSourceContentsChanged(sourceId = IMAGE_SOURCE_ID)
        // highlight-android-basic-thumbnails

        // highlight-android-audio-preview-uri
        engine.asset.addLocalSource(
            sourceId = AUDIO_SOURCE_ID,
            supportedMimeTypes = listOf("audio/x-m4a"),
        )

        val audioAsset = AssetDefinition(
            id = "short-audio-preview",
            label = mapOf("en" to "Short Audio Preview"),
            meta = mapOf(
                "uri" to "$ASSET_BASE_URI/ly.img.audio/audios/far_from_home.m4a",
                "thumbUri" to "$ASSET_BASE_URI/ly.img.audio/thumbnails/audio-wave.png",
                "previewUri" to "$ASSET_BASE_URI/ly.img.audio/audios/far_from_home.m4a",
                "mimeType" to "audio/x-m4a",
                "blockType" to DesignBlockType.Audio.key,
                "duration" to "98.716009",
            ),
        )
        engine.asset.addAsset(sourceId = AUDIO_SOURCE_ID, asset = audioAsset)
        engine.asset.assetSourceContentsChanged(sourceId = AUDIO_SOURCE_ID)
        // highlight-android-audio-preview-uri

        engine.asset.addSource(remoteSource)

        val queriedImage = engine.asset.findAssets(
            sourceId = IMAGE_SOURCE_ID,
            query = FindAssetsQuery(perPage = 1, page = 0),
        ).assets.first()
        val queriedAudio = engine.asset.findAssets(
            sourceId = AUDIO_SOURCE_ID,
            query = FindAssetsQuery(perPage = 1, page = 0),
        ).assets.first()
        val remoteAssets = engine.asset.findAssets(
            sourceId = REMOTE_SOURCE_ID,
            query = FindAssetsQuery(perPage = 10, page = 0),
        )

        return AssetLibraryThumbnailsSummary(
            imageThumbUri = queriedImage.meta?.get("thumbUri").orEmpty(),
            audioThumbUri = queriedAudio.meta?.get("thumbUri").orEmpty(),
            audioPreviewUri = queriedAudio.meta?.get("previewUri").orEmpty(),
            remoteAssetCount = remoteAssets.total,
            remoteThumbUris = remoteAssets.assets.mapNotNull { it.meta?.get("thumbUri") },
        )
    } finally {
        listOf(IMAGE_SOURCE_ID, AUDIO_SOURCE_ID, REMOTE_SOURCE_ID).forEach { sourceId ->
            if (sourceId in engine.asset.findAllSources()) {
                engine.asset.removeSource(sourceId)
            }
        }
    }
}

private class ThumbnailAssetSource : AssetSource(sourceId = REMOTE_SOURCE_ID) {
    override val supportedMimeTypes = listOf("image/jpeg")

    override suspend fun getGroups(): List<String> = listOf("remote")

    // highlight-android-custom-source-thumbnails
    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val pageAssets = remotePhotos.drop(query.page * query.perPage).take(query.perPage)

        return FindAssetsResult(
            assets = pageAssets.map { photo ->
                Asset(
                    id = photo.id,
                    context = AssetContext(sourceId = sourceId),
                    label = photo.label,
                    locale = "en",
                    groups = listOf("remote"),
                    meta = mapOf(
                        "uri" to photo.fullSizeUri,
                        "thumbUri" to photo.thumbnailUri,
                        "mimeType" to "image/jpeg",
                        "kind" to "image",
                        "blockType" to DesignBlockType.Graphic.key,
                        "fillType" to FillType.Image.key,
                        "shapeType" to ShapeType.Rect.key,
                        "width" to photo.width.toString(),
                        "height" to photo.height.toString(),
                    ),
                )
            },
            currentPage = query.page,
            nextPage = if ((query.page + 1) * query.perPage < remotePhotos.size) query.page + 1 else -1,
            total = remotePhotos.size,
        )
    }
    // highlight-android-custom-source-thumbnails

    private val remotePhotos = listOf(
        RemotePhoto(
            id = "remote-landscape",
            label = "Remote Landscape",
            fullSizeUri = "$ASSET_BASE_URI/ly.img.image/images/sample_4.jpg",
            thumbnailUri = "$ASSET_BASE_URI/ly.img.image/thumbnails/sample_4.jpg",
            width = 1178,
            height = 1767,
        ),
        RemotePhoto(
            id = "remote-water",
            label = "Remote Water",
            fullSizeUri = "$ASSET_BASE_URI/ly.img.image/images/sample_5.jpg",
            thumbnailUri = "$ASSET_BASE_URI/ly.img.image/thumbnails/sample_5.jpg",
            width = 1767,
            height = 1262,
        ),
    )
}

private data class RemotePhoto(
    val id: String,
    val label: String,
    val fullSizeUri: String,
    val thumbnailUri: String,
    val width: Int,
    val height: Int,
)
