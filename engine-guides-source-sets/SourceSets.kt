import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*

fun sourceSets(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

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
        sourceSet =
            listOf(
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
    val assetWithSourceSet =
        AssetDefinition(
            id = "my-image",
            meta =
                mapOf(
                    "kind" to "image",
                    "fillType" to "//ly.img.ubq/fill/image",
                ),
            payload =
                AssetPayload(
                    sourceSet =
                        listOf(
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
    val asset =
        Asset(
            id = assetWithSourceSet.id,
            meta = assetWithSourceSet.meta?.toMap(),
            context = AssetContext(sourceId = "my-dynamic-images"),
        )
    val result = engine.asset.defaultApplyAsset(asset)
    // highlight-apply-asset

    engine.stop()
}
