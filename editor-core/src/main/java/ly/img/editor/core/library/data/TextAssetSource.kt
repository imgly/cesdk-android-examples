package ly.img.editor.core.library.data

import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetSource
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import kotlin.math.ceil

/**
 * A custom asset source that applies different font weight and size when applied to a text block. Note that this source is used in
 * [ly.img.editor.core.library.LibraryCategory.Text] and [ly.img.editor.core.library.LibraryContent.Text]. Therefore, if you are
 * using the category or the content, the asset source should be loaded to the [ly.img.engine.Engine] in the onCreate callback of
 * [ly.img.editor.EngineConfiguration].
 */
class TextAssetSource : AssetSource(sourceId = AssetSourceType.Text.sourceId) {
    private val assets =
        listOf(
            createAsset("title", "Title", 700, 32),
            createAsset("headline", "Headline", 500, 18),
            createAsset("body", "Body", 400, 14),
        )

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val filteredAssets = assets.filter { it.id.contains(query.query ?: "", ignoreCase = true) }
        val totalPages = ceil(filteredAssets.size.toDouble() / query.perPage).toInt()
        return FindAssetsResult(
            assets =
                filteredAssets.subList(
                    query.page * query.perPage,
                    filteredAssets.size,
                ).take(query.perPage),
            currentPage = query.page,
            nextPage = if (query.page == totalPages) -1 else query.page + 1,
            total = filteredAssets.size,
        )
    }

    override suspend fun getGroups(): List<String>? = null

    private fun createAsset(
        id: String,
        label: String,
        fontWeight: Int,
        fontSize: Int,
    ): Asset =
        Asset(
            id = id,
            context = AssetContext(sourceId),
            label = label,
            locale = "en",
            meta =
                mapOf(
                    "fontFamily" to "Roboto",
                    "fontWeight" to fontWeight.toString(),
                    "fontSize" to fontSize.toString(),
                ),
        )
}
