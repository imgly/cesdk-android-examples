package ly.img.editor.showcases.plugin.backgroundremoval.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Utilities for processing images and applying masks
 */
object ImageProcessor {
    /**
     * Applies a segmentation mask to a bitmap to remove the background
     */
    suspend fun applyMaskToBitmap(
        originalBitmap: Bitmap,
        maskBuffer: ByteBuffer,
        maskWidth: Int,
        maskHeight: Int,
    ): Bitmap = withContext(Dispatchers.Default) {
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height
        val resultBitmap = createBitmap(originalWidth, originalHeight)

        val maskConfidences = FloatArray(maskWidth * maskHeight)
        maskBuffer.rewind()

        for (i in 0 until maskWidth * maskHeight) {
            maskConfidences[i] = maskBuffer.float
        }

        val originalPixels = IntArray(originalWidth * originalHeight)
        originalBitmap.getPixels(originalPixels, 0, originalWidth, 0, 0, originalWidth, originalHeight)

        for (y in 0 until originalHeight) {
            for (x in 0 until originalWidth) {
                val pixelIndex = y * originalWidth + x

                val maskX = (x.toFloat() / originalWidth * maskWidth).coerceIn(0f, maskWidth - 1f)
                val maskY = (y.toFloat() / originalHeight * maskHeight).coerceIn(0f, maskHeight - 1f)

                val maskIndex = (maskY.toInt() * maskWidth + maskX.toInt()).coerceIn(0, maskConfidences.size - 1)
                val confidence = maskConfidences[maskIndex]

                val originalPixel = originalPixels[pixelIndex]
                val alpha = (confidence * 255f).toInt().coerceIn(0, 255)
                val newPixel = (originalPixel and 0x00FFFFFF) or (alpha shl 24)
                originalPixels[pixelIndex] = newPixel
            }
        }

        resultBitmap.setPixels(originalPixels, 0, originalWidth, 0, 0, originalWidth, originalHeight)

        resultBitmap
    }

    /**
     * Saves a bitmap to a temporary file and returns the URI.
     * The temporary file will be automatically deleted when the JVM exits.
     */
    suspend fun saveBitmapAsTempFile(
        bitmap: Bitmap,
        context: Context,
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val tempFile = File.createTempFile("background_removed_", ".png", context.cacheDir)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Uri.fromFile(tempFile)
            }
        } catch (e: IOException) {
            Log.e("BackgroundRemovalPlugin", "Failed to save bitmap to temp file", e)
            null
        }
    }
}
