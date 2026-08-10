import java.nio.ByteBuffer

data class ChromaKeyGreenScreenResult(
    val blockSupportsEffects: Boolean,
    val colorMatch: Float,
    val smoothness: Float,
    val spill: Float,
    val enabledAfterToggle: Boolean,
    val removed: Boolean,
    val heroPng: ByteBuffer,
)
