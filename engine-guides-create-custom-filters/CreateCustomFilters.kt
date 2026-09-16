import android.net.Uri
import ly.img.editor.defaultBaseUri
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

data class CreateCustomFilters(
    val customSourceId: String,
    val jsonSourceId: String,
    val customFilterCount: Int,
    val warmToneFilterCount: Int,
    val jsonFilterCount: Int,
    val appliedFilterLabel: String,
    val appliedLutUri: String,
    val appliedThumbnailUri: String,
    val jsonThumbnailUri: String,
    val horizontalTileCount: Int,
    val verticalTileCount: Int,
    val intensity: Float,
    val exportedImage: ByteBuffer,
)

private const val CUSTOM_FILTER_SOURCE_ID = "my-custom-filters"
private const val JSON_FILTER_SOURCE_ID = "my-json-filters"

suspend fun createCustomFilters(
    engine: Engine,
    assetBaseUri: Uri = defaultBaseUri,
): CreateCustomFilters {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 50F)
    engine.block.setPositionY(imageBlock, value = 50F)
    engine.block.setWidth(imageBlock, value = 300F)
    engine.block.setHeight(imageBlock, value = 225F)
    engine.block.appendChild(parent = page, child = imageBlock)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)

    val warmLutUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.filter.lut")
        .appendPath("LUTs")
        .appendPath("imgly_lut_ad1920_5_5_128.png")
        .build()
        .toString()
    val warmThumbnailUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.filter")
        .appendPath("thumbnails")
        .appendPath("imgly_lut_ad1920.jpg")
        .build()
        .toString()
    val monochromeLutUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.filter.lut")
        .appendPath("LUTs")
        .appendPath("imgly_lut_bw_5_5_128.png")
        .build()
        .toString()
    val monochromeThumbnailUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.filter")
        .appendPath("thumbnails")
        .appendPath("imgly_lut_bw.jpg")
        .build()
        .toString()

    // highlight-android-filter-metadata
    val customFilters = listOf(
        Asset(
            id = "vintage-warm",
            context = AssetContext(sourceId = CUSTOM_FILTER_SOURCE_ID),
            label = "Vintage Warm",
            locale = "en",
            tags = listOf("vintage", "warm", "retro"),
            groups = listOf("Warm Tones"),
            meta = mapOf(
                "uri" to warmLutUri,
                "thumbUri" to warmThumbnailUri,
                "horizontalTileCount" to "5",
                "verticalTileCount" to "5",
                "blockType" to EffectType.LutFilter.key,
            ),
        ),
        Asset(
            id = "cool-cinema",
            context = AssetContext(sourceId = CUSTOM_FILTER_SOURCE_ID),
            label = "Cool Cinema",
            locale = "en",
            tags = listOf("cinema", "cool", "film"),
            groups = listOf("Cool Tones"),
            meta = mapOf(
                "uri" to monochromeLutUri,
                "thumbUri" to monochromeThumbnailUri,
                "horizontalTileCount" to "5",
                "verticalTileCount" to "5",
                "blockType" to EffectType.LutFilter.key,
            ),
        ),
        Asset(
            id = "bw-classic",
            context = AssetContext(sourceId = CUSTOM_FILTER_SOURCE_ID),
            label = "B&W Classic",
            locale = "en",
            tags = listOf("black and white", "classic", "monochrome"),
            groups = listOf("Monochrome"),
            meta = mapOf(
                "uri" to monochromeLutUri,
                "thumbUri" to monochromeThumbnailUri,
                "horizontalTileCount" to "5",
                "verticalTileCount" to "5",
                "blockType" to EffectType.LutFilter.key,
            ),
        ),
    )
    // highlight-android-filter-metadata

    // highlight-android-register-source
    val customSource = CustomFilterAssetSource(
        sourceId = CUSTOM_FILTER_SOURCE_ID,
        filters = customFilters,
    )

    if (customSource.sourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = customSource.sourceId)
    }
    engine.asset.addSource(source = customSource)
    // highlight-android-register-source

    // highlight-android-load-json
    if (JSON_FILTER_SOURCE_ID in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = JSON_FILTER_SOURCE_ID)
    }

    val loadedJsonSourceId = engine.asset.addLocalSourceFromJSON(
        contentJSON = """
            {
              "version": "2.0.0",
              "id": "$JSON_FILTER_SOURCE_ID",
              "assets": [
                {
                  "id": "sunset-glow",
                  "label": { "en": "Sunset Glow" },
                  "tags": { "en": ["warm", "sunset", "golden"] },
                  "groups": ["Warm Tones"],
                  "meta": {
                    "uri": "$warmLutUri",
                    "thumbUri": "$warmThumbnailUri",
                    "horizontalTileCount": "5",
                    "verticalTileCount": "5",
                    "blockType": "${EffectType.LutFilter.key}"
                  }
                },
                {
                  "id": "ocean-breeze",
                  "label": { "en": "Ocean Breeze" },
                  "tags": { "en": ["cool", "blue", "ocean"] },
                  "groups": ["Cool Tones"],
                  "meta": {
                    "uri": "$monochromeLutUri",
                    "thumbUri": "$monochromeThumbnailUri",
                    "horizontalTileCount": "5",
                    "verticalTileCount": "5",
                    "blockType": "${EffectType.LutFilter.key}"
                  }
                }
              ]
            }
        """.trimIndent(),
    )
    // highlight-android-load-json
    check(loadedJsonSourceId == JSON_FILTER_SOURCE_ID)

    // highlight-android-query-filters
    val customFilterResults = engine.asset.findAssets(
        sourceId = CUSTOM_FILTER_SOURCE_ID,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )

    val warmToneFilters = engine.asset.findAssets(
        sourceId = CUSTOM_FILTER_SOURCE_ID,
        query = FindAssetsQuery(page = 0, perPage = 10, groups = listOf("Warm Tones")),
    )

    val jsonFilterResults = engine.asset.findAssets(
        sourceId = JSON_FILTER_SOURCE_ID,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    // highlight-android-query-filters
    check(customFilterResults.total == customFilters.size)
    check(warmToneFilters.assets.map { it.id } == listOf("vintage-warm"))
    check(jsonFilterResults.total == 2)

    // highlight-android-apply-filter
    val filterAsset = warmToneFilters.assets.first()
    val filterMeta = filterAsset.meta ?: error("Filter asset ${filterAsset.id} is missing metadata.")

    require(engine.block.supportsEffects(imageBlock)) {
        "The selected block must support effects before applying a LUT filter."
    }

    val lutEffect = engine.block.createEffect(type = EffectType.LutFilter)
    engine.block.setString(
        block = lutEffect,
        property = "effect/lut_filter/lutFileURI",
        value = filterMeta["uri"] ?: error("Filter asset ${filterAsset.id} is missing meta.uri."),
    )
    engine.block.setInt(
        block = lutEffect,
        property = "effect/lut_filter/horizontalTileCount",
        value = filterMeta["horizontalTileCount"]?.toInt()
            ?: error("Filter asset ${filterAsset.id} is missing meta.horizontalTileCount."),
    )
    engine.block.setInt(
        block = lutEffect,
        property = "effect/lut_filter/verticalTileCount",
        value = filterMeta["verticalTileCount"]?.toInt()
            ?: error("Filter asset ${filterAsset.id} is missing meta.verticalTileCount."),
    )
    engine.block.setFloat(
        block = lutEffect,
        property = "effect/lut_filter/intensity",
        value = 0.85F,
    )
    engine.block.appendEffect(block = imageBlock, effectBlock = lutEffect)
    // highlight-android-apply-filter

    val appliedLutUri = engine.block.getString(
        block = lutEffect,
        property = "effect/lut_filter/lutFileURI",
    )
    val appliedThumbnailUri = filterMeta["thumbUri"]
        ?: error("Filter asset ${filterAsset.id} is missing meta.thumbUri.")
    val jsonThumbnailUri = jsonFilterResults.assets.first().meta?.get("thumbUri")
        ?: error("JSON filter asset is missing meta.thumbUri.")
    val horizontalTileCount = engine.block.getInt(
        block = lutEffect,
        property = "effect/lut_filter/horizontalTileCount",
    )
    val verticalTileCount = engine.block.getInt(
        block = lutEffect,
        property = "effect/lut_filter/verticalTileCount",
    )
    val intensity = engine.block.getFloat(
        block = lutEffect,
        property = "effect/lut_filter/intensity",
    )

    // highlight-android-export
    val exportedImage = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return CreateCustomFilters(
        customSourceId = customSource.sourceId,
        jsonSourceId = loadedJsonSourceId,
        customFilterCount = customFilterResults.total,
        warmToneFilterCount = warmToneFilters.total,
        jsonFilterCount = jsonFilterResults.total,
        appliedFilterLabel = filterAsset.label.orEmpty(),
        appliedLutUri = appliedLutUri,
        appliedThumbnailUri = appliedThumbnailUri,
        jsonThumbnailUri = jsonThumbnailUri,
        horizontalTileCount = horizontalTileCount,
        verticalTileCount = verticalTileCount,
        intensity = intensity,
        exportedImage = exportedImage,
    )
}

