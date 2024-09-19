package ly.img.editor.core.ui.utils

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.AndroidFont
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.library.data.font.FontData
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

val FontData.fontFamily: FontFamily
    @Composable
    get() {
        val font =
            uri.toAssetPathOrNull()?.let {
                Font(
                    path = it,
                    assetManager = LocalContext.current.assets,
                    weight = weight,
                )
            } ?: uri.toFileOrNull()?.let {
                Font(
                    file = it,
                    weight = weight,
                )
            } ?: RemoteFont(uri = uri, weight = weight)
        return FontFamily(font)
    }

class RemoteFont(val uri: Uri, override val weight: FontWeight) : AndroidFont(
    loadingStrategy = FontLoadingStrategy.Async,
    typefaceLoader = RemoteTypefaceLoader(),
    variationSettings = FontVariation.Settings(),
) {
    override val style: FontStyle = FontStyle.Normal
}

class RemoteTypefaceLoader : AndroidFont.TypefaceLoader {
    override suspend fun awaitLoad(
        context: Context,
        font: AndroidFont,
    ): Typeface? {
        if (font !is RemoteFont) return null
        val fontFile = font.fontFile ?: return null
        if (fontFile.exists()) return Typeface.createFromFile(fontFile)
        return withContext(Dispatchers.IO) {
            fontFile.parentFile?.mkdirs()
            val url = URL(font.uri.toString())
            val connection = url.openConnection() as HttpURLConnection
            val responseCode = connection.responseCode
            require(responseCode == HttpURLConnection.HTTP_OK)
            connection.inputStream.use { cis ->
                val tempFile = File(fontFile.absolutePath + ".temp")
                tempFile.outputStream().use { fos ->
                    cis.copyTo(fos)
                }
                tempFile.renameTo(fontFile)
            }
            Typeface.createFromFile(fontFile)
        }
    }

    override fun loadBlocking(
        context: Context,
        font: AndroidFont,
    ): Typeface? = font.fontFile?.let(Typeface::createFromFile)

    private val AndroidFont.fontFile: File?
        get() {
            if (this !is RemoteFont) return null
            if (uri.scheme != "http" && uri.scheme != "https") {
                return null
            }
            return File(Environment.getEditorCacheDir(), requireNotNull(uri.path))
        }
}
