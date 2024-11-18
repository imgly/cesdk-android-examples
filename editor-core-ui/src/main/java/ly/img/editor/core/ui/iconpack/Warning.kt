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

val IconPack.Warning: ImageVector
    get() {
        if (_warning != null) {
            return _warning!!
        }
        _warning =
            Builder(
                name = "Warning",
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
                    moveTo(1.0f, 21.0f)
                    lineTo(12.0f, 2.0f)
                    lineTo(23.0f, 21.0f)
                    horizontalLineTo(1.0f)
                    close()
                    moveTo(12.0f, 18.0f)
                    curveTo(12.2833f, 18.0f, 12.521f, 17.904f, 12.713f, 17.712f)
                    curveTo(12.9043f, 17.5207f, 13.0f, 17.2833f, 13.0f, 17.0f)
                    curveTo(13.0f, 16.7167f, 12.9043f, 16.4793f, 12.713f, 16.288f)
                    curveTo(12.521f, 16.096f, 12.2833f, 16.0f, 12.0f, 16.0f)
                    curveTo(11.7167f, 16.0f, 11.4793f, 16.096f, 11.288f, 16.288f)
                    curveTo(11.096f, 16.4793f, 11.0f, 16.7167f, 11.0f, 17.0f)
                    curveTo(11.0f, 17.2833f, 11.096f, 17.5207f, 11.288f, 17.712f)
                    curveTo(11.4793f, 17.904f, 11.7167f, 18.0f, 12.0f, 18.0f)
                    close()
                    moveTo(11.0f, 15.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(15.0f)
                    close()
                }
            }
                .build()
        return _warning!!
    }

private var _warning: ImageVector? = null
