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

val IconPack.Movedown: ImageVector
    get() {
        if (_movedown != null) {
            return _movedown!!
        }
        _movedown =
            Builder(
                name = "Movedown",
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
                    moveTo(6.0f, 22.0f)
                    curveTo(5.45f, 22.0f, 4.9793f, 21.8043f, 4.588f, 21.413f)
                    curveTo(4.1967f, 21.0217f, 4.0007f, 20.5507f, 4.0f, 20.0f)
                    verticalLineTo(4.0f)
                    curveTo(4.0f, 3.45f, 4.196f, 2.9793f, 4.588f, 2.588f)
                    curveTo(4.98f, 2.1967f, 5.4507f, 2.0007f, 6.0f, 2.0f)
                    horizontalLineTo(14.0f)
                    lineTo(20.0f, 8.0f)
                    verticalLineTo(20.0f)
                    curveTo(20.0f, 20.55f, 19.8043f, 21.021f, 19.413f, 21.413f)
                    curveTo(19.0217f, 21.805f, 18.5507f, 22.0007f, 18.0f, 22.0f)
                    horizontalLineTo(6.0f)
                    close()
                    moveTo(13.0f, 9.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(13.0f)
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
                    moveTo(13.0f, 11.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(15.175f)
                    lineTo(9.425f, 13.6f)
                    lineTo(8.0f, 15.0f)
                    lineTo(12.0f, 19.0f)
                    lineTo(16.0f, 15.0f)
                    lineTo(14.6f, 13.575f)
                    lineTo(13.0f, 15.175f)
                    verticalLineTo(11.0f)
                    close()
                }
            }
                .build()
        return _movedown!!
    }

private var _movedown: ImageVector? = null
