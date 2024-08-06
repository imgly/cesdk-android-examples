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

val IconPack.Book: ImageVector
    get() {
        if (_book != null) {
            return _book!!
        }
        _book =
            Builder(
                name = "Book",
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
                    moveTo(14.0f, 9.9f)
                    verticalLineTo(8.2f)
                    curveTo(14.55f, 7.9667f, 15.1127f, 7.7917f, 15.688f, 7.675f)
                    curveTo(16.2633f, 7.5583f, 16.8673f, 7.5f, 17.5f, 7.5f)
                    curveTo(17.9333f, 7.5f, 18.3583f, 7.5333f, 18.775f, 7.6f)
                    curveTo(19.1917f, 7.6667f, 19.6f, 7.75f, 20.0f, 7.85f)
                    verticalLineTo(9.45f)
                    curveTo(19.6f, 9.3f, 19.1957f, 9.1877f, 18.787f, 9.113f)
                    curveTo(18.3783f, 9.0383f, 17.9493f, 9.0007f, 17.5f, 9.0f)
                    curveTo(16.8667f, 9.0f, 16.2583f, 9.0793f, 15.675f, 9.238f)
                    curveTo(15.0917f, 9.3967f, 14.5333f, 9.6173f, 14.0f, 9.9f)
                    close()
                    moveTo(14.0f, 15.4f)
                    verticalLineTo(13.7f)
                    curveTo(14.55f, 13.4667f, 15.1127f, 13.2917f, 15.688f, 13.175f)
                    curveTo(16.2633f, 13.0583f, 16.8673f, 13.0f, 17.5f, 13.0f)
                    curveTo(17.9333f, 13.0f, 18.3583f, 13.0333f, 18.775f, 13.1f)
                    curveTo(19.1917f, 13.1667f, 19.6f, 13.25f, 20.0f, 13.35f)
                    verticalLineTo(14.95f)
                    curveTo(19.6f, 14.8f, 19.1957f, 14.6873f, 18.787f, 14.612f)
                    curveTo(18.3783f, 14.5367f, 17.9493f, 14.4993f, 17.5f, 14.5f)
                    curveTo(16.8667f, 14.5f, 16.2583f, 14.575f, 15.675f, 14.725f)
                    curveTo(15.0917f, 14.875f, 14.5333f, 15.1f, 14.0f, 15.4f)
                    close()
                    moveTo(14.0f, 12.65f)
                    verticalLineTo(10.95f)
                    curveTo(14.55f, 10.7167f, 15.1127f, 10.5417f, 15.688f, 10.425f)
                    curveTo(16.2633f, 10.3083f, 16.8673f, 10.25f, 17.5f, 10.25f)
                    curveTo(17.9333f, 10.25f, 18.3583f, 10.2833f, 18.775f, 10.35f)
                    curveTo(19.1917f, 10.4167f, 19.6f, 10.5f, 20.0f, 10.6f)
                    verticalLineTo(12.2f)
                    curveTo(19.6f, 12.05f, 19.1957f, 11.9373f, 18.787f, 11.862f)
                    curveTo(18.3783f, 11.7867f, 17.9493f, 11.7493f, 17.5f, 11.75f)
                    curveTo(16.8667f, 11.75f, 16.2583f, 11.8293f, 15.675f, 11.988f)
                    curveTo(15.0917f, 12.1467f, 14.5333f, 12.3673f, 14.0f, 12.65f)
                    close()
                    moveTo(6.5f, 16.0f)
                    curveTo(7.2833f, 16.0f, 8.046f, 16.0877f, 8.788f, 16.263f)
                    curveTo(9.53f, 16.4383f, 10.2673f, 16.7007f, 11.0f, 17.05f)
                    verticalLineTo(7.2f)
                    curveTo(10.3167f, 6.8f, 9.5917f, 6.5f, 8.825f, 6.3f)
                    curveTo(8.0583f, 6.1f, 7.2833f, 6.0f, 6.5f, 6.0f)
                    curveTo(5.9f, 6.0f, 5.304f, 6.0583f, 4.712f, 6.175f)
                    curveTo(4.12f, 6.2917f, 3.5493f, 6.4667f, 3.0f, 6.7f)
                    verticalLineTo(16.6f)
                    curveTo(3.5833f, 16.4f, 4.1627f, 16.25f, 4.738f, 16.15f)
                    curveTo(5.3133f, 16.05f, 5.9007f, 16.0f, 6.5f, 16.0f)
                    close()
                    moveTo(13.0f, 17.05f)
                    curveTo(13.7333f, 16.7f, 14.471f, 16.4377f, 15.213f, 16.263f)
                    curveTo(15.955f, 16.0883f, 16.7173f, 16.0007f, 17.5f, 16.0f)
                    curveTo(18.1f, 16.0f, 18.6877f, 16.05f, 19.263f, 16.15f)
                    curveTo(19.8383f, 16.25f, 20.4173f, 16.4f, 21.0f, 16.6f)
                    verticalLineTo(6.7f)
                    curveTo(20.45f, 6.4667f, 19.879f, 6.2917f, 19.287f, 6.175f)
                    curveTo(18.695f, 6.0583f, 18.0993f, 6.0f, 17.5f, 6.0f)
                    curveTo(16.7167f, 6.0f, 15.9417f, 6.1f, 15.175f, 6.3f)
                    curveTo(14.4083f, 6.5f, 13.6833f, 6.8f, 13.0f, 7.2f)
                    verticalLineTo(17.05f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveTo(11.2f, 19.3667f, 10.3333f, 18.875f, 9.4f, 18.525f)
                    curveTo(8.4667f, 18.175f, 7.5f, 18.0f, 6.5f, 18.0f)
                    curveTo(5.8f, 18.0f, 5.1127f, 18.0917f, 4.438f, 18.275f)
                    curveTo(3.7633f, 18.4583f, 3.1173f, 18.7167f, 2.5f, 19.05f)
                    curveTo(2.15f, 19.2333f, 1.8127f, 19.225f, 1.488f, 19.025f)
                    curveTo(1.1633f, 18.825f, 1.0007f, 18.5333f, 1.0f, 18.15f)
                    verticalLineTo(6.1f)
                    curveTo(1.0f, 5.9167f, 1.046f, 5.7417f, 1.138f, 5.575f)
                    curveTo(1.23f, 5.4083f, 1.3673f, 5.2833f, 1.55f, 5.2f)
                    curveTo(2.3167f, 4.8f, 3.1167f, 4.5f, 3.95f, 4.3f)
                    curveTo(4.7833f, 4.1f, 5.6333f, 4.0f, 6.5f, 4.0f)
                    curveTo(7.4667f, 4.0f, 8.4127f, 4.125f, 9.338f, 4.375f)
                    curveTo(10.2633f, 4.625f, 11.1507f, 5.0f, 12.0f, 5.5f)
                    curveTo(12.85f, 5.0f, 13.7377f, 4.625f, 14.663f, 4.375f)
                    curveTo(15.5883f, 4.125f, 16.534f, 4.0f, 17.5f, 4.0f)
                    curveTo(18.3667f, 4.0f, 19.2167f, 4.1f, 20.05f, 4.3f)
                    curveTo(20.8833f, 4.5f, 21.6833f, 4.8f, 22.45f, 5.2f)
                    curveTo(22.6333f, 5.2833f, 22.771f, 5.4083f, 22.863f, 5.575f)
                    curveTo(22.955f, 5.7417f, 23.0007f, 5.9167f, 23.0f, 6.1f)
                    verticalLineTo(18.15f)
                    curveTo(23.0f, 18.5333f, 22.8377f, 18.825f, 22.513f, 19.025f)
                    curveTo(22.1883f, 19.225f, 21.8507f, 19.2333f, 21.5f, 19.05f)
                    curveTo(20.8833f, 18.7167f, 20.2377f, 18.4583f, 19.563f, 18.275f)
                    curveTo(18.8883f, 18.0917f, 18.2007f, 18.0f, 17.5f, 18.0f)
                    curveTo(16.5f, 18.0f, 15.5333f, 18.175f, 14.6f, 18.525f)
                    curveTo(13.6667f, 18.875f, 12.8f, 19.3667f, 12.0f, 20.0f)
                    close()
                }
            }
                .build()
        return _book!!
    }

private var _book: ImageVector? = null
