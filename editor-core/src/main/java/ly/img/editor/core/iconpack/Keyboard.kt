package ly.img.editor.core.iconpack

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

val IconPack.Keyboard: ImageVector
    get() {
        if (_keyboard != null) {
            return _keyboard!!
        }
        _keyboard =
            Builder(
                name = "Keyboard",
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
                    moveTo(4.0f, 19.0f)
                    curveTo(3.45f, 19.0f, 2.9793f, 18.8043f, 2.588f, 18.413f)
                    curveTo(2.196f, 18.021f, 2.0f, 17.55f, 2.0f, 17.0f)
                    verticalLineTo(7.0f)
                    curveTo(2.0f, 6.45f, 2.196f, 5.9793f, 2.588f, 5.588f)
                    curveTo(2.9793f, 5.196f, 3.45f, 5.0f, 4.0f, 5.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.55f, 5.0f, 21.021f, 5.196f, 21.413f, 5.588f)
                    curveTo(21.8043f, 5.9793f, 22.0f, 6.45f, 22.0f, 7.0f)
                    verticalLineTo(17.0f)
                    curveTo(22.0f, 17.55f, 21.8043f, 18.021f, 21.413f, 18.413f)
                    curveTo(21.021f, 18.8043f, 20.55f, 19.0f, 20.0f, 19.0f)
                    horizontalLineTo(4.0f)
                    close()
                    moveTo(11.0f, 10.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(10.0f)
                    close()
                    moveTo(11.0f, 13.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(13.0f)
                    close()
                    moveTo(8.0f, 10.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(10.0f)
                    close()
                    moveTo(8.0f, 13.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(13.0f)
                    close()
                    moveTo(5.0f, 13.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(13.0f)
                    close()
                    moveTo(5.0f, 10.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(10.0f)
                    close()
                    moveTo(8.0f, 16.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(16.0f)
                    close()
                    moveTo(14.0f, 13.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(13.0f)
                    close()
                    moveTo(14.0f, 10.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(10.0f)
                    close()
                    moveTo(17.0f, 13.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(13.0f)
                    close()
                    moveTo(17.0f, 10.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(10.0f)
                    close()
                }
            }.build()
        return _keyboard!!
    }

private var _keyboard: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Keyboard.IconPreview()
