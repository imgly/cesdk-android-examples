import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.MimeType
import ly.img.engine.ShapeType

// highlight-android-remote-image-source
class RemoteImageAssetSource(
    assetBaseUri: String,
) : AssetSource(sourceId = SOURCE_ID) {
    override val supportedMimeTypes = listOf(MimeType.JPEG.key, MimeType.PNG.key)

    override suspend fun getGroups(): List<String>? = null

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = FindAssetsResult(
        assets = imageAssets,
        currentPage = query.page,
        nextPage = -1,
        total = imageAssets.size,
    )

    private val imageBaseUri = assetBaseUri.trimEnd('/')

    private val imageAssets = listOf(
        Asset(
            id = "brand-background",
            context = AssetContext(sourceId = sourceId),
            label = "Brand Background",
            locale = "en",
            tags = listOf("background", "brand"),
            meta = mapOf(
                "uri" to "$imageBaseUri/ly.img.image/images/sample_1.jpg",
                "thumbUri" to "$imageBaseUri/ly.img.image/thumbnails/sample_1.jpg",
                "mimeType" to MimeType.JPEG.key,
                "kind" to "image",
                "blockType" to DesignBlockType.Graphic.key,
                "fillType" to FillType.Image.key,
                "shapeType" to ShapeType.Rect.key,
                "width" to "1080",
                "height" to "720",
            ),
        ),
        Asset(
            id = "brand-landscape",
            context = AssetContext(sourceId = sourceId),
            label = "Brand Landscape",
            locale = "en",
            tags = listOf("landscape", "brand"),
            meta = mapOf(
                "uri" to "$imageBaseUri/ly.img.image/images/sample_10.jpg",
                "thumbUri" to "$imageBaseUri/ly.img.image/thumbnails/sample_10.jpg",
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

    private companion object {
        const val SOURCE_ID = "remote-image-assets"
    }
}
// highlight-android-remote-image-source
