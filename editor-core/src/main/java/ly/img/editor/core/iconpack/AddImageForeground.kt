package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.AddImageForeground: ImageVector
    get() {
        if (_addimageforeground != null) {
            return _addimageforeground!!
        }
        _addimageforeground =
            Builder(
                name = "Addimageforeground",
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(13.0f, 6.0f)
                    curveTo(13.0f, 3.2386f, 15.2386f, 1.0f, 18.0f, 1.0f)
                    curveTo(20.7614f, 1.0f, 23.0f, 3.2386f, 23.0f, 6.0f)
                    curveTo(23.0f, 8.7614f, 20.7614f, 11.0f, 18.0f, 11.0f)
                    curveTo(15.2386f, 11.0f, 13.0f, 8.7614f, 13.0f, 6.0f)
                    close()
                    moveTo(17.5f, 3.0f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(17.5f)
                    close()
                    moveTo(19.0f, 19.0f)
                    verticalLineTo(12.9291f)
                    curveTo(19.7061f, 12.8281f, 20.3783f, 12.6217f, 21.0f, 12.3264f)
                    verticalLineTo(19.0f)
                    curveTo(21.0f, 19.55f, 20.8043f, 20.021f, 20.413f, 20.413f)
                    curveTo(20.0217f, 20.805f, 19.5507f, 21.0007f, 19.0f, 21.0f)
                    horizontalLineTo(5.0f)
                    curveTo(4.45f, 21.0f, 3.9793f, 20.8043f, 3.588f, 20.413f)
                    curveTo(3.1967f, 20.0217f, 3.0007f, 19.5507f, 3.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(3.0f, 4.45f, 3.196f, 3.9793f, 3.588f, 3.588f)
                    curveTo(3.98f, 3.1967f, 4.4507f, 3.0007f, 5.0f, 3.0f)
                    horizontalLineTo(11.6736f)
                    curveTo(11.3783f, 3.6217f, 11.1719f, 4.2938f, 11.0709f, 5.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(19.0f)
                    close()
                    moveTo(6.0f, 17.0f)
                    horizontalLineTo(18.0f)
                    lineTo(14.25f, 12.0f)
                    lineTo(11.25f, 16.0f)
                    lineTo(9.0f, 13.0f)
                    lineTo(6.0f, 17.0f)
                    close()
                    moveTo(8.5f, 10.0f)
                    curveTo(8.9167f, 10.0f, 9.271f, 9.8543f, 9.563f, 9.563f)
                    curveTo(9.855f, 9.2717f, 10.0007f, 8.9173f, 10.0f, 8.5f)
                    curveTo(9.9993f, 8.0827f, 9.8537f, 7.7287f, 9.563f, 7.438f)
                    curveTo(9.2723f, 7.1473f, 8.918f, 7.0013f, 8.5f, 7.0f)
                    curveTo(8.082f, 6.9987f, 7.728f, 7.1447f, 7.438f, 7.438f)
                    curveTo(7.148f, 7.7313f, 7.002f, 8.0853f, 7.0f, 8.5f)
                    curveTo(6.998f, 8.9147f, 7.144f, 9.269f, 7.438f, 9.563f)
                    curveTo(7.732f, 9.857f, 8.086f, 10.0027f, 8.5f, 10.0f)
                    close()
                }
            }.build()
        return _addimageforeground!!
    }

private var _addimageforeground: ImageVector? = null
