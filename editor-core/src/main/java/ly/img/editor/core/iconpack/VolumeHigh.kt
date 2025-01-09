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

val IconPack.VolumeHigh: ImageVector
    get() {
        if (_volumehigh != null) {
            return _volumehigh!!
        }
        _volumehigh =
            Builder(
                name = "Volumehigh",
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
                    moveTo(14.0f, 3.2305f)
                    verticalLineTo(5.2905f)
                    curveTo(16.89f, 6.1505f, 19.0f, 8.8305f, 19.0f, 12.0005f)
                    curveTo(19.0f, 15.1705f, 16.89f, 17.8405f, 14.0f, 18.7005f)
                    verticalLineTo(20.7705f)
                    curveTo(18.0f, 19.8605f, 21.0f, 16.2805f, 21.0f, 12.0005f)
                    curveTo(21.0f, 7.7205f, 18.0f, 4.1405f, 14.0f, 3.2305f)
                    close()
                    moveTo(16.5f, 12.0005f)
                    curveTo(16.5f, 10.2305f, 15.5f, 8.7105f, 14.0f, 7.9705f)
                    verticalLineTo(16.0005f)
                    curveTo(15.5f, 15.2905f, 16.5f, 13.7605f, 16.5f, 12.0005f)
                    close()
                    moveTo(3.0f, 9.0005f)
                    verticalLineTo(15.0005f)
                    horizontalLineTo(7.0f)
                    lineTo(12.0f, 20.0005f)
                    verticalLineTo(4.0005f)
                    lineTo(7.0f, 9.0005f)
                    horizontalLineTo(3.0f)
                    close()
                }
            }
                .build()
        return _volumehigh!!
    }

private var _volumehigh: ImageVector? = null
