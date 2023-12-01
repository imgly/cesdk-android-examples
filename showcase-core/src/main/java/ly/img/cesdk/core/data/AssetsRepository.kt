package ly.img.cesdk.core.data

import android.net.Uri
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.cesdk.core.Environment
import ly.img.cesdk.core.Secrets
import ly.img.cesdk.core.data.font.FontData
import ly.img.cesdk.core.data.font.FontData.Companion.createFontData
import ly.img.cesdk.core.data.font.FontFamilyData
import ly.img.cesdk.core.engine.BASE_PATH
import ly.img.cesdk.core.engine.FONT_BASE_PATH
import ly.img.engine.Engine
import ly.img.engine.addDefaultAssetSources
import ly.img.engine.addDemoAssetSources
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

private const val FONTS_MANIFEST_URL = "$BASE_PATH$FONT_BASE_PATH/manifest.json"

class AssetsRepository {

    private val _fontFamilies = MutableStateFlow<Result<Map<String, FontFamilyData>>?>(null)
    val fontFamilies = _fontFamilies.asStateFlow()

    suspend fun loadAssetSources(engine: Engine) {
        engine.asset.addSource(UnsplashAssetSource(Secrets.unsplashHost))
        engine.asset.addSource(TextAssetSource())
        coroutineScope {
            launch {
                val baseUri = Uri.parse("https://cdn.img.ly/assets/v2")
                engine.addDefaultAssetSources(baseUri = baseUri)
            }
            launch {
                engine.addDemoAssetSources(
                    sceneMode = Environment.sceneMode,
                    withUploadAssetSources = true,
                    baseUri = Uri.parse("https://cdn.img.ly/assets/demo/v2")
                )
            }
        }
    }

    suspend fun loadFonts() {
        withContext(Dispatchers.IO) {
            try {
                val parsedFontFamilies: Map<String, FontFamilyData>
                if (areFontsDownloaded()) {
                    // read fonts from manifest file
                    val jsonAssets = JSONArray(getManifestFile().inputStream().bufferedReader().use { it.readText() })
                    parsedFontFamilies = parseFontsJson(jsonAssets)
                } else {
                    val response = getResponseAsInputStream(FONTS_MANIFEST_URL).bufferedReader().use { it.readText() }
                    val jsonAssets = JSONObject(response).getJSONArray("assets").getJSONObject(0).getJSONArray("assets")

                    // delete if already exists
                    getFontsDir().deleteRecursively()
                    getFontsDir().mkdir()

                    // write manifest to file
                    FileWriter(getManifestFile().also {
                        it.createNewFile()
                    }).use {
                        it.write(jsonAssets.toString())
                    }

                    parsedFontFamilies = parseFontsJson(jsonAssets)
                    downloadFonts(parsedFontFamilies)
                    setFontsDownloaded()
                }
                _fontFamilies.update { Result.success(parsedFontFamilies) }
            } catch (ex: Exception) {
                _fontFamilies.update { Result.failure(ex) }
            }
        }
    }

    private fun parseFontsJson(json: JSONArray): Map<String, FontFamilyData> {
        val fontFamilyMap = hashMapOf<String, ArrayList<FontData>>()
        for (i in 0 until json.length()) {
            val fontData = createFontData(json.getJSONObject(i))
            val fontFamily = fontFamilyMap.getOrPut(fontData.fontFamily) { ArrayList() }
            fontFamily.add(fontData)
        }
        return fontFamilyMap.mapValues {
            FontFamilyData(name = it.key, fonts = it.value)
        }.toSortedMap()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun downloadFonts(map: Map<String, FontFamilyData>) {
        val fontList = map.values.flatMap { it.displayFontsData.toList() }
        val limitedParallelismDispatcher = Dispatchers.IO.limitedParallelism(8)
        coroutineScope {
            val jobs = fontList.map {
                launch(limitedParallelismDispatcher) {
                    getResponseAsInputStream("$BASE_PATH$FONT_BASE_PATH/${it.fontPath}").use { input ->
                        File(getFontsDir(), it.fontPath).also {
                            it.parentFile?.mkdirs()
                        }.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
            jobs.joinAll()
        }
    }

    private fun getResponseAsInputStream(url: String): InputStream {
        val connection = URL(url).openConnection() as HttpURLConnection
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return connection.inputStream
        } else {
            throw IOException()
        }
    }

    private fun areFontsDownloaded(): Boolean {
        return Environment.getPreferences().getBoolean("fonts_downloaded", false) && getManifestFile().exists()
    }

    private fun setFontsDownloaded() {
        Environment.getPreferences().edit { putBoolean("fonts_downloaded", true) }
    }

    private fun getManifestFile(): File = File(getFontsDir(), "manifest.json")
}

fun getFontsDir(): File = File(Environment.getFilesDir(), "cesdk_fonts")