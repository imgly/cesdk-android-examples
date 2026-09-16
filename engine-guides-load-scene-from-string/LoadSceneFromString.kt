import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

suspend fun loadSceneFromString(
    engine: Engine,
    context: Context,
): DesignBlock {
    // highlight-android-load-from-string
    val sceneString = withContext(Dispatchers.IO) {
        context.assets.open("imgly-assets/ly.img.templates/templates/cesdk_postcard_1.scene")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }
    val scene = engine.scene.load(scene = sceneString, waitForResources = true)
    // highlight-android-load-from-string

    val text = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.setDropShadowEnabled(block = text, enabled = true)

    return scene
}
