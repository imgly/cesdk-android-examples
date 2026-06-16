import android.net.Uri
import ly.img.engine.ContentFillMode

data class VideoFillsResult(
    val currentFillType: String,
    val currentVideoUri: Uri,
    val coverMode: ContentFillMode,
    val containMode: ContentFillMode,
    val finalContentFillMode: ContentFillMode,
    val responsiveSourceCount: Int,
    val durationSeconds: Double,
    val thumbnailCount: Int,
    val thumbnailsHavePixels: Boolean,
    val sharedFillType: String,
    val opacity: Float,
)
