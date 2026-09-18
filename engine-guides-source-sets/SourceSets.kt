import android.net.Uri
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.Source
import java.nio.ByteBuffer

data class SourceSets(
    val configuredImageSourceWidths: List<Int>,
    val updatedImageSourceWidths: List<Int>,
    val assetSourceWidths: List<Int>,
    val videoSourceWidths: List<Int>,
    val lowQualityVideoPreviewEnabled: Boolean,
    val exportedImage: ByteBuffer,
)

suspend fun sourceSets(engine: Engine): SourceSets {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-set-source-set
    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(imageBlock, value = 300F)
    engine.block.setHeight(imageBlock, value = 300F)
    engine.block.setPositionX(imageBlock, value = 50F)
    engine.block.setPositionY(imageBlock, value = 50F)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
        sourceSet = listOf(
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_512x341.jpg"),
                width = 512,
                height = 341,
            ),
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_1024x683.jpg"),
                width = 1024,
                height = 683,
            ),
        ),
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.appendChild(parent = page, child = imageBlock)
    // highlight-android-set-source-set

    val configuredImageSourceSet = engine.block.getSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
    )
    check(configuredImageSourceSet.map(Source::width) == listOf(1024, 512))

    // highlight-android-query-source-set
    val currentImageSourceSet = engine.block.getSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
    )

    engine.block.addImageFileUriToSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
        uri = "https://img.ly/static/ubq_samples/sample_1_2048x1366.jpg",
    )

    val updatedImageSourceSet = engine.block.getSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
    )
    // highlight-android-query-source-set

    check(currentImageSourceSet.map(Source::width) == listOf(1024, 512))
    check(updatedImageSourceSet.map(Source::width) == listOf(2048, 1024, 512))

    // highlight-android-asset-source-set
    val assetSourceId = "android-guide-source-sets"
    if (assetSourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(assetSourceId)
    }

    val assetWithSourceSet = AssetDefinition(
        id = "multi-resolution-image",
        label = mapOf("en" to "Multi-resolution image"),
        meta = mapOf(
            "kind" to "image",
            "fillType" to FillType.Image.key,
        ),
        payload = AssetPayload(
            sourceSet = listOf(
                Source(
                    uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_512x341.jpg"),
                    width = 512,
                    height = 341,
                ),
                Source(
                    uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_1024x683.jpg"),
                    width = 1024,
                    height = 683,
                ),
                Source(
                    uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_2048x1366.jpg"),
                    width = 2048,
                    height = 1366,
                ),
            ),
        ),
    )

    engine.asset.addLocalSource(
        sourceId = assetSourceId,
        supportedMimeTypes = listOf(MimeType.JPEG.key),
    )
    engine.asset.addAsset(sourceId = assetSourceId, asset = assetWithSourceSet)

    val asset = engine.asset.fetchAsset(
        sourceId = assetSourceId,
        assetId = assetWithSourceSet.id,
    ) ?: error("Expected the local source to return the asset.")

    val assetBlock = engine.asset.defaultApplyAsset(asset)
        ?: error("Expected the image asset to create a block.")
    val assetFill = engine.block.getFill(assetBlock)
    val assetSourceSet = engine.block.getSourceSet(
        block = assetFill,
        property = "fill/image/sourceSet",
    )
    // highlight-android-asset-source-set

    check(assetSourceSet.map(Source::width) == listOf(2048, 1024, 512))

    // highlight-android-video-source-set
    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(videoBlock, value = 400F)
    engine.block.setHeight(videoBlock, value = 225F)
    engine.block.setPositionX(videoBlock, value = 50F)
    engine.block.setPositionY(videoBlock, value = 400F)

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setSourceSet(
        block = videoFill,
        property = "fill/video/sourceSet",
        sourceSet = listOf(
            Source(
                uri = Uri.parse("https://img.ly/static/example-assets/sourceset/1x.mp4"),
                width = 720,
                height = 1280,
            ),
        ),
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)
    engine.block.appendChild(parent = page, child = videoBlock)

    engine.block.addVideoFileUriToSourceSet(
        block = videoFill,
        property = "fill/video/sourceSet",
        uri = "https://img.ly/static/example-assets/sourceset/2x.mp4",
    )

    val videoSourceSet = engine.block.getSourceSet(
        block = videoFill,
        property = "fill/video/sourceSet",
    )
    // highlight-android-video-source-set

    check(videoSourceSet.map(Source::width) == listOf(1440, 720))

    val previousLowQualityVideoPreview = engine.editor.getSettingBoolean(
        keypath = "features/forceLowQualityVideoPreview",
    )

    // highlight-android-video-preview-settings
    engine.editor.setSettingBoolean(
        keypath = "features/forceLowQualityVideoPreview",
        value = true,
    )
    // highlight-android-video-preview-settings

    val lowQualityVideoPreviewEnabled = engine.editor.getSettingBoolean(
        keypath = "features/forceLowQualityVideoPreview",
    )
    engine.editor.setSettingBoolean(
        keypath = "features/forceLowQualityVideoPreview",
        value = previousLowQualityVideoPreview,
    )

    val exportedImage = engine.block.export(imageBlock, mimeType = MimeType.PNG)

    return SourceSets(
        configuredImageSourceWidths = configuredImageSourceSet.map(Source::width),
        updatedImageSourceWidths = updatedImageSourceSet.map(Source::width),
        assetSourceWidths = assetSourceSet.map(Source::width),
        videoSourceWidths = videoSourceSet.map(Source::width),
        lowQualityVideoPreviewEnabled = lowQualityVideoPreviewEnabled,
        exportedImage = exportedImage,
    )
}
