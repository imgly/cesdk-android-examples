package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.FlashOn: ImageVector
    get() {
        if (`_flash-on` != null) {
            return `_flash-on`!!
        }
        `_flash-on` =
            Builder(
                name = "Flash-on",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(10.0f, 22.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(2.0f)
                    horizontalLineTo(17.0f)
                    lineTo(15.0f, 9.0f)
                    horizontalLineTo(19.0f)
                    lineTo(10.0f, 22.0f)
                    close()
                }
            }
                .build()
        return `_flash-on`!!
    }

private var `_flash-on`: ImageVector? = null
