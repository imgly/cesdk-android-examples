package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.PlayArrow: ImageVector
    get() {
        if (_PlayArrow != null) {
            return _PlayArrow!!
        }
        _PlayArrow = ImageVector.Builder(
            name = "PlayArrow",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(8f, 6.82f)
                verticalLineToRelative(10.36f)
                curveToRelative(0f, 0.79f, 0.87f, 1.27f, 1.54f, 0.84f)
                lineToRelative(8.14f, -5.18f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -1.69f)
                lineTo(9.54f, 5.98f)
                arcTo(0.998f, 0.998f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8f, 6.82f)
                close()
            }
        }.build()

        return _PlayArrow!!
    }

@Suppress("ObjectPropertyName")
private var _PlayArrow: ImageVector? = null