// highlight-android-custom-source
private class CustomFilterAssetSource(
    sourceId: String,
    private val filters: List<Asset>,
) : AssetSource(sourceId = sourceId) {
    override suspend fun getGroups(): List<String>? = filters.flatMap { it.groups.orEmpty() }.distinct()

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val searchQuery = query.query
        val queryGroups = query.groups.orEmpty()

        val filteredAssets = filters.filter { asset ->
            val matchesQuery =
                searchQuery.isNullOrBlank() ||
                    buildList {
                        asset.label?.let(::add)
                        addAll(asset.tags.orEmpty())
                    }.any { value ->
                        value.contains(searchQuery, ignoreCase = true)
                    }

            val matchesGroups =
                queryGroups.isEmpty() ||
                    asset.groups.orEmpty().any(queryGroups::contains)

            matchesQuery && matchesGroups
        }

        val startIndex = query.page * query.perPage
        val pageAssets = filteredAssets.drop(startIndex).take(query.perPage)
        val nextPage =
            if (startIndex + pageAssets.size < filteredAssets.size) {
                query.page + 1
            } else {
                -1
            }

        return FindAssetsResult(
            assets = pageAssets,
            currentPage = query.page,
            nextPage = nextPage,
            total = filteredAssets.size,
        )
    }
}
// highlight-android-custom-source
