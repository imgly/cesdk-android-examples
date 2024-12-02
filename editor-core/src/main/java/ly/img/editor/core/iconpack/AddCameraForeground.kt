package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.AddCameraForeground: ImageVector
    get() {
        if (_addcameraforegound != null) {
            return _addcameraforegound!!
        }
        _addcameraforegound =
            Builder(
                name = "Addcameraforegound",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(11.47f, 8.5269f)
                    curveTo(10.4459f, 8.6342f, 9.5603f, 9.0629f, 8.813f, 9.813f)
                    curveTo(7.9397f, 10.6897f, 7.502f, 11.752f, 7.5f, 13.0f)
                    curveTo(7.498f, 14.248f, 7.9357f, 15.3107f, 8.813f, 16.188f)
                    curveTo(9.6903f, 17.0653f, 10.7527f, 17.5027f, 12.0f, 17.5f)
                    curveTo(13.25f, 17.5f, 14.3127f, 17.0627f, 15.188f, 16.188f)
                    curveTo(16.0633f, 15.3133f, 16.5007f, 14.2507f, 16.5f, 13.0f)
                    curveTo(16.5f, 12.9458f, 16.4991f, 12.8919f, 16.4974f, 12.8384f)
                    curveTo(15.6959f, 12.663f, 14.9457f, 12.3504f, 14.2728f, 11.9263f)
                    curveTo(14.4243f, 12.2502f, 14.5f, 12.6081f, 14.5f, 13.0f)
                    curveTo(14.5f, 13.7f, 14.2583f, 14.2917f, 13.775f, 14.775f)
                    curveTo(13.2917f, 15.2583f, 12.7f, 15.5f, 12.0f, 15.5f)
                    curveTo(11.3f, 15.5f, 10.7083f, 15.2583f, 10.225f, 14.775f)
                    curveTo(9.7417f, 14.2917f, 9.5f, 13.7f, 9.5f, 13.0f)
                    curveTo(9.5f, 12.3f, 9.7417f, 11.7083f, 10.225f, 11.225f)
                    curveTo(10.7083f, 10.7417f, 11.3f, 10.5f, 12.0f, 10.5f)
                    curveTo(12.2542f, 10.5f, 12.4941f, 10.5319f, 12.7197f, 10.5956f)
                    curveTo(12.1912f, 9.9889f, 11.7656f, 9.2903f, 11.47f, 8.5269f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(20.0f, 12.7101f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(8.05f)
                    lineTo(9.875f, 5.0f)
                    horizontalLineTo(11.0709f)
                    curveTo(11.1719f, 4.2938f, 11.3783f, 3.6217f, 11.6736f, 3.0f)
                    horizontalLineTo(9.0f)
                    lineTo(7.15f, 5.0f)
                    horizontalLineTo(4.0f)
                    curveTo(3.4507f, 5.0007f, 2.98f, 5.1967f, 2.588f, 5.588f)
                    curveTo(2.196f, 5.9793f, 2.0f, 6.45f, 2.0f, 7.0f)
                    verticalLineTo(19.0f)
                    curveTo(2.0007f, 19.5507f, 2.1967f, 20.0217f, 2.588f, 20.413f)
                    curveTo(2.9793f, 20.8043f, 3.45f, 21.0f, 4.0f, 21.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.5507f, 21.0007f, 21.0217f, 20.805f, 21.413f, 20.413f)
                    curveTo(21.8043f, 20.021f, 22.0f, 19.55f, 22.0f, 19.0f)
                    verticalLineTo(11.7453f)
                    curveTo(21.396f, 12.1666f, 20.7224f, 12.4951f, 20.0f, 12.7101f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(18.0f, 1.0f)
                    curveTo(15.2386f, 1.0f, 13.0f, 3.2386f, 13.0f, 6.0f)
                    curveTo(13.0f, 8.7614f, 15.2386f, 11.0f, 18.0f, 11.0f)
                    curveTo(20.7614f, 11.0f, 23.0f, 8.7614f, 23.0f, 6.0f)
                    curveTo(23.0f, 3.2386f, 20.7614f, 1.0f, 18.0f, 1.0f)
                    close()
                    moveTo(17.5f, 5.5f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(17.5f)
                    close()
                }
            }.build()
        return _addcameraforegound!!
    }

private var _addcameraforegound: ImageVector? = null
