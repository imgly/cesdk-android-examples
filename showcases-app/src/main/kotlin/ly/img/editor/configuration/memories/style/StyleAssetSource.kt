package ly.img.editor.configuration.memories.style

import androidx.core.net.toUri
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery

/**
 * The custom **local** asset source that supplies the bundled style assets — the looping backdrop
 * videos and every style's picker thumbnail. The files stay local (in `src/main/assets`) and are
 * described by `assets/ly.img.memories.style/content.json`; the engine loads them through
 * [addLocalSourceFromJSON][ly.img.engine.AssetApi.addLocalSourceFromJSON], exactly like the default
 * IMG.LY sources. Keeping them behind an asset source means the style catalog references assets by
 * id ([VideoStyle]) instead of hard-coding `file:///android_asset/...` paths across the kit.
 */
const val STYLE_SOURCE_ID = "ly.img.memories.style"

/** The bundled `content.json` describing [STYLE_SOURCE_ID], resolved as an `android_asset` URI. */
private const val STYLE_SOURCE_CONTENT_URI = "file:///android_asset/$STYLE_SOURCE_ID/content.json"

/**
 * Register [STYLE_SOURCE_ID] from its bundled `content.json` (idempotent). Call once while loading
 * the other asset sources, before any style is applied or the Styles picker is shown.
 */
suspend fun Engine.registerStyleAssetSource() {
    if (STYLE_SOURCE_ID !in asset.findAllSources()) {
        asset.addLocalSourceFromJSON(contentUri = STYLE_SOURCE_CONTENT_URI.toUri())
    }
}

/**
 * The picker thumbnail URI for each style, keyed by style id, read from [STYLE_SOURCE_ID]. Styles
 * without a bundled asset (e.g. the unstyled default) are simply absent from the map.
 */
suspend fun Engine.loadStyleThumbnails(): Map<String, String> = asset.findAssets(
    sourceId = STYLE_SOURCE_ID,
    query = FindAssetsQuery(page = 0, perPage = 100),
).assets.mapNotNull { asset ->
    asset.meta?.get("thumbUri")?.let { asset.id to it }
}.toMap()

/** The backdrop video URI for a style, read from its [STYLE_SOURCE_ID] asset (null if absent). */
suspend fun Engine.styleBackgroundVideoUri(assetId: String): String? =
    asset.fetchAsset(sourceId = STYLE_SOURCE_ID, assetId = assetId)?.meta?.get("uri")
