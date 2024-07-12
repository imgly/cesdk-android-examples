package ly.img.editor.core.ui.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Flip: ImageVector
    get() {
        if (_flip != null) {
            return _flip!!
        }
        _flip =
            Builder(
                name = "Flip",
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
                    moveTo(9.0f, 21.0f)
                    horizontalLineTo(5.0f)
                    curveTo(4.45f, 21.0f, 3.979f, 20.8043f, 3.587f, 20.413f)
                    curveTo(3.1957f, 20.021f, 3.0f, 19.55f, 3.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(3.0f, 4.45f, 3.1957f, 3.979f, 3.587f, 3.587f)
                    curveTo(3.979f, 3.1957f, 4.45f, 3.0f, 5.0f, 3.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(21.0f)
                    close()
                    moveTo(11.0f, 23.0f)
                    verticalLineTo(1.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(23.0f)
                    horizontalLineTo(11.0f)
                    close()
                    moveTo(19.0f, 5.0f)
                    verticalLineTo(3.0f)
                    curveTo(19.55f, 3.0f, 20.021f, 3.1957f, 20.413f, 3.587f)
                    curveTo(20.8043f, 3.979f, 21.0f, 4.45f, 21.0f, 5.0f)
                    horizontalLineTo(19.0f)
                    close()
                    moveTo(19.0f, 13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(19.0f)
                    close()
                    moveTo(19.0f, 21.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(21.0f)
                    curveTo(21.0f, 19.55f, 20.8043f, 20.021f, 20.413f, 20.413f)
                    curveTo(20.021f, 20.8043f, 19.55f, 21.0f, 19.0f, 21.0f)
                    close()
                    moveTo(19.0f, 9.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(19.0f)
                    close()
                    moveTo(19.0f, 17.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(19.0f)
                    close()
                    moveTo(15.0f, 21.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(15.0f)
                    close()
                    moveTo(15.0f, 5.0f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(15.0f)
                    close()
                }
            }.build()
        return _flip!!
    }

private var _flip: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Flip.IconPreview()
