import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import ly.img.engine.Asset
import ly.img.engine.AssetColor
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetLicense
import ly.img.engine.AssetPayload
import ly.img.engine.AssetSource
import ly.img.engine.AssetUTM
import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import ly.img.engine.Source
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private const val assetsPath = "/assets"

suspend fun Engine.addRemoteAssetSources(
    host: String,
    paths: Set<RemoteAssetSource.Path>,
) = coroutineScope {
    paths.map { async { RemoteAssetSource(engine = this@addRemoteAssetSources, host = host, path = it).create() } }
        .awaitAll().forEach(asset::addSource)
}

class RemoteAssetSource(
    private val engine: Engine,
    private val host: String,
    private val path: Path,
) {
    enum class Path(
        val pathString: String,
    ) {
        ImagePexels("/api/assets/v1/image-pexels"),
        ImageUnsplash("/api/assets/v1/image-unsplash"),
        VideoPexels("/api/assets/v1/video-pexels"),
        VideoGiphy("/api/assets/v1/video-giphy"),
        VideoGiphySicker("/api/assets/v1/video-giphy-sticker"),
    }

    suspend fun create(): AssetSource {
        val manifestData = withContext(Dispatchers.IO) {
            val manifestString = getResponseAsString("$host${path.pathString}")
            JSONObject(manifestString).getJSONObject("data").toData()
        }
        return object : AssetSource(manifestData.id) {
            override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult = withContext(Dispatchers.IO) {
                val queryParams = listOf(
                    "query" to query.query,
                    "page" to query.page,
                    "per_page" to query.perPage,
                    "locale" to query.locale,
                    "tags" to query.tags?.joinToString(","),
                    "groups" to query.groups?.joinToString(","),
                    "excludeGroups" to query.excludeGroups?.joinToString(","),
                ).filter { it.second != null }.joinToString(separator = "&") { (key, value) -> "$key=$value" }
                val response = getResponseAsString("$host${path.pathString}$assetsPath?$queryParams").let(::JSONObject)
                response.toFindAssetsResult(manifestData)
            }

            override suspend fun getGroups() = null

            override suspend fun applyAsset(asset: Asset): DesignBlock? {
                val block = engine.asset.defaultApplyAsset(asset) ?: return null
                engine.block.ensureAssetDuration(block, asset)
                engine.block.ensureMetadataKeys(block, asset, sourceId)
                return block
            }

            override suspend fun applyAsset(
                asset: Asset,
                block: DesignBlock,
            ) {
                engine.asset.defaultApplyAsset(asset, block)
                engine.block.ensureMetadataKeys(block, asset, sourceId)
            }

            override val supportedMimeTypes: List<String> = manifestData.supportedMimeTypes

            override val credits: AssetCredits? = manifestData.credits

            override val license: AssetLicense? = manifestData.license
        }
    }

    private fun BlockApi.ensureMetadataKeys(
        designBlock: DesignBlock,
        asset: Asset,
        sourceId: String,
    ) {
        setMetadata(designBlock, "source/id", sourceId)
        setMetadata(designBlock, "source/externalId", asset.id)
    }

    private suspend fun BlockApi.ensureAssetDuration(
        designBlock: DesignBlock,
        asset: Asset,
    ) {
        if (asset.meta?.get("duration") != null || !hasFill(designBlock)) return
        val fill = getFill(designBlock)
        if (FillType.getOrNull(getType(fill)) != FillType.Video) return
        forceLoadAVResource(fill)
        val duration = getAVResourceTotalDuration(fill)
        setDuration(designBlock, duration)
    }

    private suspend fun getResponseAsString(url: String) = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        require(connection.responseCode in 200 until 300) {
            connection.errorStream.bufferedReader().use { it.readText() }
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    }

    private data class Data(
        val id: String,
        val name: Map<String, String>? = null,
        val canGetGroups: Boolean = false,
        val credits: AssetCredits? = null,
        val license: AssetLicense? = null,
        val canAddAsset: Boolean = false,
        val canRemoveAsset: Boolean = false,
        val supportedMimeTypes: List<String> = emptyList(),
    )

    private fun JSONObject.toData() = Data(
        id = getString("id"),
        name = optJSONObject("name")?.toMapping()?.takeIf { it.isNotEmpty() },
        canGetGroups = optBoolean("canGetGroups"),
        credits = optJSONObject("credits")?.toCredits(),
        license = optJSONObject("license")?.toLicense(),
        canAddAsset = optBoolean("canAddAsset"),
        canRemoveAsset = optBoolean("canRemoveAsset"),
        supportedMimeTypes = optJSONArray("supportedMimeTypes")?.toList() ?: emptyList(),
    )

    private fun JSONObject.toMapping(): Map<String, String> {
        val mapping = mutableMapOf<String, String>()
        for (key in keys()) {
            mapping[key] = getString(key)
        }
        return mapping
    }

    private fun JSONArray.toList(): List<String> {
        val mapping = mutableListOf<String>()
        for (i in 0 until length()) {
            mapping.add(getString(i))
        }
        return mapping
    }

    private fun JSONObject.toCredits() = AssetCredits(
        name = getString("name"),
        uri = (opt("url") as? String)?.let(Uri::parse),
    )

    private fun JSONObject.toLicense() = AssetLicense(
        name = getString("name"),
        uri = (opt("url") as? String)?.let(Uri::parse),
    )

    private fun JSONObject.toFindAssetsResult(manifestData: Data): FindAssetsResult {
        val responseData = getJSONObject("data")
        val assetsArray = responseData.getJSONArray("assets")
        return FindAssetsResult(
            assets = (0 until assetsArray.length()).map { assetsArray.getJSONObject(it).toAsset(manifestData) },
            currentPage = responseData.getInt("currentPage"),
            nextPage = responseData.optInt("nextPage", -1),
            total = responseData.getInt("total"),
        )
    }

    private fun JSONObject.toAsset(manifestData: Data) = Asset(
        id = getString("id"),
        context = AssetContext(manifestData.id),
        label = optString("label"),
        locale = optString("locale"),
        tags = optJSONArray("tags")?.toList()?.takeIf { it.isNotEmpty() },
        groups = optJSONArray("groups")?.toList()?.takeIf { it.isNotEmpty() },
        meta = optJSONObject("meta")?.toMapping()?.takeIf { it.isNotEmpty() },
        payload = optJSONObject("payload")?.toPayload() ?: AssetPayload(),
        credits = optJSONObject("credits")?.toCredits(),
        license = optJSONObject("license")?.toLicense(),
        utm = optJSONObject("utm")?.toUtm(),
    )

    private fun JSONObject.toPayload() = AssetPayload(
        color = optJSONObject("color")?.toColor(),
        sourceSet = optJSONArray("sourceSet")?.run {
            val sourceSet = mutableListOf<Source>()
            (0 until length()).forEach { index ->
                runCatching { getJSONObject(index).toSource() }.getOrNull()?.let {
                    sourceSet.add(it)
                }
            }
            sourceSet
        },
    )

    private fun JSONObject.toColor(): AssetColor? = when (opt("colorSpace")) {
        "sRGB" -> toRGBColor()
        "CMYK" -> toCMYKColor()
        "SpotColor" -> toSpotColor()
        else -> null
    }

    private fun JSONObject.toRGBColor() = AssetColor.RGB(
        r = getDouble("r").toFloat(),
        g = getDouble("g").toFloat(),
        b = getDouble("b").toFloat(),
    )

    private fun JSONObject.toCMYKColor() = AssetColor.CMYK(
        c = getDouble("c").toFloat(),
        m = getDouble("m").toFloat(),
        y = getDouble("y").toFloat(),
        k = getDouble("k").toFloat(),
    )

    private fun JSONObject.toSpotColor() = AssetColor.SpotColor(
        name = getString("name"),
        representation = optJSONObject("representation")?.toColor() as AssetColor.Representation,
        externalReference = opt("externalReference") as? String?,
    )

    private fun JSONObject.toSource() = Source(
        uri = getString("uri").let(Uri::parse),
        width = getInt("width"),
        height = getInt("height"),
    )

    private fun JSONObject.toUtm() = AssetUTM(
        source = opt("source") as? String,
        medium = opt("medium") as? String,
    )
}
