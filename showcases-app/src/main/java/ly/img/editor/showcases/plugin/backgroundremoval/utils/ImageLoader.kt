package ly.img.editor.showcases.plugin.backgroundremoval.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Utilities for loading images from various sources
 */
object ImageLoader {
    /**
     * Loads an InputImage from a URI, handling both local and network sources
     */
    suspend fun loadImageFromUri(
        uri: Uri,
        context: Context,
    ): InputImage = when (uri.scheme) {
        "http", "https" -> {
            withContext(Dispatchers.IO) {
                val bitmap = loadBitmapFromNetworkUrl(uri.toString())
                InputImage.fromBitmap(bitmap, 0)
            }
        }
        "content" -> {
            InputImage.fromFilePath(context, uri)
        }
        "file", null -> {
            InputImage.fromFilePath(context, uri)
        }
        else -> {
            throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }

    /**
     * Loads a Bitmap from a URI, handling both local and network sources
     */
    suspend fun loadBitmapFromUri(
        uri: Uri,
        context: Context,
    ): Bitmap? = when (uri.scheme) {
        "http", "https" -> {
            withContext(Dispatchers.IO) {
                loadBitmapFromNetworkUrl(uri.toString())
            }
        }
        "content" -> {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                Log.e("BackgroundRemovalPlugin", "Failed to load bitmap from content URI", e)
                null
            }
        }
        "file", null -> {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                } ?: BitmapFactory.decodeFile(uri.path)
            } catch (e: Exception) {
                Log.e("BackgroundRemovalPlugin", "Failed to load bitmap from content URI", e)
                null
            }
        }
        else -> {
            null
        }
    }

    /**
     * Downloads and decodes a bitmap from a network URL
     */
    private suspend fun loadBitmapFromNetworkUrl(url: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.doInput = true
            connection.connect()
            connection.inputStream.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
                    ?: throw IOException("Failed to decode bitmap from URL: $url")
            }
        } finally {
            connection.disconnect()
        }
    }
}
