package ly.img.cesdk.core.data

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import ly.img.engine.AssetCredits
import ly.img.engine.AssetLicense
import ly.img.engine.AssetSource
import ly.img.engine.AssetUTM
import ly.img.engine.FindAssetsQuery
import ly.img.engine.FindAssetsResult
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

internal class UnsplashAssetSource(private val baseUrl: String) :
    AssetSource(sourceId = ly.img.cesdk.core.data.AssetSource.Unsplash.sourceId) {

    override suspend fun getGroups(): List<String>? = null

    override val supportedMimeTypes = listOf("image/jpeg")

    override val credits = AssetCredits(
        name = "Unsplash",
        uri = Uri.parse("https://unsplash.com/")
    )

    override val license = AssetLicense(
        name = "Unsplash license (free)",
        uri = Uri.parse("https://unsplash.com/license")
    )

    override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
        return if (query.query.isNullOrEmpty()) query.getPopularList() else query.getSearchList()
    }

    private suspend fun FindAssetsQuery.getPopularList(): FindAssetsResult {
        val queryParams = listOf(
            "order_by" to "popular",
            "page" to page + 1,
            "per_page" to perPage
        ).joinToString(separator = "&") { (key, value) -> "$key=$value" }
        val assetsArray = getResponseAsString("$baseUrl/photos?$queryParams").let(::JSONArray)
        return FindAssetsResult(
            assets = (0 until assetsArray.length()).map { assetsArray.getJSONObject(it).toAsset() },
            currentPage = page,
            nextPage = page + 1,
            total = Int.MAX_VALUE
        )
    }

    private suspend fun FindAssetsQuery.getSearchList(): FindAssetsResult {
        val queryParams = listOf(
            "query" to query,
            "page" to page + 1,
            "per_page" to perPage,
            "content_filter" to "high",
        ).joinToString(separator = "&") { (key, value) -> "$key=$value" }
        val response = getResponseAsString("$baseUrl/search/photos?$queryParams").let(::JSONObject)
        val assetsArray = response.getJSONArray("results")
        val total = response.getInt("total")
        val totalPages = response.getInt("total_pages")
        return FindAssetsResult(
            assets = (0 until assetsArray.length()).map { assetsArray.getJSONObject(it).toAsset() },
            currentPage = page,
            nextPage = if (page == totalPages) -1 else page + 1,
            total = total
        )
    }

    private suspend fun getResponseAsString(url: String): String = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        require(connection.responseCode in 200 until 300) {
            connection.errorStream.bufferedReader().use { it.readText() }
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun JSONObject.toAsset() = Asset(
        id = getString("id"),
        locale = "en",
        label = when {
            !isNull("description") -> getString("description")
            !isNull("alt_description") -> getString("alt_description")
            else -> null
        },
        tags = takeIf { has("tags") }?.let { getJSONArray("tags") }?.let {
            (0 until it.length()).map { index -> it.getJSONObject(index).getString("title") }
        }?.takeIf { it.isNotEmpty() },
        meta = mapOf(
            "uri" to getJSONObject("urls").getString("full"),
            "thumbUri" to getJSONObject("urls").getString("thumb"),
            "kind" to "image",
            "fillType" to "//ly.img.ubq/fill/image",
            "width" to getInt("width").toString(),
            "height" to getInt("height").toString()
        ),
        context = AssetContext(sourceId = "unsplash"),
        credits = AssetCredits(
            name = getJSONObject("user").getString("name"),
            uri = getJSONObject("user")
                .takeIf { it.has("links") }
                ?.getJSONObject("links")
                ?.getString("html")
                ?.let { Uri.parse(it) }
        ),
        utm = AssetUTM(source = "CE.SDK Demo", medium = "referral")
    )
}