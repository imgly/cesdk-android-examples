import ly.img.engine.ContentFillMode
import java.nio.ByteBuffer

data class ProgrammaticImageEditResult(
    val width: Float,
    val height: Float,
    val fillMode: ContentFillMode,
    val brightness: Float,
    val contrast: Float,
    val imageFillType: String,
    val exportedPng: ByteBuffer,
)
