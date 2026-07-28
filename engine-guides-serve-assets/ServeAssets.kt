import android.net.Uri
import ly.img.engine.Engine

// highlight-android-default-source-ids
private val serveAssetsDefaultSourceIds =
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
        "ly.img.caption.presets",
    )
// highlight-android-default-source-ids

// highlight-android-sample-source-ids
private val serveAssetsSampleSourceIds =
    listOf(
        "ly.img.image",
        "ly.img.video",
        "ly.img.audio",
        "ly.img.templates",
        "ly.img.templates.premium",
    )
// highlight-android-sample-source-ids

data class ServeAssetsResult(
    val defaultSourceIds: List<String>,
    val sampleSourceIds: List<String>,
    val basePath: String,
)

// highlight-android-register-sources
private suspend fun registerServeAssetSources(
    engine: Engine,
    baseUri: Uri,
    sourceIds: List<String>,
    matchersBySourceId: Map<String, List<String>> = emptyMap(),
): List<String> {
    val existingSourceIds = engine.asset.findAllSources().toSet()
    val registeredSourceIds = mutableListOf<String>()

    sourceIds
        .filterNot(existingSourceIds::contains)
        .forEach { sourceId ->
            registeredSourceIds +=
                engine.asset.addLocalSourceFromJSON(
                    contentUri = baseUri.buildUpon()
                        .appendPath(sourceId)
                        .appendPath("content.json")
                        .build(),
                    matcher = matchersBySourceId[sourceId],
                )
        }

    return registeredSourceIds
}
// highlight-android-register-sources

suspend fun serveAssets(
    engine: Engine,
    baseUri: Uri,
): ServeAssetsResult {
    registerServeAssetSources(
        engine = engine,
        baseUri = baseUri,
        sourceIds = serveAssetsDefaultSourceIds,
    )

    registerServeAssetSources(
        engine = engine,
        baseUri = baseUri,
        sourceIds = serveAssetsSampleSourceIds,
    )

    // highlight-android-engine-level-assets
    engine.editor.setSettingString(
        keypath = "basePath",
        value = baseUri.toString(),
    )
    // highlight-android-engine-level-assets

    val sourceIds = engine.asset.findAllSources()
    return ServeAssetsResult(
        defaultSourceIds = serveAssetsDefaultSourceIds.filter(sourceIds::contains),
        sampleSourceIds = serveAssetsSampleSourceIds.filter(sourceIds::contains),
        basePath = engine.editor.getSettingString(keypath = "basePath"),
    )
}

fun serveAssetsRemoteBaseUri(): Uri {
    // highlight-android-remote-base-uri
    return Uri.parse("https://cdn.your.custom.domain/assets")
    // highlight-android-remote-base-uri
}

fun serveAssetsAppAssetsBaseUri(): Uri {
    // highlight-android-app-assets-base-uri
    return Uri.parse("file:///android_asset/assets")
    // highlight-android-app-assets-base-uri
}

suspend fun serveAssetsWithFilteredStickers(
    engine: Engine,
    baseUri: Uri,
) {
    // highlight-android-filtered-source
    registerServeAssetSources(
        engine = engine,
        baseUri = baseUri,
        sourceIds = listOf("ly.img.sticker"),
        matchersBySourceId =
            mapOf(
                "ly.img.sticker" to listOf("ly.img.sticker.emoji.*"),
            ),
    )
    // highlight-android-filtered-source
}
