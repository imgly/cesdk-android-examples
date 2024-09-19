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

val IconPack.Checkcircleoutline: ImageVector
    get() {
        if (_checkcircleoutline != null) {
            return _checkcircleoutline!!
        }
        _checkcircleoutline =
            Builder(
                name = "Checkcircleoutline",
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
                    moveTo(10.6f, 16.6f)
                    lineTo(17.65f, 9.55f)
                    lineTo(16.25f, 8.15f)
                    lineTo(10.6f, 13.8f)
                    lineTo(7.75f, 10.95f)
                    lineTo(6.35f, 12.35f)
                    lineTo(10.6f, 16.6f)
                    close()
                    moveTo(12.0f, 22.0f)
                    curveTo(10.6167f, 22.0f, 9.3167f, 21.7373f, 8.1f, 21.212f)
                    curveTo(6.8833f, 20.6867f, 5.825f, 19.9743f, 4.925f, 19.075f)
                    curveTo(4.025f, 18.175f, 3.3127f, 17.1167f, 2.788f, 15.9f)
                    curveTo(2.2633f, 14.6833f, 2.0007f, 13.3833f, 2.0f, 12.0f)
                    curveTo(2.0f, 10.6167f, 2.2627f, 9.3167f, 2.788f, 8.1f)
                    curveTo(3.3133f, 6.8833f, 4.0257f, 5.825f, 4.925f, 4.925f)
                    curveTo(5.825f, 4.025f, 6.8833f, 3.3127f, 8.1f, 2.788f)
                    curveTo(9.3167f, 2.2633f, 10.6167f, 2.0007f, 12.0f, 2.0f)
                    curveTo(13.3833f, 2.0f, 14.6833f, 2.2627f, 15.9f, 2.788f)
                    curveTo(17.1167f, 3.3133f, 18.175f, 4.0257f, 19.075f, 4.925f)
                    curveTo(19.975f, 5.825f, 20.6877f, 6.8833f, 21.213f, 8.1f)
                    curveTo(21.7383f, 9.3167f, 22.0007f, 10.6167f, 22.0f, 12.0f)
                    curveTo(22.0f, 13.3833f, 21.7373f, 14.6833f, 21.212f, 15.9f)
                    curveTo(20.6867f, 17.1167f, 19.9743f, 18.175f, 19.075f, 19.075f)
                    curveTo(18.175f, 19.975f, 17.1167f, 20.6877f, 15.9f, 21.213f)
                    curveTo(14.6833f, 21.7383f, 13.3833f, 22.0007f, 12.0f, 22.0f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveTo(14.2333f, 20.0f, 16.125f, 19.225f, 17.675f, 17.675f)
                    curveTo(19.225f, 16.125f, 20.0f, 14.2333f, 20.0f, 12.0f)
                    curveTo(20.0f, 9.7667f, 19.225f, 7.875f, 17.675f, 6.325f)
                    curveTo(16.125f, 4.775f, 14.2333f, 4.0f, 12.0f, 4.0f)
                    curveTo(9.7667f, 4.0f, 7.875f, 4.775f, 6.325f, 6.325f)
                    curveTo(4.775f, 7.875f, 4.0f, 9.7667f, 4.0f, 12.0f)
                    curveTo(4.0f, 14.2333f, 4.775f, 16.125f, 6.325f, 17.675f)
                    curveTo(7.875f, 19.225f, 9.7667f, 20.0f, 12.0f, 20.0f)
                    close()
                }
            }
                .build()
        return _checkcircleoutline!!
    }

private var _checkcircleoutline: ImageVector? = null
