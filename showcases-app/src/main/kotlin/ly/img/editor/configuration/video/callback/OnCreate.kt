@file:OptIn(UnstableEditorApi::class)
@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.video.callback

import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.SystemGalleryAssetSource
import ly.img.editor.core.library.data.SystemGalleryPermission
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.Color
import ly.img.engine.DefaultAssetSource
import ly.img.engine.DemoAssetSource
import ly.img.engine.populateAssetSource

/**
 * The callback that is invoked when the editor is created.
 */
suspend fun VideoConfigurationBuilder.onCreate(
    preCreateScene: suspend VideoConfigurationBuilder.() -> Unit = {
        onPreCreateScene()
    },
    createScene: suspend VideoConfigurationBuilder.() -> Unit = {
        onCreateScene()
    },
    loadAssetSources: suspend VideoConfigurationBuilder.() -> Unit = {
        onLoadAssetSources()
    },
    postCreateScene: suspend VideoConfigurationBuilder.() -> Unit = {
        onPostCreateScene()
    },
    finally: suspend VideoConfigurationBuilder.() -> Unit = {
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
fun VideoConfigurationBuilder.onPreCreateScene() {
    showLoading = true
    val engine = editorContext.engine
    engine.editor.setSettingEnum(keypath = "touch/pinchAction", value = "Scale")
    engine.editor.setSettingBoolean(
        keypath = "controlGizmo/showRotateHandles",
        value = false,
    )
    engine.editor.setSettingBoolean(
        keypath = "controlGizmo/showScaleHandles",
        value = false,
    )
    engine.editor.setSettingBoolean(keypath = "controlGizmo/showMoveHandles", value = false)
    engine.editor.setSettingBoolean(keypath = "touch/singlePointPanning", value = false)
    engine.editor.setSettingColor(
        keypath = "page/innerBorderColor",
        value = Color.fromRGBA(0.67f, 0.67f, 0.67f, 0.5f),
    )
}

/**
 * The callback that is responsible for creating the scene.
 */
// highlight-starter-kit-on-create-scene
suspend fun VideoConfigurationBuilder.onCreateScene() {
    getOrLoadScene(sceneUri = "file:///android_asset/scene/video.scene".toUri())
}
// highlight-starter-kit-on-create-scene

/**
 * The callback that loads all the required assets sources.
 */
// highlight-starter-kit-on-load-asset-sources
suspend fun VideoConfigurationBuilder.onLoadAssetSources() {
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
            DemoAssetSource.AUDIO.key,
            DemoAssetSource.VIDEO.key,
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

    // Required for animations
    editorContext.engine.block.setMetadata(
        block = requireNotNull(editorContext.engine.scene.get()),
        key = "ly.img.defaultAssetSourcesBaseUri",
        value = editorContext.baseUri.toString(),
    )

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
            "image/apng",
            "image/bmp",
        ),
    )

    editorContext.engine.asset.addLocalSource(
        sourceId = DemoAssetSource.AUDIO_UPLOAD.key,
        supportedMimeTypes = listOf(
            "audio/x-m4a",
            "audio/mp3",
            "audio/mpeg",
        ),
    )
    editorContext.engine.asset.addLocalSource(
        sourceId = DemoAssetSource.VIDEO_UPLOAD.key,
        supportedMimeTypes = listOf(
            "video/mp4",
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
fun VideoConfigurationBuilder.onPostCreateScene() {
    // Do nothing
}

/**
 * The callback that is invoked as the last step of [onCreate].
 * It always runs, no matter success or failure on previous steps.
 */
fun VideoConfigurationBuilder.onCreateFinally() {
    showLoading = false
}
