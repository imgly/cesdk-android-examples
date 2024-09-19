package ly.img.editor.core.ui.iconpack

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

val IconPack.Erroroutline: ImageVector
    get() {
        if (_erroroutline != null) {
            return _erroroutline!!
        }
        _erroroutline =
            Builder(
                name = "Erroroutline",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    moveTo(12.0f, 17.0f)
                    curveTo(12.2833f, 17.0f, 12.521f, 16.904f, 12.713f, 16.712f)
                    curveTo(12.9043f, 16.5207f, 13.0f, 16.2833f, 13.0f, 16.0f)
                    curveTo(13.0f, 15.7167f, 12.9043f, 15.479f, 12.713f, 15.287f)
                    curveTo(12.521f, 15.0957f, 12.2833f, 15.0f, 12.0f, 15.0f)
                    curveTo(11.7167f, 15.0f, 11.4793f, 15.0957f, 11.288f, 15.287f)
                    curveTo(11.096f, 15.479f, 11.0f, 15.7167f, 11.0f, 16.0f)
                    curveTo(11.0f, 16.2833f, 11.096f, 16.5207f, 11.288f, 16.712f)
                    curveTo(11.4793f, 16.904f, 11.7167f, 17.0f, 12.0f, 17.0f)
                    close()
                    moveTo(11.0f, 13.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(13.0f)
                    close()
                    moveTo(12.0f, 22.0f)
                    curveTo(10.6167f, 22.0f, 9.3167f, 21.7373f, 8.1f, 21.212f)
                    curveTo(6.8833f, 20.6873f, 5.825f, 19.975f, 4.925f, 19.075f)
                    curveTo(4.025f, 18.175f, 3.3127f, 17.1167f, 2.788f, 15.9f)
                    curveTo(2.2627f, 14.6833f, 2.0f, 13.3833f, 2.0f, 12.0f)
                    curveTo(2.0f, 10.6167f, 2.2627f, 9.3167f, 2.788f, 8.1f)
                    curveTo(3.3127f, 6.8833f, 4.025f, 5.825f, 4.925f, 4.925f)
                    curveTo(5.825f, 4.025f, 6.8833f, 3.3123f, 8.1f, 2.787f)
                    curveTo(9.3167f, 2.2623f, 10.6167f, 2.0f, 12.0f, 2.0f)
                    curveTo(13.3833f, 2.0f, 14.6833f, 2.2623f, 15.9f, 2.787f)
                    curveTo(17.1167f, 3.3123f, 18.175f, 4.025f, 19.075f, 4.925f)
                    curveTo(19.975f, 5.825f, 20.6873f, 6.8833f, 21.212f, 8.1f)
                    curveTo(21.7373f, 9.3167f, 22.0f, 10.6167f, 22.0f, 12.0f)
                    curveTo(22.0f, 13.3833f, 21.7373f, 14.6833f, 21.212f, 15.9f)
                    curveTo(20.6873f, 17.1167f, 19.975f, 18.175f, 19.075f, 19.075f)
                    curveTo(18.175f, 19.975f, 17.1167f, 20.6873f, 15.9f, 21.212f)
                    curveTo(14.6833f, 21.7373f, 13.3833f, 22.0f, 12.0f, 22.0f)
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
            }.build()
        return _erroroutline!!
    }

private var _erroroutline: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Erroroutline.IconPreview()
