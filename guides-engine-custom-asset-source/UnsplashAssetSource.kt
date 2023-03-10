import android.net.Uri
import kotlinx.coroutines.*
import ly.img.engine.*
import org.json.*
import java.net.HttpURLConnection
import java.net.URL

fun customAssetSource() = CoroutineScope(Dispatchers.Main).launch {
	val engine = Engine.also { it.start() }
	engine.bindOffscreen(width = 100, height = 100)

	// highlight-unsplash-definition
	val source = UnsplashAssetSource()
	engine.asset.addSource(source)
	// highlight-unsplash-definition

	// highlight-unsplash-findAssets
	val list = engine.asset.findAssets(
		sourceId = "ly.img.asset.source.unsplash",
		query = FindAssetsQuery(query = "", page = 1, perPage = 10)
	)
	// highlight-unsplash-findAssets

	// highlight-unsplash-list
	val search = engine.asset.findAssets(
		sourceId = "ly.img.asset.source.unsplash",
		query = FindAssetsQuery(query = "banana", page = 1, perPage = 10)
	)
	// highlight-unsplash-list

	// highlight-add-local-source
	engine.asset.addLocalSource(sourceId = "background-videos", supportedMimeTypes = listOf("video/mp4"))
	// highlight-add-local-source
	// highlight-add-asset-to-source
	val asset = AssetDefinition(
		id = "ocean-waves-1",
		labels = mapOf(
			"en" to "relaxing ocean waves",
			"es" to "olas del mar relajantes"
		),
		tags = mapOf(
			"en" to  listOf("ocean", "waves", "soothing", "slow"),
			"es" to listOf("mar", "olas", "calmante", "lento")
		),
		meta = mapOf(
			"uri" to "https://example.com/ocean-waves-1.mp4",
			"thumbUri" to "https://example.com/thumbnails/ocean-waves-1.jpg",
			"mimeType" to "video/mp4",
			"width" to "1920",
			"height" to "1080"
		)
	)
	engine.asset.addAsset(sourceId = "background-videos", asset = asset)
	// highlight-add-asset-to-source

	engine.stop()
}

// highlight-unsplash-source-creation
class UnsplashAssetSource : AssetSource(sourceId = "ly.img.asset.source.unsplash") {
// highlight-unsplash-source-creation

	override suspend fun getGroups(): List<String>? = null

	override val supportedMimeTypes = listOf("image/jpeg")

	// highlight-unsplash-credits-license
	override val credits = AssetCredits(
		name = "Unsplash",
		uri = Uri.parse("https://unsplash.com/")
	)

	override val license = AssetLicense(
		name = "Unsplash license (free)",
		uri = Uri.parse("https://unsplash.com/license")
	)
	// highlight-unsplash-credits-license

	// highlight-unsplash-query
	override suspend fun findAssets(query: FindAssetsQuery): FindAssetsResult {
		return if (query.query.isNullOrEmpty()) query.getPopularList() else query.getSearchList()
	}

	private suspend fun FindAssetsQuery.getPopularList(): FindAssetsResult {
		val queryParams = listOf(
			"order_by" to "popular",
			"page" to page,
			"perPage" to perPage
		).joinToString(separator = "&") { (key, value) -> "$key=$value" }
		val assetsArray = getResponseAsString("$BASE_URL/photos?$queryParams").let(::JSONArray)
		return FindAssetsResult(
			assets = (0 until assetsArray.length()).map { assetsArray.getJSONObject(it).toAsset() },
			currentPage = page,
			nextPage = page + 1,
			total = 0
		)
	}

	private suspend fun FindAssetsQuery.getSearchList(): FindAssetsResult {
		val queryParams = listOf(
			"query" to query,
			"page" to page,
			"perPage" to perPage
		).joinToString(separator = "&") { (key, value) -> "$key=$value" }
		// highlight-unsplash-result-mapping
		val response = getResponseAsString("$BASE_URL/search/photos?$queryParams").let(::JSONObject)
		val assetsArray = response.getJSONArray("results")
		val total = response.getInt("total")
		val totalPages = response.getInt("total_pages")
		return FindAssetsResult(
			assets = (0 until assetsArray.length()).map { assetsArray.getJSONObject(it).toAsset() },
			currentPage = page,
			nextPage = if (page == totalPages) -1 else page + 1,
			total = total
		)
		// highlight-unsplash-result-mapping
	}
	// highlight-unsplash-query

	private suspend fun getResponseAsString(url: String) = withContext(Dispatchers.IO) {
		val connection = URL(url).openConnection() as HttpURLConnection
		require(connection.responseCode in 200 until 300) {
			connection.errorStream.bufferedReader().use { it.readText() }
		}
		connection.inputStream.bufferedReader().use { it.readText() }
	}

	// highlight-translateToAssetResult
	private fun JSONObject.toAsset() = Asset(
		// highlight-result-id
		id = getString("id"),
		// highlight-result-id
		// highlight-result-locale
		locale = "en",
		// highlight-result-locale
		// highlight-result-label
		label = when {
			has("description") -> getString("description")
			has("alt_description") -> getString("alt_description")
			else -> null
		},
		// highlight-result-label
		// highlight-result-tags
		tags = takeIf { has("tags") }?.let { getJSONArray("tags") }?.let {
			(0 until it.length()).map { index -> it.getJSONObject(index).getString("title") }
		}?.takeIf { it.isNotEmpty() },
		// highlight-result-tags
		// highlight-result-meta
		meta = mapOf(
			// highlight-result-uri
			"uri" to getJSONObject("urls").getString("full"),
			// highlight-result-uri
			// highlight-result-thumbUri
			"thumbUri" to getJSONObject("urls").getString("thumb"),
			// highlight-result-thumbUri
			// highlight-result-blockType
			"blockType" to "//ly.img.ubq/image",
			// highlight-result-blockType
			// highlight-result-size
			"width" to getInt("width").toString(),
			"height" to getInt("height").toString()
			// highlight-result-size
		),
		// highlight-result-meta
		// highlight-result-context
		context = AssetContext(sourceId = "unsplash"),
		// highlight-result-context
		// highlight-result-credits
		credits = AssetCredits(
			name = getJSONObject("user").getString("name"),
			uri = getJSONObject("user")
				.takeIf { it.has("links") }
				?.getJSONObject("links")
				?.getString("html")
				?.let { Uri.parse(it) }
		),
		// highlight-result-credits
		// highlight-result-utm
		utm = AssetUTM(source = "CE.SDK Demo", medium = "referral")
		// highlight-result-utm
	)
	// highlight-translateToAssetResult

	companion object {
		private const val BASE_URL = "https://us-central1-cesdk-ab4f3.cloudfunctions.net/photos"
	}
}