package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.DualCameraOutline: ImageVector
    get() {
        if (`_dual-camera-outline` != null) {
            return `_dual-camera-outline`!!
        }
        `_dual-camera-outline` =
            Builder(
                name = "Dual-camera-outline",
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
                    moveTo(11.5858f, 2.0f)
                    lineTo(9.5858f, 4.0f)
                    horizontalLineTo(8.0f)
                    curveTo(6.8954f, 4.0f, 6.0f, 4.8954f, 6.0f, 6.0f)
                    horizontalLineTo(10.4142f)
                    lineTo(12.4142f, 4.0f)
                    horizontalLineTo(15.5858f)
                    lineTo(17.5858f, 6.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(20.0f)
                    curveTo(21.1046f, 17.0f, 22.0f, 16.1046f, 22.0f, 15.0f)
                    verticalLineTo(6.0f)
                    curveTo(22.0f, 4.8954f, 21.1046f, 4.0f, 20.0f, 4.0f)
                    horizontalLineTo(18.4142f)
                    lineTo(16.4142f, 2.0f)
                    horizontalLineTo(11.5858f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(14.0f, 15.0f)
                    curveTo(14.0f, 17.2091f, 12.2091f, 19.0f, 10.0f, 19.0f)
                    curveTo(7.7909f, 19.0f, 6.0f, 17.2091f, 6.0f, 15.0f)
                    curveTo(6.0f, 12.7909f, 7.7909f, 11.0f, 10.0f, 11.0f)
                    curveTo(12.2091f, 11.0f, 14.0f, 12.7909f, 14.0f, 15.0f)
                    close()
                    moveTo(12.0f, 15.0f)
                    curveTo(12.0f, 16.1046f, 11.1046f, 17.0f, 10.0f, 17.0f)
                    curveTo(8.8954f, 17.0f, 8.0f, 16.1046f, 8.0f, 15.0f)
                    curveTo(8.0f, 13.8954f, 8.8954f, 13.0f, 10.0f, 13.0f)
                    curveTo(11.1046f, 13.0f, 12.0f, 13.8954f, 12.0f, 15.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(7.5858f, 7.0f)
                    lineTo(5.5858f, 9.0f)
                    horizontalLineTo(4.0f)
                    curveTo(2.8954f, 9.0f, 2.0f, 9.8954f, 2.0f, 11.0f)
                    verticalLineTo(20.0f)
                    curveTo(2.0f, 21.1046f, 2.8954f, 22.0f, 4.0f, 22.0f)
                    horizontalLineTo(16.0f)
                    curveTo(17.1046f, 22.0f, 18.0f, 21.1046f, 18.0f, 20.0f)
                    verticalLineTo(11.0f)
                    curveTo(18.0f, 9.8954f, 17.1046f, 9.0f, 16.0f, 9.0f)
                    horizontalLineTo(14.4142f)
                    lineTo(12.4142f, 7.0f)
                    horizontalLineTo(7.5858f)
                    close()
                    moveTo(8.4142f, 9.0f)
                    horizontalLineTo(11.5858f)
                    lineTo(13.5858f, 11.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(6.4142f)
                    lineTo(8.4142f, 9.0f)
                    close()
                }
            }
                .build()
        return `_dual-camera-outline`!!
    }

private var `_dual-camera-outline`: ImageVector? = null
