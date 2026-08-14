package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.ArrowForward: ImageVector
    get() {
        if (_ArrowForward != null) {
            return _ArrowForward!!
        }
        _ArrowForward = ImageVector.Builder(
            name = "ArrowForward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 13f)
                horizontalLineToRelative(11.17f)
                lineToRelative(-4.88f, 4.88f)
                curveToRelative(-0.39f, 0.39f, -0.39f, 1.03f, 0f, 1.42f)
                curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0f)
                lineToRelative(6.59f, -6.59f)
                arcToRelative(0.996f, 0.996f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -1.41f)
                lineToRelative(-6.58f, -6.6f)
                arcToRelative(0.996f, 0.996f, 0f, isMoreThanHalf = true, isPositiveArc = false, -1.41f, 1.41f)
                lineTo(16.17f, 11f)
                horizontalLineTo(5f)
                curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
                reflectiveCurveToRelative(0.45f, 1f, 1f, 1f)
                close()
            }
        }.build()

        return _ArrowForward!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowForward: ImageVector? = null
