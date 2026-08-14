package ly.img.editor.configuration.memories.extension

import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.style.registerStyleAssetSource
import ly.img.editor.core.EditorScope
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.SystemGalleryAssetSource
import ly.img.editor.core.library.data.SystemGalleryConfiguration
import ly.img.editor.core.library.data.SystemGalleryPermission
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider

suspend fun EditorScope.onLoadAssetSources() {
    // Register the kit's own custom local asset source: the bundled style backdrop videos and
    // picker thumbnails, described by assets/ly.img.memories.style/content.json (files stay local).
    editorContext.engine.registerStyleAssetSource()

    // Load the default asset libraries in parallel from their content.json files using the current
    // v5 source ids via addLocalSourceFromJSON, mirroring the video starter kit. The legacy v4 ids
    // (ly.img.vectorpath / ly.img.filter.lut / ly.img.filter.duotone / ly.img.textComponents) plus
    // the deprecated populateAssetSource combination 404s on the CDN and hangs the whole load.
    coroutineScope {
        val baseUri = editorContext.baseUri
        listOf(
            "ly.img.sticker",
            "ly.img.vector.shape",
            "ly.img.filter",
            "ly.img.color.palette",
            "ly.img.effect",
            "ly.img.blur",
            "ly.img.typeface",
            "ly.img.crop.presets",
            "ly.img.page.presets",
            "ly.img.text",
            "ly.img.text.styles",
            "ly.img.text.curves",
            "ly.img.text.components",
            "ly.img.image",
            "ly.img.audio",
            "ly.img.video",
        ).forEach { id ->
            launch {
                editorContext.engine.asset.addLocalSourceFromJSON(
                    contentUri = "$baseUri/$id/content.json".toUri(),
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

    // Load local (upload) asset sources
    editorContext.engine.asset.addLocalSource(
        sourceId = "ly.img.image.upload",
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

    editorContext.engine.asset.addLocalSource(
        sourceId = "ly.img.audio.upload",
        supportedMimeTypes = listOf(
            "audio/x-m4a",
            "audio/mp3",
            "audio/mpeg",
        ),
    )
    editorContext.engine.asset.addLocalSource(
        sourceId = "ly.img.video.upload",
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
    SystemGalleryPermission.setMode(SystemGalleryConfiguration.Enabled)

    // Register text asset source
    TypefaceProvider().provideTypeface(
        engine = editorContext.engine,
        name = "Roboto",
    )?.let {
        val textAssetSource = TextAssetSource(engine = editorContext.engine, typeface = it)
        editorContext.engine.asset.addSource(textAssetSource)
    }
}
