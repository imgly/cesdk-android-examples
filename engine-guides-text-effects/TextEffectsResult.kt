import ly.img.engine.Color
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle
import java.nio.ByteBuffer

data class TextEffectsResult(
    val dropShadowEnabled: Boolean,
    val dropShadowColor: Color,
    val dropShadowOffsetX: Float,
    val dropShadowOffsetY: Float,
    val dropShadowBlurRadiusX: Float,
    val dropShadowBlurRadiusY: Float,
    val strokeEnabled: Boolean,
    val strokeWidth: Float,
    val strokeColor: Color,
    val strokeStyle: StrokeStyle,
    val strokePosition: StrokePosition,
    val pngData: ByteBuffer,
)
