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

val IconPack.AddGalleryForeground: ImageVector
    get() {
        if (_addgalleryforeground != null) {
            return _addgalleryforeground!!
        }
        _addgalleryforeground =
            Builder(
                name = "Addgalleryforeground",
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
                    moveTo(18.0f, 15.0f)
                    horizontalLineTo(9.0f)
                    lineTo(11.3906f, 12.175f)
                    lineTo(12.9375f, 14.0f)
                    lineTo(15.0469f, 11.5f)
                    lineTo(18.0f, 15.0f)
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
                    moveTo(19.0251f, 12.9255f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(8.0251f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(11.0709f)
                    curveTo(11.1719f, 4.2938f, 11.3783f, 3.6217f, 11.6736f, 3.0f)
                    horizontalLineTo(8.0251f)
                    curveTo(7.4758f, 3.0007f, 7.0051f, 3.1967f, 6.6131f, 3.588f)
                    curveTo(6.2211f, 3.9793f, 6.0251f, 4.45f, 6.0251f, 5.0f)
                    verticalLineTo(16.0f)
                    curveTo(6.0244f, 16.5507f, 6.2201f, 17.0217f, 6.6121f, 17.413f)
                    curveTo(7.0041f, 17.8043f, 7.4751f, 18.0f, 8.0251f, 18.0f)
                    horizontalLineTo(19.0251f)
                    curveTo(19.5758f, 18.0007f, 20.0468f, 17.805f, 20.4381f, 17.413f)
                    curveTo(20.8294f, 17.021f, 21.0251f, 16.55f, 21.0251f, 16.0f)
                    verticalLineTo(12.3144f)
                    curveTo(20.4038f, 12.6126f, 19.7316f, 12.8218f, 19.0251f, 12.9255f)
                    close()
                    moveTo(17.3001f, 20.0f)
                    horizontalLineTo(11.8251f)
                    lineTo(4.4751f, 20.9f)
                    lineTo(3.1251f, 9.95f)
                    lineTo(4.0251f, 9.85f)
                    verticalLineTo(7.85f)
                    lineTo(2.9001f, 7.975f)
                    curveTo(2.3508f, 8.0417f, 1.9051f, 8.2917f, 1.5631f, 8.725f)
                    curveTo(1.2211f, 9.1583f, 1.0834f, 9.65f, 1.1501f, 10.2f)
                    lineTo(2.4501f, 21.15f)
                    curveTo(2.5174f, 21.7f, 2.7718f, 22.1417f, 3.2131f, 22.475f)
                    curveTo(3.6544f, 22.8083f, 4.1501f, 22.9417f, 4.7001f, 22.875f)
                    lineTo(15.6001f, 21.55f)
                    curveTo(16.0334f, 21.5007f, 16.4001f, 21.33f, 16.7001f, 21.038f)
                    curveTo(17.0001f, 20.746f, 17.2001f, 20.4f, 17.3001f, 20.0f)
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
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(10.5f, 8.5f)
                    moveToRelative(-1.5f, 0.0f)
                    arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
                    arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
                }
            }.build()
        return _addgalleryforeground!!
    }

private var _addgalleryforeground: ImageVector? = null
