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

val IconPack.FlipCamera: ImageVector
    get() {
        if (`_flip-camera` != null) {
            return `_flip-camera`!!
        }
        `_flip-camera` =
            Builder(
                name = "Flip-camera",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    moveTo(12.0f, 22.0f)
                    curveTo(9.6167f, 22.0f, 7.5083f, 21.25f, 5.675f, 19.75f)
                    curveTo(3.8417f, 18.25f, 2.6833f, 16.3333f, 2.2f, 14.0f)
                    horizontalLineTo(4.25f)
                    curveTo(4.7167f, 15.7667f, 5.6667f, 17.2083f, 7.1f, 18.325f)
                    curveTo(8.5333f, 19.4417f, 10.1667f, 20.0f, 12.0f, 20.0f)
                    curveTo(13.4333f, 20.0f, 14.7667f, 19.646f, 16.0f, 18.938f)
                    curveTo(17.2333f, 18.2293f, 18.2f, 17.25f, 18.9f, 16.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(18.0f)
                    curveTo(19.05f, 19.2667f, 17.875f, 20.25f, 16.475f, 20.95f)
                    curveTo(15.075f, 21.65f, 13.5833f, 22.0f, 12.0f, 22.0f)
                    close()
                    moveTo(12.0f, 15.0f)
                    curveTo(11.1667f, 15.0f, 10.4583f, 14.7083f, 9.875f, 14.125f)
                    curveTo(9.2917f, 13.5417f, 9.0f, 12.8333f, 9.0f, 12.0f)
                    curveTo(9.0f, 11.1667f, 9.2917f, 10.4583f, 9.875f, 9.875f)
                    curveTo(10.4583f, 9.2917f, 11.1667f, 9.0f, 12.0f, 9.0f)
                    curveTo(12.8333f, 9.0f, 13.5417f, 9.2917f, 14.125f, 9.875f)
                    curveTo(14.7083f, 10.4583f, 15.0f, 11.1667f, 15.0f, 12.0f)
                    curveTo(15.0f, 12.8333f, 14.7083f, 13.5417f, 14.125f, 14.125f)
                    curveTo(13.5417f, 14.7083f, 12.8333f, 15.0f, 12.0f, 15.0f)
                    close()
                    moveTo(2.0f, 10.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(6.0f)
                    curveTo(4.95f, 4.7333f, 6.125f, 3.75f, 7.525f, 3.05f)
                    curveTo(8.925f, 2.35f, 10.4167f, 2.0f, 12.0f, 2.0f)
                    curveTo(14.3833f, 2.0f, 16.4917f, 2.75f, 18.325f, 4.25f)
                    curveTo(20.1583f, 5.75f, 21.3167f, 7.6667f, 21.8f, 10.0f)
                    horizontalLineTo(19.75f)
                    curveTo(19.2833f, 8.2333f, 18.3333f, 6.7917f, 16.9f, 5.675f)
                    curveTo(15.4667f, 4.5583f, 13.8333f, 4.0f, 12.0f, 4.0f)
                    curveTo(10.5667f, 4.0f, 9.2333f, 4.354f, 8.0f, 5.062f)
                    curveTo(6.7667f, 5.7707f, 5.8f, 6.75f, 5.1f, 8.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(2.0f)
                    close()
                }
            }
                .build()
        return `_flip-camera`!!
    }

private var `_flip-camera`: ImageVector? = null
