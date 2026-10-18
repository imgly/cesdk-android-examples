package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

val IconPack.Style: ImageVector
    get() {
        if (_Style != null) {
            return _Style!!
        }
        _Style = ImageVector.Builder(
            name = "Style",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(2.53f, 19.65f)
                lineToRelative(1.34f, 0.56f)
                verticalLineToRelative(-9.03f)
                lineToRelative(-2.43f, 5.86f)
                curveToRelative(-0.41f, 1.02f, 0.08f, 2.19f, 1.09f, 2.61f)
                close()
                moveTo(22.03f, 15.95f)
                lineTo(17.07f, 3.98f)
                arcToRelative(2.013f, 2.013f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.81f, -1.23f)
                curveToRelative(-0.26f, 0f, -0.53f, 0.04f, -0.79f, 0.15f)
                lineTo(7.1f, 5.95f)
                arcToRelative(1.999f, 1.999f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.08f, 2.6f)
                lineToRelative(4.96f, 11.97f)
                arcToRelative(1.998f, 1.998f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.6f, 1.08f)
                lineToRelative(7.36f, -3.05f)
                arcToRelative(1.994f, 1.994f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.09f, -2.6f)
                close()
                moveTo(7.88f, 8.75f)
                curveToRelative(-0.55f, 0f, -1f, -0.45f, -1f, -1f)
                reflectiveCurveToRelative(0.45f, -1f, 1f, -1f)
                reflectiveCurveToRelative(1f, 0.45f, 1f, 1f)
                reflectiveCurveToRelative(-0.45f, 1f, -1f, 1f)
                close()
                moveTo(5.88f, 19.75f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(1.45f)
                lineToRelative(-3.45f, -8.34f)
                verticalLineToRelative(6.34f)
                close()
            }
        }.build()

        return _Style!!
    }

@Suppress("ObjectPropertyName")
private var _Style: ImageVector? = null
