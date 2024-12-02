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

val IconPack.AddSticker: ImageVector
    get() {
        if (_addsticker != null) {
            return _addsticker!!
        }
        _addsticker =
            Builder(
                name = "Addsticker",
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
                    moveTo(12.3875f, 7.3026f)
                    curveTo(11.6643f, 7.6753f, 11.2879f, 8.5114f, 11.4972f, 9.34f)
                    curveTo(11.5472f, 9.49f, 11.6172f, 9.64f, 11.7072f, 9.78f)
                    lineTo(13.4748f, 9.2984f)
                    curveTo(13.0107f, 8.7027f, 12.6408f, 8.0299f, 12.3875f, 7.3026f)
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
                    moveTo(16.1252f, 11.3843f)
                    lineTo(7.12f, 13.86f)
                    curveTo(7.7684f, 14.6103f, 8.6154f, 15.1623f, 9.5637f, 15.4524f)
                    curveTo(10.5119f, 15.7425f, 11.5228f, 15.759f, 12.48f, 15.5f)
                    curveTo(13.4351f, 15.2328f, 14.2954f, 14.7017f, 14.9623f, 13.9676f)
                    curveTo(15.6103f, 13.2542f, 16.0501f, 12.3774f, 16.2346f, 11.4325f)
                    curveTo(16.1979f, 11.4168f, 16.1615f, 11.4007f, 16.1252f, 11.3843f)
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
                    moveTo(20.0f, 11.9291f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(18.5f)
                    curveTo(16.56f, 15.0f, 15.0f, 16.56f, 15.0f, 18.5f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(5.75f)
                    curveTo(5.2859f, 20.0f, 4.8408f, 19.8156f, 4.5126f, 19.4874f)
                    curveTo(4.1844f, 19.1592f, 4.0f, 18.7141f, 4.0f, 18.25f)
                    verticalLineTo(5.75f)
                    curveTo(4.0f, 5.2859f, 4.1844f, 4.8408f, 4.5126f, 4.5126f)
                    curveTo(4.8408f, 4.1844f, 5.2859f, 4.0f, 5.75f, 4.0f)
                    horizontalLineTo(12.0709f)
                    curveTo(12.1719f, 3.2938f, 12.3783f, 2.6217f, 12.6736f, 2.0f)
                    horizontalLineTo(5.5f)
                    curveTo(3.56f, 2.0f, 2.0f, 3.56f, 2.0f, 5.5f)
                    verticalLineTo(18.5f)
                    curveTo(2.0f, 20.44f, 3.56f, 22.0f, 5.5f, 22.0f)
                    horizontalLineTo(16.0f)
                    lineTo(22.0f, 16.0f)
                    verticalLineTo(11.3264f)
                    curveTo(21.3783f, 11.6217f, 20.7061f, 11.8281f, 20.0f, 11.9291f)
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
                    moveTo(7.23f, 8.64f)
                    curveTo(7.38f, 8.59f, 7.53f, 8.59f, 7.7f, 8.59f)
                    curveTo(8.0825f, 8.5862f, 8.4553f, 8.71f, 8.7596f, 8.9418f)
                    curveTo(9.0639f, 9.1736f, 9.2822f, 9.5002f, 9.38f, 9.87f)
                    curveTo(9.42f, 10.03f, 9.44f, 10.2f, 9.44f, 10.37f)
                    lineTo(6.21f, 11.25f)
                    curveTo(6.1976f, 11.2279f, 6.1851f, 11.2062f, 6.1729f, 11.1848f)
                    curveTo(6.0963f, 11.051f, 6.0259f, 10.9279f, 6.0f, 10.79f)
                    curveTo(5.75f, 9.86f, 6.3f, 8.9f, 7.23f, 8.64f)
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
        return _addsticker!!
    }

private var _addsticker: ImageVector? = null
