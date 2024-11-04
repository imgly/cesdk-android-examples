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

val IconPack.TimerTenOutline: ImageVector
    get() {
        if (`_timer-ten-outline` != null) {
            return `_timer-ten-outline`!!
        }
        `_timer-ten-outline` =
            Builder(
                name = "Timer-ten-outline",
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
                    moveTo(8.75f, 17.225f)
                    horizontalLineTo(10.55f)
                    verticalLineTo(8.775f)
                    horizontalLineTo(7.55f)
                    verticalLineTo(10.6f)
                    horizontalLineTo(8.75f)
                    verticalLineTo(17.225f)
                    close()
                    moveTo(13.2f, 17.225f)
                    horizontalLineTo(14.4f)
                    curveTo(14.9f, 17.225f, 15.329f, 17.05f, 15.687f, 16.7f)
                    curveTo(16.045f, 16.35f, 16.2243f, 15.925f, 16.225f, 15.425f)
                    verticalLineTo(10.6f)
                    curveTo(16.225f, 10.1f, 16.046f, 9.671f, 15.688f, 9.313f)
                    curveTo(15.33f, 8.955f, 14.9007f, 8.7757f, 14.4f, 8.775f)
                    horizontalLineTo(13.2f)
                    curveTo(12.7f, 8.775f, 12.275f, 8.954f, 11.925f, 9.312f)
                    curveTo(11.575f, 9.67f, 11.4f, 10.0993f, 11.4f, 10.6f)
                    verticalLineTo(15.425f)
                    curveTo(11.4f, 15.925f, 11.575f, 16.35f, 11.925f, 16.7f)
                    curveTo(12.275f, 17.05f, 12.7f, 17.225f, 13.2f, 17.225f)
                    close()
                    moveTo(13.2f, 15.425f)
                    verticalLineTo(10.6f)
                    horizontalLineTo(14.4f)
                    verticalLineTo(15.425f)
                    horizontalLineTo(13.2f)
                    close()
                    moveTo(9.0f, 3.0f)
                    verticalLineTo(1.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(9.0f)
                    close()
                    moveTo(12.0f, 22.0f)
                    curveTo(10.7667f, 22.0f, 9.604f, 21.7623f, 8.512f, 21.287f)
                    curveTo(7.42f, 20.8117f, 6.466f, 20.166f, 5.65f, 19.35f)
                    curveTo(4.8333f, 18.5333f, 4.1873f, 17.579f, 3.712f, 16.487f)
                    curveTo(3.2367f, 15.395f, 2.9993f, 14.2327f, 3.0f, 13.0f)
                    curveTo(3.0f, 11.7667f, 3.2377f, 10.604f, 3.713f, 9.512f)
                    curveTo(4.1883f, 8.42f, 4.834f, 7.466f, 5.65f, 6.65f)
                    curveTo(6.4667f, 5.8333f, 7.421f, 5.1873f, 8.513f, 4.712f)
                    curveTo(9.605f, 4.2367f, 10.7673f, 3.9993f, 12.0f, 4.0f)
                    curveTo(13.0333f, 4.0f, 14.025f, 4.1667f, 14.975f, 4.5f)
                    curveTo(15.925f, 4.8333f, 16.8167f, 5.3167f, 17.65f, 5.95f)
                    lineTo(19.05f, 4.55f)
                    lineTo(20.45f, 5.95f)
                    lineTo(19.05f, 7.35f)
                    curveTo(19.6833f, 8.1833f, 20.1667f, 9.075f, 20.5f, 10.025f)
                    curveTo(20.8333f, 10.975f, 21.0f, 11.9667f, 21.0f, 13.0f)
                    curveTo(21.0f, 14.2333f, 20.7623f, 15.396f, 20.287f, 16.488f)
                    curveTo(19.8117f, 17.58f, 19.166f, 18.534f, 18.35f, 19.35f)
                    curveTo(17.5333f, 20.1667f, 16.579f, 20.8127f, 15.487f, 21.288f)
                    curveTo(14.395f, 21.7633f, 13.2327f, 22.0007f, 12.0f, 22.0f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveTo(13.9333f, 20.0f, 15.5833f, 19.3167f, 16.95f, 17.95f)
                    curveTo(18.3167f, 16.5833f, 19.0f, 14.9333f, 19.0f, 13.0f)
                    curveTo(19.0f, 11.0667f, 18.3167f, 9.4167f, 16.95f, 8.05f)
                    curveTo(15.5833f, 6.6833f, 13.9333f, 6.0f, 12.0f, 6.0f)
                    curveTo(10.0667f, 6.0f, 8.4167f, 6.6833f, 7.05f, 8.05f)
                    curveTo(5.6833f, 9.4167f, 5.0f, 11.0667f, 5.0f, 13.0f)
                    curveTo(5.0f, 14.9333f, 5.6833f, 16.5833f, 7.05f, 17.95f)
                    curveTo(8.4167f, 19.3167f, 10.0667f, 20.0f, 12.0f, 20.0f)
                    close()
                }
            }
                .build()
        return `_timer-ten-outline`!!
    }

private var `_timer-ten-outline`: ImageVector? = null
