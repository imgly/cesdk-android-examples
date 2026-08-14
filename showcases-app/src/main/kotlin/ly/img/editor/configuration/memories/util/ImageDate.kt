package ly.img.editor.configuration.memories.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.configuration.memories.model.ImageItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Build [ImageItem]s from freshly-picked [uris], assigning ids from [baseId] + 1 so they stay unique
 * even after deletions. Runs entirely on [Dispatchers.IO] because resolving MIME types and reading
 * EXIF dates hits the ContentResolver; callers dispatch the result back to the main thread.
 */
suspend fun buildImageItems(
    contentResolver: ContentResolver,
    uris: List<Uri>,
    baseId: Int,
): List<ImageItem> = withContext(Dispatchers.IO) {
    uris.mapIndexed { index, uri ->
        val isVideo = contentResolver.getType(uri)?.startsWith("video/") == true
        ImageItem(
            id = baseId + index + 1,
            url = uri.toString(),
            isVideo = isVideo,
            creationDate = if (isVideo) null else getImageCreationDate(contentResolver, uri),
        )
    }
}

/**
 * Extract creation date from an image URI using MediaStore and EXIF data.
 * Returns timestamp in milliseconds since epoch, or null if not available.
 */
fun getImageCreationDate(
    contentResolver: ContentResolver,
    uri: Uri,
): Long? {
    try {
        // First try to get DATE_TAKEN from MediaStore (most reliable for gallery images)
        val projection = arrayOf(MediaStore.Images.ImageColumns.DATE_TAKEN)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)
                val dateTaken = cursor.getLong(dateIndex)
                if (dateTaken > 0) {
                    return dateTaken
                }
            }
        }
    } catch (e: Exception) {
        // MediaStore query failed, continue to EXIF fallback
    }

    try {
        // Fallback to EXIF data
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val exif = ExifInterface(inputStream)
            val dateTimeOriginal = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)

            dateTimeOriginal?.let { dateString ->
                // EXIF DateTimeOriginal is local wall-clock time with no zone. Interpret it in the
                // device's zone so the epoch lines up with MediaStore's DATE_TAKEN (which we prefer
                // above); parsing it as UTC instead skews mixed-source sorting by the UTC offset.
                val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)
                sdf.timeZone = TimeZone.getDefault()
                return sdf.parse(dateString)?.time
            }
        }
    } catch (e: Exception) {
        // EXIF extraction failed
    }

    return null
}
