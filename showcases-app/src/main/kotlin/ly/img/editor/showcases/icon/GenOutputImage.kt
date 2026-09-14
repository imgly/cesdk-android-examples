package ly.img.editor.showcases.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.GenOutputImage: ImageVector
    get() {
        if (genOutputImage != null) {
            return genOutputImage!!
        }
        genOutputImage = ImageVector.Builder(
            name = "GenOutputImage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF444655))) {
                moveTo(6f, 18f)
                lineTo(10f, 14f)
                lineTo(11.8f, 15.775f)
                lineTo(14f, 13f)
                lineTo(18f, 18f)
                horizontalLineTo(6f)
                close()
                moveTo(8f, 10f)
                curveTo(7.45f, 10f, 6.979f, 9.804f, 6.588f, 9.412f)
                curveTo(6.196f, 9.021f, 6f, 8.55f, 6f, 8f)
                curveTo(6f, 7.45f, 6.196f, 6.979f, 6.588f, 6.588f)
                curveTo(6.979f, 6.196f, 7.45f, 6f, 8f, 6f)
                curveTo(8.55f, 6f, 9.021f, 6.196f, 9.413f, 6.588f)
                curveTo(9.804f, 6.979f, 10f, 7.45f, 10f, 8f)
                curveTo(10f, 8.55f, 9.804f, 9.021f, 9.413f, 9.412f)
                curveTo(9.021f, 9.804f, 8.55f, 10f, 8f, 10f)
                close()
                moveTo(5f, 22f)
                curveTo(4.167f, 22f, 3.458f, 21.708f, 2.875f, 21.125f)
                curveTo(2.292f, 20.542f, 2f, 19.833f, 2f, 19f)
                verticalLineTo(5f)
                curveTo(2f, 4.167f, 2.292f, 3.458f, 2.875f, 2.875f)
                curveTo(3.458f, 2.292f, 4.167f, 2f, 5f, 2f)
                horizontalLineTo(19f)
                curveTo(19.833f, 2f, 20.542f, 2.292f, 21.125f, 2.875f)
                curveTo(21.708f, 3.458f, 22f, 4.167f, 22f, 5f)
                verticalLineTo(19f)
                curveTo(22f, 19.833f, 21.708f, 20.542f, 21.125f, 21.125f)
                curveTo(20.542f, 21.708f, 19.833f, 22f, 19f, 22f)
                horizontalLineTo(5f)
                close()
                moveTo(5f, 20f)
                horizontalLineTo(19f)
                curveTo(19.283f, 20f, 19.521f, 19.904f, 19.712f, 19.712f)
                curveTo(19.904f, 19.521f, 20f, 19.283f, 20f, 19f)
                verticalLineTo(5f)
                curveTo(20f, 4.717f, 19.904f, 4.479f, 19.712f, 4.287f)
                curveTo(19.521f, 4.096f, 19.283f, 4f, 19f, 4f)
                horizontalLineTo(5f)
                curveTo(4.717f, 4f, 4.479f, 4.096f, 4.287f, 4.287f)
                curveTo(4.096f, 4.479f, 4f, 4.717f, 4f, 5f)
                verticalLineTo(19f)
                curveTo(4f, 19.283f, 4.096f, 19.521f, 4.287f, 19.712f)
                curveTo(4.479f, 19.904f, 4.717f, 20f, 5f, 20f)
                close()
            }
        }.build()

        return genOutputImage!!
    }

private var genOutputImage: ImageVector? = null
