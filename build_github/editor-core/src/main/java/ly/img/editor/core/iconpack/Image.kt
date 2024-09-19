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

val IconPack.Image: ImageVector
    get() {
        if (_image != null) {
            return _image!!
        }
        _image =
            Builder(
                name = "Image",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(6.0f, 17.0f)
                    horizontalLineTo(18.0f)
                    lineTo(14.25f, 12.0f)
                    lineTo(11.25f, 16.0f)
                    lineTo(9.0f, 13.0f)
                    lineTo(6.0f, 17.0f)
                    close()
                    moveTo(5.0f, 21.0f)
                    curveTo(4.45f, 21.0f, 3.979f, 20.8043f, 3.587f, 20.413f)
                    curveTo(3.1957f, 20.021f, 3.0f, 19.55f, 3.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(3.0f, 4.45f, 3.1957f, 3.979f, 3.587f, 3.587f)
                    curveTo(3.979f, 3.1957f, 4.45f, 3.0f, 5.0f, 3.0f)
                    horizontalLineTo(19.0f)
                    curveTo(19.55f, 3.0f, 20.021f, 3.1957f, 20.413f, 3.587f)
                    curveTo(20.8043f, 3.979f, 21.0f, 4.45f, 21.0f, 5.0f)
                    verticalLineTo(19.0f)
                    curveTo(21.0f, 19.55f, 20.8043f, 20.021f, 20.413f, 20.413f)
                    curveTo(20.021f, 20.8043f, 19.55f, 21.0f, 19.0f, 21.0f)
                    horizontalLineTo(5.0f)
                    close()
                }
            }
                .build()
        return _image!!
    }

private var _image: ImageVector? = null
