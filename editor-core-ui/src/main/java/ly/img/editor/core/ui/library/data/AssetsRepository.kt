package ly.img.editor.core.ui.library.data

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
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.engine.FONT_BASE_PATH
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.editor.core.ui.library.data.font.FontData.Companion.createFontData
import ly.img.editor.core.ui.library.data.font.FontFamilyData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AssetsRepository {
    private val _fontFamilies = MutableStateFlow<Map<String, FontFamilyData>?>(null)
    val fontFamilies = _fontFamilies.asStateFlow()

    suspend fun loadFonts(basePath: String) =
        withContext(Dispatchers.IO) {
            val parsedFontFamilies: Map<String, FontFamilyData>
            if (areFontsDownloaded()) {
                // read fonts from manifest file
                val jsonAssets = JSONArray(getManifestFile().inputStream().bufferedReader().use { it.readText() })
                parsedFontFamilies = parseFontsJson(jsonAssets)
            } else {
                val fontsManifestUrl = "$basePath$FONT_BASE_PATH/manifest.json"
                val response = getResponseAsInputStream(fontsManifestUrl).bufferedReader().use { it.readText() }
                val jsonAssets = JSONObject(response).getJSONArray("assets").getJSONObject(0).getJSONArray("assets")

                // delete if already exists
                getFontsDir().deleteRecursively()
                getFontsDir().mkdir()

                // write manifest to file
                FileWriter(
                    getManifestFile().also {
                        it.createNewFile()
                    },
                ).use {
                    it.write(jsonAssets.toString())
                }

                parsedFontFamilies = parseFontsJson(jsonAssets)
                downloadFonts(basePath, parsedFontFamilies)
                setFontsDownloaded()
            }
            _fontFamilies.update { parsedFontFamilies }
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
    private suspend fun downloadFonts(
        basePath: String,
        map: Map<String, FontFamilyData>,
    ) {
        val fontList = map.values.flatMap { it.displayFontsData.toList() }
        val limitedParallelismDispatcher = Dispatchers.IO.limitedParallelism(8)
        coroutineScope {
            val jobs =
                fontList.map {
                    launch(limitedParallelismDispatcher) {
                        getResponseAsInputStream("$basePath$FONT_BASE_PATH/${it.fontPath}").use { input ->
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

fun getFontsDir(): File = File(Environment.getFilesDir(), "editor_fonts")
