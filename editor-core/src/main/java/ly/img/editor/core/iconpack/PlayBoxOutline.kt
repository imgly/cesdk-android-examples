package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Deprecated("Use IconPack.PlayBoxOutline instead.", ReplaceWith("IconPack.PlayBoxOutline"))
val IconPack.Playboxoutline: ImageVector
    get() = PlayBoxOutline

val IconPack.PlayBoxOutline: ImageVector
    get() {
        if (`_play-box-outline` != null) {
            return `_play-box-outline`!!
        }
        `_play-box-outline` =
            Builder(
                name = "Play-box-outline",
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
                    moveTo(19.0f, 19.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(19.0f)
                    moveTo(19.0f, 3.0f)
                    horizontalLineTo(5.0f)
                    curveTo(4.4696f, 3.0f, 3.9609f, 3.2107f, 3.5858f, 3.5858f)
                    curveTo(3.2107f, 3.9609f, 3.0f, 4.4696f, 3.0f, 5.0f)
                    verticalLineTo(19.0f)
                    curveTo(3.0f, 19.5304f, 3.2107f, 20.0391f, 3.5858f, 20.4142f)
                    curveTo(3.9609f, 20.7893f, 4.4696f, 21.0f, 5.0f, 21.0f)
                    horizontalLineTo(19.0f)
                    curveTo(19.5304f, 21.0f, 20.0391f, 20.7893f, 20.4142f, 20.4142f)
                    curveTo(20.7893f, 20.0391f, 21.0f, 19.5304f, 21.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(21.0f, 4.4696f, 20.7893f, 3.9609f, 20.4142f, 3.5858f)
                    curveTo(20.0391f, 3.2107f, 19.5304f, 3.0f, 19.0f, 3.0f)
                    close()
                    moveTo(10.0f, 8.0f)
                    verticalLineTo(16.0f)
                    lineTo(15.0f, 12.0f)
                    lineTo(10.0f, 8.0f)
                    close()
                }
            }
                .build()
        return `_play-box-outline`!!
    }

private var `_play-box-outline`: ImageVector? = null
