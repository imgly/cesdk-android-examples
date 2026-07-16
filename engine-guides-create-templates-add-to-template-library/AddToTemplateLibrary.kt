import android.net.Uri
import ly.img.engine.AssetDefinition
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import ly.img.engine.assetBaseUri

@Suppress("DEPRECATION")
suspend fun addToTemplateLibrary(
    engine: Engine,
    assetBaseUri: Uri = Engine.assetBaseUri,
) {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.setString(headline, property = "text/text", value = "Seasonal Sale")
    engine.block.setPositionX(headline, value = 120F)
    engine.block.setPositionY(headline, value = 180F)
    engine.block.setWidth(headline, value = 840F)
    engine.block.setHeight(headline, value = 140F)
    engine.block.appendChild(parent = page, child = headline)

    // highlight-android-save-string
    val currentScene = engine.scene.get() ?: error("Create or load a scene before saving it.")
    val templateString = engine.scene.saveToString(scene = currentScene)
    // highlight-android-save-string
    check(templateString.isNotBlank())

    // highlight-android-save-archive
    val templateArchive = engine.scene.saveToArchive(scene = currentScene)
    // highlight-android-save-archive
    val savedArchiveBytes = templateArchive.remaining()
    check(savedArchiveBytes > 0)

    // highlight-android-create-source
    val templateSourceId = "my-templates"
    if (templateSourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = templateSourceId)
    }
    engine.asset.addLocalSource(
        sourceId = templateSourceId,
        supportedMimeTypes = emptyList(),
        applyAsset = { asset ->
            val templateUri = asset.meta?.get("uri")?.let(Uri::parse)
                ?: error("Template asset ${asset.id} is missing meta.uri")
            engine.scene.applyTemplate(templateUri = templateUri)
            null
        },
    )
    // highlight-android-create-source

    // highlight-android-add-templates
    val postcardTemplateUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_postcard_1.scene")
        .build()
    val postcardThumbnailUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("thumbnails")
        .appendPath("cesdk_postcard_1.jpg")
        .build()
    val businessCardTemplateUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_business_card_1.scene")
        .build()
    val businessCardThumbnailUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("thumbnails")
        .appendPath("cesdk_business_card_1.jpg")
        .build()
    val templates = listOf(
        AssetDefinition(
            id = "template-postcard",
            label = mapOf("en" to "Postcard"),
            meta = mapOf(
                "uri" to postcardTemplateUri.toString(),
                "thumbUri" to postcardThumbnailUri.toString(),
            ),
        ),
        AssetDefinition(
            id = "template-business-card",
            label = mapOf("en" to "Business Card"),
            meta = mapOf(
                "uri" to businessCardTemplateUri.toString(),
                "thumbUri" to businessCardThumbnailUri.toString(),
            ),
        ),
    )

    templates.forEach { template ->
        engine.asset.addAsset(sourceId = templateSourceId, asset = template)
    }
    engine.asset.assetSourceContentsChanged(sourceId = templateSourceId)
    // highlight-android-add-templates

    // highlight-android-apply-template
    val postcardAsset = engine.asset.fetchAsset(
        sourceId = templateSourceId,
        assetId = "template-postcard",
    ) ?: error("Template asset not found.")
    val createdBlock = engine.asset.applyAssetSourceAsset(
        sourceId = templateSourceId,
        asset = postcardAsset,
    )
    // highlight-android-apply-template
    check(createdBlock == null)
    check(engine.scene.get() != null)
    check(engine.block.findByType(DesignBlockType.Page).isNotEmpty())

    // highlight-android-json-source
    val jsonSourceId = "my-json-templates"
    if (jsonSourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = jsonSourceId)
    }
    val loadedJsonSourceId = engine.asset.addLocalSourceFromJSON(
        contentJSON = """
            {
              "version": "2.0.0",
              "id": "$jsonSourceId",
              "assets": [
                {
                  "id": "template-flyer",
                  "label": { "en": "Flyer" },
                  "meta": {
                    "uri": "$postcardTemplateUri",
                    "thumbUri": "$postcardThumbnailUri"
                  }
                }
              ]
            }
        """.trimIndent(),
        basePath = null,
        matcher = null,
    )
    // highlight-android-json-source
    check(loadedJsonSourceId == jsonSourceId)

    // highlight-android-manage-templates
    val sources = engine.asset.findAllSources()
    check(templateSourceId in sources)

    val queryResult = engine.asset.findAssets(
        sourceId = templateSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )

    engine.asset.removeAsset(sourceId = templateSourceId, assetId = "template-business-card")
    engine.asset.assetSourceContentsChanged(sourceId = templateSourceId)

    val remainingTemplateIds = engine.asset.findAssets(
        sourceId = templateSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    ).assets.map { asset -> asset.id }
    // highlight-android-manage-templates

    check(queryResult.total == templates.size)
    check(remainingTemplateIds == listOf("template-postcard"))
}
