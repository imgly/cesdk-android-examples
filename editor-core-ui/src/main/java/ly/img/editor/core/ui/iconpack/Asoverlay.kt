package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.Asoverlay: ImageVector
    get() {
        if (_asoverlay != null) {
            return _asoverlay!!
        }
        _asoverlay =
            Builder(
                name = "Asoverlay",
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
                    moveTo(6.0f, 4.0f)
                    horizontalLineTo(18.0f)
                    curveTo(18.5523f, 4.0f, 19.0f, 4.4477f, 19.0f, 5.0f)
                    verticalLineTo(13.0f)
                    curveTo(19.0f, 13.5523f, 18.5523f, 14.0f, 18.0f, 14.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(18.0f)
                    curveTo(19.6569f, 16.0f, 21.0f, 14.6569f, 21.0f, 13.0f)
                    verticalLineTo(5.0f)
                    curveTo(21.0f, 3.3431f, 19.6569f, 2.0f, 18.0f, 2.0f)
                    horizontalLineTo(6.0f)
                    curveTo(4.3432f, 2.0f, 3.0f, 3.3431f, 3.0f, 5.0f)
                    verticalLineTo(13.0f)
                    curveTo(3.0f, 14.6569f, 4.3432f, 16.0f, 6.0f, 16.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(6.0f)
                    curveTo(5.4477f, 14.0f, 5.0f, 13.5523f, 5.0f, 13.0f)
                    verticalLineTo(5.0f)
                    curveTo(5.0f, 4.4477f, 5.4477f, 4.0f, 6.0f, 4.0f)
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
                    moveTo(12.0f, 7.0f)
                    lineTo(16.0f, 11.0f)
                    lineTo(14.6f, 12.425f)
                    lineTo(13.0f, 10.825f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(10.825f)
                    lineTo(9.4f, 12.425f)
                    lineTo(8.0f, 11.0f)
                    lineTo(12.0f, 7.0f)
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
                    moveTo(3.0f, 22.0f)
                    curveTo(3.0f, 21.4477f, 3.4477f, 21.0f, 4.0f, 21.0f)
                    horizontalLineTo(8.0f)
                    curveTo(8.5523f, 21.0f, 9.0f, 21.4477f, 9.0f, 22.0f)
                    horizontalLineTo(11.0f)
                    curveTo(11.0f, 20.3431f, 9.6568f, 19.0f, 8.0f, 19.0f)
                    horizontalLineTo(4.0f)
                    curveTo(2.3431f, 19.0f, 1.0f, 20.3431f, 1.0f, 22.0f)
                    horizontalLineTo(3.0f)
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
                    moveTo(20.0f, 19.0f)
                    curveTo(21.6569f, 19.0f, 23.0f, 20.3431f, 23.0f, 22.0f)
                    horizontalLineTo(21.0f)
                    curveTo(21.0f, 21.4477f, 20.5523f, 21.0f, 20.0f, 21.0f)
                    horizontalLineTo(16.0f)
                    curveTo(15.4477f, 21.0f, 15.0f, 21.4477f, 15.0f, 22.0f)
                    horizontalLineTo(13.0f)
                    curveTo(13.0f, 20.3431f, 14.3431f, 19.0f, 16.0f, 19.0f)
                    horizontalLineTo(20.0f)
                    close()
                }
            }
                .build()
        return _asoverlay!!
    }

private var _asoverlay: ImageVector? = null
