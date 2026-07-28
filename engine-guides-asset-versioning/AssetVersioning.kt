import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Source

data class AssetVersioning(
    val storedUri: Uri,
    val sceneStringLength: Int,
    val archiveSizeBytes: Int,
    val updatedUri: Uri,
    val migratedUris: List<Uri>,
    val migratedSourceSetUris: List<Uri>,
    val migratedSceneStringLength: Int,
    val versionedUrlPatterns: List<String>,
)

suspend fun assetVersioning(engine: Engine): AssetVersioning {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg")
    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block = imageBlock, value = 50F)
    engine.block.setPositionY(block = imageBlock, value = 50F)
    engine.block.setWidth(block = imageBlock, value = 300F)
    engine.block.setHeight(block = imageBlock, value = 200F)
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.appendChild(parent = page, child = imageBlock)

    val secondImageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_3.jpg")
    val secondImageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = secondImageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(block = secondImageBlock, value = 400F)
    engine.block.setPositionY(block = secondImageBlock, value = 50F)
    engine.block.setWidth(block = secondImageBlock, value = 300F)
    engine.block.setHeight(block = secondImageBlock, value = 200F)
    val secondImageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = secondImageFill,
        property = "fill/image/imageFileURI",
        value = secondImageUri,
    )
    engine.block.setSourceSet(
        block = secondImageFill,
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
    engine.block.setFill(block = secondImageBlock, fill = secondImageFill)
    engine.block.appendChild(parent = page, child = secondImageBlock)

    // highlight-android-how-urls-stored
    val fill = engine.block.getFill(imageBlock)
    val storedUri = engine.block.getUri(
        block = fill,
        property = "fill/image/imageFileURI",
    )
    // highlight-android-how-urls-stored

    check(storedUri == imageUri)

    // highlight-android-save-scene
    val sceneString = engine.scene.saveToString(scene = scene)
    // highlight-android-save-scene

    // highlight-android-save-archive
    val archiveBuffer = engine.scene.saveToArchive(scene = scene)
    // highlight-android-save-archive

    val archiveSizeBytes = archiveBuffer.asReadOnlyBuffer().remaining()
    check(sceneString.isNotEmpty())
    check(archiveSizeBytes > 0)

    // highlight-android-update-url
    val loadedScene = engine.scene.load(scene = sceneString, waitForResources = true)
    val loadedPage = engine.scene.getPages().first()
    val loadedImageBlock = engine.block.getChildren(loadedPage).first()
    val loadedFill = engine.block.getFill(loadedImageBlock)
    val updatedUri = Uri.parse("https://img.ly/static/ubq_samples/sample_2.jpg")

    engine.block.setUri(
        block = loadedFill,
        property = "fill/image/imageFileURI",
        value = updatedUri,
    )

    val currentUri = engine.block.getUri(
        block = loadedFill,
        property = "fill/image/imageFileURI",
    )
    // highlight-android-update-url

    check(currentUri == updatedUri)

    // highlight-android-batch-update
    val oldAssetPrefix = "https://img.ly/static/ubq_samples"
    val newAssetPrefix = "https://cdn.example.com/assets/v2"
    val migratedUris = mutableListOf<Uri>()
    val migratedSourceSetUris = mutableListOf<Uri>()

    engine.block.findByType(DesignBlockType.Graphic).forEach { block ->
        if (!engine.block.supportsFill(block)) return@forEach

        val blockFill = engine.block.getFill(block)
        if (engine.block.getType(blockFill) != FillType.Image.key) return@forEach

        val previousUri = engine.block.getUri(
            block = blockFill,
            property = "fill/image/imageFileURI",
        )
        if (previousUri.toString().startsWith(oldAssetPrefix)) {
            val migratedUri = Uri.parse(
                previousUri.toString().replace(
                    oldValue = oldAssetPrefix,
                    newValue = newAssetPrefix,
                ),
            )
            engine.block.setUri(
                block = blockFill,
                property = "fill/image/imageFileURI",
                value = migratedUri,
            )
            migratedUris += migratedUri
        }

        var sourceSetChanged = false
        val migratedSourceSet = engine.block.getSourceSet(
            block = blockFill,
            property = "fill/image/sourceSet",
        ).map { source ->
            if (!source.uri.toString().startsWith(oldAssetPrefix)) return@map source

            sourceSetChanged = true
            val migratedSourceUri = Uri.parse(
                source.uri.toString().replace(
                    oldValue = oldAssetPrefix,
                    newValue = newAssetPrefix,
                ),
            )
            migratedSourceSetUris += migratedSourceUri
            source.copy(uri = migratedSourceUri)
        }

        if (sourceSetChanged) {
            engine.block.setSourceSet(
                block = blockFill,
                property = "fill/image/sourceSet",
                sourceSet = migratedSourceSet,
            )
        }
    }

    val migratedSceneString = engine.scene.saveToString(scene = loadedScene)
    // highlight-android-batch-update

    check(migratedUris.isNotEmpty())
    check(migratedSceneString.isNotEmpty())

    // highlight-android-versioned-urls
    val pathVersionedUrl = "https://cdn.example.com/assets/v2/logo.png"
    val hashVersionedUrl = "https://cdn.example.com/assets/logo-a1b2c3d4.png"
    val queryVersionedUrl = "https://cdn.example.com/assets/logo.png?v=2"
    // highlight-android-versioned-urls

    return AssetVersioning(
        storedUri = storedUri,
        sceneStringLength = sceneString.length,
        archiveSizeBytes = archiveSizeBytes,
        updatedUri = currentUri,
        migratedUris = migratedUris,
        migratedSourceSetUris = migratedSourceSetUris,
        migratedSceneStringLength = migratedSceneString.length,
        versionedUrlPatterns = listOf(pathVersionedUrl, hashVersionedUrl, queryVersionedUrl),
    )
}
