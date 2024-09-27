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

val IconPack.Photolibraryoutline: ImageVector
    get() {
        if (_photolibraryoutline != null) {
            return _photolibraryoutline!!
        }
        _photolibraryoutline =
            Builder(
                name = "Photolibraryoutline",
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
                    moveTo(20.0f, 4.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(20.0f)
                    close()
                    moveTo(20.0f, 2.0f)
                    horizontalLineTo(8.0f)
                    curveTo(6.9f, 2.0f, 6.0f, 2.9f, 6.0f, 4.0f)
                    verticalLineTo(16.0f)
                    curveTo(6.0f, 17.1f, 6.9f, 18.0f, 8.0f, 18.0f)
                    horizontalLineTo(20.0f)
                    curveTo(21.1f, 18.0f, 22.0f, 17.1f, 22.0f, 16.0f)
                    verticalLineTo(4.0f)
                    curveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f)
                    close()
                    moveTo(11.5f, 11.67f)
                    lineTo(13.19f, 13.93f)
                    lineTo(15.67f, 10.83f)
                    lineTo(19.0f, 15.0f)
                    horizontalLineTo(9.0f)
                    lineTo(11.5f, 11.67f)
                    close()
                    moveTo(2.0f, 6.0f)
                    verticalLineTo(20.0f)
                    curveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(2.0f)
                    close()
                }
            }
                .build()
        return _photolibraryoutline!!
    }

private var _photolibraryoutline: ImageVector? = null
