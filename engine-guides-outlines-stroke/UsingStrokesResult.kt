import ly.img.engine.Color
import ly.img.engine.StrokeCornerGeometry
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle

data class UsingStrokesResult(
    val canHaveStroke: Boolean,
    val strokeIsEnabled: Boolean = false,
    val strokeColor: Color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 0F),
    val strokeWidth: Float = 0F,
    val strokeStyle: StrokeStyle = StrokeStyle.SOLID,
    val strokePosition: StrokePosition = StrokePosition.CENTER,
    val strokeCornerGeometry: StrokeCornerGeometry = StrokeCornerGeometry.MITER,
)
