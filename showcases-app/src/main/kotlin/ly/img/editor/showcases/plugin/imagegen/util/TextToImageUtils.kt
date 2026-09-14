package ly.img.editor.showcases.plugin.imagegen.util

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.IntSize
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object TextToImageUtils {
    suspend fun getImageSize(
        context: Context,
        imageUri: String,
    ): IntSize? = withContext(Dispatchers.IO) {
        try {
            val uri = imageUri.toUri()

            when (uri.scheme) {
                "content" -> {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeStream(inputStream, null, options)
                        if (options.outWidth > 0 && options.outHeight > 0) {
                            IntSize(options.outWidth, options.outHeight)
                        } else {
                            null
                        }
                    }
                }
                "http", "https" -> {
                    val connection = URL(imageUri).openConnection()
                    connection.doInput = true
                    connection.connect()

                    connection.getInputStream().use { inputStream ->
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeStream(inputStream, null, options)
                        if (options.outWidth > 0 && options.outHeight > 0) {
                            IntSize(options.outWidth, options.outHeight)
                        } else {
                            null
                        }
                    }
                }
                "file" -> {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(uri.path, options)
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        IntSize(options.outWidth, options.outHeight)
                    } else {
                        null
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun calculateSizeWithAspectRatio(
        aspectRatio: Float,
        pageWidth: Float,
        pageHeight: Float,
    ): Pair<Float, Float> {
        val maxWidth = pageWidth * 0.8f
        val maxHeight = pageHeight * 0.8f

        val widthBasedHeight = maxWidth / aspectRatio
        val heightBasedWidth = maxHeight * aspectRatio

        return if (widthBasedHeight <= maxHeight) {
            maxWidth to widthBasedHeight
        } else {
            heightBasedWidth to maxHeight
        }
    }
}
