package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _Repeat: ImageVector? = null

val IconPack.Repeat: ImageVector
    get() {
        if (_Repeat != null) {
            return _Repeat!!
        }
        _Repeat =
            ImageVector.Builder(
                name = "Repeat",
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
                    moveTo(4f, 10f)
                    curveTo(4f, 7.7909f, 5.7909f, 6f, 8f, 6f)
                    horizontalLineTo(16f)
                    curveTo(18.2091f, 6f, 20f, 7.7909f, 20f, 10f)
                    verticalLineTo(14f)
                    curveTo(20f, 16.2091f, 18.2091f, 18f, 16f, 18f)
                    horizontalLineTo(14.8f)
                    lineTo(16.4f, 19.6f)
                    lineTo(15f, 21f)
                    lineTo(11f, 17f)
                    lineTo(15f, 13f)
                    lineTo(16.4f, 14.4f)
                    lineTo(14.8f, 16f)
                    horizontalLineTo(16f)
                    curveTo(17.1046f, 16f, 18f, 15.1046f, 18f, 14f)
                    verticalLineTo(10f)
                    curveTo(18f, 8.8954f, 17.1046f, 8f, 16f, 8f)
                    horizontalLineTo(8f)
                    curveTo(6.8954f, 8f, 6f, 8.8954f, 6f, 10f)
                    verticalLineTo(14f)
                    curveTo(6f, 15.1046f, 6.8954f, 16f, 8f, 16f)
                    horizontalLineTo(9f)
                    verticalLineTo(18f)
                    horizontalLineTo(8f)
                    curveTo(5.7909f, 18f, 4f, 16.2091f, 4f, 14f)
                    verticalLineTo(10f)
                    close()
                }
            }.build()
        return _Repeat!!
    }
