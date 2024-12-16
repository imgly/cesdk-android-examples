package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.Duplicate: ImageVector
    get() {
        if (_duplicate != null) {
            return _duplicate!!
        }
        _duplicate =
            Builder(
                name = "Duplicate",
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
                    moveTo(4.0f, 22.0f)
                    curveTo(3.45f, 22.0f, 2.979f, 21.8043f, 2.587f, 21.413f)
                    curveTo(2.1957f, 21.021f, 2.0f, 20.55f, 2.0f, 20.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(22.0f)
                    close()
                    moveTo(2.0f, 18.5f)
                    verticalLineTo(16.5f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(18.5f)
                    horizontalLineTo(2.0f)
                    close()
                    moveTo(2.0f, 15.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(2.0f)
                    close()
                    moveTo(2.0f, 11.5f)
                    verticalLineTo(9.5f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(11.5f)
                    horizontalLineTo(2.0f)
                    close()
                    moveTo(2.0f, 8.0f)
                    curveTo(2.0f, 7.45f, 2.1957f, 6.9793f, 2.587f, 6.588f)
                    curveTo(2.979f, 6.196f, 3.45f, 6.0f, 4.0f, 6.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(2.0f)
                    close()
                    moveTo(5.5f, 22.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(7.5f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(5.5f)
                    close()
                    moveTo(8.0f, 18.0f)
                    curveTo(7.45f, 18.0f, 6.9793f, 17.8043f, 6.588f, 17.413f)
                    curveTo(6.196f, 17.021f, 6.0f, 16.55f, 6.0f, 16.0f)
                    verticalLineTo(4.0f)
                    curveTo(6.0f, 3.45f, 6.196f, 2.979f, 6.588f, 2.587f)
                    curveTo(6.9793f, 2.1957f, 7.45f, 2.0f, 8.0f, 2.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.55f, 2.0f, 21.021f, 2.1957f, 21.413f, 2.587f)
                    curveTo(21.8043f, 2.979f, 22.0f, 3.45f, 22.0f, 4.0f)
                    verticalLineTo(16.0f)
                    curveTo(22.0f, 16.55f, 21.8043f, 17.021f, 21.413f, 17.413f)
                    curveTo(21.021f, 17.8043f, 20.55f, 18.0f, 20.0f, 18.0f)
                    horizontalLineTo(8.0f)
                    close()
                    moveTo(8.0f, 16.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(16.0f)
                    close()
                    moveTo(9.0f, 22.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(9.0f)
                    close()
                    moveTo(16.0f, 22.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(18.0f)
                    curveTo(18.0f, 20.55f, 17.8043f, 21.021f, 17.413f, 21.413f)
                    curveTo(17.021f, 21.8043f, 16.55f, 22.0f, 16.0f, 22.0f)
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
                    moveTo(12.5f, 20.0f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(14.5f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(12.5f)
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
                    moveTo(15.0f, 14.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(14.0f)
                    close()
                }
            }.build()
        return _duplicate!!
    }

private var _duplicate: ImageVector? = null
