package ly.img.editor.core.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Deprecated("Use IconPack.CropRotate instead.", ReplaceWith("IconPack.CropRotate"))
val IconPack.Croprotate: ImageVector
    get() = CropRotate

val IconPack.CropRotate: ImageVector
    get() {
        if (_croprotate != null) {
            return _croprotate!!
        }
        _croprotate =
            Builder(
                name = "Croprotate",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 24.0f)
                    curveTo(10.4333f, 24.0f, 8.954f, 23.721f, 7.562f, 23.163f)
                    curveTo(6.1707f, 22.6043f, 4.9377f, 21.829f, 3.863f, 20.837f)
                    curveTo(2.7877f, 19.8457f, 1.9083f, 18.679f, 1.225f, 17.337f)
                    curveTo(0.5417f, 15.9957f, 0.1333f, 14.55f, 0.0f, 13.0f)
                    horizontalLineTo(2.0f)
                    curveTo(2.1333f, 14.2f, 2.4543f, 15.321f, 2.963f, 16.363f)
                    curveTo(3.471f, 17.4043f, 4.1293f, 18.325f, 4.938f, 19.125f)
                    curveTo(5.746f, 19.925f, 6.675f, 20.5707f, 7.725f, 21.062f)
                    curveTo(8.775f, 21.554f, 9.9f, 21.85f, 11.1f, 21.95f)
                    lineTo(9.55f, 20.4f)
                    lineTo(10.95f, 19.0f)
                    lineTo(15.45f, 23.5f)
                    curveTo(14.8833f, 23.6833f, 14.3127f, 23.8123f, 13.738f, 23.887f)
                    curveTo(13.1627f, 23.9623f, 12.5833f, 24.0f, 12.0f, 24.0f)
                    close()
                    moveTo(15.0f, 19.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(9.0f)
                    curveTo(8.45f, 17.0f, 7.9793f, 16.8043f, 7.588f, 16.413f)
                    curveTo(7.196f, 16.021f, 7.0f, 15.55f, 7.0f, 15.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(15.0f)
                    close()
                    moveTo(15.0f, 13.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(15.0f)
                    curveTo(15.55f, 7.0f, 16.021f, 7.1957f, 16.413f, 7.587f)
                    curveTo(16.8043f, 7.979f, 17.0f, 8.45f, 17.0f, 9.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(15.0f)
                    close()
                    moveTo(22.0f, 11.0f)
                    curveTo(21.8833f, 9.8f, 21.5667f, 8.679f, 21.05f, 7.637f)
                    curveTo(20.5333f, 6.5957f, 19.8707f, 5.675f, 19.062f, 4.875f)
                    curveTo(18.254f, 4.075f, 17.325f, 3.429f, 16.275f, 2.937f)
                    curveTo(15.225f, 2.4457f, 14.1f, 2.15f, 12.9f, 2.05f)
                    lineTo(14.45f, 3.6f)
                    lineTo(13.05f, 5.0f)
                    lineTo(8.55f, 0.5f)
                    curveTo(9.1167f, 0.3167f, 9.6873f, 0.1873f, 10.262f, 0.112f)
                    curveTo(10.8373f, 0.0373f, 11.4167f, 0.0f, 12.0f, 0.0f)
                    curveTo(13.5667f, 0.0f, 15.046f, 0.279f, 16.438f, 0.837f)
                    curveTo(17.8293f, 1.3957f, 19.0627f, 2.1707f, 20.138f, 3.162f)
                    curveTo(21.2127f, 4.154f, 22.0917f, 5.3207f, 22.775f, 6.662f)
                    curveTo(23.4583f, 8.004f, 23.8667f, 9.45f, 24.0f, 11.0f)
                    horizontalLineTo(22.0f)
                    close()
                }
            }.build()
        return _croprotate!!
    }

private var _croprotate: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.CropRotate.IconPreview()
