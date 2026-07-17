import android.net.Uri
import java.nio.ByteBuffer

data class InsertMediaVideosResult(
    val videoUri: Uri,
    val fillType: String,
    val sourceDuration: Double,
    val trimOffset: Double,
    val trimLength: Double,
    val blockDuration: Double,
    val videoBlockCount: Int,
    val remainingVideoBlockCount: Int,
    val exportedImage: ByteBuffer,
)
