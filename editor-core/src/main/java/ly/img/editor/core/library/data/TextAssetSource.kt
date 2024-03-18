package ly.img.editor.core.library.data

import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.SizeMode
import kotlin.math.ceil

private const val FONT_BASE_PATH = "/extensions/ly.img.cesdk.fonts"

/**
 * A custom asset source that applies different font weight and size when applied to a text block. Note that this source is used in
 * [ly.img.editor.core.library.LibraryCategory.Text] and [ly.img.editor.core.library.LibraryContent.Text]. Therefore, if you are
 * using the category or the content, the asset source should be loaded to the [ly.img.engine.Engine] in the onCreate callback of
 * [ly.img.editor.EngineConfiguration].
 * @param engine the engine that is used in the editor.
 */
class TextAssetSource(private val engine: Engine) : AssetSource(sourceId = AssetSourceType.Text.sourceId) {
    private val fontWeightPathMap =
        mapOf(
            700 to "fonts/Roboto/Roboto-Bold.ttf",
            500 to "fonts/Roboto/Roboto-Medium.ttf",
            400 to "fonts/Roboto/Roboto-Regular.ttf",
        )

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

    override suspend fun applyAsset(asset: Asset): DesignBlock? {
        val textBlock = engine.asset.defaultApplyAsset(asset) ?: return null
        val fontSize = requireNotNull(asset.meta?.get("fontSize")).toFloat() * (50.0f / 24.0f)
        engine.block.setString(textBlock, "text/text", "Text")
        engine.block.setFloat(textBlock, "text/fontSize", fontSize)
        engine.block.setEnum(textBlock, "text/horizontalAlignment", "Center")
        engine.block.setHeightMode(textBlock, SizeMode.AUTO)
        return textBlock
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
                    "blockType" to DesignBlockType.Text.key,
                    "uri" to "$FONT_BASE_PATH/${fontWeightPathMap[fontWeight]}",
                ),
        )
}
