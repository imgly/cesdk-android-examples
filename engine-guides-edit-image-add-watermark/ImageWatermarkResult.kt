import android.net.Uri
import ly.img.engine.DesignBlock
import java.nio.ByteBuffer

data class ImageWatermarkResult(
    val pageWidth: Float,
    val pageHeight: Float,
    val textWatermark: DesignBlock,
    val logoWatermark: DesignBlock,
    val textOpacity: Float,
    val logoOpacity: Float,
    val textShadowEnabled: Boolean,
    val logoUri: Uri,
    val exportedPng: ByteBuffer,
    val exportedJpeg: ByteBuffer,
)
