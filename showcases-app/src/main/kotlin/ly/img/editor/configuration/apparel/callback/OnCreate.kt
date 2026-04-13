@file:OptIn(UnstableEditorApi::class)

package ly.img.editor.configuration.apparel.callback

import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.SystemGalleryAssetSource
import ly.img.editor.core.library.data.SystemGalleryPermission
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.BlendMode
import ly.img.engine.Color
import ly.img.engine.DefaultAssetSource
import ly.img.engine.DemoAssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.ShapeType
import ly.img.engine.StrokeStyle
import ly.img.engine.populateAssetSource

/**
 * The callback that is invoked when the editor is created.
 */
suspend fun ApparelConfigurationBuilder.onCreate(
    preCreateScene: suspend ApparelConfigurationBuilder.() -> Unit = {
        onPreCreateScene()
    },
    createScene: suspend ApparelConfigurationBuilder.() -> Unit = {
        onCreateScene()
    },
    loadAssetSources: suspend ApparelConfigurationBuilder.() -> Unit = {
        onLoadAssetSources()
    },
    postCreateScene: suspend ApparelConfigurationBuilder.() -> Unit = {
        onPostCreateScene()
    },
    finally: suspend ApparelConfigurationBuilder.() -> Unit = {
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
fun ApparelConfigurationBuilder.onPreCreateScene() {
    showLoading = true
    editorContext.engine.editor.setSettingBoolean(
        keypath = "ubq://page/dimOutOfPageAreas",
        value = false,
    )
}

/**
 * The callback that is responsible for creating the scene.
 */
// highlight-starter-kit-on-create-scene
suspend fun ApparelConfigurationBuilder.onCreateScene() {
    getOrLoadScene(sceneUri = "file:///android_asset/scene/apparel.scene".toUri())
}
// highlight-starter-kit-on-create-scene

/**
 * The callback that loads all the required assets sources.
 */
// highlight-starter-kit-on-load-asset-sources
suspend fun ApparelConfigurationBuilder.onLoadAssetSources() {
    // Load asset sources in parallel from content.json files
    coroutineScope {
        listOf(
            DefaultAssetSource.STICKER.key,
            DefaultAssetSource.VECTOR_PATH.key,
            DefaultAssetSource.FILTER_LUT.key,
            DefaultAssetSource.FILTER_DUO_TONE.key,
            DefaultAssetSource.CROP_PRESETS.key,
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
fun ApparelConfigurationBuilder.onPostCreateScene() {
    val engine = editorContext.engine
    val page = requireNotNull(engine.scene.getCurrentPage())
    engine.block.setClipped(block = page, clipped = true)
    engine.block.setBoolean(block = page, property = "fill/enabled", value = false)

    // Add dotted outline design block.
    engine.block.findByName(ApparelConfigurationBuilder.Companion.OUTLINE_BLOCK_NAME).firstOrNull() ?: run {
        val outline = engine.block.create(blockType = DesignBlockType.Graphic)
        engine.block.setName(block = outline, name = ApparelConfigurationBuilder.Companion.OUTLINE_BLOCK_NAME)
        engine.block.setShape(
            block = outline,
            shape = engine.block.createShape(ShapeType.Rect),
        )
        engine.block.appendChild(parent = page, child = outline)
        engine.block.setFillEnabled(block = outline, enabled = false)
        engine.block.setStrokeEnabled(block = outline, enabled = true)
        engine.block.setStrokeColor(
            block = outline,
            color = Color.fromRGBA(r = 1F, g = 1F, b = 1F),
        )
        engine.block.setStrokeStyle(block = outline, style = StrokeStyle.DOTTED)
        engine.block.setStrokeWidth(block = outline, width = 1.0F)
        engine.block.setBlendMode(block = outline, blendMode = BlendMode.DIFFERENCE)
        engine.block.setScopeEnabled(
            block = outline,
            key = "editor/select",
            enabled = false,
        )
        engine.block.setScopeEnabled(
            block = outline,
            key = "lifecycle/destroy",
            enabled = false,
        )
    }
}

/**
 * The callback that is invoked as the last step of [onCreate].
 * It always runs, no matter success or failure on previous steps.
 */
fun ApparelConfigurationBuilder.onCreateFinally() {
    showLoading = false
}
