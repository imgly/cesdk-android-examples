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

val IconPack.AsClip: ImageVector
    get() {
        if (_asclip != null) {
            return _asclip!!
        }
        _asclip =
            Builder(
                name = "Asclip",
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
                    moveTo(6.0f, 4.0f)
                    horizontalLineTo(18.0f)
                    curveTo(18.5523f, 4.0f, 19.0f, 4.4477f, 19.0f, 5.0f)
                    verticalLineTo(13.0f)
                    curveTo(19.0f, 13.5523f, 18.5523f, 14.0f, 18.0f, 14.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(18.0f)
                    curveTo(19.6569f, 16.0f, 21.0f, 14.6569f, 21.0f, 13.0f)
                    verticalLineTo(5.0f)
                    curveTo(21.0f, 3.3431f, 19.6569f, 2.0f, 18.0f, 2.0f)
                    horizontalLineTo(6.0f)
                    curveTo(4.3432f, 2.0f, 3.0f, 3.3431f, 3.0f, 5.0f)
                    verticalLineTo(13.0f)
                    curveTo(3.0f, 14.6569f, 4.3432f, 16.0f, 6.0f, 16.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(6.0f)
                    curveTo(5.4477f, 14.0f, 5.0f, 13.5523f, 5.0f, 13.0f)
                    verticalLineTo(5.0f)
                    curveTo(5.0f, 4.4477f, 5.4477f, 4.0f, 6.0f, 4.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 23.0f)
                    lineTo(16.0f, 19.0f)
                    lineTo(14.6f, 17.575f)
                    lineTo(13.0f, 19.175f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(19.175f)
                    lineTo(9.4f, 17.575f)
                    lineTo(8.0f, 19.0f)
                    lineTo(12.0f, 23.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(16.0f, 22.0f)
                    curveTo(16.0f, 20.3431f, 17.3431f, 19.0f, 19.0f, 19.0f)
                    horizontalLineTo(23.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(19.0f)
                    curveTo(18.4477f, 21.0f, 18.0f, 21.4477f, 18.0f, 22.0f)
                    horizontalLineTo(16.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(1.0f, 19.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(5.0f)
                    curveTo(5.5523f, 21.0f, 6.0f, 21.4477f, 6.0f, 22.0f)
                    horizontalLineTo(8.0f)
                    curveTo(8.0f, 20.3431f, 6.6568f, 19.0f, 5.0f, 19.0f)
                    horizontalLineTo(1.0f)
                    close()
                }
            }
                .build()
        return _asclip!!
    }

private var _asclip: ImageVector? = null
