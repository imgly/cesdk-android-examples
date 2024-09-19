package ly.img.editor.core.library.data

import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetPayload
import ly.img.engine.AssetSource
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.FontWeight
import ly.img.engine.SizeMode
import ly.img.engine.Typeface
import kotlin.math.ceil

/**
 * A custom asset source that applies different font weight and size when applied to a text block. Note that this source is used in
 * [ly.img.editor.core.library.LibraryCategory.Text] and [ly.img.editor.core.library.LibraryContent.Text]. Therefore, if you are
 * using the category or the content, the asset source should be loaded to the [ly.img.engine.Engine] in the onCreate callback of
 * [ly.img.editor.EngineConfiguration].
 *
 * @param engine the engine that is used in the editor.
 * @param typeface the typeface that should be used when applying text asset. You can find typefaces using the
 * [ly.img.engine.DefaultAssetSource.TYPEFACE] asset source that is added through [ly.img.engine.addDefaultAssetSources]. Note
 * that there is a convenience class [TypefaceProvider] which does that.
 */
class TextAssetSource(
    private val engine: Engine,
    private val typeface: Typeface,
) : AssetSource(sourceId = AssetSourceType.Text.sourceId) {
    private val assets =
        listOf(
            createAsset(
                id = "title",
                label = "Title",
                fontWeight = FontWeight.BOLD,
                fontSize = 32,
                fontScale = 4F,
            ),
            createAsset(
                id = "headline",
                label = "Headline",
                fontWeight = FontWeight.MEDIUM,
                fontSize = 18,
                fontScale = 2.8F,
            ),
            createAsset(
                id = "body",
                label = "Body",
                fontWeight = FontWeight.NORMAL,
                fontSize = 14,
                fontScale = 2F,
            ),
        )

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        val filteredAssets = assets.filter { it.id.contains(query.query ?: "", ignoreCase = true) }
        val totalPages = ceil(filteredAssets.size.toDouble() / query.perPage).toInt()
        return FindAssetsResult(
            assets =
                filteredAssets
                    .subList(
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
        engine.block.setString(textBlock, "text/text", asset.label ?: "Text")
        engine.block.setEnum(textBlock, "text/horizontalAlignment", "Center")
        engine.block.setHeightMode(textBlock, SizeMode.AUTO)
        engine.block.setWidthMode(textBlock, SizeMode.ABSOLUTE)
        engine.block.setBoolean(textBlock, "text/clipLinesOutsideOfFrame", false)
        return textBlock
    }

    override suspend fun getGroups(): List<String>? = null

    private fun createAsset(
        id: String,
        label: String,
        fontWeight: FontWeight,
        fontSize: Int,
        fontScale: Float,
    ): Asset {
        val fontUri =
            typeface
                .fonts
                .filter { it.weight == fontWeight }
                .minByOrNull { it.style }
                ?.uri
        requireNotNull(fontUri) {
            "TextAssetSource.defaultTypeface must have support for ${fontWeight.name} font weight."
        }
        return Asset(
            id = id,
            context = AssetContext(sourceId),
            label = label,
            locale = "en",
            meta =
                mapOf(
                    "uri" to fontUri.toString(),
                    "fontFamily" to typeface.name,
                    "fontWeight" to fontWeight.value.toString(),
                    "fontSize" to fontSize.toString(),
                    "fontScale" to fontScale.toString(),
                    "blockType" to DesignBlockType.Text.key,
                ),
            payload = AssetPayload(typeface = typeface),
        )
    }
}
