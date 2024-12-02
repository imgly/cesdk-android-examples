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

val IconPack.AddOverlay: ImageVector
    get() {
        if (_addoverlay != null) {
            return _addoverlay!!
        }
        _addoverlay =
            Builder(
                name = "Addoverlay",
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
                    moveTo(12.0f, 5.0f)
                    horizontalLineTo(6.0f)
                    curveTo(4.8954f, 5.0f, 4.0f, 5.8954f, 4.0f, 7.0f)
                    horizontalLineTo(12.2899f)
                    curveTo(12.1013f, 6.3663f, 12.0f, 5.695f, 12.0f, 5.0f)
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
                    moveTo(12.6736f, 8.0f)
                    horizontalLineTo(4.0f)
                    curveTo(2.8954f, 8.0f, 2.0f, 8.8954f, 2.0f, 10.0f)
                    verticalLineTo(20.0f)
                    curveTo(2.0f, 21.1046f, 2.8954f, 22.0f, 4.0f, 22.0f)
                    horizontalLineTo(20.0f)
                    curveTo(21.1046f, 22.0f, 22.0f, 21.1046f, 22.0f, 20.0f)
                    verticalLineTo(11.3264f)
                    curveTo(21.3783f, 11.6217f, 20.7061f, 11.8281f, 20.0f, 11.9291f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(11.9291f)
                    curveTo(17.2939f, 11.8281f, 16.6217f, 11.6217f, 16.0f, 11.3264f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(14.101f)
                    curveTo(13.5151f, 9.4259f, 13.0297f, 8.7496f, 12.6736f, 8.0f)
                    close()
                    moveTo(6.0f, 10.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(10.0f)
                    close()
                    moveTo(6.0f, 14.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(14.0f)
                    close()
                    moveTo(4.0f, 18.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(18.0f)
                    close()
                    moveTo(18.0f, 20.0f)
                    verticalLineTo(18.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(18.0f)
                    close()
                    moveTo(16.0f, 20.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(20.0f)
                    close()
                    moveTo(20.0f, 14.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(20.0f)
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
                    moveTo(12.6736f, 2.0f)
                    horizontalLineTo(8.0f)
                    curveTo(6.8954f, 2.0f, 6.0f, 2.8954f, 6.0f, 4.0f)
                    horizontalLineTo(12.0709f)
                    curveTo(12.1719f, 3.2938f, 12.3783f, 2.6217f, 12.6736f, 2.0f)
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
            }
                .build()
        return _addoverlay!!
    }

private var _addoverlay: ImageVector? = null
