import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import java.io.File
import java.util.UUID

private const val LOCAL_ASSET_SOURCE_ID = "ly.img.guide.local-assets"

private val LOCAL_ASSET_MIME_TYPES = listOf(
    "image/*",
    "video/*",
    "audio/*",
)

data class LocalAssetImportResult(
    val sourceId: String,
    val assetId: String,
    val mimeType: String,
    val insertedBlock: DesignBlock?,
)

suspend fun importLocalAsset(
    context: Context,
    engine: Engine,
    fileUri: Uri,
    displayName: String,
    sourceId: String = LOCAL_ASSET_SOURCE_ID,
    thumbnailUri: Uri? = null,
    videoDurationSeconds: Double? = null,
): LocalAssetImportResult {
    // highlight-android-register-source
    if (sourceId !in engine.asset.findAllSources()) {
        engine.asset.addLocalSource(
            sourceId = sourceId,
            supportedMimeTypes = LOCAL_ASSET_MIME_TYPES,
        )
    }
    // highlight-android-register-source

    // highlight-android-validate-file
    val mimeType = engine.editor.getMimeType(uri = fileUri)
    val supportedMimeTypes = engine.asset.getSourceSupportedMimeTypes(sourceId = sourceId)
    val acceptsMimeType = supportedMimeTypes.isEmpty() ||
        supportedMimeTypes.any { supportedMimeType ->
            supportedMimeType == "*/*" ||
                supportedMimeType == mimeType ||
                (supportedMimeType.endsWith("/*") && mimeType.startsWith(supportedMimeType.removeSuffix("*")))
        }

    require(acceptsMimeType) {
        "Unsupported local asset MIME type: $mimeType"
    }
    // highlight-android-validate-file

    val asset = createLocalAssetDefinition(
        context = context,
        fileUri = fileUri,
        displayName = displayName,
        mimeType = mimeType,
        thumbnailUri = thumbnailUri,
        videoDurationSeconds = videoDurationSeconds,
    )

    // highlight-android-add-asset
    engine.asset.addAsset(sourceId = sourceId, asset = asset)
    // highlight-android-add-asset

    // highlight-android-insert-asset
    val importedAsset = engine.asset.fetchAsset(
        sourceId = sourceId,
        assetId = asset.id,
    ) ?: error("Could not fetch imported local asset: ${asset.id}")

    val insertedBlock = engine.asset.defaultApplyAsset(asset = importedAsset)
    // highlight-android-insert-asset

    return LocalAssetImportResult(
        sourceId = sourceId,
        assetId = asset.id,
        mimeType = mimeType,
        insertedBlock = insertedBlock,
    )
}

// highlight-android-read-dimensions
data class LocalAssetDimensions(
    val width: Int,
    val height: Int,
)

fun readLocalAssetDimensions(
    context: Context,
    fileUri: Uri,
    mimeType: String,
): LocalAssetDimensions? = when {
    mimeType.startsWith("image/") -> readImageDimensions(context, fileUri)
    mimeType.startsWith("video/") -> readVideoDimensions(context, fileUri)
    else -> null
}

private fun readImageDimensions(
    context: Context,
    fileUri: Uri,
): LocalAssetDimensions {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    when (fileUri.scheme) {
        "file" -> {
            val file = File(requireNotNull(fileUri.path) { "File URI has no path: $fileUri" })
            file.inputStream().use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }
        }

        else -> {
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            } ?: error("Could not open local image: $fileUri")
        }
    }

    return requireDimensions(
        width = options.outWidth,
        height = options.outHeight,
        fileUri = fileUri,
    )
}

private fun readVideoDimensions(
    context: Context,
    fileUri: Uri,
): LocalAssetDimensions {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, fileUri)
        val width = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            ?.toIntOrNull()
        val height = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            ?.toIntOrNull()
        val rotation = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            ?.toIntOrNull()
        val dimensions = requireDimensions(
            width = width,
            height = height,
            fileUri = fileUri,
        )
        if (rotation == 90 || rotation == 270) {
            LocalAssetDimensions(width = dimensions.height, height = dimensions.width)
        } else {
            dimensions
        }
    } finally {
        runCatching { retriever.release() }
    }
}

private fun requireDimensions(
    width: Int?,
    height: Int?,
    fileUri: Uri,
): LocalAssetDimensions {
    require(width != null && width > 0 && height != null && height > 0) {
        "Could not read dimensions for local asset: $fileUri"
    }
    return LocalAssetDimensions(width = width, height = height)
}
// highlight-android-read-dimensions

// highlight-android-define-asset
fun createLocalAssetDefinition(
    context: Context,
    fileUri: Uri,
    displayName: String,
    mimeType: String,
    thumbnailUri: Uri? = null,
    videoDurationSeconds: Double? = null,
    dimensions: LocalAssetDimensions? = null,
): AssetDefinition {
    val metadata = mutableMapOf(
        "uri" to fileUri.toString(),
        "mimeType" to mimeType,
    )
    val mediaDimensions = dimensions ?: readLocalAssetDimensions(
        context = context,
        fileUri = fileUri,
        mimeType = mimeType,
    )
    mediaDimensions?.let {
        metadata["width"] = it.width.toString()
        metadata["height"] = it.height.toString()
    }

    when {
        mimeType.startsWith("image/") -> {
            metadata["kind"] = "image"
            metadata["thumbUri"] = (thumbnailUri ?: fileUri).toString()
            metadata["blockType"] = DesignBlockType.Graphic.key
            metadata["fillType"] = FillType.Image.key
            metadata["shapeType"] = ShapeType.Rect.key
        }

        mimeType.startsWith("video/") -> {
            val duration = requireNotNull(videoDurationSeconds) {
                "Video assets require duration metadata in seconds."
            }
            metadata["kind"] = "video"
            thumbnailUri?.let { metadata["thumbUri"] = it.toString() }
            metadata["duration"] = duration.toString()
            metadata["blockType"] = DesignBlockType.Graphic.key
            metadata["fillType"] = FillType.Video.key
            metadata["shapeType"] = ShapeType.Rect.key
        }

        mimeType.startsWith("audio/") -> {
            metadata["kind"] = "audio"
            metadata["blockType"] = DesignBlockType.Audio.key
        }

        else -> error("Unsupported local asset MIME type: $mimeType")
    }

    return AssetDefinition(
        id = UUID.randomUUID().toString(),
        label = mapOf("en" to displayName),
        tags = mapOf("en" to listOf("local", "device")),
        meta = metadata,
    )
}
// highlight-android-define-asset
