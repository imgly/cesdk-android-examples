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

val IconPack.Elements: ImageVector
    get() {
        if (_elements != null) {
            return _elements!!
        }
        _elements =
            Builder(
                name = "Elements",
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
                    moveTo(13.0f, 10.899f)
                    verticalLineTo(21.0f)
                    lineTo(21.0f, 17.9998f)
                    verticalLineTo(12.3264f)
                    curveTo(20.3783f, 12.6217f, 19.7061f, 12.8281f, 19.0f, 12.9291f)
                    verticalLineTo(16.6138f)
                    lineTo(15.0f, 18.1139f)
                    verticalLineTo(12.3264f)
                    curveTo(14.2504f, 11.9703f, 13.5741f, 11.4849f, 13.0f, 10.899f)
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
                    moveTo(5.0f, 3.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(3.0f)
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
                    moveTo(8.0f, 3.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(3.0f)
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
            }.build()
        return _elements!!
    }

private var _elements: ImageVector? = null
