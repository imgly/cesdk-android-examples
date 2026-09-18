import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout

suspend fun createSceneFromImageURL(engine: Engine) {
    // highlight-android-create-from-url
    val imageRemoteUri = Uri.parse("https://img.ly/static/ubq_samples/sample_4.jpg")
    engine.scene.createFromImage(imageRemoteUri)
    // highlight-android-create-from-url

    // highlight-android-work-with-scene
    val page = engine.block.findByType(DesignBlockType.Page).first()
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    // highlight-android-work-with-scene

    check(pageWidth > 0F)
    check(pageHeight > 0F)

    // highlight-android-inspect-page-fill
    val pageFill = engine.block.getFill(page)
    val imageFillType = engine.block.getType(pageFill)
    // highlight-android-inspect-page-fill

    check(imageFillType == FillType.Image.key)

    // highlight-android-configure-scene
    val configuredScene = engine.scene.createFromImage(
        imageUri = imageRemoteUri,
        dpi = 300F,
        pixelScaleFactor = 1F,
        sceneLayout = SceneLayout.FREE,
    )
    // highlight-android-configure-scene

    check(configuredScene >= 0)
}
