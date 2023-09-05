package ly.img.cesdk.core.data

import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetSource
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import kotlin.math.ceil

internal class TextAssetSource : AssetSource(ly.img.cesdk.core.data.AssetSource.Text.sourceId) {

    val assets: List<Asset>

    init {
        assets = listOf(
            createAsset("title", "Title", 700, 32),
            createAsset("headline", "Headline", 500, 18),
            createAsset("body", "Body", 400, 14)
        )
    }

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val totalPages = ceil(assets.size.toDouble() / query.perPage).toInt() - 1
        val filteredAssets = assets.filter { it.id.contains(query.query ?: "", ignoreCase = true) }
        return FindAssetsResult(
            assets = filteredAssets,
            currentPage = query.page,
            nextPage = if (query.page == totalPages) -1 else totalPages + 1,
            total = filteredAssets.size
        )
    }

    override suspend fun getGroups(): List<String>? = null

    private fun createAsset(id: String, label: String, fontWeight: Int, fontSize: Int): Asset = Asset(
        id = id,
        context = AssetContext(sourceId),
        label = label,
        locale = "en",
        meta = mapOf(
            "fontFamily" to "Roboto",
            "fontWeight" to fontWeight.toString(),
            "fontSize" to fontSize.toString()
        )
    )
}