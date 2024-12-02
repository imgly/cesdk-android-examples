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

@Deprecated("Use IconPack.PlayBox instead.", ReplaceWith("IconPack.PlayBox"))
val IconPack.Playbox: ImageVector
    get() = PlayBox

val IconPack.PlayBox: ImageVector
    get() {
        if (`_play-box` != null) {
            return `_play-box`!!
        }
        `_play-box` =
            Builder(
                name = "Play-box",
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
                    moveTo(19.0f, 3.0f)
                    horizontalLineTo(5.0f)
                    curveTo(3.89f, 3.0f, 3.0f, 3.89f, 3.0f, 5.0f)
                    verticalLineTo(19.0f)
                    curveTo(3.0f, 20.1f, 3.9f, 21.0f, 5.0f, 21.0f)
                    horizontalLineTo(19.0f)
                    curveTo(20.1f, 21.0f, 21.0f, 20.1f, 21.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(21.0f, 4.4696f, 20.7893f, 3.9609f, 20.4142f, 3.5858f)
                    curveTo(20.0391f, 3.2107f, 19.5304f, 3.0f, 19.0f, 3.0f)
                    close()
                    moveTo(10.0f, 16.0f)
                    verticalLineTo(8.0f)
                    lineTo(15.0f, 12.0f)
                }
            }
                .build()
        return `_play-box`!!
    }

private var `_play-box`: ImageVector? = null
