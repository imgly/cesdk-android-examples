import ly.img.engine.RGBAColor
import java.nio.ByteBuffer

data class EditShapesResult(
    val supportsGraphicShape: Boolean,
    val supportsTextShape: Boolean,
    val initialShapeType: String,
    val cornerRadiusTopLeft: Float,
    val replacementShapeType: String,
    val starProperties: List<String>,
    val starPoints: Int,
    val starInnerDiameter: Float,
    val fillColor: RGBAColor,
    val positionX: Float,
    val positionY: Float,
    val width: Float,
    val height: Float,
    val rotation: Float,
    val previewPngData: ByteBuffer,
)
