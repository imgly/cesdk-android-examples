import java.nio.ByteBuffer

data class InsertMediaShapesOrStickersResult(
    val graphicSupportsShape: Boolean,
    val textSupportsShape: Boolean,
    val starPoints: Int,
    val starInnerDiameter: Float,
    val polygonSides: Int,
    val vectorPath: String,
    val starPropertyCount: Int,
    val stickerResultCount: Int,
    val stickerBlockCount: Int,
    val pngData: ByteBuffer,
)
