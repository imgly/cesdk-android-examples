package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _Split: ImageVector? = null

val IconPack.Split: ImageVector
    get() {
        if (_Split != null) {
            return _Split!!
        }
        _Split =
            ImageVector.Builder(
                name = "Split",
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
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(13f, 1f)
                    verticalLineTo(5f)
                    horizontalLineTo(11f)
                    verticalLineTo(1f)
                    horizontalLineTo(13f)
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
                    moveTo(11f, 11f)
                    verticalLineTo(7f)
                    horizontalLineTo(13f)
                    verticalLineTo(11f)
                    horizontalLineTo(11f)
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
                    moveTo(11f, 13f)
                    verticalLineTo(17f)
                    horizontalLineTo(13f)
                    verticalLineTo(13f)
                    horizontalLineTo(11f)
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
                    moveTo(13f, 19f)
                    horizontalLineTo(11f)
                    verticalLineTo(23f)
                    horizontalLineTo(13f)
                    verticalLineTo(19f)
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
                    moveTo(3f, 4f)
                    horizontalLineTo(9f)
                    verticalLineTo(6f)
                    horizontalLineTo(3f)
                    lineTo(3f, 18f)
                    horizontalLineTo(9f)
                    verticalLineTo(20f)
                    horizontalLineTo(3f)
                    curveTo(1.8954f, 20f, 1f, 19.1046f, 1f, 18f)
                    verticalLineTo(6f)
                    curveTo(1f, 4.8954f, 1.8954f, 4f, 3f, 4f)
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
                    moveTo(21f, 18f)
                    horizontalLineTo(15f)
                    verticalLineTo(20f)
                    horizontalLineTo(21f)
                    curveTo(22.1046f, 20f, 23f, 19.1046f, 23f, 18f)
                    verticalLineTo(6f)
                    curveTo(23f, 4.8954f, 22.1046f, 4f, 21f, 4f)
                    horizontalLineTo(15f)
                    verticalLineTo(6f)
                    horizontalLineTo(21f)
                    verticalLineTo(18f)
                    close()
                }
            }.build()
        return _Split!!
    }
