package ly.img.editor.core.ui.library.data

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration.Companion.milliseconds

class MediaMetadataExtractor(private val context: Context) {
    suspend fun getAudioMetadata(uri: Uri): AudioMetadata? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val title =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).takeIf { !it.isNullOrEmpty() }
                        ?: getFileName(uri)
                val durationInSeconds = retriever.getDuration()
                val albumArtUri =
                    retriever.embeddedPicture?.let {
                        val file = File.createTempFile("artwork", null)
                        val outputStream = FileOutputStream(file)
                        outputStream.write(it)
                        outputStream.close()
                        Uri.fromFile(file)
                    }
                AudioMetadata(title, durationInSeconds?.toString(), albumArtUri?.toString())
            } catch (ex: Exception) {
                null
            } finally {
                retriever.release()
            }
        }
    }

    suspend fun getVideoMetadata(uri: Uri): VideoMetadata? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val bitmap = retriever.frameAtTime
                val thumbFile =
                    bitmap?.let {
                        try {
                            val file = File.createTempFile("thumb", null)
                            val outputStream = FileOutputStream(file)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush()
                            outputStream.close()
                            file
                        } catch (e: Exception) {
                            null
                        }
                    } ?: return@withContext null
                val durationInSeconds = retriever.getDuration()?.toString()
                VideoMetadata(
                    duration = durationInSeconds,
                    thumbUri = Uri.fromFile(thumbFile).toString(),
                    width = bitmap.width.toString(),
                    height = bitmap.height.toString(),
                )
            } catch (e: Exception) {
                null
            } finally {
                retriever.release()
            }
        }
    }

    @Suppress("NAME_SHADOWING")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }
}

data class AudioMetadata(
    val title: String?,
    val duration: String?,
    val artworkUri: String?,
)

data class VideoMetadata(
    val duration: String?,
    val thumbUri: String,
    val width: String,
    val height: String,
)

private fun MediaMetadataRetriever.getDuration(): Long? =
    extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?.milliseconds?.inWholeSeconds
