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

val IconPack.Mic: ImageVector
    get() {
        if (_mic != null) {
            return _mic!!
        }
        _mic =
            Builder(
                name = "Mic",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth =
                24.0f,
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
                    moveTo(12.0f, 14.0f)
                    curveTo(11.1667f, 14.0f, 10.4583f, 13.7083f, 9.875f, 13.125f)
                    curveTo(9.2917f, 12.5417f, 9.0f, 11.8333f, 9.0f, 11.0f)
                    verticalLineTo(5.0f)
                    curveTo(9.0f, 4.1667f, 9.2917f, 3.4583f, 9.875f, 2.875f)
                    curveTo(10.4583f, 2.2917f, 11.1667f, 2.0f, 12.0f, 2.0f)
                    curveTo(12.8333f, 2.0f, 13.5417f, 2.2917f, 14.125f, 2.875f)
                    curveTo(14.7083f, 3.4583f, 15.0f, 4.1667f, 15.0f, 5.0f)
                    verticalLineTo(11.0f)
                    curveTo(15.0f, 11.8333f, 14.7083f, 12.5417f, 14.125f, 13.125f)
                    curveTo(13.5417f, 13.7083f, 12.8333f, 14.0f, 12.0f, 14.0f)
                    close()
                    moveTo(11.0f, 21.0f)
                    verticalLineTo(17.925f)
                    curveTo(9.2667f, 17.6917f, 7.8333f, 16.9167f, 6.7f, 15.6f)
                    curveTo(5.5667f, 14.2833f, 5.0f, 12.75f, 5.0f, 11.0f)
                    horizontalLineTo(7.0f)
                    curveTo(7.0f, 12.3833f, 7.4877f, 13.5627f, 8.463f, 14.538f)
                    curveTo(9.4383f, 15.5133f, 10.6173f, 16.0007f, 12.0f, 16.0f)
                    curveTo(13.3827f, 15.9993f, 14.562f, 15.5117f, 15.538f, 14.537f)
                    curveTo(16.514f, 13.5623f, 17.0013f, 12.3833f, 17.0f, 11.0f)
                    horizontalLineTo(19.0f)
                    curveTo(19.0f, 12.75f, 18.4333f, 14.2833f, 17.3f, 15.6f)
                    curveTo(16.1667f, 16.9167f, 14.7333f, 17.6917f, 13.0f, 17.925f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(11.0f)
                    close()
                }
            }
                .build()
        return _mic!!
    }

private var _mic: ImageVector? = null
