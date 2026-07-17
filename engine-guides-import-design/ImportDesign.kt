import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.MimeType
import java.io.File

suspend fun importDesign(
    engine: Engine,
    assetBaseUri: Uri,
): ImportDesignResult {
    // highlight-android-source-uris
    val sceneUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_postcard_1.scene")
        .build()
    val imageUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates.premium")
        .appendPath("thumbnails")
        .appendPath("e-commerce")
        .appendPath("e-commerce_modern_square_n1.jpg")
        .build()
    val videoUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.video")
        .appendPath("videos")
        .appendPath("pexels-tony-schnagl-5528015.mp4")
        .build()
    // highlight-android-source-uris

    // highlight-android-load-from-uri
    val remoteScene = engine.scene.load(sceneUri = sceneUri, waitForResources = true)
    // highlight-android-load-from-uri

    // highlight-android-load-from-string
    val sceneString = engine.scene.saveToString(scene = remoteScene)
    val stringScene = engine.scene.load(scene = sceneString, waitForResources = true)
    // highlight-android-load-from-string

    val archiveBytes = engine.scene.saveToArchive(scene = stringScene)
    val archiveFile = withContext(Dispatchers.IO) {
        val file = File.createTempFile("import-design-", ".zip")
        val archiveBuffer = archiveBytes.asReadOnlyBuffer()
        file.outputStream().channel.use { channel ->
            while (archiveBuffer.hasRemaining()) {
                channel.write(archiveBuffer)
            }
        }
        file
    }
    // highlight-android-load-from-archive
    val archiveScene = engine.scene.loadArchive(
        archiveUri = Uri.fromFile(archiveFile),
        waitForResources = true,
    )
    // highlight-android-load-from-archive

    // highlight-android-create-from-image
    val imageScene = engine.scene.createFromImage(imageUri = imageUri)
    // highlight-android-create-from-image

    val previewPngData = engine.block.export(
        block = engine.scene.getPages().first(),
        mimeType = MimeType.PNG,
    )

    // highlight-android-create-from-video
    val videoScene = engine.scene.createFromVideo(videoUri = videoUri)
    // highlight-android-create-from-video

    // highlight-android-modify-loaded-scene
    engine.scene.load(sceneUri = sceneUri, waitForResources = true)
    val textBlock = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.setOpacity(block = textBlock, value = 0.85F)
    // highlight-android-modify-loaded-scene

    return ImportDesignResult(
        remoteScene = remoteScene,
        stringScene = stringScene,
        archiveScene = archiveScene,
        imageScene = imageScene,
        videoScene = videoScene,
        textOpacity = engine.block.getOpacity(textBlock),
        pageCount = engine.scene.getPages().size,
        previewPngData = previewPngData,
    )
}
