import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

suspend fun loadSceneFromBlob(
    engine: Engine,
    context: Context,
): DesignBlock {
    // highlight-android-load-from-blob
    val sceneBytes = withContext(Dispatchers.IO) {
        context.assets.open("imgly-assets/ly.img.templates/templates/cesdk_postcard_1.scene")
            .use { it.readBytes() }
    }
    val sceneString = sceneBytes.toString(Charsets.UTF_8)
    val scene = engine.scene.load(scene = sceneString, waitForResources = true)
    // highlight-android-load-from-blob

    val text = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.setDropShadowEnabled(block = text, enabled = true)

    return scene
}
