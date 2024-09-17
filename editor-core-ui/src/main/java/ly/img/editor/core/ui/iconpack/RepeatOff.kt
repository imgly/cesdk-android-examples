package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _RepeatOff: ImageVector? = null

val IconPack.RepeatOff: ImageVector
    get() {
        if (_RepeatOff != null) {
            return _RepeatOff!!
        }
        _RepeatOff =
            ImageVector.Builder(
                name = "CustomRepeatOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.EvenOdd,
                ) {
                    moveTo(4.27f, 3f)
                    lineTo(3f, 4.27f)
                    lineTo(5.56008f, 6.83008f)
                    curveTo(4.6113f, 7.5615f, 4f, 8.7094f, 4f, 10f)
                    verticalLineTo(14f)
                    curveTo(4f, 16.2091f, 5.7909f, 18f, 8f, 18f)
                    verticalLineTo(16f)
                    curveTo(6.8954f, 16f, 6f, 15.1046f, 6f, 14f)
                    verticalLineTo(10f)
                    curveTo(6f, 9.2604f, 6.4015f, 8.6145f, 6.9985f, 8.2685f)
                    lineTo(12.865f, 14.135f)
                    lineTo(10f, 17f)
                    lineTo(14f, 21f)
                    lineTo(15.4f, 19.6f)
                    lineTo(13.8f, 18f)
                    horizontalLineTo(16f)
                    curveTo(16.2295f, 18f, 16.4546f, 17.9807f, 16.6735f, 17.9435f)
                    lineTo(19.73f, 21f)
                    lineTo(21f, 19.73f)
                    lineTo(7.3265f, 6.05646f)
                    curveTo(7.3265f, 6.0565f, 7.3265f, 6.0565f, 7.3265f, 6.0565f)
                    lineTo(4.27f, 3f)
                    close()
                    moveTo(14.265f, 15.535f)
                    lineTo(14.73f, 16f)
                    horizontalLineTo(13.8f)
                    lineTo(14.265f, 15.535f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(9.81004f, 6f)
                    lineTo(11.81f, 8f)
                    horizontalLineTo(16f)
                    curveTo(17.1046f, 8f, 18f, 8.8954f, 18f, 10f)
                    verticalLineTo(14f)
                    curveTo(18f, 14.0613f, 17.9972f, 14.1219f, 17.9918f, 14.1818f)
                    lineTo(19.5854f, 15.7754f)
                    curveTo(19.8508f, 15.2404f, 20f, 14.6376f, 20f, 14f)
                    verticalLineTo(10f)
                    curveTo(20f, 7.7909f, 18.2091f, 6f, 16f, 6f)
                    horizontalLineTo(9.81004f)
                    close()
                }
            }.build()
        return _RepeatOff!!
    }
