package ly.img.editor.core.iconpack

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

val IconPack.AddAudio: ImageVector
    get() {
        if (_addaudio != null) {
            return _addaudio!!
        }
        _addaudio =
            Builder(
                name = "Addaudio",
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
                    moveTo(12.0004f, 4.9283f)
                    curveTo(12.0001f, 4.9522f, 12.0f, 4.9761f, 12.0f, 5.0f)
                    curveTo(12.0f, 6.0086f, 12.2133f, 6.9673f, 12.5973f, 7.8336f)
                    lineTo(9.0f, 8.5998f)
                    verticalLineTo(18.4998f)
                    curveTo(9.0f, 19.4281f, 8.6313f, 20.3183f, 7.9749f, 20.9747f)
                    curveTo(7.3185f, 21.6311f, 6.4283f, 21.9998f, 5.5f, 21.9998f)
                    curveTo(4.5717f, 21.9998f, 3.6815f, 21.6311f, 3.0251f, 20.9747f)
                    curveTo(2.3688f, 20.3183f, 2.0f, 19.4281f, 2.0f, 18.4998f)
                    curveTo(2.0f, 17.5716f, 2.3688f, 16.6813f, 3.0251f, 16.025f)
                    curveTo(3.6815f, 15.3686f, 4.5717f, 14.9998f, 5.5f, 14.9998f)
                    curveTo(6.04f, 14.9998f, 6.55f, 15.1198f, 7.0f, 15.3398f)
                    verticalLineTo(5.9998f)
                    lineTo(12.0004f, 4.9283f)
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
                    moveTo(21.0f, 11.7101f)
                    curveTo(20.3663f, 11.8987f, 19.695f, 12.0f, 19.0f, 12.0f)
                    verticalLineTo(13.3398f)
                    curveTo(18.55f, 13.1198f, 18.04f, 12.9998f, 17.5f, 12.9998f)
                    curveTo(16.5717f, 12.9998f, 15.6815f, 13.3686f, 15.0251f, 14.025f)
                    curveTo(14.3687f, 14.6813f, 14.0f, 15.5716f, 14.0f, 16.4998f)
                    curveTo(14.0f, 17.4281f, 14.3687f, 18.3183f, 15.0251f, 18.9747f)
                    curveTo(15.6815f, 19.6311f, 16.5717f, 19.9998f, 17.5f, 19.9998f)
                    curveTo(18.4283f, 19.9998f, 19.3185f, 19.6311f, 19.9749f, 18.9747f)
                    curveTo(20.6313f, 18.3183f, 21.0f, 17.4281f, 21.0f, 16.4998f)
                    verticalLineTo(11.7101f)
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
                    moveTo(19.0f, 0.0f)
                    curveTo(16.2386f, 0.0f, 14.0f, 2.2386f, 14.0f, 5.0f)
                    curveTo(14.0f, 7.7614f, 16.2386f, 10.0f, 19.0f, 10.0f)
                    curveTo(21.7614f, 10.0f, 24.0f, 7.7614f, 24.0f, 5.0f)
                    curveTo(24.0f, 2.2386f, 21.7614f, 0.0f, 19.0f, 0.0f)
                    close()
                    moveTo(18.5f, 4.5f)
                    verticalLineTo(2.0f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(4.5f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(4.5f)
                    horizontalLineTo(18.5f)
                    close()
                }
            }
                .build()
        return _addaudio!!
    }

private var _addaudio: ImageVector? = null
