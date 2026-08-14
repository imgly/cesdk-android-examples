package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.DeleteOutline: ImageVector
    get() {
        if (_DeleteOutline != null) {
            return _DeleteOutline!!
        }
        _DeleteOutline = ImageVector.Builder(
            name = "DeleteOutline",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 9f)
                verticalLineToRelative(10f)
                horizontalLineTo(8f)
                verticalLineTo(9f)
                horizontalLineToRelative(8f)
                moveToRelative(-1.5f, -6f)
                horizontalLineToRelative(-5f)
                lineToRelative(-1f, 1f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(14f)
                verticalLineTo(4f)
                horizontalLineToRelative(-3.5f)
                lineToRelative(-1f, -1f)
                close()
                moveTo(18f, 7f)
                horizontalLineTo(6f)
                verticalLineToRelative(12f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(8f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(7f)
                close()
            }
        }.build()

        return _DeleteOutline!!
    }

@Suppress("ObjectPropertyName")
private var _DeleteOutline: ImageVector? = null
