import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Source

fun sourceSets(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    engine.scene.zoomToBlock(
        block = page,
        paddingLeft = 50F,
        paddingTop = 50F,
        paddingRight = 50F,
        paddingBottom = 50F,
    )
    // highlight-setup

    // highlight-set-source-set
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, engine.block.createShape(ShapeType.Rect))
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
            Source(
                uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_2048x1366.jpg"),
                width = 2048,
                height = 1366,
            ),
        ),
    )
    engine.block.setFill(block = block, fill = imageFill)
    engine.block.appendChild(parent = page, child = block)
    // highlight-set-source-set

    // highlight-asset-definition
    val assetWithSourceSet = AssetDefinition(
        id = "my-image",
        meta = mapOf(
            "kind" to "image",
            "fillType" to "//ly.img.ubq/fill/image",
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
    // highlight-asset-definition

    // highlight-asset-source
    engine.asset.addLocalSource(
        sourceId = "my-dynamic-images",
        supportedMimeTypes = listOf("image/jpeg"),
    )
    engine.asset.addAsset(sourceId = "my-dynamic-images", asset = assetWithSourceSet)
    // highlight-asset-source

    // highlight-apply-asset
    // Could also acquire the asset using `findAssets` on the source
    val asset = Asset(
        id = assetWithSourceSet.id,
        meta = assetWithSourceSet.meta?.toMap(),
        context = AssetContext(sourceId = "my-dynamic-images"),
    )
    val result = engine.asset.defaultApplyAsset(asset)
    // highlight-apply-asset

    // highlight-video-source-sets
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

    engine.block.addVideoFileUriToSourceSet(
        block = videoFill,
        property = "fill/video/sourceSet",
        uri = "https://img.ly/static/example-assets/sourceset/2x.mp4",
    )

    // highlight-video-source-sets

    engine.stop()
}
