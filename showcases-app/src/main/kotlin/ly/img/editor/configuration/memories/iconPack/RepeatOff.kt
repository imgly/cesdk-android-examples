package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.RepeatOff: ImageVector
    get() {
        if (_RepeatOff != null) {
            return _RepeatOff!!
        }
        _RepeatOff = ImageVector.Builder(
            name = "RepeatOff",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF46464F)),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(4.905f, 3.635f)
                curveTo(4.554f, 3.284f, 3.986f, 3.284f, 3.635f, 3.635f)
                curveTo(3.284f, 3.986f, 3.284f, 4.554f, 3.635f, 4.905f)
                lineTo(5.56f, 6.83f)
                curveTo(4.611f, 7.561f, 4f, 8.709f, 4f, 10f)
                verticalLineTo(14f)
                curveTo(4f, 16.209f, 5.791f, 18f, 8f, 18f)
                curveTo(8.552f, 18f, 9f, 17.552f, 9f, 17f)
                curveTo(9f, 16.448f, 8.552f, 16f, 8f, 16f)
                curveTo(6.895f, 16f, 6f, 15.105f, 6f, 14f)
                verticalLineTo(10f)
                curveTo(6f, 9.26f, 6.402f, 8.614f, 6.998f, 8.268f)
                lineTo(12.865f, 14.135f)
                lineTo(10.707f, 16.293f)
                curveTo(10.317f, 16.683f, 10.317f, 17.317f, 10.707f, 17.707f)
                lineTo(13.3f, 20.3f)
                curveTo(13.687f, 20.687f, 14.313f, 20.687f, 14.7f, 20.3f)
                curveTo(15.087f, 19.913f, 15.087f, 19.287f, 14.7f, 18.9f)
                lineTo(13.8f, 18f)
                horizontalLineTo(16f)
                curveTo(16.229f, 18f, 16.455f, 17.981f, 16.674f, 17.944f)
                lineTo(19.095f, 20.365f)
                curveTo(19.446f, 20.716f, 20.014f, 20.716f, 20.365f, 20.365f)
                curveTo(20.716f, 20.014f, 20.716f, 19.446f, 20.365f, 19.095f)
                lineTo(7.326f, 6.056f)
                lineTo(4.905f, 3.635f)
                close()
                moveTo(14.265f, 15.535f)
                lineTo(14.73f, 16f)
                horizontalLineTo(13.8f)
                lineTo(14.265f, 15.535f)
                close()
            }
            path(fill = SolidColor(Color(0xFF46464F))) {
                moveTo(9.81f, 6f)
                lineTo(11.81f, 8f)
                horizontalLineTo(16f)
                curveTo(17.105f, 8f, 18f, 8.895f, 18f, 10f)
                verticalLineTo(14f)
                curveTo(18f, 14.061f, 17.997f, 14.122f, 17.992f, 14.182f)
                lineTo(19.585f, 15.775f)
                curveTo(19.851f, 15.24f, 20f, 14.638f, 20f, 14f)
                verticalLineTo(10f)
                curveTo(20f, 7.791f, 18.209f, 6f, 16f, 6f)
                horizontalLineTo(9.81f)
                close()
            }
        }.build()

        return _RepeatOff!!
    }

@Suppress("ObjectPropertyName")
private var _RepeatOff: ImageVector? = null
