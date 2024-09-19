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

val IconPack.Videolibraryoutline: ImageVector
    get() {
        if (_videolibraryoutline != null) {
            return _videolibraryoutline!!
        }
        _videolibraryoutline =
            Builder(
                name = "Videolibraryoutline",
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
                    moveTo(11.5f, 14.5f)
                    lineTo(18.5f, 10.0f)
                    lineTo(11.5f, 5.5f)
                    verticalLineTo(14.5f)
                    close()
                    moveTo(8.0f, 18.0f)
                    curveTo(7.45f, 18.0f, 6.9793f, 17.8043f, 6.588f, 17.413f)
                    curveTo(6.1967f, 17.0217f, 6.0007f, 16.5507f, 6.0f, 16.0f)
                    verticalLineTo(4.0f)
                    curveTo(6.0f, 3.45f, 6.196f, 2.9793f, 6.588f, 2.588f)
                    curveTo(6.98f, 2.1967f, 7.4507f, 2.0007f, 8.0f, 2.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.55f, 2.0f, 21.021f, 2.196f, 21.413f, 2.588f)
                    curveTo(21.805f, 2.98f, 22.0007f, 3.4507f, 22.0f, 4.0f)
                    verticalLineTo(16.0f)
                    curveTo(22.0f, 16.55f, 21.8043f, 17.021f, 21.413f, 17.413f)
                    curveTo(21.0217f, 17.805f, 20.5507f, 18.0007f, 20.0f, 18.0f)
                    horizontalLineTo(8.0f)
                    close()
                    moveTo(8.0f, 16.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(16.0f)
                    close()
                    moveTo(4.0f, 22.0f)
                    curveTo(3.45f, 22.0f, 2.9793f, 21.8043f, 2.588f, 21.413f)
                    curveTo(2.1967f, 21.0217f, 2.0007f, 20.5507f, 2.0f, 20.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(4.0f)
                    close()
                }
            }
                .build()
        return _videolibraryoutline!!
    }

private var _videolibraryoutline: ImageVector? = null
