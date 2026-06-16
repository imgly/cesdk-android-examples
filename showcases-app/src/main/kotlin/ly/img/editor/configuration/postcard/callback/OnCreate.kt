@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.postcard.callback

import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.postcard.PostcardConfigurationBuilder
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.SystemGalleryAssetSource
import ly.img.editor.core.library.data.SystemGalleryPermission
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.GlobalScope

/**
 * The callback that is invoked when the editor is created.
 */
suspend fun PostcardConfigurationBuilder.onCreate(
    preCreateScene: suspend PostcardConfigurationBuilder.() -> Unit = {
        onPreCreateScene()
    },
    createScene: suspend PostcardConfigurationBuilder.() -> Unit = {
        onCreateScene()
    },
    loadAssetSources: suspend PostcardConfigurationBuilder.() -> Unit = {
        onLoadAssetSources()
    },
    postCreateScene: suspend PostcardConfigurationBuilder.() -> Unit = {
        onPostCreateScene()
    },
    finally: suspend PostcardConfigurationBuilder.() -> Unit = {
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
fun PostcardConfigurationBuilder.onPreCreateScene() {
    showLoading = true
    editorContext.engine.editor.setGlobalScope("editor/add", GlobalScope.DEFER)
}

/**
 * The callback that is responsible for creating the scene.
 */
// highlight-starter-kit-on-create-scene
suspend fun PostcardConfigurationBuilder.onCreateScene() {
    getOrLoadScene(sceneUri = "file:///android_asset/scene/postcard.scene".toUri())
}
// highlight-starter-kit-on-create-scene

/**
 * The callback that loads all the required assets sources.
 */
// highlight-starter-kit-on-load-asset-sources
suspend fun PostcardConfigurationBuilder.onLoadAssetSources() {
    // Load asset sources in parallel from content.json files
    coroutineScope {
        val baseUri = editorContext.baseUri
        // Postcard intentionally registers a custom subset of default sources (no
        // ly.img.color.palette / ly.img.page.presets — those are provided by the postcard builder).
        val sourceIds = listOf(
            "ly.img.sticker",
            "ly.img.vector.shape",
            "ly.img.filter",
            "ly.img.crop.presets",
            "ly.img.effect",
            "ly.img.blur",
            "ly.img.typeface",
            "ly.img.text.presets",
            "ly.img.text.components",
            "ly.img.caption.presets",
            "ly.img.image",
        )
        sourceIds.forEach { id ->
            launch {
                editorContext.engine.asset.addLocalSourceFromJSON(
                    contentUri = "$baseUri/$id/content.json".toUri(),
                )
            }
        }
    }

    // Load local asset sources
    editorContext.engine.asset.addLocalSource(
        sourceId = "ly.img.image.upload",
        supportedMimeTypes = listOf(
            "image/jpeg",
            "image/png",
            "image/heic",
            "image/heif",
            "image/svg+xml",
            "image/gif",
            "image/apng",
            "image/bmp",
        ),
    )

    // Register gallery asset sources
    listOf(
        AssetSourceType.GalleryAllVisuals,
        AssetSourceType.GalleryImage,
        AssetSourceType.GalleryVideo,
    ).forEach { type ->
        editorContext.engine.asset.addSource(
            source = SystemGalleryAssetSource(
                context = editorContext.engine.applicationContext,
                type = type,
            ),
        )
    }
    SystemGalleryPermission.setMode(systemGalleryConfiguration)

    // Register text asset source
    TypefaceProvider().provideTypeface(
        engine = editorContext.engine,
        name = "Roboto",
    )?.let {
        val textAssetSource = TextAssetSource(engine = editorContext.engine, typeface = it)
        editorContext.engine.asset.addSource(textAssetSource)
    }
}
// highlight-starter-kit-on-load-asset-sources

/**
 * The callback that is invoked right after [onCreateScene], after the scene is created.
 */
fun PostcardConfigurationBuilder.onPostCreateScene() {
    // Do nothing
}

/**
 * The callback that is invoked as the last step of [onCreate].
 * It always runs, no matter success or failure on previous steps.
 */
fun PostcardConfigurationBuilder.onCreateFinally() {
    showLoading = false
}
