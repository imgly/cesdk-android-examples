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

val IconPack.AddText: ImageVector
    get() {
        if (_addtext != null) {
            return _addtext!!
        }
        _addtext =
            Builder(
                name = "Addtext",
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
                    moveTo(14.6669f, 10.498f)
                    curveTo(14.4301f, 10.7337f, 14.2397f, 10.9908f, 14.0957f, 11.2695f)
                    curveTo(13.8939f, 11.6602f, 13.793f, 12.0638f, 13.793f, 12.4805f)
                    horizontalLineTo(16.1367f)
                    curveTo(16.1367f, 12.2005f, 16.2148f, 11.9499f, 16.3711f, 11.7285f)
                    curveTo(16.4161f, 11.6647f, 16.4663f, 11.6048f, 16.5215f, 11.5486f)
                    curveTo(15.8475f, 11.2933f, 15.2231f, 10.937f, 14.6669f, 10.498f)
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
                    moveTo(19.7266f, 11.9627f)
                    curveTo(19.8333f, 12.2151f, 19.8867f, 12.5081f, 19.8867f, 12.8418f)
                    verticalLineTo(13.5742f)
                    horizontalLineTo(18.3828f)
                    curveTo(17.6146f, 13.5742f, 16.931f, 13.6523f, 16.332f, 13.8086f)
                    curveTo(15.7331f, 13.9583f, 15.2253f, 14.1797f, 14.8086f, 14.4727f)
                    curveTo(14.3984f, 14.7656f, 14.0859f, 15.1302f, 13.8711f, 15.5664f)
                    curveTo(13.6628f, 15.9961f, 13.5586f, 16.4909f, 13.5586f, 17.0508f)
                    curveTo(13.5586f, 17.6497f, 13.7116f, 18.1868f, 14.0176f, 18.6621f)
                    curveTo(14.3236f, 19.1309f, 14.7467f, 19.5052f, 15.2871f, 19.7852f)
                    curveTo(15.8275f, 20.0586f, 16.4427f, 20.1953f, 17.1328f, 20.1953f)
                    curveTo(17.6797f, 20.1953f, 18.1615f, 20.1074f, 18.5781f, 19.9316f)
                    curveTo(19.0013f, 19.7559f, 19.3626f, 19.5312f, 19.6621f, 19.2578f)
                    curveTo(19.7699f, 19.1592f, 19.8705f, 19.0589f, 19.9637f, 18.9571f)
                    curveTo(19.9672f, 18.9824f, 19.9709f, 19.0078f, 19.9746f, 19.0332f)
                    curveTo(20.0397f, 19.4238f, 20.1276f, 19.7461f, 20.2383f, 20.0f)
                    horizontalLineTo(22.6504f)
                    verticalLineTo(19.8438f)
                    curveTo(22.5202f, 19.5638f, 22.4193f, 19.235f, 22.3477f, 18.8574f)
                    curveTo(22.2826f, 18.4733f, 22.25f, 18.0404f, 22.25f, 17.5586f)
                    verticalLineTo(12.8613f)
                    curveTo(22.25f, 12.2836f, 22.1583f, 11.7759f, 21.9749f, 11.3382f)
                    curveTo(21.2803f, 11.6648f, 20.5233f, 11.8806f, 19.7266f, 11.9627f)
                    close()
                    moveTo(19.7109f, 17.3047f)
                    curveTo(19.7804f, 17.207f, 19.839f, 17.1112f, 19.8867f, 17.0171f)
                    verticalLineTo(15.0293f)
                    horizontalLineTo(18.5586f)
                    curveTo(18.1224f, 15.0293f, 17.7383f, 15.0716f, 17.4062f, 15.1562f)
                    curveTo(17.0742f, 15.2344f, 16.7975f, 15.3516f, 16.5762f, 15.5078f)
                    curveTo(16.3548f, 15.6641f, 16.1888f, 15.8529f, 16.0781f, 16.0742f)
                    curveTo(15.9674f, 16.2956f, 15.9121f, 16.5462f, 15.9121f, 16.8262f)
                    curveTo(15.9121f, 17.1061f, 15.9772f, 17.3633f, 16.1074f, 17.5977f)
                    curveTo(16.2376f, 17.8255f, 16.4264f, 18.0046f, 16.6738f, 18.1348f)
                    curveTo(16.9277f, 18.265f, 17.2337f, 18.3301f, 17.5918f, 18.3301f)
                    curveTo(18.0736f, 18.3301f, 18.4935f, 18.2324f, 18.8516f, 18.0371f)
                    curveTo(19.2161f, 17.8353f, 19.5026f, 17.5911f, 19.7109f, 17.3047f)
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
                    moveTo(7.9609f, 5.7813f)
                    horizontalLineTo(5.7148f)
                    lineTo(0.7148f, 20.0f)
                    horizontalLineTo(3.3027f)
                    lineTo(4.3419f, 16.7285f)
                    horizontalLineTo(9.3411f)
                    lineTo(10.3828f, 20.0f)
                    horizontalLineTo(12.9902f)
                    lineTo(7.9609f, 5.7813f)
                    close()
                    moveTo(8.7004f, 14.7168f)
                    horizontalLineTo(4.9809f)
                    lineTo(6.8383f, 8.8691f)
                    lineTo(8.7004f, 14.7168f)
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
                    moveTo(19.0f, 0.0f)
                    curveTo(16.2386f, 0.0f, 14.0f, 2.2386f, 14.0f, 5.0f)
                    curveTo(14.0f, 7.7614f, 16.2386f, 10.0f, 19.0f, 10.0f)
                    curveTo(21.7614f, 10.0f, 24.0f, 7.7614f, 24.0f, 5.0f)
                    curveTo(24.0f, 2.2386f, 21.7614f, 0.0f, 19.0f, 0.0f)
                    close()
                    moveTo(18.5f, 4.5f)
                    verticalLineTo(2.0f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(4.5f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(4.5f)
                    horizontalLineTo(18.5f)
                    close()
                }
            }.build()
        return _addtext!!
    }

private var _addtext: ImageVector? = null
