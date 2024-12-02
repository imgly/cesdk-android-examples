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

val IconPack.AddCameraBackground: ImageVector
    get() {
        if (_addcamera != null) {
            return _addcamera!!
        }
        _addcamera =
            Builder(
                name = "Addcamera",
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
                    moveTo(12.1616f, 17.4975f)
                    curveTo(12.1081f, 17.4992f, 12.0542f, 17.5f, 12.0f, 17.5f)
                    curveTo(10.7507f, 17.5007f, 9.6883f, 17.0633f, 8.813f, 16.188f)
                    curveTo(7.9377f, 15.3127f, 7.5f, 14.25f, 7.5f, 13.0f)
                    curveTo(7.4993f, 11.7507f, 7.9367f, 10.6883f, 8.812f, 9.813f)
                    curveTo(9.6873f, 8.9377f, 10.75f, 8.5f, 12.0f, 8.5f)
                    curveTo(13.2493f, 8.4993f, 14.3117f, 8.9367f, 15.187f, 9.812f)
                    curveTo(15.9359f, 10.5609f, 16.3645f, 11.447f, 16.4726f, 12.4702f)
                    curveTo(17.2566f, 12.1665f, 18.1088f, 12.0f, 19.0f, 12.0f)
                    curveTo(20.0736f, 12.0f, 21.0907f, 12.2417f, 22.0f, 12.6736f)
                    verticalLineTo(7.0f)
                    curveTo(22.0007f, 6.4507f, 21.805f, 5.98f, 21.413f, 5.588f)
                    curveTo(21.021f, 5.196f, 20.55f, 5.0f, 20.0f, 5.0f)
                    horizontalLineTo(16.85f)
                    lineTo(15.0f, 3.0f)
                    horizontalLineTo(9.0f)
                    lineTo(7.15f, 5.0f)
                    horizontalLineTo(4.0f)
                    curveTo(3.4507f, 4.9993f, 2.98f, 5.195f, 2.588f, 5.587f)
                    curveTo(2.196f, 5.979f, 2.0f, 6.45f, 2.0f, 7.0f)
                    verticalLineTo(19.0f)
                    curveTo(1.9993f, 19.5493f, 2.195f, 20.02f, 2.587f, 20.412f)
                    curveTo(2.979f, 20.804f, 3.45f, 21.0f, 4.0f, 21.0f)
                    horizontalLineTo(12.2899f)
                    curveTo(12.1013f, 20.3663f, 12.0f, 19.695f, 12.0f, 19.0f)
                    curveTo(12.0f, 18.4842f, 12.0558f, 17.9815f, 12.1616f, 17.4975f)
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
                    moveTo(13.0737f, 15.2728f)
                    curveTo(12.7498f, 15.4243f, 12.3919f, 15.5f, 12.0f, 15.5f)
                    curveTo(11.3f, 15.5f, 10.7083f, 15.2583f, 10.225f, 14.775f)
                    curveTo(9.7417f, 14.2917f, 9.5f, 13.7f, 9.5f, 13.0f)
                    curveTo(9.5f, 12.3f, 9.7417f, 11.7083f, 10.225f, 11.225f)
                    curveTo(10.7083f, 10.7417f, 11.3f, 10.5f, 12.0f, 10.5f)
                    curveTo(12.7f, 10.5f, 13.2917f, 10.7417f, 13.775f, 11.225f)
                    curveTo(14.2583f, 11.7083f, 14.5f, 12.3f, 14.5f, 13.0f)
                    curveTo(14.5f, 13.2542f, 14.4681f, 13.4941f, 14.4044f, 13.7197f)
                    curveTo(13.8889f, 14.1687f, 13.4398f, 14.692f, 13.0737f, 15.2728f)
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
                    moveTo(10.225f, 14.775f)
                    curveTo(10.7083f, 15.2583f, 11.3f, 15.5f, 12.0f, 15.5f)
                    curveTo(12.7f, 15.5f, 13.2917f, 15.2583f, 13.775f, 14.775f)
                    curveTo(14.2583f, 14.2917f, 14.5f, 13.7f, 14.5f, 13.0f)
                    curveTo(14.5f, 12.3f, 14.2583f, 11.7083f, 13.775f, 11.225f)
                    curveTo(13.2917f, 10.7417f, 12.7f, 10.5f, 12.0f, 10.5f)
                    curveTo(11.3f, 10.5f, 10.7083f, 10.7417f, 10.225f, 11.225f)
                    curveTo(9.7417f, 11.7083f, 9.5f, 12.3f, 9.5f, 13.0f)
                    curveTo(9.5f, 13.7f, 9.7417f, 14.2917f, 10.225f, 14.775f)
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
                    moveTo(19.0f, 14.0f)
                    curveTo(16.2386f, 14.0f, 14.0f, 16.2386f, 14.0f, 19.0f)
                    curveTo(14.0f, 21.7614f, 16.2386f, 24.0f, 19.0f, 24.0f)
                    curveTo(21.7614f, 24.0f, 24.0f, 21.7614f, 24.0f, 19.0f)
                    curveTo(24.0f, 16.2386f, 21.7614f, 14.0f, 19.0f, 14.0f)
                    close()
                    moveTo(18.5f, 18.5f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(18.5f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(19.5f)
                    horizontalLineTo(19.5f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(19.5f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(18.5f)
                    horizontalLineTo(18.5f)
                    close()
                }
            }
                .build()
        return _addcamera!!
    }

private var _addcamera: ImageVector? = null
