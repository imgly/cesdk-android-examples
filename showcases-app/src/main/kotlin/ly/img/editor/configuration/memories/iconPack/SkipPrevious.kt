package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.SkipPrevious: ImageVector
    get() {
        if (_SkipPrevious != null) {
            return _SkipPrevious!!
        }
        _SkipPrevious = ImageVector.Builder(
            name = "SkipPrevious",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(7f, 6f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                verticalLineToRelative(10f)
                curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
                reflectiveCurveToRelative(-1f, -0.45f, -1f, -1f)
                lineTo(6f, 7f)
                curveToRelative(0f, -0.55f, 0.45f, -1f, 1f, -1f)
                close()
                moveTo(10.66f, 12.82f)
                lineTo(16.43f, 16.89f)
                curveToRelative(0.66f, 0.47f, 1.58f, -0.01f, 1.58f, -0.82f)
                lineTo(18.01f, 7.93f)
                curveToRelative(0f, -0.81f, -0.91f, -1.28f, -1.58f, -0.82f)
                lineToRelative(-5.77f, 4.07f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 1.64f)
                close()
            }
        }.build()

        return _SkipPrevious!!
    }

@Suppress("ObjectPropertyName")
private var _SkipPrevious: ImageVector? = null
