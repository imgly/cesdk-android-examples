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

val IconPack.FlashOff: ImageVector
    get() {
        if (`_flash-off` != null) {
            return `_flash-off`!!
        }
        `_flash-off` =
            Builder(
                name = "Flash-off",
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
                    moveTo(7.0f, 2.0f)
                    horizontalLineTo(17.0f)
                    lineTo(15.0f, 9.0f)
                    horizontalLineTo(19.0f)
                    lineTo(16.075f, 13.225f)
                    lineTo(7.0f, 4.15f)
                    verticalLineTo(2.0f)
                    close()
                    moveTo(10.0f, 22.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(9.85f)
                    lineTo(1.375f, 4.225f)
                    lineTo(2.8f, 2.8f)
                    lineTo(21.2f, 21.2f)
                    lineTo(19.775f, 22.625f)
                    lineTo(13.75f, 16.6f)
                    lineTo(10.0f, 22.0f)
                    close()
                }
            }
                .build()
        return `_flash-off`!!
    }

private var `_flash-off`: ImageVector? = null
