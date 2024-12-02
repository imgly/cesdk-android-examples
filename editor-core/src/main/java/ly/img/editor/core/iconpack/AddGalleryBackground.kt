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

val IconPack.AddGalleryBackground: ImageVector
    get() {
        if (_addgallery != null) {
            return _addgallery!!
        }
        _addgallery =
            Builder(
                name = "Addgallery",
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(19.0f, 14.0f)
                    curveTo(16.2386f, 14.0f, 14.0f, 16.2386f, 14.0f, 19.0f)
                    curveTo(14.0f, 21.7614f, 16.2386f, 24.0f, 19.0f, 24.0f)
                    curveTo(21.7614f, 24.0f, 24.0f, 21.7614f, 24.0f, 19.0f)
                    curveTo(24.0f, 16.2386f, 21.7614f, 14.0f, 19.0f, 14.0f)
                    close()
                    moveTo(18.5f, 18.5f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(18.5f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(19.5f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(19.5f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(18.5f)
                    horizontalLineTo(18.5f)
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
                    moveTo(6.588f, 17.413f)
                    curveTo(6.9793f, 17.8043f, 7.45f, 18.0f, 8.0f, 18.0f)
                    horizontalLineTo(12.0709f)
                    curveTo(12.2288f, 16.8959f, 12.6444f, 15.875f, 13.2547f, 15.0f)
                    lineTo(9.0223f, 15.0f)
                    curveTo(8.6062f, 15.0f, 8.3722f, 14.5215f, 8.6276f, 14.193f)
                    lineTo(11.1888f, 10.9f)
                    curveTo(11.36f, 10.6799f, 11.6781f, 10.6423f, 11.8958f, 10.8165f)
                    lineTo(13.6264f, 12.201f)
                    curveTo(13.8354f, 12.3681f, 14.1388f, 12.3412f, 14.315f, 12.1398f)
                    lineTo(17.1174f, 8.9372f)
                    curveTo(17.3188f, 8.707f, 17.6778f, 8.71f, 17.8753f, 8.9434f)
                    lineTo(20.6213f, 12.1887f)
                    curveTo(21.102f, 12.3027f, 21.5634f, 12.4662f, 22.0f, 12.6736f)
                    verticalLineTo(4.0f)
                    curveTo(22.0007f, 3.4507f, 21.805f, 2.98f, 21.413f, 2.588f)
                    curveTo(21.021f, 2.196f, 20.55f, 2.0f, 20.0f, 2.0f)
                    horizontalLineTo(8.0f)
                    curveTo(7.4507f, 2.0007f, 6.98f, 2.1967f, 6.588f, 2.588f)
                    curveTo(6.196f, 2.9793f, 6.0f, 3.45f, 6.0f, 4.0f)
                    verticalLineTo(16.0f)
                    curveTo(6.0007f, 16.5507f, 6.1967f, 17.0217f, 6.588f, 17.413f)
                    close()
                    moveTo(10.5f, 8.0f)
                    curveTo(11.3284f, 8.0f, 12.0f, 7.3284f, 12.0f, 6.5f)
                    curveTo(12.0f, 5.6716f, 11.3284f, 5.0f, 10.5f, 5.0f)
                    curveTo(9.6716f, 5.0f, 9.0f, 5.6716f, 9.0f, 6.5f)
                    curveTo(9.0f, 7.3284f, 9.6716f, 8.0f, 10.5f, 8.0f)
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
                    moveTo(12.0709f, 20.0f)
                    curveTo(12.1719f, 20.7061f, 12.3783f, 21.3783f, 12.6736f, 22.0f)
                    horizontalLineTo(4.0f)
                    curveTo(3.45f, 22.0f, 2.9793f, 21.8043f, 2.588f, 21.413f)
                    curveTo(2.1967f, 21.0217f, 2.0007f, 20.5507f, 2.0f, 20.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(12.0709f)
                    close()
                }
            }
                .build()
        return _addgallery!!
    }

private var _addgallery: ImageVector? = null
