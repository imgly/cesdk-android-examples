package ly.img.editor.showcase

import android.net.Uri
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType

data class RemoteAsset(
    val remoteSourceId: String,
    val stringSourceId: String,
    val hostedSourceId: String,
    val remoteAssetLoaded: Boolean,
    val stringAssetCount: Int,
    val appliedBlock: DesignBlock?,
    val appliedBlockType: String?,
    val registeredSources: List<String>,
    val hostedSourceRemoved: Boolean,
    val invalidJsonRejected: Boolean,
)

suspend fun remoteAsset(
    engine: Engine,
    assetBaseUri: Uri,
): RemoteAsset {
    listOf("my.remote.images.uri", "my.remote.images", "my.remote.images.hosted").forEach { sourceId ->
        if (sourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId = sourceId)
        }
    }

    // highlight-android-setup
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-setup

    // highlight-android-remote-uris
    val remoteAssetsBaseUri = assetBaseUri
    val imageManifestUri = remoteAssetsBaseUri.buildUpon()
        .appendPath("my.remote.images.uri")
        .appendPath("content.json")
        .build()

    val sampleImageUri = remoteAssetsBaseUri.buildUpon()
        .appendPath("imgly-assets")
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_1.jpg")
        .build()
    // highlight-android-remote-uris

    // highlight-android-load-from-uri
    val imageSourceId = engine.asset.addLocalSourceFromJSON(
        contentUri = imageManifestUri,
        matcher = listOf("sample_1"),
    )
    // highlight-android-load-from-uri

    // highlight-android-load-from-string
    val manifestJSON = """
        {
          "version": "2.0.0",
          "id": "my.remote.images",
          "assets": [
            {
              "id": "sample_image",
              "label": { "en": "Sample Image" },
              "meta": {
                "uri": "$sampleImageUri",
                "thumbUri": "$sampleImageUri",
                "kind": "image",
                "fillType": "${FillType.Image.key}",
                "mimeType": "${MimeType.JPEG.key}",
                "width": 2500,
                "height": 1667
              }
            }
          ]
        }
    """.trimIndent()
    val stringSourceId = engine.asset.addLocalSourceFromJSON(
        contentJSON = manifestJSON,
        basePath = null,
        matcher = null,
    )
    // highlight-android-load-from-string

    // highlight-android-base-path
    val hostedManifestJSON = """
        {
          "version": "2.0.0",
          "id": "my.remote.images.hosted",
          "assets": [
            {
              "id": "hosted_sample_image",
              "label": { "en": "Hosted Sample Image" },
              "meta": {
                "uri": "{{base_url}}/imgly-assets/ly.img.image/images/sample_1.jpg",
                "thumbUri": "{{base_url}}/imgly-assets/ly.img.image/images/sample_1.jpg",
                "kind": "image",
                "fillType": "${FillType.Image.key}",
                "mimeType": "${MimeType.JPEG.key}",
                "width": 2500,
                "height": 1667
              }
            }
          ]
        }
    """.trimIndent()
    val hostedSourceId = engine.asset.addLocalSourceFromJSON(
        contentJSON = hostedManifestJSON,
        basePath = remoteAssetsBaseUri.toString(),
        matcher = null,
    )
    // highlight-android-base-path

    // highlight-android-verify-assets
    val remoteAssets = engine.asset.findAssets(
        sourceId = imageSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    val remoteAsset = engine.asset.fetchAsset(
        sourceId = imageSourceId,
        assetId = "sample_1",
    )
    val stringAssets = engine.asset.findAssets(
        sourceId = stringSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    // highlight-android-verify-assets
    check(remoteAssets.total > 0)

    // highlight-android-apply-asset
    val hostedAssets = engine.asset.findAssets(
        sourceId = hostedSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    val appliedBlock = hostedAssets.assets.firstOrNull()?.let { asset ->
        engine.asset.defaultApplyAsset(asset = asset)
    }
    // highlight-android-apply-asset

    // highlight-android-list-sources
    val registeredSources = engine.asset.findAllSources()
    // highlight-android-list-sources

    // highlight-android-remove-source
    engine.asset.removeSource(sourceId = hostedSourceId)
    // highlight-android-remove-source

    // highlight-android-error-handling
    val invalidJsonRejected = try {
        engine.asset.addLocalSourceFromJSON(
            contentJSON = "{ not valid json }",
            basePath = null,
            matcher = null,
        )
        false
    } catch (_: Exception) {
        true
    }
    // highlight-android-error-handling

    return RemoteAsset(
        remoteSourceId = imageSourceId,
        stringSourceId = stringSourceId,
        hostedSourceId = hostedSourceId,
        remoteAssetLoaded = remoteAsset != null,
        stringAssetCount = stringAssets.assets.size,
        appliedBlock = appliedBlock,
        appliedBlockType = appliedBlock?.let(engine.block::getType),
        registeredSources = registeredSources,
        hostedSourceRemoved = hostedSourceId !in engine.asset.findAllSources(),
        invalidJsonRejected = invalidJsonRejected,
    )
}
