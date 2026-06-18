@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.photo.callback

import android.net.Uri
import android.util.SizeF
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.engine.DesignBlock
import ly.img.engine.SceneLayout

/**
 * The callback that is invoked when the editor is created.
 */
suspend fun PhotoConfigurationBuilder.onCreate(
    preCreateScene: suspend PhotoConfigurationBuilder.() -> Unit = {
        onPreCreateScene()
    },
    createScene: suspend PhotoConfigurationBuilder.() -> Unit = {
        onCreateScene()
    },
    loadAssetSources: suspend PhotoConfigurationBuilder.() -> Unit = {
        onLoadAssetSources()
    },
    postCreateScene: suspend PhotoConfigurationBuilder.() -> Unit = {
        onPostCreateScene()
    },
    finally: suspend PhotoConfigurationBuilder.() -> Unit = {
        onCreateFinally()
    },
) {
    try {
        preCreateScene()
        createScene()
        loadAssetSources()
        postCreateScene()
    } finally {
        finally()
    }
}

/**
 * The callback that is invoked before the scene is created.
 */
fun PhotoConfigurationBuilder.onPreCreateScene() {
    showLoading = true
    editorContext.engine.editor.setSettingBoolean(keypath = "page/moveChildrenWhenCroppingFill", value = true)
    editorContext.engine.editor.setSettingBoolean(keypath = "page/selectWhenNoBlocksSelected", value = true)
    editorContext.engine.editor.setSettingBoolean(keypath = "page/highlightWhenCropping", value = true)
    editorContext.engine.editor.setSettingBoolean(keypath = "doubleClickToCropEnabled", value = false)
}

/**
 * The callback that is responsible for creating the scene.
 */
// highlight-starter-kit-on-create-scene
suspend fun PhotoConfigurationBuilder.onCreateScene() {
    getOrCreateSceneFromImage(imageUri = "https://cdn.img.ly/assets/demo/v3/ly.img.image/images/sample_1.jpg".toUri())
}

suspend fun PhotoConfigurationBuilder.getOrCreateSceneFromImage(
    imageUri: Uri,
    size: SizeF? = null,
    dpi: Float = 300F,
    pixelScaleFactor: Float = 1F,
    sceneLayout: SceneLayout = SceneLayout.FREE,
): DesignBlock = editorContext.engine.scene.get() ?: editorContext.engine.scene.createFromImage(
    imageUri = imageUri,
    dpi = dpi,
    pixelScaleFactor = pixelScaleFactor,
    sceneLayout = sceneLayout,
).also {
    size?.let {
        val page = requireNotNull(editorContext.engine.scene.getCurrentPage())
        editorContext.engine.block.setWidth(block = page, value = size.width)
        editorContext.engine.block.setHeight(block = page, value = size.height)
    }
}
// highlight-starter-kit-on-create-scene

/**
 * The callback that loads all the required assets sources.
 */
// highlight-starter-kit-on-load-asset-sources
suspend fun PhotoConfigurationBuilder.onLoadAssetSources() {
    // Load asset sources in parallel from content.json files
    coroutineScope {
        val baseUri = editorContext.baseUri
        val sourceIds = listOf(
            "ly.img.sticker",
            "ly.img.vector.shape",
            "ly.img.filter",
            "ly.img.color.palette",
            "ly.img.effect",
            "ly.img.blur",
            "ly.img.typeface",
            "ly.img.crop.presets",
            "ly.img.page.presets",
            "ly.img.text.presets",
            "ly.img.text.components",
        )
        sourceIds.forEach { id ->
            launch {
                editorContext.engine.asset.addLocalSourceFromJSON(
                    contentUri = "$baseUri/$id/content.json".toUri(),
                )
            }
        }
    }
}
// highlight-starter-kit-on-load-asset-sources

/**
 * The callback that is invoked right after [onCreateScene], after the scene is created.
 */
fun PhotoConfigurationBuilder.onPostCreateScene() {
    val page = requireNotNull(editorContext.engine.scene.getCurrentPage())
    editorContext.engine.block.setScopeEnabled(block = page, key = "layer/move", enabled = false)
}

/**
 * The callback that is invoked as the last step of [onCreate].
 * It always runs, no matter success or failure on previous steps.
 */
fun PhotoConfigurationBuilder.onCreateFinally() {
    showLoading = false
}
