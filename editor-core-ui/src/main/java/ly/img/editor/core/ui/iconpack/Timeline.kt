package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _Timeline: ImageVector? = null

val IconPack.Timeline: ImageVector
    get() {
        if (_Timeline != null) {
            return _Timeline!!
        }
        _Timeline =
            ImageVector.Builder(
                name = "Timeline",
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
                    moveTo(17f, 1f)
                    horizontalLineTo(14f)
                    verticalLineTo(4f)
                    lineTo(15.5f, 5f)
                    lineTo(17f, 4f)
                    verticalLineTo(1f)
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
                    moveTo(2f, 7f)
                    verticalLineTo(10f)
                    horizontalLineTo(12f)
                    verticalLineTo(7f)
                    horizontalLineTo(2f)
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
                    moveTo(5f, 17f)
                    verticalLineTo(20f)
                    horizontalLineTo(13.5f)
                    verticalLineTo(17f)
                    horizontalLineTo(5f)
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
                    moveTo(15f, 6f)
                    horizontalLineTo(16f)
                    verticalLineTo(22f)
                    horizontalLineTo(15f)
                    verticalLineTo(6f)
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
                    moveTo(17.5f, 17f)
                    horizontalLineTo(20f)
                    verticalLineTo(20f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(17f)
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
                    moveTo(8f, 12f)
                    verticalLineTo(15f)
                    horizontalLineTo(13.5f)
                    verticalLineTo(12f)
                    horizontalLineTo(8f)
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
                    moveTo(17.5f, 12f)
                    horizontalLineTo(22f)
                    verticalLineTo(15f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(12f)
                    close()
                }
            }.build()
        return _Timeline!!
    }
