package ly.img.editor.configuration.memories.widget

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap

private const val ANDROID_ASSET_PREFIX = "file:///android_asset/"

// Small process-wide cache so scrolling / recomposition doesn't re-decode, and the grid and the
// timeline strip share frames for the same video.
private val thumbnailCache = LruCache<String, Bitmap>(16)

/**
 * Returns a single still frame from the middle of the video at [url] for use as a thumbnail, or
 * null while it is still being decoded. Uses the platform [MediaMetadataRetriever] (no extra
 * dependency) off the main thread, and caches the result by URL.
 *
 * Handles `content://` (picked media), `file://`, and bundled `file:///android_asset/...` URIs.
 */
@Composable
fun rememberVideoThumbnail(
    url: String,
    targetPx: Int = 256,
): Bitmap? {
    val context = LocalContext.current
    return produceState<Bitmap?>(initialValue = thumbnailCache.get(url), key1 = url) {
        if (value != null) return@produceState
        value = withContext(Dispatchers.IO) {
            extractMiddleFrame(context, url, targetPx)?.also { thumbnailCache.put(url, it) }
        }
    }.value
}

// Cache of already-written thumbnail files (video URL -> file:// URI) so a repeated request reuses
// the PNG on disk instead of decoding another frame. Concurrent because several thumbnail requests
// run on Dispatchers.IO threads at once.
private val thumbnailFileCache = ConcurrentHashMap<String, String>()

/**
 * Extract the middle frame of the video at [url], write it to a PNG in the app cache, and return its
 * `file://` URI (suitable for an engine image fill), or null if a frame can't be decoded. Cached by
 * URL. Runs its decode + write off the main thread.
 */
suspend fun videoThumbnailFileUri(
    context: Context,
    url: String,
    targetPx: Int = 512,
): String? {
    thumbnailFileCache[url]?.let { cached ->
        if (Uri.parse(cached).path?.let { File(it).exists() } == true) return cached
    }
    return withContext(Dispatchers.IO) {
        val bitmap = extractMiddleFrame(context, url, targetPx) ?: return@withContext null
        try {
            val dir = File(context.cacheDir, "burst_thumbs").apply { mkdirs() }
            val file = File(dir, "thumb_${url.hashCode()}.png")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 90, it) }
            Uri.fromFile(file).toString().also { thumbnailFileCache[url] = it }
        } catch (e: Exception) {
            null
        }
    }
}

private fun extractMiddleFrame(
    context: Context,
    url: String,
    targetPx: Int,
): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        if (url.startsWith(ANDROID_ASSET_PREFIX)) {
            // Bundled assets aren't real files: open via an asset file descriptor.
            context.assets.openFd(url.removePrefix(ANDROID_ASSET_PREFIX)).use { afd ->
                retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
        } else {
            retriever.setDataSource(context, Uri.parse(url))
        }
        val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        val middleUs = (durationMs / 2) * 1000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // Decode straight to a small bitmap to keep memory low.
            retriever.getScaledFrameAtTime(
                middleUs,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                targetPx,
                targetPx,
            )
        } else {
            retriever.getFrameAtTime(middleUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }
    } catch (e: Exception) {
        null
    } finally {
        runCatching { retriever.release() }
    }
}
