package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.RepeatOn: ImageVector
    get() {
        if (_RepeatOn != null) {
            return _RepeatOn!!
        }
        _RepeatOn = ImageVector.Builder(
            name = "RepeatOn",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF46464F))) {
                moveTo(4f, 10f)
                curveTo(4f, 7.791f, 5.791f, 6f, 8f, 6f)
                horizontalLineTo(16f)
                curveTo(18.209f, 6f, 20f, 7.791f, 20f, 10f)
                verticalLineTo(14f)
                curveTo(20f, 16.209f, 18.209f, 18f, 16f, 18f)
                horizontalLineTo(13.8f)
                lineTo(14.7f, 18.9f)
                curveTo(15.087f, 19.287f, 15.087f, 19.913f, 14.7f, 20.3f)
                curveTo(14.313f, 20.687f, 13.687f, 20.687f, 13.3f, 20.3f)
                lineTo(10.707f, 17.707f)
                curveTo(10.317f, 17.317f, 10.317f, 16.683f, 10.707f, 16.293f)
                lineTo(13.3f, 13.7f)
                curveTo(13.687f, 13.313f, 14.313f, 13.313f, 14.7f, 13.7f)
                curveTo(15.087f, 14.087f, 15.087f, 14.713f, 14.7f, 15.1f)
                lineTo(13.8f, 16f)
                horizontalLineTo(16f)
                curveTo(17.105f, 16f, 18f, 15.105f, 18f, 14f)
                verticalLineTo(10f)
                curveTo(18f, 8.895f, 17.105f, 8f, 16f, 8f)
                horizontalLineTo(8f)
                curveTo(6.895f, 8f, 6f, 8.895f, 6f, 10f)
                verticalLineTo(14f)
                curveTo(6f, 15.105f, 6.895f, 16f, 8f, 16f)
                curveTo(8.552f, 16f, 9f, 16.448f, 9f, 17f)
                curveTo(9f, 17.552f, 8.552f, 18f, 8f, 18f)
                curveTo(5.791f, 18f, 4f, 16.209f, 4f, 14f)
                verticalLineTo(10f)
                close()
            }
        }.build()

        return _RepeatOn!!
    }

@Suppress("ObjectPropertyName")
private var _RepeatOn: ImageVector? = null
