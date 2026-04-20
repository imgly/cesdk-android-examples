package ly.img.editor.configuration.design.callback

import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.SystemGalleryAssetSource
import ly.img.editor.core.library.data.SystemGalleryPermission
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.DefaultAssetSource
import ly.img.engine.DemoAssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.populateAssetSource

/**
 * The callback that is invoked when the editor is created.
 */
suspend fun DesignConfigurationBuilder.onCreate(
    preCreateScene: suspend DesignConfigurationBuilder.() -> Unit = {
        onPreCreateScene()
    },
    createScene: suspend DesignConfigurationBuilder.() -> Unit = {
        onCreateScene()
    },
    loadAssetSources: suspend DesignConfigurationBuilder.() -> Unit = {
        onLoadAssetSources()
    },
    postCreateScene: suspend DesignConfigurationBuilder.() -> Unit = {
        onPostCreateScene()
    },
    finally: suspend DesignConfigurationBuilder.() -> Unit = {
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
fun DesignConfigurationBuilder.onPreCreateScene() {
    showLoading = true
    // Enable horizontal sliding between pages
    editorContext.engine.editor.setSettingBoolean(
        keypath = "features/pageCarouselEnabled",
        value = true,
    )
}

/**
 * The callback that is responsible for creating the scene.
 */
// highlight-starter-kit-on-create-scene
suspend fun DesignConfigurationBuilder.onCreateScene() {
    getOrLoadScene(sceneUri = "file:///android_asset/scene/design.scene".toUri())
}
// highlight-starter-kit-on-create-scene

/**
 * The callback that loads all the required assets sources.
 */
// highlight-starter-kit-on-load-asset-sources
suspend fun DesignConfigurationBuilder.onLoadAssetSources() {
    // Load asset sources in parallel from content.json files
    coroutineScope {
        listOf(
            DefaultAssetSource.STICKER.key,
            DefaultAssetSource.VECTOR_PATH.key,
            DefaultAssetSource.FILTER_LUT.key,
            DefaultAssetSource.FILTER_DUO_TONE.key,
            DefaultAssetSource.CROP_PRESETS.key,
            DefaultAssetSource.PAGE_PRESETS.key,
            DefaultAssetSource.EFFECT.key,
            DefaultAssetSource.BLUR.key,
            DefaultAssetSource.TYPEFACE.key,
            DemoAssetSource.IMAGE.key,
            DemoAssetSource.TEXT_COMPONENTS.key,
        ).forEach { assetSource ->
            launch {
                val baseUri = editorContext.baseUri
                editorContext.engine.populateAssetSource(
                    id = assetSource,
                    jsonUri = "$baseUri/$assetSource/content.json".toUri(),
                    replaceBaseUri = baseUri,
                )
            }
        }
    }

    // Load local asset sources
    editorContext.engine.asset.addLocalSource(
        sourceId = DemoAssetSource.IMAGE_UPLOAD.key,
        supportedMimeTypes = listOf(
            "image/jpeg",
            "image/png",
            "image/heic",
            "image/heif",
            "image/svg+xml",
            "image/gif",
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
fun DesignConfigurationBuilder.onPostCreateScene() {
    editorContext.engine.block
        .findByType(DesignBlockType.Stack)
        .firstOrNull()
        ?.let {
            // Display all pages in a horizontal stack.
            editorContext.engine.block.setEnum(
                block = it,
                property = "stack/axis",
                value = "Horizontal",
            )
        }
}

/**
 * The callback that is invoked as the last step of [onCreate].
 * It always runs, no matter success or failure on previous steps.
 */
fun DesignConfigurationBuilder.onCreateFinally() {
    showLoading = false
}
