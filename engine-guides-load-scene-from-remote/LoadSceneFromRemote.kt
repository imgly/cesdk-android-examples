import android.net.Uri
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

suspend fun loadSceneFromRemote(engine: Engine): DesignBlock {
    // highlight-android-load-from-url
    val sceneUri = Uri.parse(
        "file:///android_asset/imgly-assets/ly.img.templates/templates/cesdk_postcard_1.scene",
    )
    val scene = engine.scene.load(sceneUri = sceneUri, waitForResources = true)
    // highlight-android-load-from-url

    // highlight-android-modify-loaded-scene
    val text = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.setDropShadowEnabled(block = text, enabled = true)
    // highlight-android-modify-loaded-scene

    return scene
}
