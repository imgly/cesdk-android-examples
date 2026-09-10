package ly.img.editor.showcases.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.DockCreateWithAi: ImageVector
    get() {
        if (dockCreateWithAi != null) {
            return dockCreateWithAi!!
        }
        dockCreateWithAi = ImageVector.Builder(
            name = "DockCreateWithAi",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 9f)
                lineTo(17.75f, 6.25f)
                lineTo(15f, 5f)
                lineTo(17.75f, 3.75f)
                lineTo(19f, 1f)
                lineTo(20.25f, 3.75f)
                lineTo(23f, 5f)
                lineTo(20.25f, 6.25f)
                lineTo(19f, 9f)
                close()
                moveTo(19f, 23f)
                lineTo(17.75f, 20.25f)
                lineTo(15f, 19f)
                lineTo(17.75f, 17.75f)
                lineTo(19f, 15f)
                lineTo(20.25f, 17.75f)
                lineTo(23f, 19f)
                lineTo(20.25f, 20.25f)
                lineTo(19f, 23f)
                close()
                moveTo(9f, 20f)
                lineTo(6.5f, 14.5f)
                lineTo(1f, 12f)
                lineTo(6.5f, 9.5f)
                lineTo(9f, 4f)
                lineTo(11.5f, 9.5f)
                lineTo(17f, 12f)
                lineTo(11.5f, 14.5f)
                lineTo(9f, 20f)
                close()
            }
        }.build()

        return dockCreateWithAi!!
    }

private var dockCreateWithAi: ImageVector? = null
